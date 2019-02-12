package l2trunk.scripts.ai.seedofinfinity;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.instances.HeartInfinityDefence;

import java.util.List;

public final class EkimusFood extends DefaultAI {
    private static final List<Location> ROUTE_1 = List.of(
            new Location(-179544, 207400, -15496),
            new Location(-178856, 207464, -15496),
            new Location(-178168, 207864, -15496),
            new Location(-177512, 208728, -15496),
            new Location(-177336, 209528, -15496),
            new Location(-177448, 210328, -15496),
            new Location(-177864, 211048, -15496),
            new Location(-178584, 211608, -15496),
            new Location(-179304, 211848, -15496),
            new Location(-179512, 211864, -15496),
            new Location(-179528, 211448, -15472));

    private static final List<Location> ROUTE_2 = List.of(
            new Location(-179576, 207352, -15496),
            new Location(-180440, 207544, -15496),
            new Location(-181256, 208152, -15496),
            new Location(-181752, 209112, -15496),
            new Location(-181720, 210264, -15496),
            new Location(-181096, 211224, -15496),
            new Location(-180264, 211720, -15496),
            new Location(-179528, 211848, -15496),
            new Location(-179528, 211400, -15472));

    private final List<Location> points;

    private int _lastPoint = 0;

    public EkimusFood(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = Integer.MAX_VALUE - 10;
        points = Rnd.chance(50) ? ROUTE_1 : ROUTE_2;
        actor.startDebuffImmunity();
    }

    @Override
    public boolean checkAggression(Playable target, boolean avoidAttack) {
        return false;
    }

    @Override
    public void onEvtArrived() {
        startMoveTask();
        super.onEvtArrived();
    }

    @Override
    public boolean thinkActive() {
        if (!defThink)
            startMoveTask();
        return true;
    }

    private void startMoveTask() {
        NpcInstance npc = getActor();
        _lastPoint++;
        if (_lastPoint >= points.size()) {
            if (!npc.getReflection().isDefault()) {
                ((HeartInfinityDefence) npc.getReflection()).notifyWagonArrived();
                npc.deleteMe();
                return;
            }
        }
        addTaskMove(Location.findPointToStay(points.get(_lastPoint), 250, npc.getGeoIndex()), true);
        doTask();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    public boolean randomWalk() {
        return false;
    }

    @Override
    public boolean maybeMoveToHome() {
        return false;
    }

    @Override
    public void teleportHome() {
    }
}
