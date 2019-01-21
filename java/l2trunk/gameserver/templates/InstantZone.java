package l2trunk.gameserver.templates;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.time.cron.SchedulingPattern;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Territory;
import l2trunk.gameserver.templates.spawn.SpawnTemplate;
import l2trunk.gameserver.utils.Location;

import java.util.List;
import java.util.Map;

public final class InstantZone {
    private final int _id;
    private final String _name;
    private final SchedulingPattern resetReuse;
    private final int _sharedReuseGroup;
    private final int _timelimit;
    private final int _minLevel;
    private final int _maxLevel;
    private final int minParty;
    private final int _maxParty;
    private final boolean _onPartyDismiss;
    private final int _timer;
    private final List<Location> teleportCoords;
    private final Location _returnCoords;
    private final int _mapx;
    private final int _mapy;
    private final Map<Integer, DoorInfo> doors;
    private final Map<String, ZoneInfo> zones;
    private final Map<String, SpawnInfo2> spawns;
    private final List<SpawnInfo> _spawnsInfo;
    private final int _collapseIfEmpty;
    private final int maxChannels;
    private final int _removedItemId;
    private final int _removedItemCount;
    private final boolean _removedItemNecessity;
    private final int _giveItemId;
    private final int _givedItemCount;
    private final int _requiredQuestId;
    private final boolean _setReuseUponEntry;
    private final StatsSet addParams;
    private final InstantZoneEntryType _entryType;
    private final boolean dispelBuffs;

    public InstantZone(int id, String name, SchedulingPattern resetReuse,
                       int sharedReuseGroup, int timelimit, boolean dispelBuffs,
                       int minLevel, int maxLevel, int minParty, int maxParty,
                       int timer, boolean onPartyDismiss, List<Location> tele,
                       Location ret, int mapx, int mapy, Map<Integer, DoorInfo> doors,
                       Map<String, ZoneInfo> zones, Map<String, SpawnInfo2> spawns,
                       List<SpawnInfo> spawnsInfo, int collapseIfEmpty, int maxChannels,
                       int removedItemId, int removedItemCount, boolean removedItemNecessity,
                       int giveItemId, int givedItemCount, int requiredQuestId,
                       boolean setReuseUponEntry, StatsSet params) {
        _id = id;
        _name = name;
        this.resetReuse = resetReuse;
        _sharedReuseGroup = sharedReuseGroup;
        _timelimit = timelimit;
        this.dispelBuffs = dispelBuffs;
        _minLevel = minLevel;
        _maxLevel = maxLevel;
        teleportCoords = tele;
        _returnCoords = ret;
        this.minParty = minParty;
        _maxParty = maxParty;
        _onPartyDismiss = onPartyDismiss;
        _timer = timer;
        _mapx = mapx;
        _mapy = mapy;
        this.doors = doors;
        this.zones = zones;
        _spawnsInfo = spawnsInfo;
        this.spawns = spawns;
        _collapseIfEmpty = collapseIfEmpty;
        this.maxChannels = maxChannels;
        _removedItemId = removedItemId;
        _removedItemCount = removedItemCount;
        _removedItemNecessity = removedItemNecessity;
        _giveItemId = giveItemId;
        _givedItemCount = givedItemCount;
        _requiredQuestId = requiredQuestId;
        _setReuseUponEntry = setReuseUponEntry;
        addParams = params;

        if (getMinParty() == 1)
            _entryType = InstantZoneEntryType.SOLO;
        else if (getMinParty() > 1 && getMaxParty() <= 9)
            _entryType = InstantZoneEntryType.PARTY;
        else if (getMaxParty() > 9)
            _entryType = InstantZoneEntryType.COMMAND_CHANNEL;
        else
            throw new IllegalArgumentException("Invalid type?: " + _name);
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public SchedulingPattern getResetReuse() {
        return resetReuse;
    }

    public boolean isDispelBuffs() {
        return dispelBuffs;
    }

    public int getTimelimit() {
        return _timelimit;
    }

    public int getMinLevel() {
        return _minLevel;
    }

    public int getMaxLevel() {
        return _maxLevel;
    }

    public int getMinParty() {
        return minParty;
    }

    public int getMaxParty() {
        return _maxParty;
    }

    public int getTimerOnCollapse() {
        return _timer;
    }

    public boolean isCollapseOnPartyDismiss() {
        return _onPartyDismiss;
    }

    public Location getTeleportCoord() {
        if (teleportCoords == null || teleportCoords.size() == 0)
            return null;
        if (teleportCoords.size() == 1)   // fast hack?
            return teleportCoords.get(0);
        return teleportCoords.get(Rnd.get(teleportCoords.size()));
    }

    public Location getReturnCoords() {
        return _returnCoords;
    }

    public int getMapX() {
        return _mapx;
    }

    public int getMapY() {
        return _mapy;
    }

    public List<SpawnInfo> getSpawnsInfo() {
        return _spawnsInfo;
    }

    public int getSharedReuseGroup() {
        return _sharedReuseGroup;
    }

    public int getCollapseIfEmpty() {
        return _collapseIfEmpty;
    }

    public int getRemovedItemId() {
        return _removedItemId;
    }

    public int getRemovedItemCount() {
        return _removedItemCount;
    }

    public boolean getRemovedItemNecessity() {
        return _removedItemNecessity;
    }

    public int getGiveItemId() {
        return _giveItemId;
    }

    public int getGiveItemCount() {
        return _givedItemCount;
    }

    public int getRequiredQuestId() {
        return _requiredQuestId;
    }

    public boolean getSetReuseUponEntry() {
        return _setReuseUponEntry;
    }

    public int getMaxChannels() {
        return maxChannels;
    }

    public InstantZoneEntryType getEntryType() {
        return _entryType;
    }

    public Map<Integer, DoorInfo> getDoors() {
        return doors;
    }

    public Map<String, ZoneInfo> getZones() {
        return zones;
    }

    public List<Location> getTeleportCoords() {
        return teleportCoords;
    }

    public Map<String, SpawnInfo2> getSpawns() {
        return spawns;
    }

    public StatsSet getAddParams() {
        return addParams;
    }

    public static class DoorInfo {
        private final DoorTemplate _template;
        private final boolean _opened;
        private final boolean _invul;

        public DoorInfo(DoorTemplate template, boolean opened, boolean invul) {
            _template = template;
            _opened = opened;
            _invul = invul;
        }

        public DoorTemplate getTemplate() {
            return _template;
        }

        public boolean isOpened() {
            return _opened;
        }

        public boolean isInvul() {
            return _invul;
        }
    }

    public static class ZoneInfo {
        private final ZoneTemplate _template;
        private final boolean _active;

        public ZoneInfo(ZoneTemplate template, boolean opened) {
            _template = template;
            _active = opened;
        }

        public ZoneTemplate getTemplate() {
            return _template;
        }

        public boolean isActive() {
            return _active;
        }
    }

    public static class SpawnInfo2 {
        private final List<SpawnTemplate> _template;
        private final boolean _spawned;

        public SpawnInfo2(List<SpawnTemplate> template, boolean spawned) {
            _template = template;
            _spawned = spawned;
        }

        public List<SpawnTemplate> getTemplates() {
            return _template;
        }

        public boolean isSpawned() {
            return _spawned;
        }
    }

    //@Deprecated
    public static class SpawnInfo {
        private final int _spawnType;
        private final int _npcId;
        private final int _count;
        private final int _respawn;
        private final int respawnRnd;
        private final List<Location> coords;
        private final Territory territory;

        public SpawnInfo(int spawnType, int npcId, int count, int respawn, int respawnRnd, List<Location> coords, Territory territory) {
            _spawnType = spawnType;
            _npcId = npcId;
            _count = count;
            _respawn = respawn;
            this.respawnRnd = respawnRnd;
            this.coords = coords;
            this.territory = territory;
        }

        public int getSpawnType() {
            return _spawnType;
        }

        public int getNpcId() {
            return _npcId;
        }

        public int getCount() {
            return _count;
        }

        public int getRespawnDelay() {
            return _respawn;
        }

        public int getRespawnRnd() {
            return respawnRnd;
        }

        public List<Location> getCoords() {
            return coords;
        }

        public Territory getTerritory() {
            return territory;
        }
    }
}
