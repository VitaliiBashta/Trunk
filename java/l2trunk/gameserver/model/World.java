package l2trunk.gameserver.model;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class World {
    /**
     * Map dimensions
     */
    public static final int MAP_MIN_X = Config.GEO_X_FIRST - 20 << 15;
    public static final int MAP_MAX_X = (Config.GEO_X_LAST - 20 + 1 << 15) - 1;
    public static final int MAP_MIN_Y = Config.GEO_Y_FIRST - 18 << 15;
    public static final int MAP_MAX_Y = (Config.GEO_Y_LAST - 18 + 1 << 15) - 1;
    public static final int MAP_MIN_Z = Config.MAP_MIN_Z;
    public static final int MAP_MAX_Z = Config.MAP_MAX_Z;
    public static final int WORLD_SIZE_X = Config.GEO_X_LAST - Config.GEO_X_FIRST + 1;
    public static final int WORLD_SIZE_Y = Config.GEO_Y_LAST - Config.GEO_Y_FIRST + 1;
    private static final int SHIFT_BY = Config.SHIFT_BY;
    private static final int SHIFT_BY_Z = Config.SHIFT_BY_Z;
    /**
     * calculated offset used so top left region is 0,0
     */
    private static final int OFFSET_X = Math.abs(MAP_MIN_X >> SHIFT_BY);
    private static final int OFFSET_Y = Math.abs(MAP_MIN_Y >> SHIFT_BY);
    private static final int OFFSET_Z = Math.abs(MAP_MIN_Z >> SHIFT_BY_Z);
    private static final Logger _log = LoggerFactory.getLogger(World.class);
    /**
     * Размерность массива регионов
     */
    private static final int REGIONS_X = (MAP_MAX_X >> SHIFT_BY) + OFFSET_X;
    private static final int REGIONS_Y = (MAP_MAX_Y >> SHIFT_BY) + OFFSET_Y;
    private static final int REGIONS_Z = (MAP_MAX_Z >> SHIFT_BY_Z) + OFFSET_Z;

    private static final WorldRegion[][][] worldRegions = new WorldRegion[REGIONS_X + 1][REGIONS_Y + 1][REGIONS_Z + 1];

    public static void init() {
        _log.info("L2World: Creating regions: [" + (REGIONS_X + 1) + "][" + (REGIONS_Y + 1) + "][" + (REGIONS_Z + 1) + "].");
    }

    private static WorldRegion[][][] getRegions() {
        return worldRegions;
    }

    private static int validX(int x) {
        if (x < 0)
            x = 0;
        else if (x > REGIONS_X)
            x = REGIONS_X;
        return x;
    }

    private static int validY(int y) {
        if (y < 0)
            y = 0;
        else if (y > REGIONS_Y)
            y = REGIONS_Y;
        return y;
    }

    private static int validZ(int z) {
        if (z < 0)
            z = 0;
        else if (z > REGIONS_Z)
            z = REGIONS_Z;
        return z;
    }

    static int validCoordX(int x) {
        if (x < MAP_MIN_X)
            x = MAP_MIN_X + 1;
        else if (x > MAP_MAX_X)
            x = MAP_MAX_X - 1;
        return x;
    }

    static int validCoordY(int y) {
        if (y < MAP_MIN_Y)
            y = MAP_MIN_Y + 1;
        else if (y > MAP_MAX_Y)
            y = MAP_MAX_Y - 1;
        return y;
    }

    static int validCoordZ(int z) {
        if (z < MAP_MIN_Z)
            z = MAP_MIN_Z + 1;
        else if (z > MAP_MAX_Z)
            z = MAP_MAX_Z - 1;
        return z;
    }

    private static int regionX(int x) {
        return (x >> SHIFT_BY) + OFFSET_X;
    }

    private static int regionY(int y) {
        return (y >> SHIFT_BY) + OFFSET_Y;
    }

    private static int regionZ(int z) {
        return (z >> SHIFT_BY_Z) + OFFSET_Z;
    }

    private static boolean isNeighbour(int x1, int y1, int z1, int x2, int y2, int z2) {
        return x1 <= x2 + 1 && x1 >= x2 - 1 && y1 <= y2 + 1 && y1 >= y2 - 1 && z1 <= z2 + 1 && z1 >= z2 - 1;
    }

    /**
     * @param loc локация для поиска региона
     * @return Регион, соответствующий локации
     */
    static WorldRegion getRegion(Location loc) {
        return getRegion(validX(regionX(loc.x)), validY(regionY(loc.y)), validZ(regionZ(loc.z)));
    }

    /**
     * @param obj обьект для поиска региона
     * @return Регион, соответствующий координатам обьекта
     */
    private static WorldRegion getRegion(GameObject obj) {
        return getRegion(validX(regionX(obj.getX())), validY(regionY(obj.getY())), validZ(regionZ(obj.getZ())));
    }

    /**
     * @param x координата на карте регионов
     * @param y координата на карте регионов
     * @param z координата на карте регионов
     * @return Регион, соответствующий координатам
     */
    private synchronized static WorldRegion getRegion(int x, int y, int z) {
        WorldRegion[][][] regions = getRegions();
        WorldRegion region;
        region = regions[x][y][z];
        if (region == null)
            region = regions[x][y][z];
        if (region == null)
            region = regions[x][y][z] = new WorldRegion(x, y, z);
        return region;
    }


    /**
     * Находит игрока по имени
     * Регистр символов любой.
     *
     * @param name имя
     * @return найденый игрок или null если игрока нет
     */
    public static Player getPlayer(String name) {
        return GameObjectsStorage.getPlayer(name);
    }

    public static Player getPlayer(int objId) {
        return GameObjectsStorage.getPlayer(objId);
    }

    /**
     * Проверяет, сменился ли регион в котором находится обьект
     * Если сменился - удаляет обьект из старого региона и добавляет в новый.
     *
     * @param object  обьект для проверки
     * @param dropper - если это L2ItemInstance, то будет анимация дропа с перса
     */
    static void addVisibleObject(GameObject object, Creature dropper) {
        if (object == null || !object.isVisible() || (object instanceof Player && ((Player) object).isInObserverMode()))
            return;

        WorldRegion region = getRegion(object);
        WorldRegion currentRegion = object.getCurrentRegion();

        if (currentRegion == region)
            return;

        if (currentRegion == null) {// Новый обьект (пример - игрок вошел в мир, заспаунился моб, дропнули вещь)
            // Добавляем обьект в список видимых
            object.setCurrentRegion(region);
            region.addObject(object);

            // Показываем обьект в текущем и соседних регионах
            // Если обьект игрок, показываем ему все обьекты в текущем и соседних регионах
            for (int x = validX(region.x - 1); x <= validX(region.x + 1); x++)
                for (int y = validY(region.y - 1); y <= validY(region.y + 1); y++)
                    for (int z = validZ(region.z - 1); z <= validZ(region.z + 1); z++)
                        getRegion(x, y, z).addToPlayers(object, dropper);
        } else// Обьект уже существует, перешел из одного региона в другой
        {
            currentRegion.removeObject(object); // Удаляем обьект из старого региона
            object.setCurrentRegion(region);
            region.addObject(object); // Добавляем обьект в список видимых

            // Убираем обьект из старых соседей.
            for (int x = validX(currentRegion.x - 1); x <= validX(currentRegion.x + 1); x++)
                for (int y = validY(currentRegion.y - 1); y <= validY(currentRegion.y + 1); y++)
                    for (int z = validZ(currentRegion.z - 1); z <= validZ(currentRegion.z + 1); z++)
                        if (!isNeighbour(region.x, region.y, region.z, x, y, z))
                            getRegion(x, y, z).removeFromPlayers(object);

            // Показываем обьект, но в отличие от первого случая - только для новых соседей.
            for (int x = validX(region.x - 1); x <= validX(region.x + 1); x++)
                for (int y = validY(region.y - 1); y <= validY(region.y + 1); y++)
                    for (int z = validZ(region.z - 1); z <= validZ(region.z + 1); z++)
                        if (!isNeighbour(currentRegion.x, currentRegion.y, currentRegion.z, x, y, z))
                            getRegion(x, y, z).addToPlayers(object, dropper);
        }
    }

    static void removeVisibleObject(GameObject object) {
        if (object == null || object.isVisible() || (object instanceof Player && ((Player) object).isInObserverMode()))
            return;

        WorldRegion currentRegion;
        if ((currentRegion = object.getCurrentRegion()) == null)
            return;

        object.setCurrentRegion(null);
        currentRegion.removeObject(object);

        for (int x = validX(currentRegion.x - 1); x <= validX(currentRegion.x + 1); x++)
            for (int y = validY(currentRegion.y - 1); y <= validY(currentRegion.y + 1); y++)
                for (int z = validZ(currentRegion.z - 1); z <= validZ(currentRegion.z + 1); z++)
                    getRegion(x, y, z).removeFromPlayers(object);
    }

    private static Stream<GameObject> getAroundObjects(GameObject object) {
        WorldRegion currentRegion = object.getCurrentRegion();
        if (currentRegion == null)
            return Stream.empty();
        List<GameObject> result = new ArrayList<>(128);
        for (int x = validX(currentRegion.x - 1); x <= validX(currentRegion.x + 1); x++)
            for (int y = validY(currentRegion.y - 1); y <= validY(currentRegion.y + 1); y++)
                for (int z = validZ(currentRegion.z - 1); z <= validZ(currentRegion.z + 1); z++)
                    getRegion(x, y, z).getObjects().stream()
                            .filter(Objects::nonNull)
                            .filter(obj -> obj.objectId() != object.objectId())
                            .filter(obj -> obj.getReflectionId() == object.getReflectionId())
                            .forEach(result::add);
        return result.stream();
    }

    public static GameObject getAroundObjectById(GameObject object, int objId) {
        return getAroundObjects(object)
                .filter(obj -> obj.objectId() == objId)
                .findFirst().orElse(null);
    }

    public static Stream<ItemInstance> getAroundItems(GameObject object, int radius, int height) {
        return getAroundObjects(object, radius, height)
                .filter(o -> o instanceof ItemInstance)
                .map(o -> (ItemInstance) o);
    }

    private static Stream<GameObject> getAroundObjects(GameObject object, int radius, int height) {
        return getAroundObjects(object)
                .filter(obj -> Math.abs(obj.getZ() - object.getZ()) <= height)
                .filter(obj -> distance(obj, object) <= radius);
    }

    public static Stream<Creature> getAroundCharacters(GameObject object) {
        return getAroundObjects(object)
                .filter(obj -> obj instanceof Creature)
                .map(obj -> (Creature) obj);
    }

    public static Stream<Creature> getAroundCharacters(GameObject object, int radius, int height) {
        return getAroundObjects(object, radius, height)
                .filter(obj -> obj instanceof Creature)
                .map(obj -> (Creature) obj);
    }

    public static Stream<NpcInstance> getAroundNpc(GameObject object) {
        return getAroundObjects(object)
                .filter(obj -> obj instanceof NpcInstance)
                .map(obj -> (NpcInstance) obj);
    }

    public static Stream<NpcInstance> getAroundNpc(GameObject object, int radius, int height) {
        return getAroundObjects(object, radius, height)
                .filter(obj -> obj instanceof NpcInstance)
                .map(obj -> (NpcInstance) obj);
    }

    private static int distance(GameObject o1, GameObject o2) {
        return (int) Math.sqrt(
                Math.pow(o1.getX() - o2.getX(), 2)
                        + Math.pow(o1.getY() - o2.getY(), 2));
    }

    public static Stream<Playable> getAroundPlayables(GameObject object) {
        return getAroundObjects(object)
                .filter(obj -> obj instanceof Playable)
                .map(obj -> (Playable) obj);
    }

    public static Stream<Playable> getAroundPlayables(GameObject object, int radius, int height) {
        return getAroundObjects(object, radius, height)
                .filter(obj -> obj instanceof Playable)
                .map(obj -> (Playable) obj);
    }

    public static List<Player> getAroundPlayers(GameObject object) {
        return getAroundObjects(object)
                .filter(obj -> obj instanceof Player)
                .map(obj -> (Player) obj)
                .collect(Collectors.toList());
    }

    public static Stream<Player> getAroundPlayers(GameObject object, int radius, int height) {
        return getAroundObjects(object, radius, height)
                .filter(obj -> obj instanceof Player)
                .map(obj -> (Player) obj);
    }

    private static boolean isNeighborsEmpty(WorldRegion region) {
        for (int x = validX(region.x - 1); x <= validX(region.x + 1); x++)
            for (int y = validY(region.y - 1); y <= validY(region.y + 1); y++)
                for (int z = validZ(region.z - 1); z <= validZ(region.z + 1); z++)
                    if (!getRegion(x, y, z).isEmpty())
                        return false;
        return true;
    }

    public static void activate(WorldRegion currentRegion) {
        for (int x = validX(currentRegion.x - 1); x <= validX(currentRegion.x + 1); x++)
            for (int y = validY(currentRegion.y - 1); y <= validY(currentRegion.y + 1); y++)
                for (int z = validZ(currentRegion.z - 1); z <= validZ(currentRegion.z + 1); z++)
                    getRegion(x, y, z).setActive(true);
    }

    static void deactivate(WorldRegion currentRegion) {
        for (int x = validX(currentRegion.x - 1); x <= validX(currentRegion.x + 1); x++)
            for (int y = validY(currentRegion.y - 1); y <= validY(currentRegion.y + 1); y++)
                for (int z = validZ(currentRegion.z - 1); z <= validZ(currentRegion.z + 1); z++)
                    if (isNeighborsEmpty(getRegion(x, y, z)))
                        getRegion(x, y, z).setActive(false);
    }

    /**
     * Показывает игроку все видимые обьекты в текущем регионе и соседних
     */
    public static void showObjectsToPlayer(Player player) {
        WorldRegion currentRegion = player.isInObserverMode() ? player.getObserverRegion() : player.getCurrentRegion();
        if (currentRegion == null)
            return;
        getAroundObjects(player)
                .forEach(obj -> player.sendPacket(player.addVisibleObject(obj, null)));
    }

    /**
     * Убирает у игрока все видимые обьекты в текущем регионе и соседних
     */
    static void removeObjectsFromPlayer(Player player) {
        WorldRegion currentRegion = player.isInObserverMode() ? player.getObserverRegion() : player.getCurrentRegion();
        if (currentRegion == null)
            return;

        getAroundObjects(player)
                .forEach(obj -> player.sendPacket(player.removeVisibleObject(obj, null)));
    }

    /**
     * Убирает обьект у всех игроков в регионе
     */
    public static void removeObjectFromPlayers(GameObject object) {
        WorldRegion currentRegion = object.getCurrentRegion();
        if (currentRegion == null)
            return;

        int oid = object.objectId();
        int rid = object.getReflectionId();

        Player p;
        List<L2GameServerPacket> d = null;

        for (int x = validX(currentRegion.x - 1); x <= validX(currentRegion.x + 1); x++)
            for (int y = validY(currentRegion.y - 1); y <= validY(currentRegion.y + 1); y++)
                for (int z = validZ(currentRegion.z - 1); z <= validZ(currentRegion.z + 1); z++)
                    for (GameObject obj : getRegion(x, y, z).getObjects()) {
                        if (obj instanceof Player && obj.objectId() != oid && obj.getReflectionId() == rid && !((Player) obj).isGM()) {
                            p = (Player) obj;
                            p.sendPacket(p.removeVisibleObject(object, d == null ? d = object.deletePacketList() : d));
                        }

                    }
    }

    static void addZone(Zone zone) {
        Reflection reflection = zone.getReflection();

        Territory territory = zone.getTerritory();
        if (territory == null) {
            _log.info("World: zone - " + zone.getName() + " not has territory.");
            return;
        }
        for (int x = validX(regionX(territory.getXmin())); x <= validX(regionX(territory.getXmax())); x++)
            for (int y = validY(regionY(territory.getYmin())); y <= validY(regionY(territory.getYmax())); y++)
                for (int z = validZ(regionZ(territory.getZmin())); z <= validZ(regionZ(territory.getZmax())); z++) {
                    WorldRegion region = getRegion(x, y, z);
                    region.addZone(zone);
                    region.getObjects().stream()
                            .filter(obj -> obj.getReflection() == reflection)
                            .filter(obj -> obj instanceof Creature)
                            .map(obj -> (Creature) obj)
                            .forEach(Creature::updateZones);
                }
    }

    static void removeZone(Zone zone) {
        Reflection reflection = zone.getReflection();

        Territory territory = zone.getTerritory();
        if (territory == null) {
            _log.info("World: zone - " + zone.getName() + " not has territory.");
            return;
        }
        for (int x = validX(regionX(territory.getXmin())); x <= validX(regionX(territory.getXmax())); x++)
            for (int y = validY(regionY(territory.getYmin())); y <= validY(regionY(territory.getYmax())); y++)
                for (int z = validZ(regionZ(territory.getZmin())); z <= validZ(regionZ(territory.getZmax())); z++) {
                    WorldRegion region = getRegion(x, y, z);
                    region.removeZone(zone);
                    for (GameObject obj : region.getObjects()) {
                        if (obj instanceof Creature && obj.getReflection() == reflection) {
                            ((Creature) obj).updateZones();
                        }

                    }
                }
    }

    /**
     * Создает и возвращает список территорий для точек x, y, z
     */
    public static void getZones(List<Zone> inside, Location loc, Reflection reflection) {
        WorldRegion region = getRegion(loc);
        List<Zone> zones = region.getZones();
        for (Zone zone : zones)
            if (zone.checkIfInZone(loc, reflection))
                inside.add(zone);
    }

    static boolean isWater(Location loc, Reflection reflection) {
        return getRegion(loc).getZones().stream()
                .filter(Objects::nonNull)
                .filter(zone -> zone.getType() == ZoneType.water)
                .anyMatch(zone -> zone.checkIfInZone(loc, reflection));
    }

}