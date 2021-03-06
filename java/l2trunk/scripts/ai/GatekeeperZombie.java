package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

/**
 * AI охраны входа в Pagan Temple.<br>
 * <li>кидаются на всех игроков, у которых в кармане нету предмета 8064 или 8067
 * <li>не умеют ходить
 *
 * @author SYS
 */
public final class GatekeeperZombie extends Mystic {
    public GatekeeperZombie(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    public boolean checkAggression(Playable target, boolean avoidAttack) {
        NpcInstance actor = getActor();
        if (actor.isDead())
            return false;
        if (getIntention() != CtrlIntention.AI_INTENTION_ACTIVE || !isGlobalAggro())
            return false;
        if (target.isAlikeDead() || !(target instanceof Player))
            return false;
        if (!target.isInRangeZ(actor.getSpawnedLoc(), actor.getAggroRange()))
            return false;
        if (((Player) target).haveItem( 8067)  || ((Player) target).haveItem( 8064) )
            return false;
        if (!GeoEngine.canSeeTarget(actor, target, false))
            return false;

        if (!avoidAttack && getIntention() != CtrlIntention.AI_INTENTION_ATTACK) {
            actor.getAggroList().addDamageHate(target, 0, 1);
            setIntentionAttack(target);
        }

        return true;
    }

    @Override
    public boolean randomWalk() {
        return false;
    }
}