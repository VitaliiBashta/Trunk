package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.HashMap;
import java.util.Map;

public final class _369_CollectorOfJewels extends Quest {
    // NPCs
    private static final int NELL = 30376;
    // Mobs
    private static final int Roxide = 20747;
    private static final int Rowin_Undine = 20619;
    private static final int Lakin_Undine = 20616;
    private static final int Salamander_Rowin = 20612;
    private static final int Lakin_Salamander = 20609;
    private static final int Death_Fire = 20749;
    // Quest items
    private static final int FLARE_SHARD = 5882;
    private static final int FREEZING_SHARD = 5883;

    private final Map<Integer, int[]> DROPLIST = new HashMap<>();

    public _369_CollectorOfJewels() {
        addStartNpc(NELL);
        addKillId(Roxide,Rowin_Undine,Lakin_Undine,Salamander_Rowin,Lakin_Salamander,Death_Fire);
        addQuestItem(FLARE_SHARD,FREEZING_SHARD);

        DROPLIST.put(Roxide, new int[]{
                FREEZING_SHARD,
                85
        });
        DROPLIST.put(Rowin_Undine, new int[]{
                FREEZING_SHARD,
                73
        });
        DROPLIST.put(Lakin_Undine, new int[]{
                FREEZING_SHARD,
                60
        });
        DROPLIST.put(Salamander_Rowin, new int[]{
                FLARE_SHARD,
                77
        });
        DROPLIST.put(Lakin_Salamander, new int[]{
                FLARE_SHARD,
                77
        });
        DROPLIST.put(Death_Fire, new int[]{
                FLARE_SHARD,
                85
        });
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("30376-03.htm".equalsIgnoreCase(event) && st.getState() == CREATED) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("30376-08.htm".equalsIgnoreCase(event) && st.getState() == STARTED) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != NELL)
            return htmltext;
        int state = st.getState();

        if (state == CREATED) {
            if (st.player.getLevel() >= 25) {
                st.setCond(0);
                return "30376-02.htm";
            }
            st.exitCurrentQuest();
            return "30376-01.htm";
        }

        if (state != STARTED)
            return htmltext;
        int cond = st.getCond();
        if (cond == 1)
            htmltext = "30376-04.htm";
        else if (cond == 3)
            htmltext = "30376-09.htm";
        else if (cond == 2 || cond == 4) {
            int max_count = cond == 2 ? 50 : 200;
            long FLARE_SHARD_COUNT = st.getQuestItemsCount(FLARE_SHARD);
            long FREEZING_SHARD_COUNT = st.getQuestItemsCount(FREEZING_SHARD);
            if (FLARE_SHARD_COUNT != max_count || FREEZING_SHARD_COUNT != max_count) {
                st.setCond(cond - 1);
                return onTalk(npc, st);
            }

            st.takeAllItems(FLARE_SHARD,FREEZING_SHARD);
            if (cond == 2) {
                htmltext = "30376-05.htm";
                st.giveAdena( 12500);
                st.playSound(SOUND_MIDDLE);
                st.setCond(3);
            } else {
                htmltext = "30376-10.htm";
                st.giveAdena( 63500);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            }
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        int cond = qs.getCond();
        if (cond != 1 && cond != 3)
            return;

        int[] drop = DROPLIST.get(npc.getNpcId());
        if (drop == null)
            return;

        int max_count = cond == 1 ? 50 : 200;
        if (qs.getQuestItemsCount(drop[0]) < max_count && qs.rollAndGive(drop[0], 1, 1, max_count, drop[1]) && qs.getQuestItemsCount(FLARE_SHARD) >= max_count && qs.getQuestItemsCount(FREEZING_SHARD) >= max_count)
            qs.setCond(cond == 1 ? 2 : 4);

    }
}