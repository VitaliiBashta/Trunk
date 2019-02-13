package l2trunk.scripts.ai.freya;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class JiniaGuild extends Fighter {
    public JiniaGuild(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead())
            return false;
        actor.getAroundNpc(4000, 300)
                .filter(npc -> npc.getNpcId() == 29179 || npc.getNpcId() == 29180)
                .forEach(npc ->
                        actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, npc, 3000));
        return true;
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        if (attacker == null || attacker instanceof Playable)
            return;

        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public boolean checkAggression(Playable target, boolean avoidAttack) {
        return false;
    }
}