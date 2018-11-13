package l2trunk.scripts.ai.hellbound;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class FoundryWorker extends Fighter {
    public FoundryWorker(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (attacker != null) {
            Location pos = Location.findPointToStay(actor, 150, 250);
            if (GeoEngine.canMoveToCoord(attacker.getLoc(), pos, actor.getGeoIndex())) {
                actor.setRunning();
                addTaskMove(pos, false);
            }
        }
    }

    @Override
    public boolean checkAggression(Creature target, boolean avoidAttack) {
        return false;
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
    }
}