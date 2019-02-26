package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;
import java.util.stream.IntStream;

public final class _647_InfluxOfMachines extends Quest {
    // Settings: drop chance in %
    private static final int DROP_CHANCE = 60;

    // QUEST ITEMS
    private static final int BROKEN_GOLEM_FRAGMENT = 15521;

    // REWARDS
    private static final List<Integer> RECIPES = List.of(
            6887, 6881, 6897, 7580, 6883, 6899, 6891, 6885, 6893, 6895);

    public _647_InfluxOfMachines() {
        super(true);

        addStartNpc(32069);
        addTalkId(32069);
        addKillId(IntStream.rangeClosed(22801, 22812).toArray());

        addQuestItem(BROKEN_GOLEM_FRAGMENT);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("quest_accept".equalsIgnoreCase(event)) {
            htmltext = "collecter_gutenhagen_q0647_0103.htm";
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("647_3".equalsIgnoreCase(event))
            if (st.haveQuestItem(BROKEN_GOLEM_FRAGMENT, 500)) {
                st.takeItems(BROKEN_GOLEM_FRAGMENT);
                st.giveItems(Rnd.get(RECIPES));
                htmltext = "collecter_gutenhagen_q0647_0201.htm";
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            } else
                htmltext = "collecter_gutenhagen_q0647_0106.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        long count = st.getQuestItemsCount(BROKEN_GOLEM_FRAGMENT);
        if (cond == 0)
            if (st.player.getLevel() >= 70)
                htmltext = "collecter_gutenhagen_q0647_0101.htm";
            else {
                htmltext = "collecter_gutenhagen_q0647_0102.htm";
                st.exitCurrentQuest();
            }
        else if (cond == 1 && count < 500)
            htmltext = "collecter_gutenhagen_q0647_0106.htm";
        else if (cond == 2 && count >= 500)
            htmltext = "collecter_gutenhagen_q0647_0105.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1 && st.rollAndGive(BROKEN_GOLEM_FRAGMENT, 1, 1, 500, DROP_CHANCE * npc.getTemplate().rateHp))
            st.setCond(2);
    }
}