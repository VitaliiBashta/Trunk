package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Guard;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class AirshipGuard1 extends Guard {
    private static final Location[] points = {
            new Location(-149633, 254016, -180),
            new Location(-149874, 254224, -184),
            new Location(-150088, 254429, -184),
            new Location(-150229, 254603, -184),
            new Location(-150368, 254822, -184),
            new Location(-150570, 255125, -184),
            new Location(-149649, 254189, -180),
            new Location(-149819, 254291, -184),
            new Location(-150038, 254487, -184),
            new Location(-150182, 254654, -184),
            new Location(-150301, 254855, -184),
            new Location(-150438, 255133, -181)};

    private int current_point = -1;
    private long wait_timeout = 0;
    private boolean wait = false;

    public AirshipGuard1(NpcInstance actor) {
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
            if (!wait && (current_point == 0 || current_point == 8)) {
                wait_timeout = System.currentTimeMillis() + Rnd.get(0, 30000);
                wait = true;
                return true;
            }

            wait_timeout = 0;
            wait = false;
            current_point++;

            if (current_point >= points.length)
                current_point = 0;

            addTaskMove(Location.findPointToStay(actor, points[current_point], 0, 100), true);
            doTask();
            return true;
        }
        return false;
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
    }
}