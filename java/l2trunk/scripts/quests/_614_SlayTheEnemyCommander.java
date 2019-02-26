package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _614_SlayTheEnemyCommander extends Quest {
    // NPC
    private static final int DURAI = 31377;
    private static final int KETRAS_COMMANDER_TAYR = 25302;

    private static final int HEAD_OF_TAYR = 7241;
    private static final int FEATHER_OF_WISDOM = 7230;

    public _614_SlayTheEnemyCommander() {
        super(true);
        addStartNpc(DURAI);
        addKillId(KETRAS_COMMANDER_TAYR);
        addQuestItem(HEAD_OF_TAYR);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("quest_accept".equalsIgnoreCase(event)) {
            htmltext = "elder_ashas_barka_durai_q0614_0104.htm";
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("614_3".equals(event))
            if (st.haveQuestItem(HEAD_OF_TAYR) ) {
                htmltext = "elder_ashas_barka_durai_q0614_0201.htm";
                st.takeItems(HEAD_OF_TAYR);
                st.giveItems(FEATHER_OF_WISDOM);
                st.addExpAndSp(0, 10000);
                st.unset("cond");
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            } else
                htmltext = "elder_ashas_barka_durai_q0614_0106.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            if (st.player.getLevel() >= 75) {
                if (st.player.getVarka() >3)
                    htmltext = "elder_ashas_barka_durai_q0614_0101.htm";
                else {
                    htmltext = "elder_ashas_barka_durai_q0614_0102.htm";
                    st.exitCurrentQuest();
                }
            } else {
                htmltext = "elder_ashas_barka_durai_q0614_0103.htm";
                st.exitCurrentQuest();
            }
        } else if (cond == 1 && st.getQuestItemsCount(HEAD_OF_TAYR) == 0)
            htmltext = "elder_ashas_barka_durai_q0614_0106.htm";
        else if (cond == 2 && st.getQuestItemsCount(HEAD_OF_TAYR) >= 1)
            htmltext = "elder_ashas_barka_durai_q0614_0105.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1) {
            st.giveItems(HEAD_OF_TAYR);
            st.setCond(2);
            st.playSound(SOUND_ITEMGET);
        }
    }
}