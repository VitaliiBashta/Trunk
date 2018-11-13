package l2trunk.scripts.ai.isle_of_prayer;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class Shade extends Fighter {
    private long _wait_timeout = 0;
    private boolean _wait = false;
    private static final int DESPAWN_TIME = 5 * 60 * 1000; // 5 min
    private static final int BLUE_CRYSTAL = 9595;

    public Shade(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead())
            return true;

        if (_def_think) {
            doTask();
            _wait = false;
            return true;
        }

        if (!_wait) {
            _wait = true;
            _wait_timeout = System.currentTimeMillis() + DESPAWN_TIME;
        }

        if (_wait_timeout != 0 && _wait && _wait_timeout < System.currentTimeMillis()) {
            actor.deleteMe();
            return true;
        }

        return super.thinkActive();
    }

    @Override
    public boolean randomWalk() {
        return false;
    }

    @Override
    public void onEvtDead(Creature killer) {
        if (killer != null) {
            final Player player = killer.getPlayer();
            if (player != null) {
                final NpcInstance actor = getActor();
                if (Rnd.chance(10))
                    actor.dropItem(player, BLUE_CRYSTAL, 1);
            }
        }
        super.onEvtDead(killer);
    }
}