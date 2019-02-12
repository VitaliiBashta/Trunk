package l2trunk.scripts.services;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.PositionUtils;
import l2trunk.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public final class TeleToParnassus extends Functions implements ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(TeleToParnassus.class);
    private static final List<SimpleSpawner> _spawns = new ArrayList<>();
    private final Zone _zone = ReflectionUtils.getZone("[parnassus_offshore]");
    private static final String en = "<br>[scripts_services.TeleToParnassus:toParnassus @811;Parnassus|\"Move to Parnassus (offshore zone) - " + Config.SERVICES_PARNASSUS_PRICE + " Adena.\"]";
    private static final String en2 = "<br>[scripts_services.ManaRegen:DoManaRegen|Full MP Regeneration. (1 MP for 5 Adena)]<br>[scripts_services.TeleToParnassus:fromParnassus @811;From Parnassus|\"Exit the Parnassus.\"]<br>";
    private static ZoneListener _zoneListener;

    @Override
    public void onLoad() {
        if (!Config.SERVICES_PARNASSUS_ENABLED)
            return;

        ReflectionManager.PARNASSUS.setCoreLoc(new Location(149384, 171896, -952));

        // spawn wh keeper
        SimpleSpawner spawn = new SimpleSpawner(30086);
        spawn.setLoc(new Location(149960, 174136, -920, 32768))
                .setAmount(1)
                .setRespawnDelay(5)
                .setReflection(ReflectionManager.PARNASSUS)
                .init();
        _spawns.add(spawn);

        // spawn grocery trader (Helvetia)
        spawn = new SimpleSpawner(30839);
        spawn.setLoc(new Location(149368, 174264, -896, 49152))
                .setAmount(1)
                .setRespawnDelay(5)
                .setReflection(ReflectionManager.PARNASSUS)
                .init();
        _spawns.add(spawn);

        // spawn gk
        spawn = new SimpleSpawner(13129);
        spawn.setLoc(new Location(149368, 172568, -952, 49152))
                .setAmount(1)
                .setRespawnDelay(5)
                .setReflection(ReflectionManager.PARNASSUS)
                .init();
        _spawns.add(spawn);

        // spawn Orion the Cat
        spawn = new SimpleSpawner(31860);
        spawn.setLoc(new Location(148904, 173656, -952, 49152))
                .setAmount(1)
                .setRespawnDelay(5)
                .setReflection(ReflectionManager.PARNASSUS)
                .init();
        _spawns.add(spawn);

        // spawn blacksmith (Pushkin)
        spawn = new SimpleSpawner(30300);
        spawn.setLoc(new Location(148760, 174136, -920, 0))
                .setAmount(1)
                .setRespawnDelay(5)
                .setReflection(ReflectionManager.PARNASSUS)
                .init();
        _spawns.add(spawn);

        // spawn Item Broker
        spawn = new SimpleSpawner(32320);
        spawn.setLoc(new Location(149368, 173064, -952, 16384))
                .setAmount(1)
                .setRespawnDelay(5)
                .setReflection(ReflectionManager.PARNASSUS)
                .init();
        _spawns.add(spawn);

        _zoneListener = new ZoneListener();
        _zone.addListener(_zoneListener);
        _zone.setReflection(ReflectionManager.PARNASSUS);
        _zone.setActive(true);
        Zone zone = ReflectionUtils.getZone("[parnassus_peace]");
        zone.setReflection(ReflectionManager.PARNASSUS);
        zone.setActive(true);
        zone = ReflectionUtils.getZone("[parnassus_no_trade]");
        zone.setReflection(ReflectionManager.PARNASSUS);
        zone.setActive(true);

        _log.info("Loaded Service: teleport to Parnassus");
    }

    @Override
    public void onReload() {
        _zone.removeListener(_zoneListener);
        for (SimpleSpawner spawn : _spawns)
            spawn.deleteAll();
        _spawns.clear();
    }

    public void toParnassus() {
        if (npc == null || !npc.isInRange(player, 1000L))
            return;

        if (!NpcInstance.canBypassCheck(player, npc))
            return;

        if (player.getAdena() < Config.SERVICES_PARNASSUS_PRICE) {
            player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }

        player.reduceAdena(Config.SERVICES_PARNASSUS_PRICE, true, "TeleToParnassus");
        player.setVar("backCoords", player.getLoc().toXYZString());
        player.teleToLocation(Location.findPointToStay(_zone.getSpawn(), 30, 200, ReflectionManager.PARNASSUS.getGeoIndex()), ReflectionManager.PARNASSUS);
    }

    public void fromParnassus() {
        if (npc == null || !npc.isInRange(player, 1000L))
            return;

        if (!NpcInstance.canBypassCheck(player, npc))
            return;

        String var = player.getVar("backCoords");
        if (var == null || var.equals("")) {
            teleOut();
            return;
        }
        player.teleToLocation(Location.of(var), 0);
    }

    private void teleOut() {
        if (npc == null || !npc.isInRange(player, 1000L))
            return;
        player.teleToLocation(new Location(46776, 185784, -3528), 0);
        show("I don't know from where you came here, but I can teleport you the another border side.", player, npc);
    }

    public String DialogAppend_30059(Integer val) {
        return getHtmlAppends(val);
    }

    public String DialogAppend_30080(Integer val) {
        return getHtmlAppends(val);
    }

    public String DialogAppend_30177(Integer val) {
        return getHtmlAppends(val);
    }

    public String DialogAppend_30233(Integer val) {
        return getHtmlAppends(val);
    }

    public String DialogAppend_30256(Integer val) {
        return getHtmlAppends(val);
    }

    public String DialogAppend_30320(Integer val) {
        return getHtmlAppends(val);
    }

    public String DialogAppend_30848(Integer val) {
        return getHtmlAppends(val);
    }

    public String DialogAppend_30878(Integer val) {
        return getHtmlAppends(val);
    }

    public String DialogAppend_30899(Integer val) {
        return getHtmlAppends(val);
    }

    public String DialogAppend_31210(Integer val) {
        return getHtmlAppends(val);
    }

    public String DialogAppend_31275(Integer val) {
        return getHtmlAppends(val);
    }

    public String DialogAppend_31320(Integer val) {
        return getHtmlAppends(val);
    }

    public String DialogAppend_31964(Integer val) {
        return getHtmlAppends(val);
    }

    public String DialogAppend_30006(Integer val) {
        return getHtmlAppends(val);
    }

    public String DialogAppend_30134(Integer val) {
        return getHtmlAppends(val);
    }

    public String DialogAppend_30146(Integer val) {
        return getHtmlAppends(val);
    }

    public String DialogAppend_32163(Integer val) {
        return getHtmlAppends(val);
    }

    public String DialogAppend_30576(Integer val) {
        return getHtmlAppends(val);
    }

    public String DialogAppend_30540(Integer val) {
        return getHtmlAppends(val);
    }

    private String getHtmlAppends(Integer val) {
        if (val != 0 || !Config.SERVICES_PARNASSUS_ENABLED)
            return "";
        if (player == null)
            return "";
        return en;
    }

    public String DialogAppend_13129(Integer val) {
        return getHtmlAppends2(val);
    }

    private String getHtmlAppends2(Integer val) {
        if (val != 0 || !Config.SERVICES_PARNASSUS_ENABLED)
            return "";
        if (player == null || player.getReflection() != ReflectionManager.PARNASSUS)
            return "";
        return en2;
    }

    public class ZoneListener implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Player cha) {
        }

        @Override
        public void onZoneLeave(Zone zone, Player player) {
                if (Config.SERVICES_PARNASSUS_ENABLED && player.getReflection() == ReflectionManager.PARNASSUS && player.isVisible()) {
                    double angle = PositionUtils.convertHeadingToDegree(player.getHeading());
                    double radian = Math.toRadians(angle - 90);
                    player.teleToLocation((int) (player.getX() + 50 * Math.sin(radian)), (int) (player.getY() - 50 * Math.cos(radian)), player.getZ());
                }
        }
    }
}