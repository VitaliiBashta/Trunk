package l2trunk.gameserver.model.entity.boat;

import l2trunk.gameserver.ai.BoatAI;
import l2trunk.gameserver.ai.CharacterAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.entity.events.impl.BoatWayEvent;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.ActionFail;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.CharTemplate;
import l2trunk.gameserver.templates.item.WeaponTemplate;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.PositionUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class Boat extends Creature {
    final Set<Player> players = new CopyOnWriteArraySet<>();
    private final BoatWayEvent[] _ways = new BoatWayEvent[2];
    int _fromHome;
    int runState;
    private int _moveSpeed; //speed 1
    private int rotationSpeed; //speed 2

    Boat(int objectId, CharTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onSpawn() {
        _fromHome = 1;

        getCurrentWay().reCalcNextTime(false);
    }

    @Override
    public void setXYZ(int x, int y, int z, boolean MoveTask) {
        super.setXYZ(x, y, z, MoveTask);

        updatePeopleInTheBoat(new Location(x, y, z));
    }

    public void onEvtArrived() {
        getCurrentWay().moveNext();
    }

    void updatePeopleInTheBoat(Location loc) {
        players.stream()
                .filter(Objects::nonNull)
                .forEach(p -> p.setLoc(loc, true));

    }

    public void addPlayer(Player player, Location boatLoc) {
        synchronized (players) {
            players.add(player);

            player.setBoat(this);
            player.setLoc(getLoc(), true);
            player.setInBoatPosition(boatLoc);
            player.broadcastPacket(getOnPacket(player, boatLoc));
        }
    }

    public void moveInBoat(Player player, Location ori, Location loc) {
        if (player.getPet() != null) {
            player.sendPacket(SystemMsg.YOU_SHOULD_RELEASE_YOUR_PET_OR_SERVITOR_SO_THAT_IT_DOES_NOT_FALL_OFF_OF_THE_BOAT_AND_DROWN, ActionFail.STATIC);
            return;
        }

        if (player.getTransformation() != 0) {
            player.sendPacket(SystemMsg.YOU_CANNOT_BOARD_A_SHIP_WHILE_YOU_ARE_POLYMORPHED, ActionFail.STATIC);
            return;
        }

        if (player.isMovementDisabled() || player.isSitting()) {
            player.sendActionFailed();
            return;
        }

        if (!player.isInBoat())
            player.setBoat(this);

        loc.h = PositionUtils.getHeadingTo(ori, loc);
        player.setInBoatPosition(loc);
        player.broadcastPacket(inMovePacket(player, ori, loc));
    }

    public void trajetEnded(boolean oust) {
        runState = 0;
        _fromHome = _fromHome == 1 ? 0 : 1;

        L2GameServerPacket checkLocation = checkLocationPacket();
        if (checkLocation != null)
            broadcastPacket(infoPacket(), checkLocation);

        if (oust) {
            oustPlayers();
            getCurrentWay().reCalcNextTime(false);
        }
    }

    public void teleportShip(Location loc) {
        if (isMoving)
            stopMove(false);

        players.forEach(p -> p.teleToLocation(loc));

        setHeading(calcHeading(loc.x, loc.y));

        setLoc(loc, true);

        getCurrentWay().moveNext();
    }

    public void oustPlayer(Player player, Location loc, boolean teleport) {
        synchronized (players) {
            player.stablePoint = null;

            player.setBoat(null);
            player.setInBoatPosition(null);
            player.broadcastPacket(getOffPacket(player, loc));

            if (teleport)
                player.teleToLocation(loc);

            players.remove(player);
        }
    }

    public void removePlayer(Player player) {
        synchronized (players) {
            players.remove(player);
        }
    }

    void broadcastPacketToPassengers(IStaticPacket packet) {
        players.forEach(p -> p.sendPacket(packet));
    }

    //=========================================================================================================
    protected abstract L2GameServerPacket infoPacket();

    public abstract L2GameServerPacket movePacket();

    protected abstract L2GameServerPacket inMovePacket(Player player, Location src, Location desc);

    public abstract L2GameServerPacket stopMovePacket();

    public abstract L2GameServerPacket inStopMovePacket(Player player);

    public abstract L2GameServerPacket startPacket();

    public abstract L2GameServerPacket validateLocationPacket(Player player);

    protected abstract L2GameServerPacket checkLocationPacket();

    public abstract L2GameServerPacket getOnPacket(Player player, Location location);

    protected abstract L2GameServerPacket getOffPacket(Player player, Location location);

    protected abstract void oustPlayers();

    //=========================================================================================================
    @Override
    public CharacterAI getAI() {
        if (super.getAI() == null)
            super.setAI(new BoatAI(this));
        return super.getAI();
    }

    @Override
    public void broadcastCharInfo() {
        broadcastPacket(infoPacket());
    }

    @Override
    public void broadcastPacket(L2GameServerPacket... packets) {
        this.players.forEach(p -> p.sendPacket(packets));
        World.getAroundPlayers(this)
                .forEach(p -> p.sendPacket(packets));

    }

    @Override
    public void validateLocation(int broadcast) {
    }

    @Override
    public void sendChanges() {
    }

    @Override
    public int getMoveSpeed() {
        return _moveSpeed;
    }

    public void setMoveSpeed(int moveSpeed) {
        _moveSpeed = moveSpeed;
    }

    @Override
    public int getRunSpeed() {
        return _moveSpeed;
    }

    @Override
    public ItemInstance getActiveWeaponInstance() {
        return null;
    }

    @Override
    public WeaponTemplate getActiveWeaponItem() {
        return null;
    }

    @Override
    public ItemInstance getSecondaryWeaponInstance() {
        return null;
    }

    @Override
    public WeaponTemplate getSecondaryWeaponItem() {
        return null;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return false;
    }

    //=========================================================================================================
    public int getRunState() {
        return runState;
    }

    public void setRunState(int runState) {
        this.runState = runState;
    }

    public int getRotationSpeed() {
        return rotationSpeed;
    }

    public void setRotationSpeed(int rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public BoatWayEvent getCurrentWay() {
        return _ways[_fromHome];
    }

    public void setWay(int id, BoatWayEvent v) {
        _ways[id] = v;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public boolean isDocked() {
        return runState == 0;
    }

    public Location getReturnLoc() {
        return getCurrentWay().getReturnLoc();
    }

    @Override
    public boolean isBoat() {
        return true;
    }

    @Override
    public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
        if (!isMoving) {
            return Collections.singletonList(infoPacket());
        } else {
            List<L2GameServerPacket> list = new ArrayList<>(2);
            list.add(infoPacket());
            list.add(movePacket());
            return list;
        }
    }
}
