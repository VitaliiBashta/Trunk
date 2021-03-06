package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class Remy extends DefaultAI {
    private static final List<Location> points = List.of(
           Location.of(-81926, 243894, -3712),
           Location.of(-82134, 243600, -3728),
           Location.of(-83165, 243987, -3728),
           Location.of(-84501, 243245, -3728),
           Location.of(-85100, 243285, -3728),
           Location.of(-86152, 242898, -3728),
           Location.of(-86288, 242962, -3720),
           Location.of(-86348, 243223, -3720),
           Location.of(-86522, 242762, -3720),
           Location.of(-86500, 242615, -3728),
           Location.of(-86123, 241606, -3728),
           Location.of(-85167, 240589, -3728),
           Location.of(-84323, 241245, -3728),
           Location.of(-83215, 241170, -3728),
           Location.of(-82364, 242944, -3728),
           Location.of(-81674, 243391, -3712),
           Location.of(-81926, 243894, -3712));

    private int current_point = -1;
    private long wait_timeout = 0;
    private boolean wait = false;

    public Remy(NpcInstance actor) {
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
                        wait_timeout = System.currentTimeMillis() + 15000;
                        Functions.npcSay(actor, NpcString.A_DELIVERY_FOR_MR);
                        wait = true;
                        return true;
                    case 3:
                        wait_timeout = System.currentTimeMillis() + 15000;
                        Functions.npcSay(actor, NpcString.I_NEED_A_BREAK);
                        wait = true;
                        return true;
                    case 7:
                        wait_timeout = System.currentTimeMillis() + 15000;
                        Functions.npcSay(actor, NpcString.HELLO_MR);
                        wait = true;
                        return true;
                    case 12:
                        wait_timeout = System.currentTimeMillis() + 15000;
                        Functions.npcSay(actor, NpcString.LULU);
                        wait = true;
                        return true;
                    case 15:
                        wait_timeout = System.currentTimeMillis() + 60000;
                        wait = true;
                        return true;
                }

            wait_timeout = 0;
            wait = false;
            current_point++;

            if (current_point >= points.size())
                current_point = 0;

            // Remy всегда бегает
            actor.setRunning();

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