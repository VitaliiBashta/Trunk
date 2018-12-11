package l2trunk.gameserver.tables;

import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.skills.SkillsEngine;

import java.util.HashMap;
import java.util.Map;

public enum SkillTable {
    INSTANCE;
    private Map<Integer, Skill> skills;
    private Map<Integer, Integer> maxLevelsTable;
    private Map<Integer, Integer> baseLevelsTable;

    public static int getSkillHashCode(Skill skill) {
        return SkillTable.getSkillHashCode(skill.getId(), skill.getLevel());
    }

    private static int getSkillHashCode(int skillId, int skillLevel) {
        return skillId * 1000 + skillLevel;
    }

    public void load() {
        skills = SkillsEngine.INSTANCE.loadAllSkills();
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
            int skillId = s.getId();
            int level = s.getLevel();
            int maxLevel = 0;
            if (maxLevelsTable.containsKey(skillId))
                maxLevel = maxLevelsTable.get(skillId);
            if (level > maxLevel)
                maxLevelsTable.put(skillId, level);
            if (baseLevelsTable.get(skillId) != null)
                baseLevelsTable.put(skillId, s.getBaseLevel());
        });
    }
}