package l2trunk.scripts.quests;

import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _219_TestimonyOfFate extends Quest {
    //NPC
    private static final int Kaira = 30476;
    private static final int Metheus = 30614;
    private static final int Ixia = 30463;
    private static final int AldersSpirit = 30613;
    private static final int Roa = 30114;
    private static final int Norman = 30210;
    private static final int Thifiell = 30358;
    private static final int Arkenia = 30419;
    private static final int BloodyPixy = 31845;
    private static final int BlightTreant = 31850;
    //QuestItem
    private static final int KairasLetter = 3173;
    private static final int MetheussFuneralJar = 3174;
    private static final int KasandrasRemains = 3175;
    private static final int HerbalismTextbook = 3176;
    private static final int IxiasList = 3177;
    private static final int MedusasIchor = 3178;
    private static final int MarshSpiderFluids = 3179;
    private static final int DeadSeekerDung = 3180;
    private static final int TyrantsBlood = 3181;
    private static final int NightshadeRoot = 3182;
    private static final int Belladonna = 3183;
    private static final int AldersSkull1 = 3184;
    private static final int AldersSkull2 = 3185;
    private static final int AldersReceipt = 3186;
    private static final int RevelationsManuscript = 3187;
    private static final int KairasRecommendation = 3189;
    private static final int KairasInstructions = 3188;
    private static final int PalusCharm = 3190;
    private static final int ThifiellsLetter = 3191;
    private static final int ArkeniasNote = 3192;
    private static final int PixyGarnet = 3193;
    private static final int BlightTreantSeed = 3199;
    private static final int GrandissSkull = 3194;
    private static final int KarulBugbearSkull = 3195;
    private static final int BrekaOverlordSkull = 3196;
    private static final int LetoOverlordSkull = 3197;
    private static final int BlackWillowLeaf = 3200;
    private static final int RedFairyDust = 3198;
    private static final int BlightTreantSap = 3201;
    private static final int ArkeniasLetter = 1246;
    //items
    private static final int MarkofFate = 3172;
    //MOB
    private static final int HangmanTree = 20144;
    private static final int Medusa = 20158;
    private static final int MarshSpider = 20233;
    private static final int DeadSeeker = 20202;
    private static final int Tyrant = 20192;
    private static final int TyrantKingpin = 20193;
    private static final int MarshStakatoWorker = 20230;
    private static final int MarshStakato = 20157;
    private static final int MarshStakatoSoldier = 20232;
    private static final int MarshStakatoDrone = 20234;
    private static final int Grandis = 20554;
    private static final int KarulBugbear = 20600;
    private static final int BrekaOrcOverlord = 20270;
    private static final int LetoLizardmanOverlord = 20582;
    private static final int BlackWillowLurker = 27079;
    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    6,
                    0,
                    Medusa,
                    IxiasList,
                    MedusasIchor,
                    10,
                    100,
                    1
            },
            {
                    6,
                    0,
                    MarshSpider,
                    IxiasList,
                    MarshSpiderFluids,
                    10,
                    100,
                    1
            },
            {
                    6,
                    0,
                    DeadSeeker,
                    IxiasList,
                    DeadSeekerDung,
                    10,
                    100,
                    1
            },
            {
                    6,
                    0,
                    Tyrant,
                    IxiasList,
                    TyrantsBlood,
                    10,
                    100,
                    1
            },
            {
                    6,
                    0,
                    TyrantKingpin,
                    IxiasList,
                    TyrantsBlood,
                    10,
                    100,
                    1
            },
            {
                    6,
                    0,
                    MarshStakatoWorker,
                    IxiasList,
                    NightshadeRoot,
                    10,
                    100,
                    1
            },
            {
                    6,
                    0,
                    MarshStakato,
                    IxiasList,
                    NightshadeRoot,
                    10,
                    100,
                    1
            },
            {
                    6,
                    0,
                    MarshStakatoSoldier,
                    IxiasList,
                    NightshadeRoot,
                    10,
                    100,
                    1
            },
            {
                    6,
                    0,
                    MarshStakatoDrone,
                    IxiasList,
                    NightshadeRoot,
                    10,
                    100,
                    1
            },
            {
                    17,
                    0,
                    Grandis,
                    PixyGarnet,
                    GrandissSkull,
                    10,
                    100,
                    1
            },
            {
                    17,
                    0,
                    KarulBugbear,
                    PixyGarnet,
                    KarulBugbearSkull,
                    10,
                    100,
                    1
            },
            {
                    17,
                    0,
                    BrekaOrcOverlord,
                    PixyGarnet,
                    BrekaOverlordSkull,
                    10,
                    100,
                    1
            },
            {
                    17,
                    0,
                    LetoLizardmanOverlord,
                    PixyGarnet,
                    LetoOverlordSkull,
                    10,
                    100,
                    1
            },
            {
                    17,
                    0,
                    BlackWillowLurker,
                    BlightTreantSeed,
                    BlackWillowLeaf,
                    10,
                    100,
                    1
            }
    };

    public _219_TestimonyOfFate() {
        addStartNpc(Kaira);

        addTalkId(Metheus,Ixia,AldersSpirit,Roa,Norman,Thifiell,Arkenia,BloodyPixy,BlightTreant);

        for (int[] aDROPLIST_COND : DROPLIST_COND) addKillId(aDROPLIST_COND[2]);

        addKillId(HangmanTree);

        addQuestItem(KairasLetter,
                MetheussFuneralJar,
                KasandrasRemains,
                IxiasList,
                Belladonna,
                AldersSkull1,
                AldersSkull2,
                AldersReceipt,
                RevelationsManuscript,
                KairasRecommendation,
                KairasInstructions,
                ThifiellsLetter,
                PalusCharm,
                ArkeniasNote,
                PixyGarnet,
                BlightTreantSeed,
                RedFairyDust,
                BlightTreantSap,
                ArkeniasLetter,
                MedusasIchor,
                MarshSpiderFluids,
                DeadSeekerDung,
                TyrantsBlood,
                NightshadeRoot,
                GrandissSkull,
                KarulBugbearSkull,
                BrekaOverlordSkull,
                LetoOverlordSkull,
                BlackWillowLeaf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("30476-05.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            st.giveItems(KairasLetter);
            if (!st.player.isVarSet("dd2")) {
                st.giveItems(7562, 72);
                st.player.setVar("dd2");
            }
        } else if ("30114-04.htm".equalsIgnoreCase(event)) {
            st.takeItems(AldersSkull2, 1);
            st.giveItems(AldersReceipt);
            st.setCond(12);
            st.start();
        } else if ("30476-12.htm".equalsIgnoreCase(event)) {
            if (st.player.getLevel() >= 38) {
                st.takeItems(RevelationsManuscript);
                st.giveItems(KairasRecommendation);
                st.setCond(15);
                st.start();
            } else {
                htmltext = "30476-13.htm";
                st.takeItems(RevelationsManuscript);
                st.giveItems(KairasInstructions);
                st.setCond(14);
                st.start();
            }
        } else if ("30419-02.htm".equalsIgnoreCase(event)) {
            st.takeItems(ThifiellsLetter);
            st.giveItems(ArkeniasNote);
            st.setCond(17);
            st.start();
        } else if ("31845-02.htm".equalsIgnoreCase(event))
            st.giveItems(PixyGarnet);
        else if ("31850-02.htm".equalsIgnoreCase(event))
            st.giveItems(BlightTreantSeed);
        else if ("30419-05.htm".equalsIgnoreCase(event)) {
            st.takeAllItems(ArkeniasNote,RedFairyDust,BlightTreantSap);
            st.giveItems(ArkeniasLetter);
            st.setCond(18);
            st.start();
        }
        if ("AldersSpirit_Fail".equalsIgnoreCase(event)) {
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(AldersSpirit);
            if (isQuest != null)
                isQuest.deleteMe();
            st.setCond(9);
        }

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Kaira) {
            if (st.getQuestItemsCount(MarkofFate) != 0) {
                htmltext = "completed";
                st.exitCurrentQuest();
            } else if (cond == 0) {
                if (st.player.getRace() == Race.darkelf && st.player.getLevel() >= 37)
                    htmltext = "30476-03.htm";
                else if (st.player.getRace() == Race.darkelf) {
                    htmltext = "30476-02.htm";
                    st.exitCurrentQuest();
                } else {
                    htmltext = "30476-01.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 2)
                htmltext = "30476-06.htm";
            else if (cond == 9 || cond == 10) {
                NpcInstance AldersSpiritObject = GameObjectsStorage.getByNpcId(AldersSpirit);
                if (AldersSpiritObject == null) {
                    st.takeItems(AldersSkull1);
                    st.giveItemIfNotHave(AldersSkull2);
                    htmltext = "30476-09.htm";
                    st.setCond(10);
                    st.start();
                    st.addSpawn(AldersSpirit);
                    st.startQuestTimer("AldersSpirit_Fail", 300000);
                } else
                    htmltext = "<html><head><body>I am borrowed, approach in some minutes</body></html>";
            } else if (cond == 13)
                htmltext = "30476-11.htm";
            else if (cond == 14) {
                if (st.haveQuestItem(KairasInstructions) && st.player.getLevel() < 38)
                    htmltext = "30476-14.htm";
                else if (st.haveQuestItem(KairasInstructions) && st.player.getLevel() >= 38) {
                    st.giveItems(KairasRecommendation);
                    st.takeItems(KairasInstructions, 1);
                    htmltext = "30476-15.htm";
                    st.setCond(15);
                    st.start();
                }
            } else if (cond == 15)
                htmltext = "30476-16.htm";
            else if (cond == 16 || cond == 17)
                htmltext = "30476-17.htm";
            else if (st.haveAnyQuestItems(MetheussFuneralJar, KasandrasRemains))
                htmltext = "30476-07.htm";
            else if (st.haveAnyQuestItems(HerbalismTextbook, IxiasList))
                htmltext = "30476-08.htm";
            else if (st.haveAnyQuestItems(AldersSkull2, AldersReceipt))
                htmltext = "30476-10.htm";
        } else if (npcId == Metheus) {
            if (cond == 1) {
                htmltext = "30614-01.htm";
                st.takeItems(KairasLetter);
                st.giveItems(MetheussFuneralJar);
                st.setCond(2);
                st.start();
            } else if (cond == 2)
                htmltext = "30614-02.htm";
            else if (cond == 3) {
                st.takeItems(KasandrasRemains, -1);
                st.giveItems(HerbalismTextbook, 1);
                htmltext = "30614-03.htm";
                st.setCond(5);
                st.start();
            } else if (cond == 8) {
                st.takeItems(Belladonna, -1);
                st.giveItems(AldersSkull1, 1);
                htmltext = "30614-05.htm";
                st.setCond(9);
                st.start();
            } else if (st.haveAnyQuestItems(HerbalismTextbook, IxiasList))
                htmltext = "30614-04.htm";
            else if (st.haveAnyQuestItems(AldersSkull1, AldersSkull2, AldersReceipt, RevelationsManuscript, KairasInstructions, KairasRecommendation))
                htmltext = "30614-06.htm";
        } else if (npcId == Ixia) {
            if (cond == 5) {
                st.takeItems(HerbalismTextbook);
                st.giveItems(IxiasList);
                htmltext = "30463-01.htm";
                st.setCond(6);
                st.start();
            } else if (cond == 6)
                htmltext = "30463-02.htm";
            else if (cond == 7 && st.getQuestItemsCount(MedusasIchor) >= 10 && st.getQuestItemsCount(MarshSpiderFluids) >= 10 && st.getQuestItemsCount(DeadSeekerDung) >= 10 && st.getQuestItemsCount(TyrantsBlood) >= 10 && st.getQuestItemsCount(NightshadeRoot) >= 10) {
                st.takeItems(MedusasIchor);
                st.takeItems(MarshSpiderFluids);
                st.takeItems(DeadSeekerDung);
                st.takeItems(TyrantsBlood);
                st.takeItems(NightshadeRoot);
                st.takeItems(IxiasList);
                st.giveItems(Belladonna);
                htmltext = "30463-03.htm";
                st.setCond(8);
                st.start();
            } else if (cond == 7) //На случай если игрок удалит квест айтемы.
            {
                htmltext = "30463-02.htm";
                st.setCond(6);
            } else if (cond == 8)
                htmltext = "30463-04.htm";
            else if (st.haveAnyQuestItems(AldersSkull1, AldersSkull2, AldersReceipt, RevelationsManuscript, KairasInstructions, KairasRecommendation))
                htmltext = "30463-05.htm";
        } else if (npcId == AldersSpirit) {
            htmltext = "30613-02.htm";
            st.setCond(11);
            st.start();
            st.cancelQuestTimer("AldersSpirit_Fail");
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(AldersSpirit);
            if (isQuest != null)
                isQuest.deleteMe();
        } else if (npcId == Roa) {
            if (cond == 11)
                htmltext = "30114-01.htm";
            else if (cond == 12)
                htmltext = "30114-05.htm";
            else if (st.haveAnyQuestItems(RevelationsManuscript, KairasInstructions, KairasRecommendation))
                htmltext = "30114-06.htm";
        } else if (npcId == Norman) {
            if (cond == 12) {
                st.takeItems(AldersReceipt);
                st.giveItems(RevelationsManuscript);
                htmltext = "30210-01.htm";
                st.setCond(13);
                st.start();
            } else if (cond == 13)
                htmltext = "30210-02.htm";
        } else if (npcId == Thifiell) {
            if (cond == 15) {
                st.takeItems(KairasRecommendation);
                st.giveItems(ThifiellsLetter);
                st.giveItems(PalusCharm);
                htmltext = "30358-01.htm";
                st.setCond(16);
                st.start();
            } else if (cond == 16)
                htmltext = "30358-02.htm";
            else if (cond == 17)
                htmltext = "30358-03.htm";
            else if (cond == 18) {
                if (!st.player.isVarSet("prof2.2")) {
                    st.addExpAndSp(682735, 45562);
                    st.giveItems(ADENA_ID, 123854);
                    st.player.setVar("prof2.2");
                }
                st.takeItems(ArkeniasLetter);
                st.takeItems(PalusCharm);
                st.giveItems(MarkofFate);
                htmltext = "30358-04.htm";
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            }
        } else if (npcId == Arkenia) {
            if (cond == 16)
                htmltext = "30419-01.htm";
            else if (cond == 17) {
                if (st.haveAllQuestItems(RedFairyDust, BlightTreantSap))
                    htmltext = "30419-04.htm";
                else
                    htmltext = "30419-03.htm";
            } else if (cond == 18)
                htmltext = "30419-06.htm";
        } else if (npcId == BloodyPixy && cond == 17) {
            if (st.getQuestItemsCount(RedFairyDust) == 0 && st.getQuestItemsCount(PixyGarnet) == 0)
                htmltext = "31845-01.htm";
            else if (st.getQuestItemsCount(RedFairyDust) == 0 && st.getQuestItemsCount(PixyGarnet) > 0 && (st.getQuestItemsCount(GrandissSkull) < 10 || st.getQuestItemsCount(KarulBugbearSkull) < 10 || st.getQuestItemsCount(BrekaOverlordSkull) < 10 || st.getQuestItemsCount(LetoOverlordSkull) < 10))
                htmltext = "31845-03.htm";
            else if (st.getQuestItemsCount(RedFairyDust) == 0 && st.getQuestItemsCount(PixyGarnet) > 0 && st.getQuestItemsCount(GrandissSkull) >= 10 && st.getQuestItemsCount(KarulBugbearSkull) >= 10 && st.getQuestItemsCount(BrekaOverlordSkull) >= 10 && st.getQuestItemsCount(LetoOverlordSkull) >= 10) {
                st.takeItems(GrandissSkull);
                st.takeItems(KarulBugbearSkull);
                st.takeItems(BrekaOverlordSkull);
                st.takeItems(LetoOverlordSkull);
                st.takeItems(PixyGarnet);
                st.giveItems(RedFairyDust);
                htmltext = "31845-04.htm";
            } else if (st.haveQuestItem(RedFairyDust))
                htmltext = "31845-05.htm";
        } else if (npcId == BlightTreant && cond == 17)
            if (st.getQuestItemsCount(BlightTreantSap) == 0 && st.getQuestItemsCount(BlightTreantSeed) == 0)
                htmltext = "31850-01.htm";
            else if (st.getQuestItemsCount(BlightTreantSap) == 0 && st.haveQuestItem(BlightTreantSeed) && st.getQuestItemsCount(BlackWillowLeaf) == 0)
                htmltext = "31850-03.htm";
            else if (st.getQuestItemsCount(BlightTreantSap) == 0 && st.haveQuestItem(BlightTreantSeed) && st.getQuestItemsCount(BlackWillowLeaf) > 0) {
                st.takeItems(BlackWillowLeaf);
                st.takeItems(BlightTreantSeed);
                st.giveItems(BlightTreantSap);
                htmltext = "31850-04.htm";
            } else if (st.haveQuestItem(BlightTreantSap))
                htmltext = "31850-05.htm";
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
        if (cond == 2 && npcId == HangmanTree) {
            st.takeItems(MetheussFuneralJar);
            st.giveItems(KasandrasRemains);
            st.playSound(SOUND_MIDDLE);
            st.setCond(3);
            st.start();
        } else if (cond == 6 && st.getQuestItemsCount(MedusasIchor) >= 10 && st.getQuestItemsCount(MarshSpiderFluids) >= 10 && st.getQuestItemsCount(DeadSeekerDung) >= 10 && st.getQuestItemsCount(TyrantsBlood) >= 10 && st.getQuestItemsCount(NightshadeRoot) >= 10) {
            st.setCond(7);
            st.start();
        }
    }
}