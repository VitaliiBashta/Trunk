package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _10505_JewelOfValakas extends Quest {
    //NPC's
    private static final int KLEIN = 31540;
    private static final int VALAKAS = 29028;
    //Item's
    private static final int EMPTY_CRYSTAL = 21906;
    private static final int FILLED_CRYSTAL_VALAKAS = 21908;
    private static final int VACUALITE_FLOATING_STONE = 7267;
    private static final int JEWEL_OF_VALAKAS = 21896;

    public _10505_JewelOfValakas() {
        super(PARTY_ALL);
        addStartNpc(KLEIN);
        addQuestItem(EMPTY_CRYSTAL, FILLED_CRYSTAL_VALAKAS);
        addKillId(VALAKAS);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("valakas_watchman_klein_q10505_04.htm".equalsIgnoreCase(event)) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
            st.giveItems(EMPTY_CRYSTAL);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == KLEIN) {
            if (cond == 0) {
                if (st.player.getLevel() < 84)
                    htmltext = "valakas_watchman_klein_q10505_00.htm";
                else if (st.getQuestItemsCount(VACUALITE_FLOATING_STONE) < 1)
                    htmltext = "valakas_watchman_klein_q10505_00a.htm";
                else if (st.isNowAvailable())
                    htmltext = "valakas_watchman_klein_q10505_01.htm";
                else
                    htmltext = "valakas_watchman_klein_q10505_09.htm";
            } else if (cond == 1) {
                if (st.haveQuestItem(EMPTY_CRYSTAL)) {
                    htmltext = "valakas_watchman_klein_q10505_05.htm";
                } else {
                    htmltext = "valakas_watchman_klein_q10505_08.htm";
                    st.giveItems(EMPTY_CRYSTAL);
                }
            } else if (cond == 2) {
                if (st.haveQuestItem(FILLED_CRYSTAL_VALAKAS)) {
                    htmltext = "valakas_watchman_klein_q10505_07.htm";
                    st.takeItems(FILLED_CRYSTAL_VALAKAS);
                    st.giveItems(JEWEL_OF_VALAKAS);
                    st.playSound(SOUND_FINISH);
                    st.setState(COMPLETED);
                    st.exitCurrentQuest(false);
                } else
                    htmltext = "valakas_watchman_klein_q10505_06.htm";
            }
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1 && npcId == VALAKAS) {
            st.takeItems(EMPTY_CRYSTAL);
            st.giveItems(FILLED_CRYSTAL_VALAKAS);
            st.setCond(2);
        }
    }
}