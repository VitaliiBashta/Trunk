package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _319_ScentOfDeath extends Quest {
    //NPC
    private static final int MINALESS = 30138;
    //Item
    private static final int HealingPotion = 1060;
    //Quest Item
    private static final int ZombieSkin = 1045;

    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    1,
                    2,
                    20015,
                    0,
                    ZombieSkin,
                    5,
                    20,
                    1
            },
            {
                    1,
                    2,
                    20020,
                    0,
                    ZombieSkin,
                    5,
                    25,
                    1
            }
    };


    public _319_ScentOfDeath() {
        super(false);

        addStartNpc(MINALESS);
        addTalkId(MINALESS);
        //Mob Drop
        for (int[] aDROPLIST_COND : DROPLIST_COND) addKillId(aDROPLIST_COND[2]);

        addQuestItem(ZombieSkin);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("mina_q0319_04.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        int cond = 0;
        if (id != CREATED)
            cond = st.getCond();
        if (npcId == MINALESS)
            if (cond == 0)
                if (st.player.getLevel() < 11) {
                    htmltext = "mina_q0319_02.htm";
                    st.exitCurrentQuest(true);
                } else
                    htmltext = "mina_q0319_03.htm";
            else if (cond == 1)
                htmltext = "mina_q0319_05.htm";
            else if (cond == 2 && st.getQuestItemsCount(ZombieSkin) >= 5) {
                htmltext = "mina_q0319_06.htm";
                st.takeItems(ZombieSkin);
                st.giveItems(ADENA_ID, 3350);
                st.giveItems(HealingPotion);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(true);
            } else {
                htmltext = "mina_q0319_05.htm";
                st.setCond(1);
            }
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
                            st.setState(STARTED);
                        }
    }

}
