package l2trunk.gameserver.model.quest;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.instances.NpcInstance;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class QuestTimer extends RunnableImpl {
    private final String _name;
    private final NpcInstance _npc;
    private long _time;
    private QuestState _qs;
    private ScheduledFuture<?> _schedule;

    QuestTimer(String name, long time, NpcInstance npc) {
        _name = name;
        _time = time;
        _npc = npc;
    }

    private QuestState getQuestState() {
        return _qs;
    }

    void setQuestState(QuestState qs) {
        _qs = qs;
    }

    void start() {
        _schedule = ThreadPoolManager.INSTANCE.schedule(this, _time);
    }

    @Override
    public void runImpl() {
        QuestState qs = getQuestState();
        if (qs != null) {
            qs.removeQuestTimer(getName());
            qs.quest.notifyEvent(getName(), qs, getNpc());
        }
    }

    void pause() {
        // Запоминаем оставшееся время, для возможности возобновления таска
        if (_schedule != null) {
            _time = _schedule.getDelay(TimeUnit.SECONDS);
            _schedule.cancel(false);
        }
    }

    void stop() {
        if (_schedule != null)
            _schedule.cancel(false);
    }

    public boolean isActive() {
        return _schedule != null && !_schedule.isDone();
    }

    private String getName() {
        return _name;
    }

    public long getTime() {
        return _time;
    }

    private NpcInstance getNpc() {
        return _npc;
    }

    @Override
    public final String toString() {
        return _name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o == null)
            return false;
        if (o.getClass() != this.getClass())
            return false;
        return ((QuestTimer) o).getName().equals(this.getName());
    }
}