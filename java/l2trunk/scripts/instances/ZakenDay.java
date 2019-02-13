package l2trunk.scripts.instances;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.network.serverpackets.ExSendUIEvent;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class ZakenDay extends Reflection {
    private static final int Anchor = 32468;
    private static final List<Location> zakenTp = List.of(
            new Location(55272, 219080, -2952),
            new Location(55272, 219080, -3224),
            new Location(55272, 219080, -3496));
    private long _savedTime;

    @Override
    protected void onCreate() {
        super.onCreate();
        addSpawnWithoutRespawn(Anchor, Rnd.get(zakenTp));
        _savedTime = System.currentTimeMillis();
    }

    @Override
    public void onPlayerEnter(Player player) {
        super.onPlayerEnter(player);
        player.sendPacket(new ExSendUIEvent(player, false, true, (int) (System.currentTimeMillis() - _savedTime) / 1000, 0, NpcString.ELAPSED_TIME));
    }

    @Override
    public void onPlayerExit(Player player) {
        super.onPlayerExit(player);
        player.sendPacket(new ExSendUIEvent(player, true, true, 0, 0));
    }
}