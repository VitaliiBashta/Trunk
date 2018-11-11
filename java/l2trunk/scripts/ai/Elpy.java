package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public class Elpy extends Fighter {
    public Elpy(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (attacker != null && Rnd.chance(50)) {
            Location pos = Location.findPointToStay(actor, 150, 200);
            if (GeoEngine.canMoveToCoord(actor, pos)) {
                actor.setRunning();
                addTaskMove(pos, false);
            }
        }
    }

    @Override
    protected boolean checkAggression(Creature target, boolean avoidAttack) {
        return false;
    }

    @Override
    protected void onEvtAggression(Creature target, int aggro) {

    }
}