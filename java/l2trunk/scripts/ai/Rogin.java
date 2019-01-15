package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

public class Rogin extends DefaultAI {
    private static final Location[] points = {
            new Location(115756, -183472, -1480),
            new Location(115866, -183287, -1480),
            new Location(116280, -182918, -1520),
            new Location(116587, -184306, -1568),
            new Location(116392, -184090, -1560),
            new Location(117083, -182538, -1528),
            new Location(117802, -182541, -1528),
            new Location(116720, -182479, -1528),
            new Location(115857, -183295, -1480),
            new Location(115756, -183472, -1480)};

    private int current_point = -1;
    private long wait_timeout = 0;
    private boolean wait = false;

    public Rogin(NpcInstance actor) {
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
                    case 3:
                        wait_timeout = System.currentTimeMillis() + 15000;
                        Functions.npcSay(actor, NpcString.HAVE_YOU_SEEN_TOROCCO_TODAY);
                        wait = true;
                        return true;
                    case 6:
                        wait_timeout = System.currentTimeMillis() + 15000;
                        Functions.npcSay(actor, NpcString.HAVE_YOU_SEEN_TOROCCO);
                        wait = true;
                        return true;
                    case 7:
                        wait_timeout = System.currentTimeMillis() + 15000;
                        Functions.npcSay(actor, NpcString.WHERE_IS_THAT_FOOL_HIDING);
                        wait = true;
                        return true;
                    case 8:
                        wait_timeout = System.currentTimeMillis() + 60000;
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

        if (randomAnimation())
            return true;

        return false;
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
    }
}