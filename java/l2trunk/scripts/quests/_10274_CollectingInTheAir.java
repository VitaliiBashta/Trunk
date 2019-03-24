package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _10274_CollectingInTheAir extends Quest {
    private final static int Lekon = 32557;

    private final static int StarStoneExtractionScroll = 13844;
    private final static int ExpertTextStarStoneExtractionSkillLevel1 = 13728;
    private final static int ExtractedCoarseRedStarStone = 13858;
    private final static int ExtractedCoarseBlueStarStone = 13859;
    private final static int ExtractedCoarseGreenStarStone = 13860;

    public _10274_CollectingInTheAir() {
        addStartNpc(Lekon);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("32557-03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.giveItems(StarStoneExtractionScroll, 8);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int id = st.getState();
        if (id == COMPLETED)
            htmltext = "32557-0a.htm";
        else if (id == CREATED) {
            if (st.player.isQuestCompleted(_10273_GoodDayToFly.class) && st.player.getLevel() >= 75)
                htmltext = "32557-01.htm";
            else
                htmltext = "32557-00.htm";
        } else if (st.getQuestItemsCount(ExtractedCoarseRedStarStone) + st.getQuestItemsCount(ExtractedCoarseBlueStarStone) + st.getQuestItemsCount(ExtractedCoarseGreenStarStone) >= 8) {
            htmltext = "32557-05.htm";
            st.takeAllItems(ExtractedCoarseRedStarStone, ExtractedCoarseBlueStarStone, ExtractedCoarseGreenStarStone);
            st.giveItems(ExpertTextStarStoneExtractionSkillLevel1);
            st.addExpAndSp(25160, 2525);
            st.finish();
            st.playSound(SOUND_FINISH);
        } else
            htmltext = "32557-04.htm";
        return htmltext;
    }
}