package l2trunk.gameserver.model.entity;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.instancemanager.UnderGroundColliseumManager;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.base.TeamType;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class Coliseum {
    private static final Logger _log = LoggerFactory.getLogger(Coliseum.class);
    private static Integer eventCycle = 0;
    private final int minlevel;
    private final int maxlevel;
    private final List<Party> party_waiting_list = new ArrayList<>();
    private final List<Party> party_inbattle_list = new ArrayList<>();
    private Party previusWinners = null;
    private boolean isWaitingRoom1Free = true;
    private boolean isWaitingRoom2Free = true;
    private boolean isInUse;
    private int winCount = 0;
    private Party partyInRoom1 = null;
    private Party partyInRoom2 = null;
    private Zone zone;
    private int id = 0;

    public Coliseum() {
        id += 1;
        minlevel = 1;
        maxlevel = 85;
        try {
            load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (eventCycle == 0) {
            init();
        }
    }

    public static void register(Player player, int minLevel, int maxLevel) {
        Coliseum coli = UnderGroundColliseumManager.INSTANCE.getColiseumByLevelLimit(maxLevel);
        if (coli == null) {
            player.sendMessage("this is not work now, if you have any information about it, contact as");
            return;
        }
        Location teleloc = coli.getFreeWaitingRoom();
        if (teleloc == null) {
            player.sendMessage("this is not work now, if you have any information about it, contact as");
        }
        coli.party_waiting_list.add(player.getParty());
        coli.teleportToWaitingRoom(player.getParty(), teleloc);
    }

    private int getMinLevel() {
        return minlevel;
    }

    public int getMaxLevel() {
        return maxlevel;
    }

    private boolean checkOffline(Party party) {
        party_waiting_list.add(party);
        return party.size() == 0;
    }

    private int getId() {
        return id;
    }

    private void load() {
        eventCycle = getColiseumMatchNumber();
        if (eventCycle == 2147483647) {
            eventCycle = 1;
        } else {
            Coliseum.eventCycle++;
        }
    }

    private void init() {
        setcoliseummatchnumber(1);
    }

    private void startBattle(Party party, Party party2) {
        if (!isInUse()) {
            if (getPreviusWinners() == null) {
                ThreadPoolManager.INSTANCE.schedule(new StartBattle(party, party2), 10000L);
            } else {
                ThreadPoolManager.INSTANCE.schedule(new StartBattle(party, getPreviusWinners()), 10000L);
                setIsWaitingRoom1Free(true);
                Location teleloc = getFreeWaitingRoom();
                setIsWaitingRoom2Free(true);
                teleportToWaitingRoom(party2, teleloc);
            }
        } else {
            ThreadPoolManager.INSTANCE.schedule(new TryStart(party, party2), 300000L);
        }
    }

    private void teleportToWaitingRoom(Party party, Location teleloc) {
        if (isWaitingRoom1Free()) {
            setIsWaitingRoom1Free(false);
            setPartyInRoom1(party);
        } else if (isWaitingRoom2Free()) {
            setIsWaitingRoom2Free(false);
            setPartyInRoom2(party);
        } else {
            party.getLeader().sendMessage("rooms are not free you has been registred try to use teleport function later");
            return;
        }
        party.getMembersStream().forEach(member -> member.teleToLocation(teleloc));
        if (!isWaitingRoom2Free) {
            startBattle(getPartyInRoom1(), getPartyInRoom2());
        }
    }

    private Location getFreeWaitingRoom() {
        if (isWaitingRoom1Free()) {
            return zone.getRestartPoints().get(0);
        }
        if (isWaitingRoom2Free()) {
            return zone.getRestartPoints().get(1);
        }
        return null;
    }

    private int getColiseumMatchNumber() {
        return eventCycle;
    }

    private void setcoliseummatchnumber(int number) {
        eventCycle = number;
    }

    public boolean checkIfInZone(int x, int y) {
        return getZone().checkIfInZone(x, y);
    }

    private Zone getZone() {
        if (zone == null) {
            zone = ReflectionUtils.getZone("UnderGroundColiseum");
        }
        return zone;
    }

    private void StopBattle(Party party, Party party2, TeamType winner, long period) {
        ThreadPoolManager.INSTANCE.schedule(new StopBattle(party, party2, winner), period);
    }

    private void teleportPlayers(Party party, Party party2) {
        if (party == null && party2 == null || party2 == null && party.getMembersStream().count() == 0 || party == null && party2 != null && party2.getMembersStream().count() == 0 || party2 != null && party != null && party.getMembersStream().count() == 0 && party2.getMembersStream().count() == 0) {
            StopBattle(party, party2, TeamType.NONE, 20000L);
            party_inbattle_list.remove(party);
            party_inbattle_list.remove(party2);
        }
        if ((party2 != null) && ((party == null) || party.getMembers().isEmpty())) {
            StopBattle(party, party2, party2.getLeader().getTeam(), 20000L);
            party_inbattle_list.remove(party);
        } else if ((party != null) && ((party2 == null) || party2.getMembers().isEmpty())) {
            StopBattle(party, party2, party.getLeader().getTeam(), 20000L);
            party_inbattle_list.remove(party2);
        } else {
            if (party != null) {
                party.getMembersStream().forEach(member -> {
                    member.setTeam(TeamType.BLUE);

                    Location locOnBGToTP = zone.getRestartPoints().get(3);
                    member.teleToLocation(locOnBGToTP);
                });
            }
            if (party2 != null) {
                party2.getMembersStream().forEach(member -> {
                    member.setTeam(TeamType.RED);
                    Location locOnBGToTP = zone.getRestartPoints().get(4);
                    member.teleToLocation(locOnBGToTP);
                });
            }
        }
    }

    private void setPreviusWinner(Party previusWinners) {
        this.previusWinners = previusWinners;
    }

    private Party getPreviusWinners() {
        return previusWinners;
    }

    private void setIsWaitingRoom1Free(boolean isWaitingRoom1Free) {
        this.isWaitingRoom1Free = isWaitingRoom1Free;
    }

    private boolean isWaitingRoom1Free() {
        return isWaitingRoom1Free;
    }

    private void setIsWaitingRoom2Free(boolean isWaitingRoom2Free) {
        this.isWaitingRoom2Free = isWaitingRoom2Free;
    }

    private boolean isWaitingRoom2Free() {
        return isWaitingRoom2Free;
    }

    private void setIsInUse(boolean isInUse) {
        this.isInUse = isInUse;
    }

    private boolean isInUse() {
        return isInUse;
    }

    public List<Party> getPartysInBattleGround() {
        return party_inbattle_list;
    }

    public List<Party> getWaitingPartys() {
        return party_waiting_list;
    }

    private void incWinCount() {
        winCount += 1;
    }

    private int getWinCount() {
        return winCount;
    }

    private void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    private Party getPartyInRoom1() {
        return partyInRoom1;
    }

    private void setPartyInRoom1(Party p1) {
        partyInRoom1 = p1;
    }

    private Party getPartyInRoom2() {
        return partyInRoom2;
    }

    private void setPartyInRoom2(Party p2) {
        partyInRoom2 = p2;
    }

    class TryStart implements Runnable {
        final Party _party1;
        final Party _party2;

        TryStart(Party party, Party party2) {
            _party1 = party;
            _party2 = party2;
        }

        @Override
        public void run() {
            startBattle(_party1, _party2);
        }
    }

    class StopBattle implements Runnable {
        final Party party1;
        final Party party2;
        TeamType _winner;

        StopBattle(Party party, Party party2, TeamType winner) {
            party1 = party;
            this.party2 = party2;
            _winner = winner;
        }

        @Override
        public void run() {
            party_inbattle_list.remove(party1);
            party_inbattle_list.remove(party2);
            if (_winner == TeamType.NONE) {
                setPreviusWinner(null);
                setWinCount(0);
            } else {
                if (party1.getLeader().getTeam() == _winner) {
                    if (!getPreviusWinners().equals(party1)) {
                        setPreviusWinner(party1);
                        setWinCount(1);
                        for (Player member : party1.getMembersStream()) {
                            member.addFame(80, "Coliseum");
                        }
                    } else {
                        incWinCount();
                        party1.getMembersStream().forEach(member -> member.addFame(80 + (getWinCount() * 5), "Coliseum"));
                    }
                    Location teleloc = getFreeWaitingRoom();
                    if (teleloc == null) {
                        teleloc = zone.getRestartPoints().get(4);
                    } else if (isWaitingRoom2Free() && isWaitingRoom1Free()) {
                        setPreviusWinner(null);
                        party1.getMembersStream()
                                .filter(member -> !member.isDead())
                                .forEach(Player::teleToClosestTown);
                    } else if (!isWaitingRoom1Free()) {
                        teleloc = zone.getRestartPoints().get(1);
                        startBattle(getPartyInRoom1(), party1);
                    }
                }
                if (party2.getLeader().getTeam() == _winner) {
                    if (!getPreviusWinners().equals(party2)) {
                        setPreviusWinner(party2);
                        setWinCount(1);
                        party2.getMembersStream().forEach(m -> m.addFame(80, "Coliseum"));

                    } else {
                        incWinCount();
                        party2.getMembersStream().forEach(m -> m.addFame(80 + (getWinCount() * 5), "Coliseum"));

                    }
                    Location teleloc = getFreeWaitingRoom();
                    if (teleloc == null) {
                        teleloc = zone.getRestartPoints().get(4);
                    } else if (isWaitingRoom2Free() && isWaitingRoom1Free()) {
                        setPreviusWinner(null);
                        for (Player member : party2.getMembersStream()) {
                            if (!member.isDead()) {
                                member.teleToClosestTown();
                            }
                        }
                    } else if (!isWaitingRoom1Free()) {
                        teleloc = zone.getRestartPoints().get(1);
                        startBattle(getPartyInRoom1(), party2);
                    }
                }
                setIsInUse(false);
            }
        }
    }

    private class StartBattle implements Runnable {
        final int _number;
        final Party _party1;
        final Party _party2;

        StartBattle(Party party, Party party2) {
            _number = getColiseumMatchNumber();
            _party1 = party;
            _party2 = party2;
        }

        @Override
        public void run() {
            party_waiting_list.remove(_party1);
            party_waiting_list.remove(_party2);
            if (getPartyInRoom1().equals(_party1) || getPartyInRoom1().equals(_party2)) {
                setPartyInRoom1(null);
            }
            if (getPartyInRoom2().equals(_party1) || getPartyInRoom2().equals(_party2)) {
                setPartyInRoom2(null);
            }

            boolean isParty1Ready = checkOffline(_party1);
            boolean isParty2Ready = checkOffline(_party2);
            if (!isParty1Ready) {
                party_waiting_list.remove(_party1);
                setIsWaitingRoom1Free(true);
                _party2.getMembersStream().forEach(m -> m.sendMessage("opponents party is offline, wait for next opponent"));
                return;
            }
            if (!isParty2Ready) {
                party_waiting_list.remove(_party2);
                setIsWaitingRoom2Free(true);
                _party1.getMembersStream().forEach(m -> m.sendMessage("opponents party is offline, wait for next opponent"));
                return;
            }

            party_inbattle_list.addAll(party_waiting_list);
            party_waiting_list.remove(_party2);
            party_waiting_list.remove(_party1);
            setIsWaitingRoom1Free(true);
            setIsWaitingRoom2Free(true);
            teleportPlayers(_party1, _party2);
            setIsInUse(true);
            Party next;
            if (party_waiting_list.size() > 0) {
                ArrayList<Party> toDel = new ArrayList<>();
                for (Party party : party_waiting_list) {
                    if (party.getMembersStream().size() > 6) {
                        party.getMembersStream().forEach(m -> m.sendMessage("Free room at coliseum append"));
                    } else {
                        toDel.add(party);
                    }

                }

                if (party_waiting_list.size() == 0) {
                    return;
                }
                next = Rnd.get(party_waiting_list);
                for (Player member : next.getMembersStream()) {
                    if ((member.getLevel() > getMaxLevel()) || (member.getLevel() < getMinLevel())) {
                        next.sendPacket(member, new SystemMessage(2097).addName(member));
                        return;
                    }
                    if (member.isCursedWeaponEquipped()) {
                        next.sendPacket(member, new SystemMessage(2098).addName(member));
                        return;
                    }
                    Location teleloc = getFreeWaitingRoom();
                    if (teleloc == null) {
                        _log.info("bug cannot find teleloc for coliseum id: " + getId(), "UC");
                        return;
                    }
                    teleportToWaitingRoom(next, teleloc);
                }
                if (party_waiting_list.size() < 2) {
                    return;
                }
                Party next2 = Rnd.get(party_waiting_list);
                while (next2 == next) {
                    next2 = party_waiting_list.get(Rnd.get(party_waiting_list.size() - 1));
                }
                next = next2;
                for (Player member : next.getMembersStream()) {
                    if ((member.getLevel() > getMaxLevel()) || (member.getLevel() < getMinLevel())) {
                        next.sendPacket(member, new SystemMessage(2097).addName(member));
                        return;
                    }
                    Location teleloc = getFreeWaitingRoom();
                    if (teleloc == null) {
                        _log.info("bug cannot find teleloc for coliseum id: " + getId(), "UC");
                        return;
                    }
                    teleportToWaitingRoom(next, teleloc);
                }
            }
        }
    }
}