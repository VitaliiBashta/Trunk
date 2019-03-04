package l2trunk.scripts.quests;

import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import static l2trunk.gameserver.model.base.ClassId.*;

public final class _225_TestOfTheSearcher extends Quest {
    //NPC
    private static final int Luther = 30690;
    private static final int Alex = 30291;
    private static final int Tyra = 30420;
    private static final int Chest = 30628;
    private static final int Leirynn = 30728;
    private static final int Borys = 30729;
    private static final int Jax = 30730;
    private static final int Tree = 30627;
    //Quest items
    private static final int LuthersLetter = 2784;
    private static final int AlexsWarrant = 2785;
    private static final int Leirynns1stOrder = 2786;
    private static final int DeluTotem = 2787;
    private static final int Leirynns2ndOrder = 2788;
    private static final int ChiefKalkisFang = 2789;
    private static final int AlexsRecommend = 2808;
    private static final int LambertsMap = 2792;
    private static final int LeirynnsReport = 2790;
    private static final int AlexsLetter = 2793;
    private static final int StrangeMap = 2791;
    private static final int AlexsOrder = 2794;
    private static final int CombinedMap = 2805;
    private static final int GoldBar = 2807;
    private static final int WineCatalog = 2795;
    private static final int OldOrder = 2799;
    private static final int MalrukianWine = 2798;
    private static final int TyrasContract = 2796;
    private static final int RedSporeDust = 2797;
    private static final int JaxsDiary = 2800;
    private static final int SoltsMap = 2803;
    private static final int MakelsMap = 2804;
    private static final int RustedKey = 2806;
    private static final int TornMapPiece1st = 2801;
    private static final int TornMapPiece2st = 2802;
    //items
    private static final int MarkOfSearcher = 2809;
    //MOB
    private static final int DeluLizardmanShaman = 20781;
    private static final int DeluLizardmanAssassin = 27094;
    private static final int DeluChiefKalkis = 27093;
    private static final int GiantFungus = 20555;
    private static final int RoadScavenger = 20551;
    private static final int HangmanTree = 20144;
    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    3,
                    4,
                    DeluLizardmanShaman,
                    0,
                    DeluTotem,
                    10,
                    100,
                    1
            },
            {
                    3,
                    4,
                    DeluLizardmanAssassin,
                    0,
                    DeluTotem,
                    10,
                    100,
                    1
            },
            {
                    10,
                    11,
                    GiantFungus,
                    0,
                    RedSporeDust,
                    10,
                    100,
                    1
            }
    };

    public _225_TestOfTheSearcher() {
        super(false);
        addStartNpc(Luther);
        addTalkId(Alex,Leirynn,Borys,Tyra,Jax,Tree,Chest);
        //mob Drop
        addKillId(DeluChiefKalkis, RoadScavenger, HangmanTree);
        for (int[] aDROPLIST_COND : DROPLIST_COND)
            addKillId(aDROPLIST_COND[2]);
        addQuestItem(DeluTotem,
                RedSporeDust,
                LuthersLetter,
                AlexsWarrant,
                Leirynns1stOrder,
                Leirynns2ndOrder,
                LeirynnsReport,
                ChiefKalkisFang,
                StrangeMap,
                LambertsMap,
                AlexsLetter,
                AlexsOrder,
                WineCatalog,
                TyrasContract,
                OldOrder,
                MalrukianWine,
                JaxsDiary,
                TornMapPiece1st,
                TornMapPiece2st,
                SoltsMap,
                MakelsMap,
                RustedKey,
                CombinedMap);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("30690-05.htm".equalsIgnoreCase(event)) {
            st.giveItems(LuthersLetter);
            st.setCond(1);
            st.start();
            if (!st.player.isVarSet("dd3")) {
                st.giveItems(7562, 82);
                st.player.setVar("dd3");
            }
            st.playSound(SOUND_ACCEPT);
        } else if ("30291-07.htm".equalsIgnoreCase(event)) {
            st.takeItems(LeirynnsReport);
            st.takeItems(StrangeMap);
            st.giveItems(LambertsMap);
            st.giveItems(AlexsLetter);
            st.giveItems(AlexsOrder);
            st.setCond(8);
            st.start();
        } else if ("30420-01a.htm".equalsIgnoreCase(event)) {
            st.takeItems(WineCatalog);
            st.giveItems(TyrasContract);
            st.setCond(10);
            st.start();
        } else if ("30730-01d.htm".equalsIgnoreCase(event)) {
            st.takeItems(OldOrder);
            st.giveItems(JaxsDiary);
            st.setCond(14);
            st.start();
        } else if ("30627-01a.htm".equalsIgnoreCase(event)) {
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(Chest);
            if (isQuest == null) {
                st.giveItemIfNotHave(RustedKey);
                st.addSpawn(Chest);
                st.startQuestTimer("Chest", 300000);
                st.setCond(17);
                st.start();
            } else {
                if (!st.isRunningQuestTimer("Wait1"))
                    st.startQuestTimer("Wait1", 300000);
                htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
            }
        } else if ("30628-01a.htm".equalsIgnoreCase(event)) {
            st.takeItems(RustedKey);
            st.giveItems(GoldBar, 20);
            st.setCond(18);
        } else if ("Wait1".equalsIgnoreCase(event) || "Chest".equalsIgnoreCase(event)) {
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(Chest);
            if (isQuest != null)
                isQuest.deleteMe();
            st.cancelQuestTimer("Wait1");
            st.cancelQuestTimer("Chest");
            if (st.getCond() == 17)
                st.setCond(16);
            return null;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Luther) {
            if (st.haveQuestItem(MarkOfSearcher)) {
                htmltext = "completed";
                st.exitCurrentQuest();
            } else if (cond == 0) {
                if (st.player.getClassId() == rogue || st.player.getClassId() == elvenScout || st.player.getClassId() == assassin || st.player.getClassId() == scavenger) {
                    if (st.player.getLevel() >= 39) {
                        if (st.player.getClassId() == scavenger)
                            htmltext = "30690-04.htm";
                        else
                            htmltext = "30690-03.htm";
                    } else {
                        htmltext = "30690-02.htm";
                        st.exitCurrentQuest();
                    }
                } else {
                    htmltext = "30690-01.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "30690-06.htm";
            else if (cond > 1 && cond < 16)
                htmltext = "30623-17.htm";
            else if (cond == 19) {
                htmltext = "30690-08.htm";
                if (!st.player.isVarSet("prof2.3")) {
                    st.addExpAndSp(447444, 30704);
                    st.giveItems(ADENA_ID, 80093);
                    st.player.setVar("prof2.3");
                }
                st.takeItems(AlexsRecommend);
                st.giveItems(MarkOfSearcher);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            }
        } else if (npcId == Alex) {
            if (cond == 1) {
                htmltext = "30291-01.htm";
                st.takeItems(LuthersLetter);
                st.giveItems(AlexsWarrant);
                st.setCond(2);
                st.start();
            } else if (cond == 2)
                htmltext = "30291-02.htm";
            else if (cond > 2 && cond < 7)
                htmltext = "30291-03.htm";
            else if (cond == 7)
                htmltext = "30291-04.htm";
            else if (cond == 8)
                htmltext = "30291-08.htm";
            else if (cond == 13 || cond == 14)
                htmltext = "30291-09.htm";
            else if (cond == 18) {
                st.takeAllItems(AlexsOrder, CombinedMap, GoldBar);
                st.giveItems(AlexsRecommend);
                htmltext = "30291-11.htm";
                st.setCond(19);
                st.start();
            } else if (cond == 19)
                htmltext = "30291-12.htm";

        } else if (npcId == Leirynn) {
            if (cond == 2) {
                htmltext = "30728-01.htm";
                st.takeItems(AlexsWarrant);
                st.giveItems(Leirynns1stOrder);
                st.setCond(3);
                st.start();
            } else if (cond == 3)
                htmltext = "30728-02.htm";
            else if (cond == 4) {
                htmltext = "30728-03.htm";
                st.takeAllItems(DeluTotem,Leirynns1stOrder);
                st.giveItems(Leirynns2ndOrder);
                st.setCond(5);
                st.start();
            } else if (cond == 5)
                htmltext = "30728-04.htm";
            else if (cond == 6) {
                st.takeAllItems(ChiefKalkisFang,Leirynns2ndOrder);
                st.giveItems(LeirynnsReport);
                htmltext = "30728-05.htm";
                st.setCond(7);
                st.start();
            } else if (cond == 7)
                htmltext = "30728-06.htm";
            else if (cond == 8)
                htmltext = "30728-07.htm";
        } else if (npcId == Borys) {
            if (cond == 8) {
                st.takeItems(AlexsLetter);
                st.giveItems(WineCatalog);
                htmltext = "30729-01.htm";
                st.setCond(9);
                st.start();
            } else if (cond == 9)
                htmltext = "30729-02.htm";
            else if (cond == 12) {
                st.takeAllItems(WineCatalog,MalrukianWine);
                st.giveItems(OldOrder, 1);
                htmltext = "30729-03.htm";
                st.setCond(13);
                st.start();
            } else if (cond == 13)
                htmltext = "30729-04.htm";
            else if (cond >= 8 && cond <= 14)
                htmltext = "30729-05.htm";
        } else if (npcId == Tyra) {
            if (cond == 9)
                htmltext = "30420-01.htm";
            else if (cond == 10)
                htmltext = "30420-02.htm";
            else if (cond == 11) {
                st.takeItems(TyrasContract, -1);
                st.takeItems(RedSporeDust, -1);
                st.giveItems(MalrukianWine, 1);
                htmltext = "30420-03.htm";
                st.setCond(12);
                st.start();
            } else if (cond == 12 || cond == 13)
                htmltext = "30420-04.htm";
        } else if (npcId == Jax) {
            if (cond == 13)
                htmltext = "30730-01.htm";
            else if (cond == 14)
                htmltext = "30730-02.htm";
            else if (cond == 15) {
                st.takeItems(SoltsMap, -1);
                st.takeItems(MakelsMap, -1);
                st.takeItems(LambertsMap, -1);
                st.takeItems(JaxsDiary, -1);
                st.giveItems(CombinedMap, 1);
                htmltext = "30730-03.htm";
                st.setCond(16);
                st.start();
            } else if (cond == 16)
                htmltext = "30730-04.htm";
        } else if (npcId == Tree) {
            if (cond == 16 || cond == 17)
                htmltext = "30627-01.htm";
        } else if (npcId == Chest)
            if (cond == 17)
                htmltext = "30628-01.htm";
            else
                htmltext = "<html><head><body>You haven't got a Key for this Chest.</body></html>";
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
        if (cond == 5 && npcId == DeluChiefKalkis) {
            st.giveItemIfNotHave(StrangeMap);
            st.giveItemIfNotHave(ChiefKalkisFang);
            st.playSound(SOUND_MIDDLE);
            st.setCond(6);
            st.start();
        } else if (cond == 14) {
            if (npcId == RoadScavenger && !st.haveQuestItem(SoltsMap)) {
                st.giveItems(TornMapPiece1st);
                if (st.haveQuestItem(TornMapPiece1st, 4)) {
                    st.takeItems(TornMapPiece1st);
                    st.giveItems(SoltsMap);
                }
            } else if (npcId == HangmanTree && st.getQuestItemsCount(MakelsMap) == 0) {
                st.giveItems(TornMapPiece2st);
                if (st.haveQuestItem(TornMapPiece2st, 4)) {
                    st.takeItems(TornMapPiece2st);
                    st.giveItems(MakelsMap);
                }
            }
            if (st.haveAllQuestItems(SoltsMap, MakelsMap)) {
                st.setCond(15);
                st.start();
            }
        }
    }
}