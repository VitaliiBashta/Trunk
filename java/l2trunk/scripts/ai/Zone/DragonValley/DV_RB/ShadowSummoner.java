package l2trunk.scripts.ai.Zone.DragonValley.DV_RB;

import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class ShadowSummoner extends Mystic {

    private long last_attack_time = 0;

    public ShadowSummoner(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        super.thinkActive();
        if (last_attack_time != 0 && last_attack_time + 30 * 60 * 1000L < System.currentTimeMillis()) {
            getActor().deleteMe();
        }
        return true;
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        last_attack_time = System.currentTimeMillis();
    }

}
