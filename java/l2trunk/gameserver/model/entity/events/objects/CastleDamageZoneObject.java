package l2trunk.gameserver.model.entity.events.objects;

public class CastleDamageZoneObject extends ZoneObject {
    private final long price;

    public CastleDamageZoneObject(String name, long price) {
        super(name);
        this.price = price;
    }

    public long getPrice() {
        return price;
    }
}
