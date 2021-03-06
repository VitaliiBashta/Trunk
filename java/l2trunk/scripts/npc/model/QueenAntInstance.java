package l2trunk.scripts.npc.model;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.instances.BossInstance;
import l2trunk.gameserver.model.instances.MinionInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.PlaySound;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.List;

public final class QueenAntInstance extends BossInstance {
    private static final int Queen_Ant_Larva = 29002;

    private final List<SimpleSpawner> _spawns = new ArrayList<>();
    private NpcInstance Larva = null;

    public QueenAntInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    public NpcInstance getLarva() {
        if (Larva == null) {
            Larva = SpawnLarva();
        }
        return Larva;
    }

    @Override
    protected int getKilledInterval(MinionInstance minion) {
        return minion.getNpcId() == 29003 ? 10000 : 280000 + Rnd.get(40000);
    }

    @Override
    protected void onDeath(Creature killer) {
        broadcastPacketToOthers(new PlaySound(PlaySound.Type.MUSIC, "BS02_D", 1, 0, getLoc()));
        Functions.deSpawnNPCs(_spawns);
        Larva = null;
        super.onDeath(killer);
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        getLarva();
        broadcastPacketToOthers(new PlaySound(PlaySound.Type.MUSIC, "BS01_A", 1, 0, getLoc()));

        // Synerge - On Queen Ant spawn teleport every getPlayer that is inside the zone to the closest town
        final Zone zone = getZone(ZoneType.epic);
        if (zone != null) {
            zone.getInsidePlayers().forEach(Player::teleToClosestTown);
        }
    }

    private NpcInstance SpawnLarva() {
        SimpleSpawner sp = (SimpleSpawner) new SimpleSpawner(QueenAntInstance.Queen_Ant_Larva)
                .setLoc(Location.of(-21600, 179482, -5846, Rnd.get(0, 0xFFFF)))
                .setAmount(1)
                .setRespawnDelay(0);
        _spawns.add(sp);
        return sp.spawnOne();
    }
}