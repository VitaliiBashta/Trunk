package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import static l2trunk.gameserver.model.base.ClassId.*;

public final class _215_TrialOfPilgrim extends Quest {
    private static final int MARK_OF_PILGRIM_ID = 2721;
    private static final int BOOK_OF_SAGE_ID = 2722;
    private static final int VOUCHER_OF_TRIAL_ID = 2723;
    private static final int SPIRIT_OF_FLAME_ID = 2724;
    private static final int ESSENSE_OF_FLAME_ID = 2725;
    private static final int BOOK_OF_GERALD_ID = 2726;
    private static final int GREY_BADGE_ID = 2727;
    private static final int PICTURE_OF_NAHIR_ID = 2728;
    private static final int HAIR_OF_NAHIR_ID = 2729;
    private static final int STATUE_OF_EINHASAD_ID = 2730;
    private static final int BOOK_OF_DARKNESS_ID = 2731;
    private static final int DEBRIS_OF_WILLOW_ID = 2732;
    private static final int TAG_OF_RUMOR_ID = 2733;
    private static final int ADENA_ID = 57;
    private static final int RewardExp = 629125;
    private static final int RewardSP = 40803;
    private static final int RewardAdena = 114649;

    public _215_TrialOfPilgrim() {
        addStartNpc(30648);

        addTalkId(30117, 30362, 30550, 30571, 30612, 30648, 30649, 30650, 30651, 30652);

        addKillId(27116, 27117, 27118);

        addQuestItem(BOOK_OF_SAGE_ID,
                VOUCHER_OF_TRIAL_ID,
                ESSENSE_OF_FLAME_ID,
                BOOK_OF_GERALD_ID,
                TAG_OF_RUMOR_ID,
                PICTURE_OF_NAHIR_ID,
                HAIR_OF_NAHIR_ID,
                BOOK_OF_DARKNESS_ID,
                DEBRIS_OF_WILLOW_ID,
                GREY_BADGE_ID,
                SPIRIT_OF_FLAME_ID,
                STATUE_OF_EINHASAD_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("1")) {
            htmltext = "hermit_santiago_q0215_04.htm";
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            st.giveItems(VOUCHER_OF_TRIAL_ID);
        }
        if (!st.player.isVarSet("dd1")) {
            st.giveItems(7562, 64);
            st.player.setVar("dd1");
        } else if ("30648_1".equals(event))
            htmltext = "hermit_santiago_q0215_05.htm";
        else if ("30648_2".equals(event))
            htmltext = "hermit_santiago_q0215_06.htm";
        else if ("30648_3".equals(event))
            htmltext = "hermit_santiago_q0215_07.htm";
        else if ("30648_4".equals(event))
            htmltext = "hermit_santiago_q0215_08.htm";
        else if ("30648_5".equals(event))
            htmltext = "hermit_santiago_q0215_05.htm";
        else if ("30649_1".equals(event)) {
            htmltext = "ancestor_martankus_q0215_04.htm";
            st.giveItems(SPIRIT_OF_FLAME_ID);
            st.takeItems(ESSENSE_OF_FLAME_ID, 1);
            st.setCond(5);
        } else if (event.equals("30650_1")) {
            if (st.getQuestItemsCount(ADENA_ID) >= 100000) {
                htmltext = "gerald_priest_of_earth_q0215_02.htm";
                st.giveItems(BOOK_OF_GERALD_ID);
                st.takeItems(ADENA_ID, 100000);
                st.setCond(7);
            } else
                htmltext = "gerald_priest_of_earth_q0215_03.htm";
        } else if (event.equals("30650_2"))
            htmltext = "gerald_priest_of_earth_q0215_03.htm";
        else if (event.equals("30362_1")) {
            htmltext = "andellria_q0215_05.htm";
            st.takeItems(BOOK_OF_DARKNESS_ID, 1);
            st.setCond(16);
        } else if (event.equals("30362_2")) {
            htmltext = "andellria_q0215_04.htm";
            st.setCond(16);
        } else if (event.equals("30652_1")) {
            htmltext = "uruha_q0215_02.htm";
            st.giveItems(BOOK_OF_DARKNESS_ID);
            st.takeItems(DEBRIS_OF_WILLOW_ID, 1);
            st.setCond(15);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        if (st.haveQuestItem(MARK_OF_PILGRIM_ID)) {
            st.exitCurrentQuest();
            return "completed";
        }
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        if (id == CREATED) {
            st.start();
            st.setCond(0);
            st.unset("id");
        }
        if (npcId == 30648 && cond == 0) {
            if (st.player.getClassId() == cleric
                    || st.player.getClassId() == oracle
                    || st.player.getClassId() == shillienOracle
                    || st.player.getClassId() == orcShaman) {
                if (st.player.getLevel() >= 35)
                    htmltext = "hermit_santiago_q0215_03.htm";
                else {
                    htmltext = "hermit_santiago_q0215_01.htm";
                    st.exitCurrentQuest();
                }
            } else {
                htmltext = "hermit_santiago_q0215_02.htm";
                st.exitCurrentQuest();
            }
        } else if (npcId == 30648 && cond == 1 && st.haveQuestItem(VOUCHER_OF_TRIAL_ID))
            htmltext = "hermit_santiago_q0215_09.htm";
        else if (npcId == 30648 && cond == 17 && st.haveQuestItem(BOOK_OF_SAGE_ID)) {
            htmltext = "hermit_santiago_q0215_10.htm";
            st.takeItems(BOOK_OF_SAGE_ID);
            st.giveItems(MARK_OF_PILGRIM_ID);
            if (!st.player.isVarSet("prof2.1")) {
                st.addExpAndSp(RewardExp, RewardSP);
                st.giveItems(ADENA_ID, RewardAdena);
                st.player.setVar("prof2.1");
            }
            st.playSound(SOUND_FINISH);
            st.unset("cond");
            st.finish();
        } else if (npcId == 30571 && cond == 1 && st.haveQuestItem(VOUCHER_OF_TRIAL_ID)) {
            htmltext = "seer_tanapi_q0215_01.htm";
            st.takeItems(VOUCHER_OF_TRIAL_ID);
            st.setCond(2);
        } else if (npcId == 30571 && cond == 2)
            htmltext = "seer_tanapi_q0215_02.htm";
        else if (npcId == 30571 && cond == 5 && st.haveQuestItem(SPIRIT_OF_FLAME_ID))
            htmltext = "seer_tanapi_q0215_03.htm";
        else if (npcId == 30649 && cond == 2) {
            htmltext = "ancestor_martankus_q0215_01.htm";
            st.setCond(3);
        } else if (npcId == 30649 && cond == 3)
            htmltext = "ancestor_martankus_q0215_02.htm";
        else if (npcId == 30649 && cond == 4 && st.haveQuestItem(ESSENSE_OF_FLAME_ID))
            htmltext = "ancestor_martankus_q0215_03.htm";
        else if (npcId == 30550 && cond == 5 && st.haveQuestItem(SPIRIT_OF_FLAME_ID)) {
            htmltext = "gauri_twinklerock_q0215_01.htm";
            st.giveItems(TAG_OF_RUMOR_ID);
            st.setCond(6);
        } else if (npcId == 30550 && cond == 6)
            htmltext = "gauri_twinklerock_q0215_02.htm";
        else if (npcId == 30650 && cond == 6 && st.haveQuestItem(TAG_OF_RUMOR_ID))
            htmltext = "gerald_priest_of_earth_q0215_01.htm";
        else if (npcId == 30650 && cond >= 8 && st.haveAllQuestItems(GREY_BADGE_ID, BOOK_OF_GERALD_ID)) {
            htmltext = "gerald_priest_of_earth_q0215_04.htm";
            st.giveAdena(100000);
            st.takeItems(BOOK_OF_GERALD_ID);
        } else if (npcId == 30651 && cond == 6 && st.haveQuestItem(TAG_OF_RUMOR_ID)) {
            htmltext = "wanderer_dorf_q0215_01.htm";
            st.giveItems(GREY_BADGE_ID);
            st.takeItems(TAG_OF_RUMOR_ID);
            st.setCond(8);
        } else if (npcId == 30651 && cond == 7 && st.haveQuestItem(TAG_OF_RUMOR_ID)) {
            htmltext = "wanderer_dorf_q0215_02.htm";
            st.giveItems(GREY_BADGE_ID);
            st.takeItems(TAG_OF_RUMOR_ID);
            st.setCond(8);
        } else if (npcId == 30651 && cond == 8)
            htmltext = "wanderer_dorf_q0215_03.htm";
        else if (npcId == 30117 && cond == 8) {
            htmltext = "primoz_q0215_01.htm";
            st.setCond(9);
        } else if (npcId == 30117 && cond == 9)
            htmltext = "primoz_q0215_02.htm";
        else if (npcId == 30036 && cond == 9) {
            htmltext = "potter_q0215_01.htm";
            st.giveItems(PICTURE_OF_NAHIR_ID);
            st.setCond(10);
        } else if (npcId == 30036 && cond == 10)
            htmltext = "potter_q0215_02.htm";
        else if (npcId == 30036 && cond == 11) {
            htmltext = "potter_q0215_03.htm";
            st.giveItems(STATUE_OF_EINHASAD_ID);
            st.takeAllItems(PICTURE_OF_NAHIR_ID, HAIR_OF_NAHIR_ID);
            st.setCond(12);
        } else if (npcId == 30036 && cond == 12 && st.haveQuestItem(STATUE_OF_EINHASAD_ID))
            htmltext = "potter_q0215_04.htm";
        else if (npcId == 30362 && cond == 12) {
            htmltext = "andellria_q0215_01.htm";
            st.setCond(13);
        } else if (npcId == 30362 && cond == 13)
            htmltext = "andellria_q0215_02.htm";
        else if (npcId == 30362 && cond == 15 && st.haveQuestItem(BOOK_OF_DARKNESS_ID))
            htmltext = "andellria_q0215_03.htm";
        else if (npcId == 30362 && cond == 16)
            htmltext = "andellria_q0215_06.htm";
        else if (npcId == 30362 && cond == 15 && !st.haveQuestItem(BOOK_OF_DARKNESS_ID))
            htmltext = "andellria_q0215_07.htm";
        else if (npcId == 30652 && cond == 14 && st.haveQuestItem(DEBRIS_OF_WILLOW_ID))
            htmltext = "uruha_q0215_01.htm";
        else if (npcId == 30652 && cond == 15 && st.haveQuestItem(BOOK_OF_DARKNESS_ID))
            htmltext = "uruha_q0215_03.htm";
        else if (npcId == 30612 && cond == 16) {
            htmltext = "sage_kasian_q0215_01.htm";
            st.giveItems(BOOK_OF_SAGE_ID);

            st.setCond(17);
            st.takeAllItems(BOOK_OF_DARKNESS_ID,BOOK_OF_GERALD_ID,GREY_BADGE_ID,SPIRIT_OF_FLAME_ID, STATUE_OF_EINHASAD_ID);
        } else if (npcId == 30612 && cond == 17)
            htmltext = "sage_kasian_q0215_02.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == 27116) {
            if (st.getCond() == 3)
                if (Rnd.chance(30)) {
                    st.giveItemIfNotHave(ESSENSE_OF_FLAME_ID);
                    st.setCond(4);
                    st.playSound(SOUND_MIDDLE);
                }
        } else if (npcId == 27117) {
            if (st.getCond() == 10) {
                st.giveItemIfNotHave(HAIR_OF_NAHIR_ID);
                st.setCond(11);
                st.playSound(SOUND_MIDDLE);
            }
        } else if (npcId == 27118)
            if (st.getCond() == 13)
                if (Rnd.chance(20)) {
                    st.giveItemIfNotHave(DEBRIS_OF_WILLOW_ID);
                    st.setCond(14);
                    st.playSound(SOUND_MIDDLE);
                }
    }
}