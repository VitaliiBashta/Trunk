package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _217_TestimonyOfTrust extends Quest {
    private static final int MARK_OF_TRUST_ID = 2734;
    private static final int LETTER_TO_ELF_ID = 1558;
    private static final int LETTER_TO_DARKELF_ID = 1556;
    private static final int LETTER_TO_DWARF_ID = 2737;
    private static final int LETTER_TO_ORC_ID = 2738;
    private static final int LETTER_TO_SERESIN_ID = 2739;
    private static final int SCROLL_OF_DARKELF_TRUST_ID = 2740;
    private static final int SCROLL_OF_ELF_TRUST_ID = 2741;
    private static final int SCROLL_OF_DWARF_TRUST_ID = 2742;
    private static final int SCROLL_OF_ORC_TRUST_ID = 2743;
    private static final int RECOMMENDATION_OF_HOLLIN_ID = 2744;
    private static final int ORDER_OF_OZZY_ID = 2745;
    private static final int BREATH_OF_WINDS_ID = 2746;
    private static final int SEED_OF_VERDURE_ID = 2747;
    private static final int LETTER_OF_THIFIELL_ID = 2748;
    private static final int BLOOD_OF_GUARDIAN_BASILISK_ID = 2749;
    private static final int GIANT_APHID_ID = 2750;
    private static final int STAKATOS_FLUIDS_ID = 2751;
    private static final int BASILISK_PLASMA_ID = 2752;
    private static final int HONEY_DEW_ID = 2753;
    private static final int STAKATO_ICHOR_ID = 2754;
    private static final int ORDER_OF_CLAYTON_ID = 2755;
    private static final int PARASITE_OF_LOTA_ID = 2756;
    private static final int LETTER_TO_MANAKIA_ID = 2757;
    private static final int LETTER_OF_MANAKIA_ID = 2758;
    private static final int LETTER_TO_NICHOLA_ID = 2759;
    private static final int ORDER_OF_NICHOLA_ID = 2760;
    private static final int HEART_OF_PORTA_ID = 2761;
    private static final int RewardAdena = 126106;

    public _217_TestimonyOfTrust() {
        super(false);

        addStartNpc(30191);

        addTalkId(30031, 30154, 30358, 30464, 30515, 30531, 30565, 30621, 30657);

        addKillId(20013, 20157, 20019, 20213, 20230, 20232, 20234, 20036, 20044, 27120,
                27121, 20550, 20553, 20082, 20084, 20086, 20087, 20088);

        addQuestItem(SCROLL_OF_DARKELF_TRUST_ID,
                SCROLL_OF_ELF_TRUST_ID,
                SCROLL_OF_DWARF_TRUST_ID,
                SCROLL_OF_ORC_TRUST_ID,
                BREATH_OF_WINDS_ID,
                SEED_OF_VERDURE_ID,
                ORDER_OF_OZZY_ID,
                LETTER_TO_ELF_ID,
                ORDER_OF_CLAYTON_ID,
                BASILISK_PLASMA_ID,
                STAKATO_ICHOR_ID,
                HONEY_DEW_ID,
                LETTER_TO_DARKELF_ID,
                LETTER_OF_THIFIELL_ID,
                LETTER_TO_SERESIN_ID,
                LETTER_TO_ORC_ID,
                LETTER_OF_MANAKIA_ID,
                LETTER_TO_MANAKIA_ID,
                PARASITE_OF_LOTA_ID,
                LETTER_TO_DWARF_ID,
                LETTER_TO_NICHOLA_ID,
                HEART_OF_PORTA_ID,
                ORDER_OF_NICHOLA_ID,
                RECOMMENDATION_OF_HOLLIN_ID,
                BLOOD_OF_GUARDIAN_BASILISK_ID,
                STAKATOS_FLUIDS_ID,
                GIANT_APHID_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "1":
                if (!st.player.isVarSet("dd2")) {
                    st.giveItems(7562, 96);
                    st.player.setVar("dd2");
                }
                htmltext = "hollin_q0217_04.htm";
                st.setCond(1);
                st.set("id", 0);
                st.start();
                st.playSound(SOUND_ACCEPT);
                st.giveItems(LETTER_TO_ELF_ID);
                st.giveItems(LETTER_TO_DARKELF_ID);
                break;
            case "30154_1":
                htmltext = "ozzy_q0217_02.htm";
                break;
            case "30154_2":
                htmltext = "ozzy_q0217_03.htm";
                st.takeItems(LETTER_TO_ELF_ID, 1);
                st.giveItems(ORDER_OF_OZZY_ID);
                st.setCond(2);
                break;
            case "30358_1":
                htmltext = "tetrarch_thifiell_q0217_02.htm";
                st.takeItems(LETTER_TO_DARKELF_ID, 1);
                st.giveItems(LETTER_OF_THIFIELL_ID);
                st.setCond(5);
                break;
            case "30657_1":
                if (st.player.getLevel() >= 38) {
                    htmltext = "cardinal_seresin_q0217_03.htm";
                    st.takeItems(LETTER_TO_SERESIN_ID, 1);
                    st.giveItems(LETTER_TO_ORC_ID);
                    st.giveItems(LETTER_TO_DWARF_ID);
                    st.setCond(12);
                } else
                    htmltext = "cardinal_seresin_q0217_02.htm";
                break;
            case "30565_1":
                htmltext = "kakai_the_lord_of_flame_q0217_02.htm";
                st.takeItems(LETTER_TO_ORC_ID, 1);
                st.giveItems(LETTER_TO_MANAKIA_ID, 1);
                st.setCond(13);
                break;
            case "30515_1":
                htmltext = "seer_manakia_q0217_02.htm";
                st.takeItems(LETTER_TO_MANAKIA_ID, 1);
                st.setCond(14);
                break;
            case "30531_1":
                htmltext = "first_elder_lockirin_q0217_02.htm";
                st.takeItems(LETTER_TO_DWARF_ID, 1);
                st.giveItems(LETTER_TO_NICHOLA_ID, 1);
                st.setCond(18);
                break;
            case "30621_1":
                htmltext = "maestro_nikola_q0217_02.htm";
                st.takeItems(LETTER_TO_NICHOLA_ID, 1);
                st.giveItems(ORDER_OF_NICHOLA_ID, 1);
                st.setCond(19);
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (st.getQuestItemsCount(MARK_OF_TRUST_ID) > 0) {
            st.exitCurrentQuest();
            return "completed";
        }
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == 30191) {
            if (cond == 0) {
                if (st.player.getRace() == Race.human)
                    if (st.player.getLevel() >= 37)
                        htmltext = "hollin_q0217_03.htm";
                    else {
                        htmltext = "hollin_q0217_01.htm";
                        st.exitCurrentQuest();
                    }
                else {
                    htmltext = "hollin_q0217_02.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 9 && st.haveAllQuestItems(SCROLL_OF_ELF_TRUST_ID, SCROLL_OF_DARKELF_TRUST_ID)) {
                htmltext = "hollin_q0217_05.htm";
                st.takeAllItems(SCROLL_OF_DARKELF_TRUST_ID, SCROLL_OF_ELF_TRUST_ID);
                st.giveItems(LETTER_TO_SERESIN_ID);
                st.setCond(10);
            } else if (cond == 22 && st.haveAllQuestItems(SCROLL_OF_DWARF_TRUST_ID, SCROLL_OF_ORC_TRUST_ID)) {
                htmltext = "hollin_q0217_06.htm";
                st.takeAllItems(SCROLL_OF_DWARF_TRUST_ID, SCROLL_OF_ORC_TRUST_ID);
                st.giveItems(RECOMMENDATION_OF_HOLLIN_ID);
                st.setCond(23);
            } else if (cond == 19)
                htmltext = "hollin_q0217_07.htm";
            else if (cond == 1)
                htmltext = "hollin_q0217_08.htm";
            else if (cond == 8)
                htmltext = "hollin_q0217_09.htm";
        } else if (npcId == 30154) {
            if (cond == 1 && st.getQuestItemsCount(LETTER_TO_ELF_ID) > 0)
                htmltext = "ozzy_q0217_01.htm";
            else if (cond == 2 && st.getQuestItemsCount(ORDER_OF_OZZY_ID) > 0)
                htmltext = "ozzy_q0217_04.htm";
            else if (cond == 3 && st.haveAllQuestItems(BREATH_OF_WINDS_ID, SEED_OF_VERDURE_ID)) {
                htmltext = "ozzy_q0217_05.htm";
                st.takeAllItems(BREATH_OF_WINDS_ID, SEED_OF_VERDURE_ID, ORDER_OF_OZZY_ID);
                st.giveItems(SCROLL_OF_ELF_TRUST_ID);
                st.setCond(4);
            } else if (cond == 4)
                htmltext = "ozzy_q0217_06.htm";
        } else if (npcId == 30358) {
            if (cond == 4 && st.getQuestItemsCount(LETTER_TO_DARKELF_ID) > 0)
                htmltext = "tetrarch_thifiell_q0217_01.htm";
            else if (cond == 8 && st.haveAllQuestItems(STAKATO_ICHOR_ID, HONEY_DEW_ID, BASILISK_PLASMA_ID)) {
                st.takeAllItems(BASILISK_PLASMA_ID, STAKATO_ICHOR_ID, HONEY_DEW_ID);
                st.giveItems(SCROLL_OF_DARKELF_TRUST_ID);
                st.setCond(9);
                htmltext = "tetrarch_thifiell_q0217_03.htm";
            } else if (cond == 7)
                htmltext = "tetrarch_thifiell_q0217_04.htm";
            else if (cond == 5)
                htmltext = "tetrarch_thifiell_q0217_05.htm";
        } else if (npcId == 30464) {
            if (cond == 5 && st.haveQuestItem(LETTER_OF_THIFIELL_ID)) {
                htmltext = "magister_clayton_q0217_01.htm";
                st.takeItems(LETTER_OF_THIFIELL_ID, 1);
                st.giveItems(ORDER_OF_CLAYTON_ID);
                st.setCond(6);
            } else if (cond == 6 && !st.haveAllQuestItems(ORDER_OF_CLAYTON_ID, STAKATO_ICHOR_ID, HONEY_DEW_ID, BASILISK_PLASMA_ID))
                htmltext = "magister_clayton_q0217_02.htm";
            else if (cond == 7 && st.haveAllQuestItems(ORDER_OF_CLAYTON_ID, STAKATO_ICHOR_ID, HONEY_DEW_ID, BASILISK_PLASMA_ID)) {
                st.takeItems(ORDER_OF_CLAYTON_ID, 1);
                st.setCond(8);
                htmltext = "magister_clayton_q0217_03.htm";
            }
        } else if (npcId == 30657) {
            if ((cond == 10 || cond == 11) && st.haveQuestItem(LETTER_TO_SERESIN_ID) && st.player.getLevel() >= 38)
                htmltext = "cardinal_seresin_q0217_01.htm";
            else if ((cond == 10 || cond == 11) && st.player.getLevel() < 38) {
                htmltext = "cardinal_seresin_q0217_02.htm";
                if (cond == 10)
                    st.setCond(11);
            } else if (cond == 18)
                htmltext = "cardinal_seresin_q0217_05.htm";
        } else if (npcId == 30565) {
            if (cond == 12 && st.haveQuestItem(LETTER_TO_ORC_ID))
                htmltext = "kakai_the_lord_of_flame_q0217_01.htm";
            else if (cond == 13)
                htmltext = "kakai_the_lord_of_flame_q0217_03.htm";
            else if (cond == 16) {
                htmltext = "kakai_the_lord_of_flame_q0217_04.htm";
                st.takeItems(LETTER_OF_MANAKIA_ID, 1);
                st.giveItems(SCROLL_OF_ORC_TRUST_ID);
                st.setCond(17);
            } else if (cond >= 17)
                htmltext = "kakai_the_lord_of_flame_q0217_05.htm";
        } else if (npcId == 30515) {
            if (cond == 13 && st.haveQuestItem(LETTER_TO_MANAKIA_ID))
                htmltext = "seer_manakia_q0217_01.htm";
            else if (cond == 14 && st.getQuestItemsCount(PARASITE_OF_LOTA_ID) < 10)
                htmltext = "seer_manakia_q0217_03.htm";
            else if (cond == 15 && st.getQuestItemsCount(PARASITE_OF_LOTA_ID) == 10) {
                htmltext = "seer_manakia_q0217_04.htm";
                st.takeItems(PARASITE_OF_LOTA_ID);
                st.giveItems(LETTER_OF_MANAKIA_ID);
                st.setCond(16);
            } else if (cond == 16)
                htmltext = "seer_manakia_q0217_05.htm";
        } else if (npcId == 30531) {
            if (cond == 17 && st.haveQuestItem(LETTER_TO_DWARF_ID))
                htmltext = "first_elder_lockirin_q0217_01.htm";
            else if (cond == 18)
                htmltext = "first_elder_lockirin_q0217_03.htm";
            else if (cond == 21) {
                htmltext = "first_elder_lockirin_q0217_04.htm";
                st.giveItems(SCROLL_OF_DWARF_TRUST_ID, 1);
                st.setCond(22);
            } else if (cond == 22)
                htmltext = "first_elder_lockirin_q0217_05.htm";
        } else if (npcId == 30621) {
            if (cond == 18 && st.getQuestItemsCount(LETTER_TO_NICHOLA_ID) > 0)
                htmltext = "maestro_nikola_q0217_01.htm";
            else if (cond == 19 && st.getQuestItemsCount(HEART_OF_PORTA_ID) < 1)
                htmltext = "maestro_nikola_q0217_03.htm";
            else if (cond == 20 && st.getQuestItemsCount(HEART_OF_PORTA_ID) >= 1) {
                htmltext = "maestro_nikola_q0217_04.htm";
                st.takeItems(HEART_OF_PORTA_ID);
                st.takeItems(ORDER_OF_NICHOLA_ID);
                st.setCond(21);
            } else if (cond == 21)
                htmltext = "maestro_nikola_q0217_05.htm";
        } else if (npcId == 30031 && cond == 23 && st.haveQuestItem(RECOMMENDATION_OF_HOLLIN_ID)) {
            htmltext = "quilt_q0217_01.htm";
            st.takeItems(RECOMMENDATION_OF_HOLLIN_ID);
            st.giveItems(MARK_OF_TRUST_ID);
            if (!st.player.isVarSet("prof2.2")) {
                st.addExpAndSp(695149, 46391);
                st.giveItems(ADENA_ID, RewardAdena);
                st.player.setVar("prof2.2");
            }
            st.playSound(SOUND_FINISH);
            st.unset("cond");
            st.exitCurrentQuest();
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == 20036 || npcId == 20044) {
            if (cond == 2 && !st.haveQuestItem(BREATH_OF_WINDS_ID)) {
                st.inc("id");
                if (Rnd.chance(st.getInt("id") * 33)) {
                    st.addSpawn(27120);
                    st.playSound(SOUND_BEFORE_BATTLE);
                }
            }
        } else if (npcId == 20013 || npcId == 20019) {
            if (cond == 2 && !st.haveQuestItem(SEED_OF_VERDURE_ID)) {
                st.inc("id");
                if (Rnd.chance(st.getInt("id") * 33)) {
                    st.addSpawn(27121);
                    st.playSound(SOUND_BEFORE_BATTLE);
                }
            }
        } else if (npcId == 27120) {
            if (cond == 2 && !st.haveQuestItem(BREATH_OF_WINDS_ID))
                if (st.haveQuestItem(SEED_OF_VERDURE_ID)) {
                    st.giveItems(BREATH_OF_WINDS_ID);
                    st.setCond(3);
                    st.playSound(SOUND_MIDDLE);
                } else {
                    st.giveItems(BREATH_OF_WINDS_ID);
                    st.playSound(SOUND_ITEMGET);
                }
        } else if (npcId == 27121) {
            if (cond == 2 && !st.haveQuestItem(SEED_OF_VERDURE_ID))
                if (st.haveQuestItem(BREATH_OF_WINDS_ID)) {
                    st.giveItems(SEED_OF_VERDURE_ID);
                    st.setCond(3);
                    st.playSound(SOUND_MIDDLE);
                } else {
                    st.giveItems(SEED_OF_VERDURE_ID);
                    st.playSound(SOUND_ITEMGET);
                }
        } else if (npcId == 20550) {
            if (cond == 6 && st.getQuestItemsCount(BLOOD_OF_GUARDIAN_BASILISK_ID) < 10 && st.getQuestItemsCount(ORDER_OF_CLAYTON_ID) > 0 && st.getQuestItemsCount(BASILISK_PLASMA_ID) == 0)
                if (st.getQuestItemsCount(BLOOD_OF_GUARDIAN_BASILISK_ID) == 9) {
                    st.takeItems(BLOOD_OF_GUARDIAN_BASILISK_ID);
                    st.giveItems(BASILISK_PLASMA_ID);
                    if (st.getQuestItemsCount(STAKATO_ICHOR_ID) + st.getQuestItemsCount(BASILISK_PLASMA_ID) + st.getQuestItemsCount(HONEY_DEW_ID) == 3)
                        st.setCond(7);
                    st.playSound(SOUND_MIDDLE);
                } else {
                    st.giveItems(BLOOD_OF_GUARDIAN_BASILISK_ID);
                    st.playSound(SOUND_ITEMGET);
                }
        } else if (npcId == 20157 || npcId == 20230 || npcId == 20232 || npcId == 20234) {
            if (cond == 6 && st.getQuestItemsCount(STAKATOS_FLUIDS_ID) < 10 && st.getQuestItemsCount(ORDER_OF_CLAYTON_ID) > 0 && st.getQuestItemsCount(STAKATO_ICHOR_ID) == 0)
                if (st.getQuestItemsCount(STAKATOS_FLUIDS_ID) == 9) {
                    st.takeItems(STAKATOS_FLUIDS_ID);
                    st.giveItems(STAKATO_ICHOR_ID);
                    if (st.getQuestItemsCount(STAKATO_ICHOR_ID) + st.getQuestItemsCount(BASILISK_PLASMA_ID) + st.getQuestItemsCount(HONEY_DEW_ID) == 3)
                        st.setCond(7);
                    st.playSound(SOUND_MIDDLE);
                } else {
                    st.giveItems(STAKATOS_FLUIDS_ID, 1);
                    st.playSound(SOUND_ITEMGET);
                }
        } else if (npcId == 20082 || npcId == 20086 || npcId == 20087 || npcId == 20084 || npcId == 20088) {
            if (cond == 6 && st.getQuestItemsCount(GIANT_APHID_ID) < 10 && st.getQuestItemsCount(ORDER_OF_CLAYTON_ID) > 0 && st.getQuestItemsCount(HONEY_DEW_ID) == 0)
                if (st.getQuestItemsCount(GIANT_APHID_ID) == 9) {
                    st.takeItems(GIANT_APHID_ID);
                    st.giveItems(HONEY_DEW_ID, 1);
                    if (st.getQuestItemsCount(STAKATO_ICHOR_ID) + st.getQuestItemsCount(BASILISK_PLASMA_ID) + st.getQuestItemsCount(HONEY_DEW_ID) == 3)
                        st.setCond(7);
                    st.playSound(SOUND_MIDDLE);
                } else {
                    st.giveItems(GIANT_APHID_ID);
                    st.playSound(SOUND_ITEMGET);
                }
        } else if (npcId == 20553) {
            if (cond == 14 && st.getQuestItemsCount(PARASITE_OF_LOTA_ID) < 10 && Rnd.chance(50))
                if (st.getQuestItemsCount(PARASITE_OF_LOTA_ID) == 9) {
                    st.giveItems(PARASITE_OF_LOTA_ID);
                    st.setCond(15);
                    st.playSound(SOUND_MIDDLE);
                } else {
                    st.giveItems(PARASITE_OF_LOTA_ID);
                    st.playSound(SOUND_ITEMGET);
                }
        } else if (npcId == 20213 && cond == 19 && st.getQuestItemsCount(HEART_OF_PORTA_ID) < 1) {
            st.giveItems(HEART_OF_PORTA_ID);
            st.setCond(20);
            st.playSound(SOUND_MIDDLE);
        }
    }
}