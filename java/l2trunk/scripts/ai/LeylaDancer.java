package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.SocialAction;
import l2trunk.gameserver.utils.Location;

/**
 * @author Grivesky
 * - AI for Dancers (32424, 32425, 32426, 32427, 32428, 32432).
 * - Indicates the social sphere, and shout in the chat.
 * - AI is tested and works.
 */
public final class LeylaDancer extends DefaultAI {
    private static int count = 0;

    public LeylaDancer(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        ThreadPoolManager.INSTANCE.schedule(new ScheduleStart(), 5000);
        ThreadPoolManager.INSTANCE.schedule(new ScheduleMoveFinish(), 220000);
        super.onEvtSpawn();
    }

    private class ScheduleStart implements Runnable {
        @Override
        public void run() {
            NpcInstance actor = getActor();
            if (actor != null) {
                if (count < 50) {
                    count++;
                    actor.broadcastPacket(new SocialAction(actor.getObjectId(), Rnd.get(1, 2)));
                    ThreadPoolManager.INSTANCE.schedule(new ScheduleStart(), 3600);
                } else {
                    count = 0;
                }
            }
        }
    }

    private class ScheduleMoveFinish implements Runnable {
        @Override
        public void run() {
            NpcInstance actor = getActor();
            if (actor != null) {
                // actor.say(NPC_STRING.WE_LOVE_YOU, CHAT_TYPES.ALL);
                addTaskMove(new Location(-56594, -56064, -1988), true);
                doTask();
            }
        }
    }
}