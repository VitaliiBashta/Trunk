package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _222_TestOfDuelist extends Quest {
    //NPC
    private static final int Kaien = 30623;
    //Quest items
    private static final int OrderGludio = 2763;
    private static final int OrderDion = 2764;
    private static final int OrderGiran = 2765;
    private static final int OrderOren = 2766;
    private static final int OrderAden = 2767;
    private static final int PunchersShard = 2768;
    private static final int NobleAntsFeeler = 2769;
    private static final int DronesChitin = 2770;
    private static final int DeadSeekerFang = 2771;
    private static final int OverlordNecklace = 2772;
    private static final int FetteredSoulsChain = 2773;
    private static final int ChiefsAmulet = 2774;
    private static final int EnchantedEyeMeat = 2775;
    private static final int TamrinOrcsRing = 2776;
    private static final int TamrinOrcsArrow = 2777;
    private static final int FinalOrder = 2778;
    private static final int ExcurosSkin = 2779;
    private static final int KratorsShard = 2780;
    private static final int GrandisSkin = 2781;
    private static final int TimakOrcsBelt = 2782;
    private static final int LakinsMace = 2783;
    //items
    private static final int MarkOfDuelist = 2762;
    //MOB
    private static final int Puncher = 20085;
    private static final int NobleAntLeader = 20090;
    private static final int MarshStakatoDrone = 20234;
    private static final int DeadSeeker = 20202;
    private static final int BrekaOrcOverlord = 20270;
    private static final int FetteredSoul = 20552;
    private static final int LetoLizardmanOverlord = 20582;
    private static final int EnchantedMonstereye = 20564;
    private static final int TamlinOrc = 20601;
    private static final int TamlinOrcArcher = 20602;
    private static final int Excuro = 20214;
    private static final int Krator = 20217;
    private static final int Grandis = 20554;
    private static final int TimakOrcOverlord = 20588;
    private static final int Lakin = 20604;
    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    2,
                    0,
                    Puncher,
                    0,
                    PunchersShard,
                    10,
                    70,
                    1
            },
            {
                    2,
                    0,
                    NobleAntLeader,
                    0,
                    NobleAntsFeeler,
                    10,
                    70,
                    1
            },
            {
                    2,
                    0,
                    MarshStakatoDrone,
                    0,
                    DronesChitin,
                    10,
                    70,
                    1
            },
            {
                    2,
                    0,
                    DeadSeeker,
                    0,
                    DeadSeekerFang,
                    10,
                    70,
                    1
            },
            {
                    2,
                    0,
                    BrekaOrcOverlord,
                    0,
                    OverlordNecklace,
                    10,
                    70,
                    1
            },
            {
                    2,
                    0,
                    FetteredSoul,
                    0,
                    FetteredSoulsChain,
                    10,
                    70,
                    1
            },
            {
                    2,
                    0,
                    LetoLizardmanOverlord,
                    0,
                    ChiefsAmulet,
                    10,
                    70,
                    1
            },
            {
                    2,
                    0,
                    EnchantedMonstereye,
                    0,
                    EnchantedEyeMeat,
                    10,
                    70,
                    1
            },
            {
                    2,
                    0,
                    TamlinOrc,
                    0,
                    TamrinOrcsRing,
                    10,
                    70,
                    1
            },
            {
                    2,
                    0,
                    TamlinOrcArcher,
                    0,
                    TamrinOrcsArrow,
                    10,
                    70,
                    1
            },
            {
                    4,
                    0,
                    Excuro,
                    0,
                    ExcurosSkin,
                    3,
                    70,
                    1
            },
            {
                    4,
                    0,
                    Krator,
                    0,
                    KratorsShard,
                    3,
                    70,
                    1
            },
            {
                    4,
                    0,
                    Grandis,
                    0,
                    GrandisSkin,
                    3,
                    70,
                    1
            },
            {
                    4,
                    0,
                    TimakOrcOverlord,
                    0,
                    TimakOrcsBelt,
                    3,
                    70,
                    1
            },
            {
                    4,
                    0,
                    Lakin,
                    0,
                    LakinsMace,
                    3,
                    70,
                    1
            }
    };

    public _222_TestOfDuelist() {
        super(false);
        addStartNpc(Kaien);
        //mob Drop
        for (int[] aDROPLIST_COND : DROPLIST_COND) {
            addKillId(aDROPLIST_COND[2]);
            addQuestItem(aDROPLIST_COND[4]);
        }
        addQuestItem(OrderGludio, OrderDion, OrderGiran, OrderOren, OrderAden, FinalOrder);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("30623-04.htm".equalsIgnoreCase(event) && st.player.getRace() == Race.orc)
            htmltext = "30623-05.htm";
        else if ("30623-07.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.start();
            st.giveItems(OrderGludio);
            st.giveItems(OrderDion);
            st.giveItems(OrderGiran);
            st.giveItems(OrderOren);
            st.giveItems(OrderAden);
            if (!st.player.isVarSet("dd3")) {
                st.giveItems(7562, 72, false);
                st.player.setVar("dd3");
            }
            st.playSound(SOUND_ACCEPT);
        } else if ("30623-16.htm".equalsIgnoreCase(event)) {
            st.takeAllItems(PunchersShard,NobleAntsFeeler,DronesChitin,DeadSeekerFang,OverlordNecklace,
                    FetteredSoulsChain,ChiefsAmulet,EnchantedEyeMeat,TamrinOrcsRing,TamrinOrcsArrow,
                    OrderGludio,OrderDion,OrderGiran,OrderOren,OrderAden);
            st.giveItems(FinalOrder);
            st.setCond(4);
            st.start();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Kaien)
            if (st.haveQuestItem(MarkOfDuelist)) {
                htmltext = "completed";
                st.exitCurrentQuest();
            } else if (cond == 0) {
                if (st.player.getClassId().id == 0x01 || st.player.getClassId().id == 0x2f || st.player.getClassId().id == 0x13 || st.player.getClassId().id == 0x20) {
                    if (st.player.getLevel() >= 39)
                        htmltext = "30623-03.htm";
                    else {
                        htmltext = "30623-01.htm";
                        st.exitCurrentQuest();
                    }
                } else {
                    htmltext = "30623-02.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 2)
                htmltext = "30623-14.htm";
            else if (cond == 3)
                htmltext = "30623-13.htm";
            else if (cond == 4)
                htmltext = "30623-17.htm";
            else if (cond == 5) {
                st.giveItems(MarkOfDuelist);
                if (!st.player.isVarSet("prof2.3")) {
                    st.addExpAndSp(474444, 30704);
                    st.giveItems(ADENA_ID, 80000);
                    st.player.setVar("prof2.3");
                }
                htmltext = "30623-18.htm";
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
        if (cond == 2 && st.getQuestItemsCount(PunchersShard) >= 10 && st.getQuestItemsCount(NobleAntsFeeler) >= 10 && st.getQuestItemsCount(DronesChitin) >= 10 && st.getQuestItemsCount(DeadSeekerFang) >= 10 && st.getQuestItemsCount(OverlordNecklace) >= 10 && st.getQuestItemsCount(FetteredSoulsChain) >= 10 && st.getQuestItemsCount(ChiefsAmulet) >= 10 && st.getQuestItemsCount(EnchantedEyeMeat) >= 10 && st.getQuestItemsCount(TamrinOrcsRing) >= 10 && st.getQuestItemsCount(TamrinOrcsArrow) >= 10) {
            st.setCond(3);
            st.start();
        } else if (cond == 4 && st.getQuestItemsCount(ExcurosSkin) >= 3 && st.getQuestItemsCount(KratorsShard) >= 3 && st.getQuestItemsCount(LakinsMace) >= 3 && st.getQuestItemsCount(GrandisSkin) >= 3 && st.getQuestItemsCount(TimakOrcsBelt) >= 3) {
            st.setCond(5);
            st.start();
        }
    }
}