package l2trunk.scripts.ai;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CharacterAI;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.PlaySound;

/**
 * - AI for Music Box (32437).
 * - Plays music.
 * - AI has been tested and works.
 */
public final class MusicBox extends CharacterAI {
    public MusicBox(NpcInstance actor) {
        super(actor);
        ThreadPoolManager.INSTANCE.schedule(() -> World.getAroundPlayers(getActor(), 5000, 5000).forEach(player ->
                player.broadcastPacket(new PlaySound("TP04_F"))), 1000);
    }
}