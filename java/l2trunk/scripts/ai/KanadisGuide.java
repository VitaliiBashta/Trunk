package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;


public final class KanadisGuide extends Fighter {

    public KanadisGuide(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        getActor().getAroundNpc(5000, 300)
                .filter(npc -> npc.getNpcId() == 36562)
                .forEach(npc -> actor.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, npc, 5000));
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (attacker.getNpcId() == 36562) {
            actor.getAggroList().addDamageHate(attacker, 0, 1);
            startRunningTask(2000);
            setIntentionAttack(attacker);
        }
        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public boolean maybeMoveToHome() {
        return false;
    }
}