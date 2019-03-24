package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import static l2trunk.gameserver.model.base.ClassId.artisan;
import static l2trunk.gameserver.model.base.ClassId.dwarvenFighter;

public final class _418_PathToArtisan extends Quest {
    //NPC
    private static final int Silvera = 30527;
    private static final int Kluto = 30317;
    private static final int Pinter = 30298;
    //Quest Item
    private static final int SilverasRing = 1632;
    private static final int BoogleRatmanTooth = 1636;
    private static final int BoogleRatmanLeadersTooth = 1637;
    private static final int PassCertificate1st = 1633;
    private static final int KlutosLetter = 1638;
    private static final int FootprintOfThief = 1639;
    private static final int StolenSecretBox = 1640;
    private static final int PassCertificate2nd = 1634;
    private static final int SecretBox = 1641;
    //Item
    private static final int FinalPassCertificate = 1635;
    //MOB
    private static final int BoogleRatman = 20389;
    private static final int BoogleRatmanLeader = 20390;
    private static final int VukuOrcFighter = 20017;
    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    1,
                    0,
                    BoogleRatman,
                    SilverasRing,
                    BoogleRatmanTooth,
                    10,
                    35,
                    1
            },
            {
                    1,
                    0,
                    BoogleRatmanLeader,
                    SilverasRing,
                    BoogleRatmanLeadersTooth,
                    2,
                    25,
                    1
            },
            {
                    5,
                    6,
                    VukuOrcFighter,
                    FootprintOfThief,
                    StolenSecretBox,
                    1,
                    20,
                    1
            }
    };

    public _418_PathToArtisan() {
        super(false);
        addStartNpc(Silvera);
        addTalkId(Kluto, Pinter);
        //mob Drop
        for (int[] aDROPLIST_COND : DROPLIST_COND) {
            addKillId(aDROPLIST_COND[2]);
            addQuestItem(aDROPLIST_COND[4]);
        }
        addQuestItem(SilverasRing, PassCertificate1st, SecretBox, KlutosLetter, FootprintOfThief, PassCertificate2nd);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("blacksmith_silvery_q0418_06.htm".equalsIgnoreCase(event)) {
            st.giveItems(SilverasRing);
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("blacksmith_kluto_q0418_04.htm".equalsIgnoreCase(event) || "blacksmith_kluto_q0418_07.htm".equalsIgnoreCase(event)) {
            st.giveItems(KlutosLetter);
            st.setCond(4);
            st.start();
        } else if ("blacksmith_pinter_q0418_03.htm".equalsIgnoreCase(event)) {
            st.takeItems(KlutosLetter);
            st.giveItems(FootprintOfThief);
            st.setCond(5);
            st.start();
        } else if ("blacksmith_pinter_q0418_06.htm".equalsIgnoreCase(event)) {
            st.takeItems(StolenSecretBox);
            st.takeItems(FootprintOfThief);
            st.giveItems(SecretBox);
            st.giveItems(PassCertificate2nd);
            st.setCond(7);
            st.start();
        } else if ("blacksmith_kluto_q0418_10.htm".equalsIgnoreCase(event) || "blacksmith_kluto_q0418_12.htm".equalsIgnoreCase(event)) {
            st.takeAllItems(PassCertificate1st,PassCertificate2nd,SecretBox);
            if (st.player.getClassId().occupation() == 0) {
                st.giveItems(FinalPassCertificate);
                if (!st.player.isVarSet("prof1")) {
                    st.player.setVar("prof1");
                    st.addExpAndSp(228064, 16455);
                    st.giveAdena(81900);
                }
            }
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Silvera) {
            if (st.haveQuestItem(FinalPassCertificate) ) {
                htmltext = "blacksmith_silvery_q0418_04.htm";
                st.exitCurrentQuest();
            } else if (cond == 0) {
                if (st.player.getClassId() != dwarvenFighter) {
                    if (st.player.getClassId() ==artisan)
                        htmltext = "blacksmith_silvery_q0418_02a.htm";
                    else
                        htmltext = "blacksmith_silvery_q0418_02.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getLevel() < 18) {
                    htmltext = "blacksmith_silvery_q0418_03.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "blacksmith_silvery_q0418_01.htm";
            } else if (cond == 1)
                htmltext = "blacksmith_silvery_q0418_07.htm";
            else if (cond == 2) {
                st.takeAllItems(BoogleRatmanTooth,BoogleRatmanLeadersTooth,SilverasRing);
                st.giveItems(PassCertificate1st);
                htmltext = "blacksmith_silvery_q0418_08.htm";
                st.setCond(3);
            } else if (cond == 3)
                htmltext = "blacksmith_silvery_q0418_09.htm";
        } else if (npcId == Kluto) {
            if (cond == 3)
                htmltext = "blacksmith_kluto_q0418_01.htm";
            else if (cond == 4 || cond == 5)
                htmltext = "blacksmith_kluto_q0418_08.htm";
            else if (cond == 7)
                htmltext = "blacksmith_kluto_q0418_09.htm";
        } else if (npcId == Pinter)
            if (cond == 4)
                htmltext = "blacksmith_pinter_q0418_01.htm";
            else if (cond == 5)
                htmltext = "blacksmith_pinter_q0418_04.htm";
            else if (cond == 6)
                htmltext = "blacksmith_pinter_q0418_05.htm";
            else if (cond == 7)
                htmltext = "blacksmith_pinter_q0418_07.htm";
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
        if (cond == 1 && st.getQuestItemsCount(BoogleRatmanTooth) >= 10 && st.getQuestItemsCount(BoogleRatmanLeadersTooth) >= 2) {
            st.setCond(2);
            st.start();
        }
    }
}