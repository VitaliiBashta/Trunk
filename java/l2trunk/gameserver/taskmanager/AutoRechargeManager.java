package l2trunk.gameserver.taskmanager;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.threading.SteppingRunnableQueueManager;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.UserInfo;
import l2trunk.gameserver.utils.ItemFunctions;

import java.util.List;
import java.util.concurrent.Future;

public final class AutoRechargeManager extends SteppingRunnableQueueManager {
    private static final AutoRechargeManager _instance = new AutoRechargeManager();
    private static final int TYPE_CP = 0x01;
    private static final int TYPE_HP = 0x02;
    private static final int TYPE_MP = 0x03;
    private static final long CP_CHECK_TIME = 3000L; // 3 sec
    private static final long MP_CHECK_TIME = 7000L; // 7 sec (actually is 8, because task is set every second)
    private static final long HP_CHECK_TIME = 7000L; // 7 sec

    private AutoRechargeManager() {
        super(10000L);
        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(this, 1000L, 1000L);
        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() {
                AutoRechargeManager.this.purge();
            }

        }, 60000L, 60000L);
    }

    public static AutoRechargeManager getInstance() {
        return _instance;
    }

    public Future<?> addAutoChargeTask(final Player player) {
        long delay = 1000L;

        return scheduleAtFixedRate(new RunnableImpl() {

            private long msHpLastCheck = System.currentTimeMillis();
            private long msMpLastCheck = System.currentTimeMillis();
            private long msCpLastCheck = System.currentTimeMillis();

            boolean consumeItem(int itemId) {
                if (player.haveItem(itemId)) {
                    List<Skill> itemSkills = player.inventory.getItemByItemId(itemId).getTemplate().getAttachedSkills();
                    itemSkills.stream()
                            .mapToInt(s ->s.id)
                            .forEach(s -> player.altUseSkill(s, player));
                } else
                    return false;

                return true;
            }

            void runValidationAndConsume(int type, int itemId, double percent) {
                switch (type) {
                    case TYPE_CP:
                        if ((player.getCurrentCp() / player.getMaxCp()) <= percent) {
                            if (!consumeItem(itemId)) {
                                player.AutoCp(false);
                            }
                        }
                        break;
                    case TYPE_HP:
                        if ((player.getCurrentHp() / player.getMaxHp()) <= percent) {
                            if (!consumeItem(itemId)) {
                                player.AutoHp(false);
                            }
                        }
                        break;
                    case TYPE_MP:
                        if ((player.getCurrentMp() / player.getMaxMp()) <= percent) {
                            if (!consumeItem(itemId)) {
                                player.AutoMp(false);
                            }
                        }
                        break;
                }

                player.broadcastStatusUpdate();
                player.broadcastCharInfo();
                player.sendPacket(new UserInfo(player));
            }

            @Override
            public void runImpl() {
                long current = System.currentTimeMillis();

                if (player.isAfraid() || player.isAlikeDead() || player.isInOlympiadMode() || player.isDead())
                    return;

                if (player._autoCp && (current >= (msCpLastCheck + CP_CHECK_TIME))) {
                    runValidationAndConsume(TYPE_CP, Player.autoCp, 0.95);
                    msCpLastCheck = current;
                    //LOG.info("Checking CP");
                }

                if (player._autoHp && (current >= (msHpLastCheck + HP_CHECK_TIME))) {
                    runValidationAndConsume(TYPE_HP, Player.autoHp, 0.70);
                    msHpLastCheck = current;
                    //LOG.info("Checking HP");
                }

                if (player._autoMp && (current >= (msMpLastCheck + MP_CHECK_TIME))) {
                    runValidationAndConsume(TYPE_MP, Player.autoMp, 0.75);
                    msMpLastCheck = current;
                    //LOG.info("Checking MP");
                }

            }
        }, delay, delay);
    }
}