package l2trunk.gameserver.instancemanager;

import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.entity.Coliseum;
import l2trunk.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum UnderGroundColliseumManager {
    INSTANCE;
    private final Logger LOG = LoggerFactory.getLogger(UnderGroundColliseumManager.class);
    private Map<String, Coliseum> coliseums = new HashMap<>();

    UnderGroundColliseumManager() {
        List<Zone> zones = ReflectionUtils.getZonesByType(ZoneType.UnderGroundColiseum);
        if (zones.isEmpty())
            LOG.info("Not found zones for UnderGround Coliseum!!!");
        else {
            zones.forEach(zone -> coliseums.put(zone.getName(), new Coliseum()));
        }
        LOG.info("Loaded: " + coliseums.size() + " UnderGround Coliseums.");
    }

    public Coliseum getColiseumByLevelLimit(int limit) {
        return coliseums.values().stream()
                .filter(coliseum -> coliseum.getMaxLevel() == limit)
                .findFirst().orElse(null);
    }
}