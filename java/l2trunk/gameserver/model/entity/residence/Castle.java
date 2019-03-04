package l2trunk.gameserver.model.entity.residence;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.commons.math.SafeMath;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.dao.CastleDAO;
import l2trunk.gameserver.dao.CastleHiredGuardDAO;
import l2trunk.gameserver.dao.ClanDataDAO;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.hibenate.HibernateUtil;
import l2trunk.gameserver.hibenate.dao.CastleEntity;
import l2trunk.gameserver.instancemanager.CastleManorManager;
import l2trunk.gameserver.model.Manor;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.SevenSigns;
import l2trunk.gameserver.model.items.ClanWarehouse;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.templates.item.support.MerchantGuard;
import l2trunk.gameserver.templates.manor.CropProcure;
import l2trunk.gameserver.templates.manor.SeedProduction;
import l2trunk.gameserver.utils.GameStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public final class Castle extends Residence {
    private static final Logger _log = LoggerFactory.getLogger(Castle.class);

    private static final String CASTLE_MANOR_DELETE_PRODUCTION = "DELETE FROM castle_manor_production WHERE castle_id=?;";
    private static final String CASTLE_MANOR_DELETE_PRODUCTION_PERIOD = "DELETE FROM castle_manor_production WHERE castle_id=? AND period=?;";
    private static final String CASTLE_MANOR_DELETE_PROCURE = "DELETE FROM castle_manor_procure WHERE castle_id=?;";
    private static final String CASTLE_MANOR_DELETE_PROCURE_PERIOD = "DELETE FROM castle_manor_procure WHERE castle_id=? AND period=?;";
    private static final String CASTLE_UPDATE_CROP = "UPDATE castle_manor_procure SET can_buy=? WHERE crop_id=? AND castle_id=? AND period=?";
    private static final String CASTLE_UPDATE_SEED = "UPDATE castle_manor_production SET can_produce=? WHERE seed_id=? AND castle_id=? AND period=?";

    private final Map<Integer, MerchantGuard> merchantGuards = new HashMap<>();
    private final Map<Integer, List<Integer>> relatedFortresses = new ConcurrentHashMap<>();
    private final NpcString npcStringName;
    private final Set<ItemInstance> _spawnMerchantTickets = new CopyOnWriteArraySet<>();
    private Dominion dominion;
    private List<CropProcure> procures;
    private List<SeedProduction> production;
    private List<CropProcure> procureNext;
    private List<SeedProduction> productionNext;
    private boolean _isNextPeriodApproved;
    private int _TaxPercent;
    private double taxRate;
    private long treasury;
    private long collectedShops;
    private long collectedSeed;

    public Castle(StatsSet set) {
        super(set);
        npcStringName = NpcString.valueOf(1001000 + id);
    }

    @Override
    public void init() {
        super.init();

        for (Map.Entry<Integer, List<Integer>> entry : relatedFortresses.entrySet()) {
            relatedFortresses.remove(entry.getKey());

            List<Integer> list = entry.getValue();
            List<Integer> list2 = new ArrayList<>(list.size());
            for (Integer i : list) {
                Fortress fortress = ResidenceHolder.getFortress(i);
                if (fortress == null)
                    continue;

                list2.add(fortress.id);

                fortress.addRelatedCastle(this);
            }
            relatedFortresses.put(entry.getKey(), list2);
        }
    }

    @Override
    public ResidenceType getType() {
        return ResidenceType.Castle;
    }

    // This method sets the castle owner; null here means give it back to NPC
    @Override
    public void changeOwner(Clan newOwner) {
        // Если клан уже владел каким-либо замком/крепостью, отбираем его.
        if (newOwner != null) {
            if (newOwner.getHasFortress() != 0) {
                Fortress oldFortress = ResidenceHolder.getFortress(newOwner.getHasFortress());
                if (oldFortress != null)
                    oldFortress.changeOwner(null);
            }
            if (newOwner.getCastle() != 0) {
                Castle oldCastle = ResidenceHolder.getCastle(newOwner.getCastle());
                if (oldCastle != null)
                    oldCastle.changeOwner(null);
            }
        }

        Clan oldOwner;
        // Если этим замком уже кто-то владел, отбираем у него замок
        if (getOwnerId() > 0 && (newOwner == null || newOwner.clanId() != getOwnerId())) {
            // Удаляем замковые скилы у старого владельца
            removeSkills();
            getDominion().changeOwner(null);
            getDominion().removeSkills();

            // Убираем налог
            setTaxPercent(null, 0);
            cancelCycleTask();

            oldOwner = getOwner();
            if (oldOwner != null) {
                // Переносим сокровищницу в вархауз старого владельца
                long amount = getTreasury();
                if (amount > 0) {
                    ClanWarehouse warehouse = oldOwner.getWarehouse();
                    if (warehouse != null) {
                        warehouse.addItem(ItemTemplate.ITEM_ID_ADENA, amount, "Castle Change Owner");
                        addToTreasuryNoTax(-amount, false, false);
                    }
                }

                // Проверяем членов старого клана владельца, снимаем короны замков и корону лорда с лидера
                for (Player clanMember : oldOwner.getOnlineMembers())
                    if (clanMember != null && clanMember.getInventory() != null)
                        clanMember.getInventory().validateItems();

                // Отнимаем замок у старого владельца
                oldOwner.setHasCastle(0);
            }
        }

        // Выдаем замок новому владельцу
        if (newOwner != null)
            newOwner.setHasCastle(getId());

        // Сохраняем в базу
        updateOwnerInDB(newOwner);

        // Выдаем замковые скилы новому владельцу
        rewardSkills();

        update();
    }

    // This method loads castle
    @Override
    protected void loadData() {
        CastleEntity castleEntity = HibernateUtil.getSession().get(CastleEntity.class, id);
        _TaxPercent = castleEntity.getTaxPercent();
        setTaxPercent(_TaxPercent);
        treasury = castleEntity.getTreasury();
        setRewardCount(castleEntity.getRewardCount());
        siegeDate.setTimeInMillis(castleEntity.getSiegeDate());
        getLastSiegeDate().setTimeInMillis(castleEntity.getLastSiegeDate());
        getOwnDate().setTimeInMillis(castleEntity.getOwnDate());

        procures = new ArrayList<>();
        production = new ArrayList<>();
        procureNext = new ArrayList<>();
        productionNext = new ArrayList<>();
        _isNextPeriodApproved = false;

        owner = ClanDataDAO.INSTANCE.getOwner(this);
        CastleHiredGuardDAO.INSTANCE.load(this);
    }

    private void updateOwnerInDB(Clan clan) {
        owner = clan; // Update owner id property

        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET hasCastle=0 WHERE hasCastle=? LIMIT 1");
            statement.setInt(1, getId());
            statement.execute();

            if (clan != null) {
                statement = con.prepareStatement("UPDATE clan_data SET hasCastle=? WHERE clan_id=? LIMIT 1");
                statement.setInt(1, getId());
                statement.setInt(2, getOwnerId());
                statement.execute();

                clan.broadcastClanStatus(true, false, false);
            }
        } catch (SQLException e) {
            _log.error("Error while updating Castle Owner in database", e);
        }
    }

    public int getTaxPercent() {
        // Если печатью SEAL_STRIFE владеют DUSK то налог можно выставлять не более 5%
        if (_TaxPercent > 5 && SevenSigns.INSTANCE.getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DUSK)
            _TaxPercent = 5;
        return _TaxPercent;
    }

    public void setTaxPercent(int p) {
        _TaxPercent = Math.min(Math.max(0, p), 100);
        taxRate = _TaxPercent / 100.0;
    }

    public int getTaxPercent0() {
        return _TaxPercent;
    }

    public long getCollectedShops() {
        return collectedShops;
    }

    public void setCollectedShops(long value) {
        collectedShops = value;
    }

    public long getCollectedSeed() {
        return collectedSeed;
    }

    public void setCollectedSeed(long value) {
        collectedSeed = value;
    }

    /**
     * Add amount to castle instance's treasury (warehouse).
     */
    public void addToTreasury(long amount, boolean shop, boolean seed) {
        if (getOwnerId() <= 0)
            return;

        if (amount == 0)
            return;

        if (amount > 1 && id != 5 && id != 8) // If current castle instance is not Aden or Rune
        {
            Castle royal = ResidenceHolder.getCastle(id >= 7 ? 8 : 5);
            if (royal != null) {
                long royalTax = (long) (amount * royal.getTaxRate()); // Find out what royal castle gets from the current castle instance's income
                if (royal.getOwnerId() > 0) {
                    royal.addToTreasury(royalTax, shop, seed); // Only bother to really add the tax to the treasury if not npc owned
                }

                amount -= royalTax; // Subtract royal castle income from current castle instance's income
            }
        }

        addToTreasuryNoTax(amount, shop, seed);
    }

    // This method add to the treasury

    /**
     * Add amount to castle instance's treasury (warehouse), no tax paying.
     */
    public void addToTreasuryNoTax(long amount, boolean shop, boolean seed) {
        if (getOwnerId() <= 0)
            return;

        if (amount == 0)
            return;

        if (Config.RATE_DROP_ADENA < 20)
            GameStats.addAdena(amount);

        // Add to the current treasury total.  Use "-" to substract from treasury
        treasury = SafeMath.addAndLimit(treasury, amount);

        if (shop)
            collectedShops += amount;

        if (seed)
            collectedSeed += amount;

        setJdbcState(JdbcEntityState.UPDATED);
        update();
    }

    public int getCropRewardType(int crop) {
        return procures.stream()
                .filter(cp -> cp.cropId == crop)
                .mapToInt(CropProcure::getReward)
                .findFirst().orElse(0);
    }

    // This method updates the castle tax rate
    public void setTaxPercent(Player activeChar, int taxPercent) {
        setTaxPercent(taxPercent);

        setJdbcState(JdbcEntityState.UPDATED);
        update();

        if (activeChar != null)
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.model.entity.Castle.OutOfControl.CastleTaxChangetTo").addString(getName()).addNumber(taxPercent));
    }

    public double getTaxRate() {
        // Если печатью SEAL_STRIFE владеют DUSK то налог можно выставлять не более 5%
        if (taxRate > 0.05 && SevenSigns.INSTANCE.getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DUSK)
            taxRate = 0.05;
        return taxRate;
    }

    public long getTreasury() {
        return treasury;
    }

    public List<SeedProduction> getSeedProduction(int period) {
        return period == CastleManorManager.PERIOD_CURRENT ? production : productionNext;
    }

    public List<CropProcure> getCropProcure(int period) {
        return period == CastleManorManager.PERIOD_CURRENT ? procures : procureNext;
    }

    public void setSeedProduction(List<SeedProduction> seed, int period) {
        if (period == CastleManorManager.PERIOD_CURRENT)
            production = seed;
        else
            productionNext = seed;
    }

    public void setCropProcure(List<CropProcure> crop, int period) {
        if (period == CastleManorManager.PERIOD_CURRENT)
            procures = crop;
        else
            procureNext = crop;
    }

    public synchronized SeedProduction getSeed(int seedId, int period) {
        return getSeedProduction(period).stream()
                .filter(seed -> seed.getId() == seedId)
                .findFirst().orElse(null);
    }

    public synchronized CropProcure getCrop(int cropId, int period) {
        return getCropProcure(period).stream()
                .filter(crop -> crop.cropId == cropId)
                .findFirst().orElse(null);
    }

    public long getManorCost(int period) {
        List<CropProcure> procure;
        List<SeedProduction> production;

        if (period == CastleManorManager.PERIOD_CURRENT) {
            procure = procures;
            production = this.production;
        } else {
            procure = procureNext;
            production = productionNext;
        }

        long total = 0;
        if (production != null)
            total = production.stream()
                    .mapToLong(seed -> Manor.INSTANCE.getSeedBuyPrice(seed.getId()) * seed.getStartProduce())
                    .sum();
        if (procure != null)
            total += procure.stream()
                    .mapToLong(crop -> crop.price * crop.getStartAmount())
                    .sum();
        return total;
    }

    // Save manor production data
    public void saveSeedData() {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            PreparedStatement statement = con.prepareStatement(CASTLE_MANOR_DELETE_PRODUCTION);
            statement.setInt(1, getId());
            statement.execute();

            if (production != null) {
                int count = 0;
                String query = "INSERT INTO castle_manor_production VALUES ";
                String[] values = new String[production.size()];
                for (SeedProduction s : production) {
                    values[count] = "(" + getId() + "," + s.getId() + "," + s.getCanProduce() + "," + s.getStartProduce() + "," + s.getPrice() + "," + CastleManorManager.PERIOD_CURRENT + ")";
                    count++;
                }
                if (values.length > 0) {
                    query += values[0];
                    for (int i = 1; i < values.length; i++)
                        query += "," + values[i];
                    statement = con.prepareStatement(query);
                    statement.execute();
                }
            }

            if (productionNext != null) {
                int count = 0;
                String query = "INSERT INTO castle_manor_production VALUES ";
                String[] values = new String[productionNext.size()];
                for (SeedProduction s : productionNext) {
                    values[count] = "(" + getId() + "," + s.getId() + "," + s.getCanProduce() + "," + s.getStartProduce() + "," + s.getPrice() + "," + CastleManorManager.PERIOD_NEXT + ")";
                    count++;
                }
                if (values.length > 0) {
                    query += values[0];
                    for (int i = 1; i < values.length; i++)
                        query += "," + values[i];
                    statement = con.prepareStatement(query);
                    statement.execute();
                }
            }
        } catch (SQLException e) {
            _log.error("Error adding seed production data for castle " + getName() + '!', e);
        }
    }

    // Save manor production data for specified period
    public void saveSeedData(int period) {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            PreparedStatement statement = con.prepareStatement(CASTLE_MANOR_DELETE_PRODUCTION_PERIOD);
            statement.setInt(1, getId());
            statement.setInt(2, period);
            statement.execute();

            List<SeedProduction> prod = getSeedProduction(period);

            if (prod != null) {
                int count = 0;
                String query = "INSERT INTO castle_manor_production VALUES ";
                String[] values = new String[prod.size()];
                for (SeedProduction s : prod) {
                    values[count] = "(" + getId() + "," + s.getId() + "," + s.getCanProduce() + "," + s.getStartProduce() + "," + s.getPrice() + "," + period + ")";
                    count++;
                }
                if (values.length > 0) {
                    query += values[0];
                    for (int i = 1; i < values.length; i++)
                        query += "," + values[i];
                    statement = con.prepareStatement(query);
                    statement.execute();
                }
            }
        } catch (SQLException e) {
            _log.error("Error adding seed production data for castle " + getName() + '!', e);
        }
    }

    // Save crop procure data
    public void saveCropData() {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            PreparedStatement statement = con.prepareStatement(CASTLE_MANOR_DELETE_PROCURE);
            statement.setInt(1, getId());
            statement.execute();
            if (procures != null) {
                int count = 0;
                StringBuilder query = new StringBuilder("INSERT INTO castle_manor_procure VALUES ");
                String[] values = new String[procures.size()];
                for (CropProcure cp : procures) {
                    values[count] = "(" + getId() + "," + cp.cropId + "," + cp.getAmount() + "," + cp.getStartAmount() + "," + cp.price + "," + cp.getReward() + "," + CastleManorManager.PERIOD_CURRENT + ")";
                    count++;
                }
                if (values.length > 0) {
                    query.append(values[0]);
                    for (int i = 1; i < values.length; i++)
                        query.append(",").append(values[i]);
                    statement = con.prepareStatement(query.toString());
                    statement.execute();
                }
            }
            if (procureNext != null) {
                int count = 0;
                StringBuilder query = new StringBuilder("INSERT INTO castle_manor_procure VALUES ");
                String[] values = new String[procureNext.size()];
                for (CropProcure cp : procureNext) {
                    values[count] = "(" + getId() + "," + cp.cropId + "," + cp.getAmount() + "," + cp.getStartAmount() + "," + cp.price + "," + cp.getReward() + "," + CastleManorManager.PERIOD_NEXT + ")";
                    count++;
                }
                if (values.length > 0) {
                    query.append(values[0]);
                    for (int i = 1; i < values.length; i++)
                        query.append(",").append(values[i]);
                    statement = con.prepareStatement(query.toString());
                    statement.execute();
                }
            }
        } catch (SQLException e) {
            _log.error("Error adding crop data for castle " + getName() + '!', e);
        }
    }

    // Save crop procure data for specified period
    public void saveCropData(int period) {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            PreparedStatement statement = con.prepareStatement(CASTLE_MANOR_DELETE_PROCURE_PERIOD);
            statement.setInt(1, getId());
            statement.setInt(2, period);
            statement.execute();

            List<CropProcure> proc = getCropProcure(period);

            if (proc != null) {
                int count = 0;
                String query = "INSERT INTO castle_manor_procure VALUES ";
                String[] values = new String[proc.size()];

                for (CropProcure cp : proc) {
                    values[count] = "(" + getId() + "," + cp.cropId + "," + cp.getAmount() + "," + cp.getStartAmount() + "," + cp.price + "," + cp.getReward() + "," + period + ")";
                    count++;
                }
                if (values.length > 0) {
                    query += values[0];
                    for (int i = 1; i < values.length; i++)
                        query += "," + values[i];
                    statement = con.prepareStatement(query);
                    statement.execute();
                }
            }
        } catch (SQLException e) {
            _log.error("Error adding crop data for castle " + getName() + '!', e);
        }
    }

    public void updateCrop(int cropId, long amount, int period) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(CASTLE_UPDATE_CROP)) {
            statement.setLong(1, amount);
            statement.setInt(2, cropId);
            statement.setInt(3, getId());
            statement.setInt(4, period);
            statement.execute();
        } catch (SQLException e) {
            _log.error("Error adding crop data for castle " + getName() + '!', e);
        }
    }

    public void updateSeed(int seedId, long amount, int period) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(CASTLE_UPDATE_SEED)) {
            statement.setLong(1, amount);
            statement.setInt(2, seedId);
            statement.setInt(3, getId());
            statement.setInt(4, period);
            statement.execute();
        } catch (SQLException e) {
            _log.error("Error adding seed production data for castle " + getName() + '!', e);
        }
    }

    public boolean isNextPeriodApproved() {
        return _isNextPeriodApproved;
    }

    public void setNextPeriodApproved(boolean val) {
        _isNextPeriodApproved = val;
    }

    public Dominion getDominion() {
        return dominion;
    }

    public void setDominion(Dominion dominion) {
        this.dominion = dominion;
    }

    public void addRelatedFortress(int type, int fortress) {
//        List<Integer> fortresses =
        List<Integer> fortresses = relatedFortresses.computeIfAbsent(type, k -> new ArrayList<>());
        fortresses.add(fortress);
//        fortresses.add(ResidenceHolder.INSTANCE().getResidence(Fortress.class, fortress));
//                relatedFortresses.get(fortress));
    }

    public int getDomainFortressContract() {
        List<Integer> list = relatedFortresses.get(Fortress.DOMAIN);
        return (list == null) ? 0 : 1;
    }

    @Override
    public void update() {
        CastleDAO.INSTANCE.update(this);
    }

    public NpcString getNpcStringName() {
        return npcStringName;
    }

    public Map<Integer, List<Integer>> getRelatedFortresses() {
        return relatedFortresses;
    }

    public void addMerchantGuard(MerchantGuard merchantGuard) {
        merchantGuards.put(merchantGuard.getItemId(), merchantGuard);
    }

    public MerchantGuard getMerchantGuard(int itemId) {
        return merchantGuards.get(itemId);
    }

    public Map<Integer, MerchantGuard> getMerchantGuards() {
        return merchantGuards;
    }

    public Set<ItemInstance> getSpawnMerchantTickets() {
        return _spawnMerchantTickets;
    }

    @Override
    public void startCycleTask() {
    }
}