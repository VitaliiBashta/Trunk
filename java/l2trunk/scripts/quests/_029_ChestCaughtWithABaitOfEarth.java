package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _029_ChestCaughtWithABaitOfEarth extends Quest {
    private static final int Willie = 31574;
    private static final int Anabel = 30909;

    private static final int SmallPurpleTreasureChest = 6507;
    private static final int SmallGlassBox = 7627;
    private static final int PlatedLeatherGloves = 2455;

    public _029_ChestCaughtWithABaitOfEarth() {
        addStartNpc(Willie);
        addTalkId(Anabel);
        addQuestItem(SmallGlassBox);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "fisher_willeri_q0029_0104.htm":
                st.start();
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
                break;
            case "fisher_willeri_q0029_0201.htm":
                if (st.haveQuestItem(SmallPurpleTreasureChest)) {
                    st.setCond(2);
                    st.playSound(SOUND_MIDDLE);
                    st.takeItems(SmallPurpleTreasureChest, 1);
                    st.giveItems(SmallGlassBox);
                } else
                    htmltext = "fisher_willeri_q0029_0202.htm";
                break;
            case "29_GiveGlassBox":
                if (st.haveQuestItem(SmallGlassBox)) {
                    htmltext = "magister_anabel_q0029_0301.htm";
                    st.takeItems(SmallGlassBox);
                    st.giveItems(PlatedLeatherGloves);
                    st.playSound(SOUND_FINISH);
                    st.finish();
                } else {
                    htmltext = "magister_anabel_q0029_0302.htm";
                    st.exitCurrentQuest();
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        int cond = st.getCond();
        if (npcId == Willie) {
            if (id == CREATED) {
                if (st.player.getLevel() < 48) {
                    htmltext = "fisher_willeri_q0029_0102.htm";
                    st.exitCurrentQuest();
                } else {
                    QuestState WilliesSpecialBait = st.player.getQuestState(_052_WilliesSpecialBait.class);
                    if (WilliesSpecialBait != null) {
                        if (WilliesSpecialBait.isCompleted())
                            htmltext = "fisher_willeri_q0029_0101.htm";
                        else {
                            htmltext = "fisher_willeri_q0029_0102.htm";
                            st.exitCurrentQuest();
                        }
                    } else {
                        htmltext = "fisher_willeri_q0029_0103.htm";
                        st.exitCurrentQuest();
                    }
                }
            } else if (cond == 1) {
                htmltext = "fisher_willeri_q0029_0105.htm";
                if (st.getQuestItemsCount(SmallPurpleTreasureChest) == 0)
                    htmltext = "fisher_willeri_q0029_0106.htm";
            } else if (cond == 2)
                htmltext = "fisher_willeri_q0029_0203.htm";
        } else if (npcId == Anabel)
            if (cond == 2)
                htmltext = "magister_anabel_q0029_0201.htm";
            else
                htmltext = "magister_anabel_q0029_0302.htm";
        return htmltext;
    }
}
