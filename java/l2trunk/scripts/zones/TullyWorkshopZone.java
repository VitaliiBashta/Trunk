package l2trunk.scripts.zones;

import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.List;

public final class TullyWorkshopZone implements ScriptFile {
    private static final List<String> zones = List.of(
            "[tully1]",
            "[tully2]",
            "[tully3]",
            "[tully4]");

    @Override
    public void onLoad() {
        ZoneListener _zoneListener = new ZoneListener();
        zones.forEach(s -> ReflectionUtils.getZone(s).addListener(_zoneListener));
    }

    public class ZoneListener implements OnZoneEnterLeaveListener {
        final Location TullyFloor2LocationPoint = new Location(-14180, 273060, -13600);
        final Location TullyFloor3LocationPoint = new Location(-13361, 272107, -11936);
        final Location TullyFloor4LocationPoint = new Location(-14238, 273002, -10496);
        final Location TullyFloor5LocationPoint = new Location(-10952, 272536, -9062);

        @Override
        public void onZoneEnter(Zone zone, Creature cha) {
            Player player = cha.getPlayer();
            if (player == null)
                return;
            if (zone.isActive()) {
                if (zone.getName().equalsIgnoreCase("[tully1]"))
                    player.teleToLocation(TullyFloor2LocationPoint);
                else if (zone.getName().equalsIgnoreCase("[tully2]"))
                    player.teleToLocation(TullyFloor4LocationPoint);
                else if (zone.getName().equalsIgnoreCase("[tully3]"))
                    player.teleToLocation(TullyFloor3LocationPoint);
                else if (zone.getName().equalsIgnoreCase("[tully4]"))
                    player.teleToLocation(TullyFloor5LocationPoint);

            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature cha) {
        }
    }
}
