package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantWestern extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
            new Location(114221, -18762, -1768),
            new Location(115920, -19177, -2120),
            new Location(117105, -19759, -2400),
            new Location(118417, -20135, -2632),
            new Location(118881, -20011, -2712),
            new Location(117210, -18329, -1816),
            new Location(118881, -20011, -2712),
            new Location(118417, -20135, -2632),
            new Location(117105, -19759, -2400),
            new Location(115920, -19177, -2120),
            new Location(114221, -18762, -1768)};

    public SuspiciousMerchantWestern(NpcInstance actor) {
        super(actor);
    }

    public boolean thinkActive() {
        return super.thinkActive0(points);
    }
}