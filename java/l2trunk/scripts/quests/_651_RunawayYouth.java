package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _651_RunawayYouth extends Quest {
    //Npc
    private static final int IVAN = 32014;
    private static final int BATIDAE = 31989;

    //items
    private static final int SOE = 736;

    public _651_RunawayYouth() {
        addStartNpc(IVAN);
        addTalkId(BATIDAE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("runaway_boy_ivan_q0651_03.htm".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(SOE)) {
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
                st.takeItems(SOE, 1);
                htmltext = "runaway_boy_ivan_q0651_04.htm";
                //npc.broadcastPacket(MagicSkillUser(npc,npc,2013,1,20000,0));
                //Каст СОЕ и изчезновение НПЦ
                st.startQuestTimer("ivan_timer", 20000);
            }
        } else if ("runaway_boy_ivan_q0651_05.htm".equalsIgnoreCase(event)) {
            st.exitCurrentQuest();
            st.playSound(SOUND_GIVEUP);
        } else if ("ivan_timer".equalsIgnoreCase(event)) {
            npc.deleteMe();
            htmltext = null;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == IVAN && cond == 0) {
            if (st.player.getLevel() >= 26)
                htmltext = "runaway_boy_ivan_q0651_01.htm";
            else {
                htmltext = "runaway_boy_ivan_q0651_01a.htm";
                st.exitCurrentQuest();
            }
        } else if (npcId == BATIDAE && cond == 1) {
            htmltext = "fisher_batidae_q0651_01.htm";
            st.giveAdena(Math.round(2883 * st.getRateQuestsReward()));
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return htmltext;
    }
}
