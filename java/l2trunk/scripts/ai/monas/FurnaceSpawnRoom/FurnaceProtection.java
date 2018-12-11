package l2trunk.scripts.ai.monas.FurnaceSpawnRoom;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.impl.MonasteryFurnaceEvent;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;

public final class FurnaceProtection extends DefaultAI {
    private boolean _firstTimeAttacked = true;

    public FurnaceProtection(NpcInstance actor) {
        super(actor);
        actor.setNameNpcString(NpcString.FURN3);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        int event_id = actor.getAISpawnParam();
        MonasteryFurnaceEvent furnace = EventHolder.getEvent(EventType.MAIN_EVENT, event_id);

        if (_firstTimeAttacked && !furnace.isInProgress()) {
            _firstTimeAttacked = false;
            attacker.setTarget(null);
            actor.setTargetable(false);
            actor.setNpcState((byte) 1);
            Functions.npcShout(actor, NpcString.FURN1);
            furnace.registerActions();
            ThreadPoolManager.INSTANCE.schedule(() ->
                    EventHolder.getEvent(EventType.MAIN_EVENT, getActor().getAISpawnParam())
                            .spawnAction(MonasteryFurnaceEvent.PROTECTOR_ROOM, true), 15000);
        }

        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public void onEvtDead(Creature killer) {
        _firstTimeAttacked = true;
        super.onEvtDead(killer);
    }
}