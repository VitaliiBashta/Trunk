package l2trunk.scripts.ai;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CharacterAI;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.PlaySound;

public final class MusicBox extends CharacterAI {
    public MusicBox(NpcInstance actor) {
        super(actor);
        ThreadPoolManager.INSTANCE.schedule(() -> World.getAroundPlayers(getActor(), 5000, 5000)
                .forEach(player -> player.broadcastPacket(new PlaySound("TP04_F"))), 1000);
    }
}