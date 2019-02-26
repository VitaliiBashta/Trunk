package l2trunk.scripts.events.BossRandom;

import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Spawner;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.NpcUtils;
import l2trunk.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class BossRandom extends Functions implements ScriptFile {

    private static final Logger LOG = LoggerFactory.getLogger(BossRandom.class);
    private static boolean isActiveBossRandom = true;
    private static final int BossId = Config.RANDOM_BOSS_ID; //enter boss id
    private static Creature boss;
    private static final int BossEventInterval = Config.RANDOM_BOSS_TIME; // 60*60*1000 - time
    private final Zone _zone = ReflectionUtils.getZone("[dino_peace]");

    private static void spawnBoss() {
        boss = NpcUtils.spawnSingle(BossId, Location.of(Config.RANDOM_BOSS_X, Config.RANDOM_BOSS_Y, Config.RANDOM_BOSS_Z));
    }

    public void OnDie(Creature self, Creature killer) {
        if (self.getNpcId() == BossId) {
            Announcements.INSTANCE.announceToAll(self.getName() + " defeated, the getPlayer " + killer.getName() + " final blow!");
            Announcements.INSTANCE.announceToAll("Peace zone in the island canceled.");
            ThreadPoolManager.INSTANCE.schedule(new spawnBossShedule(), BossEventInterval);
            _zone.setActive(false);
        }
    }

    private class spawnBossShedule implements Runnable {

        @Override
        public void run() {
            spawnBoss();
            Location nearestTown = Location.findNearest(boss, List.of(boss.getLoc()));
            Announcements.INSTANCE.announceToAll(boss.getName() + " appeared in " + String.valueOf(nearestTown) + "!");
            Announcements.INSTANCE.announceToAll("Part of the land on the island was peaceful.");
            _zone.setActive(false);

        }
    }

    @Override
    public void onLoad() {

        if (!Config.RANDOM_BOSS_ENABLE)
            return;

        if (NpcHolder.getTemplate(BossId) == null) {
            isActiveBossRandom = false;
        }
        if (isActiveBossRandom) {
            spawnBoss();
            LOG.info("Loaded Event: Boss Random");
        }
    }
}