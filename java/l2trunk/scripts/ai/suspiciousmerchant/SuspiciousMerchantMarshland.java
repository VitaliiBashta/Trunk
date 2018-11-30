package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantMarshland extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
            new Location(71436, -58182, -2904),
            new Location(71731, -56949, -3080),
            new Location(72715, -56729, -3104),
            new Location(73277, -56055, -3104),
            new Location(73369, -55636, -3104),
            new Location(74136, -54646, -3104),
            new Location(73408, -54422, -3104),
            new Location(72998, -53404, -3136),
            new Location(71661, -52937, -3104),
            new Location(71127, -52304, -3104),
            new Location(70225, -52304, -3064),
            new Location(69668, -52780, -3064),
            new Location(68422, -52407, -3240),
            new Location(67702, -52940, -3208),
            new Location(67798, -52940, -3232),
            new Location(66667, -55841, -2840),
            new Location(67798, -52940, -3232),
            new Location(67702, -52940, -3208),
            new Location(68422, -52407, -3240),
            new Location(69668, -52780, -3064),
            new Location(70225, -52304, -3064),
            new Location(71127, -52304, -3104),
            new Location(71661, -52937, -3104),
            new Location(72998, -53404, -3136),
            new Location(73408, -54422, -3104),
            new Location(74136, -54646, -3104),
            new Location(73369, -55636, -3104),
            new Location(73277, -56055, -3104),
            new Location(72715, -56729, -3104),
            new Location(71731, -56949, -3080),
            new Location(71436, -58182, -2904)};

    public SuspiciousMerchantMarshland(NpcInstance actor) {
        super(actor);
    }

    public boolean thinkActive() {
        return super.thinkActive0(points);
    }

}