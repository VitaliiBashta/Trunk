package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _251_NoSecrets extends Quest {
    private static final int GuardPinaps = 30201;
    private static final List<Integer> SelMahumTrainers = List.of(22775, 22777, 22778);
    private static final List<Integer> SelMahumRecruits = List.of(22780, 22782, 22784);
    private static final int SelMahumTrainingDiary = 15508;
    private static final int SelMahumTrainingTimetable = 15509;

    public _251_NoSecrets() {
        super(false);
        addStartNpc(GuardPinaps);
        addKillId(SelMahumTrainers);
        addKillId(SelMahumRecruits);
        addQuestItem(SelMahumTrainingDiary, SelMahumTrainingTimetable);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("pinaps_q251_03.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npc.getNpcId() == GuardPinaps) {
            if (cond == 0) {
                if (st.getPlayer().getLevel() >= 82)
                    htmltext = "pinaps_q251_01.htm";
                else
                    htmltext = "pinaps_q251_00.htm";
            } else if (cond == 1)
                htmltext = "pinaps_q251_04.htm";
            else if (cond == 2) {
                st.takeAllItems(SelMahumTrainingDiary, SelMahumTrainingTimetable);
                htmltext = "pinaps_q251_05.htm";
                st.setState(COMPLETED);
                st.giveItems(57, 313355);
                st.addExpAndSp(56787, 160578);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(false);
            }
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        if (cond == 1) {
            if (st.getQuestItemsCount(SelMahumTrainingDiary) < 10 && SelMahumRecruits.contains(npc.getNpcId()))
                st.rollAndGive(SelMahumTrainingDiary, 3, 40);
            else if (st.getQuestItemsCount(SelMahumTrainingTimetable) < 5 && SelMahumTrainers.contains(npc.getNpcId()))
                st.rollAndGive(SelMahumTrainingTimetable, 3, 25);

            if (st.getQuestItemsCount(SelMahumTrainingDiary) >= 10 && st.getQuestItemsCount(SelMahumTrainingTimetable) >= 5)
                st.setCond(2);
        }
        return null;
    }
}