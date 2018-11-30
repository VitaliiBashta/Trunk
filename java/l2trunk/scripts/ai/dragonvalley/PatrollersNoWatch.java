package l2trunk.scripts.ai.dragonvalley;

import l2trunk.commons.lang.ArrayUtils;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Location;

public class PatrollersNoWatch extends Fighter {
    Location[] _points;
    private final int[] _teleporters = {22857, 22833, 22834};

    private int _lastPoint = 0;
    private boolean _firstThought = true;

    PatrollersNoWatch(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = Integer.MAX_VALUE - 10;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public boolean checkAggression(Creature target) {
        NpcInstance actor = getActor();
        if (target.isPlayable() && !target.isDead() && !target.isInvisible() && !((Playable) target).isSilentMoving()) {
            actor.getAggroList().addDamageHate(target, 0, 1);
            setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
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
        if (_lastPoint >= _points.length) {
            _lastPoint = 0;
            if (ArrayUtils.contains(_teleporters, npc.getNpcId()))
                npc.teleToLocation(_points[_lastPoint]);
        }
        npc.setRunning();
        try {
            if (Rnd.chance(30))
                npc.altOnMagicUseTimer(npc, SkillTable.INSTANCE().getInfo(6757, 1));
            addTaskMove(Location.findPointToStay(_points[_lastPoint], 250, npc.getGeoIndex()), true);
        } catch (Exception e) {
        }
        doTask();
    }

    private int getIndex(Location loc) {
        for (int i = 0; i < _points.length; i++)
            if (_points[i] == loc)
                return i;
        return 0;
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
