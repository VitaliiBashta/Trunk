package l2trunk.scripts.ai;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class MCManager extends DefaultAI {
    public MCManager(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        ThreadPoolManager.getInstance().schedule(new ScheduleStart(1, actor), 30000);
        super.onEvtSpawn();
    }

    private class ScheduleStart implements Runnable {
        private final int _taskId;
        private final NpcInstance _actor;

        ScheduleStart(int taskId, NpcInstance actor) {
            _taskId = taskId;
            _actor = actor;
        }

        @Override
        public void run() {
            switch (_taskId) {
                case 1:
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(2, _actor), 1000);
                    break;
                case 2:
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(3, _actor), 6000);
                    break;
                case 3:
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(4, _actor), 4000);
                    break;
                case 4:
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(5, _actor), 5000);
                    break;
                case 5:
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(6, _actor), 3000);
                    addTaskMove(new Location(-56511, -56647, -2008), true);
                    doTask();
                    break;
                case 6:
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(7, _actor), 220000);
                    break;
                case 7:
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(8, _actor), 12000);
                    addTaskMove(new Location(-56698, -56430, -2008), true);
                    doTask();
                    break;
                case 8:
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(9, _actor), 3000);
                    addTaskMove(new Location(-56511, -56647, -2008), true);
                    doTask();
                    break;
                case 9:
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(10, _actor), 102000);
                    break;
                case 10:
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(11, _actor), 5000);
                    addTaskMove(new Location(-56698, -56430, -2008), true);
                    doTask();
                    break;
                case 11:
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(12, _actor), 3000);
                    break;
                case 12:
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(13, _actor), 3000);
                    break;
                case 13:
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(14, _actor), 2000);
                    break;
                case 14:
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(15, _actor), 1000);
                    break;
                case 15:
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(16, _actor), 2000);
                    break;
                case 16:
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(17, _actor), 2000);
                    break;
                case 17:
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(18, _actor), 3000);
                    break;
                case 18:
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(19, _actor), 5000);
                    break;
                case 19:
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(20, _actor), 10000);
                    addTaskMove(new Location(-56698, -56340, -2008), true);
                    doTask();
                    break;
                case 20:
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(21, _actor), 10000);
                    break;
                case 21:
                    break;
            }
        }
    }
}