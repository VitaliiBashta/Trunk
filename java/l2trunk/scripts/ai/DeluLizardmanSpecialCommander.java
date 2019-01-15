package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

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
            actor.getAroundNpc(1000, 300)
                    .filter(GameObject::isMonster)
                    .forEach(npc -> npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 5000));
        }
        super.onEvtAttacked(attacker, damage);
    }
}