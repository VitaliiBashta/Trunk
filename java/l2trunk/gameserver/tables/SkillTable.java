package l2trunk.gameserver.tables;

import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.skills.SkillsEngine;

import java.util.HashMap;
import java.util.Map;

public final class SkillTable {
    private static final SkillTable _instance = new SkillTable();

    private Map<Integer, Skill> _skills;
    private Map<Integer, Integer> _maxLevelsTable;
    private Map<Integer, Integer> _baseLevelsTable;

    public static SkillTable getInstance() {
        return _instance;
    }

    public static int getSkillHashCode(Skill skill) {
        return SkillTable.getSkillHashCode(skill.getId(), skill.getLevel());
    }

    private static int getSkillHashCode(int skillId, int skillLevel) {
        return skillId * 1000 + skillLevel;
    }

    public void load() {
        _skills = SkillsEngine.getInstance().loadAllSkills();
        makeLevelsTable();
    }

    public void reload() {
        _skills = SkillsEngine.getInstance().loadAllSkills();
    }

    public Skill getInfo(int skillId, int level) {
        return _skills.get(getSkillHashCode(skillId, level));
    }

    public int getMaxLevel(int skillId) {
        return _maxLevelsTable.get(skillId);
    }

    public int getBaseLevel(int skillId) {
        return _baseLevelsTable.get(skillId);
    }

    private void makeLevelsTable() {
        _maxLevelsTable = new HashMap<>();
        _baseLevelsTable = new HashMap<>();
        for (Skill s : _skills.values()) {
            int skillId = s.getId();
            int level = s.getLevel();
            int maxLevel = 0;
            if (_maxLevelsTable.containsKey(skillId))
                maxLevel = _maxLevelsTable.get(skillId);
            if (level > maxLevel)
                _maxLevelsTable.put(skillId, level);
            if (_baseLevelsTable.get(skillId) != null)
                _baseLevelsTable.put(skillId, s.getBaseLevel());
        }
    }
}