package l2trunk.scripts.ai.hellbound;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.instancemanager.naia.NaiaCoreManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class Epidos extends Fighter {

    public Epidos(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NaiaCoreManager.removeSporesAndSpawnCube();
        NaiaSpore.resetEpidosStats();
        super.onEvtDead(killer);
    }
}