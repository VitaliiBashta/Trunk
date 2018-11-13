package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantAaru extends AbstractSuspiciousMerchant {
    private static final Location[] points =
            {
                    new Location(71692, 188004, -2616),
                    new Location(69326, 187042, -3008),
                    new Location(68627, 185540, -2984),
                    new Location(69077, 184566, -2976),
                    new Location(70642, 182573, -2992),
                    new Location(73647, 181706, -3160),
                    new Location(74283, 181756, -3152),
                    new Location(73655, 182960, -2736),
                    new Location(74283, 181756, -3152),
                    new Location(73647, 181706, -3160),
                    new Location(70642, 182573, -2992),
                    new Location(69077, 184566, -2976),
                    new Location(68627, 185540, -2984),
                    new Location(69326, 187042, -3008),
                    new Location(71692, 188004, -2616)
            };


    public SuspiciousMerchantAaru(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        return super.thinkActive0(points);
    }

}