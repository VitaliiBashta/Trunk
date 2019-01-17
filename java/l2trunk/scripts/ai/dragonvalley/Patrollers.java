package l2trunk.scripts.ai.dragonvalley;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public class Patrollers extends Fighter {
    List<Location> points;
    private final List<Integer> _teleporters = List.of(22857, 22833, 22834, 22835);

    private int _lastPoint = 0;
    private boolean _firstThought = true;

    public Patrollers(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = Integer.MAX_VALUE - 10;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public boolean checkAggression(Creature target, boolean avoidAttack) {
        NpcInstance actor = getActor();
        if (actor.isDead())
            return false;
        if (target.isAlikeDead() || !target.isPlayable() || target.isInvisible())
            return false;
        if (!target.isInRangeZ(actor.getLoc(), actor.getAggroRange()))
            return false;
        if (!GeoEngine.canSeeTarget(actor, target, false))
            return false;

        if (!avoidAttack && getIntention() != CtrlIntention.AI_INTENTION_ATTACK) {
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
            _lastPoint = getIndex(Location.findNearest(npc, points));
            _firstThought = false;
        } else
            _lastPoint++;

        if (_lastPoint >= points.size()) {
            _lastPoint = 0;
            if (_teleporters.contains(npc.getNpcId()))
                npc.teleToLocation(points.get(_lastPoint));
        }

        npc.setRunning();
        if (Rnd.chance(30))
            npc.altOnMagicUseTimer(npc,6757);
        addTaskMove(Location.findPointToStay(points.get(_lastPoint), 250, npc.getGeoIndex()), true);
        if (npc instanceof MonsterInstance) {
            MonsterInstance _monster = (MonsterInstance) npc;
            if (_monster.getMinionList() != null && _monster.getMinionList().hasMinions())
                for (NpcInstance _npc : _monster.getMinionList().getAliveMinions()) {
                    _npc.setRunning();
                    ((Fighter) _npc.getAI()).addTaskMove(Location.findPointToStay(points.get(_lastPoint), 250, _npc.getGeoIndex()), true);
                }
        }
        doTask();
    }

    private int getIndex(Location loc) {
        return points.indexOf(loc) == -1 ? 0 : points.indexOf(loc);
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
