package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.Inventory;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import static l2trunk.gameserver.model.base.ClassId.*;

public final class _229_TestOfWitchcraft extends Quest {
    //NPC
    private static final int Orim = 30630;
    private static final int Alexandria = 30098;
    private static final int Iker = 30110;
    private static final int Kaira = 30476;
    private static final int Lara = 30063;
    private static final int Roderik = 30631;
    private static final int Nestle = 30314;
    private static final int Leopold = 30435;
    private static final int Vasper = 30417;
    private static final int Vadin = 30188;
    private static final int Evert = 30633;
    private static final int Endrigo = 30632;
    //Quest Item
    private static final int MarkOfWitchcraft = 3307;
    private static final int OrimsDiagram = 3308;
    private static final int AlexandriasBook = 3309;
    private static final int IkersList = 3310;
    private static final int DireWyrmFang = 3311;
    private static final int LetoLizardmanCharm = 3312;
    private static final int EnchantedGolemHeartstone = 3313;
    private static final int LarasMemo = 3314;
    private static final int NestlesMemo = 3315;
    private static final int LeopoldsJournal = 3316;
    private static final int Aklantoth_1stGem = 3317;
    private static final int Aklantoth_2stGem = 3318;
    private static final int Aklantoth_3stGem = 3319;
    private static final int Aklantoth_4stGem = 3320;
    private static final int Aklantoth_5stGem = 3321;
    private static final int Aklantoth_6stGem = 3322;
    private static final int Brimstone_1st = 3323;
    private static final int OrimsInstructions = 3324;
    private static final int Orims1stLetter = 3325;
    private static final int Orims2stLetter = 3326;
    private static final int SirVaspersLetter = 3327;
    private static final int VadinsCrucifix = 3328;
    private static final int TamlinOrcAmulet = 3329;
    private static final int VadinsSanctions = 3330;
    private static final int IkersAmulet = 3331;
    private static final int SoultrapCrystal = 3332;
    private static final int PurgatoryKey = 3333;
    private static final int ZeruelBindCrystal = 3334;
    private static final int Brimstone_2nd = 3335;
    private static final int SwordOfBinding = 3029;
    //MOBs
    private static final int DireWyrm = 20557;
    private static final int EnchantedStoneGolem = 20565;
    private static final int LetoLizardman = 20577;
    private static final int LetoLizardmanArcher = 20578;
    private static final int LetoLizardmanSoldier = 20579;
    private static final int LetoLizardmanWarrior = 20580;
    private static final int LetoLizardmanShaman = 20581;
    private static final int LetoLizardmanOverlord = 20582;
    private static final int NamelessRevenant = 27099;
    private static final int SkeletalMercenary = 27100;
    private static final int DrevanulPrinceZeruel = 27101;
    private static final int TamlinOrc = 20601;
    private static final int TamlinOrcArcher = 20602;
    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    2,
                    0,
                    DireWyrm,
                    IkersList,
                    DireWyrmFang,
                    20,
                    100,
                    1
            },
            {
                    2,
                    0,
                    EnchantedStoneGolem,
                    IkersList,
                    EnchantedGolemHeartstone,
                    20,
                    80,
                    1
            },
            {
                    2,
                    0,
                    LetoLizardman,
                    IkersList,
                    LetoLizardmanCharm,
                    20,
                    50,
                    1
            },
            {
                    2,
                    0,
                    LetoLizardmanArcher,
                    IkersList,
                    LetoLizardmanCharm,
                    20,
                    50,
                    1
            },
            {
                    2,
                    0,
                    LetoLizardmanSoldier,
                    IkersList,
                    LetoLizardmanCharm,
                    20,
                    60,
                    1
            },
            {
                    2,
                    0,
                    LetoLizardmanWarrior,
                    IkersList,
                    LetoLizardmanCharm,
                    20,
                    60,
                    1
            },
            {
                    2,
                    0,
                    LetoLizardmanShaman,
                    IkersList,
                    LetoLizardmanCharm,
                    20,
                    70,
                    1
            },
            {
                    2,
                    0,
                    LetoLizardmanOverlord,
                    IkersList,
                    LetoLizardmanCharm,
                    20,
                    70,
                    1
            },
            {
                    2,
                    0,
                    NamelessRevenant,
                    LarasMemo,
                    Aklantoth_3stGem,
                    1,
                    100,
                    1
            },
            {
                    6,
                    0,
                    TamlinOrc,
                    VadinsCrucifix,
                    TamlinOrcAmulet,
                    20,
                    50,
                    1
            },
            {
                    6,
                    0,
                    TamlinOrcArcher,
                    VadinsCrucifix,
                    TamlinOrcAmulet,
                    20,
                    55,
                    1
            }
    };

    public _229_TestOfWitchcraft() {

        addTalkId(Alexandria, Iker, Kaira, Lara, Roderik, Nestle, Leopold, Vasper, Vadin, Evert, Endrigo);

        //mob Drop
        for (int[] aDROPLIST_COND : DROPLIST_COND) addKillId(aDROPLIST_COND[2]);

        addKillId(SkeletalMercenary, DrevanulPrinceZeruel);

        addQuestItem(OrimsDiagram,
                OrimsInstructions,
                Orims1stLetter,
                Orims2stLetter,
                Brimstone_1st,
                AlexandriasBook,
                IkersList,
                Aklantoth_1stGem,
                SoultrapCrystal,
                IkersAmulet,
                Aklantoth_2stGem,
                LarasMemo,
                NestlesMemo,
                LeopoldsJournal,
                Aklantoth_4stGem,
                Aklantoth_5stGem,
                Aklantoth_6stGem,
                SirVaspersLetter,
                SwordOfBinding,
                VadinsCrucifix,
                VadinsSanctions,
                Brimstone_2nd,
                PurgatoryKey,
                ZeruelBindCrystal,
                DireWyrmFang,
                EnchantedGolemHeartstone,
                LetoLizardmanCharm,
                Aklantoth_3stGem,
                TamlinOrcAmulet);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("30630-08.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_ACCEPT);
            st.giveItems(OrimsDiagram, 1);
            if (!st.player.isVarSet("dd3")) {
                st.giveItems(7562, 104);
                st.player.setVar("dd3");
            }
            st.setCond(1);
            st.start();
        } else if ("30098-03.htm".equalsIgnoreCase(event)) {
            st.giveItems(AlexandriasBook);
            st.takeItems(OrimsDiagram, 1);
            st.setCond(2);
            st.start();
        } else if ("30110-03.htm".equalsIgnoreCase(event))
            st.giveItems(IkersList);
        else if ("30476-02.htm".equalsIgnoreCase(event))
            st.giveItems(Aklantoth_2stGem);
        else if (event.equalsIgnoreCase("30063-02.htm"))
            st.giveItems(LarasMemo);
        else if (event.equalsIgnoreCase("30314-02.htm"))
            st.giveItems(NestlesMemo);
        else if (event.equalsIgnoreCase("30435-02.htm")) {
            st.takeItems(NestlesMemo, 1);
            st.giveItems(LeopoldsJournal);
        } else if (event.equalsIgnoreCase("30630-14.htm")) {
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(DrevanulPrinceZeruel);
            if (isQuest != null && !isQuest.isDead())
                htmltext = "Drevanul Prince Zeruel is already spawned.";
            else {
                st.takeItems(AlexandriasBook, 1);
                st.takeItems(Aklantoth_1stGem, 1);
                st.takeItems(Aklantoth_2stGem, 1);
                st.takeItems(Aklantoth_3stGem, 1);
                st.takeItems(Aklantoth_4stGem, 1);
                st.takeItems(Aklantoth_5stGem, 1);
                st.takeItems(Aklantoth_6stGem, 1);
                st.giveItemIfNotHave(Brimstone_1st);
                st.setCond(4);
                st.set("id");
                st.startQuestTimer("DrevanulPrinceZeruel_Fail", 300000);
                NpcInstance Zeruel = st.addSpawn(DrevanulPrinceZeruel);
                if (Zeruel != null) {
                    Zeruel.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.player, 1);
                }
            }
        } else if ("30630-16.htm".equalsIgnoreCase(event)) {
            htmltext = "30630-16.htm";
            st.takeItems(Brimstone_1st);
            st.giveItems(OrimsInstructions);
            st.giveItems(Orims1stLetter);
            st.giveItems(Orims2stLetter);
            st.setCond(6);
        } else if ("30110-08.htm".equalsIgnoreCase(event)) {
            st.takeItems(Orims2stLetter, 1);
            st.giveItems(SoultrapCrystal);
            st.giveItems(IkersAmulet);
            if (st.haveQuestItem(SwordOfBinding)) {
                st.setCond(7);
                st.start();
            }
        } else if ("30417-03.htm".equalsIgnoreCase(event)) {
            st.takeItems(Orims1stLetter, 1);
            st.giveItems(SirVaspersLetter);
        } else if ("30633-02.htm".equalsIgnoreCase(event)) {
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(DrevanulPrinceZeruel);
            if (isQuest != null)
                htmltext = "30633-fail.htm";
            else {
                st.set("id", 2);
                st.setCond(9);
                st.giveItemIfNotHave(Brimstone_2nd);
                st.addSpawn(DrevanulPrinceZeruel);
                st.startQuestTimer("DrevanulPrinceZeruel_Fail", 300000);
                NpcInstance Zeruel = GameObjectsStorage.getByNpcId(DrevanulPrinceZeruel);
                if (Zeruel != null)
                    Zeruel.getAggroList().addDamageHate(st.player, 0, 1);
            }
        } else if ("30630-20.htm".equalsIgnoreCase(event))
            st.takeItems(ZeruelBindCrystal, 1);
        else if ("30630-21.htm".equalsIgnoreCase(event))
            st.takeItems(PurgatoryKey, 1);
        else if ("30630-22.htm".equalsIgnoreCase(event)) {
            st.takeItems(SwordOfBinding);
            st.takeItems(IkersAmulet);
            st.takeItems(OrimsInstructions);
            if (!st.player.isVarSet("prof2.3")) {
                st.addExpAndSp(1029122, 70620);
                st.giveItems(ADENA_ID, 186077);
                st.player.setVar("prof2.3");
            }
            st.giveItems(MarkOfWitchcraft);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        if (event.equalsIgnoreCase("DrevanulPrinceZeruel_Fail")) {
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(DrevanulPrinceZeruel);
            if (isQuest != null)
                isQuest.deleteMe();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Orim) {
            if (st.getQuestItemsCount(MarkOfWitchcraft) != 0) {
                htmltext = "completed";
                st.exitCurrentQuest();
            } else if (cond == 0) {
                if (st.player.getClassId() == wizard
                        || st.player.getClassId() == knight || st.player.getClassId() == palusKnight)
                    if (st.player.getLevel() < 39) {
                        htmltext = "30630-02.htm";
                        st.exitCurrentQuest();
                    } else if (st.player.getClassId() == wizard)
                        htmltext = "30630-03.htm";
                    else
                        htmltext = "30630-05.htm";
                else {
                    htmltext = "30630-01.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "30630-09.htm";
            else if (cond == 2)
                htmltext = "30630-10.htm";
            else if (cond == 3 || st.isSet("id"))
                htmltext = "30630-11.htm";
            else if (cond == 5)
                htmltext = "30630-15.htm";
            else if (cond == 6)
                htmltext = "30630-17.htm";
            else if (cond == 7) {
                htmltext = "30630-18.htm";
                st.setCond(8);
            } else if (cond == 10)
                if (st.haveQuestItem(ZeruelBindCrystal))
                    htmltext = "30630-19.htm";
                else if (st.haveQuestItem(PurgatoryKey))
                    htmltext = "30630-20.htm";
                else
                    htmltext = "30630-21.htm";
        } else if (npcId == Alexandria) {
            if (cond == 1)
                htmltext = "30098-01.htm";
            else if (cond == 2)
                htmltext = "30098-04.htm";
            else
                htmltext = "30098-05.htm";
        } else if (npcId == Iker) {
            if (cond == 2) {
                if (st.haveQuestItem(Aklantoth_1stGem) || st.haveQuestItem(IkersList)) {
                    if (st.haveQuestItem(IkersList) && (st.getQuestItemsCount(DireWyrmFang) < 20 || st.getQuestItemsCount(LetoLizardmanCharm) < 20 || st.getQuestItemsCount(EnchantedGolemHeartstone) < 20))
                        htmltext = "30110-04.htm";
                    else if (!st.haveQuestItem(Aklantoth_1stGem) && st.haveQuestItem(IkersList)) {
                        st.takeAllItems(IkersList, DireWyrmFang, LetoLizardmanCharm, EnchantedGolemHeartstone);
                        st.giveItems(Aklantoth_1stGem);
                        htmltext = "30110-05.htm";
                    } else if (st.haveQuestItem(Aklantoth_1stGem))
                        htmltext = "30110-06.htm";
                } else {
                    htmltext = "30110-01.htm";
                }
            } else if (cond == 6)
                htmltext = "30110-07.htm";
            else if (cond == 10)
                htmltext = "30110-10.htm";
            else
                htmltext = "30110-09.htm";
        } else if (npcId == Kaira) {
            if (cond == 2)
                if (st.haveQuestItem(Aklantoth_2stGem)) htmltext = "30476-03.htm";
                else htmltext = "30476-01.htm";
            else if (cond > 2)
                htmltext = "30476-04.htm";
        } else if (npcId == Lara) {
            if (cond == 2) {
                if (!st.haveAnyQuestItems(LarasMemo,Aklantoth_3stGem))
                    htmltext = "30063-01.htm";
                else if (st.haveQuestItem(LarasMemo)  && st.getQuestItemsCount(Aklantoth_3stGem) == 0)
                    htmltext = "30063-03.htm";
                else if (st.haveQuestItem(Aklantoth_3stGem))
                    htmltext = "30063-04.htm";
            } else if (cond > 2)
                htmltext = "30063-05.htm";
        } else if (npcId == Roderik && cond == 2 && st.haveQuestItem(LarasMemo))
            htmltext = "30631-01.htm";
        else if (npcId == Nestle && cond == 2)
            if (st.haveAllQuestItems(Aklantoth_1stGem, Aklantoth_2stGem, Aklantoth_3stGem))
                htmltext = "30314-01.htm";
            else
                htmltext = "30314-04.htm";
        else if (npcId == Leopold) {
            if (cond == 2 && st.haveQuestItem(NestlesMemo)) {
                if (!st.haveAnyQuestItems(Aklantoth_4stGem, Aklantoth_5stGem, Aklantoth_6stGem))
                    htmltext = "30435-01.htm";
                else
                    htmltext = "30435-04.htm";
            } else
                htmltext = "30435-05.htm";
        } else if (npcId == Vasper) {
            if (cond == 6) {
                if (st.haveAnyQuestItems(SirVaspersLetter, VadinsCrucifix))
                    htmltext = "30417-04.htm";
                else if (!st.haveQuestItem(VadinsSanctions))
                    htmltext = "30417-01.htm";
                else if (st.haveQuestItem(VadinsSanctions)) {
                    htmltext = "30417-05.htm";
                    st.takeItems(VadinsSanctions, 1);
                    st.giveItems(SwordOfBinding);
                    if (st.haveQuestItem(SoultrapCrystal)) {
                        st.setCond(7);
                        st.start();
                    }
                }
            } else if (cond == 7)
                htmltext = "30417-06.htm";
        } else if (npcId == Vadin) {
            if (cond == 6) {
                if (st.haveQuestItem(SirVaspersLetter)) {
                    htmltext = "30188-01.htm";
                    st.takeItems(SirVaspersLetter);
                    st.giveItems(VadinsCrucifix);
                } else if (st.haveQuestItem(VadinsCrucifix) && st.getQuestItemsCount(TamlinOrcAmulet) < 20)
                    htmltext = "30188-02.htm";
                else if (st.haveQuestItem(TamlinOrcAmulet, 20)) {
                    htmltext = "30188-03.htm";
                    st.takeAllItems(TamlinOrcAmulet, VadinsCrucifix);
                    st.giveItems(VadinsSanctions);
                } else if (st.haveQuestItem(VadinsSanctions))
                    htmltext = "30188-04.htm";
            } else if (cond == 7)
                htmltext = "30188-05.htm";
        } else if (npcId == Evert) {
            if (st.getInt("id") == 2 || cond == 8 && !st.haveQuestItem(Brimstone_2nd))
                htmltext = "30633-01.htm";
            else
                htmltext = "30633-03.htm";
        } else if (npcId == Endrigo && cond == 2)
            htmltext = "30632-01.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        for (int[] aDROPLIST_COND : DROPLIST_COND)
            if (cond == aDROPLIST_COND[0] && npcId == aDROPLIST_COND[2])
                if (aDROPLIST_COND[3] == 0 || st.getQuestItemsCount(aDROPLIST_COND[3]) > 0) {
                    if (npcId == NamelessRevenant)
                        st.takeItems(LarasMemo);
                    st.rollAndGive(aDROPLIST_COND[4], aDROPLIST_COND[7], aDROPLIST_COND[7], aDROPLIST_COND[5], aDROPLIST_COND[6]);
                }
        if (cond == 2 && st.haveQuestItem(LeopoldsJournal) && npcId == SkeletalMercenary) {
            if (Rnd.chance(50))
                st.giveItemIfNotHave(Aklantoth_4stGem);
            if (Rnd.chance(50))
                st.giveItemIfNotHave(Aklantoth_5stGem);
            if (Rnd.chance(50))
                st.giveItemIfNotHave(Aklantoth_6stGem);
            if (st.haveAllQuestItems(Aklantoth_4stGem, Aklantoth_5stGem, Aklantoth_6stGem)) {
                st.takeItems(LeopoldsJournal);
                st.playSound(SOUND_MIDDLE);
                st.setCond(3);
                st.start();
            }
        } else if (cond == 4 && npcId == DrevanulPrinceZeruel) {
            st.cancelQuestTimer("DrevanulPrinceZeruel_Fail");
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(DrevanulPrinceZeruel);
            if (isQuest != null)
                isQuest.deleteMe();
            st.setCond(5);
            st.unset("id");
            st.start();
        } else if (cond == 9 && npcId == DrevanulPrinceZeruel) {
            if (st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == SwordOfBinding) {
                st.takeItems(Brimstone_2nd, 1);
                st.takeItems(SoultrapCrystal, 1);
                st.giveItems(PurgatoryKey);
                st.giveItems(ZeruelBindCrystal);
                st.playSound(SOUND_MIDDLE);
                st.unset("id");
                st.setCond(10);
                st.start();
                return;
            }
            st.cancelQuestTimer("DrevanulPrinceZeruel_Fail");
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(DrevanulPrinceZeruel);
            if (isQuest != null)
                isQuest.deleteMe();
        }
    }
}