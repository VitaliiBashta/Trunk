package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

public final class Leandro extends DefaultAI {
    private static final Location[] points = {
            new Location(-82428, 245204, -3720),
            new Location(-82422, 245448, -3704),
            new Location(-82080, 245401, -3720),
            new Location(-82108, 244974, -3720),
            new Location(-83595, 244051, -3728),
            new Location(-83898, 242776, -3728),
            new Location(-85966, 241371, -3728),
            new Location(-86079, 240868, -3720),
            new Location(-86076, 240392, -3712),
            new Location(-86519, 240706, -3712),
            new Location(-86343, 241130, -3720),
            new Location(-86519, 240706, -3712),
            new Location(-86076, 240392, -3712),
            new Location(-86079, 240868, -3720),
            new Location(-85966, 241371, -3728),
            new Location(-83898, 242776, -3728),
            new Location(-83595, 244051, -3728),
            new Location(-82108, 244974, -3720)};

    private int current_point = -1;
    private long wait_timeout = 0;
    private boolean wait = false;

    public Leandro(NpcInstance actor) {
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

        if (System.currentTimeMillis() > wait_timeout && (current_point > -1 || Rnd.chance(5))) {
            if (!wait)
                switch (current_point) {
                    case 5:
                        wait_timeout = System.currentTimeMillis() + 30000;
                        Functions.npcSay(actor, NpcString.WHERE_HAS_HE_GONE);
                        wait = true;
                        return true;
                    case 10:
                        wait_timeout = System.currentTimeMillis() + 60000;
                        Functions.npcSay(actor, NpcString.HAVE_YOU_SEEN_WINDAWOOD);
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