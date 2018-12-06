package l2trunk.gameserver.model;

import l2trunk.commons.collections.MultiValueSet;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.events.EventOwner;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.instances.MinionInstance;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.PetInstance;
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
    int _referenceCount;
    int _currentCount;
    int _scheduledCount;

    int respawnDelay;
    int _respawnDelayRandom;
    List<NpcInstance> spawned;
    Reflection reflection = ReflectionManager.DEFAULT;
    private int _respawnTime;
    private boolean doRespawn;
    private NpcInstance _lastSpawn;

    public void decreaseScheduledCount() {
        if (_scheduledCount > 0)
            _scheduledCount--;
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
        return _respawnDelayRandom == 0 ? respawnDelay : Rnd.get(respawnDelay - _respawnDelayRandom, respawnDelay);
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
        if (_referenceCount == 0)
            _referenceCount = amount;
        maximumCount = amount;
        return this;
    }

    public void deleteAll() {
        stopRespawn();
        for (NpcInstance npc : spawned)
            npc.deleteMe();
        spawned.clear();
        _respawnTime = 0;
        _scheduledCount = 0;
        _currentCount = 0;
    }

    //-----------------------------------------------------------------------------------------------------------------------------------
    public abstract void decreaseCount(NpcInstance oldNpc);

    public abstract NpcInstance doSpawn(boolean spawn);

    public abstract void respawnNpc(NpcInstance oldNpc);

    protected abstract NpcInstance initNpc(NpcInstance mob, boolean spawn, MultiValueSet<String> set);

    public abstract int getCurrentNpcId();

    public abstract SpawnRange getCurrentSpawnRange();

    //-----------------------------------------------------------------------------------------------------------------------------------
    public int init() {
        while (_currentCount + _scheduledCount < maximumCount)
            doSpawn(false);

        doRespawn = true;

        return _currentCount;
    }

    public List<NpcInstance> initAndReturn() {
        List<NpcInstance> spawnedNpcs = new ArrayList<>();
        while (_currentCount + _scheduledCount < maximumCount)
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
        _respawnDelayRandom = respawnDelayRandom;
        return this;
    }

    //-----------------------------------------------------------------------------------------------------------------------------------
    NpcInstance doSpawn0(NpcTemplate template, boolean spawn, MultiValueSet<String> set) {
        if (template.isInstanceOf(PetInstance.class) || template.isInstanceOf(MinionInstance.class)) {
            _currentCount++;
            return null;
        }

        NpcInstance tmp = template.getNewInstance();
        if (tmp == null)
            return null;

        if (!spawn)
            spawn = _respawnTime <= System.currentTimeMillis() / 1000 + MIN_RESPAWN_DELAY;

        return initNpc(tmp, spawn, set);
    }

    NpcInstance initNpc0(NpcInstance mob, Location newLoc, boolean spawn, MultiValueSet<String> set) {
        mob.setParameters(set);

        // Set the HP and MP of the L2NpcInstance to the max
        mob.setFullHpMp();

        // Link the L2NpcInstance to this L2Spawn
        mob.setSpawn(this);

        // save spawned points
        mob.setSpawnedLoc(newLoc);

        // Является ли моб "подземным" мобом?
        mob.setUnderground(GeoEngine.getHeight(newLoc, getReflection().getGeoIndex()) < GeoEngine.getHeight(newLoc.clone().changeZ(5000), getReflection().getGeoIndex()));

        for (GlobalEvent e : getEvents())
            mob.addEvent(e);

        if (spawn) {
            // Спавнится в указанном отражении
            mob.setReflection(getReflection());

            if (mob.isMonster())
                ((MonsterInstance) mob).setChampion();

            // Init other values of the L2NpcInstance (ex : from its L2CharTemplate for INT, STR, DEX...) and add it in the world as a visible object
            mob.spawnMe(newLoc);

            // Increase the current number of L2NpcInstance managed by this L2Spawn
            _currentCount++;
        } else {
            mob.setLoc(newLoc);

            // Update the current number of SpawnTask in progress or stand by of this L2Spawn
            _scheduledCount++;

            SpawnTaskManager.INSTANCE.addSpawnTask(mob, _respawnTime * 1000L - System.currentTimeMillis());
        }

        spawned.add(mob);
        _lastSpawn = mob;
        return mob;
    }

    void decreaseCount0(NpcTemplate template, NpcInstance spawnedNpc, long deadTime) {
        _currentCount--;

        if (_currentCount < 0)
            _currentCount = 0;

        if (respawnDelay == 0)
            return;

        if (doRespawn && _scheduledCount + _currentCount < maximumCount) {
            // Update the current number of SpawnTask in progress or stand by of this L2Spawn
            _scheduledCount++;

            long delay = (long) (template.isRaid ? Config.ALT_RAID_RESPAWN_MULTIPLIER * getRespawnDelayWithRnd() : getRespawnDelayWithRnd()) * 1000L;
            delay = Math.max(1000, delay - deadTime);

            _respawnTime = (int) ((System.currentTimeMillis() + delay) / 1000);

            SpawnTaskManager.INSTANCE.addSpawnTask(spawnedNpc, delay);
        }
    }
}
