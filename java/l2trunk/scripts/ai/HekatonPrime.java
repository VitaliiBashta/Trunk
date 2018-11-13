package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class HekatonPrime extends Fighter {
    private long _lastTimeAttacked;

    public HekatonPrime(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        _lastTimeAttacked = System.currentTimeMillis();
    }

    @Override
    public boolean thinkActive() {
        if (_lastTimeAttacked + 600000 < System.currentTimeMillis()) {
            if (getActor().getMinionList().hasMinions())
                getActor().getMinionList().deleteMinions();
            getActor().deleteMe();
            return true;
        }
        return false;
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        _lastTimeAttacked = System.currentTimeMillis();
        super.onEvtAttacked(attacker, damage);
    }
}