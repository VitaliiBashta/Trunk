package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _189_ContractCompletion extends Quest {
    private static final int Kusto = 30512;
    private static final int Lorain = 30673;
    private static final int Luka = 30621;
    private static final int Shegfield = 30068;

    private static final int Metal = 10370;

    public _189_ContractCompletion() {
        addTalkId(Kusto, Luka, Lorain, Shegfield);
        addFirstTalkId(Luka);
        addQuestItem(Metal);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("blueprint_seller_luka_q0189_03.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_ACCEPT);
            st.setCond(1);
            st.giveItems(Metal);
        } else if ("researcher_lorain_q0189_02.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_MIDDLE);
            st.setCond(2);
            st.takeItems(Metal);
        } else if ("shegfield_q0189_03.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        } else if ("head_blacksmith_kusto_q0189_02.htm".equalsIgnoreCase(event)) {
            st.giveAdena(121527);
            st.addExpAndSp(309467, 20614);
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
            if (npcId == Luka) {
                if (cond == 0)
                    if (st.player.getLevel() < 42)
                        htmltext = "blueprint_seller_luka_q0189_02.htm";
                    else
                        htmltext = "blueprint_seller_luka_q0189_01.htm";
                else if (cond == 1)
                    htmltext = "blueprint_seller_luka_q0189_04.htm";
            } else if (npcId == Lorain) {
                if (cond == 1)
                    htmltext = "researcher_lorain_q0189_01.htm";
                else if (cond == 2)
                    htmltext = "researcher_lorain_q0189_03.htm";
                else if (cond == 3) {
                    htmltext = "researcher_lorain_q0189_04.htm";
                    st.setCond(4);
                    st.playSound(SOUND_MIDDLE);
                } else if (cond == 4)
                    htmltext = "researcher_lorain_q0189_05.htm";
            } else if (npcId == Shegfield) {
                if (cond == 2)
                    htmltext = "shegfield_q0189_01.htm";
                else if (cond == 3)
                    htmltext = "shegfield_q0189_04.htm";
            } else if (npcId == Kusto)
                if (cond == 4)
                    htmltext = "head_blacksmith_kusto_q0189_01.htm";
        return htmltext;
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        if (player.isQuestCompleted(_186_ContractExecution.class) && player.getQuestState(this) == null)
            newQuestState(player, STARTED);
        return "";
    }
}