package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _187_NikolasHeart extends Quest {
    private static final int Kusto = 30512;
    private static final int Lorain = 30673;
    private static final int Nikola = 30621;

    private static final int Certificate = 10362;
    private static final int Metal = 10368;

    public _187_NikolasHeart() {
        addTalkId(Kusto, Nikola, Lorain);
        addFirstTalkId(Lorain);
        addQuestItem(Certificate, Metal);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("researcher_lorain_q0187_03.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_ACCEPT);
            st.setCond(1);
            st.takeItems(Certificate);
            st.giveItems(Metal);
        } else if ("maestro_nikola_q0187_03.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("head_blacksmith_kusto_q0187_03.htm".equalsIgnoreCase(event)) {
            st.giveAdena( 93383);
            st.addExpAndSp(285935, 18711);
            st.finish();
            st.playSound(SOUND_FINISH);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (st.getState() == STARTED)
            if (npcId == Lorain) {
                if (cond == 0)
                    if (st.player.getLevel() < 41)
                        htmltext = "researcher_lorain_q0187_02.htm";
                    else
                        htmltext = "researcher_lorain_q0187_01.htm";
                else if (cond == 1)
                    htmltext = "researcher_lorain_q0187_04.htm";
            } else if (npcId == Nikola) {
                if (cond == 1)
                    htmltext = "maestro_nikola_q0187_01.htm";
                else if (cond == 2)
                    htmltext = "maestro_nikola_q0187_04.htm";
            } else if (npcId == Kusto)
                if (cond == 2)
                    htmltext = "head_blacksmith_kusto_q0187_01.htm";
        return htmltext;
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        if (player.isQuestCompleted(_185_NikolasCooperationConsideration.class) && player.getQuestState(getClass()) == null)
            newQuestState(player, STARTED);
        return "";
    }
}