package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantAntharas extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
            new Location(74810, 90814, -3344),
            new Location(75094, 92951, -3104),
            new Location(75486, 92906, -3072),
            new Location(75765, 91794, -2912),
            new Location(77116, 90455, -2896),
            new Location(77743, 89119, -2896),
            new Location(77118, 90457, -2896),
            new Location(75750, 91811, -2912),
            new Location(75479, 92904, -3072),
            new Location(75094, 92943, -3104),
            new Location(74809, 90794, -3344),
            new Location(76932, 88297, -3296),
            new Location(77882, 87441, -3408),
            new Location(78257, 85859, -3632),
            new Location(80994, 85866, -3472),
            new Location(82676, 87519, -3360),
            new Location(83778, 88414, -3376),
            new Location(83504, 90378, -3120),
            new Location(84431, 90379, -3264),
            new Location(85453, 90117, -3312),
            new Location(85605, 89708, -3296),
            new Location(84894, 88975, -3344),
            new Location(83735, 88382, -3376),
            new Location(82616, 87485, -3360),
            new Location(80971, 85855, -3472),
            new Location(78247, 85853, -3632),
            new Location(77868, 87463, -3408),
            new Location(76916, 88304, -3280),
            new Location(75494, 89865, -3200)};

    public SuspiciousMerchantAntharas(NpcInstance actor) {
        super(actor);
    }


    @Override
    public boolean thinkActive() {
        return thinkActive0(points);
    }
}