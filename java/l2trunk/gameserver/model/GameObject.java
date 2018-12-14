package l2trunk.gameserver.model;

import l2trunk.commons.lang.reference.HardReference;
import l2trunk.commons.lang.reference.HardReferences;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.base.InvisibleType;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.events.EventOwner;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.network.serverpackets.DeleteObject;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.scripts.Events;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class GameObject extends EventOwner {
    public static final GameObject[] EMPTY_L2OBJECT_ARRAY = new GameObject[0];
    /**
     * Основные состояния объекта
     */
    private static final int CREATED = 0;
    private static final int VISIBLE = 1;
    private static final int DELETED = -1;
    private static final Logger _log = LoggerFactory.getLogger(GameObject.class);
    /**
     * Состояние объекта
     */
    private final AtomicInteger _state = new AtomicInteger(CREATED);
    /**
     * Идентификатор объекта
     */
    protected int objectId;
    private Reflection reflection = ReflectionManager.DEFAULT;
    /**
     * Позиция объекта в мире
     */
    private int x;
    private int y;
    private int z;
    private WorldRegion currentRegion;

    protected GameObject() {

    }

    /**
     * Constructor<?> of L2Object.<BR><BR>
     *
     * @param objectId Идентификатор объекта
     */
    protected GameObject(int objectId) {
        this.objectId = objectId;
    }

    protected HardReference<? extends GameObject> getRef() {
        return HardReferences.emptyRef();
    }

    private void clearRef() {
        HardReference<? extends GameObject> reference = getRef();
        if (reference != null)
            reference.clear();
    }

    public Reflection getReflection() {
        return reflection;
    }

    public void setReflection(int reflectionId) {
        Reflection r = ReflectionManager.INSTANCE.get(reflectionId);
        if (r == null) {
            Log.debug("Trying to set unavailable reflection: " + reflectionId + " for object: " + this + "!", new Throwable().fillInStackTrace());
            return;
        }

        setReflection(r);
    }

    public GameObject setReflection(Reflection reflection) {
        if (this.reflection == reflection)
            return this;

        boolean respawn = false;
        if (isVisible()) {
            decayMe();
            respawn = true;
        }

        Reflection r = getReflection();
        if (!r.isDefault()) {
            r.removeObject(this);
        }

        this.reflection = reflection;

        if (!reflection.isDefault()) {
            reflection.addObject(this);
        }

        if (respawn)
            spawnMe();
        return this;
    }

    public int getReflectionId() {
        return reflection.getId();
    }

    public int getGeoIndex() {
        return reflection.getGeoIndex();
    }

    /**
     * Return the identifier of the L2Object.<BR><BR>
     */
//    @Override
//    public final int hashCode() {
//        return objectId;
//    }
    public final int getObjectId() {
        return objectId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    /**
     * Возвращает позицию (x, y, z, heading)
     *
     * @return Location
     */
    public Location getLoc() {
        return new Location(x, y, z, getHeading());
    }

    /**
     * Устанавливает позицию (x, y, z) L2Object
     *
     * @param loc Location
     */
    public void setLoc(Location loc) {
        setXYZ(loc.x, loc.y, loc.z);
    }

    protected int getGeoZ(Location loc) {
        return GeoEngine.getHeight(loc, getGeoIndex());
    }


    public void setXYZ(int x, int y, int z) {
        this.x = World.validCoordX(x);
        this.y = World.validCoordY(y);
        this.z = World.validCoordZ(z);

        World.addVisibleObject(this, null);
    }

    /**
     * Return the visibility state of the L2Object. <BR><BR>
     *
     * <B><U> Concept</U> :</B><BR><BR>
     * A L2Object is invisible if <B>_isVisible</B>=false or <B>_worldregion</B>==null <BR><BR>
     *
     * @return true if visible
     */
    public final boolean isVisible() {
        return _state.get() == VISIBLE;
    }

    InvisibleType getInvisibleType() {
        return InvisibleType.NONE;
    }

    public final boolean isInvisible() {
        return getInvisibleType() != InvisibleType.NONE;
    }

    public void spawnMe(Location loc) {
        spawnMe0(loc, null);
    }

    protected void spawnMe0(Location loc, Creature dropper) {
        x = loc.x;
        y = loc.y;
        z = getGeoZ(loc);

        spawn0(dropper);
    }

    public final void spawnMe() {
        spawn0(null);
    }

    /**
     * Добавляет обьект в мир, добавляет в текущий регион. Делает обьект видимым.
     */
    private void spawn0(Creature dropper) {
        if (!_state.compareAndSet(CREATED, VISIBLE))
            return;

        World.addVisibleObject(this, dropper);

        onSpawn();
    }

    /**
     * Do Nothing.<BR><BR>
     *
     * <B><U> Overriden in </U> :</B><BR><BR>
     * <li> L2Summon :  Reset isShowSpawnAnimation flag</li>
     * <li> L2NpcInstance    :  Reset some flags</li><BR><BR>
     */
    void onSpawn() {

    }

    /**
     * Удаляет объект из текущего региона, делая его невидимым.
     * Не путать с deleteMe. Объект после decayMe подлежит реюзу через spawnMe.
     * Если перепутать будет утечка памяти.
     */
    public final void decayMe() {
        if (!_state.compareAndSet(VISIBLE, CREATED))
            return;

        World.removeVisibleObject(this);
        onDespawn();
    }

    void onDespawn() {

    }

    /**
     * Удаляет объект из мира. После этого объект не подлежит использованию.
     */
    public final void deleteMe() {
        decayMe();

        if (!_state.compareAndSet(CREATED, DELETED))
            return;

        onDelete();
    }

    public final boolean isDeleted() {
        return _state.get() == DELETED;
    }

    void onDelete() {
        Reflection r = getReflection();
        if (!r.isDefault())
            r.removeObject(this);

        clearRef();
    }

    public void onAction(Player player, boolean shift) {
        if (Events.onAction(player, this, shift))
            return;

        player.sendActionFailed();
    }

    public void onForcedAttack(Player player, boolean shift) {
        player.sendActionFailed();
    }

    public boolean isAttackable(Creature attacker) {
        return false;
    }

    public String getL2ClassShortName() {
        return getClass().getSimpleName();
    }

    private long getXYDeltaSq(int x, int y) {
        long dx = x - getX();
        long dy = y - getY();
        return dx * dx + dy * dy;
    }

    private long getXYDeltaSq(Location loc) {
        return getXYDeltaSq(loc.x, loc.y);
    }

    private long getZDeltaSq(int z) {
        long dz = z - getZ();
        return dz * dz;
    }

    private long getXYZDeltaSq(int x, int y, int z) {
        return getXYDeltaSq(x, y) + getZDeltaSq(z);
    }

    private long getXYZDeltaSq(Location loc) {
        return getXYDeltaSq(loc.x, loc.y) + getZDeltaSq(loc.z);
    }

    public final double getDistance(int x, int y) {
        return Math.sqrt(getXYDeltaSq(x, y));
    }

    private double getDistance(int x, int y, int z) {
        return Math.sqrt(getXYZDeltaSq(x, y, z));
    }

    public final double getDistance(Location loc) {
        return getDistance(loc.x, loc.y, loc.z);
    }

    public final boolean isInRange(GameObject obj, long range) {
        if (obj == null)
            return false;
        if (obj.getReflection() != getReflection())
            return false;
        long dx = Math.abs(obj.getX() - getX());
        if (dx > range)
            return false;
        long dy = Math.abs(obj.getY() - getY());
        if (dy > range)
            return false;
        long dz = Math.abs(obj.getZ() - getZ());
        return dz <= 1500 && dx * dx + dy * dy <= range * range;
    }

    public final boolean isInRangeZ(GameObject obj, long range) {
        if (obj == null)
            return false;
        if (obj.getReflection() != getReflection())
            return false;
        long dx = Math.abs(obj.getX() - getX());
        if (dx > range)
            return false;
        long dy = Math.abs(obj.getY() - getY());
        if (dy > range)
            return false;
        long dz = Math.abs(obj.getZ() - getZ());
        return dz <= range && dx * dx + dy * dy + dz * dz <= range * range;
    }

    public final boolean isInRange(Location loc, long range) {
        return isInRangeSq(loc, range * range);
    }

    public final boolean isInRangeSq(Location loc, long range) {
        return getXYDeltaSq(loc) <= range;
    }

    public final boolean isInRangeZ(Location loc, long range) {
        return isInRangeZSq(loc, range * range);
    }

    private boolean isInRangeZSq(Location loc, long range) {
        return getXYZDeltaSq(loc) <= range;
    }

    public final double getDistance(GameObject obj) {
        if (obj == null)
            return 0;
        return Math.sqrt(getXYDeltaSq(obj.getX(), obj.getY()));
    }

    public final double getDistance3D(GameObject obj) {
        if (obj == null)
            return 0;
        return Math.sqrt(getXYZDeltaSq(obj.getX(), obj.getY(), obj.getZ()));
    }

    public final long getDistance3DNoRoot(GameObject obj) {
        if (obj == null)
            return 0;
        return getXYZDeltaSq(obj.getX(), obj.getY(), obj.getZ());
    }

    public final double getRealDistance(GameObject obj) {
        return getRealDistance3D(obj, true);
    }

    public final double getRealDistance3D(GameObject obj) {
        return getRealDistance3D(obj, false);
    }

    private double getRealDistance3D(GameObject obj, boolean ignoreZ) {
        double distance = ignoreZ ? getDistance(obj) : getDistance3D(obj);
        if (isCreature())
            distance -= ((Creature) this).getTemplate().collisionRadius;
        if (obj.isCreature())
            distance -= ((Creature) obj).getTemplate().collisionRadius;
        return distance > 0 ? distance : 0;
    }

    /**
     * Возвращает L2Player управляющий даным обьектом.<BR>
     * <li>Для L2Player это сам игрок.</li>
     * <li>Для L2Summon это его хозяин.</li><BR><BR>
     *
     * @return L2Player управляющий даным обьектом.
     */
    public Player getPlayer() {
        return null;
    }

    public int getHeading() {
        return 0;
    }

    int getMoveSpeed() {
        return 0;
    }

    public WorldRegion getCurrentRegion() {
        return currentRegion;
    }

    void setCurrentRegion(WorldRegion region) {
        currentRegion = region;
    }

    public boolean isInObserverMode() {
        return false;
    }

    public boolean isInOlympiadMode() {
        return false;
    }

    public boolean isInBoat() {
        return false;
    }

    public boolean isFlying() {
        return false;
    }

    public double getColRadius() {
        _log.warn("getColRadius called directly from L2Object");
        Thread.dumpStack();
        return 0;
    }

    public double getColHeight() {
        _log.warn("getColHeight called directly from L2Object");
        Thread.dumpStack();
        return 0;
    }

    public boolean isCreature() {
        return false;
    }

    public boolean isPlayable() {
        return false;
    }

    public boolean isPlayer() {
        return false;
    }

    public boolean isPet() {
        return false;
    }

    public boolean isSummon() {
        return false;
    }

    public boolean isNpc() {
        return false;
    }

    public boolean isMonster() {
        return false;
    }

    public boolean isItem() {
        return false;
    }

    public boolean isRaid() {
        return false;
    }

    public boolean isChampion() {
        return false;
    }

    public boolean isBoss() {
        return false;
    }

    public boolean isTrap() {
        return false;
    }

    public boolean isDoor() {
        return false;
    }

    public boolean isArtefact() {
        return false;
    }

    public boolean isSiegeGuard() {
        return false;
    }

    public boolean isClanAirShip() {
        return false;
    }

    public boolean isAirShip() {
        return false;
    }

    protected boolean isBoat() {
        return false;
    }

    public boolean isVehicle() {
        return false;
    }

    public boolean isMinion() {
        return false;
    }

    public String getName() {
        return getClass().getSimpleName() + ":" + objectId;
    }

    protected List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
        return Collections.emptyList();
    }

    public List<L2GameServerPacket> deletePacketList() {
        return Collections.singletonList(new DeleteObject(this));
    }

    @Override
    public void addEvent(GlobalEvent event) {
        event.onAddEvent(this);

        super.addEvent(event);
    }

    @Override
    public void removeEvent(GlobalEvent event) {
        event.onRemoveEvent(this);

        super.removeEvent(event);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (obj.getClass() != getClass())
            return false;
        return ((GameObject) obj).objectId == objectId;
    }
}