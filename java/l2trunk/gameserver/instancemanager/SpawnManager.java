package l2trunk.gameserver.instancemanager;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.GameTimeController;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.data.xml.holder.SpawnHolder;
import l2trunk.gameserver.listener.game.OnDayNightChangeListener;
import l2trunk.gameserver.listener.game.OnSSPeriodListener;
import l2trunk.gameserver.model.HardSpawner;
import l2trunk.gameserver.model.Spawner;
import l2trunk.gameserver.model.entity.SevenSigns;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.templates.spawn.SpawnTemplate;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum SpawnManager {
    INSTANCE;
    private static final Logger _log = LoggerFactory.getLogger(SpawnManager.class);
    private static final String DAWN_GROUP = "dawn_spawn";
    private static final String DUSK_GROUP = "dusk_spawn";
    private static final String DAWN_GROUP2 = "dawn_spawn2";
    private static final String DUSK_GROUP2 = "dusk_spawn2";
    //    private static final SpawnManager _instance = new SpawnManager();
    private final Map<String, List<Spawner>> spawns = new ConcurrentHashMap<>();
    private final Listeners listeners = new Listeners();
    private final Map<Integer, Integer> spawnCountByNpcId = new HashMap<>();
    private final Map<Integer, List<Location>> spawnLocationsByNpcId = new HashMap<>();

    SpawnManager() {
//        for (Map.Entry<String, List<SpawnTemplate>> entry :
        SpawnHolder.getSpawns().forEach(this::fillSpawn);
        GameTimeController.INSTANCE.addListener(listeners);
        SevenSigns.INSTANCE.addListener(listeners);
    }

    private void fillSpawn(String group, List<SpawnTemplate> templateList) {
        if (Config.DONTLOADSPAWN) {
            return;
        }

        List<Spawner> spawnerList = spawns.get(group);
        if (spawnerList == null) {
            spawns.put(group, spawnerList = new ArrayList<>(templateList.size()));
        }

        for (SpawnTemplate template : templateList) {
            HardSpawner spawner = new HardSpawner(template);
            spawnerList.add(spawner);

            NpcTemplate npcTemplate = NpcHolder.getTemplate(spawner.getCurrentNpcId());

            int toAdd;
            if ((Config.RATE_MOB_SPAWN > 1) && (npcTemplate.type.equals("MonsterInstance")) && (npcTemplate.level >= Config.RATE_MOB_SPAWN_MIN_LEVEL) && (npcTemplate.level <= Config.RATE_MOB_SPAWN_MAX_LEVEL)) {
                toAdd = template.getCount() * Config.RATE_MOB_SPAWN;
                spawner.setAmount(toAdd);
            } else {
                toAdd = template.getCount();
                spawner.setAmount(toAdd);
            }

            if (Config.ALLOW_DROP_CALCULATOR) {
                int currentCount = spawnCountByNpcId.getOrDefault(npcTemplate.getNpcId(), 0);
                spawnCountByNpcId.put(npcTemplate.getNpcId(), currentCount + toAdd);
            }

            spawner.setRespawnDelay(template.getRespawn(), template.getRespawnRandom());
            spawner.setReflection(ReflectionManager.DEFAULT);
            spawner.setRespawnTime(0);

            if (Config.ALLOW_DROP_CALCULATOR) {
                Location spawnLoc = spawner.getCurrentSpawnRange().getRandomLoc(ReflectionManager.DEFAULT.getGeoIndex());
                if (!spawnLocationsByNpcId.containsKey(npcTemplate.getNpcId()))
                    spawnLocationsByNpcId.put(npcTemplate.getNpcId(), new ArrayList<>());
                spawnLocationsByNpcId.get(npcTemplate.getNpcId()).add(spawnLoc);
            }


            if (npcTemplate.isRaid && group.equals("NONE")) {
                RaidBossSpawnManager.INSTANCE.addNewSpawn(npcTemplate.getNpcId(), spawner);
            }
        }

    }

    public void spawnAll() {
        spawn("NONE");
        if (Config.ALLOW_EVENT_GATEKEEPER) {
            spawn("event_gatekeeper");
        }
        if (!Config.ALLOW_CLASS_MASTERS_LIST.isEmpty()) {
            spawn("class_master");
        }
        if (Config.SPAWN_NPC_BUFFER) {
            spawn("npc_buffer");
        }
        if (Config.SPAWN_scrubwoman) {
            spawn("scrubwoman");
        }
        if (Config.SPAWN_CITIES_TREE) {
            spawn("cities_tree");
        }
        if (Config.ALLOW_UPDATE_ANNOUNCER) {
            spawn("update_announcer");
        }
    }

    public void spawn(String group) {
        List<Spawner> spawnerList = spawns.get(group);
        if (spawnerList == null) {
            return;
        }

        int npcSpawnCount = 0;

        for (Spawner spawner : spawnerList) {
            npcSpawnCount += spawner.init();

            if (((npcSpawnCount % 1000) == 0) && (npcSpawnCount != 0)) {
                _log.info("SpawnManager: spawned " + npcSpawnCount + " npc for group: " + group);
            }
        }
        _log.info("SpawnManager: spawned " + npcSpawnCount + " npc; spawns: " + spawnerList.size() + "; group: " + group);
    }

    public void despawn(String group) {
        List<Spawner> spawnerList = spawns.get(group);
        if (spawnerList == null) {
            return;
        }
        spawnerList.forEach(Spawner::deleteAll);
    }

    public List<Spawner> getSpawners(String group) {
        List<Spawner> list = spawns.get(group);
        return list == null ? List.of() : list;
    }

    public int getSpawnedCountByNpc(int npcId) {
        if (!spawnCountByNpcId.containsKey(npcId)) {
            return 0;
        }
        return spawnCountByNpcId.get(npcId);
    }

    public List<Location> getRandomSpawnsByNpc(int npcId) {
        return spawnLocationsByNpcId.get(npcId);
    }

    public void reloadAll() {
        for (List<Spawner> spawnerList : spawns.values()) {
            for (Spawner spawner : spawnerList) {
                spawner.deleteAll();
            }
        }

        RaidBossSpawnManager.INSTANCE.reloadBosses();

        spawnAll();

        // FIXME [VISTALL] come up with another way to
        int mode = 0;
        if (SevenSigns.INSTANCE.getCurrentPeriod() == SevenSigns.PERIOD_SEAL_VALIDATION) {
            mode = SevenSigns.INSTANCE.getCabalHighestScore();
        }

        listeners.onPeriodChange(mode);

        if (GameTimeController.INSTANCE.isNowNight()) {
            listeners.onNight();
        } else {
            listeners.onDay();
        }
    }

    public List<NpcInstance> getAllSpawned(String group) {
        List<NpcInstance> result = new ArrayList<>();
        for (Spawner spawner : getSpawners(group)) {
            result.addAll(spawner.getAllSpawned());
        }
        return result;
    }

    private class Listeners implements OnDayNightChangeListener, OnSSPeriodListener {
        @Override
        public void onDay() {
            despawn("NIGHT");
            spawn("DAY");
        }

        @Override
        public void onNight() {
            despawn("DAY");
            spawn("NIGHT");
        }

        @Override
        public void onPeriodChange(int mode) {
            switch (mode) {
                case 0: // all spawns
                    despawn(DAWN_GROUP);
                    despawn(DUSK_GROUP);
                    spawn(DAWN_GROUP);
                    spawn(DUSK_GROUP);
                    break;
                case 1: // dusk spawns
                    despawn(DAWN_GROUP);
                    despawn(DUSK_GROUP);
                    spawn(DUSK_GROUP);
                    spawn(DUSK_GROUP2);
                    break;
                case 2: // dawn spawns
                    despawn(DAWN_GROUP);
                    despawn(DUSK_GROUP);
                    spawn(DAWN_GROUP);
                    spawn(DAWN_GROUP2);
                    break;
            }
        }
    }
}