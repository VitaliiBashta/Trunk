package l2trunk.gameserver.model;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.templates.spawn.SpawnRange;
import l2trunk.gameserver.utils.Location;

import java.util.ArrayList;

public final class SimpleSpawner extends Spawner {
    private NpcTemplate npcTemplate;
    private Location loc;
    private Territory territory;

    private SimpleSpawner(NpcTemplate mobTemplate) {
        if (mobTemplate != null) {
//            throw new NullPointerException();

            npcTemplate = mobTemplate;
            spawned = new ArrayList<>(1);
        }
    }

    public SimpleSpawner(int npcId) {
        this(NpcHolder.getTemplate(npcId));
    }

    /**
     * Return the maximum number of L2NpcInstance that this L2Spawn can manage.<BR><BR>
     */
    public int getAmount() {
        return maximumCount;
    }

    /**
     * Set the Identifier of the location area where L2NpcInstance can be spawned.<BR><BR>
     */
    public SimpleSpawner setTerritory(Territory territory) {
        this.territory = territory;
        return this;
    }

    /**
     * Return the position of the spawn point.<BR><BR>
     */
    public Location getLoc() {
        return loc;
    }

    /**
     * Set the position(x, y, z, heading) of the spawn point.
     *
     * @param loc Location
     */
    public SimpleSpawner setLoc(Location loc) {
        this.loc = loc;
        return this;
    }

    /**
     * Return the Identifier of the L2NpcInstance manage by this L2Spwan contained in the L2NpcTemplate.<BR><BR>
     */
    @Override
    public int getCurrentNpcId() {
        return npcTemplate.getNpcId();
    }

    @Override
    public SpawnRange getCurrentSpawnRange() {
        if (loc.x == 0 && loc.y == 0)
            return territory;
        return loc;
    }

    @Override
    public void decreaseCount(NpcInstance oldNpc) {
        decreaseCount0(npcTemplate, oldNpc, oldNpc.getDeadTime());
    }

    @Override
    public NpcInstance doSpawn(boolean spawn) {
        return doSpawn0(npcTemplate, spawn, StatsSet.EMPTY);
    }

    @Override
    protected NpcInstance initNpc(NpcInstance mob, boolean spawn, StatsSet set) {
        Location newLoc;

        if (territory != null) {
            newLoc = territory.getRandomLoc(reflection.getGeoIndex());
            newLoc.setH(Rnd.get(0xFFFF));
        } else {
            newLoc = loc;

            if (newLoc.h == -1) newLoc.h = Rnd.get(0xFFFF);
        }

        return initNpc0(mob, newLoc, spawn, set);
    }

    @Override
    public void respawnNpc(NpcInstance oldNpc) {
        oldNpc.refreshID();
        initNpc(oldNpc, true, StatsSet.EMPTY);
    }

    public SimpleSpawner newInstance() {
        return (SimpleSpawner) new SimpleSpawner(npcTemplate)
                .setTerritory(territory)
                .setLoc(loc)
                .setAmount(maximumCount)
                .setRespawnDelay(respawnDelay, respawnDelayRandom);
    }
}