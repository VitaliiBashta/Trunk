package l2trunk.gameserver.model.entity.residence;

import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.TeleportLocation;
import l2trunk.gameserver.tables.SkillTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

public final class ResidenceFunction {
    // residence functions
    public static final int TELEPORT = 1;
    public static final int ITEM_CREATE = 2;
    public static final int RESTORE_HP = 3;
    public static final int RESTORE_MP = 4;
    public static final int RESTORE_EXP = 5;
    public static final int SUPPORT = 6;
    public static final int CURTAIN = 7;
    public static final int PLATFORM = 8;
    public static final String W = "W";
    public static final String M = "M";
    private static final String A = "";
    private static final Logger _log = LoggerFactory.getLogger(ResidenceFunction.class);
    private static final Object[][][] buffs_template = {{
            // occupation 0 - no buff
    },
            {
                    // occupation 1
                    {SkillTable.INSTANCE.getInfo(4342, 1), A},
                    {SkillTable.INSTANCE.getInfo(4343, 1), A},
                    {SkillTable.INSTANCE.getInfo(4344, 1), A},
                    {SkillTable.INSTANCE.getInfo(4346, 1), A},
                    {SkillTable.INSTANCE.getInfo(4345, 1), W},},
            {
                    // occupation 2
                    {SkillTable.INSTANCE.getInfo(4342, 2), A},
                    {SkillTable.INSTANCE.getInfo(4343, 3), A},
                    {SkillTable.INSTANCE.getInfo(4344, 3), A},
                    {SkillTable.INSTANCE.getInfo(4346, 4), A},
                    {SkillTable.INSTANCE.getInfo(4345, 3), W},},
            {
                    // occupation 3
                    {SkillTable.INSTANCE.getInfo(4342, 2), A},
                    {SkillTable.INSTANCE.getInfo(4343, 3), A},
                    {SkillTable.INSTANCE.getInfo(4344, 3), A},
                    {SkillTable.INSTANCE.getInfo(4346, 4), A},
                    {SkillTable.INSTANCE.getInfo(4345, 3), W},},
            {
                    // occupation 4
                    {SkillTable.INSTANCE.getInfo(4342, 2), A},
                    {SkillTable.INSTANCE.getInfo(4343, 3), A},
                    {SkillTable.INSTANCE.getInfo(4344, 3), A},
                    {SkillTable.INSTANCE.getInfo(4346, 4), A},
                    {SkillTable.INSTANCE.getInfo(4345, 3), W},
                    {SkillTable.INSTANCE.getInfo(4347, 2), A},
                    {SkillTable.INSTANCE.getInfo(4349, 1), A},
                    {SkillTable.INSTANCE.getInfo(4350, 1), W},
                    {SkillTable.INSTANCE.getInfo(4348, 2), A},},
            {
                    // occupation 5
                    {SkillTable.INSTANCE.getInfo(4342, 2), A},
                    {SkillTable.INSTANCE.getInfo(4343, 3), A},
                    {SkillTable.INSTANCE.getInfo(4344, 3), A},
                    {SkillTable.INSTANCE.getInfo(4346, 4), A},
                    {SkillTable.INSTANCE.getInfo(4345, 3), W},
                    {SkillTable.INSTANCE.getInfo(4347, 2), A},
                    {SkillTable.INSTANCE.getInfo(4349, 1), A},
                    {SkillTable.INSTANCE.getInfo(4350, 1), W},
                    {SkillTable.INSTANCE.getInfo(4348, 2), A},
                    {SkillTable.INSTANCE.getInfo(4351, 2), M},
                    {SkillTable.INSTANCE.getInfo(4352, 1), A},
                    {SkillTable.INSTANCE.getInfo(4353, 2), W},
                    {SkillTable.INSTANCE.getInfo(4358, 1), W},
                    {SkillTable.INSTANCE.getInfo(4354, 1), W},},
            {
                    // occupation 6 - unused
            },
            {
                    // occupation 7
                    {SkillTable.INSTANCE.getInfo(4342, 2), A},
                    {SkillTable.INSTANCE.getInfo(4343, 3), A},
                    {SkillTable.INSTANCE.getInfo(4344, 3), A},
                    {SkillTable.INSTANCE.getInfo(4346, 4), A},
                    {SkillTable.INSTANCE.getInfo(4345, 3), W},
                    {SkillTable.INSTANCE.getInfo(4347, 6), A},
                    {SkillTable.INSTANCE.getInfo(4349, 2), A},
                    {SkillTable.INSTANCE.getInfo(4350, 4), W},
                    {SkillTable.INSTANCE.getInfo(4348, 6), A},
                    {SkillTable.INSTANCE.getInfo(4351, 6), M},
                    {SkillTable.INSTANCE.getInfo(4352, 2), A},
                    {SkillTable.INSTANCE.getInfo(4353, 6), W},
                    {SkillTable.INSTANCE.getInfo(4358, 3), W},
                    {SkillTable.INSTANCE.getInfo(4354, 4), W},},
            {
                    // occupation 8
                    {SkillTable.INSTANCE.getInfo(4342, 2), A},
                    {SkillTable.INSTANCE.getInfo(4343, 3), A},
                    {SkillTable.INSTANCE.getInfo(4344, 3), A},
                    {SkillTable.INSTANCE.getInfo(4346, 4), A},
                    {SkillTable.INSTANCE.getInfo(4345, 3), W},
                    {SkillTable.INSTANCE.getInfo(4347, 6), A},
                    {SkillTable.INSTANCE.getInfo(4349, 2), A},
                    {SkillTable.INSTANCE.getInfo(4350, 4), W},
                    {SkillTable.INSTANCE.getInfo(4348, 6), A},
                    {SkillTable.INSTANCE.getInfo(4351, 6), M},
                    {SkillTable.INSTANCE.getInfo(4352, 2), A},
                    {SkillTable.INSTANCE.getInfo(4353, 6), W},
                    {SkillTable.INSTANCE.getInfo(4358, 3), W},
                    {SkillTable.INSTANCE.getInfo(4354, 4), W},
                    {SkillTable.INSTANCE.getInfo(4355, 1), M},
                    {SkillTable.INSTANCE.getInfo(4356, 1), M},
                    {SkillTable.INSTANCE.getInfo(4357, 1), W},
                    {SkillTable.INSTANCE.getInfo(4359, 1), W},
                    {SkillTable.INSTANCE.getInfo(4360, 1), W},},
            {
                    // occupation 9 - unused
            },
            {
                    // occupation 10 - unused
            },
            {
                    // occupation 11
                    {SkillTable.INSTANCE.getInfo(4342, 3), A},
                    {SkillTable.INSTANCE.getInfo(4343, 4), A},
                    {SkillTable.INSTANCE.getInfo(4344, 4), A},
                    {SkillTable.INSTANCE.getInfo(4346, 5), A},
                    {SkillTable.INSTANCE.getInfo(4345, 4), W},},
            {
                    // occupation 12
                    {SkillTable.INSTANCE.getInfo(4342, 4), A},
                    {SkillTable.INSTANCE.getInfo(4343, 6), A},
                    {SkillTable.INSTANCE.getInfo(4344, 6), A},
                    {SkillTable.INSTANCE.getInfo(4346, 8), A},
                    {SkillTable.INSTANCE.getInfo(4345, 6), W},},
            {
                    // occupation 13
                    {SkillTable.INSTANCE.getInfo(4342, 4), A},
                    {SkillTable.INSTANCE.getInfo(4343, 6), A},
                    {SkillTable.INSTANCE.getInfo(4344, 6), A},
                    {SkillTable.INSTANCE.getInfo(4346, 8), A},
                    {SkillTable.INSTANCE.getInfo(4345, 6), W},},
            {
                    // occupation 14
                    {SkillTable.INSTANCE.getInfo(4342, 4), A},
                    {SkillTable.INSTANCE.getInfo(4343, 6), A},
                    {SkillTable.INSTANCE.getInfo(4344, 6), A},
                    {SkillTable.INSTANCE.getInfo(4346, 8), A},
                    {SkillTable.INSTANCE.getInfo(4345, 6), W},
                    {SkillTable.INSTANCE.getInfo(4347, 8), A},
                    {SkillTable.INSTANCE.getInfo(4349, 3), A},
                    {SkillTable.INSTANCE.getInfo(4350, 5), W},
                    {SkillTable.INSTANCE.getInfo(4348, 8), A},},
            {
                    // occupation 15
                    {SkillTable.INSTANCE.getInfo(4342, 4), A},
                    {SkillTable.INSTANCE.getInfo(4343, 6), A},
                    {SkillTable.INSTANCE.getInfo(4344, 6), A},
                    {SkillTable.INSTANCE.getInfo(4346, 8), A},
                    {SkillTable.INSTANCE.getInfo(4345, 6), W},
                    {SkillTable.INSTANCE.getInfo(4347, 8), A},
                    {SkillTable.INSTANCE.getInfo(4349, 3), A},
                    {SkillTable.INSTANCE.getInfo(4350, 5), W},
                    {SkillTable.INSTANCE.getInfo(4348, 8), A},
                    {SkillTable.INSTANCE.getInfo(4351, 8), M},
                    {SkillTable.INSTANCE.getInfo(4352, 3), A},
                    {SkillTable.INSTANCE.getInfo(4353, 8), W},
                    {SkillTable.INSTANCE.getInfo(4358, 4), W},
                    {SkillTable.INSTANCE.getInfo(4354, 5), W},},
            {
                    // occupation 16 - unused
            },
            {
                    // occupation 17
                    {SkillTable.INSTANCE.getInfo(4342, 4), A},
                    {SkillTable.INSTANCE.getInfo(4343, 6), A},
                    {SkillTable.INSTANCE.getInfo(4344, 6), A},
                    {SkillTable.INSTANCE.getInfo(4346, 8), A},
                    {SkillTable.INSTANCE.getInfo(4345, 6), W},
                    {SkillTable.INSTANCE.getInfo(4347, 12), A},
                    {SkillTable.INSTANCE.getInfo(4349, 4), A},
                    {SkillTable.INSTANCE.getInfo(4350, 8), W},
                    {SkillTable.INSTANCE.getInfo(4348, 12), A},
                    {SkillTable.INSTANCE.getInfo(4351, 12), M},
                    {SkillTable.INSTANCE.getInfo(4352, 4), A},
                    {SkillTable.INSTANCE.getInfo(4353, 12), W},
                    {SkillTable.INSTANCE.getInfo(4358, 6), W},
                    {SkillTable.INSTANCE.getInfo(4354, 8), W},},
            {
                    // occupation 18
                    {SkillTable.INSTANCE.getInfo(4342, 4), A},
                    {SkillTable.INSTANCE.getInfo(4343, 6), A},
                    {SkillTable.INSTANCE.getInfo(4344, 6), A},
                    {SkillTable.INSTANCE.getInfo(4346, 8), A},
                    {SkillTable.INSTANCE.getInfo(4345, 6), W},
                    {SkillTable.INSTANCE.getInfo(4347, 12), A},
                    {SkillTable.INSTANCE.getInfo(4349, 4), A},
                    {SkillTable.INSTANCE.getInfo(4350, 8), W},
                    {SkillTable.INSTANCE.getInfo(4348, 12), A},
                    {SkillTable.INSTANCE.getInfo(4351, 12), M},
                    {SkillTable.INSTANCE.getInfo(4352, 4), A},
                    {SkillTable.INSTANCE.getInfo(4353, 12), W},
                    {SkillTable.INSTANCE.getInfo(4358, 6), W},
                    {SkillTable.INSTANCE.getInfo(4354, 8), W},
                    {SkillTable.INSTANCE.getInfo(4355, 4), M},
                    {SkillTable.INSTANCE.getInfo(4356, 4), M},
                    {SkillTable.INSTANCE.getInfo(4357, 3), W},
                    {SkillTable.INSTANCE.getInfo(4359, 4), W},
                    {SkillTable.INSTANCE.getInfo(4360, 4), W},},};
    private final int id;
    private final int type;
    private final Calendar endDate;
    private final Map<Integer, Integer> _leases = new ConcurrentSkipListMap<>();
    private final Map<Integer, List<TeleportLocation>> teleports = new ConcurrentSkipListMap<>();
    private final Map<Integer, int[]> _buylists = new ConcurrentSkipListMap<>();
    private final Map<Integer, Object[][]> buffs = new ConcurrentSkipListMap<>();
    private int level;
    private boolean inDebt;
    private boolean active;

    public ResidenceFunction(int id, int type) {
        this.id = id;
        this.type = type;
        endDate = Calendar.getInstance();
    }

    private int getResidenceId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public void setLvl(int lvl) {
        level = lvl;
    }

    public long getEndTimeInMillis() {
        return endDate.getTimeInMillis();
    }

    public void setEndTimeInMillis(long time) {
        endDate.setTimeInMillis(time);
    }

    public boolean isInDebt() {
        return inDebt;
    }

    public void setInDebt(boolean inDebt) {
        this.inDebt = inDebt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void updateRentTime(boolean inDebt) {
        setEndTimeInMillis(System.currentTimeMillis() + 86400000);

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE residence_functions SET endTime=?, inDebt=? WHERE type=? AND id=?")) {
            statement.setInt(1, (int) (getEndTimeInMillis() / 1000));
            statement.setInt(2, inDebt ? 1 : 0);
            statement.setInt(3, getType());
            statement.setInt(4, getResidenceId());
            statement.executeUpdate();
        } catch (SQLException e) {
            _log.error("Error while updating Residence Function Rent Time", e);
        }
    }

    public List<TeleportLocation> getTeleports() {
        return teleports.get(level);
    }

    public void addTeleports(int level, List<TeleportLocation> teleports) {
        this.teleports.put(level, teleports);
    }

    public int getLease() {
        if (level == 0)
            return 0;
        return getLease(level);
    }

    public int getLease(int level) {
        return _leases.get(level);
    }

    public void addLease(int level, int lease) {
        _leases.put(level, lease);
    }

    public int[] getBuylist() {
        return getBuylist(level);
    }

    private int[] getBuylist(int level) {
        return _buylists.get(level);
    }

    public void addBuylist(int level, int[] buylist) {
        _buylists.put(level, buylist);
    }

    public Object[][] getBuffs() {
        return getBuffs(level);
    }

    private Object[][] getBuffs(int level) {
        return buffs.get(level);
    }

    public void addBuffs(int level) {
        buffs.put(level, buffs_template[level]);
    }

    public Set<Integer> getLevels() {
        return _leases.keySet();
    }
}