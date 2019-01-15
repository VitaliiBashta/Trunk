package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

public final class FieldMachine extends DefaultAI {
    private long lastaction;

    public FieldMachine(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (attacker == null || attacker.getPlayer() == null)
            return;

        // Ругаемся не чаще, чем раз в 15 секунд
        if (System.currentTimeMillis() - lastaction > 15000) {
            lastaction = System.currentTimeMillis();
            Functions.npcSayCustomMessage(actor, "scripts.ai.FieldMachine." + actor.getNpcId());
            actor.getAroundNpc(1500, 300)
                    .filter(GameObject::isMonster)
                    .filter(npc -> npc.getNpcId() >= 22656)
                    .filter(npc -> npc.getNpcId() <= 22659)
                    .forEach(npc -> npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 5000));
        }
    }
}