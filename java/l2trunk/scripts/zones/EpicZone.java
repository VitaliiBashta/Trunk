package l2trunk.scripts.zones;

import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.Creature;
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
        public void onZoneEnter(Zone zone, Creature cha) {
            if (zone.getParams() == null || !cha.isPlayable() || cha.getPlayer().isGM())
                return;
            // Synerge - Added protection to only allow x max class level to certain zones if set. It also checks if player has subclasses, that should be the same as having 3rd class
            final int maxClassLvl = zone.getParams().getInteger("maxClassLevelAllowed", -1);
            if (cha.getLevel() > zone.getParams().getInteger("levelLimit")
                    || (maxClassLvl >= 0 && cha.getPlayer().getClassId().getLevel() > maxClassLvl)
                    || (maxClassLvl >= 0 && cha.getPlayer().isSubClassActive())) {
                cha.getPlayer().sendMessage(new CustomMessage("scripts.zones.epic.banishMsg", cha.getPlayer()));
                cha.teleToLocation(Location.parseLoc(zone.getParams().getString("tele")));
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature cha) {
        }
    }
}

