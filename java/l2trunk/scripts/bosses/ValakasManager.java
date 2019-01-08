package l2trunk.scripts.bosses;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.instances.BossInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.Earthquake;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.PlaySound;
import l2trunk.gameserver.network.serverpackets.SocialAction;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.Log;
import l2trunk.gameserver.utils.ReflectionUtils;
import l2trunk.gameserver.utils.TimeUtils;
import l2trunk.scripts.bosses.EpicBossState.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Stream;

import static l2trunk.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

public final class ValakasManager extends Functions implements ScriptFile, OnDeathListener {
    private static final Logger _log = LoggerFactory.getLogger(ValakasManager.class);
    private static final List<Integer> taskDelays = List.of(
            16, 1500, 3300, 2900, 2700, 1, 3200, 1400, 6700, 5700, 600000, 500, 3500, 4500, 500, 4600, 750, 2500);
    private static final List<Location> _teleportCubeLocation = List.of(
            new Location(214880, -116144, -1644),
            new Location(213696, -116592, -1644),
            new Location(212112, -116688, -1644),
            new Location(211184, -115472, -1664),
            new Location(210336, -114592, -1644),
            new Location(211360, -113904, -1644),
            new Location(213152, -112352, -1644),
            new Location(214032, -113232, -1644),
            new Location(214752, -114592, -1644),
            new Location(209824, -115568, -1421),
            new Location(210528, -112192, -1403),
            new Location(213120, -111136, -1408),
            new Location(215184, -111504, -1392),
            new Location(215456, -117328, -1392),
            new Location(213200, -118160, -1424));

    private static final List<NpcInstance> _teleportCube = new ArrayList<>();
    private static final List<NpcInstance> _spawnedMinions = new ArrayList<>();
    private static final int Valakas = 29028;
    private static final int _teleportCubeId = 31759;
    private static final int FWV_LIMITUNTILSLEEP = 20 * 60000; // 20
    private static final int FWV_APPTIMEOFVALAKAS = 15 * 60000;     // 10
    private static final Location TELEPORT_POSITION = new Location(203940, -111840, 66);
    private static BossInstance valakas;
    // Tasks.
    private static ScheduledFuture<?> valakasSpawnTask = null;
    private static ScheduledFuture<?> _intervalEndTask = null;
    private static ScheduledFuture<?> _socialTask = null;
    private static ScheduledFuture<?> _mobiliseTask = null;
    private static ScheduledFuture<?> _moveAtRandomTask = null;
    private static ScheduledFuture<?> _respawnValakasTask = null;
    private static ScheduledFuture<?> _sleepCheckTask = null;
    private static ScheduledFuture<?> onAnnihilatedTask = null;
    private static EpicBossState state;
    private static Zone zone;
    private static long lastAttackTime = 0;
    private static boolean dying = false;
    private static boolean _entryLocked = false;

    private synchronized static void checkAnnihilated() {
        if (onAnnihilatedTask == null && getPlayersInside().allMatch(Player::isDead))
            onAnnihilatedTask = ThreadPoolManager.INSTANCE.schedule(ValakasManager::sleep, 5000);
    }

    private static Stream<Player> getPlayersInside() {
        return getZone().getInsidePlayers();
    }

    private static long getRespawnInterval() {
        return (long) (Config.ALT_RAID_RESPAWN_MULTIPLIER * (Config.VALAKAS_DEFAULT_SPAWN_HOURS * 60 * 60000 + Rnd.get(0, Config.VALAKAS_RANDOM_SPAWN_HOURS * 60 * 60000)));
    }

    public static Zone getZone() {
        return zone;
    }

    private static void onValakasDie() {
        if (dying)
            return;

        dying = true;
        state.setRespawnDate(getRespawnInterval());
        state.setState(State.INTERVAL);
        state.update();

        _entryLocked = false;
        _teleportCubeLocation.forEach(loc ->
                _teleportCube.add(Functions.spawn(loc, _teleportCubeId)));
        Log.add("Valakas died", "bosses");
    }

    // Start interval.
    private static void setIntervalEndTask() {
        setUnspawn();

        if (state.getState().equals(State.ALIVE)) {
            state.setState(State.NOTSPAWN);
            state.update();
            return;
        }

        if (!state.getState().equals(State.INTERVAL)) {
            state.setRespawnDate(getRespawnInterval());
            state.setState(State.INTERVAL);
            state.update();
        }

        _intervalEndTask = ThreadPoolManager.INSTANCE.schedule(new IntervalEnd(), state.getInterval());
    }

    // Clean Valakas's lair.
    private static void setUnspawn() {
        // Eliminate players.
        getPlayersInside().forEach(Player::teleToClosestTown);

        _entryLocked = false;

        if (valakas != null)
            valakas.deleteMe();

        for (NpcInstance npc : _spawnedMinions)
            npc.deleteMe();

        // Delete teleport cube.
        _teleportCube.forEach(cube -> {
            cube.getSpawn().stopRespawn();
            cube.deleteMe();
        });
        _teleportCube.clear();

        if (valakasSpawnTask != null) {
            valakasSpawnTask.cancel(false);
            valakasSpawnTask = null;
        }
        if (_intervalEndTask != null) {
            _intervalEndTask.cancel(false);
            _intervalEndTask = null;
        }
        if (_socialTask != null) {
            _socialTask.cancel(false);
            _socialTask = null;
        }
        if (_mobiliseTask != null) {
            _mobiliseTask.cancel(false);
            _mobiliseTask = null;
        }
        if (_moveAtRandomTask != null) {
            _moveAtRandomTask.cancel(false);
            _moveAtRandomTask = null;
        }
        if (_sleepCheckTask != null) {
            _sleepCheckTask.cancel(false);
            _sleepCheckTask = null;
        }
        if (_respawnValakasTask != null) {
            _respawnValakasTask.cancel(false);
            _respawnValakasTask = null;
        }
        if (onAnnihilatedTask != null) {
            onAnnihilatedTask.cancel(false);
            onAnnihilatedTask = null;
        }
    }

    private static void sleep() {
        setUnspawn();
        if (state.getState().equals(State.ALIVE)) {
            state.setState(State.NOTSPAWN);
            state.update();
        }
    }

    public static void setLastAttackTime() {
        lastAttackTime = System.currentTimeMillis();
    }

    // Setting Valakas spawn task.
    private synchronized static void setValakasSpawnTask() {
        if (valakasSpawnTask == null)
            valakasSpawnTask = ThreadPoolManager.INSTANCE.schedule(new SpawnDespawn(1), FWV_APPTIMEOFVALAKAS);
    }

    public static boolean isEnableEnterToLair() {
        return state.getState() == State.NOTSPAWN;
    }

    public static void broadcastScreenMessage(NpcString npcs) {
        getPlayersInside().forEach(p ->
                p.sendPacket(new ExShowScreenMessage(npcs)));
    }

    public static void enterTheLair(Player player) {
        if (player == null)
            return;

        if (player.getParty() == null || !player.getParty().isInCommandChannel()) {
            player.sendPacket(Msg.YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_IN_A_CURRENT_COMMAND_CHANNEL);
            return;
        }
        CommandChannel cc = player.getParty().getCommandChannel();
        if (cc.size() > 200) {
            player.sendMessage("The maximum of 200 players can invade the Valakas Nest");
            return;
        }
        if (getPlayersInside().count() > 200) {
            player.sendMessage("The maximum of 200 players can invade the Valakas Nest");
            return;
        }
        if (state.getState() != State.NOTSPAWN) {
            player.sendMessage("Valakas is still reborning. You cannot invade the nest now");
            return;
        }
        if (_entryLocked) {
            player.sendMessage("Valakas has already been reborned and is being attacked. The entrance is sealed.");
            return;
        }

        if (player.isDead() || player.isFlying() || player.isCursedWeaponEquipped()) {
            player.sendMessage("You doesn't meet the requirements to enter the nest");
            return;
        }

        player.teleToLocation(TELEPORT_POSITION);
        setValakasSpawnTask();
    }

    public static EpicBossState getState() {
        return state;
    }

    private static void showMovie(int dist, int yaw, int pitch, int time, int turn, int rise, int unk) {
        getPlayersInside()
                .filter(pc -> pc.getDistance(valakas) <= dist)
                .forEach(pc -> {
                    pc.enterMovieMode();
                    pc.specialCamera(valakas, dist, yaw, pitch, time, 15000, turn, rise, 1, unk);
                });

        getPlayersInside()
                .filter(pc -> pc.getDistance(valakas) > dist)
                .forEach(Player::leaveMovieMode);
    }

    private static void spawnDespawn(int taskId) {
        switch (taskId) {
            case 1:
                // Do spawn.
                valakas = (BossInstance) Functions.spawn(new Location(212852, -114842, -1632, 833), Valakas);

                valakas.setBlock(true);
                valakas.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS03_A", 1, valakas.getObjectId(), valakas.getLoc()));

                state.setRespawnDate(getRespawnInterval());
                state.setState(State.ALIVE);
                state.update();

                break;
            case 2:
                // Do social.
                valakas.broadcastPacket(new SocialAction(valakas.getObjectId(), 1));
                showMovie(1800, 180, -1, 1500, 0, 0, 0);
                break;
            case 3:
                // Set camera.
                showMovie(1300, 180, -5, 3000, 0, -5, 0);
                break;
            case 4:
                // Set camera.
                showMovie(500, 180, -8, 600, 0, 60, 0);
                break;
            case 5:
                // Set camera.
                showMovie(800, 180, -8, 2700, 0, 30, 0);
                break;
            case 6:
                // Set camera.
                showMovie(200, 250, 70, 0, 30, 80, 0);
                break;
            case 7:
                // Set camera.
                showMovie(1100, 250, 70, 2500, 30, 80, 0);
                break;
            case 8:
                showMovie(700, 150, 30, 0, -10, 60, 0);
                break;
            case 9:
                showMovie(1200, 150, 20, 2900, -10, 30, 0);
                break;
            case 10:
                // Set camera.
                showMovie(750, 170, -10, 3400, 10, -15, 0);
                break;
            case 11:
                // Reset camera.
                getPlayersInside().forEach(Player::leaveMovieMode);
                valakas.setBlock();
                broadcastScreenMessage(NpcString.VALAKAS_ARROGAANT_FOOL_YOU_DARE_TO_CHALLENGE_ME);
                // Move at random.
                if (valakas.getAI().getIntention() == AI_INTENTION_ACTIVE)
                    valakas.moveToLocation(new Location(Rnd.get(211080, 214909), Rnd.get(-115841, -112822), -1662, 0), 0, false);
                break;

            // Death Movie
            case 12:
                valakas.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "B03_D", 1, valakas.getObjectId(), valakas.getLoc()));
                broadcastScreenMessage(NpcString.VALAKAS_THE_EVIL_FIRE_DRAGON_VALAKAS_DEFEATED);
                onValakasDie();
                showMovie(2000, 130, -1, 0, 0, 0, 1);
                break;
            case 13:
                showMovie(1100, 210, -5, 3000, -13, 0, 1);
                break;
            case 14:
                showMovie(1300, 200, -8, 3000, 0, 15, 1);
                break;
            case 15:
                showMovie(1000, 190, 0, 500, 0, 10, 1);
                break;
            case 16:
                showMovie(1700, 120, 0, 2500, 12, 40, 1);
                break;
            case 17:
                showMovie(1700, 20, 0, 700, 10, 10, 1);
                break;
            case 18:
                showMovie(1700, 10, 0, 1000, 20, 70, 1);
                break;
            case 19:
                getPlayersInside().forEach(pc -> {
                    pc.leaveMovieMode();
                    pc.altOnMagicUseTimer(pc, 23312);
                });
        }
    }

    @Override
    public void onDeath(Creature self, Creature killer) {
        if (self.isPlayer() && state != null && state.getState() == State.ALIVE && zone != null && zone.checkIfInZone(self.getX(), self.getY()))
            checkAnnihilated();
        else if (self.isNpc() && self.getNpcId() == Valakas)
            ThreadPoolManager.INSTANCE.schedule(new SpawnDespawn(12), 1);
    }

    private void init() {
        CharListenerList.addGlobal(this);
        state = new EpicBossState(Valakas);
        zone = ReflectionUtils.getZone("[valakas_epic]");
        _log.info("ValakasManager: State of Valakas is " + state.getState() + ".");
        if (!state.getState().equals(State.NOTSPAWN))
            setIntervalEndTask();

        _log.info("ValakasManager: Next spawn date of Valakas is " + TimeUtils.toSimpleFormat(state.getRespawnDate()) + ".");
    }

    public void onLoad() {
        init();
    }

    public void onReload() {
        sleep();
    }

    private static class CheckLastAttack extends RunnableImpl {
        @Override
        public void runImpl() {
            if (state.getState() == State.ALIVE)
                if (lastAttackTime + FWV_LIMITUNTILSLEEP < System.currentTimeMillis())
                    sleep();
                else
                    _sleepCheckTask = ThreadPoolManager.INSTANCE.schedule(new CheckLastAttack(), 60000);
        }
    }

    private static class IntervalEnd extends RunnableImpl {
        @Override
        public void runImpl() {
            state.setState(State.NOTSPAWN);
            state.update();
            //Earthquake
            Location loc = new Location(213896, -115436, -1644);
            GameObjectsStorage.getAllPlayers().forEach(p ->
                    p.broadcastPacket(new Earthquake(loc, 20, 10)));
        }
    }

    private static class SpawnDespawn extends RunnableImpl {
        private final int taskId;

        SpawnDespawn(int taskId) {
            this.taskId = taskId;
        }

        @Override
        public void runImpl() {
            _entryLocked = true;
            if (taskId == 11) {
                // Reset camera.
                spawnDespawn(taskId);
                _sleepCheckTask = ThreadPoolManager.INSTANCE.schedule(new CheckLastAttack(), taskDelays.get(taskId));
            } else if (taskId == 19) {
                spawnDespawn(taskId);
            } else {
                spawnDespawn(taskId);
                _socialTask = ThreadPoolManager.INSTANCE.schedule(new SpawnDespawn(taskId + 1), taskDelays.get(taskId));
            }
        }
    }
}