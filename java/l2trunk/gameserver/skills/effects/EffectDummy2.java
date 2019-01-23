package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.instances.SummonInstance;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.utils.PositionUtils;

public final class EffectDummy2 extends Effect {
    public static final double FEAR_RANGE = 900.0D;

    public EffectDummy2(Env env, EffectTemplate template) {
        super(env, template);
    }

    public boolean checkCondition() {
        if (this.effected.isFearImmune()) {
            getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }

        Player player = this.effected.getPlayer();
        if (player != null) {
            SiegeEvent siegeEvent = (SiegeEvent) player.getEvent(SiegeEvent.class);
            if ((this.effected.isSummon()) && (siegeEvent != null) && (siegeEvent.containsSiegeSummon((SummonInstance) this.effected))) {
                getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                return false;
            }
        }

        if (this.effected.isInZonePeace()) {
            getEffector().sendPacket(Msg.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE);
            return false;
        }

        return super.checkCondition();
    }

    public void onStart() {
        Player target = (Player) getEffected();
        if (target.getTransformation() == 303) {
            return;
        }
        super.onStart();

        if (this.effected.startFear()) {
            this.effected.abortAttack(true, true);
            this.effected.abortCast(true, true);
            this.effected.stopMove();
        }

        onActionTime();
    }

    public void onExit() {
        super.onExit();
        this.effected.stopFear();
        this.effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }

    protected boolean onActionTime() {
        double angle = Math.toRadians(PositionUtils.calculateAngleFrom(this.effector, this.effected));
        int oldX = this.effected.getX();
        int oldY = this.effected.getY();
        int x = oldX + (int) (900.0D * Math.cos(angle));
        int y = oldY + (int) (900.0D * Math.sin(angle));
        this.effected.setRunning();
        this.effected.moveToLocation(GeoEngine.moveCheck(oldX, oldY, this.effected.getZ(), x, y, this.effected.getGeoIndex()), 0, false);
        return true;
    }
}