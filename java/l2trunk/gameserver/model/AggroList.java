package l2trunk.gameserver.model;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AggroList {
    private final NpcInstance npc;
    private final Map<Integer, AggroInfo> hateList = new HashMap<>();
    /**
     * Блокировка для чтения/записи объектов списка
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    public AggroList(NpcInstance npc) {
        this.npc = npc;
    }

    public void addDamageHate(Creature attacker, int damage, int aggro) {
        damage = Math.max(damage, 0);

        if (damage == 0 && aggro == 0)
            return;

        writeLock.lock();
        try {
            AggroInfo ai;

            if ((ai = hateList.get(attacker.getObjectId())) == null)
                hateList.put(attacker.getObjectId(), ai = new AggroInfo(attacker));

            ai.damage += damage;
            ai.hate += aggro;
            ai.damage = Math.max(ai.damage, 0);
            ai.hate = Math.max(ai.hate, 0);
        } finally {
            writeLock.unlock();
        }
    }

    public AggroInfo get(Creature attacker) {
        readLock.lock();
        try {
            return hateList.get(attacker.getObjectId());
        } finally {
            readLock.unlock();
        }
    }

    public void remove(Creature attacker, boolean onlyHate) {
        writeLock.lock();
        try {
            if (!onlyHate) {
                hateList.remove(attacker.getObjectId());
                return;
            }

            AggroInfo ai = hateList.get(attacker.getObjectId());
            if (ai != null)
                ai.hate = 0;
        } finally {
            writeLock.unlock();
        }
    }

    public void clear() {
        clear(false);
    }

    public void clear(boolean onlyHate) {
        writeLock.lock();
        try {
            if (hateList.isEmpty())
                return;

            if (!onlyHate) {
                hateList.clear();
                return;
            }
            hateList.entrySet().removeIf(entry -> entry.getValue().hate == 0);

        } finally {
            writeLock.unlock();
        }
    }

    public boolean isEmpty() {
        readLock.lock();
        try {
            return hateList.isEmpty();
        } finally {
            readLock.unlock();
        }
    }

    public List<Creature> getHateList() {
        List<AggroInfo> hated;

        readLock.lock();
        try {
            if (hateList.isEmpty())
                return Collections.emptyList();
            hated = new ArrayList<>(hateList.values());
        } finally {
            readLock.unlock();
        }

        hated.sort(HateComparator.getInstance());
        if (hated.get(0).hate == 0)
            return Collections.emptyList();

        List<Creature> hateList = new ArrayList<>();
        List<Creature> chars = World.getAroundCharacters(npc);
        AggroInfo ai;
        for (AggroInfo element : hated) {
            ai = element;
            if (ai.hate == 0)
                continue;
            for (Creature cha : chars)
                if (cha.getObjectId() == ai.attackerId) {
                    hateList.add(cha);
                    break;
                }
        }

        return hateList;
    }

    public Creature getMostHated() {
        List<AggroInfo> hated = getAggroSortedInfos();
        if (hated == null) return null;

        hated.sort(HateComparator.getInstance());
        if (hated.get(0).hate == 0)
            return null;

        List<Creature> chars = World.getAroundCharacters(npc);

        AggroInfo ai;
        loop:
        for (AggroInfo aHated : hated) {
            ai = aHated;
            if (ai.hate == 0)
                continue;
            for (Creature cha : chars)
                if (cha.getObjectId() == ai.attackerId) {
                    if (cha.isDead())
                        continue loop;
                    return cha;
                }
        }

        return null;
    }

    public Creature getRandomHated() {
        List<AggroInfo> hated;

        readLock.lock();
        hated = getAggroSortedInfos();
        if (hated == null) return null;

        hated.sort(HateComparator.getInstance());
        if (hated.get(0).hate == 0)
            return null;

        List<Creature> chars = World.getAroundCharacters(npc);

        ArrayList<Creature> randomHated = new ArrayList<>();

        AggroInfo ai;
        Creature mostHated;
        loop:
        for (AggroInfo aHated : hated) {
            if (aHated.hate == 0)
                continue;
            for (Creature cha : chars)
                if (cha.getObjectId() == aHated.attackerId) {
                    if (cha.isDead())
                        continue loop;
                    randomHated.add(cha);
                    break;
                }
        }

        if (randomHated.isEmpty())
            mostHated = null;
        else
            mostHated = randomHated.get(Rnd.get(randomHated.size()));

        return mostHated;
    }

    private List<AggroInfo> getAggroSortedInfos() {
        List<AggroInfo> hated;
        readLock.lock();
        try {
            if (hateList.isEmpty())
                return null;
            hated = new ArrayList<>(hateList.values());
        } finally {
            readLock.unlock();
        }
        return hated;
    }

    public Creature getTopDamager() {
        List<AggroInfo> hated = getAggroSortedInfos();
        if (hated == null) return null;

        Creature topDamager;

        // Ady - For raids I add a custom sorting maxDealer function. Because its not for single damager, but add up all the party's damage
        if (npc.isRaid()) {
            final List<Creature> chars = World.getAroundCharacters(npc);
            final Map<Long, Integer> parties = new HashMap<>();
            long partyId;

            // First get all the players, their parties and summed damages. Players without party just go alone
            for (AggroInfo ai : hated) {
                if (ai.damage == 0)
                    continue;

                for (Creature cha : chars) {
                    if (cha.getObjectId() != ai.attackerId)
                        continue;

                    if (cha.getPlayer() == null)
                        continue;

                    if (cha.getPlayer() != null && cha.getPlayer().getParty() != null) {
                        partyId = cha.getPlayer().getParty().getLeader().getStoredId();
                        if (!parties.containsKey(partyId))
                            parties.put(partyId, 0);

                        parties.put(partyId, parties.get(partyId) + ai.damage);
                    } else {
                        parties.put((long) cha.getPlayer().getObjectId(), ai.damage);
                    }
                    break;
                }
            }

            // Now sort the map to know which party did the most damage
            final Map<Long, Integer> orderedMap = new TreeMap<>(new PartyDamageComparator(parties));
            orderedMap.putAll(parties);

            // Now choose player that did most damage in the party that did the most of the damage
            Player topDamagePlayer;
            for (Entry<Long, Integer> entry : orderedMap.entrySet()) {
                final Party party = Party.getParties().get(entry.getKey());
                if (party == null) {
                    // Single players, without party
                    topDamagePlayer = World.getPlayer((entry.getKey().intValue()));
                    if (topDamagePlayer == null)
                        continue;

                    return topDamagePlayer;
                }

                topDamagePlayer = null;
                int topDamage = 0;
                for (Player player : party.getMembers()) {
                    final AggroInfo info = hateList.get(player.getObjectId());
                    if (info == null)
                        continue;

                    if (info.damage > topDamage) {
                        topDamagePlayer = player;
                        topDamage = info.damage;
                    }
                }

                if (topDamagePlayer != null)
                    return topDamagePlayer;
            }
        }

        hated.sort(Comparator.comparingInt(o -> o.damage));
        if (hated.get(0).damage == 0)
            return null;

        final List<Creature> chars = World.getAroundCharacters(npc);
        for (AggroInfo ai : hated) {
            if (ai.damage == 0)
                continue;

            for (Creature cha : chars) {
                if (cha.getObjectId() == ai.attackerId) {
                    topDamager = cha;
                    return topDamager;
                }
            }
        }

        return null;
    }

    public Map<Creature, HateInfo> getCharMap() {
        if (isEmpty())
            return Collections.emptyMap();

        Map<Creature, HateInfo> aggroMap = new HashMap<>();
        List<Creature> chars = World.getAroundCharacters(npc);
        readLock.lock();
        try {
            AggroInfo ai;
            for (Entry<Integer, AggroInfo> itr : hateList.entrySet()) {
                ai = itr.getValue();
                if (ai.damage == 0 && ai.hate == 0)
                    continue;
                for (Creature attacker : chars)
                    if (attacker.getObjectId() == ai.attackerId) {
                        aggroMap.put(attacker, new HateInfo(attacker, ai));
                        break;
                    }
            }
        } finally {
            readLock.unlock();
        }

        return aggroMap;
    }

    public Map<Playable, HateInfo> getPlayableMap() {
        if (isEmpty())
            return Collections.emptyMap();

        Map<Playable, HateInfo> aggroMap = new HashMap<>();
        List<Playable> chars = World.getAroundPlayables(npc);
        readLock.lock();
        try {
            AggroInfo ai;
            for (Entry<Integer, AggroInfo> itr : hateList.entrySet()) {
                ai = itr.getValue();
                if (ai.damage == 0 && ai.hate == 0)
                    continue;
                for (Playable attacker : chars)
                    if (attacker.getObjectId() == ai.attackerId) {
                        aggroMap.put(attacker, new HateInfo(attacker, ai));
                        break;
                    }
            }
        } finally {
            readLock.unlock();
        }

        return aggroMap;
    }

    public static class HateComparator implements Comparator<DamageHate> {
        private static final Comparator<DamageHate> instance = new HateComparator();

        HateComparator() {
        }

        public static Comparator<DamageHate> getInstance() {
            return instance;
        }

        @Override
        public int compare(DamageHate o1, DamageHate o2) {
            int diff = o2.hate - o1.hate;
            return diff == 0 ? o2.damage - o1.damage : diff;
        }
    }

    private abstract class DamageHate {
        public int hate;
        public int damage;
    }

    public class HateInfo extends DamageHate {
        public final Creature attacker;

        HateInfo(Creature attacker, AggroInfo ai) {
            this.attacker = attacker;
            hate = ai.hate;
            damage = ai.damage;
        }
    }

    public class AggroInfo extends DamageHate {
        final int attackerId;

        AggroInfo(Creature attacker) {
            attackerId = attacker.getObjectId();
        }
    }

    class PartyDamageComparator implements Comparator<Long> {
        private final Map<Long, Integer> _theMapToSort;

        PartyDamageComparator(Map<Long, Integer> theMapToSort) {
            _theMapToSort = theMapToSort;
        }

        @Override
        public int compare(Long key1, Long key2) {
            return Integer.compare(_theMapToSort.get(key2), _theMapToSort.get(key1));
        }
    }
}