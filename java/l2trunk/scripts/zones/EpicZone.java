package l2trunk.scripts.zones;

import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;

public final class EpicZone implements ScriptFile {
    private static ZoneListener _zoneListener= new ZoneListener();

    @Override
    public void onLoad() {
        Zone zone1 = ReflectionUtils.getZone("[fix_exploit_beleth]");
        zone1.addListener(_zoneListener);

        Zone zone2 = ReflectionUtils.getZone("[fix_exploit_beleth_2]");
        zone2.addListener(_zoneListener);
    }

    public static class ZoneListener implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Player player) {
            if (zone.getParams() == null || player.isGM())
                return;
            // Synerge - Added protection to only allow x max class occupation to certain zones if set. It also checks if getPlayer has subclasses, that should be the same as having 3rd class
            final int maxClassLvl = zone.getParams().getInteger("maxClassLevelAllowed", -1);
            if (player.getLevel() > zone.getParams().getInteger("levelLimit")
                    || (maxClassLvl >= 0 && player.getClassId().occupation() > maxClassLvl-1)
                    || (maxClassLvl >= 0 && player.isSubClassActive())) {
                player.sendMessage(new CustomMessage("scripts.zones.epic.banishMsg"));
                player.teleToLocation(Location.of(zone.getParams().getString("tele")));
            }
        }

    }
}

