package l2trunk.gameserver.model;

import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.instances.MinionInstance;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.templates.npc.MinionData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class MinionList {
    private final Set<MinionData> minionData;
    private final Set<MinionInstance> _minions;
    private final Lock lock;
    private final MonsterInstance _master;

    public MinionList(MonsterInstance master) {
        _master = master;
        _minions = new HashSet<>();
        minionData = new HashSet<>();
        minionData.addAll(_master.getTemplate().getMinionData());
        lock = new ReentrantLock();
    }

    /**
     * Добавить шаблон для миниона
     *
     * @param m
     */
    public boolean addMinion(MinionData m) {
        lock.lock();
        try {
            return minionData.add(m);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Добавить миниона
     *
     * @param m
     * @return true, если успешно добавлен
     */
    public boolean addMinion(MinionInstance m) {
        lock.lock();
        try {
            return _minions.add(m);
        } finally {
            lock.unlock();
        }
    }

    /**
     * @return имеются ли живые минионы
     */
    public boolean hasAliveMinions() {
        lock.lock();
        try {
            for (MinionInstance m : _minions)
                if (m.isVisible() && !m.isDead())
                    return true;
        } finally {
            lock.unlock();
        }
        return false;
    }

    public boolean hasMinions() {
        return minionData.size() > 0;
    }

    /**
     * Возвращает список живых минионов
     *
     * @return список живых минионов
     */
    public List<MinionInstance> getAliveMinions() {
        List<MinionInstance> result = new ArrayList<>(_minions.size());
        lock.lock();
        try {
            for (MinionInstance m : _minions)
                if (m.isVisible() && !m.isDead())
                    result.add(m);
        } finally {
            lock.unlock();
        }
        return result;
    }

    /**
     * Спавнит всех недостающих миньонов
     */
    public void spawnMinions() {
        lock.lock();
        try {
            int minionCount;
            int minionId;
            for (MinionData minion : minionData) {
                minionId = minion.getMinionId();
                minionCount = minion.getAmount();

                for (MinionInstance m : _minions) {
                    if (m.getNpcId() == minionId)
                        minionCount--;
                    if (m.isDead() || !m.isVisible()) {
                        m.refreshID();
                        m.stopDecay();
                        _master.spawnMinion(m);
                    }
                }

                for (int i = 0; i < minionCount; i++) {
                    MinionInstance m = new MinionInstance(IdFactory.getInstance().getNextId(), NpcHolder.getTemplate(minionId));
                    m.setLeader(_master);
                    _master.spawnMinion(m);
                    _minions.add(m);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Деспавнит всех минионов
     */
    public void unspawnMinions() {
        lock.lock();
        try {
            for (MinionInstance m : _minions)
                m.decayMe();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Удаляет минионов и чистит список
     */
    public void deleteMinions() {
        lock.lock();
        try {
            for (MinionInstance m : _minions)
                m.deleteMe();
            _minions.clear();
        } finally {
            lock.unlock();
        }
    }
}