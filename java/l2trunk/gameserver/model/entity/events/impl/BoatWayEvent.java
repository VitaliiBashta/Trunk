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
    private final Boat boat;

    public BoatWayEvent(ClanAirShip boat) {
        super(boat.objectId(), "ClanAirShip");
        _ticketId = 0;
        this.boat = boat;
        _returnLoc = null;
    }

    public BoatWayEvent(StatsSet set) {
        super(set);
        _ticketId = set.getInteger("ticketId");
        _returnLoc = Location.of(set.getString("return_point"));
        String className = set.getString("class");
        if (className != null) {
            boat = BoatHolder.getInstance().initBoat(getName(), className);
            Location loc = Location.of(set.getString("spawn_point"));
            boat.setLoc(loc, true);
            boat.setHeading(loc.h);
        } else {
            boat = BoatHolder.getInstance().getBoat(getName());
        }
        boat.setWay(className != null ? 1 : 0, this);
    }

    @Override
    public void initEvent() {
    }

    @Override
    public void startEvent() {
        L2GameServerPacket startPacket = boat.startPacket();
        for (Player player : boat.getPlayers()) {
            if (_ticketId > 0) {
                if (player.consumeItem(_ticketId, 1)) {
                    if (startPacket != null)
                        player.sendPacket(startPacket);
                } else {
                    player.sendPacket(SystemMsg.YOU_DO_NOT_POSSESS_THE_CORRECT_TICKET_TO_BOARD_THE_BOAT);
                    boat.oustPlayer(player, _returnLoc, true);
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

        if (boat.getRunState() >= points.size()) {
            boat.trajetEnded(true);
            return;
        }

        final BoatPoint bp = points.get(boat.getRunState());

        if (bp.getSpeed1() >= 0)
            boat.setMoveSpeed(bp.getSpeed1());
        if (bp.getSpeed2() >= 0)
            boat.setRotationSpeed(bp.getSpeed2());

        if (boat.getRunState() == 0)
            boat.broadcastCharInfo();

        boat.setRunState(boat.getRunState() + 1);

        if (bp.isTeleport())
            boat.teleportShip(bp);
        else
            boat.moveToLocation(bp, 0, false);
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
            int rx = MapUtils.regionX(boat);
            int ry = MapUtils.regionY(boat);
            int offset = Config.SHOUT_OFFSET;

            return GameObjectsStorage.getAllPlayersStream()
                    .filter(p -> p.getReflection() == boat.getReflection())
                    .filter(p -> (MapUtils.regionX(p) >= rx - offset && MapUtils.regionX(p) <= rx + offset && MapUtils.regionY(p) >= ry - offset && MapUtils.regionY(p) <= ry + offset));
        } else
            return World.getAroundPlayers(boat, range, Math.max(range / 2, 200));
    }

    @Override
    protected void printInfo() {
    }

    public Location getReturnLoc() {
        return _returnLoc;
    }
}
