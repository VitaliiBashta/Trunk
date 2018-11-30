package l2trunk.gameserver.templates;

import l2trunk.gameserver.model.Skill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CubicTemplate {
    private  int id;
    private  int level;
    private  int delay;

    private final List<Map.Entry<Integer, List<SkillInfo>>> _skills = new ArrayList<>(3);

    public CubicTemplate() {
    }

    public CubicTemplate(int id, int level, int delay) {
        this.id = id;
        this.level = level;
        this.delay = delay;
    }

    public void putSkills(int chance, List<SkillInfo> skill) {
        _skills.add(new AbstractMap.SimpleImmutableEntry<>(chance, skill));
    }

    public Iterable<Map.Entry<Integer, List<SkillInfo>>> getSkills() {
        return _skills;
    }

    public int getDelay() {
        return delay;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public enum ActionType {
        ATTACK,
        DEBUFF,
        CANCEL,
        HEAL
    }

    public static class SkillInfo {
        private final Skill skill;
        private final int chance;
        private final ActionType actionType;
        private final boolean canAttackDoor;
        private final Map<Integer, Integer> chanceList;

        public SkillInfo(Skill skill, int chance, ActionType actionType, boolean canAttackDoor, Map<Integer, Integer> set) {
            this.skill = skill;
            this.chance = chance;
            this.actionType = actionType;
            this.canAttackDoor = canAttackDoor;
            chanceList = set;
        }

        public int getChance() {
            return chance;
        }

        public ActionType getActionType() {
            return actionType;
        }

        public Skill getSkill() {
            return skill;
        }

        public boolean isCanAttackDoor() {
            return canAttackDoor;
        }

        public int getChance(int a) {
            return chanceList.get(a);
        }
    }
}
