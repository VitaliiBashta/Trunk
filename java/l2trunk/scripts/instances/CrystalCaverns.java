package l2trunk.scripts.instances;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public final class CrystalCaverns extends Reflection {
    private static final int gatekeeper_provo = 22277;
    private static final int gatekeeper_lohan = 22275;

    private static final List<Integer> cor_gar_monsters = List.of(22312, 22313, 22314, 22315, 22316, 22317, 22311);
    private static final List<Integer> cor_gar_st_room_monsters = List.of(22305, 22418, 22419, 22306, 22420, 22416, 22306, 22307);
    private static final List<Integer> cor_gar_emerald_sq_monsters = List.of(22286, 22287, 22288, 22289, 22292, 22293, 22294, 22280, 22281, 22283);
    private static final List<Integer> cor_gar_emerald_sq_reef_golems = List.of(22295, 22296);
    private static final List<Integer> cor_gar_emerald_sq_callers = List.of(22301, 22299);
    private static final List<Integer> cor_gar_emerald_sq_guardians = List.of(22298, 22303, 22302, 22304);

    private static final List<Integer> evas_protectors = List.of(32284, 32285, 32286, 32287);

    private static final int door_cry_cav_corgar_main = 24220024;
    private static final int door_cry_cav_corgar_sec = 24220025;
    private static final int door_cry_cav_corgar_tear = 24220026;
    private static final int door_cry_cav_emer_chrom_door = 24220021;
    private static final int door_cry_cav_steam_c_room4_door = 24220061;
    private static final int door_cry_cav_steam_c_kechi_door = 24220023;
    private static final int door_cry_cav_emerald_darnel = 24220005;
    private static final int door_cry_cav_emerald_darnel_inner = 24220006;

    private final DeathListener deathListener = new DeathListener();
    private final ZoneListener zoneListener = new ZoneListener();
    private final Map<NpcInstance, Integer> protectorsIndex = new HashMap<>();
    private final Map<Integer, List<Location>> protectorsLoc = new HashMap<>();
    private ScheduledFuture<?> failureTimer = null;
    private boolean golemSpawned = false;
    private int golemsTrapped = 0;
    private boolean timerActivated = false;
    private boolean room1ProtectorsSpawned = false;
    private boolean room2ProtectorsSpawned = false;
    private boolean room3ProtectorsSpawned = false;
    private boolean room4DoorOpened = false;
    private boolean kechiAttacked = false;
    private boolean darnelAttacked = false;
    private boolean emeraldHallActivated = false;
    private boolean emeraldWiped = false;
    private boolean emeraldReady = false;

    @Override
    protected void onCreate() {
        super.onCreate();
        spawnByGroup("cry_cav_npc_main");

        List<Location> npc1 = List.of(
                new Location(147088, 152480, -12155, 32768),
                new Location(149776, 152496, -12155, 32768),
                new Location(152448, 152480, -12155, 32768));
        protectorsLoc.put(32284, npc1);

        List<Location> npc2 = List.of(
                new Location(147088, 152560, -12155, 32768),
                new Location(149776, 152576, -12155, 32768),
                new Location(152448, 152560, -12155, 32768));
        protectorsLoc.put(32285, npc2);

        List<Location> npc3 = List.of(
                new Location(147088, 152640, -12155, 32768),
                new Location(149776, 152656, -12155, 32768),
                new Location(152448, 152640, -12155, 32768));
        protectorsLoc.put(32286, npc3);

        List<Location> npc4 = List.of(
                new Location(147088, 152720, -12155, 32768),
                new Location(149776, 152736, -12155, 32768),
                new Location(152448, 152720, -12155, 32768));
        protectorsLoc.put(32287, npc4);
    }

    @Override
    protected void onCollapse() {
        if (failureTimer != null)
            failureTimer.cancel(false);
        super.onCollapse();
    }

    public void notifyCoralRequest() {
        despawnByGroup("cry_cav_npc_main");
        getDoor(door_cry_cav_corgar_main).openMe();
        getDoor(door_cry_cav_corgar_sec).openMe();
        spawnByGroup("cry_cav_corgar_monsters_1");
        spawnByGroup("cry_cav_corgar_monsters_2");
        spawnByGroup("cry_cav_corgar_monsters_3");
        invokeDeathListener();
    }

    public void notifyEmeraldRequest() {
        despawnByGroup("cry_cav_npc_main");
        getDoor(door_cry_cav_emer_chrom_door).openMe();
        spawnByGroup("cry_cav_chrom_hall_mon_1");
        invokeDeathListener();
        getZone("[cry_cav_steam_corr_starter]").addListener(zoneListener);
        getZone("[cry_cav_emerald_starter]").addListener(zoneListener);
    }

    public void notifyGolemTrapped() {
        golemsTrapped++;
        if (golemsTrapped == 2) {
            getDoor(door_cry_cav_corgar_tear).openMe();
            spawnByGroup("cry_cav_corgar_tear");
        }
    }

    public void notifyTearsAttacked() {
        getDoor(door_cry_cav_corgar_tear).closeMe();
    }

    public void notifyTearsDead(NpcInstance npc) {
        setReenterTime(System.currentTimeMillis());
        addSpawnWithoutRespawn(32276, npc.getLoc(), 0);
        npc.getAroundCharacters(600, 300)
        .filter(GameObject::isPlayer)
            .forEach(c ->
                ItemFunctions.addItem(c.getPlayer(), 9697, 1, true, "TearsDead")); //Clear Crystal

        // Synerge - Finish the instance after the boss dies, to avoid exploits
        startCollapseTimer(60000);
    }

    private void notifySteamStarted() {
        if (timerActivated)
            return;
        timerActivated = true;
        getPlayers().forEach(p ->
                p.altOnMagicUseTimer(p, 5239));
        failureTimer = ThreadPoolManager.INSTANCE.schedule(new FailureTimer(), 5 * 60 * 1000L);
        spawnByGroup("cry_cav_steam_room_1");
        invokeDeathListener();
    }

    public void notifyProtectorHealed(NpcInstance npc) {
        Integer idx = protectorsIndex.get(npc);
        if (idx != null)
            protectorsIndex.put(npc, idx + 1);
        else
            protectorsIndex.put(npc, 0);
        if (protectorsIndex.get(npc) > 8) {
            if (npc.isInZone("[cry_cav_steam_corr_room1]"))
                addSpawnWithoutRespawn(32273, npc.getLoc(), 0);
            else if (npc.isInZone("[cry_cav_steam_corr_room2]"))
                addSpawnWithoutRespawn(32274, npc.getLoc(), 0);
            else if (npc.isInZone("[cry_cav_steam_corr_room3]"))
                addSpawnWithoutRespawn(32275, npc.getLoc(), 0);
            protectorsLoc.remove(npc.getNpcId());
            removeProtectors();
            protectorsIndex.clear();
        }
    }

    public void notifyNextLevel(NpcInstance npc) {
        Location loc = null;
        int level = 0, min = 0;
        switch (npc.getNpcId()) {
            case 32273:
                level = 2;
                min = 10;
                loc = new Location(147512, 152616, -12168);
                spawnByGroup("cry_cav_steam_room2_1");
                break;
            case 32274:
                level = 3;
                min = 15;
                loc = new Location(150184, 152632, -12168);
                break;
            case 32275:
                level = 4;
                min = 20;
                loc = new Location(149816, 149976, -12168);
                break;
        }
        invokeDeathListener();
        if (loc == null)
            return;
        if (failureTimer != null) {
            failureTimer.cancel(false);
            failureTimer = ThreadPoolManager.INSTANCE.schedule(new FailureTimer(), min * 60 * 1000L);
        }
        Location l = loc;
        int lvl = level;
        getPlayers().forEach(p -> {
            p.altOnMagicUseTimer(p, 5239, lvl);
            p.teleToLocation(l);
        });
        npc.deleteMe();
    }

    private void removeProtectors() {
        getNpcs().filter(npc -> evas_protectors.contains(npc.getNpcId()))
                .forEach(GameObject::deleteMe);
    }

    public void notifyKechiAttacked() {
        if (!kechiAttacked) {
            kechiAttacked = true;
            getDoor(door_cry_cav_steam_c_room4_door).closeMe();
            getDoor(door_cry_cav_steam_c_kechi_door).closeMe();
        }
    }

    public void notifyKechiDead(NpcInstance npc) {
        setReenterTime(System.currentTimeMillis());
        addSpawnWithoutRespawn(32277, npc.getLoc(), 0);
        npc.getAroundCharacters(600, 300)
                .filter(GameObject::isPlayer)
                .forEach(c -> ItemFunctions.addItem(c.getPlayer(), 9696, 1, true, "KechiDead")); //Red Crystal
        if (failureTimer != null) {
            failureTimer.cancel(false);
            failureTimer = null;
        }

        // Synerge - Finish the instance after the boss dies, to avoid exploits
        startCollapseTimer(60000);
    }

    private void notifyEmeraldStarted() {
        spawnByGroup("cry_cav_emerald_main_monsters");
        invokeDeathListener();
    }

    public void notifyDarnelAttacked() {
        if (!darnelAttacked) {
            darnelAttacked = true;
            getDoor(door_cry_cav_emerald_darnel_inner).closeMe();
            getDoor(door_cry_cav_emerald_darnel).closeMe();
        }
    }

    public void notifyDarnelDead(NpcInstance npc) {
        setReenterTime(System.currentTimeMillis());
        addSpawnWithoutRespawn(32276, npc.getLoc(), 0);
        npc.getAroundCharacters(600, 300)
                .filter(GameObject::isPlayer)
                .forEach(c ->
                        ItemFunctions.addItem(c.getPlayer(), 9695, 1, true, "DarnelDead")); // Blue Crystal

        // Synerge - Finish the instance after the boss dies, to avoid exploits
        startCollapseTimer(60000);
    }

    public boolean areDoorsActivated() {
        return emeraldReady;
    }

    private void invokeDeathListener() {
        getNpcs().forEach(npc -> npc.addListener(deathListener));
    }

    private boolean checkRoomWiped(Zone zone) {
        return zone.getInsideNpcs()
                .filter(GameObject::isMonster)
                .filter(npc -> !npc.isMinion())
                .allMatch(Creature::isDead);
    }

    private class DeathListener implements OnDeathListener {
        @Override
        public void onDeath(Creature self, Creature killer) {
            if (!self.isNpc())
                return;
            Zone square = getZone("[cry_cav_emerald_emerald_square]");
            if (cor_gar_monsters.contains(self.getNpcId())) {
                if (!golemSpawned) {
                    if (getNpcs().filter(GameObject::isMonster)
                            .anyMatch(npc -> !npc.isDead()))
                        return;
                    golemSpawned = true;
                    spawnByGroup("cry_cav_corgar_golem");
                }
            } else if (self.getNpcId() == gatekeeper_provo) {
                if (getAllByNpcId(gatekeeper_lohan, true).count() > 0)
                    ((NpcInstance) self).dropItem(killer.getPlayer(), 9699, 1); //Red Coral Key
            } else if (self.getNpcId() == gatekeeper_lohan) {
                if (getAllByNpcId(gatekeeper_provo, true).count() > 0)
                    ((NpcInstance) self).dropItem(killer.getPlayer(), 9698, 1); //Blue Coral Key
            } else if (cor_gar_st_room_monsters.contains(self.getNpcId())) {
                Zone room1 = getZone("[cry_cav_steam_corr_room1]");
                Zone room2 = getZone("[cry_cav_steam_corr_room2]");
                Zone room3 = getZone("[cry_cav_steam_corr_room3]");
                Zone room4 = getZone("[cry_cav_steam_corr_room4]");
                if (self.isInZone(room1)) {
                    if (!room1ProtectorsSpawned && checkRoomWiped(room1)) {
                        room1ProtectorsSpawned = true;
                        for (int id : protectorsLoc.keySet())
                            addSpawnWithoutRespawn(id, protectorsLoc.get(id).get(0), 0); // getting first spawn location for existing set of npcs
                    }
                } else if (self.isInZone(room2)) {
                    if (!room2ProtectorsSpawned && checkRoomWiped(room2)) {
                        room2ProtectorsSpawned = true;
                        for (int id : protectorsLoc.keySet())
                            addSpawnWithoutRespawn(id, protectorsLoc.get(id).get(1), 0); // getting second spawn location for existing set of npcs
                    }
                } else if (self.isInZone(room3)) {
                    if (!room3ProtectorsSpawned && checkRoomWiped(room3)) {
                        room3ProtectorsSpawned = true;
                        for (int id : protectorsLoc.keySet())
                            addSpawnWithoutRespawn(id, protectorsLoc.get(id).get(2), 0); // getting third spawn location for existing set of npcs
                    }
                } else if (self.isInZone(room4)) {
                    if (!room4DoorOpened && checkRoomWiped(room4)) {
                        room4DoorOpened = true;
                        spawnByGroup("cry_cav_steam_kechi");
                        getDoor(door_cry_cav_steam_c_room4_door).openMe();
                        getDoor(door_cry_cav_steam_c_kechi_door).openMe();
                    }
                }
            } else if (cor_gar_emerald_sq_monsters.contains(self.getNpcId())) {
                if (self.isInZone(square)) {
                    if (!emeraldWiped && checkRoomWiped(square)) {
                        emeraldWiped = true;
                        spawnByGroup("cry_cav_emerald_main_reef_golems");
                        invokeDeathListener();
                    }
                }
            } else if (cor_gar_emerald_sq_reef_golems.contains(self.getNpcId())) {
                if (checkRoomWiped(square)) {
                    spawnByGroup("cry_cav_emerald_main_callers");
                    invokeDeathListener();
                }
            } else if (cor_gar_emerald_sq_callers.contains(self.getNpcId())) {
                if (checkRoomWiped(square) && !emeraldReady) {
                    emeraldReady = true;
                    spawnByGroup("cry_cav_emerald_main_guardian");
                    spawnByGroup("cry_cav_emerald_main_doorcontrollers");
                    spawnByGroup("cry_cav_emerald_main_trap");
                    invokeDeathListener();
                }
            } else if (cor_gar_emerald_sq_guardians.contains(self.getNpcId())) {
                if (checkRoomWiped(square) && emeraldReady) {
                    getDoor(door_cry_cav_emerald_darnel).openMe();
                    getDoor(door_cry_cav_emerald_darnel_inner).openMe();
                    spawnByGroup("cry_cav_emerald_main_darnel");
                }
            }
        }
    }

    private class ZoneListener implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Creature cha) {
            Player player = cha.getPlayer();
            if (player == null || !cha.isPlayer())
                return;
            if (!emeraldHallActivated && zone.getName().equalsIgnoreCase("[cry_cav_steam_corr_starter]")) {
                notifySteamStarted();
                zone.removeListener(this);
            }
            if (!emeraldHallActivated && zone.getName().equalsIgnoreCase("[cry_cav_emerald_starter]")) {
                emeraldHallActivated = true;
                notifyEmeraldStarted();
                zone.removeListener(this);
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature cha) {
        }
    }

    private class FailureTimer extends RunnableImpl {
        @Override
        public void runImpl() {
            collapse();
        }
    }
}
