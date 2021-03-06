package l2trunk.gameserver.model.entity;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.instancemanager.games.HandysBlockCheckerManager;
import l2trunk.gameserver.instancemanager.games.HandysBlockCheckerManager.ArenaParticipantsHolder;
import l2trunk.gameserver.listener.actor.player.OnPlayerExitListener;
import l2trunk.gameserver.listener.actor.player.OnTeleportListener;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.base.TeamType;
import l2trunk.gameserver.model.instances.BlockInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.PcInventory;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

public final class BlockCheckerEngine {
    // The needed arena coordinates
    // Arena X: team1X, team1Y, team2X, team2Y, ArenaCenterX, ArenaCenterY
    private static final int[][] _arenaCoordinates = {
            // Arena 0 - Team 1 XY, Team 2 XY - CENTER XY
            {-58368, -62745, -57751, -62131, -58053, -62417},
            // Arena 1 - Team 1 XY, Team 2 XY - CENTER XY
            {-58350, -63853, -57756, -63266, -58053, -63551},
            // Arena 2 - Team 1 XY, Team 2 XY - CENTER XY
            {-57194, -63861, -56580, -63249, -56886, -63551},
            // Arena 3 - Team 1 XY, Team 2 XY - CENTER XY
            {-57200, -62727, -56584, -62115, -56850, -62391}};
    // Common z coordinate
    private static final int _zCoord = -2405;
    // Default arena
    private static final byte DEFAULT_ARENA = -1;
    private final List<String> zoneNames = List.of(
            "[block_checker_1]", "[block_checker_2]", "[block_checker_3]", "[block_checker_4]");
    // Maps to hold getPlayer of each team and his points
    private final Map<Player, Integer> _redTeamPoints = new ConcurrentHashMap<>();
    private final Map<Player, Integer> _blueTeamPoints = new ConcurrentHashMap<>();
    // All blocks
    private final List<SimpleSpawner> _spawns = new CopyOnWriteArrayList<>();
    // List of dropped items in event (for later deletion)
    private final List<ItemInstance> drops = new ArrayList<>();
    private final OnExitPlayerListener _listener = new OnExitPlayerListener();
    // The object which holds all basic members info
    private HandysBlockCheckerManager.ArenaParticipantsHolder holder;
    // The initial points of the event
    private int _redPoints = 15;
    private int _bluePoints = 15;
    // Current used arena
    private int arena = -1;
    // Sets if the red team won the event at the end of this (used for packets)
    private boolean _isRedWinner;
    // Time when the event starts. Used on packet sending
    private long _startedTime;
    // Girl Npc
    private NpcInstance _girlNpc;
    // Event is started
    private boolean _isStarted = false;
    // Event end
    private ScheduledFuture<?> _task;
    // Preserve from exploit reward by logging out
    private boolean _abnormalEnd = false;

    public BlockCheckerEngine(HandysBlockCheckerManager.ArenaParticipantsHolder holder, int arena) {
        this.holder = holder;
        if (arena > -1 && arena < 4)
            this.arena = arena;

        for (Player player : holder.getRedPlayers())
            _redTeamPoints.put(player, 0);
        for (Player player : holder.getBluePlayers())
            _blueTeamPoints.put(player, 0);
    }

    /**
     * Updates the getPlayer holder before the event starts
     * to synchronize all info
     */
    public void updatePlayersOnStart(ArenaParticipantsHolder holder) {
        this.holder = holder;
    }

    /**
     * Returns the current holder object of this
     * object engine
     *
     * @return HandysBlockCheckerManager.ArenaParticipantsHolder
     */
    public ArenaParticipantsHolder getHolder() {
        return holder;
    }

    /**
     * Will return the id of the arena used
     * by this event
     *
     * @return false;
     */
    public int getArena() {
        return arena;
    }

    /**
     * Returns the time when the event
     * started
     *
     * @return long
     */
    public long getStarterTime() {
        return _startedTime;
    }

    /**
     * Returns the current red team points
     *
     * @return int
     */
    public synchronized int getRedPoints() {
        return _redPoints;
    }

    public synchronized int getBluePoints() {
        return _bluePoints;
    }

    /**
     * Returns the getPlayer points
     *
     * @param player
     * @param isRed
     * @return int
     */
    public int getPlayerPoints(Player player, boolean isRed) {
        if (!_redTeamPoints.containsKey(player) && !_blueTeamPoints.containsKey(player))
            return 0;

        if (isRed)
            return _redTeamPoints.get(player);
        else
            return _blueTeamPoints.get(player);
    }

    /**
     * Increases getPlayer points for his teams
     */
    public synchronized void increasePlayerPoints(Player player, int team) {
        if (player == null)
            return;

        if (team == 0) {
            int points = getPlayerPoints(player, true) + 1;
            _redTeamPoints.put(player, points);
            _redPoints++;
            _bluePoints--;
        } else {
            int points = getPlayerPoints(player, false) + 1;
            _blueTeamPoints.put(player, points);
            _bluePoints++;
            _redPoints--;
        }
    }

    /**
     * Will add a new drop into the list of
     * dropped items
     */
    public void addNewDrop(ItemInstance item) {
        if (item != null)
            drops.add(item);
    }

    /**
     * Will return true if the event is alredy
     * started
     *
     * @return boolean
     */
    public boolean isStarted() {
        return _isStarted;
    }

    /**
     * Will send all packets for the event members with
     * the relation info
     */
    private void broadcastRelationChanged(Player plr) {
        holder.getAllPlayers().forEach(p ->
                p.sendPacket(RelationChanged.update(plr, p, plr)));
    }

    /**
     * Called when a there is an empty team. The event
     * will end.
     */
    public void endEventAbnormally() {
        synchronized (this) {
            _isStarted = false;

            if (_task != null)
                _task.cancel(true);

            _abnormalEnd = true;

            ThreadPoolManager.INSTANCE.execute(new EndEvent());
        }
    }

    private void clearArena(String zoneName) {
        Zone zone = ReflectionUtils.getZone(zoneName);
        if (zone != null)
            for (Creature cha : zone.getObjects())
                if (cha instanceof Player && ((Player)cha).getBlockCheckerArena() < 0)
                    ((Player)cha).teleToClosestTown();
                else if (cha instanceof NpcInstance)
                    cha.deleteMe();
    }

    /**
     * This inner class set ups all getPlayer
     * and arena parameters to start the event
     */
    public class StartEvent extends RunnableImpl {
        // Common and unparametizer packet
        private final ExCubeGameCloseUI _closeUserInterface = new ExCubeGameCloseUI();
        // In event used skills
        private final Skill _freeze;
        private final Skill _transformationRed;
        private final Skill _transformationBlue;

        public StartEvent() {
            // Initialize all used skills
            _freeze = SkillTable.INSTANCE.getInfo(6034);
            _transformationRed = SkillTable.INSTANCE.getInfo(6035);
            _transformationBlue = SkillTable.INSTANCE.getInfo(6036);
        }

        /**
         * Will set up all getPlayer parameters and
         * port them to their respective location
         * based on their teams
         */
        private void setUpPlayers() {
            // Set current arena as being used
            HandysBlockCheckerManager.INSTANCE.setArenaBeingUsed(arena);
            // Initialize packets avoiding create a new one per getPlayer
            _redPoints = _spawns.size() / 2;
            _bluePoints = _spawns.size() / 2;
            final ExCubeGameChangePoints initialPoints = new ExCubeGameChangePoints(300, _bluePoints, _redPoints);
            ExCubeGameExtendedChangePoints clientSetUp;

            for (Player player : holder.getAllPlayers()) {
                if (player == null)
                    continue;

                player.addListener(_listener);

                // Send the secret client packet set up
                boolean isRed = holder.getRedPlayers().contains(player);

                clientSetUp = new ExCubeGameExtendedChangePoints(300, _bluePoints, _redPoints, isRed, player, 0);
                player.sendPacket(clientSetUp);

                player.sendActionFailed();

                // teleport Player - Array access
                // Team 0 * 2 = 0; 0 = 0, 0 + 1 = 1.
                // Team 1 * 2 = 2; 2 = 2, 2 + 1 = 3
                int tc = holder.getPlayerTeam(player) * 2;
                // Get x and y coordinates
                int x = _arenaCoordinates[arena][tc];
                int y = _arenaCoordinates[arena][tc + 1];
                player.teleToLocation(x, y, _zCoord);
                // Set the getPlayer team
                if (isRed) {
                    _redTeamPoints.put(player, 0);
                    player.setTeam(TeamType.RED);
                } else {
                    _blueTeamPoints.put(player, 0);
                    player.setTeam(TeamType.BLUE);
                }
                player.getEffectList().stopAllEffects();

                if (player.getPet() != null)
                    player.getPet().unSummon();

                // Give the getPlayer start up effects
                // Freeze
                _freeze.getEffects(player);
                // Tranformation
                if (holder.getPlayerTeam(player) == 0)
                    _transformationRed.getEffects(player);
                else
                    _transformationBlue.getEffects(player);
                // Set the current getPlayer arena
                player.setBlockCheckerArena((byte) arena);
                // Send needed packets
                player.sendPacket(initialPoints);
                player.sendPacket(_closeUserInterface);
                // ExBasicActionList
                player.sendPacket(new ExBasicActionList(player));
                broadcastRelationChanged(player);
                player.broadcastCharInfo();
            }
        }

        @Override
        public void runImpl() {
            // Wrong arena passed, stop event
            if (arena == -1) {
                LOG.error("Couldnt set up the arena Id for the Block Checker event, cancelling event...");
                return;
            }
            if (isStarted())
                return;
            clearArena(zoneNames.get(arena));
            _isStarted = true;
            // Spawn the blocks
            ThreadPoolManager.INSTANCE.execute(new SpawnRound(16, 1));
            // Start up getPlayer parameters
            setUpPlayers();
            // Set the started time
            _startedTime = System.currentTimeMillis() + 300000;
        }
    }

    /**
     * This class spawns the second round of boxes
     * and schedules the event end
     */
    private class SpawnRound extends RunnableImpl {
        final int numOfBoxes;
        final int round;

        SpawnRound(int numOfBoxes, int round) {
            this.numOfBoxes = numOfBoxes;
            this.round = round;
        }

        @Override
        public void runImpl() {
            if (!_isStarted)
                return;

            switch (round) {
                case 1:
                    // Schedule second spawn round
                    _task = ThreadPoolManager.INSTANCE.schedule(new SpawnRound(20, 2), 60000);
                    break;
                case 2:
                    // Schedule third spawn round
                    _task = ThreadPoolManager.INSTANCE.schedule(new SpawnRound(14, 3), 60000);
                    break;
                case 3:
                    // Schedule Event End Count Down
                    _task = ThreadPoolManager.INSTANCE.schedule(new CountDown(), 175000);
                    break;
            }
            // random % 2, if == 0 will spawn a red setBlock
            // if != 0, will spawn a blue setBlock
            byte random = 2;
            // common template
            // Spawn blocks
            // Creates 50 new blocks
            for (int i = 0; i < numOfBoxes; i++) {
                SimpleSpawner spawn = new SimpleSpawner(18672);
                spawn.setLoc(new Location(_arenaCoordinates[arena][4] + Rnd.get(-400, 400),
                        _arenaCoordinates[arena][5] + Rnd.get(-400, 400),
                        _zCoord, 1));
                spawn.setAmount(1);
                spawn.setRespawnDelay(1);
                BlockInstance blockInstance = (BlockInstance) spawn.doSpawn(true);
                blockInstance.setRed(random % 2 == 0);

                _spawns.add(spawn);
                random++;
            }

            // Spawn the setBlock carrying girl
            if (round == 1 || round == 2) {
                final SimpleSpawner girlSpawn = new SimpleSpawner(18676);
                girlSpawn.setLoc(new Location(_arenaCoordinates[arena][4] + Rnd.get(-400, 400),
                        _arenaCoordinates[arena][5] + Rnd.get(-400, 400),
                        _zCoord, 1))
                        .setAmount(1)
                        .setRespawnDelay(1)
                        .init();
                girlSpawn.doSpawn(true);
                _girlNpc = girlSpawn.getLastSpawn();
                // Schedule his deletion after 9 secs of spawn
                ThreadPoolManager.INSTANCE.schedule(() -> {
                    if (_girlNpc == null)
                        return;
                    _girlNpc.deleteMe();
                }, 9000);
            }

            _redPoints += numOfBoxes / 2;
            _bluePoints += numOfBoxes / 2;

            int timeLeft = (int) ((getStarterTime() - System.currentTimeMillis()) / 1000);
            ExCubeGameChangePoints changePoints = new ExCubeGameChangePoints(timeLeft, getBluePoints(), getRedPoints());
            getHolder().broadCastPacketToTeam(changePoints);
        }
    }

    class CountDown extends RunnableImpl {
        private int seconds = 5;

        @Override
        public void runImpl() {
            switch (seconds) {
                case 5:
                    holder.broadCastPacketToTeam(new SystemMessage(SystemMessage.BLOCK_CHECKER_WILL_END_IN_5_SECONDS));
                    break;
                case 4:
                    holder.broadCastPacketToTeam(new SystemMessage(SystemMessage.BLOCK_CHECKER_WILL_END_IN_4_SECONDS));
                    break;
                case 3:
                    holder.broadCastPacketToTeam(new SystemMessage(SystemMessage.BLOCK_CHECKER_WILL_END_IN_3_SECONDS));
                    break;
                case 2:
                    holder.broadCastPacketToTeam(new SystemMessage(SystemMessage.BLOCK_CHECKER_WILL_END_IN_2_SECONDS));
                    break;
                case 1:
                    holder.broadCastPacketToTeam(new SystemMessage(SystemMessage.BLOCK_CHECKER_WILL_END_IN_1_SECOND));
                    break;
            }

            if (--seconds > 0)
                ThreadPoolManager.INSTANCE.schedule(this, 1000L);
            else
                ThreadPoolManager.INSTANCE.execute(new EndEvent());
        }
    }

    /**
     * This class erase all event parameters on getPlayer
     * and port them back near Handy. Also, unspawn
     * blocks, runs a garbage collector and set as free
     * the used arena
     */
    class EndEvent extends RunnableImpl {
        // Garbage collector and arena free setter
        private void clearMe() {
            HandysBlockCheckerManager.INSTANCE.clearPaticipantQueueByArenaId(arena);
            for (Player player : holder.getAllPlayers()) {
                if (player == null)
                    continue;

                player.removeListener(_listener);
            }
            holder.clearPlayers();
            _blueTeamPoints.clear();
            _redTeamPoints.clear();
            HandysBlockCheckerManager.INSTANCE.setArenaFree(arena);

            for (SimpleSpawner spawn : _spawns)
                spawn.deleteAll();

            _spawns.clear();

            for (ItemInstance item : drops) {
                // npe
                if (item == null)
                    continue;

                // a getPlayer has it, it will be deleted later
                if (!item.isVisible() || item.getOwnerId() != 0)
                    continue;

                item.deleteMe();
            }
            drops.clear();
        }

        /**
         * Reward players after event.
         * Tie - No Reward
         */
        private void rewardPlayers() {
            if (_redPoints == _bluePoints)
                return;

            _isRedWinner = _redPoints > _bluePoints;

            if (_isRedWinner) {
                rewardAsWinner(true);
                rewardAsLooser(false);
                SystemMessage msg = new SystemMessage(SystemMessage.THE_C1_TEAM_HAS_WON).addString("Red Team");

                holder.broadCastPacketToTeam(msg);
            } else {
                rewardAsWinner(false);
                rewardAsLooser(true);
                SystemMessage msg = new SystemMessage(SystemMessage.THE_C1_TEAM_HAS_WON).addString("Blue Team");
                holder.broadCastPacketToTeam(msg);
            }
        }

        private void addRewardItemWithMessage(int id, long count, Player player) {
            player.getInventory().addItem(id, (long) (count * Config.ALT_RATE_COINS_REWARD_BLOCK_CHECKER), "Block Checked Reward");
            player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_OBTAINED_S2_S1).addItemName(id).addNumber(count));
        }

        /**
         * Reward the speicifed team as a winner team
         * 1) Higher score - 8 extra
         * 2) Higher score - 5 extra
         *
         * @param isRed
         */
        private void rewardAsWinner(boolean isRed) {
            Map<Player, Integer> tempPoints = isRed ? _redTeamPoints : _blueTeamPoints;

            // Main give
            for (Player pc : tempPoints.keySet()) {
                if (pc == null)
                    continue;

                if (tempPoints.get(pc) >= 10)
                    addRewardItemWithMessage(13067, 2, pc);
                else
                    tempPoints.remove(pc);
            }

            int first = 0, second = 0;
            Player winner1 = null, winner2 = null;
            for (Player pc : tempPoints.keySet()) {
                int pcPoints = tempPoints.get(pc);
                if (pcPoints > first) {
                    // Move old data
                    second = first;
                    winner2 = winner1;
                    // Set new data
                    first = pcPoints;
                    winner1 = pc;
                } else if (pcPoints > second) {
                    second = pcPoints;
                    winner2 = pc;
                }
            }
            if (winner1 != null)
                addRewardItemWithMessage(13067, 200, winner1);
            if (winner2 != null)
                addRewardItemWithMessage(13067, 100, winner2);
        }

        /**
         * Will reward the looser team with the
         * predefined rewards
         * Player got >= 10 points: 2 coins
         * Player got < 10 points: 0 coins
         */
        private void rewardAsLooser(boolean isRed) {
            Map<Player, Integer> tempPoints = isRed ? _redTeamPoints : _blueTeamPoints;

            for (Player player : tempPoints.keySet())
                if (player != null && tempPoints.get(player) >= 10)
                    addRewardItemWithMessage(13067, 2, player);
        }

        /**
         * Telport players back, give status back and
         * send final packet
         */
        private void setPlayersBack() {
            final ExCubeGameEnd end = new ExCubeGameEnd(_isRedWinner);

            for (Player player : holder.getAllPlayers()) {
                if (player == null)
                    continue;

                player.getEffectList().stopAllEffects();
                // Remove team aura
                player.setTeam(TeamType.NONE);
                // Set default arena
                player.setBlockCheckerArena(DEFAULT_ARENA);
                // Remove the event items
                PcInventory inv = player.getInventory();
                inv.destroyItemByItemId(13787, inv.getCountOf(13787), "Block Checker");
                inv.destroyItemByItemId(13788, inv.getCountOf(13788), "Block Checker");
                broadcastRelationChanged(player);
                // teleport Back
                player.teleToLocation(-57478, -60367, -2370);
                // Send end packet
                player.sendPacket(end);
                player.broadcastCharInfo();
            }
        }

        @Override
        public void runImpl() {
            if (!_abnormalEnd)
                rewardPlayers();
            _isStarted = false;
            setPlayersBack();
            clearMe();
            _abnormalEnd = false;
        }
    }

    private class OnExitPlayerListener implements OnTeleportListener, OnPlayerExitListener {
        private boolean _isExit = false;

        @Override
        public void onTeleport(Player player, Location loc, Reflection reflection) {
            if (_isExit)
                return;
            onPlayerExit(player);
        }

        @Override
        public void onPlayerExit(final Player player) {
            if (player.getBlockCheckerArena() < 0)
                return;
            _isExit = true;
            player.teleToLocation(-57478, -60367, -2370);
            player.setTransformation(0);
            player.getEffectList().stopAllEffects();
            int arena = player.getBlockCheckerArena();
            int team = HandysBlockCheckerManager.INSTANCE.getHolder(arena).getPlayerTeam(player);
            HandysBlockCheckerManager.INSTANCE.removePlayer(player, arena, team);
            // Remove team aura
            player.setTeam(TeamType.NONE);
            player.broadcastCharInfo();

            // Remove the event items
            PcInventory inv = player.getInventory();
            inv.destroyItemByItemId(13787, inv.getCountOf(13787), "Block Checker");
            inv.destroyItemByItemId(13788, inv.getCountOf(13788), "Block Checker");
        }
    }

}