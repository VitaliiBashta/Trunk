package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _10270_BirthOfTheSeed extends Quest {
    // NPC's
    private static final int PLENOS = 32563;
    private static final int ARTIUS = 32559;
    private static final int LELIKIA = 32567;
    private static final int GINBY = 32566;
    // ITEMS
    private static final int Yehan_Klodekus_Badge = 13868;
    private static final int Yehan_Klanikus_Badge = 13869;
    private static final int Lich_Crystal = 13870;
    // MOB's
    private static final int Yehan_Klodekus = 25665;
    private static final int Yehan_Klanikus = 25666;
    private static final int Cohemenes = 25634;

    public _10270_BirthOfTheSeed() {
        super(true);

        addStartNpc(PLENOS);
        addTalkId(PLENOS, ARTIUS, LELIKIA, GINBY);
        addKillId(Yehan_Klodekus, Yehan_Klanikus, Cohemenes);
        addQuestItem(Yehan_Klodekus_Badge, Yehan_Klanikus_Badge, Lich_Crystal);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();
        String htmltext = event;

        if (event.equals("take") && cond == 0) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            htmltext = "plenos_q10270_2.htm";
        } else if (event.equals("took_mission") && cond == 1) {
            st.setCond(2);
            htmltext = "artius_q10270_3.htm";
            st.playSound(SOUND_MIDDLE);
        } else if (event.equals("hand_over") && cond == 2) {
            st.takeAllItems(Yehan_Klodekus_Badge, Yehan_Klanikus_Badge, Lich_Crystal);
            htmltext = "artius_q10270_6.htm";
        } else if (event.equals("artius_q10270_7.htm") && cond == 2) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        } else if (event.equals("lelika") && cond == 3) {
            st.setCond(4);
            htmltext = "artius_q10270_9.htm";
            st.playSound(SOUND_MIDDLE);
        } else if (event.equals("lelikia_q10270_2.htm") && cond == 4) {
            st.setCond(5);
            st.playSound(SOUND_MIDDLE);
        } else if (event.equals("reward") && cond == 5) {
            htmltext = "artius_q10270_11.htm";
            st.giveItems(ADENA_ID, 41677);
            st.addExpAndSp(251602, 25244);
            st.playSound(SOUND_FINISH);
            st.finish();
        }

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        boolean haveAllQuestItems = st.haveAllQuestItems(Yehan_Klodekus_Badge, Yehan_Klanikus_Badge, Lich_Crystal);
        if (npcId == PLENOS) {
            if (cond == 0)
                if (st.player.getLevel() >= 75)
                    htmltext = "plenos_q10270_1.htm";
                else {
                    htmltext = "plenos_q10270_1a.htm";
                    st.exitCurrentQuest();
                }
        } else if (npcId == ARTIUS) {
            if (cond == 1)
                htmltext = "artius_q10270_1.htm";
            else if (cond == 2 && !haveAllQuestItems)
                htmltext = "artius_q10270_4.htm";
            else if (cond == 2)
                htmltext = "artius_q10270_5.htm";
            else if (cond == 3)
                htmltext = "artius_q10270_8.htm";
            else if (cond == 5)
                htmltext = "artius_q10270_10.htm";
        } else if (npcId == GINBY) {
            if (cond == 4)
                htmltext = "ginby_q10270_1.htm";
        } else if (npcId == LELIKIA)
            if (cond == 4)
                htmltext = "lelikia_q10270_1.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (st.getCond() == 2) {
            if (npcId == Yehan_Klodekus) st.giveItemIfNotHave(Yehan_Klodekus_Badge);
            if (npcId == Yehan_Klanikus) st.giveItemIfNotHave(Yehan_Klanikus_Badge);
            if (npcId == Cohemenes) st.giveItemIfNotHave(Lich_Crystal);
        }
    }
}