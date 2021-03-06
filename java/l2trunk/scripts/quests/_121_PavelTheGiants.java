package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _121_PavelTheGiants extends Quest {
    //NPCs
    private static final int NEWYEAR = 31961;
    private static final int YUMI = 32041;

    public _121_PavelTheGiants() {
        addStartNpc(NEWYEAR);
        addTalkId(YUMI);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("collecter_yumi_q0121_0201.htm".equals(event)) {
            st.playSound(SOUND_FINISH);
            st.addExpAndSp(346320, 26069);
            st.finish();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();

        if (id == CREATED && npcId == NEWYEAR) {
            if (st.player.getLevel() >= 70) {
                htmltext = "head_blacksmith_newyear_q0121_0101.htm";
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
            } else {
                htmltext = "head_blacksmith_newyear_q0121_0103.htm";
                st.finish();
            }
        } else if (id == STARTED)
            if (npcId == YUMI && cond == 1)
                htmltext = "collecter_yumi_q0121_0101.htm";
            else
                htmltext = "head_blacksmith_newyear_q0121_0105.htm";
        return htmltext;
    }
}