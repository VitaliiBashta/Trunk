package l2trunk.gameserver.model;

import java.util.List;

public final class DressArmorData {
    private final int chest;
    private final int legs;
    private final int gloves;
    private final int feet;
    public final int priceId;
    public final long priceCount;

    public DressArmorData(int chest, int legs, int gloves, int feet, int priceId, long priceCount) {
        this.chest = chest;
        this.legs = legs;
        this.gloves = gloves;
        this.feet = feet;
        this.priceId = priceId;
        this.priceCount = priceCount;
    }

    public List<Integer> getVisualIds() {
        return List.of(chest, legs, feet, gloves);
    }
}
