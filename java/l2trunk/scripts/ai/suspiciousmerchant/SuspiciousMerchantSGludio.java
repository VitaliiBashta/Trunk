package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantSGludio extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
            new Location(-28169, 216864, -3544),
            new Location(-29028, 215089, -3672),
            new Location(-30888, 213455, -3656),
            new Location(-31937, 211656, -3656),
            new Location(-30880, 211006, -3552),
            new Location(-27690, 210004, -3272),
            new Location(-25784, 210108, -3272),
            new Location(-21682, 211459, -3272),
            new Location(-18430, 212927, -3704),
            new Location(-16247, 212795, -3664),
            new Location(-16868, 214267, -3648),
            new Location(-17263, 215887, -3552),
            new Location(-18352, 216841, -3504),
            new Location(-17263, 215887, -3552),
            new Location(-16868, 214267, -3648),
            new Location(-16247, 212795, -3664),
            new Location(-18430, 212927, -3704),
            new Location(-21682, 211459, -3272),
            new Location(-25784, 210108, -3272),
            new Location(-27690, 210004, -3272),
            new Location(-30880, 211006, -3552),
            new Location(-31937, 211656, -3656),
            new Location(-30888, 213455, -3656),
            new Location(-29028, 215089, -3672),
            new Location(-28169, 216864, -3544)};


    public SuspiciousMerchantSGludio(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        return thinkActive0(points);
    }


}