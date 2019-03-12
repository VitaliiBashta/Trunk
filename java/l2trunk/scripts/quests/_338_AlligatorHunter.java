package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _338_AlligatorHunter extends Quest {
    //NPC
    private static final int Enverun = 30892;
    //QuestItems
    private static final int AlligatorLeather = 4337;
    //MOB
    private static final int CrokianLad = 20804;
    private static final int DailaonLad = 20805;
    private static final int CrokianLadWarrior = 20806;
    private static final int FarhiteLad = 20807;
    private static final int NosLad = 20808;
    private static final int SwampTribe = 20991;
    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private final int[][] DROPLIST_COND = {
            {
                    1,
                    0,
                    CrokianLad,
                    0,
                    AlligatorLeather,
                    0,
                    60,
                    1
            },
            {
                    1,
                    0,
                    DailaonLad,
                    0,
                    AlligatorLeather,
                    0,
                    60,
                    1
            },
            {
                    1,
                    0,
                    CrokianLadWarrior,
                    0,
                    AlligatorLeather,
                    0,
                    60,
                    1
            },
            {
                    1,
                    0,
                    FarhiteLad,
                    0,
                    AlligatorLeather,
                    0,
                    60,
                    1
            },
            {
                    1,
                    0,
                    NosLad,
                    0,
                    AlligatorLeather,
                    0,
                    60,
                    1
            },
            {
                    1,
                    0,
                    SwampTribe,
                    0,
                    AlligatorLeather,
                    0,
                    60,
                    1
            }
    };

    public _338_AlligatorHunter() {
        super(false);
        addStartNpc(Enverun);
        //mob Drop
        for (int[] aDROPLIST_COND : DROPLIST_COND) addKillId(aDROPLIST_COND[2]);
        addQuestItem(AlligatorLeather);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        long adenaCount = st.getQuestItemsCount(AlligatorLeather) * 40;
        if ("30892-02.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_ACCEPT);
            st.setCond(1);
            st.start();
        } else if ("30892-02-afmenu.htm".equalsIgnoreCase(event)) {
            st.takeItems(AlligatorLeather);
            st.giveAdena(adenaCount);
        } else if ("quit".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(AlligatorLeather) ) {
                st.takeItems(AlligatorLeather);
                st.giveAdena( adenaCount);
                htmltext = "30892-havequit.htm";
            } else
                htmltext = "30892-havent.htm";
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "<html><body>I have nothing to say you</body></html>";
        int npcId = npc.getNpcId();
        if (npcId == Enverun)
            if (st.getCond() == 0) {
                if (st.player.getLevel() >= 40)
                    htmltext = "30892-01.htm";
                else {
                    htmltext = "30892-00.htm";
                    st.exitCurrentQuest();
                }
            } else if (st.haveQuestItem(AlligatorLeather))
                htmltext = "30892-menu.htm";
            else
                htmltext = "30892-02-rep.htm";
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
    }
}