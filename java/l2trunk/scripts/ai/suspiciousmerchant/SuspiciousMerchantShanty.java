package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantShanty extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
            new Location(-58672, 154703, -2688),
            new Location(-58672, 154703, -2688),
            new Location(-57522, 156523, -2576),
            new Location(-55226, 157117, -2064),
            new Location(-57528, 156515, -2576),
            new Location(-58660, 154706, -2688),
            new Location(-60174, 156182, -2832),
            new Location(-61834, 157703, -3264),
            new Location(-62761, 159101, -3584),
            new Location(-63472, 159672, -3680),
            new Location(-64072, 160631, -3760),
            new Location(-64387, 161877, -3792),
            new Location(-63842, 163092, -3840),
            new Location(-64397, 161831, -3792),
            new Location(-64055, 160587, -3760),
            new Location(-63461, 159656, -3680),
            new Location(-62744, 159095, -3584),
            new Location(-61831, 157693, -3256),
            new Location(-60152, 156167, -2824),
            new Location(-58652, 154707, -2688)};


    public SuspiciousMerchantShanty(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        return thinkActive0(points);
    }

}