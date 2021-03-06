package l2trunk.gameserver.model.actor.instances.player;

public final class ShortCut {
    public final static int TYPE_ITEM = 1;
    public final static int TYPE_SKILL = 2;
    public final static int TYPE_ACTION = 3;

    public final static int PAGE_FLY_TRANSFORM = 10;
    public final static int PAGE_AIRSHIP = 11;

    public final static int PAGE_MAX = PAGE_AIRSHIP;

    private final int slot;
    private final int page;
    private final int type;
    private final int id;
    private final int level;
    private final int characterType;

    public ShortCut(int slot, int page, int type, int id, int level, int characterType) {
        this.slot = slot;
        this.page = page;
        this.type = type;
        this.id = id;
        this.level = level;
        this.characterType = characterType;
    }

    public int getSlot() {
        return slot;
    }

    public int getPage() {
        return page;
    }

    public int getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public int getCharacterType() {
        return characterType;
    }

    @Override
    public String toString() {
        return "ShortCut: " + slot + "/" + page + " ( " + type + "," + id + "," + level + "," + characterType + ")";
    }
}