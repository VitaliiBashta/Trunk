package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _008_AnAdventureBegins extends Quest {
    private static final int SCROLL_OF_ESCAPE_GIRAN = 7126;
    private static final int MARK_OF_TRAVELER = 7570;
    private final int JASMINE = 30134;
    private final int ROSELYN = 30355;
    private final int HARNE = 30144;
    private final int ROSELYNS_NOTE = 7573;

    public _008_AnAdventureBegins() {
        addStartNpc(JASMINE);

        addTalkId(ROSELYN,HARNE);

        addQuestItem(ROSELYNS_NOTE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("jasmine_q0008_0104.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("sentry_roseline_q0008_0201.htm".equalsIgnoreCase(event)) {
            st.giveItems(ROSELYNS_NOTE);
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("harne_q0008_0301.htm".equalsIgnoreCase(event)) {
            st.takeItems(ROSELYNS_NOTE);
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        } else if ("jasmine_q0008_0401.htm".equalsIgnoreCase(event)) {
            st.giveItems(SCROLL_OF_ESCAPE_GIRAN);
            st.giveItems(MARK_OF_TRAVELER);
            st.setCond(0);
            st.playSound(SOUND_FINISH);
            st.finish();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        boolean haveNote = st.haveQuestItem(ROSELYNS_NOTE);
        if (npcId == JASMINE) {
            if (cond == 0 && st.player.getRace() == Race.darkelf)
                if (st.player.getLevel() >= 3)
                    htmltext = "jasmine_q0008_0101.htm";
                else {
                    htmltext = "jasmine_q0008_0102.htm";
                    st.exitCurrentQuest();
                }
            else if (cond == 1)
                htmltext = "jasmine_q0008_0105.htm";
            else if (cond == 3)
                htmltext = "jasmine_q0008_0301.htm";
        } else if (npcId == ROSELYN) {
            if (!haveNote)
                htmltext = "sentry_roseline_q0008_0101.htm";
            else
                htmltext = "sentry_roseline_q0008_0202.htm";
        } else if (npcId == HARNE)
            if (cond == 2 && haveNote)
                htmltext = "harne_q0008_0201.htm";
            else if (cond == 2)
                htmltext = "harne_q0008_0302.htm";
            else if (cond == 3)
                htmltext = "harne_q0008_0303.htm";
        return htmltext;
    }

}