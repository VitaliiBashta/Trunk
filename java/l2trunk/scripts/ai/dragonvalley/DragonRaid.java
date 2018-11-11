package l2trunk.scripts.ai.dragonvalley;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class DragonRaid extends Fighter {

    private long _lastHit;

    public DragonRaid(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (_lastHit + 1500000 < System.currentTimeMillis()) {
            actor.deleteMe();
            return false;
        }
        return super.thinkActive();
    }

    @Override
    protected void onEvtSpawn() {
        _lastHit = System.currentTimeMillis();
        super.onEvtSpawn();
    }

    @Override
    protected void onEvtAttacked(Creature attacker, int damage) {
        _lastHit = System.currentTimeMillis();

        super.onEvtAttacked(attacker, damage);
    }


}