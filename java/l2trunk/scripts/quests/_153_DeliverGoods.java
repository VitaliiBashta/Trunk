package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _153_DeliverGoods extends Quest {
    private static final int RING_OF_KNOWLEDGE = 875;
    private final int DELIVERY_LIST = 1012;
    private final int HEAVY_WOOD_BOX = 1013;
    private final int CLOTH_BUNDLE = 1014;
    private final int CLAY_POT = 1015;
    private final int JACKSONS_RECEIPT = 1016;
    private final int SILVIAS_RECEIPT = 1017;
    private final int RANTS_RECEIPT = 1018;

    public _153_DeliverGoods() {
        addStartNpc(30041);

        addTalkId(30002, 30003, 30054);

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
            st.giveItemIfNotHave(DELIVERY_LIST);
            st.giveItemIfNotHave(HEAVY_WOOD_BOX);
            st.giveItemIfNotHave(CLOTH_BUNDLE);
            st.giveItemIfNotHave(CLAY_POT);
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
            } else if (cond == 1 && !st.haveAnyQuestItems(JACKSONS_RECEIPT,SILVIAS_RECEIPT,RANTS_RECEIPT))
                htmltext = "30041-05.htm";
            else if (cond == 1 && st.haveAllQuestItems(JACKSONS_RECEIPT,SILVIAS_RECEIPT,RANTS_RECEIPT)) {
                st.giveItems(RING_OF_KNOWLEDGE, 2);
                st.takeAllItems(DELIVERY_LIST,JACKSONS_RECEIPT,SILVIAS_RECEIPT,RANTS_RECEIPT);
                st.addExpAndSp(600, 0);
                st.playSound(SOUND_FINISH);
                htmltext = "30041-06.htm";
                st.finish();
            }
        } else if (npcId == 30002) {
            if (cond == 1 && st.haveQuestItem(HEAVY_WOOD_BOX) ) {
                st.takeItems(HEAVY_WOOD_BOX, -1);
                if (st.getQuestItemsCount(JACKSONS_RECEIPT) == 0)
                    st.giveItems(JACKSONS_RECEIPT);
                htmltext = "30002-01.htm";
            } else if (cond == 1 && st.haveQuestItem(JACKSONS_RECEIPT) )
                htmltext = "30002-02.htm";
        } else if (npcId == 30003) {
            if (cond == 1 && st.haveQuestItem(CLOTH_BUNDLE) ) {
                st.takeItems(CLOTH_BUNDLE);
                if (st.getQuestItemsCount(SILVIAS_RECEIPT) == 0) {
                    st.giveItems(SILVIAS_RECEIPT);
                    if (st.player.getClassId().isMage())
                        st.giveItems(2509, 3);
                    else
                        st.giveItems(1835, 6);
                }
                htmltext = "30003-01.htm";
            } else if (cond == 1 && st.haveQuestItem(SILVIAS_RECEIPT))
                htmltext = "30003-02.htm";
        } else if (npcId == 30054)
            if (cond == 1 && st.haveQuestItem(CLAY_POT)) {
                st.takeItems(CLAY_POT);
                if (st.getQuestItemsCount(RANTS_RECEIPT) == 0)
                    st.giveItems(RANTS_RECEIPT);
                htmltext = "30054-01.htm";
            } else if (cond == 1 && st.haveQuestItem(RANTS_RECEIPT) )
                htmltext = "30054-02.htm";
        return htmltext;
    }
}