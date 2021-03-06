package l2trunk.gameserver.templates.spawn;

import java.util.ArrayList;
import java.util.List;

public final class SpawnTemplate {
    private final int _count;
    private final int _respawn;
    private final int _respawnRandom;

    private final List<SpawnNpcInfo> npcList = new ArrayList<>(1);
    private final List<SpawnRange> _spawnRangeList = new ArrayList<>(1);

    public SpawnTemplate(int count, int respawn, int respawnRandom) {
        _count = count;
        _respawn = respawn;
        _respawnRandom = respawnRandom;
    }

    //----------------------------------------------------------------------------------------------------------
    public void addSpawnRange(SpawnRange range) {
        _spawnRangeList.add(range);
    }

    public SpawnRange getSpawnRange(int index) {
        return _spawnRangeList.get(index);
    }

    //----------------------------------------------------------------------------------------------------------
    public void addNpc(SpawnNpcInfo info) {
        npcList.add(info);
    }

    public SpawnNpcInfo getNpcId(int index) {
        return npcList.get(index);
    }
    //----------------------------------------------------------------------------------------------------------

    public int getNpcSize() {
        return npcList.size();
    }

    public int getSpawnRangeSize() {
        return _spawnRangeList.size();
    }

    public int getCount() {
        return _count;
    }

    public int getRespawn() {
        return _respawn;
    }

    public int getRespawnRandom() {
        return _respawnRandom;
    }

}
