package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;

public final class DeluLizardmanSpecialCommander extends Fighter {
    private boolean _shouted = false;

    public DeluLizardmanSpecialCommander(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        _shouted = false;
        super.onEvtSpawn();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();

        if (Rnd.chance(40) && !_shouted) {
            _shouted = true;
            Functions.npcSay(actor, "Come on my fellows, assist me here!");

            List<NpcInstance> around = actor.getAroundNpc(1000, 300);
            if (around != null && !around.isEmpty())
                for (NpcInstance npc : around)
                    if (npc.isMonster())
                        npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 5000);
        }
        super.onEvtAttacked(attacker, damage);
    }
}