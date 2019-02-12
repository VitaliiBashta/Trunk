package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _188_SealRemoval extends Quest {
    private static final int Dorothy = 30970;
    private static final int Lorain = 30673;
    private static final int Nikola = 30621;

    private static final int BrokenMetal = 10369;

    public _188_SealRemoval() {
        super(false);

        addTalkId(Dorothy, Nikola, Lorain);
        addFirstTalkId(Lorain);
        addQuestItem(BrokenMetal);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("researcher_lorain_q0188_03.htm")) {
            st.playSound(SOUND_ACCEPT);
            st.setCond(1);
            st.giveItems(BrokenMetal, 1);
        } else if (event.equalsIgnoreCase("maestro_nikola_q0188_03.htm")) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("dorothy_the_locksmith_q0188_03.htm")) {
            st.giveItems(ADENA_ID, 98583);
            st.addExpAndSp(285935, 18711);
            st.exitCurrentQuest(false);
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
                        htmltext = "researcher_lorain_q0188_02.htm";
                    else
                        htmltext = "researcher_lorain_q0188_01.htm";
                else if (cond == 1)
                    htmltext = "researcher_lorain_q0188_04.htm";
            } else if (npcId == Nikola) {
                if (cond == 1)
                    htmltext = "maestro_nikola_q0188_01.htm";
                else if (cond == 2)
                    htmltext = "maestro_nikola_q0188_05.htm";
            } else if (npcId == Dorothy)
                if (cond == 2)
                    htmltext = "dorothy_the_locksmith_q0188_01.htm";
        return htmltext;
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        if ((player.isQuestCompleted(_186_ContractExecution.class) || player.isQuestCompleted(_187_NikolasHeart.class)) && player.getQuestState(this) == null)
            newQuestState(player, STARTED);
        return "";
    }
}