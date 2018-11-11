package l2trunk.gameserver.taskmanager;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.threading.SteppingRunnableQueueManager;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.premium.PremiumEnd;

import java.util.concurrent.Future;

public class LazyPrecisionTaskManager extends SteppingRunnableQueueManager {
    private static final LazyPrecisionTaskManager _instance = new LazyPrecisionTaskManager();

    private LazyPrecisionTaskManager() {
        super(1000L);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 1000L, 1000L);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() {
                LazyPrecisionTaskManager.this.purge();
            }

        }, 60000L, 60000L);
    }

    public static LazyPrecisionTaskManager getInstance() {
        return _instance;
    }

    public Future<?> addPCCafePointsTask(final Player player) {
        long delay = Config.ALT_PCBANG_POINTS_DELAY * 60000L;

        return scheduleAtFixedRate(new RunnableImpl() {

            @Override
            public void runImpl() {
                if (player.isInOfflineMode() || player.getLevel() < Config.ALT_PCBANG_POINTS_MIN_LVL)
                    return;

                player.addPcBangPoints(Config.ALT_PCBANG_POINTS_BONUS, Config.ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE > 0 && Rnd.chance(Config.ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE));
            }

        }, delay, delay);
    }

    public Future<?> addVitalityRegenTask(final Player player) {
        long delay = 60000L;

        return scheduleAtFixedRate(new RunnableImpl() {

            @Override
            public void runImpl() {
                if (player.isInOfflineMode() || !player.isInPeaceZone())
                    return;

                player.setVitality(player.getVitality() + 1);
            }

        }, delay, delay);
    }

    public Future<?> startBonusExpirationTask(final Player player) {
        long delay = player.getBonus().getBonusExpire() * 1000L - System.currentTimeMillis();

        return schedule(new RunnableImpl() {

            @Override
            public void runImpl() {
                PremiumEnd.getInstance().stopBonuses(player);
            }

        }, delay);
    }

    public Future<?> addNpcAnimationTask(final NpcInstance npc) {
        return scheduleAtFixedRate(new RunnableImpl() {

            @Override
            public void runImpl() {
                if (npc.isVisible() && !npc.isActionsDisabled() && !npc.isMoving && !npc.isInCombat())
                    npc.onRandomAnimation();
            }

        }, 1000L, Rnd.get(Config.MIN_NPC_ANIMATION, Config.MAX_NPC_ANIMATION) * 1000L);
    }
}
