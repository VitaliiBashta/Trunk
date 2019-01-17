package l2trunk.gameserver.model.entity.events.impl;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.BoatHolder;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.entity.boat.Boat;
import l2trunk.gameserver.model.entity.boat.ClanAirShip;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.entity.events.objects.BoatPoint;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.MapUtils;

import java.util.List;
import java.util.stream.Stream;

public final class BoatWayEvent extends GlobalEvent {
    public static final String BOAT_POINTS = "boat_points";

    private final int _ticketId;
    private final Location _returnLoc;
    private final Boat _boat;

    public BoatWayEvent(ClanAirShip boat) {
        super(boat.getObjectId(), "ClanAirShip");
        _ticketId = 0;
        _boat = boat;
        _returnLoc = null;
    }

    public BoatWayEvent(StatsSet set) {
        super(set);
        _ticketId = set.getInteger("ticketId", 0);
        _returnLoc = Location.parseLoc(set.getString("return_point"));
        String className = set.getString("class", null);
        if (className != null) {
            _boat = BoatHolder.getInstance().initBoat(getName(), className);
            Location loc = Location.parseLoc(set.getString("spawn_point"));
            _boat.setLoc(loc, true);
            _boat.setHeading(loc.h);
        } else {
            _boat = BoatHolder.getInstance().getBoat(getName());
        }
        _boat.setWay(className != null ? 1 : 0, this);
    }

    @Override
    public void initEvent() {
    }

    @Override
    public void startEvent() {
        L2GameServerPacket startPacket = _boat.startPacket();
        for (Player player : _boat.getPlayers()) {
            if (_ticketId > 0) {
                if (player.consumeItem(_ticketId, 1)) {
                    if (startPacket != null)
                        player.sendPacket(startPacket);
                } else {
                    player.sendPacket(SystemMsg.YOU_DO_NOT_POSSESS_THE_CORRECT_TICKET_TO_BOARD_THE_BOAT);
                    _boat.oustPlayer(player, _returnLoc, true);
                }
            } else {
                if (startPacket != null)
                    player.sendPacket(startPacket);
            }
        }

        moveNext();
    }

    public void moveNext() {
        List<BoatPoint> points = getObjects(BOAT_POINTS);

        if (_boat.getRunState() >= points.size()) {
            _boat.trajetEnded(true);
            return;
        }

        final BoatPoint bp = points.get(_boat.getRunState());

        if (bp.getSpeed1() >= 0)
            _boat.setMoveSpeed(bp.getSpeed1());
        if (bp.getSpeed2() >= 0)
            _boat.setRotationSpeed(bp.getSpeed2());

        if (_boat.getRunState() == 0)
            _boat.broadcastCharInfo();

        _boat.setRunState(_boat.getRunState() + 1);

        if (bp.isTeleport())
            _boat.teleportShip(bp);
        else
            _boat.moveToLocation(bp.getX(), bp.getY(), bp.getZ(), 0, false);
    }

    @Override
    public void reCalcNextTime(boolean onInit) {
        registerActions();
    }

    @Override
    protected long startTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public Stream<Player> broadcastPlayers(int range) {
        if (range <= 0) {
            int rx = MapUtils.regionX(_boat.getX());
            int ry = MapUtils.regionY(_boat.getY());
            int offset = Config.SHOUT_OFFSET;

            return GameObjectsStorage.getAllPlayersStream()
                    .filter(p -> p.getReflection() == _boat.getReflection())
                    .filter(p -> (MapUtils.regionX(p) >= rx - offset && MapUtils.regionX(p) <= rx + offset && MapUtils.regionY(p) >= ry - offset && MapUtils.regionY(p) <= ry + offset));
        } else
            return World.getAroundPlayers(_boat, range, Math.max(range / 2, 200));
    }

    @Override
    protected void printInfo() {
    }

    public Location getReturnLoc() {
        return _returnLoc;
    }
}
