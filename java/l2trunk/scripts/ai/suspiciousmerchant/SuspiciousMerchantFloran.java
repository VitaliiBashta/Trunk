package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantFloran extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
            new Location(14186, 149947, -3352),
            new Location(16180, 150387, -3216),
            new Location(18387, 151874, -3317),
            new Location(18405, 154770, -3616),
            new Location(17655, 156863, -3664),
            new Location(12303, 153937, -2680),
            new Location(17655, 156863, -3664),
            new Location(18405, 154770, -3616),
            new Location(18387, 151874, -3317),
            new Location(16180, 150387, -3216),
            new Location(14186, 149947, -3352)};

    public SuspiciousMerchantFloran(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        return thinkActive0(points);
    }

}