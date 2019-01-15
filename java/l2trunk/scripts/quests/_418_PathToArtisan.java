package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;

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
        //Mob Drop
        for (int[] aDROPLIST_COND : DROPLIST_COND) {
            addKillId(aDROPLIST_COND[2]);
            addQuestItem(aDROPLIST_COND[4]);
        }
        addQuestItem(SilverasRing, PassCertificate1st, SecretBox, KlutosLetter, FootprintOfThief, PassCertificate2nd);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("blacksmith_silvery_q0418_06.htm")) {
            st.giveItems(SilverasRing, 1);
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("blacksmith_kluto_q0418_04.htm") || event.equalsIgnoreCase("blacksmith_kluto_q0418_07.htm")) {
            st.giveItems(KlutosLetter, 1);
            st.setCond(4);
            st.setState(STARTED);
        } else if (event.equalsIgnoreCase("blacksmith_pinter_q0418_03.htm")) {
            st.takeItems(KlutosLetter, -1);
            st.giveItems(FootprintOfThief, 1);
            st.setCond(5);
            st.setState(STARTED);
        } else if (event.equalsIgnoreCase("blacksmith_pinter_q0418_06.htm")) {
            st.takeItems(StolenSecretBox, -1);
            st.takeItems(FootprintOfThief, -1);
            st.giveItems(SecretBox, 1);
            st.giveItems(PassCertificate2nd, 1);
            st.setCond(7);
            st.setState(STARTED);
        } else if (event.equalsIgnoreCase("blacksmith_kluto_q0418_10.htm") || event.equalsIgnoreCase("blacksmith_kluto_q0418_12.htm")) {
            st.takeItems(PassCertificate1st, -1);
            st.takeItems(PassCertificate2nd, -1);
            st.takeItems(SecretBox, -1);
            if (st.getPlayer().getClassId().getLevel() == 1) {
                st.giveItems(FinalPassCertificate, 1);
                if (!st.getPlayer().getVarB("prof1")) {
                    st.getPlayer().setVar("prof1", "1", -1);
                    st.addExpAndSp(228064, 16455);
                    //FIXME [G1ta0] дать адены, только если первый чар на акке
                    st.giveItems(ADENA_ID, 81900);
                }
            }
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(true);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Silvera) {
            if (st.getQuestItemsCount(FinalPassCertificate) != 0) {
                htmltext = "blacksmith_silvery_q0418_04.htm";
                st.exitCurrentQuest(true);
            } else if (cond == 0) {
                if (st.getPlayer().getClassId().getId() != 0x35) {
                    if (st.getPlayer().getClassId().getId() == 0x38)
                        htmltext = "blacksmith_silvery_q0418_02a.htm";
                    else
                        htmltext = "blacksmith_silvery_q0418_02.htm";
                    st.exitCurrentQuest(true);
                } else if (st.getPlayer().getLevel() < 18) {
                    htmltext = "blacksmith_silvery_q0418_03.htm";
                    st.exitCurrentQuest(true);
                } else
                    htmltext = "blacksmith_silvery_q0418_01.htm";
            } else if (cond == 1)
                htmltext = "blacksmith_silvery_q0418_07.htm";
            else if (cond == 2) {
                st.takeItems(BoogleRatmanTooth, -1);
                st.takeItems(BoogleRatmanLeadersTooth, -1);
                st.takeItems(SilverasRing, -1);
                st.giveItems(PassCertificate1st, 1);
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
    public String onKill(NpcInstance npc, QuestState st) {
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
                            st.setState(STARTED);
                        }
        if (cond == 1 && st.getQuestItemsCount(BoogleRatmanTooth) >= 10 && st.getQuestItemsCount(BoogleRatmanLeadersTooth) >= 2) {
            st.setCond(2);
            st.setState(STARTED);
        }
        return null;
    }
}