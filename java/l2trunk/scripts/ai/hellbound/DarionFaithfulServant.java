package l2trunk.scripts.ai.hellbound;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DarionFaithfulServant extends Fighter {
    private static final Logger LOG = LoggerFactory.getLogger(DarionFaithfulServant.class);
    private static final int MysteriousAgent = 32372;

    private DarionFaithfulServant(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        if (Rnd.chance(15))
            try {
                SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(MysteriousAgent));
                sp.setLoc(new Location(-11984, 278880, -13599, -4472));
                sp.doSpawn(true);
                sp.stopRespawn();
                ThreadPoolManager.INSTANCE.schedule(() ->
                                GameObjectsStorage.getAllByNpcId(MysteriousAgent, true).forEach(GameObject::deleteMe)
                        , 600 * 1000L); // 10 mins
            } catch (RuntimeException e) {
                LOG.error("Error on Darion Faithful Servanth Death", e);
            }
        super.onEvtDead(killer);
    }

}