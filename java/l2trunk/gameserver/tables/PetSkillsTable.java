package l2trunk.gameserver.tables;

import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.SkillLearn;
import l2trunk.gameserver.model.Summon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PetSkillsTable {
    INSTANCE;
    private final Logger LOG = LoggerFactory.getLogger(PetSkillsTable.class);
    private final Map<Integer, List<SkillLearn>> skillTrees = new HashMap<>();

    public void reload() {
//        _instance = new PetSkillsTable();
    }

    public void load() {
        int npcId = 0;
        int count = 0;

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * FROM pets_skills ORDER BY templateId");
             ResultSet rset = statement.executeQuery()) {

            while (rset.next()) {
                npcId = rset.getInt("templateId");
                int id = rset.getInt("skillId");
                int lvl = rset.getInt("skillLvl");
                int minLvl = rset.getInt("minLvl");

                List<SkillLearn> list = skillTrees.computeIfAbsent(npcId, k -> new ArrayList<>());

                SkillLearn skillLearn = new SkillLearn(id, lvl, minLvl, 0, 0, 0, false);
                list.add(skillLearn);
                count++;
            }
        } catch (SQLException e) {
            LOG.error("Error while creating pet skill tree (Pet ID " + npcId + ')', e);
        }

        LOG.info("PetSkillsTable: Loaded " + count + " skills.");
    }

    public int getAvailableLevel(Summon cha, int skillId) {
        List<SkillLearn> skills = skillTrees.get(cha.getNpcId());
        if (skills == null)
            return 0;

        int lvl = 0;
        for (SkillLearn temp : skills) {
            if (temp.id() != skillId)
                continue;
            if (temp.getLevel() == 0) {
                if (cha.getLevel() < 70) {
                    lvl = cha.getLevel() / 10;
                    if (lvl <= 0)
                        lvl = 1;
                } else
                    lvl = 7 + (cha.getLevel() - 70) / 5;

                // formula usable for skill that have 10 or more skill levels
                int maxLvl = SkillTable.INSTANCE.getMaxLevel(temp.id());
                if (lvl > maxLvl)
                    lvl = maxLvl;
                break;
            } else if (temp.getMinLevel() <= cha.getLevel())
                if (temp.getLevel() > lvl)
                    lvl = temp.getLevel();
        }
        return lvl;
    }
}