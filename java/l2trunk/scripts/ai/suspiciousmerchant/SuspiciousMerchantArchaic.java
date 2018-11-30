package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantArchaic extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
            new Location(105447, -139845, -3120),
            new Location(104918, -140382, -3256),
            new Location(105507, -142515, -3648),
            new Location(106533, -143107, -3656),
            new Location(106714, -143825, -3656),
            new Location(107510, -144024, -3656),
            new Location(108092, -144888, -3656),
            new Location(109499, -145168, -3664),
            new Location(110064, -146169, -3456),
            new Location(110186, -147427, -3096),
            new Location(112389, -147779, -3256),
            new Location(110186, -147427, -3096),
            new Location(110064, -146169, -3456),
            new Location(109499, -145168, -3664),
            new Location(108092, -144888, -3656),
            new Location(107510, -144024, -3656),
            new Location(106714, -143825, -3656),
            new Location(106533, -143107, -3656),
            new Location(105507, -142515, -3648),
            new Location(104918, -140382, -3256),
            new Location(105447, -139845, -3120)};

    public SuspiciousMerchantArchaic(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        return thinkActive0(points);
    }


}