package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _136_MoreThanMeetsTheEye extends Quest {
    //NPC
    private static final int HARDIN = 30832;
    private static final int ERRICKIN = 30701;
    private static final int CLAYTON = 30464;
    //Item
    private static final int TransformSealbook = 9648;
    //Quest Item
    private static final int Ectoplasm = 9787;
    private static final int StabilizedEctoplasm = 9786;
    private static final int HardinsInstructions = 9788;
    private static final int GlassJaguarCrystal = 9789;
    private static final int BlankSealbook = 9790;

    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    3,
                    4,
                    20636,
                    0,
                    Ectoplasm,
                    35,
                    100,
                    1
            },
            {
                    3,
                    4,
                    20637,
                    0,
                    Ectoplasm,
                    35,
                    100,
                    1
            },
            {
                    3,
                    4,
                    20638,
                    0,
                    Ectoplasm,
                    35,
                    100,
                    1
            },
            {
                    3,
                    4,
                    20639,
                    0,
                    Ectoplasm,
                    35,
                    100,
                    2
            },
            {
                    7,
                    8,
                    20250,
                    0,
                    GlassJaguarCrystal,
                    5,
                    100,
                    1
            }
    };

    public _136_MoreThanMeetsTheEye() {
        super(false);

        addStartNpc(HARDIN);
        addTalkId(ERRICKIN,CLAYTON);

        addQuestItem(StabilizedEctoplasm,
                HardinsInstructions,
                BlankSealbook,
                Ectoplasm,
                GlassJaguarCrystal);

        for (int[] aDROPLIST_COND : DROPLIST_COND) addKillId(aDROPLIST_COND[2]);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("hardin_q0136_08.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.unset("id");
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("magister_errickin_q0136_03.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.start();
        } else if ("hardin_q0136_16.htm".equalsIgnoreCase(event)) {
            st.giveItems(HardinsInstructions);
            st.setCond(6);
            st.start();
        } else if ("magister_clayton_q0136_10.htm".equalsIgnoreCase(event)) {
            st.setCond(7);
            st.start();
        } else if ("hardin_q0136_23.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_FINISH);
            st.giveItems(TransformSealbook);
            st.giveItems(ADENA_ID, 67550, true);
            st.unset("id");
            st.unset("cond");
            st.finish();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == HARDIN) {
            if (cond == 0) {
                if (st.player.getLevel() >= 50) {
                    st.setCond(1);
                    htmltext = "hardin_q0136_01.htm";
                } else {
                    htmltext = "hardin_q0136_02.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 2 || cond == 3 || cond == 4)
                htmltext = "hardin_q0136_09.htm";
            else if (cond == 5) {
                st.takeItems(StabilizedEctoplasm, -1);
                htmltext = "hardin_q0136_10.htm";
            } else if (cond == 6)
                htmltext = "hardin_q0136_17.htm";
            else if (cond == 9) {
                st.takeItems(BlankSealbook, -1);
                htmltext = "hardin_q0136_18.htm";
            }
        } else if (npcId == ERRICKIN) {
            if (cond == 2)
                htmltext = "magister_errickin_q0136_02.htm";
            else if (cond == 3)
                htmltext = "magister_errickin_q0136_03.htm";
            else if (cond == 4 && st.getQuestItemsCount(Ectoplasm) < 35 && !st.isSet("id") ) {
                st.setCond(3);
                htmltext = "magister_errickin_q0136_03.htm";
            } else if (cond == 4 && !st.isSet("id")) {
                st.takeItems(Ectoplasm, -1);
                htmltext = "magister_errickin_q0136_05.htm";
                st.set("id");
            } else if (cond == 4 && st.isSet("id")) {
                htmltext = "magister_errickin_q0136_06.htm";
                st.giveItems(StabilizedEctoplasm);
                st.unset("id");
                st.setCond(5);
                st.start();
            } else if (cond == 5)
                htmltext = "magister_errickin_q0136_07.htm";
        } else if (npcId == CLAYTON)
            if (cond == 6) {
                st.takeItems(HardinsInstructions);
                htmltext = "magister_clayton_q0136_09.htm";
            } else if (cond == 7)
                htmltext = "magister_clayton_q0136_12.htm";
            else if (cond == 8 && st.getQuestItemsCount(GlassJaguarCrystal) < 5) {
                htmltext = "magister_clayton_q0136_12.htm";
                st.setCond(7);
            } else if (cond == 8) {
                htmltext = "magister_clayton_q0136_13.htm";
                st.takeItems(GlassJaguarCrystal);
                st.giveItems(BlankSealbook);
                st.setCond(9);
                st.start();
            } else if (cond == 9)
                htmltext = "magister_clayton_q0136_14.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        for (int[] aDROPLIST_COND : DROPLIST_COND)
            if (cond == aDROPLIST_COND[0] && npcId == aDROPLIST_COND[2])
                if (aDROPLIST_COND[3] == 0 || st.getQuestItemsCount(aDROPLIST_COND[3]) > 0) {
                    long count = st.getQuestItemsCount(aDROPLIST_COND[4]);
                    if (aDROPLIST_COND[5] > count && Rnd.chance(aDROPLIST_COND[6])) {
                        long random = 0;
                        if (aDROPLIST_COND[7] > 1) {
                            random = Rnd.get(aDROPLIST_COND[7]) + 1;
                            if (count + random > aDROPLIST_COND[5])
                                random = aDROPLIST_COND[5] - count;
                        } else
                            random = 1;
                        //Аддон
                        if (cond == 3) {
                            if (random == 1) {
                                if (Rnd.chance(15))
                                    random = 2;
                            } else if (Rnd.chance(15))
                                random = 3;
                            if (count + random > aDROPLIST_COND[5])
                                random = aDROPLIST_COND[5] - count;
                        }
                        //Конец Аддона
                        st.giveItems(aDROPLIST_COND[4], random);
                        if (count + random == aDROPLIST_COND[5]) {
                            st.playSound(SOUND_MIDDLE);
                            if (aDROPLIST_COND[1] != 0) {
                                st.setCond(aDROPLIST_COND[1]);
                                st.start();
                            }
                        } else
                            st.playSound(SOUND_ITEMGET);
                    }
                }
    }
}