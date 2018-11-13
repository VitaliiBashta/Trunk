package l2trunk.scripts.ai.monas.FurnaceSpawnRoom;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.impl.MonasteryFurnaceEvent;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class DivinityMonster extends DefaultAI {
    public DivinityMonster(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();

        int event_id = actor.getAISpawnParam();
        MonasteryFurnaceEvent furnace = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, event_id);

        if (Rnd.chance(5) && !furnace.isInProgress())
            furnace.spawnAction(MonasteryFurnaceEvent.FURNACE_ROOM, true);

        super.onEvtDead(killer);
    }
}