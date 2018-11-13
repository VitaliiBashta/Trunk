package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantDragonspine extends DefaultAI {
    private static final Location[] points = {
            new Location(9318, 92253, -3536),
            new Location(9117, 91645, -3656),
            new Location(9240, 90149, -3592),
            new Location(11509, 90093, -3720),
            new Location(13269, 90004, -3840),
            new Location(14812, 89578, -3832),
            new Location(14450, 90636, -3680),
            new Location(14236, 91690, -3656),
            new Location(13636, 92359, -3480),
            new Location(14236, 91690, -3656),
            new Location(14450, 90636, -3680),
            new Location(14812, 89578, -3832),
            new Location(13269, 90004, -3840),
            new Location(11509, 90093, -3720),
            new Location(9240, 90149, -3592),
            new Location(9117, 91645, -3656),
            new Location(9318, 92253, -3536)};

    private int current_point = -1;
    private long wait_timeout = 0;
    private boolean wait = false;

    public SuspiciousMerchantDragonspine(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public boolean thinkActive() {
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
            if (!wait)
                switch (current_point) {
                    case 0:
                        wait_timeout = System.currentTimeMillis() + 30000;
                        wait = true;
                        return true;
                    case 3:
                        wait_timeout = System.currentTimeMillis() + 20000;
                        wait = true;
                        return true;
                    case 4:
                        wait_timeout = System.currentTimeMillis() + 20000;
                        wait = true;
                        return true;
                    case 5:
                        wait_timeout = System.currentTimeMillis() + 20000;
                        wait = true;
                        return true;
                    case 8:
                        wait_timeout = System.currentTimeMillis() + 60000;
                        wait = true;
                        return true;
                    case 11:
                        wait_timeout = System.currentTimeMillis() + 20000;
                        wait = true;
                        return true;
                    case 12:
                        wait_timeout = System.currentTimeMillis() + 20000;
                        wait = true;
                        return true;
                    case 13:
                        wait_timeout = System.currentTimeMillis() + 20000;
                        wait = true;
                        return true;
                    case 16:
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