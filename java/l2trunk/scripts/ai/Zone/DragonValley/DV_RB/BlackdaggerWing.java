package l2trunk.scripts.ai.Zone.DragonValley.DV_RB;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.instances.NpcInstance;


public final class BlackdaggerWing extends Fighter {

    private long last_attack_time = 0;

    public BlackdaggerWing(NpcInstance actor) {
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