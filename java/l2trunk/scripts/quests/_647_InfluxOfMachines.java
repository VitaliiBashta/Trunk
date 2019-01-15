package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

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
        for (int i = 22801; i < 22812; i++)
            addKillId(i);

        addQuestItem(BROKEN_GOLEM_FRAGMENT);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("quest_accept")) {
            htmltext = "collecter_gutenhagen_q0647_0103.htm";
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("647_3"))
            if (st.getQuestItemsCount(BROKEN_GOLEM_FRAGMENT) >= 500) {
                st.takeItems(BROKEN_GOLEM_FRAGMENT, -1);
                st.giveItems(Rnd.get(RECIPES));
                htmltext = "collecter_gutenhagen_q0647_0201.htm";
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(true);
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
            if (st.getPlayer().getLevel() >= 70)
                htmltext = "collecter_gutenhagen_q0647_0101.htm";
            else {
                htmltext = "collecter_gutenhagen_q0647_0102.htm";
                st.exitCurrentQuest(true);
            }
        else if (cond == 1 && count < 500)
            htmltext = "collecter_gutenhagen_q0647_0106.htm";
        else if (cond == 2 && count >= 500)
            htmltext = "collecter_gutenhagen_q0647_0105.htm";
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1 && st.rollAndGive(BROKEN_GOLEM_FRAGMENT, 1, 1, 500, DROP_CHANCE * npc.getTemplate().rateHp))
            st.setCond(2);
        return null;
    }
}