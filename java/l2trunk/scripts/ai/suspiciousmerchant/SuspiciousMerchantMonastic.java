package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantMonastic extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
            new Location(69553, -91746, -1488),
            new Location(70941, -89751, -2256),
            new Location(71104, -89094, -2368),
            new Location(73471, -91462, -2024),
            new Location(74532, -92202, -1776),
            new Location(74908, -93152, -1536),
            new Location(74532, -92202, -1776),
            new Location(73471, -91462, -2024),
            new Location(71104, -89094, -2368),
            new Location(70941, -89751, -2256),
            new Location(69553, -91746, -1488)};

    public SuspiciousMerchantMonastic(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    public boolean thinkActive() {
        return super.thinkActive0(points);
    }
}