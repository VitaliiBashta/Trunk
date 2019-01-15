package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class Jaradine extends DefaultAI {
    private static final List<Location> points = List.of(
            new Location(44964, 50568, -3056),
            new Location(44435, 50025, -3056),
            new Location(44399, 49078, -3056),
            new Location(45058, 48437, -3056),
            new Location(46132, 48724, -3056),
            new Location(46452, 49743, -3056),
            new Location(45730, 50590, -3056));

    private int current_point = -1;
    private long wait_timeout = 0;
    private boolean wait = false;

    public Jaradine(NpcInstance actor) {
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
                        Functions.npcSay(actor, NpcString.THE_MOTHER_TREE_IS_SLOWLY_DYING);
                        wait = true;
                        return true;
                    case 4:
                        wait_timeout = System.currentTimeMillis() + 15000;
                        Functions.npcSay(actor, NpcString.HOW_CAN_WE_SAVE_THE_MOTHER_TREE);
                        wait = true;
                        return true;
                    case 6:
                        wait_timeout = System.currentTimeMillis() + 60000;
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