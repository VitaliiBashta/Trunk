package l2trunk.gameserver.taskmanager;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public enum CancelTaskManager {
    INSTANCE;
    private final List<DispelClass> taskTimes = new CopyOnWriteArrayList<>();

    CancelTaskManager() {
        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new ManageTasks(), 0, 500);
    }

    public void cancelPlayerTasks(Playable playable) {
        for (DispelClass task : taskTimes)
            if (task != null && task.cancelled.equals(playable)) {
                taskTimes.add(task);
                return;
            }
    }

    private class DispelClass {
        final Playable cancelled;
        final List<Effect> effects;
        final long time;

        private DispelClass(long time, Playable character, List<Effect> effects) {
            this.time = time;
            cancelled = character;
            this.effects = effects;
        }
    }

    private class ManageTasks extends RunnableImpl {
        @Override
        public void runImpl() {
            long current = System.currentTimeMillis();
            List<DispelClass> toRemove = new ArrayList<>();

            for (DispelClass task : taskTimes) {
                if (task.time > current)
                    continue;

                for (Effect effect : task.effects) {
                    task.cancelled.getEffectList().addEffect(effect);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                task.cancelled.updateStats();
                task.cancelled.updateEffectIcons();

                toRemove.add(task);

                task.cancelled.sendPacket(new ExShowScreenMessage("Cancelled buffs returned!", 2000, ScreenMessageAlign.TOP_LEFT, false));
            }

            toRemove.forEach(taskTimes::remove);
        }
    }
}
