package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _107_MercilessPunishment extends Quest {
    private final int HATOSS_ORDER1 = 1553;
    private final int HATOSS_ORDER2 = 1554;
    private final int HATOSS_ORDER3 = 1555;
    private final int LETTER_TO_HUMAN = 1557;
    private final int LETTER_TO_DARKELF = 1556;
    private final int LETTER_TO_ELF = 1558;
    private static final int BUTCHER = 1510;

    public _107_MercilessPunishment() {
        addStartNpc(30568);

        addTalkId(30580);

        addKillId(27041);

        addQuestItem(LETTER_TO_DARKELF, LETTER_TO_HUMAN, LETTER_TO_ELF, HATOSS_ORDER1, HATOSS_ORDER2, HATOSS_ORDER3);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("urutu_chief_hatos_q0107_03.htm".equalsIgnoreCase(event)) {
            st.giveItems(HATOSS_ORDER1);
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("urutu_chief_hatos_q0107_06.htm".equalsIgnoreCase(event)) {
            st.takeItems(HATOSS_ORDER2, 1);
            st.takeItems(LETTER_TO_DARKELF, 1);
            st.takeItems(LETTER_TO_HUMAN, 1);
            st.takeItems(LETTER_TO_ELF, 1);
            st.takeItems(HATOSS_ORDER1, 1);
            st.takeItems(HATOSS_ORDER2, 1);
            st.takeItems(HATOSS_ORDER3, 1);
            st.giveAdena(200);
            st.unset("cond");
            st.playSound(SOUND_GIVEUP);
        } else if ("urutu_chief_hatos_q0107_07.htm".equalsIgnoreCase(event)) {
            st.takeItems(HATOSS_ORDER1, 1);
            st.giveItemIfNotHave(HATOSS_ORDER2);
        } else if ("urutu_chief_hatos_q0107_09.htm".equalsIgnoreCase(event)) {
            st.takeItems(HATOSS_ORDER2, 1);
            if (st.getQuestItemsCount(HATOSS_ORDER3) == 0)
                st.giveItems(HATOSS_ORDER3);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        int cond = 0;
        if (id != CREATED)
            cond = st.getCond();
        if (npcId == 30568) {
            if (id == CREATED) {
                if (st.player.getRace() != Race.orc) {
                    htmltext = "urutu_chief_hatos_q0107_00.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getLevel() >= 10)
                    htmltext = "urutu_chief_hatos_q0107_02.htm";
                else {
                    htmltext = "urutu_chief_hatos_q0107_01.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1 && st.getQuestItemsCount(HATOSS_ORDER1) > 0)
                htmltext = "urutu_chief_hatos_q0107_04.htm";
            else if (cond == 2 && st.getQuestItemsCount(HATOSS_ORDER1) > 0 && st.getQuestItemsCount(LETTER_TO_HUMAN) == 0)
                htmltext = "urutu_chief_hatos_q0107_04.htm";
            else if (cond == 3 && st.getQuestItemsCount(HATOSS_ORDER1) > 0 && st.getQuestItemsCount(LETTER_TO_HUMAN) >= 1) {
                htmltext = "urutu_chief_hatos_q0107_05.htm";
                st.setCond(4);
            } else if (cond == 4 && st.getQuestItemsCount(HATOSS_ORDER2) > 0 && st.getQuestItemsCount(LETTER_TO_DARKELF) == 0)
                htmltext = "urutu_chief_hatos_q0107_05.htm";
            else if (cond == 5 && st.haveQuestItem(HATOSS_ORDER2) && st.getQuestItemsCount(LETTER_TO_DARKELF) >= 1) {
                htmltext = "urutu_chief_hatos_q0107_08.htm";
                st.setCond(6);
            } else if (cond == 6 && st.haveQuestItem(HATOSS_ORDER3) && st.getQuestItemsCount(LETTER_TO_ELF) == 0)
                htmltext = "urutu_chief_hatos_q0107_08.htm";
            else if (cond == 7 && st.haveAllQuestItems(HATOSS_ORDER3, LETTER_TO_ELF, LETTER_TO_HUMAN, LETTER_TO_DARKELF)) {
                htmltext = "urutu_chief_hatos_q0107_10.htm";
                st.takeAllItems(LETTER_TO_DARKELF, LETTER_TO_HUMAN, LETTER_TO_ELF, HATOSS_ORDER3);

                st.giveItems(BUTCHER);
                st.player.addExpAndSp(34565, 2962);
                st.giveAdena(14666);

                if (st.player.getClassId().occupation() == 0) {
                    st.player.setVar("p1q3"); // flag for helper
                    st.player.sendPacket(new ExShowScreenMessage("Acquisition of race-specific weapon complete.\n           Go find the Newbie Guide."));
                    st.giveItems(1060, 100); // healing potion
                    for (int item = 4412; item <= 4417; item++)
                        st.giveItems(item, 10); // echo cry
                    st.playTutorialVoice("tutorial_voice_026");
                    st.giveItems(5789, 7000); // newbie ss
                }

                st.finish();
                st.playSound(SOUND_FINISH);
            }
        } else if (npcId == 30580 && cond >= 1 && (st.haveAnyQuestItems(HATOSS_ORDER1, HATOSS_ORDER2, HATOSS_ORDER3))) {
            if (cond == 1)
                st.setCond(2);
            htmltext = "centurion_parugon_q0107_01.htm";
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == 27041)
            if (cond == 2 && st.haveQuestItem(HATOSS_ORDER1)) {
                st.giveItemIfNotHave(LETTER_TO_HUMAN);
                st.setCond(3);
            } else if (cond == 4 && st.haveQuestItem(HATOSS_ORDER2)) {
                st.giveItemIfNotHave(LETTER_TO_DARKELF);
                st.setCond(5);
            } else if (cond == 6 && st.haveQuestItem(HATOSS_ORDER3)) {
                st.giveItemIfNotHave(LETTER_TO_ELF);
                st.setCond(7);
            }
    }
}