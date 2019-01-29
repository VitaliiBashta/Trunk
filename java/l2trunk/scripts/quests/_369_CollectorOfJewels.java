package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

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
    // Quest Items
    private static final int FLARE_SHARD = 5882;
    private static final int FREEZING_SHARD = 5883;
    private static final Map<Integer, Integer> freezingShards = Map.of(
            Roxide, 85,
            Rowin_Undine, 73,
            Lakin_Undine, 60);
    private static final Map<Integer, Integer> flareShards = Map.of(
            Salamander_Rowin, 77,
            Lakin_Salamander, 77,
            Death_Fire, 85);

    public _369_CollectorOfJewels() {
        super(false);
        addStartNpc(NELL);
        addKillId(Roxide);
        addKillId(Rowin_Undine);
        addKillId(Lakin_Undine);
        addKillId(Salamander_Rowin);
        addKillId(Lakin_Salamander);
        addKillId(Death_Fire);
        addQuestItem(FLARE_SHARD);
        addQuestItem(FREEZING_SHARD);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("30376-03.htm".equalsIgnoreCase(event) && st.getState() == CREATED) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("30376-08.htm".equalsIgnoreCase(event) && st.getState() == STARTED) {
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(true);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != NELL)
            return htmltext;
        int _state = st.getState();

        if (_state == CREATED) {
            if (st.getPlayer().getLevel() >= 25) {
                st.setCond(0);
                return "30376-02.htm";
            }
            st.exitCurrentQuest(true);
            return "30376-01.htm";
        }

        if (_state != STARTED)
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

            st.takeItems(FLARE_SHARD);
            st.takeItems(FREEZING_SHARD);
            if (cond == 2) {
                htmltext = "30376-05.htm";
                st.giveItems(ADENA_ID, 12500);
                st.playSound(SOUND_MIDDLE);
                st.setCond(3);
            } else {
                htmltext = "30376-10.htm";
                st.giveItems(ADENA_ID, 63500);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(true);
            }
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState qs) {
        int cond = qs.getCond();
        if (cond != 1 && cond != 3)
            return null;
        int max_count = cond == 1 ? 50 : 200;
        int chance;
        if (freezingShards.containsKey(npc.getNpcId())) {
            chance = freezingShards.get(npc.getNpcId());

            if (qs.getQuestItemsCount(FREEZING_SHARD) < max_count)
                qs.rollAndGive(FREEZING_SHARD, 1, 1, max_count, chance);

        }
        if (flareShards.containsKey(npc.getNpcId())) {
            chance = flareShards.get(npc.getNpcId());
            if (qs.getQuestItemsCount(FLARE_SHARD) < max_count)
                qs.rollAndGive(FLARE_SHARD, 1, 1, max_count, chance);
        }


        if (qs.getQuestItemsCount(FLARE_SHARD) >= max_count
                && qs.getQuestItemsCount(FREEZING_SHARD) >= max_count)
            qs.setCond(cond == 1 ? 2 : 4);

        return null;
    }
}