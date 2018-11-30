package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantHive extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
            new Location(19408, 189422, -3136),
            new Location(20039, 187700, -3416),
            new Location(19016, 185813, -3552),
            new Location(17959, 181955, -3680),
            new Location(16440, 181635, -3616),
            new Location(15679, 182540, -3608),
            new Location(15310, 182791, -3568),
            new Location(15242, 184507, -3112),
            new Location(15310, 182791, -3568),
            new Location(15679, 182540, -3608),
            new Location(16440, 181635, -3616),
            new Location(17959, 181955, -3680),
            new Location(19016, 185813, -3552),
            new Location(20039, 187700, -3416),
            new Location(19408, 189422, -3136)};

    public SuspiciousMerchantHive(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        return thinkActive0(points);
    }

}