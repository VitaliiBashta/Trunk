package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _10504_JewelOfAntharas extends Quest {
    //NPC's
    private static final int THEODRIC = 30755;
    private static final int ULTIMATE_ANTHARAS = 29068;
    //Item's
    private static final int CLEAR_CRYSTAL = 21905;
    private static final int FILLED_CRYSTAL_ANTHARAS = 21907;
    private static final int PORTAL_STONE = 3865;
    private static final int JEWEL_OF_ANTHARAS = 21898;

    public _10504_JewelOfAntharas() {
        super(PARTY_ALL);
        addStartNpc(THEODRIC);
        addQuestItem(CLEAR_CRYSTAL, FILLED_CRYSTAL_ANTHARAS);
        addKillId(ULTIMATE_ANTHARAS);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("antharas_watchman_theodric_q10504_04.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
            st.giveItems(CLEAR_CRYSTAL, 1);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == THEODRIC) {
            if (cond == 0) {
                if (st.getPlayer().getLevel() < 84)
                    htmltext = "antharas_watchman_theodric_q10504_00.htm";
                else if (st.getQuestItemsCount(PORTAL_STONE) < 1)
                    htmltext = "antharas_watchman_theodric_q10504_00a.htm";
                else if (st.isNowAvailable())
                    htmltext = "antharas_watchman_theodric_q10504_01.htm";
                else
                    htmltext = "antharas_watchman_theodric_q10504_09.htm";
            } else if (cond == 1) {
                if (st.getQuestItemsCount(CLEAR_CRYSTAL) < 1) {
                    htmltext = "antharas_watchman_theodric_q10504_08.htm";
                    st.giveItems(CLEAR_CRYSTAL, 1);
                } else
                    htmltext = "antharas_watchman_theodric_q10504_05.htm";
            } else if (cond == 2) {
                if (st.getQuestItemsCount(FILLED_CRYSTAL_ANTHARAS) >= 1) {
                    htmltext = "antharas_watchman_theodric_q10504_07.htm";
                    st.takeAllItems(FILLED_CRYSTAL_ANTHARAS);
                    st.giveItems(JEWEL_OF_ANTHARAS, 1);
                    st.playSound(SOUND_FINISH);
                    st.setState(COMPLETED);
                    st.exitCurrentQuest(false);
                } else
                    htmltext = "antharas_watchman_theodric_q10504_06.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (cond == 1 && npcId == ULTIMATE_ANTHARAS) {
            st.takeAllItems(CLEAR_CRYSTAL);
            st.giveItems(FILLED_CRYSTAL_ANTHARAS, 1);
            st.setCond(2);
        }
        return null;
    }
}