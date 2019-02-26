package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _317_CatchTheWind extends Quest {
    //NPCs
    private static final int Rizraell = 30361;
    //Quest items
    private static final int WindShard = 1078;
    //Mobs
    private static final int Lirein = 20036;
    private static final int LireinElder = 20044;
    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private final int[][] DROPLIST_COND = {
            {
                    1,
                    0,
                    Lirein,
                    0,
                    WindShard,
                    0,
                    60,
                    1
            },
            {
                    1,
                    0,
                    LireinElder,
                    0,
                    WindShard,
                    0,
                    60,
                    1
            }
    };

    public _317_CatchTheWind() {
        super(false);
        addStartNpc(Rizraell);
        //mob Drop
        for (int[] aDROPLIST_COND : DROPLIST_COND) addKillId(aDROPLIST_COND[2]);
        addQuestItem(WindShard);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("rizraell_q0317_04.htm")) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("rizraell_q0317_08.htm")) {
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
        if (npcId == Rizraell)
            if (cond == 0) {
                if (st.player.getLevel() >= 18)
                    htmltext = "rizraell_q0317_03.htm";
                else {
                    htmltext = "rizraell_q0317_02.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1) {
                long count = st.getQuestItemsCount(WindShard);
                if (st.haveQuestItem(WindShard)) {
                    st.takeItems(WindShard);
                    st.giveItems(ADENA_ID, 40 * count);
                    htmltext = "rizraell_q0317_07.htm";
                } else
                    htmltext = "rizraell_q0317_05.htm";
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        for (int[] aDROPLIST_COND : DROPLIST_COND)
            if (cond == aDROPLIST_COND[0] && npcId == aDROPLIST_COND[2])
                if (aDROPLIST_COND[3] == 0 || st.haveQuestItem(aDROPLIST_COND[3]) )
                    if (aDROPLIST_COND[5] == 0)
                        st.rollAndGive(aDROPLIST_COND[4], aDROPLIST_COND[7], aDROPLIST_COND[6]);
                    else if (st.rollAndGive(aDROPLIST_COND[4], aDROPLIST_COND[7], aDROPLIST_COND[7], aDROPLIST_COND[5], aDROPLIST_COND[6]))
                        if (aDROPLIST_COND[1] != cond && aDROPLIST_COND[1] != 0) {
                            st.setCond(aDROPLIST_COND[1]);
                            st.start();
                        }
    }
}