package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Guard;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class AirshipGuard2 extends Guard {
    private static final List<Location> points = List.of(
            new Location(-148162, 255173, -180),
            new Location(-148242, 254842, -184),
            new Location(-148395, 254647, -184),
            new Location(-148607, 254347, -184),
            new Location(-148781, 254206, -184),
            new Location(-149090, 254012, -180),
            new Location(-148309, 255135, -181),
            new Location(-148357, 254894, -183),
            new Location(-148461, 254688, -183),
            new Location(-148643, 254495, -183),
            new Location(-148828, 254275, -183),
            new Location(-149093, 254183, -180));

    private int current_point = -1;
    private long wait_timeout = 0;
    private boolean wait = false;

    public AirshipGuard2(NpcInstance actor) {
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

            if (current_point >= points.size())
                current_point = 0;

            addTaskMove(Location.findPointToStay(actor, points.get(current_point), 0, 100), true);
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