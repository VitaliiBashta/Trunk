package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class Rokar extends DefaultAI {
    private static final Location[] points = {
            new Location(-46516, -117700, -264),
            new Location(-45550, -115420, -256),
            new Location(-44052, -114575, -256),
            new Location(-44024, -112688, -256),
            new Location(-45748, -111665, -256),
            new Location(-46512, -109390, -232),
            new Location(-45748, -111665, -256),
            new Location(-44024, -112688, -256),
            new Location(-44052, -114575, -256),
            new Location(-45550, -115420, -256)};

    private int current_point = -1;
    private long wait_timeout = 0;
    private boolean wait = false;

    public Rokar(NpcInstance actor) {
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
                    case 5:
                        wait_timeout = System.currentTimeMillis() + 30000;
                        wait = true;
                        return true;
                }

            wait_timeout = 0;
            wait = false;
            current_point++;

            if (current_point >= points.length)
                current_point = 0;

            addTaskMove(points[current_point], true);
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