package l2trunk.gameserver.templates.mapregion;

import l2trunk.gameserver.model.Territory;

public final class DomainArea implements RegionData {
    private final int id;
    private final Territory territory;

    public DomainArea(int id, Territory territory) {
        this.id = id;
        this.territory = territory;
    }

    public int getId() {
        return id;
    }

    @Override
    public Territory getTerritory() {
        return territory;
    }
}
