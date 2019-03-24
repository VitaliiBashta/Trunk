package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _380_BringOutTheFlavorOfIngredients extends Quest {
    //NPCs
    private static final int Rollant = 30069;
    //Quest items
    private static final int RitronsFruit = 5895;
    private static final int MoonFaceFlower = 5896;
    private static final int LeechFluids = 5897;
    //items
    private static final int Antidote = 1831;
    private static final int RitronsDessertRecipe = 5959;
    private static final int RitronJelly = 5960;
    //Chances
    private static final int RitronsDessertRecipeChance = 55;
    //Mobs
    private static final int DireWolf = 20205;
    private static final int KadifWerewolf = 20206;
    private static final int GiantMistLeech = 20225;
    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final int[][] DROPLIST_COND = {
            {
                    1,
                    0,
                    DireWolf,
                    0,
                    RitronsFruit,
                    4,
                    10,
                    1
            },
            {
                    1,
                    0,
                    KadifWerewolf,
                    0,
                    MoonFaceFlower,
                    20,
                    50,
                    1
            },
            {
                    1,
                    0,
                    GiantMistLeech,
                    0,
                    LeechFluids,
                    10,
                    50,
                    1
            }
    };

    public _380_BringOutTheFlavorOfIngredients() {
        addStartNpc(Rollant);

        for (int[] aDROPLIST_COND : DROPLIST_COND) {
            addKillId(DireWolf, KadifWerewolf,GiantMistLeech );
            addQuestItem(aDROPLIST_COND[4]);
        }
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("rollant_q0380_05.htm".equalsIgnoreCase(event)) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("rollant_q0380_12.htm".equalsIgnoreCase(event)) {
            st.giveItems(RitronsDessertRecipe);
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
        if (npcId == Rollant) {
            if (cond == 0) {
                if (st.player.getLevel() >= 24)
                    htmltext = "rollant_q0380_02.htm";
                else {
                    htmltext = "rollant_q0380_01.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "rollant_q0380_06.htm";
            else if (cond == 2 && st.getQuestItemsCount(Antidote) >= 2) {
                st.takeItems(Antidote, 2);
                st.takeAllItems(RitronsFruit,MoonFaceFlower,LeechFluids);
                htmltext = "rollant_q0380_07.htm";
                st.setCond(3);
                st.start();
            } else if (cond == 2)
                htmltext = "rollant_q0380_06.htm";
            else if (cond == 3) {
                htmltext = "rollant_q0380_08.htm";
                st.setCond(4);
            } else if (cond == 4) {
                htmltext = "rollant_q0380_09.htm";
                st.setCond(5);
            }
            if (cond == 5) {
                htmltext = "rollant_q0380_10.htm";
                st.setCond(6);
            }
            if (cond == 6) {
                st.giveItems(RitronJelly);
                if (Rnd.chance(RitronsDessertRecipeChance))
                    htmltext = "rollant_q0380_11.htm";
                else {
                    htmltext = "rollant_q0380_14.htm";
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest();
                }
            }
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        for (int[] aDROPLIST_COND : DROPLIST_COND)
            if (cond == 1 && npcId == aDROPLIST_COND[2]) {
                st.rollAndGive(aDROPLIST_COND[4], 1, 1, aDROPLIST_COND[5], aDROPLIST_COND[6]);
            }
        if (cond == 1 && st.getQuestItemsCount(RitronsFruit) >= 4 && st.getQuestItemsCount(MoonFaceFlower) >= 20 && st.getQuestItemsCount(LeechFluids) >= 10) {
            st.setCond(2);
            st.start();
        }
    }
}