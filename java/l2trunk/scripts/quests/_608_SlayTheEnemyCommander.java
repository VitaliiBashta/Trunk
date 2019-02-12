package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _608_SlayTheEnemyCommander extends Quest {
    // npc
    private static final int KADUN_ZU_KETRA = 31370;
    private static final int VARKAS_COMMANDER_MOS = 25312;

    //quest items
    private static final int HEAD_OF_MOS = 7236;
    private static final int TOTEM_OF_WISDOM = 7220;
    private static final int MARK_OF_KETRA_ALLIANCE4 = 7214;
    private static final int MARK_OF_KETRA_ALLIANCE5 = 7215;

    public _608_SlayTheEnemyCommander() {
        super(true);
        addStartNpc(KADUN_ZU_KETRA);
        addKillId(VARKAS_COMMANDER_MOS);
        addQuestItem(HEAD_OF_MOS);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("quest_accept".equalsIgnoreCase(event)) {
            htmltext = "elder_kadun_zu_ketra_q0608_0104.htm";
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if ("608_3".equals(event))
            if (st.haveQuestItem(HEAD_OF_MOS)) {
                htmltext = "elder_kadun_zu_ketra_q0608_0201.htm";
                st.takeItems(HEAD_OF_MOS);
                st.giveItems(TOTEM_OF_WISDOM);
                st.addExpAndSp(0, 10000);
                st.unset("cond");
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(true);
            } else
                htmltext = "elder_kadun_zu_ketra_q0608_0106.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            if (st.player.getLevel() >= 75) {
                if (st.haveQuestItem(MARK_OF_KETRA_ALLIANCE4)  || st.haveQuestItem(MARK_OF_KETRA_ALLIANCE5) )
                    htmltext = "elder_kadun_zu_ketra_q0608_0101.htm";
                else {
                    htmltext = "elder_kadun_zu_ketra_q0608_0102.htm";
                    st.exitCurrentQuest(true);
                }
            } else {
                htmltext = "elder_kadun_zu_ketra_q0608_0103.htm";
                st.exitCurrentQuest(true);
            }
        } else if (cond == 1 && !st.haveQuestItem(HEAD_OF_MOS) )
            htmltext = "elder_kadun_zu_ketra_q0608_0106.htm";
        else if (cond == 2 && st.haveQuestItem(HEAD_OF_MOS) )
            htmltext = "elder_kadun_zu_ketra_q0608_0105.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1) {
            st.giveItems(HEAD_OF_MOS);
            st.setCond(2);
            st.playSound(SOUND_ITEMGET);
        }
    }
}