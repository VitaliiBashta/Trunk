package l2trunk.gameserver.templates;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.geometry.Polygon;
import l2trunk.gameserver.ai.CharacterAI;
import l2trunk.gameserver.ai.DoorAI;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.ai.door.ResidenceDoor;
import l2trunk.scripts.ai.door.SSQDoor;
import l2trunk.scripts.ai.door.SiegeDoor;

public final class DoorTemplate extends CharTemplate {
    public final String name;
    private final int _id;
    private final DoorType doortype;
    private final boolean _unlockable;
    private final boolean _isHPVisible;
    private final boolean _opened;
    private final boolean _targetable;
    private final Polygon _polygon;
    private final Location _loc;
    private final int key;
    private final int _openTime;
    private final int _rndTime;
    private final int _closeTime;
    private final int masterDoor;
    private final StatsSet _aiParams;
    private String classAI;

    public DoorTemplate(StatsSet set) {
        super(set);
        _id = set.getInteger("uid");
        name = set.getString("name");
        doortype = set.getEnum("door_type", DoorType.class, DoorType.DOOR);
        _unlockable = set.getBool("unlockable", false);
        _isHPVisible = set.getBool("show_hp", false);
        _opened = set.getBool("opened", false);
        _targetable = set.getBool("targetable", true);
        _loc = set.getLocation("pos");
        _polygon = set.getPolygon("shape");
        key = set.getInteger("key");
        _openTime = set.getInteger("open_time");
        _rndTime = set.getInteger("random_time");
        _closeTime = set.getInteger("close_time");
        masterDoor = set.getInteger("master_door");
        _aiParams = set.getStats("ai_params");

        classAI = set.getString("ai", "DoorAI");
    }

    public CharacterAI getNewAI(DoorInstance door) {
        switch (classAI) {
            case "DoorAI":
                return new DoorAI(door);
            case "SiegeDoor":
                return new SiegeDoor(door);
            case "ResidenceDoor":
                return new ResidenceDoor(door);
            case "SSQDoor":
                return new SSQDoor(door);
            default:
                throw new IllegalArgumentException("no AI for door: " + classAI);
        }

    }

    @Override
    public int getNpcId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public DoorType getDoorType() {
        return doortype;
    }

    public boolean isUnlockable() {
        return _unlockable;
    }

    public boolean isHPVisible() {
        return _isHPVisible;
    }

    public Polygon getPolygon() {
        return _polygon;
    }

    public int getKey() {
        return key;
    }

    public boolean isOpened() {
        return _opened;
    }

    public Location getLoc() {
        return _loc;
    }

    public int getOpenTime() {
        return _openTime;
    }

    public int getRandomTime() {
        return _rndTime;
    }

    public int getCloseTime() {
        return _closeTime;
    }

    public boolean isTargetable() {
        return _targetable;
    }

    public int getMasterDoor() {
        return masterDoor;
    }

    public StatsSet getAIParams() {
        return _aiParams;
    }

    public enum DoorType {
        DOOR,
        WALL
    }
}
