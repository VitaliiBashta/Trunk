package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _272_WrathOfAncestors extends Quest {
    //NPC
    private static final int Livina = 30572;
    //Quest Item
    private static final int GraveRobbersHead = 1474;
    //MOB
    private static final int GoblinGraveRobber = 20319;
    private static final int GoblinTombRaiderLeader = 20320;
    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    1,
                    2,
                    GoblinGraveRobber,
                    0,
                    GraveRobbersHead,
                    50,
                    100,
                    1
            },
            {
                    1,
                    2,
                    GoblinTombRaiderLeader,
                    0,
                    GraveRobbersHead,
                    50,
                    100,
                    1
            }
    };

    public _272_WrathOfAncestors() {
        addStartNpc(Livina);
        //mob Drop
        for (int[] aDROPLIST_COND : DROPLIST_COND) addKillId(aDROPLIST_COND[2]);
        addQuestItem(GraveRobbersHead);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("1".equals(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            return  "seer_livina_q0272_03.htm";
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Livina)
            if (cond == 0) {
                if (st.player.getRace() != Race.orc) {
                    htmltext = "seer_livina_q0272_00.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getLevel() < 5) {
                    htmltext = "seer_livina_q0272_01.htm";
                    st.exitCurrentQuest();
                } else {
                    htmltext = "seer_livina_q0272_02.htm";
                    return htmltext;
                }
            } else if (cond == 1)
                htmltext = "seer_livina_q0272_04.htm";
            else if (cond == 2) {
                st.takeItems(GraveRobbersHead);
                st.giveAdena(1500);
                htmltext = "seer_livina_q0272_05.htm";
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
            if (cond == 1 && npcId == aDROPLIST_COND[2]) {
                if (st.rollAndGive(aDROPLIST_COND[4], aDROPLIST_COND[7], aDROPLIST_COND[7], aDROPLIST_COND[5], aDROPLIST_COND[6]))
                    if (aDROPLIST_COND[1] != cond && aDROPLIST_COND[1] != 0) {
                        st.setCond(aDROPLIST_COND[1]);
                        st.start();
                    }
            }
    }
}