package l2trunk.gameserver.templates.item;


public final class CreateItem {
    public final int id;
    public final boolean equipable;
    public final int shortcut;

    public CreateItem(int id, boolean equipable, int shortcut) {
        this.id = id;
        this.equipable = equipable;
        this.shortcut = shortcut;
    }
}