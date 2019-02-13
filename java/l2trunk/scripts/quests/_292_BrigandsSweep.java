package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _292_BrigandsSweep extends Quest {
    // NPCs
    private static final int Spiron = 30532;
    private static final int Balanki = 30533;
    // Mobs
    private static final int GoblinBrigand = 20322;
    private static final int GoblinBrigandLeader = 20323;
    private static final int GoblinBrigandLieutenant = 20324;
    private static final int GoblinSnooper = 20327;
    private static final int GoblinLord = 20528;
    // Quest Items
    private static final int GoblinNecklace = 1483;
    private static final int GoblinPendant = 1484;
    private static final int GoblinLordPendant = 1485;
    private static final int SuspiciousMemo = 1486;
    private static final int SuspiciousContract = 1487;
    // Chances
    private static final int Chance = 10;
    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    1,
                    0,
                    GoblinBrigand,
                    0,
                    GoblinNecklace,
                    0,
                    40,
                    1
            },
            {
                    1,
                    0,
                    GoblinBrigandLeader,
                    0,
                    GoblinNecklace,
                    0,
                    40,
                    1
            },
            {
                    1,
                    0,
                    GoblinSnooper,
                    0,
                    GoblinNecklace,
                    0,
                    40,
                    1
            },
            {
                    1,
                    0,
                    GoblinBrigandLieutenant,
                    0,
                    GoblinPendant,
                    0,
                    40,
                    1
            },
            {
                    1,
                    0,
                    GoblinLord,
                    0,
                    GoblinLordPendant,
                    0,
                    40,
                    1
            }
    };

    public _292_BrigandsSweep() {
        super(false);
        addStartNpc(Spiron);
        addTalkId(Balanki);
        //Mob Drop
        for (int[] aDROPLIST_COND : DROPLIST_COND) addKillId(aDROPLIST_COND[2]);
        addQuestItem(SuspiciousMemo);
        addQuestItem(SuspiciousContract);
        addQuestItem(GoblinNecklace);
        addQuestItem(GoblinPendant);
        addQuestItem(GoblinLordPendant);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("elder_spiron_q0292_03.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("elder_spiron_q0292_06.htm")) {
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
        if (npcId == Spiron) {
            if (cond == 0) {
                if (st.player.getRace() != Race.dwarf) {
                    htmltext = "elder_spiron_q0292_00.htm";
                    st.exitCurrentQuest(true);
                } else if (st.player.getLevel() < 5) {
                    htmltext = "elder_spiron_q0292_01.htm";
                    st.exitCurrentQuest(true);
                } else
                    htmltext = "elder_spiron_q0292_02.htm";
            } else if (cond == 1) {
                long reward = st.getQuestItemsCount(GoblinNecklace) * 12 + st.getQuestItemsCount(GoblinPendant) * 36 + st.getQuestItemsCount(GoblinLordPendant) * 33 + st.getQuestItemsCount(SuspiciousContract) * 100;
                if (reward == 0)
                    return "elder_spiron_q0292_04.htm";
                if (st.getQuestItemsCount(SuspiciousContract) != 0)
                    htmltext = "elder_spiron_q0292_10.htm";
                else if (st.getQuestItemsCount(SuspiciousMemo) == 0)
                    htmltext = "elder_spiron_q0292_05.htm";
                else if (st.getQuestItemsCount(SuspiciousMemo) == 1)
                    htmltext = "elder_spiron_q0292_08.htm";
                else
                    htmltext = "elder_spiron_q0292_09.htm";
                st.takeItems(GoblinNecklace, -1);
                st.takeItems(GoblinPendant, -1);
                st.takeItems(GoblinLordPendant, -1);
                st.takeItems(SuspiciousContract, -1);
                st.giveItems(ADENA_ID, reward);
            }
        } else if (npcId == Balanki && cond == 1)
            if (st.getQuestItemsCount(SuspiciousContract) == 0)
                htmltext = "balanki_q0292_01.htm";
            else {
                st.takeItems(SuspiciousContract, -1);
                st.giveItems(ADENA_ID, 120);
                htmltext = "balanki_q0292_02.htm";
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
                            st.setState(STARTED);
                        }
        if (st.getQuestItemsCount(SuspiciousContract) == 0 && Rnd.chance(Chance))
            if (st.getQuestItemsCount(SuspiciousMemo) < 3) {
                st.giveItems(SuspiciousMemo);
                st.playSound(SOUND_ITEMGET);
            } else {
                st.takeItems(SuspiciousMemo);
                st.giveItems(SuspiciousContract);
                st.playSound(SOUND_MIDDLE);
            }
    }
}