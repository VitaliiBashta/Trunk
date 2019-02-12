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
        if ("antharas_watchman_theodric_q10504_04.htm".equalsIgnoreCase(event)) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
            st.giveItems(CLEAR_CRYSTAL);
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
                if (st.player.getLevel() < 84)
                    htmltext = "antharas_watchman_theodric_q10504_00.htm";
                else if (st.haveQuestItem(PORTAL_STONE)) {
                    if (st.isNowAvailable())
                        htmltext = "antharas_watchman_theodric_q10504_01.htm";
                    else
                        htmltext = "antharas_watchman_theodric_q10504_09.htm";
                } else {
                    htmltext = "antharas_watchman_theodric_q10504_00a.htm";
                }
            } else if (cond == 1) {
                if (st.haveQuestItem(CLEAR_CRYSTAL)) {
                    htmltext = "antharas_watchman_theodric_q10504_05.htm";
                } else {
                    htmltext = "antharas_watchman_theodric_q10504_08.htm";
                    st.giveItems(CLEAR_CRYSTAL);
                }
            } else if (cond == 2) {
                if (st.haveQuestItem(FILLED_CRYSTAL_ANTHARAS)) {
                    htmltext = "antharas_watchman_theodric_q10504_07.htm";
                    st.takeItems(FILLED_CRYSTAL_ANTHARAS);
                    st.giveItems(JEWEL_OF_ANTHARAS);
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
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (cond == 1 && npcId == ULTIMATE_ANTHARAS) {
            st.takeItems(CLEAR_CRYSTAL);
            st.giveItems(FILLED_CRYSTAL_ANTHARAS);
            st.setCond(2);
        }
    }
}