package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantDemon extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
            new Location(104150, -57163, -848),
            new Location(106218, -59401, -1344),
            new Location(106898, -59553, -1664),
            new Location(107352, -60168, -2000),
            new Location(107651, -61177, -2400),
            new Location(109094, -62678, -3248),
            new Location(108266, -62657, -3104),
            new Location(105169, -61226, -2616),
            new Location(102968, -59982, -2384),
            new Location(100070, -60173, -2792),
            new Location(98764, -61095, -2768),
            new Location(94946, -60039, -2432),
            new Location(96103, -59078, -1992),
            new Location(96884, -59043, -1656),
            new Location(97064, -57884, -1256),
            new Location(96884, -59043, -1656),
            new Location(96103, -59078, -1992),
            new Location(94946, -60039, -2432),
            new Location(98764, -61095, -2768),
            new Location(100070, -60173, -2792),
            new Location(102968, -59982, -2384),
            new Location(105169, -61226, -2616),
            new Location(108266, -62657, -3104),
            new Location(109094, -62678, -3248),
            new Location(107651, -61177, -2400),
            new Location(107352, -60168, -2000),
            new Location(106898, -59553, -1664),
            new Location(106218, -59401, -1344),
            new Location(104150, -57163, -848)};


    public SuspiciousMerchantDemon(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        return thinkActive0(points);
    }

}