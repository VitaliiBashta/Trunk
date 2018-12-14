package l2trunk.gameserver.templates.mapregion;

import l2trunk.gameserver.model.Territory;
import l2trunk.gameserver.model.base.Race;

import java.util.Map;

public final class RestartArea implements RegionData {
    private final Territory territory;
    private final Map<Race, RestartPoint> restarts;

    public RestartArea(Territory territory, Map<Race, RestartPoint> restarts) {
        this.territory = territory;
        this.restarts = restarts;
    }

    @Override
    public Territory getTerritory() {
        return territory;
    }

    public Map<Race, RestartPoint> getRestartPoint() {
        return restarts;
    }
}
