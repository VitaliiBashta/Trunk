package l2trunk.gameserver.model;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class AggroList {
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

            if ((ai = hateList.get(attacker.objectId())) == null)
                hateList.put(attacker.objectId(), ai = new AggroInfo(attacker));

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
            return hateList.get(attacker.objectId());
        } finally {
            readLock.unlock();
        }
    }

    public void remove(Creature attacker, boolean onlyHate) {
        writeLock.lock();
        try {
            if (!onlyHate) {
                hateList.remove(attacker.objectId());
                return;
            }

            AggroInfo ai = hateList.get(attacker.objectId());
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

    public List<Playable> getHateList() {
        List<AggroInfo> hated;

        readLock.lock();
        try {
            if (hateList.isEmpty())
                return List.of();
            hated = new ArrayList<>(hateList.values());
        } finally {
            readLock.unlock();
        }

        hated.sort(new HateComparator());
        if (hated.get(0).hate == 0)
            return List.of();

        List<Playable> hateList = new ArrayList<>();
        hated.stream()
                .filter(element -> element.hate != 0)
                .forEach(element -> World.getAroundPlayables(npc)
                        .filter(cha -> cha.objectId() == element.attackerId)
                        .findFirst().ifPresent(hateList::add));

        return hateList;
    }

    public Playable getMostHated() {
        List<AggroInfo> hated = getAggroSortedInfos();
        if (hated == null) return null;

        hated.sort(new HateComparator());
        if (hated.get(0).hate == 0)
            return null;
        else
            return World.getAroundPlayables(npc)
                    .filter(cha -> cha.objectId() == hated.get(0).attackerId)
                    .filter(cha -> !cha.isDead())
                    .findFirst().orElse(null);

    }

    public Creature getRandomHated() {
        List<AggroInfo> hated;

        readLock.lock();
        hated = getAggroSortedInfos();
        if (hated == null) return null;

        hated.sort(new HateComparator());
        if (hated.get(0).hate == 0)
            return null;

        List<Creature> randomHated = new ArrayList<>();

        Creature mostHated;
        hated.stream()
                .filter(aHated -> aHated.hate != 0)
                .forEach(aHated -> World.getAroundCharacters(npc)
                        .filter(cha -> cha.objectId() == aHated.attackerId)
                        .filter(cha -> !cha.isDead())
                        .findFirst().ifPresent(randomHated::add));


        if (randomHated.isEmpty())
            mostHated = null;
        else
            mostHated = Rnd.get(randomHated);

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

    public Playable getTopDamager() {
        List<AggroInfo> hated = getAggroSortedInfos();
        if (hated == null) return null;

        Playable topDamager = null;

        // Ady - For raids I add a custom sorting maxDealer function. Because its not for single damager, but add up all the party's damage
        if (npc.isRaid()) {
            final Map<Integer, Integer> parties = new HashMap<>();
//            long partyId;

            // First get all the players, their parties and summed damages. Players without party just go alone
            for (AggroInfo ai : hated) {
                if (ai.damage == 0)
                    continue;

                World.getAroundPlayables(npc)
                        .filter(cha -> cha.objectId() == ai.attackerId)
                        .filter(cha -> cha.getPlayer() != null)
                        .findFirst().ifPresent(cha -> {
                    if (cha.getPlayer().getParty() != null) {
                        int partyId1 = cha.getPlayer().getParty().getLeader().getStoredId();
                        if (!parties.containsKey(partyId1))
                            parties.put(partyId1, 0);
                        parties.put(partyId1, parties.get(partyId1) + ai.damage);
                    } else {
                        parties.put(cha.getPlayer().objectId(), ai.damage);
                    }
                });
            }

            // Now sort the map to know which party did the most damage
            final Map<Integer, Integer> orderedMap = new TreeMap<>(new PartyDamageComparator(parties));
            orderedMap.putAll(parties);

            // Now choose getPlayer that did most damage in the party that did the most of the damage
            Player topDamagePlayer;
            for (Entry<Integer, Integer> entry : orderedMap.entrySet()) {
                final Party party = Party.getParties().get(entry.getValue());
                if (party == null) {
                    // Single players, without party
                    topDamagePlayer = World.getPlayer((entry.getKey()));
                    if (topDamagePlayer != null) {
                        return topDamagePlayer;
                    }
                    continue;

                }

                topDamagePlayer = null;
                int topDamage = 0;
                for (Player player : party.getMembers()) {
                    if (hateList.get(player.objectId()) != null) {
                        if (hateList.get(player.objectId()).damage > topDamage) {
                            topDamagePlayer = player;
                            topDamage = hateList.get(player.objectId()).damage;
                        }
                    }
                }

                if (topDamagePlayer != null)
                    return topDamagePlayer;
            }
        }

        hated.sort(Comparator.comparingInt(o -> o.damage));
        if (hated.get(0).damage == 0)
            return null;

        for (AggroInfo ai : hated) {
            if (ai.damage != 0) {
                topDamager = World.getAroundPlayables(npc)
                        .filter(cha -> cha.objectId() == ai.attackerId)
                        .findFirst().orElse(null);
            }
        }

        return topDamager;
    }

    public Map<Creature, HateInfo> getCharMap() {
        if (isEmpty())
            return Map.of();

        Map<Creature, HateInfo> aggroMap = new HashMap<>();
        readLock.lock();
        try {
            for (AggroInfo ai : hateList.values()) {
                if (ai.damage == 0 && ai.hate == 0)
                    continue;
                World.getAroundCharacters(npc)
                        .filter(c -> c.objectId() == ai.attackerId)
                        .findFirst().ifPresent(c -> aggroMap.put(c, new HateInfo(c, ai)));
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
        readLock.lock();
        try {
            hateList.values().stream()
                    .filter(ai -> ai.damage != 0)
                    .filter(ai -> ai.hate != 0)
                    .forEach(ai -> World.getAroundPlayables(npc)
                            .filter(attacker -> attacker.objectId() == ai.attackerId)
                            .findFirst().ifPresent(attacker -> aggroMap.put(attacker, new HateInfo(attacker, ai))));
        } finally {
            readLock.unlock();
        }
        return aggroMap;
    }

    public static class HateComparator implements Comparator<DamageHate> {
        @Override
        public int compare(DamageHate o1, DamageHate o2) {
            return o2.hate - o1.hate == 0 ? o2.damage - o1.damage : o2.hate - o1.hate;
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
            attackerId = attacker.objectId();
        }
    }

    private class PartyDamageComparator implements Comparator<Integer> {
        private final Map<Integer, Integer> map;

        PartyDamageComparator(Map<Integer, Integer> theMapToSort) {
            map = theMapToSort;
        }

        @Override
        public int compare(Integer key1, Integer key2) {
            return Integer.compare(map.get(key2), map.get(key1));
        }
    }
}
