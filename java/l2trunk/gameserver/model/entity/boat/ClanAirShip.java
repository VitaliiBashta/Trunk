package l2trunk.gameserver.model.entity.boat;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.BoatHolder;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.actions.StartStopAction;
import l2trunk.gameserver.model.entity.events.impl.BoatWayEvent;
import l2trunk.gameserver.model.entity.events.objects.BoatPoint;
import l2trunk.gameserver.model.instances.ControlKeyInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.DeleteObject;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.AirshipDock;
import l2trunk.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public final class ClanAirShip extends AirShip {
    public static final int MAX_FUEL = 600;
    private static final long MAINTENANCE_DELAY = 60 * 1000L;
    private final GameObject controlKey = new ControlKeyInstance();
    private final Clan clan;
    private int _currentFuel;
    private AirshipDock dock;
    private AirshipDock.AirshipPlatform platform;
    private Player driver = null;
    private boolean customMove;
    private Future<?> _deleteTask = null;

    public ClanAirShip(Clan clan) {
        super(IdFactory.getInstance().getNextId(), BoatHolder.TEMPLATE);

        BoatHolder.getInstance().addBoat(this);
        this.clan = clan;
        this.clan.setAirship(this);
        _currentFuel = clan.getAirshipFuel();
    }

    @Override
    public void onSpawn() {
        controlKey.spawnMe(getLoc());
    }

    @Override
    protected void updatePeopleInTheBoat(Location loc) {
        super.updatePeopleInTheBoat(loc);
        controlKey.setLoc(loc);
    }

    @Override
    public void oustPlayer(Player player, Location loc, boolean teleport) {
        if (player == getDriver())
            setDriver(null);

        super.oustPlayer(player, loc, teleport);
    }

    public void startDepartTask() {
        BoatWayEvent arrivalWay = new BoatWayEvent(this);
        BoatWayEvent departWay = new BoatWayEvent(this);

        platform.getArrivalPoints().forEach(p ->
                arrivalWay.addObject(BoatWayEvent.BOAT_POINTS, p));

        platform.getDepartPoints().forEach(p ->
                departWay.addObject(BoatWayEvent.BOAT_POINTS, p));

        arrivalWay.addOnTimeAction(0, new StartStopAction(StartStopAction.EVENT, true));
        departWay.addOnTimeAction(300, new StartStopAction(StartStopAction.EVENT, true));

        setWay(0, arrivalWay);
        setWay(1, departWay);

        arrivalWay.reCalcNextTime(false);
    }

    public void startArrivalTask() {
        if (_deleteTask != null) {
            _deleteTask.cancel(true);
            _deleteTask = null;
        }

        players.forEach(player -> {
            player.showQuestMovie(platform.getOustMovie());
            oustPlayer(player, getReturnLoc(), true);
        });

        deleteMe();
    }

    public void addTeleportPoint(Player player, int id) {
        if (isMoving || !isDocked()) {
            return;
        }

        if (id == 0) {
            getCurrentWay().clearActions();
            getCurrentWay().startEvent();
        } else {
            BoatPoint point = getDock().getTeleportList().get(id);

            if (getCurrentFuel() < point.getFuel()) {
                player.sendPacket(SystemMsg.YOUR_SHIP_CANNOT_TELEPORT_BECAUSE_IT_DOES_NOT_HAVE_ENOUGH_FUEL_FOR_THE_TRIP);
                return;
            }

            setCurrentFuel(getCurrentFuel() - point.getFuel());

            getCurrentWay().clearActions();
            getCurrentWay().addObject(BoatWayEvent.BOAT_POINTS, point);
            getCurrentWay().startEvent();
        }
    }

    @Override
    public void trajetEnded(boolean oust) {
        runState = 0;

        if (_fromHome == 0) {
            _fromHome = 1;
            getCurrentWay().reCalcNextTime(false);
        } else {
            customMove = true;
            _deleteTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new FuelAndDeleteTask(), MAINTENANCE_DELAY, MAINTENANCE_DELAY);
        }
    }

    @Override
    public void onEvtArrived() {
        if (!customMove)
            getCurrentWay().moveNext();
    }

    public int getCurrentFuel() {
        return _currentFuel;
    }

    public void setCurrentFuel(int fuel) {
        final int old = _currentFuel;
        _currentFuel = fuel;
        if (_currentFuel <= 0) {
            _currentFuel = 0;
            setMoveSpeed(150);
            setRotationSpeed(1000);
        } else if (_currentFuel > MAX_FUEL) {
            _currentFuel = MAX_FUEL;
        }

        if (_currentFuel == 0 && old > 0) {
            broadcastPacketToPassengers(SystemMsg.THE_AIRSHIPS_FUEL_EP_HAS_RUN_OUT);
        } else if (_currentFuel < 40) {
            broadcastPacketToPassengers(SystemMsg.THE_AIRSHIPS_FUEL_EP_WILL_SOON_RUN_OUT);
        }

        broadcastCharInfo();
    }

    public int getMaxFuel() {
        return MAX_FUEL;
    }

    public Player getDriver() {
        return driver;
    }

    public void setDriver(Player player) {
        if (player != null) {
            if (clan != player.getClan())
                return;

            if (player.getTargetId() != controlKey.objectId()) {
                player.sendPacket(SystemMsg.YOU_MUST_TARGET_THE_ONE_YOU_WISH_TO_CONTROL);
                return;
            }

            final int x = player.getInBoatPosition().x - 0x16e;
            final int y = player.getInBoatPosition().y;
            final int z = player.getInBoatPosition().z - 0x6b;
            if (x * x + y * y + z * z > 2500) {
                player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_BECAUSE_YOU_ARE_TOO_FAR);
                return;
            }

            if (player.isTrasformed()) {
                player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_THE_HELM_WHILE_TRANSFORMED);
                return;
            }

            if (player.isParalyzed()) {
                player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_THE_HELM_WHILE_YOU_ARE_PETRIFIED);
                return;
            }

            if (player.isDead() || player.isFakeDeath()) {
                player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_THE_HELM_WHEN_YOU_ARE_DEAD);
                return;
            }

            if (player.isFishing()) {
                player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_THE_HELM_WHILE_FISHING);
                return;
            }

            if (player.isInCombat()) {
                player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_THE_HELM_WHILE_IN_A_BATTLE);
                return;
            }

            if (player.isInDuel()) {
                player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_THE_HELM_WHILE_IN_A_DUEL);
                return;
            }

            if (player.isSitting()) {
                player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_THE_HELM_WHILE_IN_A_SITTING_POSITION);
                return;
            }

            if (player.isCastingNow()) {
                player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_THE_HELM_WHILE_USING_A_SKILL);
                return;
            }

            if (player.isCursedWeaponEquipped()) {
                player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_THE_HELM_WHILE_A_CURSED_WEAPON_IS_EQUIPPED);
                return;
            }

            if (player.getActiveWeaponFlagAttachment() != null) {
                player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_THE_HELM_WHILE_HOLDING_A_FLAG);
                return;
            }

            driver = player;

            player.setLockedTarget(true);
            player.unEquipWeapon();
            player.broadcastCharInfo();
        } else {
            Player oldDriver = getDriver();

            driver = null;

            if (oldDriver != null) {
                oldDriver.setLockedTarget(false);
                oldDriver.broadcastCharInfo();
            }
        }

        broadcastCharInfo();
    }

    public GameObject getControlKey() {
        return controlKey;
    }

    @Override
    protected void onDelete() {
        clan.setAirship(null);
        clan.setAirshipFuel(_currentFuel);
        clan.updateClanInDB();

        IdFactory.getInstance().releaseId(controlKey.objectId());
        BoatHolder.getInstance().removeBoat(this);

        super.onDelete();
    }

    @Override
    public Location getReturnLoc() {
        return platform == null ? null : platform.getOustLoc();
    }

    public Clan getClan() {
        return clan;
    }

    public void setPlatform(AirshipDock.AirshipPlatform platformId) {
        platform = platformId;
    }

    public AirshipDock getDock() {
        return dock;
    }

    public void setDock(AirshipDock dock) {
        this.dock = dock;
    }

    public boolean isCustomMove() {
        return customMove;
    }

    @Override
    public boolean isDocked() {
        return dock != null && !isMoving;
    }


    @Override
    public List<L2GameServerPacket> deletePacketList() {
        List<L2GameServerPacket> list = new ArrayList<>(2);
        list.add(new DeleteObject(controlKey));
        list.add(new DeleteObject(this));
        return list;
    }

    private class FuelAndDeleteTask extends RunnableImpl {
        @Override
        public void runImpl() {
            if (players.stream().noneMatch(Player::isOnline))
                deleteMe();
            else
                setCurrentFuel(getCurrentFuel() - 10);
        }
    }
}
