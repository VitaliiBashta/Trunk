package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.PlaySound;

public final class ZakenDaytime83 extends Fighter {
    private final static int zakenTele = 4222;
    private static final long _teleportSelfReuse = 120000L; // 120 secs
    private final NpcInstance actor = getActor();
    private long _teleportSelfTimer = 0L;

    public ZakenDaytime83(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = Integer.MAX_VALUE / 2;
    }

    @Override
    public void thinkAttack() {
        if (_teleportSelfTimer + _teleportSelfReuse < System.currentTimeMillis()) {
            _teleportSelfTimer = System.currentTimeMillis();
            if (Rnd.chance(20)) {
                actor.doCast(zakenTele, actor, false);
            }
        }
        super.thinkAttack();
    }

    @Override
    public void onEvtDead(Creature killer) {
        Reflection r = actor.getReflection();
        r.setReenterTime(System.currentTimeMillis());
        actor.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS02_D", 1, actor.getObjectId(), actor.getLoc()));
        super.onEvtDead(killer);
    }

    @Override
    public void teleportHome() {
    }
}