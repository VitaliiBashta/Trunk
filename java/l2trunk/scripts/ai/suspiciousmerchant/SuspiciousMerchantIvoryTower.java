package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantIvoryTower extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
            new Location(74725, 1671, -3128),
            new Location(76651, 1505, -3552),
            new Location(79421, 4977, -3080),
            new Location(77357, 7197, -3208),
            new Location(76287, 9164, -3568),
            new Location(72447, 8196, -3264),
            new Location(71780, 7467, -3160),
            new Location(72447, 8196, -3264),
            new Location(76287, 9164, -3568),
            new Location(77357, 7197, -3208),
            new Location(79421, 4977, -3080),
            new Location(76651, 1505, -3552),
            new Location(74725, 1671, -3128)};

    public SuspiciousMerchantIvoryTower(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        return thinkActive0(points);
    }

}