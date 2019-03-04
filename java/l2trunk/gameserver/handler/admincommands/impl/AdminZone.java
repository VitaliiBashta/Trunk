package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.instancemanager.MapRegionHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.templates.mapregion.DomainArea;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class AdminZone implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (activeChar == null || !activeChar.getPlayerAccess().CanTeleport)
            return false;

        switch (comm) {
            case "admin_zone_check": {
                activeChar.sendMessage("Current region: " + activeChar.getCurrentRegion());
                activeChar.sendMessage("Zone list:");
                List<Zone> zones = new ArrayList<>();
                World.getZones(zones, activeChar.getLoc(), activeChar.getReflection());
                for (Zone zone : zones)
                    activeChar.sendMessage(zone.getType().toString() + ", name: " + zone.getName() + ", state: " + (zone.isActive() ? "active" : "not active") + ", inside: " + zone.checkIfInZone(activeChar) + "/" + zone.checkIfInZone(activeChar.getX(), activeChar.getY(), activeChar.getZ()));

                break;
            }
            case "admin_region": {
                activeChar.sendMessage("Current region: " + activeChar.getCurrentRegion());
                activeChar.sendMessage("Objects list:");
                activeChar.getCurrentRegion().getObjects().stream()
                        .filter(Objects::nonNull)
                        .forEach(o -> activeChar.sendMessage(o.toString()));
                break;
            }
            case "admin_vis_count": {
                activeChar.sendMessage("Current region: " + activeChar.getCurrentRegion());
                activeChar.sendMessage("Players count: " + World.getAroundPlayers(activeChar).count());
                break;
            }
            case "admin_pos": {
                String pos = activeChar.getX() + ", " + activeChar.getY() + ", " + activeChar.getZ() + ", " + activeChar.getHeading() + " Geo [" + (activeChar.getX() - World.MAP_MIN_X >> 4) + ", " + (activeChar.getY() - World.MAP_MIN_Y >> 4) + "] Ref " + activeChar.getReflectionId();
                activeChar.sendMessage("Pos: " + pos);
                break;
            }
            case "admin_domain": {
                DomainArea domain = MapRegionHolder.getInstance().getRegionData(DomainArea.class, activeChar);
                Castle castle = domain != null ? ResidenceHolder.getCastle(domain.getId()) : null;
                if (castle != null)
                    activeChar.sendMessage("Domain: " + castle.getName());
                else
                    activeChar.sendMessage("Domain: Unknown");
            }
        }
        return true;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_zone_check",
                "admin_region",
                "admin_pos",
                "admin_vis_count",
                "admin_domain");
    }
}