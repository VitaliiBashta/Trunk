package l2trunk.gameserver.model.actor.instances.player;

import l2trunk.gameserver.model.Player;

public final class Friend {
    private final int objectId;
    private String name;
    private int classId;
    private int level;

    private Player player = null;

    public Friend(int objectId, String name, int classId, int level) {
        this.objectId = objectId;
        this.name = name;
        this.classId = classId;
        this.level = level;
    }

    public Friend(Player player) {
        objectId = player.objectId();
        update(player, true);
    }

    public void update(Player player, boolean set) {
        level = player.getLevel();
        name = player.getName();
        classId = player.getActiveClassId().id;
        this.player = set ? player : null;
    }

    public String getName() {
        Player player = this.player;
        return player == null ? name : player.getName();
    }

    public int getObjectId() {
        return objectId;
    }

    public int getClassId() {
        Player player = this.player;
        return player == null ? classId : player.getActiveClassId().id;
    }

    public int getLevel() {
        Player player = this.player;
        return player == null ? level : player.getLevel();
    }

    public boolean isOnline() {
        Player player = this.player;
        return player != null;
    }

}
