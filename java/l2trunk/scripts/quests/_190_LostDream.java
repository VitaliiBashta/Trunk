package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _190_LostDream extends Quest {
    private static final int Kusto = 30512;
    private static final int Lorain = 30673;
    private static final int Nikola = 30621;
    private static final int Juris = 30113;

    public _190_LostDream() {
        addTalkId(Kusto, Nikola, Lorain, Juris);
        addFirstTalkId(Kusto);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("head_blacksmith_kusto_q0190_03.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_ACCEPT);
            st.setCond(1);
        } else if ("head_blacksmith_kusto_q0190_06.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_MIDDLE);
            st.setCond(3);
        } else if ("juria_q0190_03.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (st.getState() == STARTED)
            if (npcId == Kusto) {
                if (cond == 0)
                    if (st.player.getLevel() < 42)
                        htmltext = "head_blacksmith_kusto_q0190_02.htm";
                    else
                        htmltext = "head_blacksmith_kusto_q0190_01.htm";
                else if (cond == 1)
                    htmltext = "head_blacksmith_kusto_q0190_04.htm";
                else if (cond == 2)
                    htmltext = "head_blacksmith_kusto_q0190_05.htm";
                else if (cond == 3)
                    htmltext = "head_blacksmith_kusto_q0190_07.htm";
                else if (cond == 5) {
                    htmltext = "head_blacksmith_kusto_q0190_08.htm";
                    st.giveAdena( 109427);
                    st.addExpAndSp(309467, 20614);
                    st.finish();
                    st.playSound(SOUND_FINISH);
                }
            } else if (npcId == Juris) {
                if (cond == 1)
                    htmltext = "juria_q0190_01.htm";
                else if (cond == 2)
                    htmltext = "juria_q0190_04.htm";
            } else if (npcId == Lorain) {
                if (cond == 3) {
                    htmltext = "researcher_lorain_q0190_01.htm";
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(4);
                } else if (cond == 4)
                    htmltext = "researcher_lorain_q0190_02.htm";
            } else if (npcId == Nikola)
                if (cond == 4) {
                    htmltext = "maestro_nikola_q0190_01.htm";
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(5);
                } else if (cond == 5)
                    htmltext = "maestro_nikola_q0190_02.htm";
        return htmltext;
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        if (player.isQuestCompleted(_187_NikolasHeart.class) && player.getQuestState(this) == null)
            newQuestState(player, STARTED);
        return "";
    }
}