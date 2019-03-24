package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.Map;

public final class _284_MuertosFeather extends Quest {
    //NPC
    private static final int Trevor = 32166;
    //Quest Item
    private static final int MuertosFeather = 9748;
    //MOBs
    private static final int MuertosGuard = 22239;
    private static final int MuertosScout = 22240;
    private static final int MuertosWarrior = 22242;
    private static final int MuertosCaptain = 22243;
    private static final int MuertosLieutenant = 22245;
    private static final int MuertosCommander = 22246;
    //Drop Cond
    //# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
    private static final Map<Integer, Integer> DROPLIST_COND = Map.of(
            MuertosGuard, 44,
            MuertosScout, 48,
            MuertosWarrior, 56,
            MuertosCaptain, 60,
            MuertosLieutenant, 64,
            MuertosCommander, 69);

    public _284_MuertosFeather() {
        addStartNpc(Trevor);

        //mob Drop
        addKillId(DROPLIST_COND.keySet());
        addQuestItem(MuertosFeather);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("trader_treauvi_q0284_0103.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("trader_treauvi_q0284_0203.htm".equalsIgnoreCase(event)) {
            long counts = st.getQuestItemsCount(MuertosFeather) * 45;
            st.takeItems(MuertosFeather);
            st.giveAdena(counts);
        } else if ("trader_treauvi_q0284_0204.htm".equalsIgnoreCase(event))
            st.exitCurrentQuest();
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == Trevor)
            if (st.player.getLevel() < 11) {
                htmltext = "trader_treauvi_q0284_0102.htm";
                st.exitCurrentQuest();
            } else if (cond == 0)
                htmltext = "trader_treauvi_q0284_0101.htm";
            else if (cond == 1 && !st.haveQuestItem(MuertosFeather))
                htmltext = "trader_treauvi_q0284_0103.htm";
            else
                htmltext = "trader_treauvi_q0284_0105.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1 && DROPLIST_COND.containsKey(npcId))
            st.rollAndGive(MuertosFeather, 1, DROPLIST_COND.get(npcId));
    }
}