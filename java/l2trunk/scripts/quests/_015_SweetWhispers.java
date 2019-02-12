package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _015_SweetWhispers extends Quest {
    public _015_SweetWhispers() {
        super(false);

        addStartNpc(31302);

        addTalkId(31517);
        addTalkId(31518);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("trader_vladimir_q0015_0104.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if ("dark_necromancer_q0015_0201.htm".equalsIgnoreCase(event))
            st.setCond(2);
        else if ("dark_presbyter_q0015_0301.htm".equalsIgnoreCase(event)) {
            st.addExpAndSp(350531, 28204);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(false);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == 31302) {
            if (cond == 0)
                if (st.player.getLevel() >= 60)
                    htmltext = "trader_vladimir_q0015_0101.htm";
                else {
                    htmltext = "trader_vladimir_q0015_0103.htm";
                    st.exitCurrentQuest(true);
                }
            else if (cond >= 1)
                htmltext = "trader_vladimir_q0015_0105.htm";
        } else if (npcId == 31518) {
            if (cond == 1)
                htmltext = "dark_necromancer_q0015_0101.htm";
            else if (cond == 2)
                htmltext = "dark_necromancer_q0015_0202.htm";
        } else if (npcId == 31517)
            if (cond == 2)
                htmltext = "dark_presbyter_q0015_0201.htm";
        return htmltext;
    }

}