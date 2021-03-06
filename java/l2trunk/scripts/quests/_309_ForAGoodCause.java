package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _309_ForAGoodCause extends Quest {
    private static final int Atra = 32647;

    private static final int MucrokianHide = 14873;
    private static final int FallenMucrokianHide = 14874;

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

    public _309_ForAGoodCause() {
        addStartNpc(Atra);
        addQuestItem(MucrokianHide, FallenMucrokianHide);
        addKillId(MucrokianFanatic, MucrokianAscetic, MucrokianSavior, MucrokianPreacher, ContaminatedMucrokian, ChangedMucrokian);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("32647-05.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
        } else if ("32646-14.htm".equalsIgnoreCase(event)) {
            st.exitCurrentQuest();
        } else if ("moirairec".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(MucrokianHide) >= 180) {
                st.takeItems(MucrokianHide, 180);
                st.giveItems(Rnd.get(MoiraiRecipes));
                return null;
            } else
                htmltext = "32646-14.htm";
        } else if ("moiraimat".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(MucrokianHide) >= 100) {
                st.takeItems(MucrokianHide, 100);
                st.giveItems(Rnd.get(Moiraimaterials));
                return null;
            } else
                htmltext = "32646-14.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();

        if (npcId == Atra)
            if (id == CREATED) {
                QuestState qs1 = st.player.getQuestState(_308_ReedFieldMaintenance.class);
                if (qs1 != null && qs1.isStarted())
                    return "32647-17.htm"; // нельзя брать оба квеста сразу
                if (st.player.getLevel() < 82)
                    return "32647-00.htm";
                return "32647-01.htm";
            } else if (cond == 1) {
                long fallen = st.getQuestItemsCount(FallenMucrokianHide);
                st.takeItems(FallenMucrokianHide);
                if (fallen > 0)
                    st.giveItems(MucrokianHide, fallen * 2);

                if (st.getQuestItemsCount(MucrokianHide) == 0)
                    return "32647-06.htm"; // нечего менять
                else if (!st.player.isQuestCompleted(_239_WontYouJoinUs.class))
                    return "32647-a1.htm"; // обычные цены
                else
                    return "32647-a2.htm"; // со скидкой
            }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        st.rollAndGive(npc.getNpcId() == ContaminatedMucrokian ? FallenMucrokianHide : MucrokianHide, 1, 60);
    }
}