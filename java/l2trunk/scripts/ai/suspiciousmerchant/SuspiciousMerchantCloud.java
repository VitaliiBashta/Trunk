package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantCloud extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
            new Location(-56032, 86017, -3259),
            new Location(-57329, 86006, -3640),
            new Location(-57470, 85306, -3664),
            new Location(-58892, 85159, -3768),
            new Location(-59030, 80150, -3632),
            new Location(-57642, 77591, -3512),
            new Location(-53971, 77664, -3224),
            new Location(-53271, 85126, -3552),
            new Location(-53971, 77664, -3224),
            new Location(-57642, 77591, -3512),
            new Location(-59030, 80150, -3632),
            new Location(-58892, 85159, -3768),
            new Location(-57470, 85306, -3664),
            new Location(-57329, 86006, -3640),
            new Location(-56032, 86017, -3259)};

    public SuspiciousMerchantCloud(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        return thinkActive0(points);
    }

}