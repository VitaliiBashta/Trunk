package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _277_GatekeepersOffering extends Quest {
    private static final int STARSTONE1_ID = 1572;
    private static final int GATEKEEPER_CHARM_ID = 1658;

    public _277_GatekeepersOffering() {
        addStartNpc(30576);
        addKillId(20333);
        addQuestItem(STARSTONE1_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("1".equals(event))
            if (st.player.getLevel() >= 15) {
                htmltext = "gatekeeper_tamil_q0277_03.htm";
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
            } else
                htmltext = "gatekeeper_tamil_q0277_01.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (npcId == 30576 && cond == 0)
            htmltext = "gatekeeper_tamil_q0277_02.htm";
        else if (npcId == 30576 && cond == 1 && st.getQuestItemsCount(STARSTONE1_ID) < 20)
            htmltext = "gatekeeper_tamil_q0277_04.htm";
        else if (npcId == 30576 && cond == 2 && st.getQuestItemsCount(STARSTONE1_ID) < 20)
            htmltext = "gatekeeper_tamil_q0277_04.htm";
        else if (npcId == 30576 && cond == 2 && st.getQuestItemsCount(STARSTONE1_ID) >= 20) {
            htmltext = "gatekeeper_tamil_q0277_05.htm";
            st.takeItems(STARSTONE1_ID);
            st.giveItems(GATEKEEPER_CHARM_ID, 2);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        st.rollAndGive(STARSTONE1_ID, 1, 1, 20, 33);
        if (st.haveQuestItem(STARSTONE1_ID,20))
            st.setCond(2);
    }
}