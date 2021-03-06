package l2trunk.scripts.ai.dragonvalley;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public class PatrollersNoWatch extends Fighter {
    private final List<Integer> _teleporters = List.of(22857, 22833, 22834);
    List<Location> _points;
    private int _lastPoint = 0;
    private boolean _firstThought = true;

    public PatrollersNoWatch(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = Integer.MAX_VALUE - 10;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public boolean checkAggression(Playable target) {
        NpcInstance actor = getActor();
        if ( !target.isDead() && !target.isInvisible() && !target.isSilentMoving()) {
            actor.getAggroList().addDamageHate(target, 0, 1);
            setIntentionAttack(target);
        }
        return true;
    }

    @Override
    public boolean thinkActive() {
        if (super.thinkActive())
            return true;

        if (!getActor().isMoving)
            startMoveTask();

        return true;
    }

    @Override
    public void onEvtArrived() {
        startMoveTask();
        super.onEvtArrived();
    }

    private void startMoveTask() {
        NpcInstance npc = getActor();
        if (_firstThought) {
            _lastPoint = getIndex(Location.findNearest(npc, _points));
            _firstThought = false;
        } else
            _lastPoint++;
        if (_lastPoint >= _points.size()) {
            _lastPoint = 0;
            if (_teleporters.contains(npc.getNpcId()))
                npc.teleToLocation(_points.get(_lastPoint));
        }
        npc.setRunning();
        if (Rnd.chance(30))
            npc.altOnMagicUseTimer(npc, 6757);
        addTaskMove(Location.findPointToStay(_points.get(_lastPoint), 250, npc.getGeoIndex()), true);
        doTask();
    }

    private int getIndex(Location loc) {
        int index = _points.indexOf(loc);
        return index == -1 ? 0 : index;
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

    @Override
    public void returnHome(boolean clearAggro, boolean teleport) {
        super.returnHome(clearAggro, teleport);
        clearTasks();
        _firstThought = true;
        startMoveTask();
    }
}
