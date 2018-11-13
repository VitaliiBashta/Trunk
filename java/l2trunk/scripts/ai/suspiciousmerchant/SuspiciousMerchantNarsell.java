package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantNarsell extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
            new Location(159377, 52403, -3312),
            new Location(161177, 54083, -3560),
            new Location(162152, 54365, -3632),
            new Location(162703, 55840, -3696),
            new Location(162370, 58534, -3504),
            new Location(160099, 60034, -3224),
            new Location(158048, 62696, -3464),
            new Location(157220, 63450, -3520),
            new Location(155076, 63731, -3544),
            new Location(153893, 64441, -3656),
            new Location(153085, 62948, -3680),
            new Location(150866, 58737, -3432),
            new Location(153085, 62948, -3680),
            new Location(153893, 64441, -3656),
            new Location(155076, 63731, -3544),
            new Location(157220, 63450, -3520),
            new Location(158048, 62696, -3464),
            new Location(160099, 60034, -3224),
            new Location(162370, 58534, -3504),
            new Location(162703, 55840, -3696),
            new Location(162152, 54365, -3632),
            new Location(161177, 54083, -3560),
            new Location(159377, 52403, -3312)};

    public SuspiciousMerchantNarsell(NpcInstance actor) {
        super(actor);
    }

    public boolean thinkActive() {
        return super.thinkActive0(points);
    }
}