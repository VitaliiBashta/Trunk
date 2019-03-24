package l2trunk.gameserver.model;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.spawn.SpawnNpcInfo;
import l2trunk.gameserver.templates.spawn.SpawnRange;
import l2trunk.gameserver.templates.spawn.SpawnTemplate;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class HardSpawner extends Spawner {
    private final SpawnTemplate template;
    private final List<NpcInstance> reSpawned = new CopyOnWriteArrayList<>();
    private int _pointIndex;
    private int npcIndex;

    public HardSpawner(SpawnTemplate template) {
        this.template = template;
        spawned = new CopyOnWriteArrayList<>();
    }

    @Override
    public void decreaseCount(NpcInstance oldNpc) {
        oldNpc.setSpawn(null); // [VISTALL] нужно убирать спавн что бы не вызвать зацикливания, и остановки спавна
        oldNpc.deleteMe();

        spawned.remove(oldNpc);

        SpawnNpcInfo npcInfo = getNextNpcInfo();

        NpcInstance npc = npcInfo.getTemplate().getNewInstance();
        npc.setSpawn(this);

        reSpawned.add(npc);

        decreaseCount0(npcInfo.getTemplate(), npc, oldNpc.getDeadTime());
    }

    @Override
    public NpcInstance doSpawn(boolean spawn) {
        SpawnNpcInfo npcInfo = getNextNpcInfo();

        return doSpawn0(npcInfo.getTemplate(), spawn, npcInfo.getParameters());
    }

    @Override
    protected NpcInstance initNpc(NpcInstance mob, boolean spawn, StatsSet set) {
        reSpawned.remove(mob);

        SpawnRange range = template.getSpawnRange(getNextRangeId());
        try {

            mob.setSpawnRange(range);
        } catch (NullPointerException e) {
            System.out.println("NPE");
        }

        return initNpc0(mob, range.getRandomLoc(getReflection().getGeoIndex()), spawn, set);
    }

    @Override
    public int getCurrentNpcId() {
        SpawnNpcInfo npcInfo = template.getNpcId(npcIndex);
        return npcInfo.getTemplate().npcId;
    }

    @Override
    public SpawnRange getCurrentSpawnRange() {
        return template.getSpawnRange(_pointIndex);
    }

    @Override
    public void respawnNpc(NpcInstance oldNpc) {
        initNpc(oldNpc, true, StatsSet.EMPTY);
    }

    @Override
    public void deleteAll() {
        super.deleteAll();

        for (NpcInstance npc : reSpawned) {
            npc.setSpawn(null);
            npc.deleteMe();
        }

        reSpawned.clear();
    }

    private synchronized SpawnNpcInfo getNextNpcInfo() {
        int old = npcIndex++;
        if (npcIndex >= template.getNpcSize())
            npcIndex = 0;

        SpawnNpcInfo npcInfo = template.getNpcId(old);
        if (npcInfo.getMax() > 0) {
            int count = 0;
            for (NpcInstance npc : spawned)
                if (npc.getNpcId() == npcInfo.getTemplate().getNpcId())
                    count++;

            if (count >= npcInfo.getMax())
                return getNextNpcInfo();
        }
        return npcInfo;
    }

    private synchronized int getNextRangeId() {
        int old = _pointIndex++;
        if (_pointIndex >= template.getSpawnRangeSize())
            _pointIndex = 0;
        return old;
    }

    @Override
    public HardSpawner clone() {
        HardSpawner spawnDat = new HardSpawner(template);
        spawnDat.setAmount(maximumCount);
        spawnDat.setRespawnDelay(respawnDelay, respawnDelayRandom);
        spawnDat.setRespawnTime(0);
        return spawnDat;
    }
}
