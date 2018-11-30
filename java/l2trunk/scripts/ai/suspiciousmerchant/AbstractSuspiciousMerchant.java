package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public abstract class AbstractSuspiciousMerchant extends DefaultAI {

    private int current_point = -1;
    private long wait_timeout = 0;
    private boolean wait = false;

    public AbstractSuspiciousMerchant(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public abstract boolean thinkActive();


    boolean thinkActive0(final Location[] points) {
        NpcInstance actor = getActor();
        if (actor.isDead())
            return true;

        if (_def_think) {
            doTask();
            return true;
        }

        if (actor.isMoving)
            return true;

        if (System.currentTimeMillis() > wait_timeout && (current_point > -1 || Rnd.chance(5))) {
            if (!wait) {
                wait_timeout = System.currentTimeMillis() + 30000;
                wait = true;
                return true;
            }

            wait_timeout = 0;
            wait = false;
            current_point++;

            if (current_point >= points.length)
                current_point = 0;

            addTaskMove(points[current_point], false);
            doTask();
            return true;
        }

        return randomAnimation();

    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
    }
}

