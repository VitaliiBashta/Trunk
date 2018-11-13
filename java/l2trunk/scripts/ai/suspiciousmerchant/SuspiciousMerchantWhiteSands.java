package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantWhiteSands extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
            new Location(114436, 202528, -3408),
            new Location(113809, 200514, -3720),
            new Location(116035, 199822, -3664),
            new Location(117017, 199876, -3632),
            new Location(119959, 201032, -3608),
            new Location(121849, 200614, -3384),
            new Location(122868, 200874, -3168),
            new Location(123130, 202427, -3128),
            new Location(122427, 204162, -3488),
            new Location(122661, 204842, -3576),
            new Location(124051, 205402, -3576),
            new Location(124211, 206023, -3504),
            new Location(124948, 206778, -3400),
            new Location(124483, 207777, -3200),
            new Location(124948, 206778, -3400),
            new Location(124211, 206023, -3504),
            new Location(124051, 205402, -3576),
            new Location(122661, 204842, -3576),
            new Location(122427, 204162, -3488),
            new Location(123130, 202427, -3128),
            new Location(122868, 200874, -3168),
            new Location(121849, 200614, -3384),
            new Location(119959, 201032, -3608),
            new Location(117017, 199876, -3632),
            new Location(116035, 199822, -3664),
            new Location(113809, 200514, -3720),
            new Location(114436, 202528, -3408)};

    public SuspiciousMerchantWhiteSands(NpcInstance actor) {
        super(actor);
    }

    public boolean thinkActive() {
        return super.thinkActive0(points);
    }
}