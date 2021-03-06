package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _116_BeyondtheHillsofWinter extends Quest {
    //NPC
    private final int FILAUR = 30535;
    private final int OBI = 32052;
    //Quest Item
    private final int Supplying_Goods_for_Railroad_Worker = 8098;
    //Item
    private static final int Bandage = 1833;
    private static final int Energy_Stone = 5589;
    private static final int Thief_Key = 1661;
    private static final int SSD = 1463;

    public _116_BeyondtheHillsofWinter() {
        addStartNpc(FILAUR);
        addTalkId(OBI);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("elder_filaur_q0116_0104.htm")) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("elder_filaur_q0116_0201.htm".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(Bandage) >= 20 && st.getQuestItemsCount(Energy_Stone) >= 5 && st.getQuestItemsCount(Thief_Key) >= 10) {
                st.takeItems(Bandage, 20);
                st.takeItems(Energy_Stone, 5);
                st.takeItems(Thief_Key, 10);
                st.giveItems(Supplying_Goods_for_Railroad_Worker);
                st.setCond(2);
                st.start();
            } else
                htmltext = "elder_filaur_q0116_0104.htm";
        } else if ("materials".equalsIgnoreCase(event)) {
            htmltext = "railman_obi_q0116_0302.htm";
            st.takeItems(Supplying_Goods_for_Railroad_Worker, 1);
            st.giveItems(SSD, 1740);
            st.addExpAndSp(82792, 4981);
            st.finish();
        } else if ("adena".equalsIgnoreCase(event)) {
            htmltext = "railman_obi_q0116_0302.htm";
            st.takeItems(Supplying_Goods_for_Railroad_Worker, 1);
            st.giveAdena( 17387);
            st.addExpAndSp(82792, 4981);
            st.finish();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        int cond = 0;
        if (id != CREATED)
            cond = st.getCond();
        if (npcId == FILAUR) {
            if (cond == 0) {
                if (st.player.getLevel() < 30) {
                    htmltext = "elder_filaur_q0116_0103.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "elder_filaur_q0116_0101.htm";
            } else if (cond == 1)
                htmltext = "elder_filaur_q0116_0105.htm";
            else if (cond == 2)
                htmltext = "elder_filaur_q0116_0201.htm";
        } else if (npcId == OBI)
            if (cond == 2 && st.getQuestItemsCount(Supplying_Goods_for_Railroad_Worker) > 0)
                htmltext = "railman_obi_q0116_0201.htm";
        return htmltext;
    }
}