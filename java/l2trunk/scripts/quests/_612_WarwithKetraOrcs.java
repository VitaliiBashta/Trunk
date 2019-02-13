package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import static l2trunk.scripts.quests._605_AllianceWithKetraOrcs.KETRA_NPC_LIST;

public final class _612_WarwithKetraOrcs extends Quest {
    // NPC
    private static final int DURAI = 31377;

    // Quest items
    private static final int MOLAR_OF_KETRA_ORC = 7234;
    private static final int MOLAR_OF_KETRA_ORC_DROP_CHANCE = 80;
    private static final int NEPENTHES_SEED = 7187;


    public _612_WarwithKetraOrcs() {
        super(true);
        addStartNpc(DURAI);
        addKillId(KETRA_NPC_LIST);
        addQuestItem(MOLAR_OF_KETRA_ORC);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("quest_accept")) {
            htmltext = "elder_ashas_barka_durai_q0612_0104.htm";
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if ("elder_ashas_barka_durai_q0612_0202.htm".equalsIgnoreCase(event)) {
            long ec = st.getQuestItemsCount(MOLAR_OF_KETRA_ORC) / 5;
            if (ec > 0) {
                st.takeItems(MOLAR_OF_KETRA_ORC, ec * 5);
                st.giveItems(NEPENTHES_SEED, ec);
            } else
                htmltext = "elder_ashas_barka_durai_q0612_0203.htm";
        } else if ("elder_ashas_barka_durai_q0612_0204.htm".equalsIgnoreCase(event)) {
            st.takeItems(MOLAR_OF_KETRA_ORC);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(true);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            if (st.player.getLevel() >= 74)
                htmltext = "elder_ashas_barka_durai_q0612_0101.htm";
            else {
                htmltext = "elder_ashas_barka_durai_q0612_0103.htm";
                st.exitCurrentQuest(true);
            }
        } else if (cond == 1 && st.getQuestItemsCount(MOLAR_OF_KETRA_ORC) == 0)
            htmltext = "elder_ashas_barka_durai_q0612_0106.htm";
        else if (cond == 1 && st.haveQuestItem(MOLAR_OF_KETRA_ORC))
            htmltext = "elder_ashas_barka_durai_q0612_0105.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (KETRA_NPC_LIST.contains(npc.getNpcId()) && st.getCond() == 1)
            st.rollAndGive(MOLAR_OF_KETRA_ORC, 1, MOLAR_OF_KETRA_ORC_DROP_CHANCE);
    }
}