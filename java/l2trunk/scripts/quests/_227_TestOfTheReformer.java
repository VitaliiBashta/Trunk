package l2trunk.scripts.quests;

import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _227_TestOfTheReformer extends Quest {
    //NPC
    private static final int Pupina = 30118;
    private static final int Sla = 30666;
    private static final int Katari = 30668;
    private static final int OlMahumPilgrimNPC = 30732;
    private static final int Kakan = 30669;
    private static final int Nyakuri = 30670;
    private static final int Ramus = 30667;
    //Quest items
    private static final int BookOfReform = 2822;
    private static final int LetterOfIntroduction = 2823;
    private static final int SlasLetter = 2824;
    private static final int Greetings = 2825;
    private static final int OlMahumMoney = 2826;
    private static final int KatarisLetter = 2827;
    private static final int NyakurisLetter = 2828;
    private static final int KakansLetter = 3037;
    private static final int UndeadList = 2829;
    private static final int RamussLetter = 2830;
    private static final int RippedDiary = 2831;
    private static final int HugeNail = 2832;
    private static final int LetterOfBetrayer = 2833;
    private static final int BoneFragment1 = 2834;
    private static final int BoneFragment2 = 2835;
    private static final int BoneFragment3 = 2836;
    private static final int BoneFragment4 = 2837;
    private static final int BoneFragment5 = 2838;
    //private static final int BoneFragment6 = 2839;
    //items
    private static final int MarkOfReformer = 2821;
    //MOB
    private static final int NamelessRevenant = 27099;
    private static final int Aruraune = 27128;
    private static final int OlMahumInspector = 27129;
    private static final int OlMahumBetrayer = 27130;
    private static final int CrimsonWerewolf = 27131;
    private static final int KrudelLizardman = 27132;
    private static final int SilentHorror = 20404;
    private static final int SkeletonLord = 20104;
    private static final int SkeletonMarksman = 20102;
    private static final int MiserySkeleton = 20022;
    private static final int SkeletonArcher = 20100;

    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private final int[][] DROPLIST_COND = {
            {
                    18,
                    0,
                    SilentHorror,
                    0,
                    BoneFragment1,
                    1,
                    70,
                    1
            },
            {
                    18,
                    0,
                    SkeletonLord,
                    0,
                    BoneFragment2,
                    1,
                    70,
                    1
            },
            {
                    18,
                    0,
                    SkeletonMarksman,
                    0,
                    BoneFragment3,
                    1,
                    70,
                    1
            },
            {
                    18,
                    0,
                    MiserySkeleton,
                    0,
                    BoneFragment4,
                    1,
                    70,
                    1
            },
            {
                    18,
                    0,
                    SkeletonArcher,
                    0,
                    BoneFragment5,
                    1,
                    70,
                    1
            }
    };

    public _227_TestOfTheReformer() {
        super(false);
        addStartNpc(Pupina);
        addTalkId(Sla,Katari,OlMahumPilgrimNPC,Kakan,Nyakuri,Ramus);
        //mob Drop
        addKillId(NamelessRevenant,Aruraune,OlMahumInspector,OlMahumBetrayer,CrimsonWerewolf,KrudelLizardman);
        for (int[] aDROPLIST_COND : DROPLIST_COND) {
            addKillId(aDROPLIST_COND[2]);
            addQuestItem(aDROPLIST_COND[4]);
        }
        addQuestItem(BookOfReform,
                HugeNail,
                LetterOfIntroduction,
                SlasLetter,
                KatarisLetter,
                LetterOfBetrayer,
                OlMahumMoney,
                NyakurisLetter,
                UndeadList,
                Greetings,
                KakansLetter,
                RamussLetter,
                RippedDiary);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("30118-04.htm".equalsIgnoreCase(event)) {
            st.giveItems(BookOfReform);
            if (!st.player.isVarSet("dd3")) {
                st.giveItems(7562, 60);
                st.player.setVar("dd3");
            }
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("30118-06.htm".equalsIgnoreCase(event)) {
            st.takeItems(HugeNail);
            st.takeItems(BookOfReform);
            st.giveItems(LetterOfIntroduction);
            st.setCond(4);
            st.start();
        } else if ("30666-04.htm".equalsIgnoreCase(event)) {
            st.takeItems(LetterOfIntroduction);
            st.giveItems(SlasLetter);
            st.setCond(5);
            st.start();
        } else if ("30669-03.htm".equalsIgnoreCase(event)) {
            if (GameObjectsStorage.getByNpcId(CrimsonWerewolf) == null) {
                st.setCond(12);
                st.start();
                st.addSpawn(CrimsonWerewolf);
                st.startQuestTimer("Wait4", 300000);
            } else {
                if (!st.isRunningQuestTimer("Wait4"))
                    st.startQuestTimer("Wait4", 300000);
                htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
            }
        } else if ("30670-03.htm".equalsIgnoreCase(event)) {
            if (GameObjectsStorage.getByNpcId(KrudelLizardman) == null) {
                st.setCond(15);
                st.start();
                st.addSpawn(KrudelLizardman);
                st.startQuestTimer("Wait5", 300000);
            } else {
                if (!st.isRunningQuestTimer("Wait5"))
                    st.startQuestTimer("Wait5", 300000);
                htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
            }
        } else if ("Wait1".equalsIgnoreCase(event)) {
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(Aruraune);
            if (isQuest != null)
                isQuest.deleteMe();
            if (st.getCond() == 2)
                st.setCond(1);
            return null;
        } else if (event.equalsIgnoreCase("Wait2")) {
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(OlMahumInspector);
            if (isQuest != null)
                isQuest.deleteMe();
            isQuest = GameObjectsStorage.getByNpcId(OlMahumPilgrimNPC);
            if (isQuest != null)
                isQuest.deleteMe();
            if (st.getCond() == 6)
                st.setCond(5);
            return null;
        } else if (event.equalsIgnoreCase("Wait3")) {
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(OlMahumBetrayer);
            if (isQuest != null)
                isQuest.deleteMe();
            return null;
        } else if (event.equalsIgnoreCase("Wait4")) {
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(CrimsonWerewolf);
            if (isQuest != null)
                isQuest.deleteMe();
            if (st.getCond() == 12)
                st.setCond(11);
            return null;
        } else if (event.equalsIgnoreCase("Wait5")) {
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(KrudelLizardman);
            if (isQuest != null)
                isQuest.deleteMe();
            if (st.getCond() == 15)
                st.setCond(14);
            return null;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Pupina) {
            if (st.haveQuestItem(MarkOfReformer) ) {
                htmltext = "completed";
                st.exitCurrentQuest();
            } else if (cond == 0) {
                if (st.player.getClassId().id == 0x0f || st.player.getClassId().id == 0x2a) {
                    if (st.player.getLevel() >= 39)
                        htmltext = "30118-03.htm";
                    else {
                        htmltext = "30118-01.htm";
                        st.exitCurrentQuest();
                    }
                } else {
                    htmltext = "30118-02.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 3)
                htmltext = "30118-05.htm";
            else if (cond >= 4)
                htmltext = "30118-07.htm";
        } else if (npcId == Sla) {
            if (cond == 4)
                htmltext = "30666-01.htm";
            else if (cond == 5)
                htmltext = "30666-05.htm";
            else if (cond == 10) {
                st.takeItems(OlMahumMoney);
                st.giveItems(Greetings, 3);
                htmltext = "30666-06.htm";
                st.setCond(11);
                st.start();
            } else if (cond == 20) {
                st.takeItems(KatarisLetter);
                st.takeItems(KakansLetter);
                st.takeItems(NyakurisLetter);
                st.takeItems(RamussLetter);
                st.giveItems(MarkOfReformer);
                if (!st.player.isVarSet("prof2.3")) {
                    st.addExpAndSp(626422, 42986);
                    st.giveItems(ADENA_ID, 113264);
                    st.player.setVar("prof2.3");
                }
                htmltext = "30666-07.htm";
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            }
        } else if (npcId == Katari) {
            if (cond == 5 || cond == 6) {
                NpcInstance NPC = GameObjectsStorage.getByNpcId(OlMahumPilgrimNPC);
                NpcInstance Mob = GameObjectsStorage.getByNpcId(OlMahumInspector);
                if (NPC == null && Mob == null) {
                    st.takeItems(SlasLetter, -1);
                    htmltext = "30668-01.htm";
                    st.setCond(6);
                    st.start();
                    st.addSpawn(OlMahumPilgrimNPC);
                    st.addSpawn(OlMahumInspector);
                    st.startQuestTimer("Wait2", 300000);
                } else {
                    if (!st.isRunningQuestTimer("Wait2"))
                        st.startQuestTimer("Wait2", 300000);
                    htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
                }
            } else if (cond == 8) {
                if (GameObjectsStorage.getByNpcId(OlMahumBetrayer) == null) {
                    htmltext = "30668-02.htm";
                    st.addSpawn(OlMahumBetrayer);
                    st.startQuestTimer("Wait3", 300000);
                } else {
                    if (!st.isRunningQuestTimer("Wait3"))
                        st.startQuestTimer("Wait3", 300000);
                    htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
                }
            } else if (cond == 9) {
                st.takeItems(LetterOfBetrayer);
                st.giveItems(KatarisLetter);
                htmltext = "30668-03.htm";
                st.setCond(10);
                st.start();
            }
        } else if (npcId == OlMahumPilgrimNPC) {
            if (cond == 7) {
                st.giveItems(OlMahumMoney);
                htmltext = "30732-01.htm";
                st.setCond(8);
                st.start();
                NpcInstance isQuest = GameObjectsStorage.getByNpcId(OlMahumInspector);
                if (isQuest != null)
                    isQuest.deleteMe();
                isQuest = GameObjectsStorage.getByNpcId(OlMahumPilgrimNPC);
                if (isQuest != null)
                    isQuest.deleteMe();
                st.cancelQuestTimer("Wait2");
            }
        } else if (npcId == Kakan) {
            if (cond == 11 || cond == 12)
                htmltext = "30669-01.htm";
            else if (cond == 13) {
                st.takeItems(Greetings, 1);
                st.giveItems(KakansLetter);
                htmltext = "30669-04.htm";
                st.setCond(14);
                st.start();
            }
        } else if (npcId == Nyakuri) {
            if (cond == 14 || cond == 15)
                htmltext = "30670-01.htm";
            else if (cond == 16) {
                st.takeItems(Greetings, 1);
                st.giveItems(NyakurisLetter);
                htmltext = "30670-04.htm";
                st.setCond(17);
                st.start();
            }
        } else if (npcId == Ramus)
            if (cond == 17) {
                st.takeItems(Greetings);
                st.giveItems(UndeadList);
                htmltext = "30667-01.htm";
                st.setCond(18);
                st.start();
            } else if (cond == 19) {
                st.takeItems(BoneFragment1);
                st.takeItems(BoneFragment2);
                st.takeItems(BoneFragment3);
                st.takeItems(BoneFragment4);
                st.takeItems(BoneFragment5);
                st.takeItems(UndeadList);
                st.giveItems(RamussLetter);
                htmltext = "30667-03.htm";
                st.setCond(20);
                st.start();
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        for (int[] aDROPLIST_COND : DROPLIST_COND)
            if (cond == aDROPLIST_COND[0] && npcId == aDROPLIST_COND[2])
                if (aDROPLIST_COND[3] == 0 || st.haveQuestItem(aDROPLIST_COND[3]))
                    if (aDROPLIST_COND[5] == 0)
                        st.rollAndGive(aDROPLIST_COND[4], aDROPLIST_COND[7], aDROPLIST_COND[6]);
                    else if (st.rollAndGive(aDROPLIST_COND[4], aDROPLIST_COND[7], aDROPLIST_COND[7], aDROPLIST_COND[5], aDROPLIST_COND[6]))
                        if (aDROPLIST_COND[1] != cond && aDROPLIST_COND[1] != 0) {
                            st.setCond(aDROPLIST_COND[1]);
                            st.start();
                        }
        if (cond == 18 && st.haveAllQuestItems(BoneFragment1,BoneFragment2, BoneFragment3, BoneFragment4,BoneFragment5)) {
            st.setCond(19);
            st.start();
        } else if (npcId == NamelessRevenant && (cond == 1 || cond == 2)) {
                st.giveItemIfNotHave(RippedDiary, 6);
            if (GameObjectsStorage.getByNpcId(Aruraune) == null) {
                st.takeItems(RippedDiary);
                st.setCond(2);
                st.start();
                st.addSpawn(Aruraune);
                st.startQuestTimer("Wait1", 300000);
            } else if (!st.isRunningQuestTimer("Wait1"))
                st.startQuestTimer("Wait1", 300000);
        } else if (npcId == Aruraune) {
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(Aruraune);
            if (isQuest != null)
                isQuest.deleteMe();
            if (cond == 2) {
                st.giveItemIfNotHave(HugeNail);
                st.setCond(3);
                st.start();
                st.cancelQuestTimer("Wait1");
            }
        } else if (npcId == OlMahumInspector) {
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(OlMahumInspector);
            if (isQuest != null)
                isQuest.deleteMe();
            st.cancelQuestTimer("Wait2");
            if (cond == 6) {
                st.setCond(7);
                st.start();
            }
        } else if (npcId == OlMahumBetrayer) {
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(OlMahumBetrayer);
            if (isQuest != null)
                isQuest.deleteMe();
            st.cancelQuestTimer("Wait3");
            if (cond == 8) {
                if (st.getQuestItemsCount(LetterOfBetrayer) == 0)
                    st.giveItems(LetterOfBetrayer);
                st.setCond(9);
                st.start();
            }
        } else if (npcId == CrimsonWerewolf) {
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(CrimsonWerewolf);
            if (isQuest != null)
                isQuest.deleteMe();
            st.cancelQuestTimer("Wait4");
            if (cond == 12) {
                st.setCond(13);
                st.start();
            }
        } else if (npcId == KrudelLizardman) {
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(KrudelLizardman);
            if (isQuest != null)
                isQuest.deleteMe();
            st.cancelQuestTimer("Wait5");
            if (cond == 15) {
                st.setCond(16);
                st.start();
            }
        }
    }
}