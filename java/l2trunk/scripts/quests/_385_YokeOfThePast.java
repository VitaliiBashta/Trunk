package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _385_YokeOfThePast extends Quest {
    private final int ANCIENT_SCROLL = 5902;
    private static final int BLANK_SCROLL = 5965;

    public _385_YokeOfThePast() {
        super(true);

        for (int npcId = 31095; npcId <= 31126; npcId++)
            if (npcId != 31111 && npcId != 31112 && npcId != 31113)
                addStartNpc(npcId);

        for (int mobs = 21208; mobs < 21256; mobs++)
            addKillId(mobs);

        addQuestItem(ANCIENT_SCROLL);
    }

    private boolean checkNPC(int npc) {
        if (npc >= 31095 && npc <= 31126)
            return npc != 31100 && npc != 31111 && npc != 31112 && npc != 31113;
        return false;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("enter_necropolis1_q0385_05.htm")) {
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
            st.setCond(1);
        } else if (event.equalsIgnoreCase("enter_necropolis1_q0385_09.htm")) {
            htmltext = "enter_necropolis1_q0385_10.htm";
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(true);
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        double rand = 60 * Experience.penaltyModifier(st.calculateLevelDiffForDrop(npc.getLevel(), st.player.getLevel()), 9) * npc.getTemplate().rateHp / 4;

        st.rollAndGive(ANCIENT_SCROLL, 1, rand);
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        if (checkNPC(npcId) && st.getCond() == 0)
            if (st.player.getLevel() < 20) {
                htmltext = "enter_necropolis1_q0385_02.htm";
                st.exitCurrentQuest(true);
            } else
                htmltext = "enter_necropolis1_q0385_01.htm";
        else if (st.getCond() == 1 && st.getQuestItemsCount(ANCIENT_SCROLL) == 0)
            htmltext = "enter_necropolis1_q0385_11.htm";
        else if (st.getCond() == 1 && st.getQuestItemsCount(ANCIENT_SCROLL) > 0) {
            htmltext = "enter_necropolis1_q0385_09.htm";
            st.giveItems(BLANK_SCROLL, st.getQuestItemsCount(ANCIENT_SCROLL));
            st.takeItems(ANCIENT_SCROLL);
        } else
            st.exitCurrentQuest(true);
        return htmltext;
    }
}