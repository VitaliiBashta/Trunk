package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.stream.IntStream;

public final class _626_ADarkTwilight extends Quest {
    //NPC
    private static final int Hierarch = 31517;
    //QuestItem
    private static final int BloodOfSaint = 7169;

    public _626_ADarkTwilight() {
        super(true);
        addStartNpc(Hierarch);
        addKillId(IntStream.rangeClosed(21520, 21542).toArray());
        addQuestItem(BloodOfSaint);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("dark_presbyter_q0626_0104.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("dark_presbyter_q0626_0201.htm".equalsIgnoreCase(event)) {
            if (!st.haveQuestItem(BloodOfSaint, 300))
                htmltext = "dark_presbyter_q0626_0203.htm";
        } else if ("rew_exp".equalsIgnoreCase(event)) {
            st.takeItems(BloodOfSaint);
            st.addExpAndSp(162773, 12500);
            htmltext = "dark_presbyter_q0626_0202.htm";
            st.exitCurrentQuest();
        } else if ("rew_adena".equalsIgnoreCase(event)) {
            st.takeItems(BloodOfSaint);
            st.giveAdena(100000);
            htmltext = "dark_presbyter_q0626_0202.htm";
            st.exitCurrentQuest();
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
                if (st.player.getLevel() < 60) {
                    htmltext = "dark_presbyter_q0626_0103.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "dark_presbyter_q0626_0101.htm";
            } else if (cond == 1)
                htmltext = "dark_presbyter_q0626_0106.htm";
            else if (cond == 2)
                htmltext = "dark_presbyter_q0626_0105.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1 && Rnd.chance(70)) {
            st.giveItems(BloodOfSaint);
            if (st.haveQuestItem(BloodOfSaint, 300))
                st.setCond(2);
        }
    }
}