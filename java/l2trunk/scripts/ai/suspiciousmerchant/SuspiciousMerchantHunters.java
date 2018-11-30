package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantHunters extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
            new Location(121072, 93215, -2736),
            new Location(122718, 92355, -2320),
            new Location(126171, 91910, -2216),
            new Location(126353, 90422, -2296),
            new Location(125796, 87720, -2432),
            new Location(124803, 85970, -2464),
            new Location(125036, 83836, -2376),
            new Location(128886, 83331, -1416),
            new Location(129697, 84969, -1256),
            new Location(126291, 86712, -2240),
            new Location(126599, 88950, -2325),
            new Location(126847, 90713, -2264),
            new Location(126599, 88950, -2325),
            new Location(126291, 86712, -2240),
            new Location(129697, 84969, -1256),
            new Location(128886, 83331, -1416),
            new Location(125036, 83836, -2376),
            new Location(124803, 85970, -2464),
            new Location(125796, 87720, -2432),
            new Location(126353, 90422, -2296),
            new Location(126171, 91910, -2216),
            new Location(122718, 92355, -2320),
            new Location(121072, 93215, -2736)};


    public SuspiciousMerchantHunters(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        return thinkActive0(points);

    }

}