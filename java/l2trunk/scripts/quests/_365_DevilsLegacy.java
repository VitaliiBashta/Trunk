package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _365_DevilsLegacy extends Quest {
    //NPC
    private static final int RANDOLF = 30095;

    //MOBS
    private static final List<Integer> MOBS = List.of(
            20836, 29027, 20845, 21629, 21630, 29026);

    //VARIABLES
    private static final int CHANCE_OF_DROP = 25;
    private static final int REWARD_PER_ONE = 5070;

    //ITEMS
    private static final int TREASURE_CHEST = 5873;

    public _365_DevilsLegacy() {
        addStartNpc(RANDOLF);
        addKillId(MOBS);
        addQuestItem(TREASURE_CHEST);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("30095-1.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("30095-5.htm".equalsIgnoreCase(event)) {
            long count = st.getQuestItemsCount(TREASURE_CHEST);
            if (count > 0) {
                long reward = count * REWARD_PER_ONE;
                st.takeItems(TREASURE_CHEST);
                st.giveAdena(reward);
            } else
                htmltext = "You don't have required items";
        } else if ("30095-6.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            if (st.player.getLevel() >= 39)
                htmltext = "30095-0.htm";
            else {
                htmltext = "30095-0a.htm";
                st.exitCurrentQuest();
            }
        } else if (cond == 1)
            if (!st.haveQuestItem(TREASURE_CHEST))
                htmltext = "30095-2.htm";
            else
                htmltext = "30095-4.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (Rnd.chance(CHANCE_OF_DROP)) {
            st.giveItems(TREASURE_CHEST);
            st.playSound(SOUND_ITEMGET);
        }
    }
}