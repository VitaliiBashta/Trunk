package l2trunk.scripts.ai.hellbound;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class RankuScapegoat extends DefaultAI {
    private static final int Eidolon_ID = 25543;

    public RankuScapegoat(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        NpcInstance mob = actor.getReflection().addSpawnWithoutRespawn(Eidolon_ID, actor);
        NpcInstance boss = getBoss();
        if (mob != null && boss != null) {
            Creature cha = boss.getAggroList().getTopDamager();
            if (cha != null)
                mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, cha, 100000);
        }
        super.onEvtDead(killer);
    }

    private NpcInstance getBoss() {
        Reflection r = getActor().getReflection();
        if (!r.isDefault())
            return r.getNpcs()
                    .filter(n -> n.getNpcId() == 25542)
                    .filter(n -> !n.isDead())
                    .findFirst().orElse(null);
        return null;
    }
}