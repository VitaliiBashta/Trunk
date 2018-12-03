package l2trunk.gameserver.instancemanager;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum  BloodAltarManager {
    INSTANCE;
    private final Logger LOG = LoggerFactory.getLogger(BloodAltarManager.class);
    private static final long delay = 30 * 60 * 1000L;
    private static final String[] bossGroups = {"bloodaltar_boss_aden",
            "bloodaltar_boss_darkelf",
            "bloodaltar_boss_dion",
            "bloodaltar_boss_dwarw",
            "bloodaltar_boss_giran",
            "bloodaltar_boss_gludin",
            "bloodaltar_boss_gludio",
            "bloodaltar_boss_goddart",
            "bloodaltar_boss_heine",
            "bloodaltar_boss_orc",
            "bloodaltar_boss_oren",
            "bloodaltar_boss_schutgart"};
    private static long bossRespawnTimer = 0;
    private static boolean bossesSpawned = false;


    public void init() {
        LOG.info("Blood Altar Manager: Initializing...");
        manageNpcs(true);
        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() {
                if (Rnd.chance(30) && bossRespawnTimer < System.currentTimeMillis())
                    if (!bossesSpawned) {
                        manageNpcs(false);
                        manageBosses(true);
                        bossesSpawned = true;
                    } else {
                        manageBosses(false);
                        manageNpcs(true);
                        bossesSpawned = false;
                    }
            }
        }, delay, delay);
    }
    private static void manageNpcs(boolean spawnAlive) {
        if (spawnAlive) {
            SpawnManager.INSTANCE.despawn("bloodaltar_dead_npc");
            SpawnManager.INSTANCE.spawn("bloodaltar_alive_npc");
        } else {
            SpawnManager.INSTANCE.despawn("bloodaltar_alive_npc");
            SpawnManager.INSTANCE.spawn("bloodaltar_dead_npc");
        }
    }

    private static void manageBosses(boolean spawn) {
        if (spawn)
            for (String s : bossGroups)
                SpawnManager.INSTANCE.spawn(s);
        else {
            bossRespawnTimer = System.currentTimeMillis() + 4 * 3600 * 1000L;
            for (String s : bossGroups)
                SpawnManager.INSTANCE.despawn(s);
        }
    }
}
