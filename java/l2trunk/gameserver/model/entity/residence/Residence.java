package l2trunk.gameserver.model.entity.residence;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.dao.JdbcEntity;
import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class Residence implements JdbcEntity {
    private static final long CYCLE_TIME = 60 * 60 * 1000L; // 1 час
    private static final Logger _log = LoggerFactory.getLogger(Residence.class);
    final int id;
    final Calendar siegeDate = Calendar.getInstance();
    private final String name;
    private final List<ResidenceFunction> _functions = new ArrayList<>();
    private final List<Skill> skills = new ArrayList<>();
    private final Calendar lastSiegeDate = Calendar.getInstance();
    private final Calendar ownDate = Calendar.getInstance();
    // points
    private final List<Location> banishPoints = new ArrayList<>();
    private final List<Location> ownerRestartPoints = new ArrayList<>();
    private final List<Location> otherRestartPoints = new ArrayList<>();
    private final List<Location> chaosRestartPoints = new ArrayList<>();
    Clan owner;
    private Zone zone;
    private SiegeEvent<?, ?> siegeevent;
    // rewards
    private ScheduledFuture<?> cycleTask;
    private JdbcEntityState _jdbcEntityState = JdbcEntityState.CREATED;
    private int cycle;
    private int rewardCount;
    private int paidCycle;

    Residence(StatsSet set) {
        id = set.getInteger("id");
        name = set.getString("name");
    }

    public abstract ResidenceType getType();

    public void init() {
        initZone();
        initEvent();

        loadData();
        loadFunctions();
        rewardSkills();
        startCycleTask();
    }

    void initZone() {
        zone = ReflectionUtils.getZone("residence_" + id);
        zone.setParam("residence", this);
    }

    void initEvent() {
        siegeevent = EventHolder.getEvent(EventType.SIEGE_EVENT, id);
    }

    public <E extends SiegeEvent> E getSiegeEvent() {
        return (E) siegeevent;
    }

    public int getId() {
        return id;
    }

    public final String getName() {
        return name;
    }

    public int getOwnerId() {
        return owner == null ? 0 : owner.clanId();
    }

    public Clan getOwner() {
        return owner;
    }

    public Zone getZone() {
        return zone;
    }

    protected abstract void loadData();

    public abstract void changeOwner(Clan clan);

    public Calendar getOwnDate() {
        return ownDate;
    }

    public Calendar getSiegeDate() {
        return siegeDate;
    }

    public Calendar getLastSiegeDate() {
        return lastSiegeDate;
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    public void addFunction(ResidenceFunction function) {
        _functions.add(function);
    }

    public boolean checkIfInZone(Location loc, Reflection ref) {
        return getZone() != null && getZone().checkIfInZone(loc, ref);
    }

    public void banishForeigner() {
        zone.getInsidePlayers()
                .filter(pl -> pl.getClanId() != getOwnerId())
                .forEach(pl -> pl.teleToLocation(getBanishPoint()));
    }


    /**
     * Gets the clan that owns the residence skills
     */
    void rewardSkills() {
        Clan owner = getOwner();
        if (owner != null) {
            for (Skill skill : skills) {
                owner.addSkill(skill, false);
                owner.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED).addSkillName(skill));
            }
        }
    }

    void removeSkills() {
        Clan owner = getOwner();
        if (owner != null) {
            skills.forEach(skill -> owner.removeSkill(skill.id));
        }
    }

    void loadFunctions() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * FROM residence_functions WHERE id = ?")) {
            statement.setInt(1, getId());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                ResidenceFunction function = getFunction(rs.getInt("type"));
                function.setLvl(rs.getInt("lvl"));
                function.setEndTimeInMillis(rs.getInt("endTime") * 1000L);
                function.setInDebt(rs.getBoolean("inDebt"));
                function.setActive(true);
                startAutoTaskForFunction(function);
            }
        } catch (SQLException e) {
            _log.warn("Residence: loadFunctions()", e);
        }
    }

    public boolean isFunctionActive(int type) {
        ResidenceFunction function = getFunction(type);
        return function != null && function.isActive() && function.getLevel() > 0;
    }

    public ResidenceFunction getFunction(int type) {
        for (ResidenceFunction _function : _functions)
            if (_function.getType() == type)
                return _function;
        return null;
    }

    public boolean updateFunctions(int type, int level) {
        Clan clan = getOwner();
        if (clan == null)
            return false;

        long count = clan.getAdenaCount();

        ResidenceFunction function = getFunction(type);
        if (function == null)
            return false;

        if (function.isActive() && function.getLevel() == level)
            return true;

        int lease = level == 0 ? 0 : getFunction(type).getLease(level);

        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            PreparedStatement statement;

            if (!function.isActive()) {
                if (count >= lease)
                    clan.getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, lease, "Residence updateFunctions");
                else
                    return false;

                long time = Calendar.getInstance().getTimeInMillis() + 86400000;

                statement = con.prepareStatement("REPLACE residence_functions SET id=?, type=?, lvl=?, endTime=?");
                statement.setInt(1, getId());
                statement.setInt(2, type);
                statement.setInt(3, level);
                statement.setInt(4, (int) (time / 1000));
                statement.execute();

                function.setLvl(level);
                function.setEndTimeInMillis(time);
                function.setActive(true);
                startAutoTaskForFunction(function);
            } else {
                if (count >= lease - getFunction(type).getLease()) {
                    if (lease > getFunction(type).getLease())
                        clan.getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, lease - getFunction(type).getLease(), "Residence updateFunctions");
                } else
                    return false;

                statement = con.prepareStatement("REPLACE residence_functions SET id=?, type=?, lvl=?");
                statement.setInt(1, getId());
                statement.setInt(2, type);
                statement.setInt(3, level);
                statement.execute();

                function.setLvl(level);
            }
        } catch (SQLException e) {
            _log.warn("Exception: SiegeUnit.updateFunctions(int type, int lvl, int lease, long rate, long time, boolean addNew): ", e);
        }
        return true;
    }

    private void removeFunction(int type) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("DELETE FROM residence_functions WHERE id=? AND type=?")) {
            statement.setInt(1, getId());
            statement.setInt(2, type);
            statement.execute();
        } catch (SQLException e) {
            _log.warn("Exception: removeFunctions(int type): ", e);
        }
    }

    private void startAutoTaskForFunction(ResidenceFunction function) {
        if (getOwnerId() == 0)
            return;

        Clan clan = getOwner();

        if (clan == null)
            return;

        if (function.getEndTimeInMillis() > System.currentTimeMillis())
            ThreadPoolManager.INSTANCE.schedule(() -> startAutoTaskForFunction(function), function.getEndTimeInMillis() - System.currentTimeMillis());
        else if (function.isInDebt() && clan.getAdenaCount() >= function.getLease()) // if getPlayer didn't pay before add extra fee
        {
            clan.getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, function.getLease(), "Residence Functions Auto Task");
            function.updateRentTime(false);
            ThreadPoolManager.INSTANCE.schedule(() -> startAutoTaskForFunction(function), function.getEndTimeInMillis() - System.currentTimeMillis());
        } else if (!function.isInDebt()) {
            function.setInDebt(true);
            function.updateRentTime(true);
            ThreadPoolManager.INSTANCE.schedule(() -> startAutoTaskForFunction(function), function.getEndTimeInMillis() - System.currentTimeMillis());
        } else {
            function.setLvl(0);
            function.setActive(false);
            removeFunction(function.getType());
        }
    }

    @Override
    public JdbcEntityState getJdbcState() {
        return _jdbcEntityState;
    }

    @Override
    public void setJdbcState(JdbcEntityState state) {
        _jdbcEntityState = state;
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException();
    }

    void cancelCycleTask() {
        cycle = 0;
        paidCycle = 0;
        rewardCount = 0;
        if (cycleTask != null) {
            cycleTask.cancel(false);
            cycleTask = null;
        }

        setJdbcState(JdbcEntityState.UPDATED);
    }

    public void startCycleTask() {
        if (owner == null)
            return;

        long ownedTime = getOwnDate().getTimeInMillis();
        if (ownedTime == 0)
            return;
        long diff = System.currentTimeMillis() - ownedTime;
        while (diff >= CYCLE_TIME)
            diff -= CYCLE_TIME;

        cycleTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(() -> {
            chanceCycle();
            update();
        }, diff, CYCLE_TIME);
    }

    void chanceCycle() {
        setCycle(getCycle() + 1);

        setJdbcState(JdbcEntityState.UPDATED);
    }

    List<Skill> getSkills() {
        return skills;
    }

    public void addBanishPoint(Location loc) {
        banishPoints.add(loc);
    }

    public void addOwnerRestartPoint(Location loc) {
        ownerRestartPoints.add(loc);
    }

    public void addOtherRestartPoint(Location loc) {
        otherRestartPoints.add(loc);
    }

    public void addChaosRestartPoint(Location loc) {
        chaosRestartPoints.add(loc);
    }

    private Location getBanishPoint() {
        if (banishPoints.isEmpty())
            return null;
        return Rnd.get(banishPoints);
    }

    public Location getOwnerRestartPoint() {
        if (ownerRestartPoints.isEmpty())
            return null;
        return Rnd.get(ownerRestartPoints);
    }

    public Location getOtherRestartPoint() {
        if (otherRestartPoints.isEmpty())
            return null;
        return Rnd.get(otherRestartPoints);
    }

    private Location getChaosRestartPoint() {
        if (chaosRestartPoints.isEmpty())
            return null;
        return Rnd.get(chaosRestartPoints);
    }

    public Location getNotOwnerRestartPoint(Player player) {
        return player.getKarma() > 0 ? getChaosRestartPoint() : getOtherRestartPoint();
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public long getCycleDelay() {
        if (cycleTask == null)
            return 0;
        return cycleTask.getDelay(TimeUnit.SECONDS);
    }

    public int getPaidCycle() {
        return paidCycle;
    }

    public void setPaidCycle(int paidCycle) {
        this.paidCycle = paidCycle;
    }

    public int getRewardCount() {
        return rewardCount;
    }

    public void setRewardCount(int rewardCount) {
        this.rewardCount = rewardCount;
    }

}