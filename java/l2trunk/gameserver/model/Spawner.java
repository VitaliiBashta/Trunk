package l2trunk.gameserver.model;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.events.EventOwner;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.taskmanager.SpawnTaskManager;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.templates.spawn.SpawnRange;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class Spawner extends EventOwner implements Cloneable {
    private static final Logger _log = LoggerFactory.getLogger(Spawner.class);
    private static final int MIN_RESPAWN_DELAY = 20;
    int maximumCount;
    private int referenceCount;
    private int currentCount;
    private int scheduledCount;

    int respawnDelay;
    int respawnDelayRandom;
    List<NpcInstance> spawned;
    Reflection reflection = ReflectionManager.DEFAULT;
    private int _respawnTime;
    private boolean doRespawn;
    private NpcInstance _lastSpawn;

    public void decreaseScheduledCount() {
        if (scheduledCount > 0)
            scheduledCount--;
    }

    public boolean isDoRespawn() {
        return doRespawn;
    }

    public Reflection getReflection() {
        return reflection;
    }

    public Spawner setReflection(Reflection reflection) {
        this.reflection = reflection;
        return this;
    }

    public int getRespawnDelay() {
        return respawnDelay;
    }

    public Spawner setRespawnDelay(int respawnDelay) {
        setRespawnDelay(respawnDelay, 0);
        return this;
    }

    private int getRespawnDelayWithRnd() {
        return respawnDelayRandom == 0 ? respawnDelay : Rnd.get(respawnDelay - respawnDelayRandom, respawnDelay);
    }

    public int getRespawnTime() {
        return _respawnTime;
    }

    public void setRespawnTime(int respawnTime) {
        _respawnTime = respawnTime;
    }

    public NpcInstance getLastSpawn() {
        return _lastSpawn;
    }

    public Spawner setAmount(int amount) {
        if (referenceCount == 0)
            referenceCount = amount;
        maximumCount = amount;
        return this;
    }

    public void deleteAll() {
        stopRespawn();
        spawned.forEach(GameObject::deleteMe);
        spawned.clear();
        _respawnTime = 0;
        scheduledCount = 0;
        currentCount = 0;
    }

    //-----------------------------------------------------------------------------------------------------------------------------------
    public abstract void decreaseCount(NpcInstance oldNpc);

    public abstract NpcInstance doSpawn(boolean spawn);

    public abstract void respawnNpc(NpcInstance oldNpc);

    abstract NpcInstance initNpc(NpcInstance mob, boolean spawn, StatsSet set);

    public abstract int getCurrentNpcId();

    public abstract SpawnRange getCurrentSpawnRange();

    //-----------------------------------------------------------------------------------------------------------------------------------
    public int init() {
        while (currentCount + scheduledCount < maximumCount)
            doSpawn(false);

        doRespawn = true;

        return currentCount;
    }

    public List<NpcInstance> initAndReturn() {
        List<NpcInstance> spawnedNpcs = new ArrayList<>();
        while (currentCount + scheduledCount < maximumCount)
            spawnedNpcs.add(doSpawn(false));

        doRespawn = true;

        return spawnedNpcs;
    }

    public NpcInstance spawnOne() {
        return doSpawn(false);
    }

    public Spawner stopRespawn() {
        doRespawn = false;
        return this;
    }

    public void startRespawn() {
        doRespawn = true;
    }

    //-----------------------------------------------------------------------------------------------------------------------------------
    public List<NpcInstance> getAllSpawned() {
        return spawned;
    }

    public NpcInstance getFirstSpawned() {
        List<NpcInstance> npcs = getAllSpawned();
        return npcs.size() > 0 ? npcs.get(0) : null;
    }

    public Spawner setRespawnDelay(int respawnDelay, int respawnDelayRandom) {
        if (respawnDelay < 0)
            _log.warn("respawn delay is negative");
        this.respawnDelay = respawnDelay;
        this.respawnDelayRandom = respawnDelayRandom;
        return this;
    }

    //-----------------------------------------------------------------------------------------------------------------------------------
    NpcInstance doSpawn0(NpcTemplate template, boolean spawn, StatsSet set) {
        if (template.type.equals("Minion") || template.type.equals("Pet")) {
            currentCount++;
            return null;
        }

        NpcInstance tmp = template.getNewInstance();

        if (!spawn)
            spawn = _respawnTime <= System.currentTimeMillis() / 1000 + MIN_RESPAWN_DELAY;

        return initNpc(tmp, spawn, set);
    }

    NpcInstance initNpc0(NpcInstance mob, Location newLoc, boolean spawn, StatsSet set) {
        mob.setParameters(set);

        // Set the HP and MP of the L2NpcInstance to the max
        mob.setFullHpMp();

        // Link the L2NpcInstance to this L2Spawn
        mob.setSpawn(this);

        // save spawned points
        mob.setSpawnedLoc(newLoc);

        // Является ли моб "подземным" мобом?
        mob.setUnderground(GeoEngine.getHeight(newLoc, getReflection().getGeoIndex()) < GeoEngine.getHeight(newLoc.clone().changeZ(5000), getReflection().getGeoIndex()));

        getEvents().forEach(mob::addEvent);

        if (spawn) {
            // Спавнится в указанном отражении
            mob.setReflection(getReflection());

            if (mob instanceof MonsterInstance)
                ((MonsterInstance) mob).setChampion();

            // Init other values of the L2NpcInstance (ex : from its L2CharTemplate for INT, STR, DEX...) and add it in the world as a visible object
            mob.spawnMe(newLoc);

            // Increase the current number of L2NpcInstance managed by this L2Spawn
            currentCount++;
        } else {
            mob.setLoc(newLoc);

            // Update the current number of SpawnTask in progress or stand by of this L2Spawn
            scheduledCount++;

            SpawnTaskManager.INSTANCE.addSpawnTask(mob, _respawnTime * 1000L - System.currentTimeMillis());
        }

        spawned.add(mob);
        _lastSpawn = mob;
        return mob;
    }

    void decreaseCount0(NpcTemplate template, NpcInstance spawnedNpc, long deadTime) {
        currentCount--;

        if (currentCount < 0)
            currentCount = 0;

        if (respawnDelay == 0)
            return;

        if (doRespawn && scheduledCount + currentCount < maximumCount) {
            // Update the current number of SpawnTask in progress or stand by of this L2Spawn
            scheduledCount++;

            long delay = (long) (template.isRaid ? Config.ALT_RAID_RESPAWN_MULTIPLIER * getRespawnDelayWithRnd() : getRespawnDelayWithRnd()) * 1000L;
            delay = Math.max(1000, delay - deadTime);

            _respawnTime = (int) ((System.currentTimeMillis() + delay) / 1000);

            SpawnTaskManager.INSTANCE.addSpawnTask(spawnedNpc, delay);
        }
    }
}
