package l2trunk.gameserver.templates.mapregion;

import l2trunk.gameserver.model.Territory;

public class DomainArea implements RegionData {
    private final int _id;
    private final Territory _territory;

    public DomainArea(int id, Territory territory) {
        _id = id;
        _territory = territory;
    }

    public int getId() {
        return _id;
    }

    @Override
    public Territory getTerritory() {
        return _territory;
    }
}
