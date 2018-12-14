package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;

public final class SealDevice extends Fighter {
    private boolean _firstAttack = false;

    public SealDevice(NpcInstance actor) {
        super(actor);
        actor.setBlock(true);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (!_firstAttack) {
            actor.broadcastPacket(new MagicSkillUse(actor,  5980));
            _firstAttack = true;
        }
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
    }
}