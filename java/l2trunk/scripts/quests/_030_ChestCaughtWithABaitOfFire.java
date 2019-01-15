package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _030_ChestCaughtWithABaitOfFire extends Quest {
    private static final int NecklaceOfProtection = 916;
    private final int Linnaeus = 31577;
    private final int Rukal = 30629;
    private final int RedTreasureChest = 6511;
    private final int RukalsMusicalScore = 7628;

    public _030_ChestCaughtWithABaitOfFire() {
        super(false);
        addStartNpc(Linnaeus);
        addTalkId(Rukal);
        addQuestItem(RukalsMusicalScore);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "fisher_linneaus_q0030_0104.htm":
                st.setState(STARTED);
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
                break;
            case "fisher_linneaus_q0030_0201.htm":
                if (st.getQuestItemsCount(RedTreasureChest) > 0) {
                    st.takeItems(RedTreasureChest, 1);
                    st.giveItems(RukalsMusicalScore, 1);
                    st.setCond(2);
                    st.playSound(SOUND_MIDDLE);
                } else
                    htmltext = "fisher_linneaus_q0030_0202.htm";
                break;
            case "bard_rukal_q0030_0301.htm":
                if (st.getQuestItemsCount(RukalsMusicalScore) == 1) {
                    st.takeItems(RukalsMusicalScore, -1);
                    st.giveItems(NecklaceOfProtection, 1);
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest(false);
                } else {
                    htmltext = "bard_rukal_q0030_0302.htm";
                    st.exitCurrentQuest(true);
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
        if (npcId == Linnaeus) {
            if (id == CREATED) {
                if (st.getPlayer().getLevel() < 60) {
                    htmltext = "fisher_linneaus_q0030_0102.htm";
                    st.exitCurrentQuest(true);
                } else {
                    QuestState LinnaeusSpecialBait = st.getPlayer().getQuestState(_053_LinnaeusSpecialBait.class);
                    if (LinnaeusSpecialBait != null) {
                        if (LinnaeusSpecialBait.isCompleted())
                            htmltext = "fisher_linneaus_q0030_0101.htm";
                        else {
                            htmltext = "fisher_linneaus_q0030_0102.htm";
                            st.exitCurrentQuest(true);
                        }
                    } else {
                        htmltext = "fisher_linneaus_q0030_0103.htm";
                        st.exitCurrentQuest(true);
                    }
                }
            } else if (cond == 1) {
                htmltext = "fisher_linneaus_q0030_0105.htm";
                if (st.getQuestItemsCount(RedTreasureChest) == 0)
                    htmltext = "fisher_linneaus_q0030_0106.htm";
            } else if (cond == 2)
                htmltext = "fisher_linneaus_q0030_0203.htm";
        } else if (npcId == Rukal)
            if (cond == 2)
                htmltext = "bard_rukal_q0030_0201.htm";
            else
                htmltext = "bard_rukal_q0030_0302.htm";
        return htmltext;
    }
}