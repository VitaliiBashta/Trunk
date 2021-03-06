package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _046_OnceMoreInTheArmsOfTheMotherTree extends Quest {
    private static final int GALLADUCCIS_ORDER_DOCUMENT_ID_1 = 7563;
    private static final int GALLADUCCIS_ORDER_DOCUMENT_ID_2 = 7564;
    private static final int GALLADUCCIS_ORDER_DOCUMENT_ID_3 = 7565;
    private static final int MAGIC_SWORD_HILT_ID = 7568;
    private static final int GEMSTONE_POWDER_ID = 7567;
    private static final int PURIFIED_MAGIC_NECKLACE_ID = 7566;
    private static final int MARK_OF_TRAVELER_ID = 7570;
    private static final int SCROLL_OF_ESCAPE_ELVEN_VILLAGE = 7118;

    public _046_OnceMoreInTheArmsOfTheMotherTree() {
        addStartNpc(30097);

        addTalkId(30094,30090,30116);

        addQuestItem(GALLADUCCIS_ORDER_DOCUMENT_ID_1,
                GALLADUCCIS_ORDER_DOCUMENT_ID_2,
                GALLADUCCIS_ORDER_DOCUMENT_ID_3,
                MAGIC_SWORD_HILT_ID,
                GEMSTONE_POWDER_ID,
                PURIFIED_MAGIC_NECKLACE_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "1":
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
                st.giveItems(GALLADUCCIS_ORDER_DOCUMENT_ID_1);
                htmltext = "galladuchi_q0046_0104.htm";
                break;
            case "2":
                st.setCond(2);
                st.takeItems(GALLADUCCIS_ORDER_DOCUMENT_ID_1);
                st.giveItems(MAGIC_SWORD_HILT_ID);
                htmltext = "gentler_q0046_0201.htm";
                break;
            case "3":
                st.setCond(3);
                st.takeItems(MAGIC_SWORD_HILT_ID);
                st.giveItems(GALLADUCCIS_ORDER_DOCUMENT_ID_2);
                htmltext = "galladuchi_q0046_0301.htm";
                break;
            case "4":
                st.setCond(4);
                st.takeItems(GALLADUCCIS_ORDER_DOCUMENT_ID_2);
                st.giveItems(GEMSTONE_POWDER_ID);
                htmltext = "sandra_q0046_0401.htm";
                break;
            case "5":
                st.setCond(5);
                st.takeItems(GEMSTONE_POWDER_ID);
                st.giveItems(GALLADUCCIS_ORDER_DOCUMENT_ID_3);
                htmltext = "galladuchi_q0046_0501.htm";
                break;
            case "6":
                st.setCond(6);
                st.takeItems(GALLADUCCIS_ORDER_DOCUMENT_ID_3);
                st.giveItems(PURIFIED_MAGIC_NECKLACE_ID);
                htmltext = "dustin_q0046_0601.htm";
                break;
            case "7":
                st.giveItems(SCROLL_OF_ESCAPE_ELVEN_VILLAGE);
                st.takeItems(PURIFIED_MAGIC_NECKLACE_ID);
                htmltext = "galladuchi_q0046_0701.htm";
                st.setCond(0);
                st.playSound(SOUND_FINISH);
                st.finish();
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        int id = st.getState();
        if (id == CREATED) {
            if (st.player.getRace() != Race.elf || !st.haveQuestItem(MARK_OF_TRAVELER_ID)) {
                htmltext = "galladuchi_q0046_0102.htm";
                st.exitCurrentQuest();
            } else if (st.player.getLevel() < 3) {
                htmltext = "galladuchi_q0046_0103.htm";
                st.exitCurrentQuest();
            } else
                htmltext = "galladuchi_q0046_0101.htm";
        } else if (npcId == 30097 && cond == 1)
            htmltext = "galladuchi_q0046_0105.htm";
        else if (npcId == 30097 && cond == 2)
            htmltext = "galladuchi_q0046_0201.htm";
        else if (npcId == 30097 && cond == 3)
            htmltext = "galladuchi_q0046_0303.htm";
        else if (npcId == 30097 && cond == 4)
            htmltext = "galladuchi_q0046_0401.htm";
        else if (npcId == 30097 && cond == 5)
            htmltext = "galladuchi_q0046_0503.htm";
        else if (npcId == 30097 && cond == 6)
            htmltext = "galladuchi_q0046_0601.htm";
        else if (npcId == 30094 && cond == 1)
            htmltext = "gentler_q0046_0101.htm";
        else if (npcId == 30094 && cond == 2)
            htmltext = "gentler_q0046_0203.htm";
        else if (npcId == 30090 && cond == 3)
            htmltext = "sandra_q0046_0301.htm";
        else if (npcId == 30090 && cond == 4)
            htmltext = "sandra_q0046_0403.htm";
        else if (npcId == 30116 && cond == 5)
            htmltext = "dustin_q0046_0501.htm";
        else if (npcId == 30116 && cond == 6)
            htmltext = "dustin_q0046_0603.htm";
        return htmltext;
    }
}