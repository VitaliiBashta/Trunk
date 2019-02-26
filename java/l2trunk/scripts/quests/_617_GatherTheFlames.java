package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _617_GatherTheFlames extends Quest {
    //npc
    private final static int VULCAN = 31539;
    private final static int HILDA = 31271;
    //items
    private final static int TORCH = 7264;
    //mobs (MOB_ID, CHANCE)
    private final static List<Integer> mobs = List.of(
            22634, 22635, 22636, 22637, 22638, 22639, 22640, 22641, 22642, 22643,
            22644, 22645, 22646, 22647, 22648, 22649, 18799, 18800, 18801, 18802, 18803);
    private static final List<Integer> Recipes = List.of(
            6881, 6883, 6885, 6887, 7580, 6891, 6893, 6895, 6897, 6899);

    public _617_GatherTheFlames() {
        super(true);

        addStartNpc(VULCAN,HILDA);
        addKillId(mobs);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("warsmith_vulcan_q0617_03.htm".equalsIgnoreCase(event)) //VULCAN
        {
            if (st.player.getLevel() < 74)
                return "warsmith_vulcan_q0617_02.htm";
            st.start();
            st.playSound(SOUND_ACCEPT);
            st.setCond(1);
        } else if ("blacksmith_hilda_q0617_03.htm".equalsIgnoreCase(event)) //HILDA
        {
            if (st.player.getLevel() < 74)
                return "blacksmith_hilda_q0617_02.htm";
            st.start();
            st.playSound(SOUND_ACCEPT);
            st.setCond(1);
        } else if ("warsmith_vulcan_q0617_08.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_FINISH);
            st.takeItems(TORCH);
            st.exitCurrentQuest();
        } else if ("warsmith_vulcan_q0617_07.htm".equalsIgnoreCase(event)) {
            if (st.getQuestItemsCount(TORCH) < 1000)
                return "warsmith_vulcan_q0617_05.htm";
            st.takeItems(TORCH, 1000);
            st.giveItems(Rnd.get(Recipes));
            st.playSound(SOUND_MIDDLE);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == VULCAN) {
            if (cond == 0) {
                if (st.player.getLevel() < 74) {
                    htmltext = "warsmith_vulcan_q0617_02.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "warsmith_vulcan_q0617_01.htm";
            } else
                htmltext = st.getQuestItemsCount(TORCH) < 1000 ? "warsmith_vulcan_q0617_05.htm" : "warsmith_vulcan_q0617_04.htm";
        } else if (npcId == HILDA)
            if (cond < 1)
                htmltext = st.player.getLevel() < 74 ? "blacksmith_hilda_q0617_02.htm" : "blacksmith_hilda_q0617_01.htm";
            else
                htmltext = "blacksmith_hilda_q0617_04.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (mobs.contains(npc.getNpcId())) {
            st.rollAndGive(TORCH, 1, 70);
        }
    }
}