package l2trunk.gameserver.model.entity.residence;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.dao.ClanDataDAO;
import l2trunk.gameserver.dao.FortressDAO;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.UnitMember;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class Fortress extends Residence {
    public static final long CASTLE_FEE = 25000;
    // type
    public static final int DOMAIN = 0;
    public static final int BOUNDARY = 1;
    // state
    public static final int NOT_DECIDED = 0;
    public static final int INDEPENDENT = 1;
    public static final int CONTRACT_WITH_CASTLE = 2;
    // facility
    public static final int REINFORCE = 0;
    public static final int GUARD_BUFF = 1;
    public static final int DOOR_UPGRADE = 2;
    public static final int DWARVENS = 3;
    public static final int SCOUT = 4;
    public static final int FACILITY_MAX = 5;
    private static final Logger _log = LoggerFactory.getLogger(Fortress.class);
    private static final long REMOVE_CYCLE = 7 * 24; // 7 Fort days may belong owneru
    private static final long REWARD_CYCLE = 6; // every 6 hours
    private final List<Castle> relatedCastles = new ArrayList<>(5);
    private final int[] facilities = new int[FACILITY_MAX];
    // envoy
    private int state;
    private int castleId;
    private int supplyCount;
    private long supplySpawn;

    public Fortress(StatsSet set) {
        super(set);
    }

    @Override
    public ResidenceType getType() {
        return ResidenceType.Fortress;
    }

    @Override
    public void changeOwner(Clan clan) {
        // If a clan is owned by a castle / fortress, we getBonuses it.
        if (clan != null) {
            if (clan.getHasFortress() != 0) {
                Fortress oldFortress = ResidenceHolder.getFortress(clan.getHasFortress());
                if (oldFortress != null)
                    oldFortress.changeOwner(null);
            }
            if (clan.getCastle() != 0) {
                Castle oldCastle = ResidenceHolder.getCastle(clan.getCastle());
                if (oldCastle != null)
                    oldCastle.changeOwner(null);
            }
        }

        // If this fortress is someone captured, it takes away from the fortress
        if (getOwnerId() > 0 && (clan == null || clan.clanId() != getOwnerId())) {
            // Remove Fortress skills with the old owner
            removeSkills();
            Clan oldOwner = getOwner();
            if (oldOwner != null)
                oldOwner.setHasFortress(0);

            cancelCycleTask();
            clearFacility();
        }

        // We provide the new owner of the fortress
        if (clan != null)
            clan.setHasFortress(getId());

        // Save to base
        updateOwnerInDB(clan);

        // We provide Fortress skills to a new owner
        rewardSkills();

        setFortState(NOT_DECIDED, 0);
        setJdbcState(JdbcEntityState.UPDATED);

        update();

        if (clan != null)
            clan.getAllMembers().stream().filter(UnitMember::isOnline).forEach(plr -> plr.getPlayer().getCounters().fortSiegesWon++);
    }

    @Override
    protected void loadData() {
        owner = ClanDataDAO.INSTANCE.getOwner(this);
        FortressDAO.INSTANCE.select(this);
    }

    private void updateOwnerInDB(Clan clan) {
        owner = clan;

        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET hasFortress=0 WHERE hasFortress=? LIMIT 1");
            statement.setInt(1, getId());
            statement.execute();

            if (clan != null) {
                statement = con.prepareStatement("UPDATE clan_data SET hasFortress=? WHERE clan_id=? LIMIT 1");
                statement.setInt(1, getId());
                statement.setInt(2, getOwnerId());
                statement.execute();

                clan.broadcastClanStatus(true, false, false);
            }
        } catch (SQLException e) {
            _log.error("Error while updating Fortress Owner in Database", e);
        }
    }

    public void setFortState(int state, int castleId) {
        this.state = state;
        this.castleId = castleId;
    }

    public int getCastleId() {
        return castleId;
    }

    public int getContractState() {
        return state;
    }

    @Override
    public void chanceCycle() {
        super.chanceCycle();
        if (getCycle() >= REMOVE_CYCLE) {
            getOwner().broadcastToOnlineMembers(SystemMsg.ENEMY_BLOOD_PLEDGES_HAVE_INTRUDED_INTO_THE_FORTRESS);
            changeOwner(null);
            return;
        }

        setPaidCycle(getPaidCycle() + 1);
        // if we add a multiple REWARD_CYCLE Revard
        if (getPaidCycle() % REWARD_CYCLE == 0) {
            setPaidCycle(0);
            setRewardCount(getRewardCount() + 1);

            if (getContractState() == CONTRACT_WITH_CASTLE) {
                Castle castle = ResidenceHolder.getCastle(castleId);
                if (castle.getOwner() == null || castle.getOwner().getReputationScore() < 2 || owner.getWarehouse().getCountOf(ItemTemplate.ITEM_ID_ADENA) > CASTLE_FEE) {
                    setSupplyCount(0);
                    setFortState(INDEPENDENT, 0);
                    clearFacility();
                } else {
                    if (supplyCount < 6) {
                        castle.getOwner().incReputation(-2, false, "Fortress:chanceCycle():" + getId());
                        owner.getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, CASTLE_FEE, "Fortress Cycle");
                        supplyCount++;
                    }
                }
            }
        }
    }

    @Override
    public void update() {
        FortressDAO.INSTANCE.update(this);
    }

    public int getSupplyCount() {
        return supplyCount;
    }

    public void setSupplyCount(int c) {
        supplyCount = c;
    }

    public int getFacilityLevel(int type) {
        return facilities[type];
    }

    public void setFacilityLevel(int type, int val) {
        facilities[type] = val;
    }

    private void clearFacility() {
        for (int i = 0; i < facilities.length; i++)
            facilities[i] = 0;
    }

    void addRelatedCastle(Castle castle) {
        relatedCastles.add(castle);
    }

    public List<Castle> getRelatedCastles() {
        return relatedCastles;
    }
}