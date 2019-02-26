package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _431_WeddingMarch extends Quest {
    private static final int MELODY_MAESTRO_KANTABILON = 31042;
    private static final int SILVER_CRYSTAL = 7540;
    private static final int WEDDING_ECHO_CRYSTAL = 7062;

    public _431_WeddingMarch() {
        super(false);

        addStartNpc(MELODY_MAESTRO_KANTABILON);

        addKillId(20786,20787);

        addQuestItem(SILVER_CRYSTAL);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("quest_accept".equalsIgnoreCase(event)) {
            htmltext = "muzyk_q0431_0104.htm";
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("431_3".equals(event))
            if (st.haveQuestItem(SILVER_CRYSTAL, 50)) {
                htmltext = "muzyk_q0431_0201.htm";
                st.takeItems(SILVER_CRYSTAL);
                st.giveItems(WEDDING_ECHO_CRYSTAL, 25);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            } else
                htmltext = "muzyk_q0431_0202.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int condition = st.getCond();
        int npcId = npc.getNpcId();
        int id = st.getState();
        if (npcId == MELODY_MAESTRO_KANTABILON)
            if (id != STARTED) {
                if (st.player.getLevel() < 38) {
                    htmltext = "muzyk_q0431_0103.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "muzyk_q0431_0101.htm";
            } else if (condition == 1)
                htmltext = "muzyk_q0431_0106.htm";
            else if (condition == 2 && st.haveQuestItem(SILVER_CRYSTAL,50))
                htmltext = "muzyk_q0431_0105.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getState() != STARTED)
            return;
        int npcId = npc.getNpcId();

        if (npcId == 20786 || npcId == 20787)
            if (st.getCond() == 1) {
                st.giveItemIfNotHave(SILVER_CRYSTAL, 50);

                if (st.haveQuestItem(SILVER_CRYSTAL,50)) {
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(2);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
    }
}