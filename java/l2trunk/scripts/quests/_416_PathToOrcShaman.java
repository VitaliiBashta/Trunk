package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.scripts.Functions;

import static l2trunk.gameserver.model.base.ClassId.orcMage;
import static l2trunk.gameserver.model.base.ClassId.orcShaman;

public final class _416_PathToOrcShaman extends Quest {
    //NPC
    private static final int Hestui = 30585;
    private static final int HestuiTotemSpirit = 30592;
    private static final int SeerUmos = 30502;
    private static final int DudaMaraTotemSpirit = 30593;
    private static final int SeerMoira = 31979;
    private static final int GandiTotemSpirit = 32057;
    private static final int LeopardCarcass = 32090;
    //Quest items
    private static final int FireCharm = 1616;
    private static final int KashaBearPelt = 1617;
    private static final int KashaBladeSpiderHusk = 1618;
    private static final int FieryEgg1st = 1619;
    private static final int HestuiMask = 1620;
    private static final int FieryEgg2nd = 1621;
    private static final int TotemSpiritClaw = 1622;
    private static final int TatarusLetterOfRecommendation = 1623;
    private static final int FlameCharm = 1624;
    private static final int GrizzlyBlood = 1625;
    private static final int BloodCauldron = 1626;
    private static final int SpiritNet = 1627;
    private static final int BoundDurkaSpirit = 1628;
    private static final int DurkaParasite = 1629;
    private static final int TotemSpiritBlood = 1630;
    //items
    private static final int MaskOfMedium = 1631;
    //MOB
    private static final int KashaBear = 20479;
    private static final int KashaBladeSpider = 20478;
    private static final int ScarletSalamander = 20415;
    private static final int GrizzlyBear = 20335;
    private static final int VenomousSpider = 20038;
    private static final int ArachnidTracker = 20043;
    private static final int QuestMonsterDurkaSpirit = 27056;
    private static final int QuestBlackLeopard = 27319;

    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    1,
                    0,
                    KashaBear,
                    FireCharm,
                    KashaBearPelt,
                    1,
                    70,
                    1
            },
            {
                    1,
                    0,
                    KashaBladeSpider,
                    FireCharm,
                    KashaBladeSpiderHusk,
                    1,
                    70,
                    1
            },
            {
                    1,
                    0,
                    ScarletSalamander,
                    FireCharm,
                    FieryEgg1st,
                    1,
                    70,
                    1
            },
            {
                    6,
                    7,
                    GrizzlyBear,
                    FlameCharm,
                    GrizzlyBlood,
                    3,
                    70,
                    1
            }
    };

    public _416_PathToOrcShaman() {
        addStartNpc(Hestui);

        addTalkId(HestuiTotemSpirit, SeerUmos, DudaMaraTotemSpirit, SeerMoira, GandiTotemSpirit, LeopardCarcass);

        //mob Drop
        for (int[] aDROPLIST_COND : DROPLIST_COND) {
            addKillId(aDROPLIST_COND[2]);
            addQuestItem(aDROPLIST_COND[4]);
        }

        addKillId(VenomousSpider, ArachnidTracker, QuestMonsterDurkaSpirit, QuestBlackLeopard);
        addQuestItem(FireCharm, HestuiMask, FieryEgg2nd, TotemSpiritClaw, TatarusLetterOfRecommendation, FlameCharm, BloodCauldron, SpiritNet, BoundDurkaSpirit, DurkaParasite, TotemSpiritBlood);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("tataru_zu_hestui_q0416_06.htm".equalsIgnoreCase(event)) {
            st.giveItems(FireCharm);
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("hestui_totem_spirit_q0416_03.htm".equalsIgnoreCase(event)) {
            st.takeAllItems(HestuiMask, FieryEgg2nd);
            st.giveItems(TotemSpiritClaw);
            st.setCond(4);
        } else if ("tataru_zu_hestui_q0416_11.htm".equalsIgnoreCase(event)) {
            st.takeItems(TotemSpiritClaw);
            st.giveItems(TatarusLetterOfRecommendation);
            st.setCond(5);
        } else if ("tataru_zu_hestui_q0416_11c.htm".equalsIgnoreCase(event)) {
            st.takeItems(TotemSpiritClaw);
            st.setCond(12);
        } else if ("dudamara_totem_spirit_q0416_03.htm".equalsIgnoreCase(event)) {
            st.takeItems(BloodCauldron);
            st.giveItems(SpiritNet);
            st.setCond(9);
        } else if ("seer_umos_q0416_07.htm".equalsIgnoreCase(event)) {
            st.takeItems(TotemSpiritBlood);
            if (st.player.getClassId().occupation() == 0) {
                st.giveItems(MaskOfMedium);
                st.addExpAndSp(228064, 16455);
                st.giveAdena(81900);
            }
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        } else if ("totem_spirit_gandi_q0416_02.htm".equalsIgnoreCase(event))
            st.setCond(14);
        else if ("dead_leopard_q0416_04.htm".equalsIgnoreCase(event))
            st.setCond(18);
        else if ("totem_spirit_gandi_q0416_05.htm".equalsIgnoreCase(event))
            st.setCond(21);
        if ("QuestMonsterDurkaSpirit_Fail".equalsIgnoreCase(event))
            GameObjectsStorage.getAllByNpcId(QuestMonsterDurkaSpirit, false)
                    .forEach(GameObject::deleteMe);
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Hestui) {
            if (st.haveQuestItem(MaskOfMedium)) {
                htmltext = "seer_umos_q0416_04.htm";
                st.exitCurrentQuest();
            } else if (cond == 0) {
                if (st.player.getClassId() != orcMage) {
                    if (st.player.getClassId() == orcShaman)
                        htmltext = "tataru_zu_hestui_q0416_02a.htm";
                    else
                        htmltext = "tataru_zu_hestui_q0416_02.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getLevel() < 18) {
                    htmltext = "tataru_zu_hestui_q0416_03.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "tataru_zu_hestui_q0416_01.htm";
            } else if (cond == 1)
                htmltext = "tataru_zu_hestui_q0416_07.htm";
            else if (cond == 2) {
                htmltext = "tataru_zu_hestui_q0416_08.htm";
                st.takeAllItems(KashaBearPelt, KashaBladeSpiderHusk, FieryEgg1st, FireCharm);
                st.giveItems(HestuiMask);
                st.giveItems(FieryEgg2nd);
                st.setCond(3);
            } else if (cond == 3)
                htmltext = "tataru_zu_hestui_q0416_09.htm";
            else if (cond == 4)
                htmltext = "tataru_zu_hestui_q0416_10.htm";
            else if (cond == 5)
                htmltext = "tataru_zu_hestui_q0416_12.htm";
            else if (cond > 5)
                htmltext = "tataru_zu_hestui_q0416_13.htm";
        } else if (npcId == HestuiTotemSpirit) {
            if (cond == 3)
                htmltext = "hestui_totem_spirit_q0416_01.htm";
            else if (cond == 4)
                htmltext = "hestui_totem_spirit_q0416_04.htm";
        } else {
            if (npcId == SeerUmos) {
                if (cond == 5) {
                    st.takeItems(TatarusLetterOfRecommendation);
                    st.giveItems(FlameCharm);
                    htmltext = "seer_umos_q0416_01.htm";
                    st.setCond(6);
                } else if (cond == 6)
                    htmltext = "seer_umos_q0416_02.htm";
                else if (cond == 7) {
                    st.takeAllItems(GrizzlyBlood, FlameCharm);
                    st.giveItems(BloodCauldron);
                    htmltext = "seer_umos_q0416_03.htm";
                    st.setCond(8);
                } else if (cond == 8)
                    htmltext = "seer_umos_q0416_04.htm";
                else if (cond == 9 || cond == 10)
                    htmltext = "seer_umos_q0416_05.htm";
                else if (cond == 11)
                    htmltext = "seer_umos_q0416_06.htm";
            } else if (npcId == SeerMoira) {
                if (cond == 12) {
                    htmltext = "seer_moirase_q0416_01.htm";
                    st.setCond(13);
                } else if (cond > 12 && cond < 21)
                    htmltext = "seer_moirase_q0416_02.htm";
                else if (cond == 21) {
                    htmltext = "seer_moirase_q0416_03.htm";
                    if (st.player.getClassId().occupation() == 0) {
                        st.giveItems(MaskOfMedium);
                        st.addExpAndSp(295862, 18194);
                        st.giveAdena(81900);
                    }
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest();
                }
            } else if (npcId == GandiTotemSpirit) {
                if (cond == 13)
                    htmltext = "totem_spirit_gandi_q0416_01.htm";
                else if (cond > 13 && cond < 20)
                    htmltext = "totem_spirit_gandi_q0416_03.htm";
                else if (cond == 20)
                    htmltext = "totem_spirit_gandi_q0416_04.htm";
            } else if (npcId == LeopardCarcass) {
                if (cond <= 14)
                    htmltext = "dead_leopard_q0416_01a.htm";
                else if (cond == 15) {
                    htmltext = "dead_leopard_q0416_01.htm";
                    st.setCond(16);
                } else if (cond == 16)
                    htmltext = "dead_leopard_q0416_01.htm";
                else if (cond == 17)
                    htmltext = "dead_leopard_q0416_02.htm";
                else if (cond == 18)
                    htmltext = "dead_leopard_q0416_05.htm";
                else if (cond == 19) {
                    htmltext = "dead_leopard_q0416_06.htm";
                    st.setCond(20);
                } else
                    htmltext = "dead_leopard_q0416_06.htm";
            } else if (npcId == DudaMaraTotemSpirit)
                if (cond == 8)
                    htmltext = "dudamara_totem_spirit_q0416_01.htm";
                else if (cond == 9)
                    htmltext = "dudamara_totem_spirit_q0416_04.htm";
                else if (cond == 10) {
                    st.takeItems(BoundDurkaSpirit);
                    st.giveItems(TotemSpiritBlood);
                    htmltext = "dudamara_totem_spirit_q0416_05.htm";
                    st.setCond(11);
                } else if (cond == 11)
                    htmltext = "dudamara_totem_spirit_q0416_06.htm";
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
                        if (aDROPLIST_COND[1] != cond && aDROPLIST_COND[1] != 0)
                            st.setCond(aDROPLIST_COND[1]);
        if (st.haveAllQuestItems(KashaBearPelt, KashaBladeSpiderHusk, FieryEgg1st))
            st.setCond(2);
        else if (cond == 9 && (npcId == VenomousSpider || npcId == ArachnidTracker)) {
            if (st.getQuestItemsCount(DurkaParasite) < 8) {
                st.giveItems(DurkaParasite);
                st.playSound(SOUND_ITEMGET);
            }
            if (st.getQuestItemsCount(DurkaParasite) == 8 || st.getQuestItemsCount(DurkaParasite) >= 5 && Rnd.chance(st.getQuestItemsCount(DurkaParasite) * 10))
                if (GameObjectsStorage.getByNpcId(QuestMonsterDurkaSpirit) == null) {
                    st.takeItems(DurkaParasite);
                    st.addSpawn(QuestMonsterDurkaSpirit);
                    st.startQuestTimer("QuestMonsterDurkaSpirit_Fail", 300000);
                }
        } else if (npcId == QuestMonsterDurkaSpirit) {
            st.cancelQuestTimer("QuestMonsterDurkaSpirit_Fail");

            GameObjectsStorage.getAllByNpcId(QuestMonsterDurkaSpirit, false)
                    .forEach(GameObject::deleteMe);
            if (cond == 9) {
                st.takeAllItems(SpiritNet, DurkaParasite);
                st.giveItems(BoundDurkaSpirit);
                st.playSound(SOUND_MIDDLE);
                st.setCond(10);
            }
        } else if (npcId == QuestBlackLeopard)
            if (cond == 14 && Rnd.chance(50)) {
                Functions.npcSayCustomMessage(GameObjectsStorage.getByNpcId(LeopardCarcass), new CustomMessage("quests._416_PathToOrcShaman.LeopardCarcass").toString(), st.player);
                st.setCond(15);
            } else if (cond == 16 && Rnd.chance(50))
                st.setCond(17);
            else if (cond == 18 && Rnd.chance(50))
                st.setCond(19);
    }
}