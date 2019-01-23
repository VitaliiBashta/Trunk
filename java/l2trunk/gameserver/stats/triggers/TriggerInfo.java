package l2trunk.gameserver.stats.triggers;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.conditions.Condition;

import java.util.ArrayList;
import java.util.List;

public final class TriggerInfo extends Skill.AddedSkill {
    private final TriggerType type;
    private final double chance;
    private List<Condition> _conditions = new ArrayList<>();

    public TriggerInfo(int id, int level, TriggerType type, double chance) {
        super(id, level);
        this.type = type;
        this.chance = chance;
    }

    public final void addCondition(Condition c) {
        _conditions.add(c);
    }

    public boolean checkCondition(Creature actor, Creature target, Creature aimTarget, Skill owner, double damage) {
        // Скилл проверяется и кастуется на aimTarget
        if (getSkill().checkTarget(actor, aimTarget, aimTarget, false, false) != null)
            return false;

        Env env = new Env();
        env.character = actor;
        env.skill = owner;
        if (owner != null && owner.id == Skill.SKILL_SERVITOR_SHARE)
            env.target = actor.getPlayer().getPet();
        else {
            env.target = target;
        }
        env.value = damage;

        for (Condition c : _conditions)
            if (!c.test(env))
                return false;
        return true;
    }

    public TriggerType getType() {
        return type;
    }

    public double getChance() {
        return chance;
    }
}
