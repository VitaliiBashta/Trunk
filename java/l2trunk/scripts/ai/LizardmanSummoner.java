package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.PositionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Bonux
 * При ударе монстра спавнятся 2 х Tanta Lizardman Scout и они агрятся на игрока.
 **/
public final class LizardmanSummoner extends Mystic {
    private static final Logger LOG = LoggerFactory.getLogger(LizardmanSummoner.class);
    private final int TANTA_LIZARDMAN_SCOUT = 22768;
    private final int SPAWN_COUNT = 2;
    private boolean spawnedMobs = false;

    private LizardmanSummoner(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        spawnedMobs = false;
        super.onEvtSpawn();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        if (!spawnedMobs && attacker.isPlayable()) {
            NpcInstance actor = getActor();
            for (int i = 0; i < SPAWN_COUNT; i++) {
                try {
                    SimpleSpawner sp = new SimpleSpawner(TANTA_LIZARDMAN_SCOUT);
                    sp.setLoc(actor.getLoc());
                    NpcInstance npc = sp.doSpawn(true);
                    npc.setHeading(PositionUtils.calculateHeadingFrom(npc, attacker));
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 1000);
                } catch (RuntimeException e) {
                    LOG.error("Error while Spawning Tanta Lizardman Scout by Lizardman Summoner", e);
                }
            }
            spawnedMobs = true;
        }
        super.onEvtAttacked(attacker, damage);
    }
}
