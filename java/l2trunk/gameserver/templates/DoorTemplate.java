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
    private final int id;
    private final DoorType doortype;
    private final boolean unlockable;
    private final boolean isHPVisible;
    private final boolean opened;
    private final boolean targetable;
    private final Polygon polygon;
    private final Location loc;
    private final int key;
    private final int openTime;
    private final int rndTime;
    private final int closeTime;
    private final int masterDoor;
    private final StatsSet aiParams;
    private String classAI;

    public DoorTemplate(StatsSet set) {
        super(set);
        id = set.getInteger("uid");
        name = set.getString("name");
        doortype = set.getEnum("door_type", DoorType.class, DoorType.DOOR);
        unlockable = set.isSet("unlockable");
        isHPVisible = set.isSet("show_hp");
        opened = set.isSet("opened");
        targetable = set.isSet("targetable");
        loc = set.getLocation("pos");
        polygon = set.getPolygon("shape");
        key = set.getInteger("key");
        openTime = set.getInteger("open_time");
        rndTime = set.getInteger("random_time");
        closeTime = set.getInteger("close_time");
        masterDoor = set.getInteger("master_door");
        aiParams = set.getStats("ai_params");

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
        return id;
    }

    public String getName() {
        return name;
    }

    public DoorType getDoorType() {
        return doortype;
    }

    public boolean isUnlockable() {
        return unlockable;
    }

    public boolean isHPVisible() {
        return isHPVisible;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public int getKey() {
        return key;
    }

    public boolean isOpened() {
        return opened;
    }

    public Location getLoc() {
        return loc;
    }

    public int getOpenTime() {
        return openTime;
    }

    public int getRandomTime() {
        return rndTime;
    }

    public int getCloseTime() {
        return closeTime;
    }

    public boolean isTargetable() {
        return targetable;
    }

    public int getMasterDoor() {
        return masterDoor;
    }

    public StatsSet getAIParams() {
        return aiParams;
    }

    public enum DoorType {
        DOOR,
        WALL
    }
}
