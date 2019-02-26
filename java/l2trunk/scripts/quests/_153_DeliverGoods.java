package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _153_DeliverGoods extends Quest {
    private final int DELIVERY_LIST = 1012;
    private final int HEAVY_WOOD_BOX = 1013;
    private final int CLOTH_BUNDLE = 1014;
    private final int CLAY_POT = 1015;
    private final int JACKSONS_RECEIPT = 1016;
    private final int SILVIAS_RECEIPT = 1017;
    private final int RANTS_RECEIPT = 1018;
    private static final int RING_OF_KNOWLEDGE = 875;

    public _153_DeliverGoods() {
        super(false);

        addStartNpc(30041);

        addTalkId(30002,30003,30054);

        addQuestItem(HEAVY_WOOD_BOX,
                CLOTH_BUNDLE,
                CLAY_POT,
                DELIVERY_LIST,
                JACKSONS_RECEIPT,
                SILVIAS_RECEIPT,
                RANTS_RECEIPT);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("30041-04.htm")) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            if (st.getQuestItemsCount(DELIVERY_LIST) == 0)
                st.giveItems(DELIVERY_LIST, 1);
            if (st.getQuestItemsCount(HEAVY_WOOD_BOX) == 0)
                st.giveItems(HEAVY_WOOD_BOX, 1);
            if (st.getQuestItemsCount(CLOTH_BUNDLE) == 0)
                st.giveItems(CLOTH_BUNDLE, 1);
            if (st.getQuestItemsCount(CLAY_POT) == 0)
                st.giveItems(CLAY_POT, 1);
            htmltext = "30041-04.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == 30041) {
            if (cond == 0) {
                if (st.player.getLevel() >= 2) {
                    htmltext = "30041-03.htm";
                    return htmltext;
                }
                htmltext = "30041-02.htm";
                st.exitCurrentQuest();
            } else if (cond == 1 && st.getQuestItemsCount(JACKSONS_RECEIPT) + st.getQuestItemsCount(SILVIAS_RECEIPT) + st.getQuestItemsCount(RANTS_RECEIPT) == 0)
                htmltext = "30041-05.htm";
            else if (cond == 1 && st.getQuestItemsCount(JACKSONS_RECEIPT) + st.getQuestItemsCount(SILVIAS_RECEIPT) + st.getQuestItemsCount(RANTS_RECEIPT) == 3) {
                st.giveItems(RING_OF_KNOWLEDGE, 2);
                st.takeItems(DELIVERY_LIST);
                st.takeItems(JACKSONS_RECEIPT);
                st.takeItems(SILVIAS_RECEIPT);
                st.takeItems(RANTS_RECEIPT);
                st.addExpAndSp(600, 0);
                st.playSound(SOUND_FINISH);
                htmltext = "30041-06.htm";
                st.finish();
            }
        } else if (npcId == 30002) {
            if (cond == 1 && st.getQuestItemsCount(HEAVY_WOOD_BOX) == 1) {
                st.takeItems(HEAVY_WOOD_BOX, -1);
                if (st.getQuestItemsCount(JACKSONS_RECEIPT) == 0)
                    st.giveItems(JACKSONS_RECEIPT);
                htmltext = "30002-01.htm";
            } else if (cond == 1 && st.getQuestItemsCount(JACKSONS_RECEIPT) > 0)
                htmltext = "30002-02.htm";
        } else if (npcId == 30003) {
            if (cond == 1 && st.getQuestItemsCount(CLOTH_BUNDLE) == 1) {
                st.takeItems(CLOTH_BUNDLE, -1);
                if (st.getQuestItemsCount(SILVIAS_RECEIPT) == 0) {
                    st.giveItems(SILVIAS_RECEIPT);
                    if (st.player.getClassId().isMage)
                        st.giveItems(2509, 3);
                    else
                        st.giveItems(1835, 6);
                }
                htmltext = "30003-01.htm";
            } else if (cond == 1 && st.getQuestItemsCount(SILVIAS_RECEIPT) > 0)
                htmltext = "30003-02.htm";
        } else if (npcId == 30054)
            if (cond == 1 && st.getQuestItemsCount(CLAY_POT) == 1) {
                st.takeItems(CLAY_POT, -1);
                if (st.getQuestItemsCount(RANTS_RECEIPT) == 0)
                    st.giveItems(RANTS_RECEIPT, 1);
                htmltext = "30054-01.htm";
            } else if (cond == 1 && st.getQuestItemsCount(RANTS_RECEIPT) > 0)
                htmltext = "30054-02.htm";
        return htmltext;
    }
}