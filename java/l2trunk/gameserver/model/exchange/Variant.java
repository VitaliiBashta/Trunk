package l2trunk.gameserver.model.exchange;

public final class Variant {
    public final int number;
    public final int id;
    public final String name;
    public final String icon;

    private Variant(int number, int id, String name, String icon) {
        this.number = number;
        this.id = id;
        this.name = name;
        this.icon = icon;
    }
    public static Variant of(int number, int id, String name, String icon){
        return new Variant(number,  id,  name,  icon);
    }
}
