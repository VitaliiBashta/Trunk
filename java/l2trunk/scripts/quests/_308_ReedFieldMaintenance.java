package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _308_ReedFieldMaintenance extends Quest {
    private static final int Katensa = 32646;

    private static final int MucrokianHide = 14871;
    private static final int AwakenMucrokianHide = 14872;

    private static final int MucrokianFanatic = 22650;
    private static final int MucrokianAscetic = 22651;
    private static final int MucrokianSavior = 22652;
    private static final int MucrokianPreacher = 22653;
    private static final int ContaminatedMucrokian = 22654;
    private static final int ChangedMucrokian = 22655;
    private static final List<Integer> MoiraiRecipes = List.of(
            15777, 15780, 15783, 15786, 15789, 15790, 15812, 15813, 15814);
    private static final List<Integer> Moiraimaterials = List.of(
            15647, 15650, 15653, 15656, 15659, 15692, 15772, 15773, 15774);

    public _308_ReedFieldMaintenance() {
        addStartNpc(Katensa);
        addQuestItem(MucrokianHide, AwakenMucrokianHide);
        addKillId(MucrokianFanatic, MucrokianAscetic, MucrokianSavior, MucrokianPreacher, ContaminatedMucrokian, ChangedMucrokian);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("32646-04.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
        } else if ("32646-11.htm".equalsIgnoreCase(event))
            st.exitCurrentQuest();
        else if ("moirairec".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(MucrokianHide) >= 180) {
                st.takeItems(MucrokianHide, 180);
                st.giveItems(Rnd.get(MoiraiRecipes), 1);
                return null;
            } else
                htmltext = "32646-16.htm";
        } else if ("moiraimat".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(MucrokianHide) >= 100) {
                st.takeItems(MucrokianHide, 100);
                st.giveItems(Rnd.get(Moiraimaterials));
                return null;
            } else
                htmltext = "32646-16.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();

        if (npcId == Katensa)
            if (id == CREATED) {
                QuestState qs1 = st.player.getQuestState(_309_ForAGoodCause.class);
                if (qs1 != null && qs1.isStarted())
                    return "32646-15.htm"; // нельзя брать оба квеста сразу
                if (st.player.getLevel() < 82)
                    return "32646-00.htm";
                return "32646-01.htm";
            } else if (cond == 1) {
                long awaken = st.getQuestItemsCount(AwakenMucrokianHide);
                st.takeItems(AwakenMucrokianHide);
                if (awaken > 0)
                    st.giveItems(MucrokianHide, awaken * 2);

                if (st.getQuestItemsCount(MucrokianHide) == 0)
                    return "32646-05.htm";
                else if (!st.player.isQuestCompleted(_238_SuccessFailureOfBusiness.class))
                    return "32646-a1.htm"; // обычные цены
                else
                    return "32646-a2.htm"; // со скидкой
            }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        st.rollAndGive(npc.getNpcId() == ChangedMucrokian ? AwakenMucrokianHide : MucrokianHide, 1, 60);
    }
}