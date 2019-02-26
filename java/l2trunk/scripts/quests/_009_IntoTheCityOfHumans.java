package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _009_IntoTheCityOfHumans extends Quest {
    //NPC
    private final int PETUKAI = 30583;
    private final int TANAPI = 30571;
    private final int TAMIL = 30576;
    //items
    private static final int SCROLL_OF_ESCAPE_GIRAN = 7126;
    //Quest Item
    private static final int MARK_OF_TRAVELER = 7570;

    public _009_IntoTheCityOfHumans() {
        super(false);

        addStartNpc(PETUKAI);

        addTalkId(TANAPI,TAMIL);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("centurion_petukai_q0009_0104.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("seer_tanapi_q0009_0201.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("gatekeeper_tamil_q0009_0301.htm".equalsIgnoreCase(event)) {
            st.giveItems(SCROLL_OF_ESCAPE_GIRAN);
            st.giveItems(MARK_OF_TRAVELER);
            st.unset("cond");
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
        if (npcId == PETUKAI) {
            if (cond == 0) {
                if (st.player.getRace() == Race.orc && st.player.getLevel() >= 3)
                    htmltext = "centurion_petukai_q0009_0101.htm";
                else {
                    htmltext = "centurion_petukai_q0009_0102.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "centurion_petukai_q0009_0105.htm";
        } else if (npcId == TANAPI) {
            if (cond == 1)
                htmltext = "seer_tanapi_q0009_0101.htm";
            else if (cond == 2)
                htmltext = "seer_tanapi_q0009_0202.htm";
        } else if (npcId == TAMIL)
            if (cond == 2)
                htmltext = "gatekeeper_tamil_q0009_0201.htm";
        return htmltext;
    }
}