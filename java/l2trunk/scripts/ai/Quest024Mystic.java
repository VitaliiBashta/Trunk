package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.scripts.quests._024_InhabitantsOfTheForestOfTheDead;

import java.util.Objects;

public final class Quest024Mystic extends Mystic {
    public Quest024Mystic(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        Quest q = QuestManager.getQuest(_024_InhabitantsOfTheForestOfTheDead.class);
        if (q != null)
            World.getAroundPlayers(getActor(), 300, 200)
                    .map(player -> player.getQuestState(_024_InhabitantsOfTheForestOfTheDead.class))
                    .filter(Objects::nonNull)
                    .filter(questState -> questState.getCond() == 3)
                    .forEach(questState -> q.notifyEvent("seePlayer", questState, getActor()));

        return super.thinkActive();
    }
}