package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.stream.IntStream;

public final class _212_TrialOfDuty extends Quest {
    private static final int MARK_OF_DUTY_ID = 2633;
    private static final int LETTER_OF_DUSTIN_ID = 2634;
    private static final int KNIGHTS_TEAR_ID = 2635;
    private static final int MIRROR_OF_ORPIC_ID = 2636;
    private static final int TEAR_OF_CONFESSION_ID = 2637;
    private static final int REPORT_PIECE_ID = 2638;
    private static final int TALIANUSS_REPORT_ID = 2639;
    private static final int TEAR_OF_LOYALTY_ID = 2640;
    private static final int MILITAS_ARTICLE_ID = 2641;
    private static final int SAINTS_ASHES_URN_ID = 2642;
    private static final int ATEBALTS_SKULL_ID = 2643;
    private static final int ATEBALTS_RIBS_ID = 2644;
    private static final int ATEBALTS_SHIN_ID = 2645;
    private static final int LETTER_OF_WINDAWOOD_ID = 2646;
    private static final int OLD_KNIGHT_SWORD_ID = 3027;
    private static final int RewardExp = 381288;
    private static final int RewardSP = 24729;
    private static final int RewardAdena = 69484;

    public _212_TrialOfDuty() {
        addStartNpc(30109);

        addTalkId(30116, 30311, 30653, 30654, 30655, 30656);

        addKillId(20144, 20190, 20191, 20200, 20201, 20270, 27119);
        addKillId(IntStream.rangeClosed(20577, 50582).toArray());

        addQuestItem(LETTER_OF_DUSTIN_ID,
                KNIGHTS_TEAR_ID,
                OLD_KNIGHT_SWORD_ID,
                TEAR_OF_CONFESSION_ID,
                MIRROR_OF_ORPIC_ID,
                TALIANUSS_REPORT_ID,
                MILITAS_ARTICLE_ID,
                ATEBALTS_SKULL_ID,
                ATEBALTS_RIBS_ID,
                ATEBALTS_SHIN_ID,
                LETTER_OF_WINDAWOOD_ID,
                TEAR_OF_LOYALTY_ID,
                SAINTS_ASHES_URN_ID,
                REPORT_PIECE_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        switch (event) {
            case "1":
                st.start();
                st.playSound(SOUND_ACCEPT);
                st.setCond(1);
                if (!st.player.isVarSet("dd1")) {
                    st.giveItems(7562, 64);
                    st.player.setVar("dd1");
                }
                return "hannavalt_q0212_04.htm";
            case "30116_1":
                return "dustin_q0212_02.htm";
            case "30116_2":
                return "dustin_q0212_03.htm";
            case "30116_3":
                return "dustin_q0212_04.htm";
            case "30116_4":
                st.takeItems(TEAR_OF_LOYALTY_ID);
                st.setCond(14);
                return "dustin_q0212_05.htm";
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (st.haveQuestItem(MARK_OF_DUTY_ID)) {
            st.exitCurrentQuest();
            return "completed";
        }
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();
        if (npcId == 30109 && id == CREATED) {
            if (st.player.getClassId() == ClassId.knight
                    || st.player.getClassId() == ClassId.elvenKnight
                    || st.player.getClassId() == ClassId.palusKnight)
                if (st.player.getLevel() >= 35)
                    htmltext = "hannavalt_q0212_03.htm";
                else {
                    htmltext = "hannavalt_q0212_01.htm";
                    st.exitCurrentQuest();
                }
            else {
                htmltext = "hannavalt_q0212_02.htm";
                st.exitCurrentQuest();
            }
        } else if (npcId == 30109) {
            if (cond == 18 && st.haveQuestItem(LETTER_OF_DUSTIN_ID)) {
                htmltext = "hannavalt_q0212_05.htm";
                st.takeItems(LETTER_OF_DUSTIN_ID);
                st.giveItems(MARK_OF_DUTY_ID);
                if (!st.player.isVarSet("prof2.1")) {
                    st.addExpAndSp(RewardExp, RewardSP);
                    st.giveAdena(RewardAdena);
                    st.player.setVar("prof2.1");
                }
                st.playSound(SOUND_FINISH);
                st.finish();
            } else if (cond == 1)
                htmltext = "hannavalt_q0212_04.htm";
        } else if (npcId == 30653) {
            if (cond == 1) {
                htmltext = "sir_aron_tanford_q0212_01.htm";
                st.giveItemIfNotHave(OLD_KNIGHT_SWORD_ID);
                st.setCond(2);
            } else if (cond == 2 && !st.haveQuestItem(KNIGHTS_TEAR_ID))
                htmltext = "sir_aron_tanford_q0212_02.htm";
            else if (cond == 3 && st.haveQuestItem(KNIGHTS_TEAR_ID)) {
                htmltext = "sir_aron_tanford_q0212_03.htm";
                st.takeItems(KNIGHTS_TEAR_ID, 1);
                st.takeItems(OLD_KNIGHT_SWORD_ID, 1);
                st.setCond(4);
            } else if (cond == 4)
                htmltext = "sir_aron_tanford_q0212_04.htm";
        } else if (npcId == 30654) {
            if (cond == 4) {
                htmltext = "sir_kiel_nighthawk_q0212_01.htm";
                st.setCond(5);
            } else if (cond == 5 && st.getQuestItemsCount(TALIANUSS_REPORT_ID) == 0)
                htmltext = "sir_kiel_nighthawk_q0212_02.htm";
            else if (cond == 6 && st.haveQuestItem(TALIANUSS_REPORT_ID)) {
                htmltext = "sir_kiel_nighthawk_q0212_03.htm";
                st.setCond(7);
                st.giveItems(MIRROR_OF_ORPIC_ID);
            } else if (cond == 6 && st.haveQuestItem(MIRROR_OF_ORPIC_ID))
                htmltext = "sir_kiel_nighthawk_q0212_04.htm";
            else if (st.haveQuestItem(TEAR_OF_CONFESSION_ID)) {
                htmltext = "sir_kiel_nighthawk_q0212_05.htm";
                st.takeItems(TEAR_OF_CONFESSION_ID);
                st.setCond(10);
            } else if (cond == 10)
                htmltext = "sir_kiel_nighthawk_q0212_06.htm";
        } else if (npcId == 30656 && cond == 8 && st.haveQuestItem(MIRROR_OF_ORPIC_ID)) {
            htmltext = "spirit_of_sir_talianus_q0212_01.htm";
            st.takeAllItems(MIRROR_OF_ORPIC_ID, TALIANUSS_REPORT_ID);
            st.giveItems(TEAR_OF_CONFESSION_ID);
            st.setCond(9);
        } else if (npcId == 30655) {
            if (cond == 10)
                if (st.player.getLevel() >= 36) {
                    htmltext = "isael_silvershadow_q0212_02.htm";
                    st.setCond(11);
                } else
                    htmltext = "isael_silvershadow_q0212_01.htm";
            else if (cond == 11)
                htmltext = "isael_silvershadow_q0212_03.htm";
            else if (cond == 12 && st.getQuestItemsCount(MILITAS_ARTICLE_ID) >= 20) {
                htmltext = "isael_silvershadow_q0212_04.htm";
                st.takeItems(MILITAS_ARTICLE_ID);
                st.giveItems(TEAR_OF_LOYALTY_ID);
                st.setCond(13);
            } else if (cond == 13)
                htmltext = "isael_silvershadow_q0212_05.htm";
        } else if (npcId == 30116) {
            if (cond == 13 && st.haveQuestItem(TEAR_OF_LOYALTY_ID))
                htmltext = "dustin_q0212_01.htm";
            else if (cond == 14 && !(st.haveAllQuestItems(ATEBALTS_SKULL_ID, ATEBALTS_RIBS_ID, ATEBALTS_SHIN_ID)))
                htmltext = "dustin_q0212_06.htm";
            else if (cond == 15) {
                htmltext = "dustin_q0212_07.htm";
                st.takeAllItems(ATEBALTS_SKULL_ID, ATEBALTS_RIBS_ID, ATEBALTS_SHIN_ID);
                st.giveItems(SAINTS_ASHES_URN_ID);
                st.setCond(16);
            } else if (cond == 17 && st.haveQuestItem(LETTER_OF_WINDAWOOD_ID)) {
                htmltext = "dustin_q0212_08.htm";
                st.takeItems(LETTER_OF_WINDAWOOD_ID);
                st.giveItems(LETTER_OF_DUSTIN_ID);
                st.setCond(18);
            } else if (cond == 16)
                htmltext = "dustin_q0212_09.htm";
            else if (cond == 18)
                htmltext = "dustin_q0212_10.htm";
        } else if (npcId == 30311)
            if (cond == 16 && st.haveQuestItem(SAINTS_ASHES_URN_ID)) {
                htmltext = "sir_collin_windawood_q0212_01.htm";
                st.takeItems(SAINTS_ASHES_URN_ID);
                st.giveItems(LETTER_OF_WINDAWOOD_ID);
                st.setCond(17);
            } else if (cond == 17)
                htmltext = "sir_collin_windawood_q0212_02.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == 20190 || npcId == 20191) {
            if (cond == 2)
                if (Rnd.chance(10)) {
                    st.addSpawn(27119);
                    st.playSound(SOUND_BEFORE_BATTLE);
                }
        } else if (npcId == 27119 && cond == 2 && st.haveQuestItem(OLD_KNIGHT_SWORD_ID)) {
            st.giveItems(KNIGHTS_TEAR_ID);
            st.playSound(SOUND_MIDDLE);
            st.setCond(3);
        } else if ((npcId == 20200 || npcId == 20201) && cond == 5 && st.getQuestItemsCount(TALIANUSS_REPORT_ID) == 0) {
            if (Rnd.chance(50)) {
                st.giveItems(REPORT_PIECE_ID);
                st.playSound(SOUND_ITEMGET);
            }
            if (st.getQuestItemsCount(REPORT_PIECE_ID) >= 10) {
                st.takeItems(REPORT_PIECE_ID);
                st.giveItems(TALIANUSS_REPORT_ID);
                st.setCond(6);
                st.playSound(SOUND_MIDDLE);
            }
        } else if (npcId == 20144 && cond == 7 && Rnd.chance(20)) {
            st.addSpawn(30656, npc.getLoc(), 0, 300000);
            st.setCond(8);
            st.playSound(SOUND_MIDDLE);
        } else if (npcId >= 20577 && npcId <= 20582 && cond == 11 && st.getQuestItemsCount(MILITAS_ARTICLE_ID) < 20) {
            if (st.getQuestItemsCount(MILITAS_ARTICLE_ID) == 19) {
                st.giveItems(MILITAS_ARTICLE_ID);
                st.setCond(12);
                st.playSound(SOUND_MIDDLE);
            } else {
                st.giveItems(MILITAS_ARTICLE_ID);
                st.playSound(SOUND_ITEMGET);
            }
        } else if (npcId == 20270 && cond == 14 && Rnd.chance(50))
            if (st.getQuestItemsCount(ATEBALTS_SKULL_ID) == 0) {
                st.giveItems(ATEBALTS_SKULL_ID);
            } else if (st.getQuestItemsCount(ATEBALTS_RIBS_ID) == 0) {
                st.giveItemIfNotHave(ATEBALTS_RIBS_ID);
            } else if (st.getQuestItemsCount(ATEBALTS_SHIN_ID) == 0) {
                st.giveItemIfNotHave(ATEBALTS_SHIN_ID);
                st.setCond(15);
            }
    }
}