package l2trunk.gameserver.model.entity;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.InstantZoneHolder;
import l2trunk.gameserver.instancemanager.DimensionalRiftManager;
import l2trunk.gameserver.instancemanager.DimensionalRiftManager.DimensionalRiftRoom;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.InstantZone;
import l2trunk.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class DimensionalRift extends Reflection {
    private static final int MILLISECONDS_IN_MINUTE = 60000;

    final int roomType;
    private List<Integer> _completedRooms = new ArrayList<>();
    private int jumps_current = 0;
    private int _choosenRoom = -1;
    private boolean _hasJumped = false;
    private boolean isBossRoom = false;
    private Future<?> teleporterTask;
    private Future<?> spawnTask;
    private Future<?> killRiftTask;

    public DimensionalRift(Party party, int type, int room) {
        super();
        onCreate();
        startCollapseTimer(7200000); // 120 минут таймер, для защиты от утечек памяти
        setName("DimensionalRift");
        if (this instanceof DelusionChamber) {
            InstantZone iz = InstantZoneHolder.getInstantZone(type + 120); // Для равенства типа комнаты и ИД инстанса
            setInstancedZone(iz);
            setName(iz.getName());
        }
        roomType = type;
        setParty(party);
        if (!(this instanceof DelusionChamber))
            party.setDimensionalRift(this);
        party.setReflection(this);
        _choosenRoom = room;
        checkBossRoom(_choosenRoom);

        Location coords = getRoomCoord(_choosenRoom);

        setReturnLoc(party.getLeader().getLoc());
        setTeleportLoc(coords);
        for (Player p : party.getMembers()) {
            p.setVar("backCoords", getReturnLoc().toXYZString(), -1);
            DimensionalRiftManager.teleToLocation(p, Location.findPointToStay(coords, 50, 100, getGeoIndex()), this);
            p.setReflection(this);
        }

        createSpawnTimer(_choosenRoom);
        createTeleporterTimer();
    }

    public int getType() {
        return roomType;
    }

    public int getCurrentRoom() {
        return _choosenRoom;
    }

    private void createTeleporterTimer() {
        if (teleporterTask != null) {
            teleporterTask.cancel(false);
            teleporterTask = null;
        }

        teleporterTask = ThreadPoolManager.INSTANCE.schedule(new RunnableImpl() {
            @Override
            public void runImpl() {
                if (jumps_current < getMaxJumps() && getPlayersInside(true) > 0) {
                    jumps_current++;
                    teleportToNextRoom();
                    createTeleporterTimer();
                } else
                    createNewKillRiftTimer();
            }
        }, calcTimeToNextJump()); //Teleporter task, 8-10 minutes
    }

    private void createSpawnTimer(int room) {
        if (spawnTask != null) {
            spawnTask.cancel(false);
            spawnTask = null;
        }

        final DimensionalRiftRoom riftRoom = DimensionalRiftManager.INSTANCE.getRoom(roomType, room);

        spawnTask = ThreadPoolManager.INSTANCE.schedule(() -> {
            for (SimpleSpawner s : riftRoom.getSpawns()) {
                SimpleSpawner sp = s.newInstance();
                sp.setReflection(DimensionalRift.this);
                addSpawn(sp);
                if (!isBossRoom)
                    sp.startRespawn();
                for (int i = 0; i < sp.getAmount(); i++)
                    sp.doSpawn(true);
            }
            DimensionalRift.this.addSpawnWithoutRespawn(getManagerId(), riftRoom.getTeleportCoords(), 0);
        }, Config.RIFT_SPAWN_DELAY);
    }

    synchronized void createNewKillRiftTimer() {
        if (killRiftTask != null) {
            killRiftTask.cancel(false);
            killRiftTask = null;
        }

        killRiftTask = ThreadPoolManager.INSTANCE.schedule(() -> {
            if (isCollapseStarted())
                return;
            getParty().getMembers().forEach(p -> {
                if (p != null && p.getReflection() == DimensionalRift.this)
                    DimensionalRiftManager.INSTANCE.teleportToWaitingRoom(p);
                DimensionalRift.this.collapse();
            });
        }, 100L);
    }

    public void partyMemberInvited() {
        createNewKillRiftTimer();
    }

    public void partyMemberExited(Player player) {
        if (getParty().size() < Config.RIFT_MIN_PARTY_SIZE || getParty().size() == 1 || getPlayersInside(true) == 0)
            createNewKillRiftTimer();
    }

    public void manualTeleport(Player player, NpcInstance npc) {
        if (!player.isInParty() || !player.getParty().isInReflection() || !(player.getParty().getReflection() instanceof DimensionalRift))
            return;

        if (!player.getParty().isLeader(player)) {
            DimensionalRiftManager.INSTANCE.showHtmlFile(player, "rift/NotPartyLeader.htm", npc);
            return;
        }

        if (!isBossRoom) {
            if (_hasJumped) {
                DimensionalRiftManager.INSTANCE.showHtmlFile(player, "rift/AlreadyTeleported.htm", npc);
                return;
            }
            _hasJumped = true;
        } else {
            manualExitRift(player, npc);
            return;
        }

        teleportToNextRoom();
    }

    public void manualExitRift(Player player, NpcInstance npc) {
        if (!player.isInParty() || !player.getParty().isInDimensionalRift())
            return;

        if (!player.getParty().isLeader(player)) {
            DimensionalRiftManager.INSTANCE.showHtmlFile(player, "rift/NotPartyLeader.htm", npc);
            return;
        }

        createNewKillRiftTimer();
    }

    private void teleportToNextRoom() {
        _completedRooms.add(_choosenRoom);

        for (Spawner s : getSpawns())
            s.deleteAll();

        int size = DimensionalRiftManager.INSTANCE.getRooms(roomType).size();
		/*
		if (jumps_current < getMaxJumps())
			size--; // комната босса может быть только последней
		 */

        if (getType() >= 11 && jumps_current == getMaxJumps())
            _choosenRoom = 9; // В DC последние 2 печати всегда кончаются рейдом
        else { // выбираем комнату, где еще не были
            List<Integer> notCompletedRooms = new ArrayList<>();
            for (int i = 1; i <= size; i++)
                if (!_completedRooms.contains(i))
                    notCompletedRooms.add(i);
            _choosenRoom = notCompletedRooms.get(Rnd.get(notCompletedRooms.size()));
        }

        checkBossRoom(_choosenRoom);
        setTeleportLoc(getRoomCoord(_choosenRoom));

        for (Player p : getParty().getMembers())
            if (p.getReflection() == this)
                DimensionalRiftManager.teleToLocation(p, Location.findPointToStay(getRoomCoord(_choosenRoom), 50, 100, DimensionalRift.this.getGeoIndex()), this);

        createSpawnTimer(_choosenRoom);
    }

    @Override
    public void collapse() {
        if (isCollapseStarted())
            return;

        Future<?> task = teleporterTask;
        if (task != null) {
            teleporterTask = null;
            task.cancel(false);
        }

        task = spawnTask;
        if (task != null) {
            spawnTask = null;
            task.cancel(false);
        }

        task = killRiftTask;
        if (task != null) {
            killRiftTask = null;
            task.cancel(false);
        }

        _completedRooms = null;

        Party party = getParty();
        if (party != null)
            party.setDimensionalRift(null);

        super.collapse();
    }

    private long calcTimeToNextJump() {
        if (isBossRoom)
            return 60 * MILLISECONDS_IN_MINUTE;
        return Config.RIFT_AUTO_JUMPS_TIME * MILLISECONDS_IN_MINUTE + Rnd.get(Config.RIFT_AUTO_JUMPS_TIME_RAND);
    }

    public void memberDead(Player player) {
        if (getPlayersInside(true) == 0)
            createNewKillRiftTimer();
    }

    public void usedTeleport(Player player) {
        if (getPlayersInside(false) < Config.RIFT_MIN_PARTY_SIZE)
            createNewKillRiftTimer();
    }

    private void checkBossRoom(int room) {
        isBossRoom = DimensionalRiftManager.INSTANCE.getRoom(roomType, room).isBossRoom();
    }

    private Location getRoomCoord(int room) {
        return DimensionalRiftManager.INSTANCE.getRoom(roomType, room).getTeleportCoords();
    }

    /**
     * По умолчанию 4
     */
    private int getMaxJumps() {
        return Math.max(Math.min(Config.RIFT_MAX_JUMPS, 8), 1);
    }

    @Override
    public boolean canChampions() {
        return true;
    }

    @Override
    public String getName() {
        return "DimensionalRift";
    }

    int getManagerId() {
        return 31865;
    }

    int getPlayersInside(boolean alive) {
        if (_playerCount == 0)
            return 0;
        return (int) getPlayers()
                .filter(p -> (!alive || !p.isDead()))
                .count();
    }

    @Override
    public void removeObject(GameObject o) {
        if (o.isPlayer())
            if (_playerCount <= 1)
                createNewKillRiftTimer();
        super.removeObject(o);
    }
}