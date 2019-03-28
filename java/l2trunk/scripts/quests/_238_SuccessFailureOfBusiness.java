package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _238_SuccessFailureOfBusiness extends Quest {
    private static final int Helvetica = 32641;

    private static final int BrazierOfPurity = 18806;
    private static final int EvilSpirit = 22658;
    private static final int GuardianSpirit = 22659;

    private static final int VicinityOfTheFieldOfSilenceResearchCenter = 14865;
    private static final int BrokenPieveOfMagicForce = 14867;
    private static final int GuardianSpiritFragment = 14868;

    public _238_SuccessFailureOfBusiness() {
        addStartNpc(Helvetica);
        addKillId(BrazierOfPurity, EvilSpirit, GuardianSpirit);
        addQuestItem(BrokenPieveOfMagicForce, GuardianSpiritFragment);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("32641-03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
        }
        if ("32641-06.htm".equalsIgnoreCase(event)) {
            st.takeItems(BrokenPieveOfMagicForce);
            st.setCond(3);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();

        if (npcId == Helvetica)
            if (id == CREATED) {
                if (st.player.getLevel() < 82 || !st.player.isQuestCompleted(_237_WindsOfChange.class)) {
                    st.exitCurrentQuest();
                    htmltext = "32641-00.htm";
                } else if (st.haveQuestItem(VicinityOfTheFieldOfSilenceResearchCenter)) {
                    htmltext = "32641-01.htm";
                } else {
                    htmltext = "32641-10.htm";
                }
            } else if (id == COMPLETED)
                htmltext = "32641-09.htm";
            else if (cond == 1)
                htmltext = "32641-04.htm";
            else if (cond == 2)
                htmltext = "32641-05.htm";
            else if (cond == 3)
                htmltext = "32641-07.htm";
            else if (cond == 4) {
                st.takeAllItems(VicinityOfTheFieldOfSilenceResearchCenter,GuardianSpiritFragment);
                st.giveAdena(283346);
                st.addExpAndSp(1319736, 103553);
                st.complete();
                st.finish();
                htmltext = "32641-08.htm";
            }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        int npcId = npc.getNpcId();
        if (cond == 1 && npcId == BrazierOfPurity) {
            st.giveItems(BrokenPieveOfMagicForce);
            if (st.getQuestItemsCount(BrokenPieveOfMagicForce) >= 10)
                st.setCond(2);
        } else if (cond == 3 && (npcId == EvilSpirit || npcId == GuardianSpirit)) {
            st.giveItems(GuardianSpiritFragment);
            if (st.haveQuestItem(GuardianSpiritFragment, 20))
                st.setCond(4);
        }
    }
}