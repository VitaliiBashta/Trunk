package l2trunk.gameserver.tables;

import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.LvlupData;
import l2trunk.gameserver.model.base.ClassId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class LevelUpTable {
    private static final String SELECT_ALL = "SELECT classid, defaulthpbase, defaulthpadd, defaulthpmod, defaultcpbase, defaultcpadd, defaultcpmod, defaultmpbase, defaultmpadd, defaultmpmod, class_lvl FROM lvlupgain";
    private static final String CLASS_LVL = "class_lvl";
    private static final String MP_MOD = "defaultmpmod";
    private static final String MP_ADD = "defaultmpadd";
    private static final String MP_BASE = "defaultmpbase";
    private static final String HP_MOD = "defaulthpmod";
    private static final String HP_ADD = "defaulthpadd";
    private static final String HP_BASE = "defaulthpbase";
    private static final String CP_MOD = "defaultcpmod";
    private static final String CP_ADD = "defaultcpadd";
    private static final String CP_BASE = "defaultcpbase";
    private static final String CLASS_ID = "classid";

    private static final Logger _log = LoggerFactory.getLogger(LevelUpTable.class);

    private static final Map<Integer, LvlupData> _lvltable = new HashMap<>();

    public static LvlupData getTemplate(int classId) {
        return _lvltable.get(classId);
    }

    public static void init() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_ALL);
             ResultSet rset = statement.executeQuery()) {
            LvlupData lvlDat;

            while (rset.next()) {
                lvlDat = new LvlupData();
                lvlDat.set_classid(rset.getInt(CLASS_ID));
                lvlDat.set_classLvl(rset.getInt(CLASS_LVL));
                lvlDat.set_classHpBase(rset.getDouble(HP_BASE));
                lvlDat.set_classHpAdd(rset.getDouble(HP_ADD));
                lvlDat.set_classHpModifier(rset.getDouble(HP_MOD));
                lvlDat.set_classCpBase(rset.getDouble(CP_BASE));
                lvlDat.set_classCpAdd(rset.getDouble(CP_ADD));
                lvlDat.set_classCpModifier(rset.getDouble(CP_MOD));
                lvlDat.set_classMpBase(rset.getDouble(MP_BASE));
                lvlDat.set_classMpAdd(rset.getDouble(MP_ADD));
                lvlDat.set_classMpModifier(rset.getDouble(MP_MOD));

                _lvltable.put(lvlDat.get_classid(), lvlDat);
            }

            _log.info("LevelUpData: Loaded " + _lvltable.size() + " Character Level Up Templates.");
        } catch (SQLException e) {
            _log.warn("Error while creating Lvl up data table", e);
        }
    }

    public LvlupData getTemplate(ClassId classId) {
        return _lvltable.get(classId.getId());
    }
}
