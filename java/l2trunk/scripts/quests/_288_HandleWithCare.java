package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _288_HandleWithCare extends Quest {
    private static final int Ankumi = 32741;
    private static final int MiddleGradeLizardScale = 15498;
    private static final int HighestGradeLizardScale = 15497;

    public _288_HandleWithCare() {
        super(true);
        addStartNpc(Ankumi);
        addQuestItem(MiddleGradeLizardScale, HighestGradeLizardScale);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int cond = st.getCond();
        if ("ankumi_q288_03.htm".equalsIgnoreCase(event)) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("request_reward".equalsIgnoreCase(event)) {
            if (cond == 2 && st.haveQuestItem(MiddleGradeLizardScale)) {
                st.takeItems(MiddleGradeLizardScale);
                switch (Rnd.get(1, 6)) {
                    case 1:
                        st.giveItems(959);
                        break;
                    case 2:
                        st.giveItems(960);
                        break;
                    case 3:
                        st.giveItems(960, 2);
                        break;
                    case 4:
                        st.giveItems(960, 3);
                        break;
                    case 5:
                        st.giveItems(9557);
                        break;
                    case 6:
                        st.giveItems(9557, 2);
                        break;
                }
                htmltext = "ankumi_q288_06.htm";
                st.exitCurrentQuest();
            } else if (cond == 3 && st.haveQuestItem(HighestGradeLizardScale) ) {
                st.takeItems(HighestGradeLizardScale);
                switch (Rnd.get(1, 4)) {
                    case 1:
                        st.giveItems(959);
                        st.giveItems(9557);
                        break;
                    case 2:
                        st.giveItems(960);
                        st.giveItems(9557);
                        break;
                    case 3:
                        st.giveItems(960, 2);
                        st.giveItems(9557);
                        break;
                    case 4:
                        st.giveItems(960, 3);
                        st.giveItems(9557);
                        break;
                }
                htmltext = "ankumi_q288_06.htm";
                st.exitCurrentQuest();
            } else {
                htmltext = "ankumi_q288_07.htm";
                st.exitCurrentQuest();
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Ankumi) {
            if (cond == 0) {
                if (st.player.getLevel() >= 82)
                    htmltext = "ankumi_q288_01.htm";
                else {
                    htmltext = "ankumi_q288_00.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "ankumi_q288_04.htm";
            else if (cond == 2 || cond == 3)
                htmltext = "ankumi_q288_05.htm";
        }
        return htmltext;
    }

}