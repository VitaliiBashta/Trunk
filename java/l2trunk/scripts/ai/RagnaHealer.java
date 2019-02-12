package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Priest;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class RagnaHealer extends Priest {
    private long lastFactionNotifyTime;

    public RagnaHealer(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (attacker == null)
            return;

        if (System.currentTimeMillis() - lastFactionNotifyTime > 10000) {
            lastFactionNotifyTime = System.currentTimeMillis();
            actor.getAroundNpc(500, 300)
                    .filter(npc -> npc.getNpcId() >= 22691)
                    .filter(npc -> npc.getNpcId() <= 22702)
                    .forEach(npc ->
                            npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 5000));
        }
        super.onEvtAttacked(attacker, damage);
    }
}