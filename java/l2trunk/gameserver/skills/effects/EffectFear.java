package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.instances.SummonInstance;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.utils.PositionUtils;

public final class EffectFear extends Effect {
    private static final double FEAR_RANGE = 900;

    public EffectFear(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean checkCondition() {
        if (effected.isFearImmune()) {
            getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }

        // Fear нельзя наложить на осадных саммонов
        Player player = effected.getPlayer();
        if (player != null) {
            SiegeEvent<?, ?> siegeEvent = player.getEvent(SiegeEvent.class);
            if (effected.isSummon() && siegeEvent != null && siegeEvent.containsSiegeSummon((SummonInstance) effected)) {
                getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                return false;
            }
        }

        if (effected.isInZonePeace()) {
            getEffector().sendPacket(SystemMsg.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE);
            return false;
        }

        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!effected.startFear()) {
            effected.abortAttack(true, true);
            effected.abortCast(true, true);
            effected.stopMove();
        }

        onActionTime();
    }

    @Override
    public void onExit() {
        super.onExit();
        effected.stopFear();
        effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }

    @Override
    protected boolean onActionTime() {
        final double angle = Math.toRadians(PositionUtils.calculateAngleFrom(_effector, effected));
        final int oldX = effected.getX();
        final int oldY = effected.getY();
        final int x = oldX + (int) (FEAR_RANGE * Math.cos(angle));
        final int y = oldY + (int) (FEAR_RANGE * Math.sin(angle));
        effected.setRunning();
        effected.moveToLocation(GeoEngine.moveCheck(oldX, oldY, effected.getZ(), x, y, effected.getGeoIndex()), 0, false);
        return true;
    }
}