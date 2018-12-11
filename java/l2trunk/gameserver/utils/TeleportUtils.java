package l2trunk.gameserver.utils;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.instancemanager.MapRegionHolder;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.RestartType;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.templates.mapregion.RestartArea;
import l2trunk.gameserver.templates.mapregion.RestartPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TeleportUtils {
    private final static Location DEFAULT_RESTART = new Location(17817, 170079, -3530);
    private static final Logger _log = LoggerFactory.getLogger(TeleportUtils.class);

    private TeleportUtils() {
    }

    public static Location getRestartLocation(Player player, RestartType restartType) {
        return getRestartLocation(player, player.getLoc(), restartType);
    }

    private static Location getRestartLocation(Player player, Location from, RestartType restartType) {
        Reflection r = player.getReflection();
        if (r != ReflectionManager.DEFAULT)
            if (r.getCoreLoc() != null)
                return r.getCoreLoc();
            else if (r.getReturnLoc() != null)
                return r.getReturnLoc();

        Clan clan = player.getClan();

        if (clan != null) {
            // If teleport to clan hall
            if (restartType == RestartType.TO_CLANHALL && clan.getHasHideout() != 0)
                return ResidenceHolder.getResidence(clan.getHasHideout()).getOwnerRestartPoint();

            // If teleport to castle
            if (restartType == RestartType.TO_CASTLE && clan.getCastle() != 0)
                return ResidenceHolder.getResidence(clan.getCastle()).getOwnerRestartPoint();

            // If teleport to fortress
            if (restartType == RestartType.TO_FORTRESS && clan.getHasFortress() != 0)
                return ResidenceHolder.getResidence(clan.getHasFortress()).getOwnerRestartPoint();
        }

        if (player.getKarma() > 1) {
            if (player.getPKRestartPoint() != null)
                return player.getPKRestartPoint();
        } else {
            if (player.getRestartPoint() != null)
                return player.getRestartPoint();
        }

        RestartArea ra = MapRegionHolder.getInstance().getRegionData(RestartArea.class, from);
        if (ra != null) {
            RestartPoint rp = ra.getRestartPoint().get(player.getRace());

            Location restartPoint = Rnd.get(rp.getRestartPoints());
            Location PKrestartPoint = Rnd.get(rp.getPKrestartPoints());

            return player.getKarma() > 1 ? PKrestartPoint : restartPoint;
        }

        _log.warn("Cannot find restart location from coordinates: " + from + "!");

        return DEFAULT_RESTART;
    }
}