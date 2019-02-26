package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _213_TrialOfSeeker extends Quest {
    //NPC
    private static final int Dufner = 30106;
    private static final int Terry = 30064;
    private static final int Viktor = 30684;
    private static final int Marina = 30715;
    private static final int Brunon = 30526;
    //Quest Item
    private static final int DufnersLetter = 2647;
    private static final int Terrys1stOrder = 2648;
    private static final int Terrys2ndOrder = 2649;
    private static final int TerrysLetter = 2650;
    private static final int ViktorsLetter = 2651;
    private static final int HawkeyesLetter = 2652;
    private static final int MysteriousRunestone = 2653;
    private static final int OlMahumRunestone = 2654;
    private static final int TurekRunestone = 2655;
    private static final int AntRunestone = 2656;
    private static final int TurakBugbearRunestone = 2657;
    private static final int TerrysBox = 2658;
    private static final int ViktorsRequest = 2659;
    private static final int MedusaScales = 2660;
    private static final int ShilensRunestone = 2661;
    private static final int AnalysisRequest = 2662;
    private static final int MarinasLetter = 2663;
    private static final int ExperimentTools = 2664;
    private static final int AnalysisResult = 2665;
    private static final int Terrys3rdOrder = 2666;
    private static final int ListOfHost = 2667;
    private static final int AbyssRunestone1 = 2668;
    private static final int AbyssRunestone2 = 2669;
    private static final int AbyssRunestone3 = 2670;
    private static final int AbyssRunestone4 = 2671;
    private static final int TerrysReport = 2672;
    private static final int MarkofSeeker = 2673;
    //MOBs
    private static final int NeerGhoulBerserker = 20198;
    private static final int OlMahumCaptain = 20211;
    private static final int TurekOrcWarlord = 20495;
    private static final int AntCaptain = 20080;
    private static final int TurakBugbearWarrior = 20249;
    private static final int Medusa = 20158;
    private static final int MarshStakatoDrone = 20234;
    private static final int BrekaOrcOverlord = 20270;
    private static final int AntWarriorCaptain = 20088;
    private static final int LetoLizardmanWarrior = 20580;
    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    2,
                    3,
                    NeerGhoulBerserker,
                    Terrys1stOrder,
                    MysteriousRunestone,
                    1,
                    10,
                    1
            },
            {
                    4,
                    0,
                    OlMahumCaptain,
                    Terrys2ndOrder,
                    OlMahumRunestone,
                    1,
                    20,
                    1
            },
            {
                    4,
                    0,
                    TurekOrcWarlord,
                    Terrys2ndOrder,
                    TurekRunestone,
                    1,
                    20,
                    1
            },
            {
                    4,
                    0,
                    AntCaptain,
                    Terrys2ndOrder,
                    AntRunestone,
                    1,
                    20,
                    1
            },
            {
                    4,
                    0,
                    TurakBugbearWarrior,
                    Terrys2ndOrder,
                    TurakBugbearRunestone,
                    1,
                    20,
                    1
            },
            {
                    9,
                    10,
                    Medusa,
                    ViktorsRequest,
                    MedusaScales,
                    10,
                    30,
                    1
            },
            {
                    16,
                    0,
                    MarshStakatoDrone,
                    ListOfHost,
                    AbyssRunestone1,
                    1,
                    25,
                    1
            },
            {
                    16,
                    0,
                    BrekaOrcOverlord,
                    ListOfHost,
                    AbyssRunestone2,
                    1,
                    25,
                    1
            },
            {
                    16,
                    0,
                    AntWarriorCaptain,
                    ListOfHost,
                    AbyssRunestone3,
                    1,
                    25,
                    1
            },
            {
                    16,
                    0,
                    LetoLizardmanWarrior,
                    ListOfHost,
                    AbyssRunestone4,
                    1,
                    25,
                    1
            }
    };

    public _213_TrialOfSeeker() {
        super(false);

        addStartNpc(Dufner);

        addTalkId(Terry,Viktor,Marina,Brunon);
        //mob Drop
        for (int[] aDROPLIST_COND : DROPLIST_COND) addKillId(aDROPLIST_COND[2]);

        addQuestItem(DufnersLetter,
                Terrys1stOrder,
                Terrys2ndOrder,
                TerrysLetter,
                TerrysBox,
                ViktorsLetter,
                ViktorsRequest,
                HawkeyesLetter,
                ShilensRunestone,
                AnalysisRequest,
                MarinasLetter,
                ExperimentTools,
                AnalysisResult,
                ListOfHost,
                Terrys3rdOrder,
                TerrysReport,
                MysteriousRunestone,
                OlMahumRunestone,
                TurekRunestone,
                AntRunestone,
                TurakBugbearRunestone,
                MedusaScales,
                AbyssRunestone1,
                AbyssRunestone2,
                AbyssRunestone3,
                AbyssRunestone4);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("dufner_q0213_05a.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            st.giveItems(DufnersLetter);
            if (!st.player.isVarSet("dd1")) {
                st.giveItems(7562, 64, false);
                st.player.setVar("dd1");
            }
        } else if ("terry_q0213_03.htm".equalsIgnoreCase(event)) {
            st.giveItems(Terrys1stOrder);
            st.takeItems(DufnersLetter);
            st.setCond(2);
            st.start();
        } else if ("terry_q0213_07.htm".equalsIgnoreCase(event)) {
            st.takeItems(Terrys1stOrder);
            st.takeItems(MysteriousRunestone);
            st.giveItems(Terrys2ndOrder);
            st.setCond(4);
            st.start();
        } else if ("terry_q0213_10.htm".equalsIgnoreCase(event)) {
            st.takeItems(Terrys2ndOrder);
            st.takeItems(OlMahumRunestone);
            st.takeItems(TurekRunestone);
            st.takeItems(AntRunestone);
            st.takeItems(TurakBugbearRunestone);
            st.giveItems(TerrysLetter);
            st.giveItems(TerrysBox);
            st.setCond(6);
            st.start();
        } else if ("trader_viktor_q0213_05.htm".equalsIgnoreCase(event)) {
            st.takeItems(TerrysLetter);
            st.giveItems(ViktorsLetter);
            st.setCond(7);
            st.start();
        } else if ("trader_viktor_q0213_11.htm".equalsIgnoreCase(event)) {
            st.takeItems(TerrysLetter);
            st.takeItems(TerrysBox);
            st.takeItems(HawkeyesLetter);
            st.giveItems(ViktorsRequest);
            st.setCond(9);
            st.start();
        } else if ("trader_viktor_q0213_15.htm".equalsIgnoreCase(event)) {
            st.takeItems(ViktorsRequest);
            st.takeItems(MedusaScales);
            st.giveItems(ShilensRunestone);
            st.giveItems(AnalysisRequest);
            st.setCond(11);
            st.start();
        } else if ("magister_marina_q0213_02.htm".equalsIgnoreCase(event)) {
            st.takeItems(ShilensRunestone);
            st.takeItems(AnalysisRequest);
            st.giveItems(MarinasLetter);
            st.setCond(12);
            st.start();
        } else if ("magister_marina_q0213_05.htm".equalsIgnoreCase(event)) {
            st.takeItems(ExperimentTools, 1);
            st.giveItems(AnalysisResult);
            st.setCond(14);
            st.start();
        } else if ("terry_q0213_18.htm".equalsIgnoreCase(event)) {
            if (st.player.getLevel() < 36) {
                htmltext = "terry_q0213_17.htm";
                st.takeItems(AnalysisResult);
                st.giveItems(Terrys3rdOrder);
            } else {
                htmltext = "terry_q0213_18.htm";
                st.giveItems(ListOfHost);
                st.takeItems(AnalysisResult);
                st.setCond(16);
                st.start();
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Dufner) {
            if (st.haveQuestItem(MarkofSeeker)) {
                htmltext = "completed";
                st.exitCurrentQuest();
            } else if (cond == 0 && st.getQuestItemsCount(TerrysReport) == 0) {
                if (st.player.getClassId() == ClassId.rogue
                        || st.player.getClassId() == ClassId.elvenScout
                        || st.player.getClassId() == ClassId.assassin)
                    if (st.player.getLevel() < 35) {
                        htmltext = "dufner_q0213_02.htm";
                        st.exitCurrentQuest();
                    } else
                        htmltext = "dufner_q0213_03.htm";
                else {
                    htmltext = "dufner_q0213_00.htm";
                    st.exitCurrentQuest();
                }
            } else if (st.getQuestItemsCount(DufnersLetter) == 1 && st.getQuestItemsCount(TerrysReport) == 0)
                htmltext = "dufner_q0213_06.htm";
            else if (st.getQuestItemsCount(DufnersLetter) == 0 && st.getQuestItemsCount(TerrysReport) == 0)
                htmltext = "dufner_q0213_07.htm";
            else if (st.haveQuestItem(TerrysReport) ) {
                if (!st.player.isVarSet("prof2.1")) {
                    st.addExpAndSp(514739, 33384);
                    st.giveItems(ADENA_ID, 93803);
                    st.player.setVar("prof2.1");
                }
                htmltext = "dufner_q0213_08.htm";
                st.playSound(SOUND_FINISH);
                st.takeItems(TerrysReport);
                st.giveItems(MarkofSeeker);
                st.exitCurrentQuest();
            }
        } else if (npcId == Terry) {
            if (cond == 1)
                htmltext = "terry_q0213_01.htm";
            else if (cond == 2)
                htmltext = "terry_q0213_04.htm";
            else if (cond == 3 && st.getQuestItemsCount(MysteriousRunestone) == 0) {
                htmltext = "terry_q0213_04.htm";
                st.setCond(2);
            } else if (cond == 3)
                htmltext = "terry_q0213_05.htm";
            else if (cond == 4)
                htmltext = "terry_q0213_07.htm";
            else if (cond == 5 && st.getQuestItemsCount(OlMahumRunestone) != 0 && st.getQuestItemsCount(TurekRunestone) != 0 && st.getQuestItemsCount(AntRunestone) != 0 && st.getQuestItemsCount(TurakBugbearRunestone) != 0)
                htmltext = "terry_q0213_09.htm";
            else if (cond == 5) {
                htmltext = "terry_q0213_07.htm";
                st.setCond(4);
            } else if (cond == 6)
                htmltext = "terry_q0213_11.htm";
            else if (cond == 7) {
                st.takeItems(ViktorsLetter, -1);
                st.giveItems(HawkeyesLetter, 1);
                htmltext = "terry_q0213_12.htm";
                st.setCond(8);
                st.start();
            } else if (cond == 8)
                htmltext = "terry_q0213_13.htm";
            else if (cond > 8 && cond < 14)
                htmltext = "terry_q0213_14.htm";
            else if (cond == 14 && st.getQuestItemsCount(AnalysisResult) > 0)
                htmltext = "terry_q0213_15.htm";
            else if ((cond == 14 || cond == 15) && st.getQuestItemsCount(Terrys3rdOrder) > 0) {
                if (st.player.getLevel() < 36)
                    htmltext = "terry_q0213_20.htm";
                else {
                    htmltext = "terry_q0213_21.htm";
                    st.takeItems(Terrys3rdOrder, -1);
                    st.giveItems(ListOfHost, 1);
                    st.setCond(16);
                    st.start();
                }
            } else if (cond == 15 || cond == 16) //15 конда пока нету, и хз как он получается
                htmltext = "terry_q0213_22.htm";
            else if (cond == 17)
                if (st.getQuestItemsCount(AbyssRunestone1) != 0 && st.getQuestItemsCount(AbyssRunestone2) != 0 && st.getQuestItemsCount(AbyssRunestone3) != 0 && st.getQuestItemsCount(AbyssRunestone4) != 0) {
                    htmltext = "terry_q0213_23.htm";
                    st.takeItems(ListOfHost);
                    st.takeItems(AbyssRunestone1);
                    st.takeItems(AbyssRunestone2);
                    st.takeItems(AbyssRunestone3);
                    st.takeItems(AbyssRunestone4);
                    st.giveItems(TerrysReport);
                    st.setCond(0); //Тут непонятки, в клиенте нету когда 18, а последний 17 говорит что нада идти к Терри, но после него есть еще один пункт.  Нужна проверка с офф сервера.
                } else {
                    htmltext = "terry_q0213_22.htm";
                    st.setCond(16);
                }
        } else if (npcId == Viktor) {
            if (cond == 6)
                htmltext = "trader_viktor_q0213_01.htm";
            else if (cond == 8)
                htmltext = "trader_viktor_q0213_12.htm";
            else if (cond == 9)
                htmltext = "trader_viktor_q0213_13.htm";
            else if (cond == 10 && st.getQuestItemsCount(MedusaScales) >= 10)
                htmltext = "trader_viktor_q0213_14.htm";
            else if (cond == 10) {
                st.setCond(9);
                htmltext = "trader_viktor_q0213_13.htm";
            }
        } else if (npcId == Marina) {
            if (cond == 11)
                htmltext = "magister_marina_q0213_01.htm";
            else if (cond == 12)
                htmltext = "magister_marina_q0213_03.htm";
            else if (cond == 13)
                htmltext = "magister_marina_q0213_04.htm";
            else if (cond > 13)
                htmltext = "magister_marina_q0213_06.htm";

        } else if (npcId == Brunon)
            if (cond == 12) {
                htmltext = "blacksmith_bronp_q0213_01.htm";
                st.takeItems(MarinasLetter, 1);
                st.giveItems(ExperimentTools, 1);
                st.setCond(13);
                st.start();
            } else if (cond == 13)
                htmltext = "blacksmith_bronp_q0213_02.htm";
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
                    else if (st.rollAndGive(aDROPLIST_COND[4], aDROPLIST_COND[7], aDROPLIST_COND[7], aDROPLIST_COND[5], aDROPLIST_COND[6]))
                        if (aDROPLIST_COND[1] != cond && aDROPLIST_COND[1] != 0) {
                            st.setCond(aDROPLIST_COND[1]);
                            st.start();
                        }
        if (cond == 4 && st.getQuestItemsCount(OlMahumRunestone) != 0 && st.getQuestItemsCount(TurekRunestone) != 0 && st.getQuestItemsCount(AntRunestone) != 0 && st.getQuestItemsCount(TurakBugbearRunestone) != 0) {
            st.setCond(5);
            st.start();
        } else if (cond == 16 && st.getQuestItemsCount(AbyssRunestone1) != 0 && st.getQuestItemsCount(AbyssRunestone2) != 0 && st.getQuestItemsCount(AbyssRunestone3) != 0 && st.getQuestItemsCount(AbyssRunestone4) != 0) {
            st.setCond(17);
            st.start();
        }
    }
}