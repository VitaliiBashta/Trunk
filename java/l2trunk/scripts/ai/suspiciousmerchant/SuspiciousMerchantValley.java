package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantValley extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
            new Location(123383, 121093, -2864),
            new Location(122670, 120890, -3088),
            new Location(124617, 119069, -3088),
            new Location(126177, 118273, -3080),
            new Location(125979, 119528, -2728),
            new Location(126177, 118273, -3080),
            new Location(124617, 119069, -3088),
            new Location(122670, 120890, -3088),
            new Location(123383, 121093, -2864)};

    public SuspiciousMerchantValley(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        return thinkActive0(points);
    }


}