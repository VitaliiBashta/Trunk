package l2trunk.gameserver.tables;

import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.skills.SkillsEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum SkillTable {
    INSTANCE;
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private Map<Integer, Skill> skills = Collections.emptyMap();
    private Map<Integer, Integer> maxLevelsTable;
    private Map<Integer, Integer> baseLevelsTable;

    public static int getSkillHashCode(Skill skill) {
        return SkillTable.getSkillHashCode(skill.id, skill.level);
    }

    private static int getSkillHashCode(int skillId, int skillLevel) {
        return skillId * 1000 + skillLevel;
    }

    public void load() {
        skills = SkillsEngine.INSTANCE.loadAllSkills();
        int maxId = skills.values().stream().mapToInt(skill ->skill.id).max().orElse(0);
        int maxLvl = skills.values().stream().mapToInt(skill -> skill.level).max().orElse(0);
        LOG.info("SkillsEngine: Loaded " + skills.values().size() + " skill templates from XML files. Max id: " + maxId + ", max level: " + maxLvl);

        makeLevelsTable();
    }

    public void reload() {
        skills = SkillsEngine.INSTANCE.loadAllSkills();
    }

    public Skill getInfo(int skillId) {
        return getInfo(skillId, 1);
    }

    public Skill getInfo(int skillId, int level) {
        return skills.get(getSkillHashCode(skillId, level));
    }

    public int getMaxLevel(int skillId) {
        return maxLevelsTable.get(skillId);
    }

    public int getBaseLevel(int skillId) {
        return baseLevelsTable.get(skillId);
    }

    private void makeLevelsTable() {
        maxLevelsTable = new HashMap<>();
        baseLevelsTable = new HashMap<>();
        skills.values().forEach(s -> {
            int skillId = s.id;
            int level = s.level;
            int maxLevel = 0;
            if (maxLevelsTable.containsKey(skillId))
                maxLevel = maxLevelsTable.get(skillId);
            if (level > maxLevel)
                maxLevelsTable.put(skillId, level);
            if (baseLevelsTable.get(skillId) != null)
                baseLevelsTable.put(skillId, s.baseLevel);
        });
    }
}