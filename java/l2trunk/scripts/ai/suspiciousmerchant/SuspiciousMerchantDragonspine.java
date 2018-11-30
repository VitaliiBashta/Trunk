package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantDragonspine extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
            new Location(9318, 92253, -3536),
            new Location(9117, 91645, -3656),
            new Location(9240, 90149, -3592),
            new Location(11509, 90093, -3720),
            new Location(13269, 90004, -3840),
            new Location(14812, 89578, -3832),
            new Location(14450, 90636, -3680),
            new Location(14236, 91690, -3656),
            new Location(13636, 92359, -3480),
            new Location(14236, 91690, -3656),
            new Location(14450, 90636, -3680),
            new Location(14812, 89578, -3832),
            new Location(13269, 90004, -3840),
            new Location(11509, 90093, -3720),
            new Location(9240, 90149, -3592),
            new Location(9117, 91645, -3656),
            new Location(9318, 92253, -3536)};


    public SuspiciousMerchantDragonspine(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        return thinkActive0(points);
    }

}