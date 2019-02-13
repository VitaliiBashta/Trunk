package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.PositionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LizardmanSummoner extends Mystic {
    private static final int TANTA_LIZARDMAN_SCOUT = 22768;
    private static final int SPAWN_COUNT = 2;
    private boolean spawnedMobs = false;

    public LizardmanSummoner(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        spawnedMobs = false;
        super.onEvtSpawn();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        if (!spawnedMobs && attacker instanceof Playable) {
            NpcInstance actor = getActor();
            for (int i = 0; i < SPAWN_COUNT; i++) {
                SimpleSpawner sp = new SimpleSpawner(TANTA_LIZARDMAN_SCOUT);
                sp.setLoc(actor.getLoc());
                NpcInstance npc = sp.doSpawn(true);
                npc.setHeading(PositionUtils.calculateHeadingFrom(npc, attacker));
                npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 1000);
            }
            spawnedMobs = true;
        }
        super.onEvtAttacked(attacker, damage);
    }
}
