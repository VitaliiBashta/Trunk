package l2trunk.gameserver.instancemanager;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Manor;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.items.ClanWarehouse;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.ClanTable;
import l2trunk.gameserver.templates.manor.CropProcure;
import l2trunk.gameserver.templates.manor.SeedProduction;
import l2trunk.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public enum CastleManorManager {
    INSTANCE;
    public static final int PERIOD_CURRENT = 0;
    public static final int PERIOD_NEXT = 1;
    private static final String var_name = "ManorApproved";
    private static final long MAINTENANCE_PERIOD = 6; // 6 mins
    private static final Logger LOG = LoggerFactory.getLogger(CastleManorManager.class);
    private static final String CASTLE_MANOR_LOAD_PROCURE = "SELECT * FROM castle_manor_procure WHERE castle_id=?";
    private static final String CASTLE_MANOR_LOAD_PRODUCTION = "SELECT * FROM castle_manor_production WHERE castle_id=?";

    private static final int NEXT_PERIOD_APPROVE = 6; // 6:00
    private static final int NEXT_PERIOD_APPROVE_MIN = 0; //
    private static final int MANOR_REFRESH = 20; // 20:00
    private static final int MANOR_REFRESH_MIN = 0; //
    private boolean underMaintenance;
    private boolean disabled;

    CastleManorManager() {
        load(); // loadFile data from database
    }

    private void load() {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            for (Castle castle : ResidenceHolder.getCastles()) {
                List<SeedProduction> production = new ArrayList<>();
                List<SeedProduction> productionNext = new ArrayList<>();
                List<CropProcure> procure = new ArrayList<>();
                List<CropProcure> procureNext = new ArrayList<>();

                // restore seed production info
                PreparedStatement statement = con.prepareStatement(CASTLE_MANOR_LOAD_PRODUCTION);
                statement.setInt(1, castle.getId());
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    int seedId = rs.getInt("seed_id");
                    long canProduce = rs.getLong("can_produce");
                    long startProduce = rs.getLong("start_produce");
                    long price = rs.getLong("seed_price");
                    int period = rs.getInt("period");
                    if (period == PERIOD_CURRENT)
                        production.add(new SeedProduction(seedId, canProduce, price, startProduce));
                    else
                        productionNext.add(new SeedProduction(seedId, canProduce, price, startProduce));
                }

                castle.setSeedProduction(production, PERIOD_CURRENT);
                castle.setSeedProduction(productionNext, PERIOD_NEXT);

                // restore procure info
                statement = con.prepareStatement(CASTLE_MANOR_LOAD_PROCURE);
                statement.setInt(1, castle.getId());
                rs = statement.executeQuery();
                while (rs.next()) {
                    int cropId = rs.getInt("crop_id");
                    long canBuy = rs.getLong("can_buy");
                    long startBuy = rs.getLong("start_buy");
                    int rewardType = rs.getInt("reward_type");
                    long price = rs.getLong("price");
                    int period = rs.getInt("period");
                    if (period == PERIOD_CURRENT)
                        procure.add(new CropProcure(cropId, canBuy, rewardType, startBuy, price));
                    else
                        procureNext.add(new CropProcure(cropId, canBuy, rewardType, startBuy, price));
                }

                castle.setCropProcure(procure, PERIOD_CURRENT);
                castle.setCropProcure(procureNext, PERIOD_NEXT);

                if (!procure.isEmpty() || !procureNext.isEmpty() || !production.isEmpty() || !productionNext.isEmpty())
                    LOG.info("Manor System: Loaded data for " + castle.getName() + " castle");
            }
        } catch (SQLException e) {
            LOG.error("Manor System: Error restoring manor data!", e);
        }
    }

    public void init() {
        if (!ServerVariables.isSet(var_name)) {
            Calendar manorRefresh = Calendar.getInstance();
            manorRefresh.set(Calendar.HOUR_OF_DAY, MANOR_REFRESH);
            manorRefresh.set(Calendar.MINUTE, MANOR_REFRESH_MIN);
            manorRefresh.set(Calendar.SECOND, 0);
            manorRefresh.set(Calendar.MILLISECOND, 0);

            Calendar periodApprove = Calendar.getInstance();
            periodApprove.set(Calendar.HOUR_OF_DAY, NEXT_PERIOD_APPROVE);
            periodApprove.set(Calendar.MINUTE, NEXT_PERIOD_APPROVE_MIN);
            periodApprove.set(Calendar.SECOND, 0);
            periodApprove.set(Calendar.MILLISECOND, 0);
            boolean isApproved = periodApprove.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() && manorRefresh.getTimeInMillis() > Calendar.getInstance().getTimeInMillis();
            if (isApproved) ServerVariables.set("ManorApproved");
            else ServerVariables.unset("ManorApproved");
        }

        Calendar FirstDelay = Calendar.getInstance();
        FirstDelay.set(Calendar.SECOND, 0);
        FirstDelay.set(Calendar.MILLISECOND, 0);
        FirstDelay.add(Calendar.MINUTE, 1);
        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new ManorTask(), FirstDelay.getTimeInMillis() - Calendar.getInstance().getTimeInMillis(), 60000);


        underMaintenance = false;
        disabled = !Config.ALLOW_MANOR;
        ResidenceHolder.getCastles().forEach(c -> c.setNextPeriodApproved(ServerVariables.isSet("ManorApproved")));
    }

    private void setNextPeriod() {
        ResidenceHolder.getCastles().forEach(c -> {
            if (c.getOwnerId() > 0) {
                Clan clan = ClanTable.INSTANCE.getClan(c.getOwnerId());
                if (clan != null) {
                    ClanWarehouse cwh = clan.getWarehouse();

                    for (CropProcure crop : c.getCropProcure(PERIOD_CURRENT)) {
                        if (crop.getStartAmount() == 0)
                            continue;

                        // adding bought crops to clan warehouse
                        if (crop.getStartAmount() > crop.getAmount()) {
                            LOG.info("Manor System [" + c.getName() + "]: Start Amount of Crop " + crop.getStartAmount() + " " +
                                    "> Amount of current " + crop.getAmount());
                            long count = crop.getStartAmount() - crop.getAmount();

                            count = count * 90 / 100;
                            if (count < 1 && Rnd.get(99) < 90)
                                count = 1;

                            if (count >= 1) {
                                int id = Manor.INSTANCE.getMatureCrop(crop.cropId);
                                ItemInstance item = cwh.addItem(id, count, "CastleManorPeriod");
                                Log.LogAddItem(clan, "Manor Period Start", item, count);
                            }
                        }

                        // reserved and not used money giving back to treasury
                        if (crop.getAmount() > 0) {
                            c.addToTreasuryNoTax(crop.getAmount() * crop.price, false, false);
                            Log.add(c.getName() + "|" + crop.getAmount() * crop.price + "|ManorManager|" + crop.getAmount() + "*" + crop.price, "treasury");
                        }

                        c.setCollectedShops(0);
                        c.setCollectedSeed(0);
                    }

                    c.setSeedProduction(c.getSeedProduction(PERIOD_NEXT), PERIOD_CURRENT);
                    c.setCropProcure(c.getCropProcure(PERIOD_NEXT), PERIOD_CURRENT);

                    long manor_cost = c.getManorCost(PERIOD_CURRENT);
                    if (c.getTreasury() < manor_cost) {
                        c.setSeedProduction(getNewSeedsList(c.getId()), PERIOD_NEXT);
                        c.setCropProcure(getNewCropsList(c.getId()), PERIOD_NEXT);
                        Log.add(c.getName() + "|" + manor_cost + "|ManorManager Error@setNextPeriod", "treasury");
                    } else {
                        List<SeedProduction> production = new ArrayList<>();
                        List<CropProcure> procure = new ArrayList<>();
                        for (SeedProduction s : c.getSeedProduction(PERIOD_CURRENT)) {
                            s.setCanProduce(s.getStartProduce());
                            production.add(s);
                        }
                        for (CropProcure cr : c.getCropProcure(PERIOD_CURRENT)) {
                            cr.setAmount(cr.getStartAmount());
                            procure.add(cr);
                        }
                        c.setSeedProduction(production, PERIOD_NEXT);
                        c.setCropProcure(procure, PERIOD_NEXT);
                    }

                    c.saveCropData();
                    c.saveSeedData();

                    // Sending notification to a clan leader
                    PlayerMessageStack.getInstance().mailto(clan.getLeaderId(), new SystemMessage2(SystemMsg.THE_MANOR_INFORMATION_HAS_BEEN_UPDATED));

                    c.setNextPeriodApproved(false);
                }

            }

        });
    }

    private void approveNextPeriod() {
        ResidenceHolder.getCastles().forEach(c -> {
            if (c.getOwnerId() > 0) {
                long manor_cost = c.getManorCost(PERIOD_NEXT);

                if (c.getTreasury() < manor_cost) {
                    c.setSeedProduction(getNewSeedsList(c.getId()), PERIOD_NEXT);
                    c.setCropProcure(getNewCropsList(c.getId()), PERIOD_NEXT);
                    manor_cost = c.getManorCost(PERIOD_NEXT);
                    if (manor_cost > 0)
                        Log.add(c.getName() + "|" + -manor_cost + "|ManorManager Error@approveNextPeriod", "treasury");
                    Clan clan = c.getOwner();
                    PlayerMessageStack.getInstance().mailto(clan.getLeaderId(), new SystemMessage2(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_FUNDS_IN_THE_CLAN_WAREHOUSE_FOR_THE_MANOR_TO_OPERATE));
                } else {
                    c.addToTreasuryNoTax(-manor_cost, false, false);
                    Log.add(c.getName() + "|" + -manor_cost + "|ManorManager", "treasury");
                }
                c.setNextPeriodApproved(true);
            }

        });
    }

    private List<SeedProduction> getNewSeedsList(int castleId) {
        return Manor.INSTANCE.getSeedsForCastle(castleId).stream()
                .map(SeedProduction::new)
                .collect(Collectors.toList());
    }

    private List<CropProcure> getNewCropsList(int castleId) {
        List<Integer> cropsIds = Manor.INSTANCE.getCropsForCastle(castleId);
        return cropsIds.stream()
                .map(CropProcure::new)
                .collect(Collectors.toList());
    }

    public boolean isUnderMaintenance() {
        return underMaintenance;
    }

    private void setUnderMaintenance(boolean mode) {
        underMaintenance = mode;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean mode) {
        disabled = mode;
    }

    public SeedProduction getNewSeedProduction(int id, long amount, long price, long sales) {
        return new SeedProduction(id, amount, price, sales);
    }

    public CropProcure getNewCropProcure(int id, long amount, int type, long price, long buy) {
        return new CropProcure(id, amount, type, buy, price);
    }

    public void save() {
        ResidenceHolder.getCastles().forEach(c -> {
            c.saveSeedData();
            c.saveCropData();
        });
    }

    private class ManorTask extends RunnableImpl {
        @Override
        public void runImpl() {
            int H = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int M = Calendar.getInstance().get(Calendar.MINUTE);

            if (ServerVariables.isSet("ManorApproved")) { // 06:00 - 20:00
                if (H < NEXT_PERIOD_APPROVE || H > MANOR_REFRESH || H == MANOR_REFRESH && M >= MANOR_REFRESH_MIN) {
                    ServerVariables.unset(var_name);
                    setUnderMaintenance(true);
                    LOG.info("Manor System: Under maintenance mode started");
                }
            } else if (isUnderMaintenance()) { // 20:00 - 20:06
                if (H != MANOR_REFRESH || M >= MANOR_REFRESH_MIN + MAINTENANCE_PERIOD) {
                    setUnderMaintenance(false);
                    LOG.info("Manor System: Next period started");
                    if (isDisabled())
                        return;
                    setNextPeriod();
                    try {
                        save();
                    } catch (RuntimeException e) {
                        LOG.info("Manor System: Failed to save manor data: ", e);
                    }
                }
            } else if (H > NEXT_PERIOD_APPROVE && H < MANOR_REFRESH || H == NEXT_PERIOD_APPROVE && M >= NEXT_PERIOD_APPROVE_MIN) {
                ServerVariables.set(var_name);
                LOG.info("Manor System: Next period approved");
                if (isDisabled())
                    return;
                approveNextPeriod();
            }
        }
    }
}
