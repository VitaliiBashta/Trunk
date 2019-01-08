package l2trunk.scripts.quests;

import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.scripts.ScriptFile;

public final class _730_ProtectTheSuppliesSafe extends Quest {
    public _730_ProtectTheSuppliesSafe() {
        super(PARTY_NONE);
        DominionSiegeRunnerEvent runnerEvent = EventHolder.getEvent(EventType.MAIN_EVENT, 1);
        runnerEvent.addBreakQuest(this);
    }
}
