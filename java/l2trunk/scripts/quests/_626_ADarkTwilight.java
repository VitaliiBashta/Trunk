package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _626_ADarkTwilight extends Quest {
    //NPC
    private static final int Hierarch = 31517;
    //QuestItem
    private static final int BloodOfSaint = 7169;

    public _626_ADarkTwilight() {
        super(true);
        addStartNpc(Hierarch);
        for (int npcId = 21520; npcId <= 21542; npcId++)
            addKillId(npcId);
        addQuestItem(BloodOfSaint);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("dark_presbyter_q0626_0104.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("dark_presbyter_q0626_0201.htm")) {
            if (st.getQuestItemsCount(BloodOfSaint) < 300)
                htmltext = "dark_presbyter_q0626_0203.htm";
        } else if (event.equalsIgnoreCase("rew_exp")) {
            st.takeItems(BloodOfSaint, -1);
            st.addExpAndSp(162773, 12500);
            htmltext = "dark_presbyter_q0626_0202.htm";
            st.exitCurrentQuest(true);
        } else if (event.equalsIgnoreCase("rew_adena")) {
            st.takeItems(BloodOfSaint, -1);
            st.giveItems(ADENA_ID, 100000, true);
            htmltext = "dark_presbyter_q0626_0202.htm";
            st.exitCurrentQuest(true);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        int npcId = npc.getNpcId();
        if (npcId == Hierarch)
            if (cond == 0) {
                if (st.getPlayer().getLevel() < 60) {
                    htmltext = "dark_presbyter_q0626_0103.htm";
                    st.exitCurrentQuest(true);
                } else
                    htmltext = "dark_presbyter_q0626_0101.htm";
            } else if (cond == 1)
                htmltext = "dark_presbyter_q0626_0106.htm";
            else if (cond == 2)
                htmltext = "dark_presbyter_q0626_0105.htm";
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1 && Rnd.chance(70)) {
            st.giveItems(BloodOfSaint, 1);
            if (st.getQuestItemsCount(BloodOfSaint) == 300)
                st.setCond(2);
        }
        return null;
    }
}