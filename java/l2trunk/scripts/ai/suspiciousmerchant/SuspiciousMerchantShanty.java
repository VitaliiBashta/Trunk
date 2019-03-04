package l2trunk.scripts.ai.suspiciousmerchant;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SuspiciousMerchantShanty extends AbstractSuspiciousMerchant {
    private static final Location[] points = {
           Location.of(-58672, 154703, -2688),
           Location.of(-58672, 154703, -2688),
           Location.of(-57522, 156523, -2576),
           Location.of(-55226, 157117, -2064),
           Location.of(-57528, 156515, -2576),
           Location.of(-58660, 154706, -2688),
           Location.of(-60174, 156182, -2832),
           Location.of(-61834, 157703, -3264),
           Location.of(-62761, 159101, -3584),
           Location.of(-63472, 159672, -3680),
           Location.of(-64072, 160631, -3760),
           Location.of(-64387, 161877, -3792),
           Location.of(-63842, 163092, -3840),
           Location.of(-64397, 161831, -3792),
           Location.of(-64055, 160587, -3760),
           Location.of(-63461, 159656, -3680),
           Location.of(-62744, 159095, -3584),
           Location.of(-61831, 157693, -3256),
           Location.of(-60152, 156167, -2824),
           Location.of(-58652, 154707, -2688)};


    public SuspiciousMerchantShanty(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        return thinkActive0(points);
    }

}