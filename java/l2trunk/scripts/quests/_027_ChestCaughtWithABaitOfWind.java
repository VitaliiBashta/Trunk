package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _027_ChestCaughtWithABaitOfWind extends Quest {
    // NPC List
    private static final int Lanosco = 31570;
    private static final int Shaling = 31434;
    //Quest items
    private static final int StrangeGolemBlueprint = 7625;
    //items
    private static final int BigBlueTreasureChest = 6500;
    private static final int BlackPearlRing = 880;

    public _027_ChestCaughtWithABaitOfWind() {
        addStartNpc(Lanosco);
        addTalkId(Shaling);
        addQuestItem(StrangeGolemBlueprint);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "fisher_lanosco_q0027_0104.htm":
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
                break;
            case "fisher_lanosco_q0027_0201.htm":
                if (st.haveQuestItem(BigBlueTreasureChest)) {
                    st.takeItems(BigBlueTreasureChest);
                    st.giveItems(StrangeGolemBlueprint);
                    st.setCond(2);
                    st.playSound(SOUND_MIDDLE);
                } else
                    htmltext = "fisher_lanosco_q0027_0202.htm";
                break;
            case "blueprint_seller_shaling_q0027_0301.htm":
                if (st.haveQuestItem(StrangeGolemBlueprint) ) {
                    st.takeItems(StrangeGolemBlueprint);
                    st.giveItems(BlackPearlRing);
                    st.playSound(SOUND_FINISH);
                    st.finish();
                } else {
                    htmltext = "blueprint_seller_shaling_q0027_0302.htm";
                    st.exitCurrentQuest();
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        int id = st.getState();
        if (npcId == Lanosco) {
            if (id == CREATED) {
                if (st.player.getLevel() < 27) {
                    htmltext = "fisher_lanosco_q0027_0101.htm";
                    st.exitCurrentQuest();
                } else {
                    if (st.player.isQuestCompleted(_050_LanoscosSpecialBait.class))
                        htmltext = "fisher_lanosco_q0027_0101.htm";
                    else {
                        htmltext = "fisher_lanosco_q0027_0102.htm";
                        st.exitCurrentQuest();
                    }

                }
            } else if (cond == 1) {
                htmltext = "fisher_lanosco_q0027_0105.htm";
                if (st.getQuestItemsCount(BigBlueTreasureChest) == 0)
                    htmltext = "fisher_lanosco_q0027_0106.htm";
            } else if (cond == 2)
                htmltext = "fisher_lanosco_q0027_0203.htm";
        } else if (npcId == Shaling)
            if (cond == 2)
                htmltext = "blueprint_seller_shaling_q0027_0201.htm";
            else
                htmltext = "blueprint_seller_shaling_q0027_0302.htm";
        return htmltext;
    }
}