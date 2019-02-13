package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _661_TheHarvestGroundsSafe extends Quest {
    //NPC
    private static final int NORMAN = 30210;

    // MOBS
    private static final int GIANT_POISON_BEE = 21095;
    private static final int CLOYDY_BEAST = 21096;
    private static final int YOUNG_ARANEID = 21097;

    //QUEST ITEMS
    private static final int STING_OF_GIANT_POISON = 8283;
    private static final int TALON_OF_YOUNG_ARANEID = 8285;
    private static final int CLOUDY_GEM = 8284;

    public _661_TheHarvestGroundsSafe() {
        super(false);

        addStartNpc(NORMAN);

        addKillId(GIANT_POISON_BEE,CLOYDY_BEAST,YOUNG_ARANEID);

        addQuestItem(STING_OF_GIANT_POISON,TALON_OF_YOUNG_ARANEID,CLOUDY_GEM);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("warehouse_keeper_norman_q0661_0103.htm") || event.equalsIgnoreCase("warehouse_keeper_norman_q0661_0201.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("warehouse_keeper_norman_q0661_0205.htm")) {
            long STING = st.getQuestItemsCount(STING_OF_GIANT_POISON);
            long TALON = st.getQuestItemsCount(TALON_OF_YOUNG_ARANEID);
            long GEM = st.getQuestItemsCount(CLOUDY_GEM);

            if (STING + GEM + TALON >= 10) {
                st.giveItems(ADENA_ID, STING * 50 + GEM * 60 + TALON * 70 + 2800);
                st.takeItems(STING_OF_GIANT_POISON, -1);
                st.takeItems(TALON_OF_YOUNG_ARANEID, -1);
                st.takeItems(CLOUDY_GEM, -1);
            } else {
                st.giveItems(ADENA_ID, STING * 50 + GEM * 60 + TALON * 70);
                st.takeItems(STING_OF_GIANT_POISON, -1);
                st.takeItems(TALON_OF_YOUNG_ARANEID, -1);
                st.takeItems(CLOUDY_GEM, -1);
            }
            st.playSound(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("warehouse_keeper_norman_q0661_0204.htm")) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(false);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0)
            if (st.player.getLevel() >= 21)
                htmltext = "warehouse_keeper_norman_q0661_0101.htm";
            else {
                htmltext = "warehouse_keeper_norman_q0661_0102.htm";
                st.exitCurrentQuest(true);
            }
        else if (cond == 1)
            if (st.haveAnyQuestItems(STING_OF_GIANT_POISON, TALON_OF_YOUNG_ARANEID,CLOUDY_GEM) )
                htmltext = "warehouse_keeper_norman_q0661_0105.htm";
            else
                htmltext = "warehouse_keeper_norman_q0661_0206.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getState() != STARTED)
            return;
        int npcId = npc.getNpcId();

        if (st.getCond() == 1) {
            if (npcId == GIANT_POISON_BEE && Rnd.chance(75)) {
                st.giveItems(STING_OF_GIANT_POISON);
                st.playSound(SOUND_ITEMGET);
            }
            if (npcId == CLOYDY_BEAST && Rnd.chance(71)) {
                st.giveItems(CLOUDY_GEM);
                st.playSound(SOUND_ITEMGET);
            }
            if (npcId == YOUNG_ARANEID && Rnd.chance(67)) {
                st.giveItems(TALON_OF_YOUNG_ARANEID);
                st.playSound(SOUND_ITEMGET);
            }
        }
    }
}