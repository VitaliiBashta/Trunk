package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantBayou extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
            new Location(190423, 43540, -3656),
            new Location(189579, 45949, -4240),
            new Location(187058, 43551, -4808),
            new Location(185916, 41869, -4512),
            new Location(185292, 39403, -4200),
            new Location(185167, 38401, -4200),
            new Location(184984, 36863, -4152),
            new Location(184377, 36425, -4080),
            new Location(185314, 35866, -3936),
            new Location(185781, 35955, -3832),
            new Location(186686, 35667, -3752),
            new Location(185781, 35955, -3832),
            new Location(185314, 35866, -3936),
            new Location(184377, 36425, -4080),
            new Location(184984, 36863, -4152),
            new Location(185167, 38401, -4200),
            new Location(185292, 39403, -4200),
            new Location(185916, 41869, -4512),
            new Location(187058, 43551, -4808),
            new Location(189579, 45949, -4240),
            new Location(190423, 43540, -3656)};


    public SuspiciousMerchantBayou(NpcInstance actor) {
        super(actor);
    }


    @Override
    public boolean thinkActive() {
        return thinkActive0(points);
    }

}