package l2trunk.gameserver.model.entity.residence;

import l2trunk.commons.dao.JdbcEntity;
import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.commons.threading.RunnableImpl;
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
import l2trunk.gameserver.templates.StatsSet;
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
    final Calendar _siegeDate = Calendar.getInstance();
    private final String name;
    private final List<ResidenceFunction> _functions = new ArrayList<>();
    private final List<Skill> skills = new ArrayList<>();
    private final Calendar lastSiegeDate = Calendar.getInstance();
    private final Calendar ownDate = Calendar.getInstance();
    // points
    private final List<Location> _banishPoints = new ArrayList<>();
    private final List<Location> _ownerRestartPoints = new ArrayList<>();
    private final List<Location> _otherRestartPoints = new ArrayList<>();
    private final List<Location> _chaosRestartPoints = new ArrayList<>();
    Clan _owner;
    private Zone _zone;
    private SiegeEvent<?, ?> _siegeEvent;
    // rewards
    private ScheduledFuture<?> _cycleTask;
    private JdbcEntityState _jdbcEntityState = JdbcEntityState.CREATED;
    private int _cycle;
    private int _rewardCount;
    private int _paidCycle;

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
        _zone = ReflectionUtils.getZone("residence_" + id);
        _zone.setParam("residence", this);
    }

    void initEvent() {
        _siegeEvent = EventHolder.getEvent(EventType.SIEGE_EVENT, id);
    }

    public <E extends SiegeEvent> E getSiegeEvent() {
        return (E) _siegeEvent;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getOwnerId() {
        return _owner == null ? 0 : _owner.getClanId();
    }

    public Clan getOwner() {
        return _owner;
    }

    public Zone getZone() {
        return _zone;
    }

    protected abstract void loadData();

    public abstract void changeOwner(Clan clan);

    public Calendar getOwnDate() {
        return ownDate;
    }

    public Calendar getSiegeDate() {
        return _siegeDate;
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
        return checkIfInZone(loc.x, loc.y, loc.z, ref);
    }

    public boolean checkIfInZone(int x, int y, int z, Reflection ref) {
        return getZone() != null && getZone().checkIfInZone(x, y, z, ref);
    }

    public void banishForeigner() {
        for (Player player : _zone.getInsidePlayers()) {
            if (player.getClanId() == getOwnerId())
                continue;

            player.teleToLocation(getBanishPoint());
        }
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

    /**
     * Removes the clan that owns the residence skills
     */
    void removeSkills() {
        Clan owner = getOwner();
        if (owner != null) {
            for (Skill skill : skills)
                owner.removeSkill(skill.getId());
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
            ThreadPoolManager.INSTANCE.schedule(new AutoTaskForFunctions(function), function.getEndTimeInMillis() - System.currentTimeMillis());
        else if (function.isInDebt() && clan.getAdenaCount() >= function.getLease()) // if player didn't pay before add extra fee
        {
            clan.getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, function.getLease(), "Residence Functions Auto Task");
            function.updateRentTime(false);
            ThreadPoolManager.INSTANCE.schedule(new AutoTaskForFunctions(function), function.getEndTimeInMillis() - System.currentTimeMillis());
        } else if (!function.isInDebt()) {
            function.setInDebt(true);
            function.updateRentTime(true);
            ThreadPoolManager.INSTANCE.schedule(new AutoTaskForFunctions(function), function.getEndTimeInMillis() - System.currentTimeMillis());
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
        _cycle = 0;
        _paidCycle = 0;
        _rewardCount = 0;
        if (_cycleTask != null) {
            _cycleTask.cancel(false);
            _cycleTask = null;
        }

        setJdbcState(JdbcEntityState.UPDATED);
    }

    public void startCycleTask() {
        if (_owner == null)
            return;

        long ownedTime = getOwnDate().getTimeInMillis();
        if (ownedTime == 0)
            return;
        long diff = System.currentTimeMillis() - ownedTime;
        while (diff >= CYCLE_TIME)
            diff -= CYCLE_TIME;

        _cycleTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new ResidenceCycleTask(), diff, CYCLE_TIME);
    }

    void chanceCycle() {
        setCycle(getCycle() + 1);

        setJdbcState(JdbcEntityState.UPDATED);
    }

    List<Skill> getSkills() {
        return skills;
    }

    public void addBanishPoint(Location loc) {
        _banishPoints.add(loc);
    }

    public void addOwnerRestartPoint(Location loc) {
        _ownerRestartPoints.add(loc);
    }

    public void addOtherRestartPoint(Location loc) {
        _otherRestartPoints.add(loc);
    }

    public void addChaosRestartPoint(Location loc) {
        _chaosRestartPoints.add(loc);
    }

    Location getBanishPoint() {
        if (_banishPoints.isEmpty())
            return null;
        return _banishPoints.get(Rnd.get(_banishPoints.size()));
    }

    public Location getOwnerRestartPoint() {
        if (_ownerRestartPoints.isEmpty())
            return null;
        return _ownerRestartPoints.get(Rnd.get(_ownerRestartPoints.size()));
    }

    public Location getOtherRestartPoint() {
        if (_otherRestartPoints.isEmpty())
            return null;
        return _otherRestartPoints.get(Rnd.get(_otherRestartPoints.size()));
    }

    private Location getChaosRestartPoint() {
        if (_chaosRestartPoints.isEmpty())
            return null;
        return _chaosRestartPoints.get(Rnd.get(_chaosRestartPoints.size()));
    }

    public Location getNotOwnerRestartPoint(Player player) {
        return player.getKarma() > 0 ? getChaosRestartPoint() : getOtherRestartPoint();
    }

    public int getCycle() {
        return _cycle;
    }

    public void setCycle(int cycle) {
        _cycle = cycle;
    }

    public long getCycleDelay() {
        if (_cycleTask == null)
            return 0;
        return _cycleTask.getDelay(TimeUnit.SECONDS);
    }

    public int getPaidCycle() {
        return _paidCycle;
    }

    public void setPaidCycle(int paidCycle) {
        _paidCycle = paidCycle;
    }

    public int getRewardCount() {
        return _rewardCount;
    }

    public void setRewardCount(int rewardCount) {
        _rewardCount = rewardCount;
    }

    public class ResidenceCycleTask extends RunnableImpl {
        @Override
        public void runImpl() {
            chanceCycle();
            update();
        }
    }

    private class AutoTaskForFunctions extends RunnableImpl {
        final ResidenceFunction _function;

        AutoTaskForFunctions(ResidenceFunction function) {
            _function = function;
        }

        @Override
        public void runImpl() {
            startAutoTaskForFunction(_function);
        }
    }
}