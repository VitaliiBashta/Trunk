package l2trunk.gameserver.model;

import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.instances.MinionInstance;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.templates.npc.MinionData;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public final class MinionList {
    private final Set<MinionData> minionData = new CopyOnWriteArraySet<>();
    private final Set<MinionInstance> _minions;
    private final Lock lock;
    private final MonsterInstance master;

    public MinionList(MonsterInstance master) {
        this.master = master;
        _minions = new HashSet<>();
        minionData.addAll(this.master.getTemplate().getMinionData());
        lock = new ReentrantLock();
    }


    public void addMinion(MinionData m) {
        minionData.add(m);
    }

    public boolean hasAliveMinions() {
        return _minions.stream()
                .filter(GameObject::isVisible)
                .anyMatch(m -> !m.isDead());
    }

    public boolean hasMinions() {
        return minionData.size() > 0;
    }

    public List<MinionInstance> getAliveMinions() {
        return _minions.stream()
                .filter(GameObject::isVisible)
                .filter(m -> !m.isDead())
                .collect(Collectors.toList());
    }


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
                        master.spawnMinion(m);
                    }
                }

                for (int i = 0; i < minionCount; i++) {
                    MinionInstance m = new MinionInstance(IdFactory.getInstance().getNextId(), NpcHolder.getTemplate(minionId));
                    m.setLeader(master);
                    master.spawnMinion(m);
                    _minions.add(m);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public synchronized void unspawnMinions() {
        _minions.forEach(GameObject::decayMe);
    }

    public void deleteMinions() {
        unspawnMinions();
        _minions.clear();
    }
}