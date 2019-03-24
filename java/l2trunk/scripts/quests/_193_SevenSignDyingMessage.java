package l2trunk.scripts.quests;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExStartScenePlayer;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

import java.util.HashMap;
import java.util.Map;

public final class _193_SevenSignDyingMessage extends Quest {
    // NPCs
    private static final int Hollint = 30191;
    private static final int Cain = 32569;
    private static final int Eric = 32570;
    private static final int SirGustavAthebaldt = 30760;

    // MOBs
    private static final int ShilensEvilThoughts = 27343;

    // ITEMS
    private static final int JacobsNecklace = 13814;
    private static final int DeadmansHerb = 13813;
    private static final int SculptureofDoubt = 14352;

    private static final Map<Integer, Integer> spawns = new HashMap<>();

    public _193_SevenSignDyingMessage() {
        addStartNpc(Hollint);
        addTalkId(Cain, Eric, SirGustavAthebaldt);
        addKillId(ShilensEvilThoughts);
        addQuestItem(JacobsNecklace, DeadmansHerb, SculptureofDoubt);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Player player = st.player;
        String htmltext = event;
        if ("30191-02.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            st.giveItems(JacobsNecklace);
        } else if ("32569-05.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("32570-02.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.giveItems(DeadmansHerb);
            st.playSound(SOUND_MIDDLE);
        } else if ("30760-02.htm".equalsIgnoreCase(event)) {
            if (player.getBaseClassId() == player.getActiveClassId()) {
                st.addExpAndSp(25000000, 2500000);
                st.complete();
                st.finish();
                st.playSound(SOUND_FINISH);
            } else
                return "subclass_forbidden.htm";
        } else if ("close_your_eyes".equalsIgnoreCase(event)) {
            st.setCond(4);
            st.takeItems(DeadmansHerb);
            st.playSound(SOUND_MIDDLE);
            player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ_DYING_MASSAGE);
            return "";
        } else if ("32569-09.htm".equalsIgnoreCase(event)) {
            htmltext = "32569-09.htm";
            Functions.npcSay(npc, st.player.getName() + "! That stranger must be defeated. Here is the ultimate help!");
            NpcInstance mob = st.addSpawn(ShilensEvilThoughts, Location.of(82425, 47232, -3216), 0, 180000);
            spawns.put(player.objectId(), mob.objectId());
            mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 100000);
        } else if ("32569-13.htm".equalsIgnoreCase(event)) {
            st.setCond(6);
            st.takeItems(SculptureofDoubt);
            st.playSound(SOUND_MIDDLE);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        int id = st.getState();
        Player player = st.player;
        if (npcId == Hollint) {
            if (id == CREATED) {
                if (player.getLevel() < 79) {
                    st.exitCurrentQuest();
                    return "30191-00.htm";
                }
                if (player.isQuestCompleted(_192_SevenSignSeriesOfDoubt.class)) {
                    st.exitCurrentQuest();
                    return "noquest";
                }
                return "30191-01.htm";
            } else if (cond == 1)
                return "30191-03.htm";
        } else if (npcId == Cain) {
            if (cond == 1)
                return "32569-01.htm";
            else if (cond == 2)
                return "32569-06.htm";
            else if (cond == 3)
                return "32569-07.htm";
            else if (cond == 4) {
                Integer obj_id = spawns.get(player.objectId());
                NpcInstance mob = obj_id != null ? GameObjectsStorage.getNpc(obj_id) : null;
                if (mob == null || mob.isDead())
                    return "32569-08.htm";
                else
                    return "32569-09.htm";
            } else if (cond == 5)
                return "32569-10.htm";
            else if (cond == 6)
                return "32569-13.htm";
        } else if (npcId == Eric) {
            if (cond == 2)
                return "32570-01.htm";
            else if (cond == 3)
                return "32570-03.htm";
        } else if (npcId == SirGustavAthebaldt)
            if (cond == 6)
                return "30760-01.htm";
        return "noquest";
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (npcId == ShilensEvilThoughts && cond == 4) {
            Integer obj_id = spawns.get(st.player.objectId());
            if (obj_id != null && obj_id == npc.objectId()) {
                spawns.remove(st.player.objectId());
                st.setCond(5);
                st.playSound(SOUND_ITEMGET);
                st.giveItems(SculptureofDoubt);
            }
        }
    }
}