package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _102_SeaofSporesFever extends Quest {
    private final int ALBERRYUS_LETTER = 964;
    private final int EVERGREEN_AMULET = 965;
    private final int DRYAD_TEARS = 966;
    private final int COBS_MEDICINE1 = 1130;
    private final int COBS_MEDICINE2 = 1131;
    private final int COBS_MEDICINE3 = 1132;
    private final int COBS_MEDICINE4 = 1133;
    private final int COBS_MEDICINE5 = 1134;
    private static final int SWORD_OF_SENTINEL = 743;
    private static final int STAFF_OF_SENTINEL = 744;
    private final int ALBERRYUS_LIST = 746;

    public _102_SeaofSporesFever() {
        super(false);

        addStartNpc(30284);

        addTalkId(30156,30217,30219,30221,30284,30285);

        addKillId(20013,20019);

        addQuestItem(ALBERRYUS_LETTER, EVERGREEN_AMULET, DRYAD_TEARS, COBS_MEDICINE1, COBS_MEDICINE2, COBS_MEDICINE3, COBS_MEDICINE4, COBS_MEDICINE5, ALBERRYUS_LIST);
    }

    private void check(QuestState st) {
        if (st.getQuestItemsCount(COBS_MEDICINE2) == 0 && st.getQuestItemsCount(COBS_MEDICINE3) == 0 && st.getQuestItemsCount(COBS_MEDICINE4) == 0 && st.getQuestItemsCount(COBS_MEDICINE5) == 0)
            st.setCond(6);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("alberryus_q0102_02.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.giveItems(ALBERRYUS_LETTER);
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == 30284) {
            if (cond == 0) {
                if (st.player.getRace() != Race.elf) {
                    htmltext = "alberryus_q0102_00.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getLevel() >= 12) {
                    htmltext = "alberryus_q0102_07.htm";
                    return htmltext;
                } else {
                    htmltext = "alberryus_q0102_08.htm";
                    st.exitCurrentQuest();
                }

            } else if (cond == 1 && st.getQuestItemsCount(ALBERRYUS_LETTER) == 1)
                htmltext = "alberryus_q0102_03.htm";
            else if (cond == 2 && st.getQuestItemsCount(EVERGREEN_AMULET) == 1)
                htmltext = "alberryus_q0102_09.htm";
            else if (cond == 4 && st.getQuestItemsCount(COBS_MEDICINE1) == 1) {
                st.setCond(5);
                st.takeItems(COBS_MEDICINE1, 1);
                st.giveItems(ALBERRYUS_LIST);
                htmltext = "alberryus_q0102_04.htm";
            } else if (cond == 5)
                htmltext = "alberryus_q0102_05.htm";
            else if (cond == 6 && st.getQuestItemsCount(ALBERRYUS_LIST) == 1) {
                st.takeItems(ALBERRYUS_LIST, 1);
                st.giveItems(ADENA_ID, 6331);
                st.player.addExpAndSp(30202, 1339);

                if (st.player.getClassId().isMage())
                    st.giveItems(STAFF_OF_SENTINEL);
                else
                    st.giveItems(SWORD_OF_SENTINEL);

                if (st.player.getClassId().occupation() == 0 && !st.player.isVarSet("p1q3")) {
                    st.player.setVar("p1q3"); // flag for helper
                    st.player.sendPacket(new ExShowScreenMessage("Now go find the Newbie Guide."));
                    st.giveItems(1060, 100); // healing potion
                    for (int item = 4412; item <= 4417; item++)
                        st.giveItems(item, 10); // echo cry
                    if (st.player.getClassId().isMage()) {
                        st.playTutorialVoice("tutorial_voice_027");
                        st.giveItems(5790, 3000); // newbie sps
                    } else {
                        st.playTutorialVoice("tutorial_voice_026");
                        st.giveItems(5789, 6000); // newbie ss
                    }
                }

                htmltext = "alberryus_q0102_06.htm";
                st.playSound(SOUND_FINISH);
                st.finish();
            }
        } else if (npcId == 30156) {
            if (cond == 1 && st.getQuestItemsCount(ALBERRYUS_LETTER) == 1) {
                st.takeItems(ALBERRYUS_LETTER, 1);
                st.giveItems(EVERGREEN_AMULET);
                st.setCond(2);
                htmltext = "cob_q0102_03.htm";
            } else if (cond == 2 && st.getQuestItemsCount(EVERGREEN_AMULET) > 0 && st.getQuestItemsCount(DRYAD_TEARS) < 10)
                htmltext = "cob_q0102_04.htm";
            else if (cond > 3 && st.getQuestItemsCount(ALBERRYUS_LIST) > 0)
                htmltext = "cob_q0102_07.htm";
            else if (cond == 3 && st.getQuestItemsCount(EVERGREEN_AMULET) > 0 && st.getQuestItemsCount(DRYAD_TEARS) >= 10) {
                st.takeItems(EVERGREEN_AMULET, 1);
                st.takeItems(DRYAD_TEARS);
                st.giveItems(COBS_MEDICINE1);
                st.giveItems(COBS_MEDICINE2);
                st.giveItems(COBS_MEDICINE3);
                st.giveItems(COBS_MEDICINE4);
                st.giveItems(COBS_MEDICINE5);
                st.setCond(4);
                htmltext = "cob_q0102_05.htm";
            } else if (cond == 4)
                htmltext = "cob_q0102_06.htm";
        } else if (npcId == 30217 && cond == 5 && st.getQuestItemsCount(ALBERRYUS_LIST) == 1 && st.getQuestItemsCount(COBS_MEDICINE2) == 1) {
            st.takeItems(COBS_MEDICINE2, 1);
            htmltext = "sentinel_berryos_q0102_01.htm";
            check(st);
        } else if (npcId == 30219 && cond == 5 && st.getQuestItemsCount(ALBERRYUS_LIST) == 1 && st.getQuestItemsCount(COBS_MEDICINE3) == 1) {
            st.takeItems(COBS_MEDICINE3, 1);
            htmltext = "sentinel_veltress_q0102_01.htm";
            check(st);
        } else if (npcId == 30221 && cond == 5 && st.getQuestItemsCount(ALBERRYUS_LIST) == 1 && st.getQuestItemsCount(COBS_MEDICINE4) == 1) {
            st.takeItems(COBS_MEDICINE4, 1);
            htmltext = "sentinel_rayjien_q0102_01.htm";
            check(st);
        } else if (npcId == 30285 && cond == 5 && st.getQuestItemsCount(ALBERRYUS_LIST) == 1 && st.getQuestItemsCount(COBS_MEDICINE5) == 1) {
            st.takeItems(COBS_MEDICINE5, 1);
            htmltext = "sentinel_gartrandell_q0102_01.htm";
            check(st);
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if ((npcId == 20013 || npcId == 20019) && Rnd.chance(33))
            if (st.getQuestItemsCount(EVERGREEN_AMULET) > 0 && st.getQuestItemsCount(DRYAD_TEARS) < 10) {
                st.giveItems(DRYAD_TEARS);
                if (st.getQuestItemsCount(DRYAD_TEARS) == 10) {
                    st.setCond(3);
                    st.playSound(SOUND_MIDDLE);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
    }
}
