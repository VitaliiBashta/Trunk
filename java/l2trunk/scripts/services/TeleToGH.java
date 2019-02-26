package l2trunk.scripts.services;

import l2trunk.gameserver.Config;
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

import java.util.ArrayList;
import java.util.List;

public final class TeleToGH extends Functions implements ScriptFile {
    private static final List<SimpleSpawner> _spawns = new ArrayList<>();

    private final Zone _zone = ReflectionUtils.getZone("[giran_harbor_offshore]");
    private static final String en = "<br>[scripts_services.TeleToGH:toGH @811;Giran Harbor|\"I want free admission to Giran Harbor.\"]";
    private static final String en2 = "<br>[scripts_services.ManaRegen:DoManaRegen|Full MP Regeneration. (1 MP for 5 Adena)]<br>[scripts_services.TeleToGH:fromGH @811;From Giran Harbor|\"Exit the Giran Harbor.\"]<br>";
    private static ZoneListener _zoneListener;

    @Override
    public void onLoad() {
        if (!Config.SERVICES_GIRAN_HARBOR_ENABLED)
            return;

        ReflectionManager.GIRAN_HARBOR.setCoreLoc( Location.of(47416, 186568, -3480));

        // spawn wh keeper
        SimpleSpawner spawn = new SimpleSpawner(30086);
        spawn.setLoc(new Location(48059, 186791, -3512, 42000))
                .setAmount(1)
                .setRespawnDelay(5)
                .setReflection(ReflectionManager.GIRAN_HARBOR);
        spawn.init();
        _spawns.add(spawn);

        // spawn grocery trader
        spawn = new SimpleSpawner(32169);
        spawn.setLoc(new Location(48146, 186753, -3512, 42000))
                .setAmount(1)
                .setRespawnDelay(5)
                .setReflection(ReflectionManager.GIRAN_HARBOR)
                .init();
        _spawns.add(spawn);

        // spawn gk
        spawn = new SimpleSpawner(13129);
        spawn.setLoc(new Location(47984, 186832, -3445, 42000))
                .setAmount(1)
                .setRespawnDelay(5)
                .setReflection(ReflectionManager.GIRAN_HARBOR)
                .init();
        _spawns.add(spawn);

        // spawn Orion the Cat
        spawn = new SimpleSpawner(31860);
        spawn.setLoc(new Location(48129, 186828, -3512, 45452))
                .setAmount(1)
                .setRespawnDelay(5)
                .setReflection(ReflectionManager.GIRAN_HARBOR)
                .init();
        _spawns.add(spawn);

        // spawn blacksmith (Pushkin)
        spawn = new SimpleSpawner(30300);
        spawn.setLoc(new Location(48102, 186772, -3512, 42000))
                .setAmount(1)
                .setRespawnDelay(5)
                .setReflection(ReflectionManager.GIRAN_HARBOR)
                .init();
        _spawns.add(spawn);

        // spawn Item Broker
        spawn = new SimpleSpawner(32320);
        spawn.setLoc(new Location(47772, 186905, -3480, 42000))
                .setAmount(1)
                .setRespawnDelay(5)
                .setReflection(ReflectionManager.GIRAN_HARBOR)
                .init();
        _spawns.add(spawn);

        // spawn Item Broker
        spawn = new SimpleSpawner(32320);
        spawn.setLoc(new Location(46360, 187672, -3480, 42000))
                .setAmount(1)
                .setRespawnDelay(5)
                .setReflection(ReflectionManager.GIRAN_HARBOR)
                .init();
        _spawns.add(spawn);

        // spawn Item Broker
        spawn = new SimpleSpawner(32320);
        spawn.setLoc(new Location(49016, 185960, -3480, 42000))
                .setAmount(1)
                .setRespawnDelay(5)
                .setReflection(ReflectionManager.GIRAN_HARBOR)
                .init();
        _spawns.add(spawn);

        _zoneListener = new ZoneListener();
        _zone.addListener(_zoneListener);
        _zone.setReflection(ReflectionManager.GIRAN_HARBOR);
        _zone.setActive(true);
        Zone zone = ReflectionUtils.getZone("[giran_harbor_peace_alt]");
        zone.setReflection(ReflectionManager.GIRAN_HARBOR);
        zone.setActive(true);
        zone = ReflectionUtils.getZone("[giran_harbor_no_trade]");
        zone.setReflection(ReflectionManager.GIRAN_HARBOR);
        zone.setActive(true);
    }

    @Override
    public void onReload() {
        _zone.removeListener(_zoneListener);
        for (SimpleSpawner spawn : _spawns)
            spawn.deleteAll();
        _spawns.clear();
    }

    public void toGH() {
        if (npc == null || !npc.isInRange(player, 1000L))
            return;

        if (!NpcInstance.canBypassCheck(player, npc))
            return;

        player.setVar("backCoords", player.getLoc().toXYZString());
        player.teleToLocation(Location.findPointToStay(_zone.getSpawn(), 30, 200, ReflectionManager.GIRAN_HARBOR.getGeoIndex()), ReflectionManager.GIRAN_HARBOR);
    }

    public void fromGH() {
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
        if (val != 0 || !Config.SERVICES_GIRAN_HARBOR_ENABLED)
            return "";
        if (player == null)
            return "";
        return en;
    }

    public String DialogAppend_13129(Integer val) {
        if (val != 0 || !Config.SERVICES_GIRAN_HARBOR_ENABLED)
            return "";
        if (player == null || player.getReflectionId() != -2)
            return "";
        return en2;
    }

    public class ZoneListener implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Player cha) {
            // обрабатывать вход в зону не надо, только выход
        }

        @Override
        public void onZoneLeave(Zone zone, Player player) {
                if (Config.SERVICES_GIRAN_HARBOR_ENABLED && player.getReflection() == ReflectionManager.GIRAN_HARBOR && player.isVisible()) {
                    double angle = PositionUtils.convertHeadingToDegree(player.getHeading()); // угол в градусах
                    double radian = Math.toRadians(angle - 90); // угол в радианах
                    player.teleToLocation((int) (player.getX() + 50 * Math.sin(radian)), (int) (player.getY() - 50 * Math.cos(radian)), player.getZ());
                }
        }
    }
}