package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;

/**
 * AI Emperor's Seal Device.
 *
 * @author pchayka
 */
public class SealDevice extends Fighter {
    private boolean _firstAttack = false;

    public SealDevice(NpcInstance actor) {
        super(actor);
        actor.block();
    }

    @Override
    protected void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (!_firstAttack) {
            actor.broadcastPacket(new MagicSkillUse(actor, actor, 5980, 1, 0, 0));
            _firstAttack = true;
        }
    }

    @Override
    protected void onEvtAggression(Creature target, int aggro) {
    }
}