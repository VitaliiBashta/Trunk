package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _271_ProofOfValor extends Quest {
    //NPC
    private static final int RUKAIN = 30577;
    //Quest Item
    private static final int KASHA_WOLF_FANG_ID = 1473;
    private static final int NECKLACE_OF_VALOR_ID = 1507;
    private static final int NECKLACE_OF_COURAGE_ID = 1506;

    public _271_ProofOfValor() {
        addStartNpc(RUKAIN);
        addKillId(20475);

        addQuestItem(KASHA_WOLF_FANG_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("praetorian_rukain_q0271_03.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_ACCEPT);
            if (st.haveAnyQuestItems(NECKLACE_OF_COURAGE_ID, NECKLACE_OF_VALOR_ID))
                htmltext = "praetorian_rukain_q0271_07.htm";
            st.setCond(1);
            st.start();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == RUKAIN)
            if (cond == 0) {
                if (st.player.getRace() != Race.orc) {
                    htmltext = "praetorian_rukain_q0271_00.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getLevel() < 4) {
                    htmltext = "praetorian_rukain_q0271_01.htm";
                    st.exitCurrentQuest();
                } else if (st.haveAnyQuestItems(NECKLACE_OF_COURAGE_ID, NECKLACE_OF_VALOR_ID)) {
                    htmltext = "praetorian_rukain_q0271_06.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "praetorian_rukain_q0271_02.htm";
            } else if (cond == 1)
                htmltext = "praetorian_rukain_q0271_04.htm";
            else if (cond == 2 && st.getQuestItemsCount(KASHA_WOLF_FANG_ID) == 50) {
                st.takeItems(KASHA_WOLF_FANG_ID);
                if (Rnd.chance(14)) {
                    st.takeItems(NECKLACE_OF_VALOR_ID);
                    st.giveItems(NECKLACE_OF_VALOR_ID);
                } else {
                    st.takeItems(NECKLACE_OF_COURAGE_ID);
                    st.giveItems(NECKLACE_OF_COURAGE_ID);
                }
                htmltext = "praetorian_rukain_q0271_05.htm";
                st.exitCurrentQuest();
            } else if (cond == 2 && st.getQuestItemsCount(KASHA_WOLF_FANG_ID) < 50) {
                htmltext = "praetorian_rukain_q0271_04.htm";
                st.setCond(1);
                st.start();
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1 && npc.getNpcId() == 20475) {
            if (st.rollAndGive(KASHA_WOLF_FANG_ID, 2, 2, 50, 25)) {
                st.setCond(2);
                st.start();
            }
        }
    }
}
