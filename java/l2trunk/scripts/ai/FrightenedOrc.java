package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;

public final class FrightenedOrc extends Fighter {
    private boolean _sayOnAttack;

    public FrightenedOrc(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        _sayOnAttack = true;
        super.onEvtSpawn();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (attacker != null && Rnd.chance(10) && _sayOnAttack) {
            Functions.npcSay(actor, NpcString.DONT_KILL_ME_PLEASE);
            _sayOnAttack = false;
        }

        super.onEvtAttacked(attacker, damage);
    }

}