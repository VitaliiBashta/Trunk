package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import static l2trunk.gameserver.model.base.ClassId.artisan;

public final class _231_TestOfTheMaestro extends Quest {
    //NPC
    private static final int Lockirin = 30531;
    private static final int Balanki = 30533;
    private static final int Arin = 30536;
    private static final int Filaur = 30535;
    private static final int Spiron = 30532;
    private static final int Croto = 30671;
    private static final int Kamur = 30675;
    private static final int Dubabah = 30672;
    private static final int Toma = 30556;
    private static final int Lorain = 30673;

    //Quest items
    private static final int RecommendationOfBalanki = 2864;
    private static final int RecommendationOfFilaur = 2865;
    private static final int RecommendationOfArin = 2866;
    private static final int LetterOfSolderDetachment = 2868;
    private static final int PaintOfKamuru = 2869;
    private static final int NecklaceOfKamuru = 2870;
    private static final int PaintOfTeleportDevice = 2871;
    private static final int TeleportDevice = 2872;
    private static final int ArchitectureOfCruma = 2873;
    private static final int ReportOfCruma = 2874;
    private static final int IngredientsOfAntidote = 2875;
    private static final int StingerWaspNeedle = 2876;
    private static final int MarshSpidersWeb = 2877;
    private static final int BloodOfLeech = 2878;
    private static final int BrokenTeleportDevice = 2916;

    // items
    private static final int DD = 7562;
    private static final int MarkOfMaestro = 2867;

    // MOB
    private static final int QuestMonsterEvilEyeLord = 27133;
    private static final int GiantMistLeech = 20225;
    private static final int StingerWasp = 20229;
    private static final int MarshSpider = 20233;

    // Drop Cond
    // [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    4,
                    5,
                    QuestMonsterEvilEyeLord,
                    0,
                    NecklaceOfKamuru,
                    1,
                    100,
                    1
            },
            {
                    13,
                    0,
                    GiantMistLeech,
                    0,
                    BloodOfLeech,
                    10,
                    100,
                    1
            },
            {
                    13,
                    0,
                    StingerWasp,
                    0,
                    StingerWaspNeedle,
                    10,
                    100,
                    1
            },
            {
                    13,
                    0,
                    MarshSpider,
                    0,
                    MarshSpidersWeb,
                    10,
                    100,
                    1
            }
    };

    public _231_TestOfTheMaestro() {
        addStartNpc(Lockirin);
        addTalkId(Balanki,Arin,Filaur,Spiron,Croto,Kamur,Dubabah,Toma,Lorain);

        // mob Drop
        for (int[] aDROPLIST_COND : DROPLIST_COND) {
            addKillId(aDROPLIST_COND[2]);
            addQuestItem(aDROPLIST_COND[4]);
        }

        addQuestItem(PaintOfKamuru,
                LetterOfSolderDetachment,
                PaintOfTeleportDevice,
                BrokenTeleportDevice,
                TeleportDevice,
                ArchitectureOfCruma,
                IngredientsOfAntidote,
                RecommendationOfBalanki,
                RecommendationOfFilaur,
                RecommendationOfArin,
                ReportOfCruma);
    }

    private void recommendationCount(QuestState st) {
        if (st.haveAllQuestItems(RecommendationOfArin,RecommendationOfFilaur,RecommendationOfBalanki))
            st.setCond(17);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("30531-04.htm".equalsIgnoreCase(event)) {
            if (!st.player.isVarSet("dd3")) {
                st.giveItems(DD, 23);
                st.player.setVar("dd3");
            }
            st.setCond(1);
            st.start();
            st.playSound("ItemSound.quest_accept");
        } else if ("30533-02.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.start();
        } else if ("30671-02.htm".equalsIgnoreCase(event)) {
            st.giveItems(PaintOfKamuru);
            st.setCond(3);
            st.start();
        } else if ("30556-05.htm".equalsIgnoreCase(event)) {
            st.takeItems(PaintOfTeleportDevice);
            st.giveItems(BrokenTeleportDevice);
            st.setCond(9);
            st.start();
            st.player.teleToLocation(140352, -194133, -2028);
        } else if ("30673-04.htm".equalsIgnoreCase(event)) {
            st.takeAllItems(BloodOfLeech,StingerWaspNeedle,MarshSpidersWeb,IngredientsOfAntidote);
            st.giveItems(ReportOfCruma);
            st.setCond(15);
            st.start();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Lockirin) {
            if (st.haveQuestItem(MarkOfMaestro)) {
                htmltext = "completed";
                st.exitCurrentQuest();
            } else if (cond == 0) {
                if (st.player.getClassId() == artisan) {
                    if (st.player.getLevel() >= 39)
                        htmltext = "30531-03.htm";
                    else {
                        htmltext = "30531-01.htm";
                        st.exitCurrentQuest();
                    }
                } else {
                    htmltext = "30531-02.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond >= 1 && cond <= 16)
                htmltext = "30531-05.htm";
            else if (cond == 17) {
                if (!st.player.isVarSet("prof2.3")) {
                    st.addExpAndSp(1029122, 70620);
                    st.giveItems(ADENA_ID, 186077);
                    st.player.setVar("prof2.3");
                }
                htmltext = "30531-06.htm";
                st.takeAllItems(RecommendationOfBalanki,RecommendationOfFilaur,RecommendationOfArin);
                st.giveItems(MarkOfMaestro);
                st.playSound("ItemSound.quest_finish");
                st.exitCurrentQuest();
            }
        } else if (npcId == Balanki) {
            if ((cond == 1 || cond == 11 || cond == 16) && st.getQuestItemsCount(RecommendationOfBalanki) == 0)
                htmltext = "30533-01.htm";
            else if (cond == 2)
                htmltext = "30533-03.htm";
            else if (cond == 6) {
                st.takeItems(LetterOfSolderDetachment);
                st.giveItems(RecommendationOfBalanki);
                htmltext = "30533-04.htm";
                st.setCond(7);
                recommendationCount(st);
                st.start();
            } else if (cond == 7 || cond == 17)
                htmltext = "30533-05.htm";
        } else if (npcId == Arin) {
            if ((cond == 1 || cond == 7 || cond == 16) && st.getQuestItemsCount(RecommendationOfArin) == 0) {
                st.giveItems(PaintOfTeleportDevice);
                htmltext = "30536-01.htm";
                st.setCond(8);
                st.start();
            } else if (cond == 8)
                htmltext = "30536-02.htm";
            else if (cond == 10) {
                st.takeItems(TeleportDevice);
                st.giveItems(RecommendationOfArin);
                htmltext = "30536-03.htm";
                st.setCond(11);
                recommendationCount(st);
                st.start();
            } else if (cond == 11 || cond == 17)
                htmltext = "30536-04.htm";
        } else if (npcId == Filaur) {
            if ((cond == 1 || cond == 7 || cond == 11) && st.getQuestItemsCount(RecommendationOfFilaur) == 0) {
                st.giveItems(ArchitectureOfCruma);
                htmltext = "30535-01.htm";
                st.setCond(12);
                st.start();
            } else if (cond == 12)
                htmltext = "30535-02.htm";
            else if (cond == 15) {
                st.takeItems(ReportOfCruma, 1);
                st.giveItems(RecommendationOfFilaur);
                st.setCond(16);
                htmltext = "30535-03.htm";
                recommendationCount(st);
                st.start();
            } else if (cond > 15)
                htmltext = "30535-04.htm";
        } else if (npcId == Croto) {
            if (cond == 2)
                htmltext = "30671-01.htm";
            else if (cond == 3)
                htmltext = "30671-03.htm";
            else if (cond == 5) {
                st.takeAllItems(NecklaceOfKamuru,PaintOfKamuru);
                st.giveItems(LetterOfSolderDetachment);
                htmltext = "30671-04.htm";
                st.setCond(6);
                st.start();
            } else if (cond == 6)
                htmltext = "30671-05.htm";
        } else if (npcId == Dubabah && cond == 3)
            htmltext = "30672-01.htm";
        else if (npcId == Kamur && cond == 3) {
            htmltext = "30675-01.htm";
            st.setCond(4);
            st.start();
        } else if (npcId == Toma) {
            if (cond == 8)
                htmltext = "30556-01.htm";
            else if (cond == 9) {
                st.takeItems(BrokenTeleportDevice);
                st.giveItems(TeleportDevice, 5);
                htmltext = "30556-06.htm";
                st.setCond(10);
                st.start();
            } else if (cond == 10)
                htmltext = "30556-07.htm";
        } else if (npcId == Lorain) {
            if (cond == 12) {
                st.takeItems(ArchitectureOfCruma);
                st.giveItems(IngredientsOfAntidote);
                st.setCond(13);
                htmltext = "30673-01.htm";
            } else if (cond == 13)
                htmltext = "30673-02.htm";
            else if (cond == 14)
                htmltext = "30673-03.htm";
            else if (cond == 15)
                htmltext = "30673-05.htm";
        } else if (npcId == Spiron && (cond == 1 || cond == 7 || cond == 11 || cond == 16))
            htmltext = "30532-01.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        for (int[] aDROPLIST_COND : DROPLIST_COND)
            if (cond == aDROPLIST_COND[0] && npcId == aDROPLIST_COND[2]) {
                if (st.rollAndGive(aDROPLIST_COND[4], aDROPLIST_COND[7], aDROPLIST_COND[7], aDROPLIST_COND[5], aDROPLIST_COND[6]))
                    if (aDROPLIST_COND[1] != cond && aDROPLIST_COND[1] != 0) {
                        st.setCond(aDROPLIST_COND[1]);
                        st.start();
                    }
            }
        if (cond == 13 && st.getQuestItemsCount(BloodOfLeech) >= 10 && st.getQuestItemsCount(StingerWaspNeedle) >= 10 && st.getQuestItemsCount(MarshSpidersWeb) >= 10) {
            st.setCond(14);
            st.start();
        }
    }
}