package l2trunk.scripts.quests;

import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import static l2trunk.gameserver.model.base.ClassId.*;

public final class _220_TestimonyOfGlory extends Quest {
    //NPC
    private static final int Vokian = 30514;
    private static final int Chianta = 30642;
    private static final int Manakia = 30515;
    private static final int Kasman = 30501;
    private static final int Voltar = 30615;
    private static final int Kepra = 30616;
    private static final int Burai = 30617;
    private static final int Harak = 30618;
    private static final int Driko = 30619;
    private static final int Tanapi = 30571;
    private static final int Kakai = 30565;
    //Quest items
    private static final int VokiansOrder = 3204;
    private static final int ManashenShard = 3205;
    private static final int TyrantTalon = 3206;
    private static final int GuardianBasiliskFang = 3207;
    private static final int VokiansOrder2 = 3208;
    private static final int NecklaceOfAuthority = 3209;
    private static final int ChiantaOrder1st = 3210;
    private static final int ScepterOfBreka = 3211;
    private static final int ScepterOfEnku = 3212;
    private static final int ScepterOfVuku = 3213;
    private static final int ScepterOfTurek = 3214;
    private static final int ScepterOfTunath = 3215;
    private static final int ChiantasOrder2rd = 3216;
    private static final int ChiantasOrder3rd = 3217;
    private static final int TamlinOrcSkull = 3218;
    private static final int TimakOrcHead = 3219;
    private static final int ScepterBox = 3220;
    private static final int PashikasHead = 3221;
    private static final int VultusHead = 3222;
    private static final int GloveOfVoltar = 3223;
    private static final int EnkuOverlordHead = 3224;
    private static final int GloveOfKepra = 3225;
    private static final int MakumBugbearHead = 3226;
    private static final int GloveOfBurai = 3227;
    private static final int ManakiaLetter1st = 3228;
    private static final int ManakiaLetter2st = 3229;
    private static final int KasmansLetter1rd = 3230;
    private static final int KasmansLetter2rd = 3231;
    private static final int KasmansLetter3rd = 3232;
    private static final int DrikosContract = 3233;
    private static final int StakatoDroneHusk = 3234;
    private static final int TanapisOrder = 3235;
    private static final int ScepterOfTantos = 3236;
    private static final int RitualBox = 3237;
    //items
    private static final int MarkOfGlory = 3203;
    //MOB
    private static final int Tyrant = 20192;
    private static final int TyrantKingpin = 20193;
    private static final int GuardianBasilisk = 20550;
    private static final int ManashenGargoyle = 20563;
    private static final int MarshStakatoDrone = 20234;
    private static final int PashikasSonOfVoltarQuestMonster = 27080;
    private static final int VultusSonOfVoltarQuestMonster = 27081;
    private static final int EnkuOrcOverlordQuestMonster = 27082;
    private static final int MakumBugbearThugQuestMonster = 27083;
    private static final int TimakOrc = 20583;
    private static final int TimakOrcArcher = 20584;
    private static final int TimakOrcSoldier = 20585;
    private static final int TimakOrcWarrior = 20586;
    private static final int TimakOrcShaman = 20587;
    private static final int TimakOrcOverlord = 20588;
    private static final int TamlinOrc = 20601;
    private static final int TamlinOrcArcher = 20602;
    private static final int RagnaOrcOverlord = 20778;
    private static final int RagnaOrcSeer = 20779;
    private static final int RevenantOfTantosChief = 27086;

    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    1,
                    0,
                    ManashenGargoyle,
                    VokiansOrder,
                    ManashenShard,
                    10,
                    70,
                    1
            },
            {
                    1,
                    0,
                    Tyrant,
                    VokiansOrder,
                    TyrantTalon,
                    10,
                    70,
                    1
            },
            {
                    1,
                    0,
                    TyrantKingpin,
                    VokiansOrder,
                    TyrantTalon,
                    10,
                    70,
                    1
            },
            {
                    1,
                    0,
                    GuardianBasilisk,
                    VokiansOrder,
                    GuardianBasiliskFang,
                    10,
                    70,
                    1
            },
            {
                    4,
                    0,
                    MarshStakatoDrone,
                    DrikosContract,
                    StakatoDroneHusk,
                    30,
                    70,
                    1
            },
            {
                    4,
                    0,
                    EnkuOrcOverlordQuestMonster,
                    GloveOfKepra,
                    EnkuOverlordHead,
                    4,
                    100,
                    1
            },
            {
                    4,
                    0,
                    MakumBugbearThugQuestMonster,
                    GloveOfBurai,
                    MakumBugbearHead,
                    2,
                    100,
                    1
            },
            {
                    6,
                    0,
                    TimakOrc,
                    ChiantasOrder3rd,
                    TimakOrcHead,
                    20,
                    50,
                    1
            },
            {
                    6,
                    0,
                    TimakOrcArcher,
                    ChiantasOrder3rd,
                    TimakOrcHead,
                    20,
                    60,
                    1
            },
            {
                    6,
                    0,
                    TimakOrcSoldier,
                    ChiantasOrder3rd,
                    TimakOrcHead,
                    20,
                    70,
                    1
            },
            {
                    6,
                    0,
                    TimakOrcWarrior,
                    ChiantasOrder3rd,
                    TimakOrcHead,
                    20,
                    80,
                    1
            },
            {
                    6,
                    0,
                    TimakOrcShaman,
                    ChiantasOrder3rd,
                    TimakOrcHead,
                    20,
                    90,
                    1
            },
            {
                    6,
                    0,
                    TimakOrcOverlord,
                    ChiantasOrder3rd,
                    TimakOrcHead,
                    20,
                    100,
                    1
            },
            {
                    6,
                    0,
                    TamlinOrc,
                    ChiantasOrder3rd,
                    TamlinOrcSkull,
                    20,
                    50,
                    1
            },
            {
                    6,
                    0,
                    TamlinOrcArcher,
                    ChiantasOrder3rd,
                    TamlinOrcSkull,
                    20,
                    60,
                    1
            }
    };

    public _220_TestimonyOfGlory() {
        addStartNpc(Vokian);
        addTalkId(Chianta, Manakia, Kasman, Voltar, Kepra, Burai, Harak, Driko, Tanapi, Kakai);

        for (int[] aDROPLIST_COND : DROPLIST_COND) addKillId(aDROPLIST_COND[2]);

        addKillId(PashikasSonOfVoltarQuestMonster, VultusSonOfVoltarQuestMonster,
                RagnaOrcOverlord, RagnaOrcSeer, RevenantOfTantosChief);

        addQuestItem(VokiansOrder,
                VokiansOrder2,
                NecklaceOfAuthority,
                ChiantaOrder1st,
                ManakiaLetter1st,
                ManakiaLetter2st,
                KasmansLetter1rd,
                KasmansLetter2rd,
                KasmansLetter3rd,
                ScepterOfBreka,
                PashikasHead,
                VultusHead,
                GloveOfVoltar,
                GloveOfKepra,
                ScepterOfEnku,
                ScepterOfTurek,
                GloveOfBurai,
                ScepterOfTunath,
                DrikosContract,
                ChiantasOrder2rd,
                ChiantasOrder3rd,
                ScepterBox,
                TanapisOrder,
                ScepterOfTantos,
                RitualBox,
                ManashenShard,
                TyrantTalon,
                GuardianBasiliskFang,
                StakatoDroneHusk,
                EnkuOverlordHead,
                MakumBugbearHead,
                TimakOrcHead,
                TamlinOrcSkull);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("RETURN".equalsIgnoreCase(event))
            return null;
        else if ("30514-05.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.giveItems(VokiansOrder);
            if (!st.player.isVarSet("dd2")) {
                st.giveItems(7562, 102);
                st.player.setVar("dd2");
            }
            st.playSound(SOUND_ACCEPT);
        } else if ("30642-03.htm".equalsIgnoreCase(event)) {
            st.takeItems(VokiansOrder2);
            st.giveItems(ChiantaOrder1st);
            st.setCond(4);
            st.start();
        } else if ("30571-03.htm".equalsIgnoreCase(event)) {
            st.takeItems(ScepterBox);
            st.giveItems(TanapisOrder);
            st.setCond(9);
            st.start();
        } else if ("30642-07.htm".equalsIgnoreCase(event)) {
            st.takeAllItems(ScepterOfBreka, ScepterOfEnku, ScepterOfVuku, ScepterOfTurek, ScepterOfTunath, ChiantaOrder1st);
            if (st.player.getLevel() >= 38) {
                st.giveItems(ChiantasOrder3rd);
                st.setCond(6);
                st.start();
            } else {
                htmltext = "30642-06.htm";
                st.giveItems(ChiantasOrder2rd);
            }
        } else if ("BREKA".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(ScepterOfBreka))
                htmltext = "30515-02.htm";
            else if (st.haveQuestItem(ManakiaLetter1st))
                htmltext = "30515-04.htm";
            else {
                htmltext = "30515-03.htm";
                st.giveItems(ManakiaLetter1st);
            }
        } else if ("ENKU".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(ScepterOfEnku))
                htmltext = "30515-05.htm";
            else if (st.haveQuestItem(ManakiaLetter2st))
                htmltext = "30515-07.htm";
            else {
                htmltext = "30515-06.htm";
                st.giveItems(ManakiaLetter2st);
            }
        } else if ("VUKU".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(ScepterOfVuku))
                htmltext = "30501-02.htm";
            else if (st.getQuestItemsCount(KasmansLetter1rd) > 0)
                htmltext = "30501-04.htm";
            else {
                htmltext = "30501-03.htm";
                st.giveItems(KasmansLetter1rd);
            }
        } else if ("TUREK".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(ScepterOfTurek))
                htmltext = "30501-05.htm";
            else if (st.haveQuestItem(KasmansLetter2rd))
                htmltext = "30501-07.htm";
            else {
                htmltext = "30501-06.htm";
                st.giveItems(KasmansLetter2rd);
            }
        } else if ("TUNATH".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(ScepterOfTunath))
                htmltext = "30501-08.htm";
            else if (st.haveQuestItem(KasmansLetter3rd))
                htmltext = "30501-10.htm";
            else {
                htmltext = "30501-09.htm";
                st.giveItems(KasmansLetter3rd);
            }
        } else if ("30615-04.htm".equalsIgnoreCase(event)) {
            //Проверяем есть ли в мире уже квест монстры
            int spawn = 0;
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(PashikasSonOfVoltarQuestMonster);
            if (isQuest != null)
                spawn = 1;
            isQuest = GameObjectsStorage.getByNpcId(VultusSonOfVoltarQuestMonster);
            if (isQuest != null)
                spawn = 1;
            if (spawn == 1) //если хоть один моб есть в мире, ставим таймер на удаление их(ня всякий) + говорим игроку подождать.
            {
                if (!st.isRunningQuestTimer("Wait1"))
                    st.startQuestTimer("Wait1", 300000);
                htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
            } else {
                st.takeItems(ManakiaLetter1st);
                st.giveItems(GloveOfVoltar);
                st.cancelQuestTimer("Wait1");
                st.startQuestTimer("PashikasSonOfVoltarQuestMonster", 200000);
                st.startQuestTimer("VultusSonOfVoltarQuestMonster", 200000);
                st.addSpawn(PashikasSonOfVoltarQuestMonster);
                st.addSpawn(VultusSonOfVoltarQuestMonster);
                st.playSound(SOUND_BEFORE_BATTLE);
            }
        } else if (event.equalsIgnoreCase("30616-04.htm")) {
            //Проверяем есть ли в мире уже квест монстры
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(EnkuOrcOverlordQuestMonster);
            if (isQuest != null) {
                if (!st.isRunningQuestTimer("Wait2"))
                    st.startQuestTimer("Wait2", 300000);
                htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
            } else {
                st.takeItems(ManakiaLetter2st);
                st.giveItems(GloveOfKepra);
                st.cancelQuestTimer("Wait2");
                st.startQuestTimer("EnkuOrcOverlordQuestMonster", 200000);
                st.addSpawn(EnkuOrcOverlordQuestMonster);
                st.addSpawn(EnkuOrcOverlordQuestMonster);
                st.addSpawn(EnkuOrcOverlordQuestMonster);
                st.addSpawn(EnkuOrcOverlordQuestMonster);
                st.playSound(SOUND_BEFORE_BATTLE);
            }
        } else if ("30617-04.htm".equalsIgnoreCase(event)) {
            //Проверяем есть ли в мире уже квест монстры
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(MakumBugbearThugQuestMonster);
            if (isQuest != null) {
                if (!st.isRunningQuestTimer("Wait3"))
                    st.startQuestTimer("Wait3", 300000);
                htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
            } else {
                st.takeItems(KasmansLetter2rd);
                st.giveItems(GloveOfBurai);
                st.cancelQuestTimer("Wait3");
                st.startQuestTimer("MakumBugbearThugQuestMonster", 200000);
                st.addSpawn(MakumBugbearThugQuestMonster);
                st.addSpawn(MakumBugbearThugQuestMonster);
                st.playSound(SOUND_BEFORE_BATTLE);
            }

        } else if (event.equalsIgnoreCase("30618-03.htm")) {
            st.takeItems(KasmansLetter3rd);
            st.giveItems(ScepterOfTunath);
            if (st.haveAllQuestItems(ScepterOfBreka, ScepterOfEnku, ScepterOfVuku, ScepterOfTurek, ScepterOfTunath)) {
                st.setCond(5);
                st.start();
            }
        } else if ("30619-03.htm".equalsIgnoreCase(event)) {
            st.takeItems(KasmansLetter1rd);
            st.giveItems(DrikosContract);
        }
        //Далее идет 3 велосипеда
        else if ("Wait1".equalsIgnoreCase(event) || "PashikasSonOfVoltarQuestMonster".equalsIgnoreCase(event) || "VultusSonOfVoltarQuestMonster".equalsIgnoreCase(event)) {
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(PashikasSonOfVoltarQuestMonster);
            if (isQuest != null)
                isQuest.deleteMe();
            isQuest = GameObjectsStorage.getByNpcId(VultusSonOfVoltarQuestMonster);
            if (isQuest != null)
                isQuest.deleteMe();
            st.cancelQuestTimer("Wait1");
            st.cancelQuestTimer("PashikasSonOfVoltarQuestMonster");
        } else if ("Wait2".equalsIgnoreCase(event) || "EnkuOrcOverlordQuestMonster".equalsIgnoreCase(event)) {
            //Велосипед, но нужно удалить всех 4 одинаковых мобов
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(EnkuOrcOverlordQuestMonster);
            if (isQuest != null)
                isQuest.deleteMe();
            isQuest = GameObjectsStorage.getByNpcId(EnkuOrcOverlordQuestMonster);
            if (isQuest != null)
                isQuest.deleteMe();
            isQuest = GameObjectsStorage.getByNpcId(EnkuOrcOverlordQuestMonster);
            if (isQuest != null)
                isQuest.deleteMe();
            isQuest = GameObjectsStorage.getByNpcId(EnkuOrcOverlordQuestMonster);
            if (isQuest != null)
                isQuest.deleteMe();
            st.cancelQuestTimer("Wait2");
            st.cancelQuestTimer("EnkuOrcOverlordQuestMonster");
        } else if ("Wait3".equalsIgnoreCase(event) || "MakumBugbearThugQuestMonster".equalsIgnoreCase(event)) {
            //Велосипед, но нужно удалить всех 2 одинаковых мобов
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(MakumBugbearThugQuestMonster);
            if (isQuest != null)
                isQuest.deleteMe();
            isQuest = GameObjectsStorage.getByNpcId(MakumBugbearThugQuestMonster);
            if (isQuest != null)
                isQuest.deleteMe();
            st.cancelQuestTimer("Wait3");
            st.cancelQuestTimer("MakumBugbearThugQuestMonster");
        } else if ("Wait4".equalsIgnoreCase(event) || "RevenantOfTantosChief".equalsIgnoreCase(event)) {
            //Тележка...
            NpcInstance isQuest = GameObjectsStorage.getByNpcId(RevenantOfTantosChief);
            if (isQuest != null)
                isQuest.deleteMe();
            st.cancelQuestTimer("Wait4");
            st.cancelQuestTimer("RevenantOfTantosChief");
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Vokian) {
            if (st.haveAnyQuestItems(MarkOfGlory)) {
                htmltext = "completed";
                st.exitCurrentQuest();
            } else if (cond == 0) {
                if (st.player.getClassId() == orcRaider
                        || st.player.getClassId() == orcMonk
                        || st.player.getClassId() == orcShaman) {
                    if (st.player.getLevel() >= 37)
                        htmltext = "30514-03.htm";
                    else {
                        htmltext = "30514-01.htm";
                        st.exitCurrentQuest();
                    }
                } else {
                    htmltext = "30514-02.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "30514-06.htm";
            else if (cond == 2) {
                st.takeAllItems(VokiansOrder, ManashenShard, TyrantTalon, GuardianBasiliskFang);
                st.giveItems(VokiansOrder2);
                st.giveItems(NecklaceOfAuthority);
                htmltext = "30514-08.htm";
                st.setCond(3);
                st.start();
            } else if (cond == 3)
                htmltext = "30514-09.htm";
            else if (cond == 4)
                htmltext = "30514-10.htm";
        } else if (npcId == Chianta) {
            if (cond == 3)
                htmltext = "30642-01.htm";
            else if (cond == 4)
                htmltext = "30642-04.htm";
            else if (cond == 5) {
                if (st.haveQuestItem(ChiantaOrder1st))
                    htmltext = "30642-05.htm";
                else if (st.haveQuestItem(ChiantasOrder2rd))
                    if (st.player.getLevel() >= 38) {
                        st.takeItems(ChiantasOrder2rd);
                        st.giveItems(ChiantasOrder3rd);
                        htmltext = "30642-09.htm";
                        st.setCond(6);
                        st.start();
                    } else
                        htmltext = "30642-08.htm";
            } else if (cond == 6)
                htmltext = "30642-10.htm";
            else if (cond == 7) {
                st.takeAllItems(NecklaceOfAuthority, ChiantasOrder3rd, TamlinOrcSkull, TimakOrcHead);
                st.giveItems(ScepterBox);
                htmltext = "30642-11.htm";
                st.setCond(8);
                st.start();
            } else if (cond == 8)
                htmltext = "30642-12.htm";
        } else if (npcId == Manakia) {
            if (cond == 4)
                htmltext = "30515-01.htm";
        } else if (npcId == Kasman) {
            if (cond == 4)
                htmltext = "30501-01.htm";
        } else if (npcId == Voltar) {
            if (cond == 4)
                if (st.getQuestItemsCount(ManakiaLetter1st) > 0)
                    htmltext = "30615-02.htm";
                else if (st.haveQuestItem(GloveOfVoltar) && (st.getQuestItemsCount(PashikasHead) == 0 || st.getQuestItemsCount(VultusHead) == 0)) {
                    htmltext = "30615-05.htm";
                    int sound = 0;
                    NpcInstance isQuest = GameObjectsStorage.getByNpcId(PashikasSonOfVoltarQuestMonster);
                    if (isQuest == null) {
                        sound = 1;
                        st.addSpawn(PashikasSonOfVoltarQuestMonster);
                        st.startQuestTimer("PashikasSonOfVoltarQuestMonster", 200000);
                    }
                    isQuest = GameObjectsStorage.getByNpcId(VultusSonOfVoltarQuestMonster);
                    if (isQuest == null) {
                        sound = 1;
                        st.addSpawn(VultusSonOfVoltarQuestMonster);
                        st.startQuestTimer("VultusSonOfVoltarQuestMonster", 200000);
                    }
                    if (sound == 1) {
                        st.playSound(SOUND_BEFORE_BATTLE);
                        st.cancelQuestTimer("Wait1");
                    } else {
                        st.startQuestTimer("Wait1", 300000);
                        htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
                    }
                } else if (st.haveAllQuestItems(PashikasHead, VultusHead)) {
                    st.takeAllItems(PashikasHead, VultusHead, GloveOfVoltar);
                    st.giveItems(ScepterOfBreka);
                    htmltext = "30615-06.htm";
                    if (st.haveAllQuestItems(ScepterOfBreka, ScepterOfEnku, ScepterOfVuku, ScepterOfTurek, ScepterOfTunath)) {
                        st.setCond(5);
                        st.start();
                        st.playSound(SOUND_MIDDLE);
                    } else
                        st.playSound(SOUND_ITEMGET);
                } else if (st.haveQuestItem(ScepterOfBreka))
                    htmltext = "30615-07.htm";
                else
                    htmltext = "30615-01.htm";
        } else if (npcId == Kepra) {
            if (cond == 4)
                if (st.getQuestItemsCount(ManakiaLetter2st) > 0)
                    htmltext = "30616-02.htm";
                else if (st.getQuestItemsCount(GloveOfKepra) > 0 && st.getQuestItemsCount(EnkuOverlordHead) < 4) {
                    htmltext = "30616-05.htm";
                    NpcInstance isQuest = GameObjectsStorage.getByNpcId(EnkuOrcOverlordQuestMonster);
                    if (isQuest != null) {
                        st.startQuestTimer("Wait2", 300000);
                        htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
                    } else {
                        st.cancelQuestTimer("Wait2");
                        st.startQuestTimer("EnkuOrcOverlordQuestMonster", 200000);
                        st.addSpawn(EnkuOrcOverlordQuestMonster);
                        st.addSpawn(EnkuOrcOverlordQuestMonster);
                        st.addSpawn(EnkuOrcOverlordQuestMonster);
                        st.addSpawn(EnkuOrcOverlordQuestMonster);
                        st.playSound(SOUND_BEFORE_BATTLE);
                    }
                } else if (st.getQuestItemsCount(EnkuOverlordHead) >= 4) {
                    htmltext = "30616-06.htm";
                    st.takeAllItems(EnkuOverlordHead, GloveOfKepra);
                    st.giveItems(ScepterOfEnku);
                    if (st.haveAllQuestItems(ScepterOfBreka, ScepterOfEnku, ScepterOfVuku, ScepterOfTurek, ScepterOfTunath)) {
                        st.setCond(5);
                        st.start();
                        st.playSound(SOUND_MIDDLE);
                    } else
                        st.playSound(SOUND_ITEMGET);
                } else if (st.getQuestItemsCount(ScepterOfEnku) > 0)
                    htmltext = "30616-07.htm";
                else
                    htmltext = "30616-01.htm";
        } else if (npcId == Burai) {
            if (cond == 4)
                if (st.getQuestItemsCount(KasmansLetter2rd) > 0)
                    htmltext = "30617-02.htm";
                else if (st.getQuestItemsCount(GloveOfBurai) > 0 && st.getQuestItemsCount(MakumBugbearHead) < 2) {
                    htmltext = "30617-05.htm";
                    NpcInstance isQuest = GameObjectsStorage.getByNpcId(MakumBugbearThugQuestMonster);
                    if (isQuest != null) {
                        st.startQuestTimer("Wait3", 300000);
                        htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
                    } else {
                        st.cancelQuestTimer("Wait3");
                        st.startQuestTimer("MakumBugbearThugQuestMonster", 200000);
                        st.addSpawn(MakumBugbearThugQuestMonster);
                        st.addSpawn(MakumBugbearThugQuestMonster);
                        st.playSound(SOUND_BEFORE_BATTLE);
                    }
                } else if (st.getQuestItemsCount(MakumBugbearHead) == 2) {
                    htmltext = "30617-06.htm";
                    st.takeAllItems(MakumBugbearHead, GloveOfBurai);
                    st.giveItems(ScepterOfTurek);
                    if (st.haveAllQuestItems(ScepterOfBreka, ScepterOfEnku, ScepterOfVuku, ScepterOfTurek, ScepterOfTunath)) {
                        st.setCond(5);
                        st.start();
                        st.playSound(SOUND_MIDDLE);
                    } else
                        st.playSound(SOUND_ITEMGET);
                } else if (st.haveQuestItem(ScepterOfTurek))
                    htmltext = "30617-07.htm";
                else
                    htmltext = "30617-01.htm";
        } else if (npcId == Harak) {
            if (cond == 4)
                if (st.haveQuestItem(KasmansLetter3rd))
                    htmltext = "30618-02.htm";
                else if (st.getQuestItemsCount(ScepterOfTunath) > 0)
                    htmltext = "30618-04.htm";
                else
                    htmltext = "30618-01.htm";
        } else if (npcId == Driko) {
            if (cond == 4)
                if (st.getQuestItemsCount(KasmansLetter1rd) > 0)
                    htmltext = "30619-02.htm";
                else if (st.getQuestItemsCount(DrikosContract) > 0) {
                    if (st.getQuestItemsCount(StakatoDroneHusk) >= 30) {
                        htmltext = "30619-05.htm";
                        st.takeAllItems(StakatoDroneHusk, DrikosContract);
                        st.giveItems(ScepterOfVuku);
                        if (st.haveAllQuestItems(ScepterOfBreka, ScepterOfEnku, ScepterOfVuku, ScepterOfTurek, ScepterOfTunath)) {
                            st.setCond(5);
                            st.start();
                            st.playSound(SOUND_MIDDLE);
                        } else
                            st.playSound(SOUND_ITEMGET);
                    } else
                        htmltext = "30619-04.htm";
                } else if (st.haveQuestItem(ScepterOfVuku))
                    htmltext = "30619-06.htm";
                else
                    htmltext = "30619-01.htm";
        } else if (npcId == Tanapi)
            if (cond == 8)
                htmltext = "30571-01.htm";
            else if (cond == 9)
                htmltext = "30571-04.htm";
            else if (cond == 10) {
                st.takeItems(ScepterOfTantos, -1);
                st.takeItems(TanapisOrder, -1);
                st.giveItems(RitualBox, 1);
                htmltext = "30571-05.htm";
                st.setCond(11);
                st.start();
            } else if (cond == 11)
                htmltext = "30571-06.htm";
        if (npcId == Kakai && cond == 11) {
            st.takeItems(RitualBox);
            st.giveItems(MarkOfGlory);
            st.addExpAndSp(724113, 48324);
            st.giveAdena(131360);
            htmltext = "30565-02.htm";
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }

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
        if (cond == 1 && st.getQuestItemsCount(TyrantTalon) >= 10 && st.getQuestItemsCount(GuardianBasiliskFang) >= 10 && st.getQuestItemsCount(ManashenShard) >= 10) {
            st.setCond(2);
            st.start();
        } else if (cond == 4) {
            if (npcId == PashikasSonOfVoltarQuestMonster) {
                st.cancelQuestTimer("PashikasSonOfVoltarQuestMonster");
                NpcInstance isQuest = GameObjectsStorage.getByNpcId(PashikasSonOfVoltarQuestMonster);
                if (isQuest != null)
                    isQuest.deleteMe();
                if (st.getQuestItemsCount(GloveOfVoltar) > 0 && st.getQuestItemsCount(PashikasHead) == 0)
                    st.giveItems(PashikasHead);
            } else if (npcId == VultusSonOfVoltarQuestMonster) {
                st.cancelQuestTimer("VultusSonOfVoltarQuestMonster");
                NpcInstance isQuest = GameObjectsStorage.getByNpcId(VultusSonOfVoltarQuestMonster);
                if (isQuest != null)
                    isQuest.deleteMe();
                if (st.getQuestItemsCount(GloveOfVoltar) > 0 && st.getQuestItemsCount(VultusHead) == 0)
                    st.giveItems(VultusHead, 1);
            }
        } else if (cond == 6 && st.getQuestItemsCount(TimakOrcHead) >= 20 && st.getQuestItemsCount(TamlinOrcSkull) >= 20) {
            st.setCond(7);
            st.start();
        } else if (cond == 9)
            if (npcId == RagnaOrcOverlord || npcId == RagnaOrcSeer) {
                NpcInstance isQuest = GameObjectsStorage.getByNpcId(RevenantOfTantosChief);
                if (isQuest == null) {
                    st.startQuestTimer("RevenantOfTantosChief", 300000);
                    st.addSpawn(RevenantOfTantosChief);
                    st.playSound(SOUND_BEFORE_BATTLE);
                } else {
                    if (!st.isRunningQuestTimer("Wait4"))
                        st.startQuestTimer("Wait4", 300000);
                }
            } else if (npcId == RevenantOfTantosChief) {
                st.cancelQuestTimer("RevenantOfTantosChief");
                st.cancelQuestTimer("Wait4");
                NpcInstance isQuest = GameObjectsStorage.getByNpcId(RevenantOfTantosChief);
                if (isQuest != null)
                    isQuest.deleteMe();
                st.giveItems(ScepterOfTantos);
                st.setCond(10);
                st.start();
                st.playSound(SOUND_MIDDLE);
            }
    }
}