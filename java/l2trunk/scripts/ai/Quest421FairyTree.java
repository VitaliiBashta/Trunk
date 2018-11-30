package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;

public final class Quest421FairyTree extends Fighter {
    public Quest421FairyTree(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (attacker != null && attacker.isPlayer()) {
            Skill skill = SkillTable.INSTANCE().getInfo(5423, 12);
            skill.getEffects(actor, attacker, false, false);
            return;
        }
        if (attacker.isPet()) {
            super.onEvtAttacked(attacker, damage);
        }
    }

    @Override
    public void onEvtAggression(Creature attacker, int aggro) {
        NpcInstance actor = getActor();
        if (attacker != null && attacker.isPlayer()) {
            Skill skill = SkillTable.INSTANCE().getInfo(5423, 12);
            skill.getEffects(actor, attacker, false, false);
            return;
        }
    }

    @Override
    public boolean randomWalk() {
        return false;
    }
}