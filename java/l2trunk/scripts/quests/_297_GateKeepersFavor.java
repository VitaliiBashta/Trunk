package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _297_GateKeepersFavor extends Quest {
    private static final int STARSTONE = 1573;
    private static final int GATEKEEPER_TOKEN = 1659;

    public _297_GateKeepersFavor() {
        addStartNpc(30540);
        addKillId(20521);
        addQuestItem(STARSTONE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("gatekeeper_wirphy_q0297_03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        boolean haveQuestItem = st.haveQuestItem(STARSTONE, 20);
        if (npcId == 30540)
            if (cond == 0) {
                if (st.player.getLevel() >= 15)
                    htmltext = "gatekeeper_wirphy_q0297_02.htm";
                else
                    htmltext = "gatekeeper_wirphy_q0297_01.htm";
            } else if (cond == 1 && !haveQuestItem)
                htmltext = "gatekeeper_wirphy_q0297_04.htm";
            else if (cond == 2 && !haveQuestItem)
                htmltext = "gatekeeper_wirphy_q0297_04.htm";
            else if (cond == 2) {
                htmltext = "gatekeeper_wirphy_q0297_05.htm";
                st.takeItems(STARSTONE);
                st.giveItems(GATEKEEPER_TOKEN, 2);
                st.exitCurrentQuest();
                st.playSound(SOUND_FINISH);
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        st.rollAndGive(STARSTONE, 1, 1, 20, 33);
        if (st.haveQuestItem(STARSTONE, 20))
            st.setCond(2);
    }
}