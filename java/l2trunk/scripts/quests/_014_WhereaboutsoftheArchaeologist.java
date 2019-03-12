package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _014_WhereaboutsoftheArchaeologist extends Quest {
    private static final int LETTER_TO_ARCHAEOLOGIST = 7253;

    public _014_WhereaboutsoftheArchaeologist() {
        super(false);

        addStartNpc(31263);
        addTalkId(31538);

        addQuestItem(LETTER_TO_ARCHAEOLOGIST);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("trader_liesel_q0014_0104.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.giveItems(LETTER_TO_ARCHAEOLOGIST);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("explorer_ghost_a_q0014_0201.htm".equalsIgnoreCase(event)) {
            st.takeItems(LETTER_TO_ARCHAEOLOGIST);
            st.addExpAndSp(325881, 32524);
            st.giveItems(ADENA_ID, 136928);
            st.playSound(SOUND_FINISH);
            st.finish();
            return "explorer_ghost_a_q0014_0201.htm";
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == 31263) {
            if (cond == 0)
                if (st.player.getLevel() >= 74)
                    htmltext = "trader_liesel_q0014_0101.htm";
                else {
                    htmltext = "trader_liesel_q0014_0103.htm";
                    st.exitCurrentQuest();
                }
            else if (cond == 1)
                htmltext = "trader_liesel_q0014_0104.htm";
        } else if (npcId == 31538)
            if (cond == 1 && st.haveQuestItem(LETTER_TO_ARCHAEOLOGIST))
                htmltext = "explorer_ghost_a_q0014_0101.htm";
        return htmltext;
    }
}