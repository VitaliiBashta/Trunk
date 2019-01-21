package l2trunk.scripts.ai.residences.fortress.siege;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2trunk.gameserver.model.entity.residence.Fortress;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.npc.model.residences.fortress.siege.MercenaryCaptionInstance;

import java.util.List;

public final class MercenaryCaption extends Fighter {
    private List<Location> _points = List.of();
    private int _tick = -1;

    public MercenaryCaption(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = 100;
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        NpcInstance actor = getActor();

        Fortress f = actor.getFortress();
        FortressSiegeEvent event = f.getSiegeEvent();

        _points = event.getObjects(FortressSiegeEvent.MERCENARY_POINTS);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isActionsDisabled())
            return true;

        if (defThink) {
            if (doTask())
                clearTasks();
            return true;
        }

        return randomWalk();

    }

    @Override
    public void onEvtArrived() {
        if (_tick != -1)
            startMove(false);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        _tick = -1;
        super.onEvtAttacked(attacker, damage);
    }

    public void startMove(boolean init) {
        if (init)
            _tick = 0;

        if (_tick == -1)
            return;

        if (_tick < _points.size()) {
            addTaskMove(_points.get(_tick++), true);
            doTask();
        }
    }

    @Override
    public MercenaryCaptionInstance getActor() {
        return (MercenaryCaptionInstance) super.getActor();
    }
}
