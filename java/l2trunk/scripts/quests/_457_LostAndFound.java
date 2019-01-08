package l2trunk.scripts.quests;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;

public final class _457_LostAndFound extends Quest implements ScriptFile {
    private static final int RESET_HOUR = 6;
    private static final int RESET_MIN = 30;
    private static final int GUMIEL =32759;

    private ScheduledFuture<?> FollowTask;

    public _457_LostAndFound() {
        super(true);
        addStartNpc(GUMIEL);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        Player player = st.getPlayer();
        if (event.equalsIgnoreCase("lost_villager_q0457_06.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
            DefaultAI.namechar = player.getName();
            if (DefaultAI.namechar != null) {
                if (FollowTask != null)
                    FollowTask.cancel(false);
                FollowTask = null;
                FollowTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new Follow(npc, player, st), 10, 1000);
            }
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        Player player = st.getPlayer();
        int npcId = npc.getNpcId();
        int state = st.getState();
        int cond = st.getCond();
        if (npcId == GUMIEL) {
            if (state == 1) {
                if (DefaultAI.namechar != null && DefaultAI.namechar != player.getName())
                    return "lost_villager_q0457_01a.htm";
                String req = st.getPlayer().getVar("NextQuest457") == null || st.getPlayer().getVar("NextQuest457").equalsIgnoreCase("null") ? "0" : st.getPlayer().getVar("NextQuest457");
                if (Long.parseLong(req) > System.currentTimeMillis())
                    return "lost_villager_q0457_02.htm";
                if (st.getPlayer().getLevel() >= 82)
                    return "lost_villager_q0457_01.htm";
                return "lost_villager_q0457_03.htm";
            }
            if (state == 2) {
                if (DefaultAI.namechar != null && !DefaultAI.namechar.equals(player.getName()))
                    return "lost_villager_q0457_01a.htm";
                if (cond == 2) {
                    st.giveItems(15716, 1);
                    st.unset("cond");
                    st.playSound(SOUND_FINISH);
                    st.setState(CREATED);
                    DefaultAI.namechar = null;
                    npc.deleteMe();
                    Calendar reDo = Calendar.getInstance();
                    reDo.set(Calendar.MINUTE, RESET_MIN);
                    if (reDo.get(Calendar.HOUR_OF_DAY) >= RESET_HOUR)
                        reDo.add(Calendar.DATE, 1);
                    reDo.set(Calendar.HOUR_OF_DAY, RESET_HOUR);
                    st.getPlayer().setVar("NextQuest457", String.valueOf(reDo.getTimeInMillis()), -1);
                    return "lost_villager_q0457_09.htm";
                }
                if (cond == 1)
                    return "lost_villager_q0457_08.htm";
            }
        }
        return "noquest";
    }

    private void checkInRadius(QuestState st, NpcInstance npc) {
        NpcInstance quest0457 = GameObjectsStorage.getByNpcId(_457_LostAndFound.GUMIEL);
        if (npc.getRealDistance3D(quest0457) <= 150) {
            st.setCond(2);
            if (FollowTask != null)
                FollowTask.cancel(false);
            FollowTask = null;
            npc.stopMove();
        }
    }

    private class Follow implements Runnable {
        private final NpcInstance npc;
        private final Player player;
        private final QuestState st;

        private Follow(NpcInstance npc, Player pl, QuestState questState) {
            this.npc = npc;
            player = pl;
            st = questState;
        }

        @Override
        public void run() {
            npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player, 150);
            checkInRadius(st, npc);
        }
    }
}