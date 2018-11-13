package l2trunk.scripts.ai.hellbound;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.instancemanager.naia.NaiaCoreManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.scripts.bosses.BelethManager;

public final class MutatedElpy extends Fighter {
    public MutatedElpy(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    public void onEvtDead(Creature killer) {
        NaiaCoreManager.launchNaiaCore();
        BelethManager.setElpyDead();
        super.onEvtDead(killer);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        actor.doDie(attacker);
    }
}