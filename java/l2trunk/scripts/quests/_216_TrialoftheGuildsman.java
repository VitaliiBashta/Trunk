package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import static l2trunk.gameserver.model.base.ClassId.artisan;
import static l2trunk.gameserver.model.base.ClassId.scavenger;

public final class _216_TrialoftheGuildsman extends Quest {
    //NPC
    private static final int VALKON = 30103;
    private static final int NORMAN = 30210;
    private static final int ALTRAN = 30283;
    private static final int PINTER = 30298;
    private static final int DUNING = 30688;
    //Quest Item
    private static final int MARK_OF_GUILDSMAN = 3119;
    private static final int VALKONS_RECOMMEND = 3120;
    private static final int MANDRAGORA_BERRY = 3121;
    private static final int ALLTRANS_INSTRUCTIONS = 3122;
    private static final int ALLTRANS_RECOMMEND1 = 3123;
    private static final int ALLTRANS_RECOMMEND2 = 3124;
    private static final int NORMANS_INSTRUCTIONS = 3125;
    private static final int NORMANS_RECEIPT = 3126;
    private static final int DUNINGS_INSTRUCTIONS = 3127;
    private static final int DUNINGS_KEY = 3128;
    private static final int NORMANS_LIST = 3129;
    private static final int GRAY_BONE_POWDER = 3130;
    private static final int GRANITE_WHETSTONE = 3131;
    private static final int RED_PIGMENT = 3132;
    private static final int BRAIDED_YARN = 3133;
    private static final int JOURNEYMAN_GEM = 3134;
    private static final int PINTERS_INSTRUCTIONS = 3135;
    private static final int AMBER_BEAD = 3136;
    private static final int AMBER_LUMP = 3137;
    private static final int JOURNEYMAN_DECO_BEADS = 3138;
    private static final int JOURNEYMAN_RING = 3139;
    private static final int RP_JOURNEYMAN_RING = 3024;
    private static final int DIMENSION_DIAMOND = 7562;
    private static final int RP_AMBER_BEAD = 3025;

    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    3,
                    4,
                    20223,
                    VALKONS_RECOMMEND,
                    MANDRAGORA_BERRY,
                    1,
                    20,
                    1
            },
            {
                    3,
                    4,
                    20154,
                    VALKONS_RECOMMEND,
                    MANDRAGORA_BERRY,
                    1,
                    50,
                    1
            },
            {
                    3,
                    4,
                    20155,
                    VALKONS_RECOMMEND,
                    MANDRAGORA_BERRY,
                    1,
                    50,
                    1
            },
            {
                    3,
                    4,
                    20156,
                    VALKONS_RECOMMEND,
                    MANDRAGORA_BERRY,
                    1,
                    50,
                    1
            },
            {
                    5,
                    0,
                    20267,
                    DUNINGS_INSTRUCTIONS,
                    DUNINGS_KEY,
                    30,
                    100,
                    1
            },
            {
                    5,
                    0,
                    20268,
                    DUNINGS_INSTRUCTIONS,
                    DUNINGS_KEY,
                    30,
                    100,
                    1
            },
            {
                    5,
                    0,
                    20269,
                    DUNINGS_INSTRUCTIONS,
                    DUNINGS_KEY,
                    30,
                    100,
                    1
            },
            {
                    5,
                    0,
                    20270,
                    DUNINGS_INSTRUCTIONS,
                    DUNINGS_KEY,
                    30,
                    100,
                    1
            },
            {
                    5,
                    0,
                    20271,
                    DUNINGS_INSTRUCTIONS,
                    DUNINGS_KEY,
                    30,
                    100,
                    1
            },
            {
                    5,
                    0,
                    20200,
                    NORMANS_LIST,
                    GRAY_BONE_POWDER,
                    70,
                    100,
                    2
            },
            {
                    5,
                    0,
                    20201,
                    NORMANS_LIST,
                    GRAY_BONE_POWDER,
                    70,
                    100,
                    2
            },
            {
                    5,
                    0,
                    20202,
                    NORMANS_LIST,
                    RED_PIGMENT,
                    70,
                    100,
                    2
            },
            {
                    5,
                    0,
                    20083,
                    NORMANS_LIST,
                    GRANITE_WHETSTONE,
                    70,
                    100,
                    2
            },
            {
                    5,
                    0,
                    20168,
                    NORMANS_LIST,
                    BRAIDED_YARN,
                    70,
                    100,
                    2
            }
    };

    public _216_TrialoftheGuildsman() {
        super(false);

        addStartNpc(VALKON);
        addTalkId(VALKON,NORMAN,ALTRAN,PINTER,DUNING);

        addKillId(20079,20080,20081);

        for (int[] aDROPLIST_COND : DROPLIST_COND) addKillId(aDROPLIST_COND[2]);

        addQuestItem(ALLTRANS_INSTRUCTIONS,
                VALKONS_RECOMMEND,
                ALLTRANS_RECOMMEND1,
                NORMANS_INSTRUCTIONS,
                NORMANS_LIST,
                NORMANS_RECEIPT,
                ALLTRANS_RECOMMEND2,
                PINTERS_INSTRUCTIONS,
                DUNINGS_INSTRUCTIONS,
                JOURNEYMAN_GEM,
                JOURNEYMAN_DECO_BEADS,
                JOURNEYMAN_RING,
                AMBER_BEAD,
                AMBER_LUMP,
                MANDRAGORA_BERRY,
                DUNINGS_KEY,
                GRAY_BONE_POWDER,
                RED_PIGMENT,
                GRANITE_WHETSTONE,
                BRAIDED_YARN);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("valkon_q0216_06.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            st.giveItems(VALKONS_RECOMMEND);
            st.takeItems(ADENA_ID, 2000);
            if (!st.player.isVarSet("dd1")) {
                st.giveItems(DIMENSION_DIAMOND, 85);
                st.player.setVar("dd1");
            }
        } else if ("valkon_q0216_07c.htm".equalsIgnoreCase(event))
            st.setCond(3);
        else if ("valkon_q0216_05.htm".equalsIgnoreCase(event) && st.getQuestItemsCount(ADENA_ID) < 2000)
            htmltext = "valkon_q0216_05a.htm";
        else if ("30103_3".equalsIgnoreCase(event) || "30103_4".equalsIgnoreCase(event)) {
            if ("30103_3".equalsIgnoreCase(event))
                htmltext = "valkon_q0216_09a.htm";
            else
                htmltext = "valkon_q0216_09b.htm";
            st.takeAllItems(JOURNEYMAN_RING,ALLTRANS_INSTRUCTIONS,RP_JOURNEYMAN_RING);
            st.giveItems(MARK_OF_GUILDSMAN);
            if (!st.player.isVarSet("prof2.1")) {
                st.addExpAndSp(514739, 33384);
                st.giveItems(57, 93803);
                st.player.setVar("prof2.1");
            }
            st.exitCurrentQuest();
            st.playSound(SOUND_FINISH);
        } else if ("blacksmith_alltran_q0216_03.htm".equalsIgnoreCase(event)) {
            st.takeAllItems(VALKONS_RECOMMEND,MANDRAGORA_BERRY);
            st.giveItems(ALLTRANS_INSTRUCTIONS);
            st.giveItems(RP_JOURNEYMAN_RING);
            st.giveItems(ALLTRANS_RECOMMEND1);
            st.giveItems(ALLTRANS_RECOMMEND2);
            st.setCond(5);
        } else if ("warehouse_keeper_norman_q0216_04.htm".equalsIgnoreCase(event)) {
            st.takeItems(ALLTRANS_RECOMMEND1);
            st.giveItems(NORMANS_INSTRUCTIONS);
            st.giveItems(NORMANS_RECEIPT);
        } else if ("warehouse_keeper_norman_q0216_10.htm".equalsIgnoreCase(event)) {
            st.takeAllItems(DUNINGS_KEY,NORMANS_INSTRUCTIONS);
            st.giveItems(NORMANS_LIST);
        } else if ("blacksmith_duning_q0216_02.htm".equalsIgnoreCase(event)) {
            st.takeItems(NORMANS_RECEIPT);
            st.giveItems(DUNINGS_INSTRUCTIONS);
        } else if ("blacksmith_pinter_q0216_04.htm".equalsIgnoreCase(event)) {
            st.takeItems(ALLTRANS_RECOMMEND2);
            st.giveItems(PINTERS_INSTRUCTIONS);

            if (st.player.getClassId() == artisan) {
                htmltext = "blacksmith_pinter_q0216_05.htm";
                st.giveItems(RP_AMBER_BEAD);
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        int cond = 0;
        if (id != CREATED)
            cond = st.getCond();
        if (npcId == VALKON) {
            if (st.haveQuestItem(MARK_OF_GUILDSMAN)) {
                htmltext = "completed";
                st.exitCurrentQuest();
            } else if (cond == 0) {
                if (st.player.getClassId() == scavenger || st.player.getClassId() == artisan) {
                    if (st.player.getLevel() >= 35)
                        htmltext = "valkon_q0216_03.htm";
                    else {
                        htmltext = "valkon_q0216_02.htm";
                        st.exitCurrentQuest();
                    }
                } else {
                    htmltext = "valkon_q0216_01.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 2 && st.haveQuestItem(VALKONS_RECOMMEND) )
                htmltext = "valkon_q0216_07.htm";
            else if (st.haveQuestItem(ALLTRANS_INSTRUCTIONS) )
                if (st.haveQuestItem(JOURNEYMAN_RING, 7))
                    htmltext = "valkon_q0216_09.htm";
                else
                    htmltext = "valkon_q0216_08.htm";
        } else if (npcId == ALTRAN) {
            if (cond == 1 && st.haveQuestItem(VALKONS_RECOMMEND) ) {
                htmltext = "blacksmith_alltran_q0216_01.htm";
                st.setCond(2);
            } else if (cond == 4 && st.haveAllQuestItems(VALKONS_RECOMMEND,MANDRAGORA_BERRY) )
                htmltext = "blacksmith_alltran_q0216_02.htm";
            else if (cond < 6 && st.haveQuestItem(ALLTRANS_INSTRUCTIONS)  && !st.haveQuestItem(JOURNEYMAN_RING, 7))
                htmltext = "blacksmith_alltran_q0216_04.htm";
            else if (cond == 6 && st.haveQuestItem(JOURNEYMAN_RING, 7))
                htmltext = "blacksmith_alltran_q0216_05.htm";
        } else if (npcId == NORMAN && cond >= 5) {
            if (st.haveAllQuestItems(ALLTRANS_INSTRUCTIONS,ALLTRANS_RECOMMEND1) )
                htmltext = "warehouse_keeper_norman_q0216_01.htm";
            else if (st.haveAllQuestItems(ALLTRANS_INSTRUCTIONS,NORMANS_INSTRUCTIONS,NORMANS_RECEIPT))
                htmltext = "warehouse_keeper_norman_q0216_05.htm";
            else if (st.haveAllQuestItems(ALLTRANS_INSTRUCTIONS,NORMANS_INSTRUCTIONS,DUNINGS_INSTRUCTIONS))
                htmltext = "warehouse_keeper_norman_q0216_06.htm";
            else if (st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS) > 0 && st.getQuestItemsCount(NORMANS_INSTRUCTIONS) > 0 && st.getQuestItemsCount(DUNINGS_KEY) >= 30)
                htmltext = "warehouse_keeper_norman_q0216_07.htm";
            else if (st.haveAllQuestItems(ALLTRANS_INSTRUCTIONS,NORMANS_LIST) ) {
                if (st.getQuestItemsCount(GRAY_BONE_POWDER) >= 70 && st.getQuestItemsCount(GRANITE_WHETSTONE) >= 70 && st.getQuestItemsCount(RED_PIGMENT) >= 70 && st.getQuestItemsCount(BRAIDED_YARN) >= 70) {
                    htmltext = "warehouse_keeper_norman_q0216_12.htm";
                    st.takeAllItems(NORMANS_LIST,GRAY_BONE_POWDER,GRANITE_WHETSTONE,RED_PIGMENT,BRAIDED_YARN);
                    st.giveItems(JOURNEYMAN_GEM, 7);
                    if (st.haveQuestItem(JOURNEYMAN_DECO_BEADS, 7) && st.haveQuestItem(JOURNEYMAN_GEM, 7))
                        st.setCond(6);
                } else
                    htmltext = "warehouse_keeper_norman_q0216_11.htm";
            } else if (st.getQuestItemsCount(NORMANS_INSTRUCTIONS) == 0 && st.getQuestItemsCount(NORMANS_LIST) == 0 && st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS) == 1 && (st.getQuestItemsCount(JOURNEYMAN_GEM) > 0 || st.getQuestItemsCount(JOURNEYMAN_RING) > 0))
                htmltext = "warehouse_keeper_norman_q0216_13.htm";
        } else if (npcId == DUNING && cond >= 5) {
            if (st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS) > 0 && st.getQuestItemsCount(NORMANS_INSTRUCTIONS) > 0 && st.getQuestItemsCount(NORMANS_RECEIPT) > 0)
                htmltext = "blacksmith_duning_q0216_01.htm";
            else if (st.haveAllQuestItems(ALLTRANS_INSTRUCTIONS,NORMANS_INSTRUCTIONS,DUNINGS_INSTRUCTIONS))
                htmltext = "blacksmith_duning_q0216_03.htm";
            else if (st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS) > 0 && st.getQuestItemsCount(NORMANS_INSTRUCTIONS) > 0 && st.getQuestItemsCount(DUNINGS_KEY) == 30)
                htmltext = "blacksmith_duning_q0216_04.htm";
            else if (st.getQuestItemsCount(NORMANS_RECEIPT) == 0 && st.getQuestItemsCount(DUNINGS_INSTRUCTIONS) == 0 && st.getQuestItemsCount(DUNINGS_KEY) == 0 && st.getQuestItemsCount(ALLTRANS_INSTRUCTIONS) == 1)
                htmltext = "blacksmith_duning_q0216_01.htm";
        } else if (npcId == PINTER && cond >= 5)
            if (st.haveAllQuestItems(ALLTRANS_INSTRUCTIONS,ALLTRANS_RECOMMEND2))
                if (st.player.getLevel() < 36)
                    htmltext = "blacksmith_pinter_q0216_01.htm";
                else
                    htmltext = "blacksmith_pinter_q0216_02.htm";
            else if (st.haveAllQuestItems(ALLTRANS_INSTRUCTIONS,PINTERS_INSTRUCTIONS))
                if (!st.haveQuestItem(AMBER_BEAD, 70))
                    htmltext = "blacksmith_pinter_q0216_06.htm";
                else {
                    htmltext = "blacksmith_pinter_q0216_07.htm";
                    st.takeAllItems(PINTERS_INSTRUCTIONS,AMBER_BEAD,RP_AMBER_BEAD,AMBER_LUMP);
                    st.giveItems(JOURNEYMAN_DECO_BEADS, 7);
                    if (st.haveQuestItem(JOURNEYMAN_DECO_BEADS, 7) && st.haveQuestItem(JOURNEYMAN_GEM, 7))
                        st.setCond(6);
                }
            else if (st.haveQuestItem(ALLTRANS_INSTRUCTIONS)  && !st.haveQuestItem(PINTERS_INSTRUCTIONS) && (st.haveAnyQuestItems(JOURNEYMAN_DECO_BEADS,JOURNEYMAN_RING)))
                htmltext = "blacksmith_pinter_q0216_08.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        for (int[] aDROPLIST_COND : DROPLIST_COND)
            if (cond == aDROPLIST_COND[0] && npcId == aDROPLIST_COND[2])
                if (aDROPLIST_COND[3] == 0 || st.getQuestItemsCount(aDROPLIST_COND[3]) > 0)
                    if (aDROPLIST_COND[5] == 0)
                        st.rollAndGive(aDROPLIST_COND[4], aDROPLIST_COND[7], aDROPLIST_COND[6]);
                    else if (st.rollAndGive(aDROPLIST_COND[4], aDROPLIST_COND[7], aDROPLIST_COND[7], aDROPLIST_COND[5], aDROPLIST_COND[6])) {
                        if (aDROPLIST_COND[4] == DUNINGS_KEY)
                            st.takeItems(DUNINGS_INSTRUCTIONS);
                        if (aDROPLIST_COND[1] != cond && aDROPLIST_COND[1] != 0) {
                            st.setCond(aDROPLIST_COND[1]);
                            st.start();
                        }
                    }
        if (cond == 5 && (npcId == 20079 || npcId == 20080 || npcId == 20081))
            if (Rnd.chance(33) && st.haveAllQuestItems(ALLTRANS_INSTRUCTIONS,PINTERS_INSTRUCTIONS) ) {
                long count = st.getQuestItemsCount(AMBER_BEAD) + st.getQuestItemsCount(AMBER_LUMP) * 5;
                if (count < 70 && st.player.getClassId() == scavenger) {
                    st.giveItems(AMBER_BEAD, 5);
                    if (st.getQuestItemsCount(AMBER_BEAD) == 70)
                        st.playSound(SOUND_MIDDLE);
                    else
                        st.playSound(SOUND_ITEMGET);
                }
                if (count < 70 && st.player.getClassId() == artisan) {
                    st.giveItems(AMBER_LUMP, 5);
                    if (((MonsterInstance) npc).isSpoiled())
                        st.giveItems(AMBER_LUMP, 5);
                    count = st.getQuestItemsCount(AMBER_BEAD) + st.getQuestItemsCount(AMBER_LUMP) * 5;
                    if (count == 70)
                        st.playSound(SOUND_MIDDLE);
                    else
                        st.playSound(SOUND_ITEMGET);
                }
            }
    }
}
