package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;

import java.util.List;


public final class KanadisFollower extends Fighter {
    public KanadisFollower(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();

        NpcInstance actor = getActor();
        List<NpcInstance> around = actor.getAroundNpc(7000, 300);
        if (around != null && !around.isEmpty())
            for (NpcInstance npc : around)
                if (npc.getNpcId() == 36562)
                    actor.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, npc, 500);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (attacker.getNpcId() == 36562) {
            actor.getAggroList().addDamageHate(attacker, 0, 100);
            startRunningTask(2000);
            setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
        }
        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public boolean maybeMoveToHome() {
        return false;
    }
}