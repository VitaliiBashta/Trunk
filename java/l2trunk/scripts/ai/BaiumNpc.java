package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.Earthquake;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;

public final class BaiumNpc extends DefaultAI {
    private static final int BAIUM_EARTHQUAKE_TIMEOUT = 1000 * 60 * 15; // 15 min
    private long _wait_timeout = 0;

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
            actor.getAroundCharacters(5000, 10000)
                    .filter(GameObject::isPlayer)
                    .forEach(cha -> cha.sendPacket(eq));
        }
        return false;
    }

    @Override
    public boolean randomWalk() {
        return false;
    }
}