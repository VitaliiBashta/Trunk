package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantTanor extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
            new Location(58314, 136319, -2000),
            new Location(57078, 137124, -2216),
            new Location(54644, 137366, -2600),
            new Location(58696, 134202, -3096),
            new Location(60967, 134154, -3416),
            new Location(62813, 134744, -3592),
            new Location(65158, 135007, -3728),
            new Location(64278, 139384, -3176),
            new Location(63711, 140599, -2720),
            new Location(63187, 141192, -2440),
            new Location(62811, 142466, -2064),
            new Location(63187, 141192, -2440),
            new Location(63711, 140599, -2720),
            new Location(64278, 139384, -3176),
            new Location(65158, 135007, -3728),
            new Location(62813, 134744, -3592),
            new Location(60967, 134154, -3416),
            new Location(58696, 134202, -3096),
            new Location(54644, 137366, -2600),
            new Location(57078, 137124, -2216),
            new Location(58314, 136319, -2000)};


    public SuspiciousMerchantTanor(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        return thinkActive0(points);
    }

}