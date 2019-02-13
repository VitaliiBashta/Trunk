package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.stats.Env;

public final class ConditionZoneType extends Condition {
    private final ZoneType zoneType;

    public ConditionZoneType(String zoneType) {
        this.zoneType = ZoneType.valueOf(zoneType);
    }

    @Override
    protected boolean testImpl(Env env) {
        if (env.character instanceof Player) return env.character.isInZone(zoneType);
        return false;
    }
}