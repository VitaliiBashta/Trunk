package l2trunk.scripts.ai;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CharacterAI;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.PlaySound;

/**
 * - AI for Music Box (32437).
 * - Plays music.
 * - AI has been tested and works.
 */
class MusicBox extends CharacterAI {
    public MusicBox(NpcInstance actor) {
        super(actor);
        ThreadPoolManager.INSTANCE().schedule(new ScheduleMusic(), 1000);
    }

    private class ScheduleMusic implements Runnable {
        @Override
        public void run() {
            NpcInstance actor = (NpcInstance) getActor();
            for (Player player : World.getAroundPlayers(actor, 5000, 5000))
                player.broadcastPacket(new PlaySound("TP04_F"));
        }
    }
}