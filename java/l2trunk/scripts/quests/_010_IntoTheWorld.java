package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _010_IntoTheWorld extends Quest {
    private static final int VERY_EXPENSIVE_NECKLACE = 7574;
    private static final int SCROLL_OF_ESCAPE_GIRAN = 7126;
    private static final int MARK_OF_TRAVELER = 7570;

    private static final int BALANKI = 30533;
    private static final int REED = 30520;
    private static final int GERALD = 30650;

    public _010_IntoTheWorld() {
        super(false);

        addStartNpc(BALANKI);

        addTalkId(REED, GERALD);

        addQuestItem(VERY_EXPENSIVE_NECKLACE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("elder_balanki_q0010_0104.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("warehouse_chief_reed_q0010_0201.htm".equalsIgnoreCase(event)) {
            st.giveItems(VERY_EXPENSIVE_NECKLACE);
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("gerald_priest_of_earth_q0010_0301.htm".equalsIgnoreCase(event)) {
            st.takeItems(VERY_EXPENSIVE_NECKLACE);
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        } else if ("warehouse_chief_reed_q0010_0401.htm".equalsIgnoreCase(event)) {
            st.setCond(4);
            st.playSound(SOUND_MIDDLE);
        } else if ("elder_balanki_q0010_0501.htm".equalsIgnoreCase(event)) {
            st.giveItems(SCROLL_OF_ESCAPE_GIRAN);
            st.giveItems(MARK_OF_TRAVELER);
            st.finish();
            st.playSound(SOUND_FINISH);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == BALANKI) {
            if (cond == 0) {
                if (st.player.getRace() == Race.dwarf && st.player.getLevel() >= 3)
                    htmltext = "elder_balanki_q0010_0101.htm";
                else {
                    htmltext = "elder_balanki_q0010_0102.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "elder_balanki_q0010_0105.htm";
            else if (cond == 4)
                htmltext = "elder_balanki_q0010_0401.htm";
        } else if (npcId == REED) {
            if (cond == 1)
                htmltext = "warehouse_chief_reed_q0010_0101.htm";
            else if (cond == 2)
                htmltext = "warehouse_chief_reed_q0010_0202.htm";
            else if (cond == 3)
                htmltext = "warehouse_chief_reed_q0010_0301.htm";
            else if (cond == 4)
                htmltext = "warehouse_chief_reed_q0010_0402.htm";
        } else if (npcId == GERALD)
            if (cond == 2 && st.haveQuestItem(VERY_EXPENSIVE_NECKLACE))
                htmltext = "gerald_priest_of_earth_q0010_0201.htm";
            else if (cond == 3)
                htmltext = "gerald_priest_of_earth_q0010_0302.htm";
            else
                htmltext = "gerald_priest_of_earth_q0010_0303.htm";
        return htmltext;
    }
}