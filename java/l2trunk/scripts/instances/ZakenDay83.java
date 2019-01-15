package l2trunk.scripts.instances;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.ExSendUIEvent;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class ZakenDay83 extends Reflection {
    private static final int Anchor = 32468;
    private static final int UltraDayZaken = 29181;
    private static final int sealedVorpalRing = 15763;
    private static final int sealedVorpalEarring = 15764;
    private static final int sealedVorpalNeckace = 15765;
    private static final List<Location> zakenTp = List.of(
            new Location(55272, 219080, -2952),
            new Location(55272, 219080, -3224),
            new Location(55272, 219080, -3496));
    private static final Location zakenSpawn = new Location(55048, 216808, -3772);
    private final DeathListener _deathListener = new DeathListener();
    private long startedTime;

    @Override
    protected void onCreate() {
        super.onCreate();
        addSpawnWithoutRespawn(Anchor, Rnd.get(zakenTp), 0);
        NpcInstance zaken = addSpawnWithoutRespawn(UltraDayZaken, zakenSpawn, 0);
        zaken.addListener(_deathListener);
        zaken.setInvul(true);
        startedTime = System.currentTimeMillis();
    }

    @Override
    public void onPlayerEnter(Player player) {
        super.onPlayerEnter(player);
        player.sendPacket(new ExSendUIEvent(player, false, true, (int) (System.currentTimeMillis() - startedTime) / 1000, 0, NpcString.ELAPSED_TIME));
    }

    @Override
    public void onPlayerExit(Player player) {
        super.onPlayerExit(player);
        player.sendPacket(new ExSendUIEvent(player, true, true, 0, 0));
    }

    private class DeathListener implements OnDeathListener {
        @Override
        public void onDeath(Creature self, Creature killer) {
            if (self.isNpc() && self.getNpcId() == UltraDayZaken) {
                long timePassed = (System.currentTimeMillis() - startedTime) / 60 / 1000;

                final int reward = timePassed < 5 ? sealedVorpalNeckace :
                        timePassed < 10 ? sealedVorpalEarring :
                                timePassed < 15 ? sealedVorpalRing : 0;
                if (reward != 0)
                    getPlayers().forEach(p -> {
                        if (Rnd.chance(10))
                            ItemFunctions.addItem(p, reward, 1, true, "ZakenDay83");

                    });
                getPlayers().forEach(p ->
                        p.sendPacket(new ExSendUIEvent(p, true, true, 0, 0)));
            }
        }
    }
}