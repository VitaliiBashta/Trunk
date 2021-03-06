package l2trunk.gameserver.instancemanager.games;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.BlockCheckerEngine;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.impl.KrateisCubeRunnerEvent;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public enum HandysBlockCheckerManager {
    INSTANCE;
    /*
     * This class manage the getPlayer add/remove, team change and
     * event arena status, as the clearance of the participants
     * list or liberate the arena
     */

    // Arena votes to start the game
    private final Map<Integer, Integer> _arenaVotes = new HashMap<>();
    // Registration request penalty (10 seconds)
    private final List<Integer> _registrationPenalty = new ArrayList<>();
    // All the participants and their team classifed by arena
    private List<ArenaParticipantsHolder> arenaPlayers = new ArrayList<>();
    // Arena Status, True = is being used, otherwise, False
    private Map<Integer, Boolean> _arenaStatus = new HashMap<>();

    HandysBlockCheckerManager() {
        _arenaStatus.put(0, false);
        _arenaStatus.put(1, false);
        _arenaStatus.put(2, false);
        _arenaStatus.put(3, false);
    }

    public boolean isRegistered(Player player) {
        for (int i = 0; i < 4; i++)
            if (arenaPlayers.get(i).getAllPlayers().contains(player))
                return true;
        return false;
    }


    /**
     * Return the number of event-start votes for the specified
     * arena id
     */
    public synchronized int getArenaVotes(int arenaId) {
        return _arenaVotes.get(arenaId);
    }

    /**
     * Add a new vote to start the event for the specified
     * arena id
     */
    public synchronized void increaseArenaVotes(int arena) {
        int newVotes = _arenaVotes.get(arena) + 1;
        ArenaParticipantsHolder holder = arenaPlayers.get(arena);

        if (newVotes > holder.getAllPlayers().size() / 2 && !holder.getEvent().isStarted()) {
            clearArenaVotes(arena);
            if (holder.getBlueTeamSize() == 0 || holder.getRedTeamSize() == 0)
                return;
            if (Config.ALT_HBCE_FAIR_PLAY)
                holder.checkAndShuffle();
            ThreadPoolManager.INSTANCE.execute(holder.getEvent().new StartEvent());
        } else
            _arenaVotes.put(arena, newVotes);
    }

    /**
     * Will clear the votes queue (of event start) for the
     * specified arena id
     */
    private synchronized void clearArenaVotes(int arena) {
        _arenaVotes.put(arena, 0);
    }

    /**
     * Returns the players holder
     */
    public ArenaParticipantsHolder getHolder(int arena) {
        return arenaPlayers.get(arena);
    }

    /**
     * Initializes the participants holder
     */
    public void startUpParticipantsQueue() {
        for (int i = 0; i < 4; ++i)
            arenaPlayers.add(new ArenaParticipantsHolder(i));
    }

    /**
     * Add the getPlayer to the specified arena (thorough the specified
     * arena manager) and send the needed server ->  client packets
     */
    public boolean addPlayerToArena(Player player, int arenaId) {
        ArenaParticipantsHolder holder = arenaPlayers.get(arenaId);

        synchronized (arenaPlayers.get(arenaId)) {
            boolean isRed;

            if (isRegistered(player)) {
                player.sendPacket(new SystemMessage2(SystemMsg.C1_IS_ALREADY_REGISTERED_ON_THE_MATCH_WAITING_LIST).addName(player));
                return false;
            }

            if (player.isCursedWeaponEquipped()) {
                player.sendPacket(new SystemMessage2(SystemMsg.YOU_CANNOT_REGISTER_WHILE_IN_POSSESSION_OF_A_CURSED_WEAPON));
                return false;
            }

            KrateisCubeRunnerEvent krateis = EventHolder.getEvent(EventType.MAIN_EVENT, 2);
            if (krateis.isRegistered(player)) {
                player.sendPacket(new SystemMessage2(SystemMsg.APPLICANTS_FOR_THE_OLYMPIAD_UNDERGROUND_COLISEUM_OR_KRATEIS_CUBE_MATCHES_CANNOT_REGISTER));
                return false;
            }

            if (Olympiad.isRegistered(player)) {
                player.sendPacket(new SystemMessage2(SystemMsg.APPLICANTS_FOR_THE_OLYMPIAD_UNDERGROUND_COLISEUM_OR_KRATEIS_CUBE_MATCHES_CANNOT_REGISTER));
                return false;
            }
			/*
			if (UnderGroundColiseum.INSTANCE().isRegisteredPlayer(getPlayer))
			{
				UngerGroundColiseum.INSTANCE().removeParticipant(getPlayer);
				getPlayer.sendPacket(new SystemMessage(SystemMessageId.COLISEUM_OLYMPIAD_KRATEIS_APPLICANTS_CANNOT_PARTICIPATE));
			}
			 */
            if (_registrationPenalty.contains(player.objectId())) {
                player.sendPacket(new SystemMessage2(SystemMsg.YOU_CANNOT_MAKE_ANOTHER_REQUEST_FOR_10_SECONDS_AFTER_CANCELLING_A_MATCH_REGISTRATION));
                return false;
            }

            if (holder.getBlueTeamSize() < holder.getRedTeamSize()) {
                holder.addPlayer(player, 1);
                isRed = false;
            } else {
                holder.addPlayer(player, 0);
                isRed = true;
            }
            holder.broadCastPacketToTeam(new ExCubeGameAddPlayer(player, isRed));
            return true;
        }
    }

    /**
     * Will remove the specified getPlayer from the specified
     * team and arena and will send the needed packet to all
     * his team mates / enemy team mates
     */
    public void removePlayer(Player player, int arenaId, int team) {
        ArenaParticipantsHolder holder = arenaPlayers.get(arenaId);
        synchronized (arenaPlayers.get(arenaId)) {
            boolean isRed = team == 0;

            holder.removePlayer(player, team);
            holder.broadCastPacketToTeam(new ExCubeGameRemovePlayer(player, isRed));

            // End event if theres an empty team
            int teamSize = isRed ? holder.getRedTeamSize() : holder.getBlueTeamSize();
            if (teamSize == 0)
                holder.getEvent().endEventAbnormally();

            Integer objId = player.objectId();
            if (!_registrationPenalty.contains(objId))
                _registrationPenalty.add(objId);
            schedulePenaltyRemoval(objId);
        }
    }

    /**
     * Will change the getPlayer from one team to other (if possible)
     * and will send the needed packets
     */
    public void changePlayerToTeam(Player player, int arena, int team) {
        ArenaParticipantsHolder holder = arenaPlayers.get(arena);

        synchronized (arenaPlayers.get(arena)) {
            boolean isFromRed = holder.redPlayers.contains(player);

            if (isFromRed && holder.getBlueTeamSize() == 6) {
                player.sendMessage("The team is full");
                return;
            } else if (!isFromRed && holder.getRedTeamSize() == 6) {
                player.sendMessage("The team is full");
                return;
            }

            int futureTeam = isFromRed ? 1 : 0;
            holder.addPlayer(player, futureTeam);

            if (isFromRed)
                holder.removePlayer(player, 0);
            else
                holder.removePlayer(player, 1);
            holder.broadCastPacketToTeam(new ExCubeGameChangeTeam(player, isFromRed));
        }
    }

    /**
     * Will erase all participants from the specified holder
     */
    public synchronized void clearPaticipantQueueByArenaId(int arenaId) {
        arenaPlayers.get(arenaId).clearPlayers();
    }

    /**
     * Returns true if arena is holding an event at this momment
     */
    public boolean arenaIsBeingUsed(int arenaId) {
        if (arenaId < 0 || arenaId > 3)
            return false;
        return _arenaStatus.get(arenaId);
    }

    /**
     * Set the specified arena as being used
     */
    public void setArenaBeingUsed(int arenaId) {
        _arenaStatus.put(arenaId, true);
    }

    /**
     * Set as free the specified arena for future
     * events
     */
    public void setArenaFree(int arenaId) {
        _arenaStatus.put(arenaId, false);
    }

    private void schedulePenaltyRemoval(int objId) {
        ThreadPoolManager.INSTANCE.schedule(new PenaltyRemove(objId), 10000);
    }

    public class ArenaParticipantsHolder {
        final int _arena;
        final List<Player> redPlayers;
        final List<Player> bluePlayers;
        final BlockCheckerEngine _engine;

        ArenaParticipantsHolder(int arena) {
            _arena = arena;
            redPlayers = new ArrayList<>(6);
            bluePlayers = new ArrayList<>(6);
            _engine = new BlockCheckerEngine(this, _arena);
        }

        public List<Player> getRedPlayers() {
            return redPlayers;
        }

        public List<Player> getBluePlayers() {
            return bluePlayers;
        }

        public List<Player> getAllPlayers() {
            List<Player> all = new ArrayList<>(12);
            all.addAll(redPlayers);
            all.addAll(bluePlayers);
            return all;
        }

        void addPlayer(Player player, int team) {
            if (team == 0)
                redPlayers.add(player);
            else
                bluePlayers.add(player);
        }

        void removePlayer(Player player, int team) {
            if (team == 0)
                redPlayers.remove(player);
            else
                bluePlayers.remove(player);
        }

        public int getPlayerTeam(Player player) {
            if (redPlayers.contains(player))
                return 0;
            else if (bluePlayers.contains(player))
                return 1;
            else
                return -1;
        }

        public int getRedTeamSize() {
            return redPlayers.size();
        }

        public int getBlueTeamSize() {
            return bluePlayers.size();
        }

        public void broadCastPacketToTeam(L2GameServerPacket packet) {
            ArrayList<Player> team = new ArrayList<>(12);
            team.addAll(redPlayers);
            team.addAll(bluePlayers);

            for (Player p : team)
                p.sendPacket(packet);
        }

        public void clearPlayers() {
            redPlayers.clear();
            bluePlayers.clear();
        }

        public BlockCheckerEngine getEvent() {
            return _engine;
        }

        public void updateEvent() {
            _engine.updatePlayersOnStart(this);
        }

        private void checkAndShuffle() {
            int redSize = redPlayers.size();
            int blueSize = bluePlayers.size();
            if (redSize > blueSize + 1) {
                broadCastPacketToTeam(new SystemMessage2(SystemMsg.THE_TEAM_WAS_ADJUSTED_BECAUSE_THE_POPULATION_RATIO_WAS_NOT_CORRECT));
                int needed = redSize - (blueSize + 1);
                for (int i = 0; i < needed + 1; i++) {
                    Player plr = redPlayers.get(i);
                    if (plr == null)
                        continue;
                    changePlayerToTeam(plr, _arena, 1);
                }
            } else if (blueSize > redSize + 1) {
                broadCastPacketToTeam(new SystemMessage2(SystemMsg.THE_TEAM_WAS_ADJUSTED_BECAUSE_THE_POPULATION_RATIO_WAS_NOT_CORRECT));
                int needed = blueSize - (redSize + 1);
                for (int i = 0; i < needed + 1; i++) {
                    Player plr = bluePlayers.get(i);
                    if (plr == null)
                        continue;
                    changePlayerToTeam(plr, _arena, 0);
                }
            }
        }
    }

    private class PenaltyRemove extends RunnableImpl {
        final Integer objectId;

        PenaltyRemove(Integer id) {
            objectId = id;
        }

        @Override
        public void runImpl() {
            _registrationPenalty.remove(objectId);
        }
    }
}