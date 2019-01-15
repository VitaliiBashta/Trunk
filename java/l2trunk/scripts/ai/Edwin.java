package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class Edwin extends DefaultAI {
    private static final List<Location> points = List.of(
            new Location(89991, -144601, -1467), // start
            new Location(90538, -143470, -1467),
            new Location(90491, -142848, -1467),
            new Location(89563, -141455, -1467),
            new Location(89138, -140621, -1467),
            new Location(87459, -140192, -1467),
            new Location(85625, -140699, -1467),
            new Location(84538, -142382, -1467),
            new Location(84527, -143913, -1467), // finish
            new Location(84538, -142382, -1467),
            new Location(85625, -140699, -1467),
            new Location(87459, -140192, -1467),
            new Location(89138, -140621, -1467),
            new Location(89563, -141455, -1467),
            new Location(90491, -142848, -1467),
            new Location(90538, -143470, -1467),
            new Location(89991, -144601, -1467)); // start

    private int current_point = -1;
    private long wait_timeout = 0;
    private boolean wait = false;

    public Edwin(NpcInstance actor) {
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

        if (defThink) {
            doTask();
            return true;
        }

        if (System.currentTimeMillis() > wait_timeout && (current_point > -1 || Rnd.chance(5))) {
            if (!wait)
                switch (current_point) {
                    case 0:
                        wait_timeout = System.currentTimeMillis() + 10000;
                        wait = true;
                        return true;
                    case 8:
                        wait_timeout = System.currentTimeMillis() + 10000;
                        wait = true;
                        return true;
                }

            wait_timeout = 0;
            wait = false;
            current_point++;

            if (current_point >= points.size())
                current_point = 0;

            addTaskMove(points.get(current_point), true);
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