package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantBorderland extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
            new Location(161876, -73407, -2984),
            new Location(161795, -75288, -3088),
            new Location(159678, -77671, -3584),
            new Location(158917, -78117, -3760),
            new Location(158989, -77130, -3720),
            new Location(158757, -75951, -3720),
            new Location(158157, -74161, -3592),
            new Location(157547, -73326, -3400),
            new Location(153815, -71497, -3392),
            new Location(153086, -70701, -3488),
            new Location(152262, -70352, -3568),
            new Location(155193, -69617, -3008),
            new Location(152262, -70352, -3568),
            new Location(153086, -70701, -3488),
            new Location(153815, -71497, -3392),
            new Location(157547, -73326, -3400),
            new Location(158157, -74161, -3592),
            new Location(158757, -75951, -3720),
            new Location(158989, -77130, -3720),
            new Location(158917, -78117, -3760),
            new Location(159678, -77671, -3584),
            new Location(161795, -75288, -3088),
            new Location(161876, -73407, -2984)};

    public SuspiciousMerchantBorderland(NpcInstance actor) {
        super(actor);
    }


    @Override
    public boolean thinkActive() {
        return thinkActive0(points);
    }

}