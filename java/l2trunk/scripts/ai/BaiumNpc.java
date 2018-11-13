package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.Earthquake;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;

import java.util.List;

public final class BaiumNpc extends DefaultAI {
    private long _wait_timeout = 0;
    private static final int BAIUM_EARTHQUAKE_TIMEOUT = 1000 * 60 * 15; // 15 min

    public BaiumNpc(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        // Is it time to start the earthquake
        if (_wait_timeout < System.currentTimeMillis()) {
            _wait_timeout = System.currentTimeMillis() + BAIUM_EARTHQUAKE_TIMEOUT;
            L2GameServerPacket eq = new Earthquake(actor.getLoc(), 40, 10);
            List<Creature> chars = actor.getAroundCharacters(5000, 10000);
            for (Creature character : chars)
                if (character.isPlayer())
                    character.sendPacket(eq);
        }
        return false;
    }

    @Override
    public boolean randomWalk() {
        return false;
    }
}