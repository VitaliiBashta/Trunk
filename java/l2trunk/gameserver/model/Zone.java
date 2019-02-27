package l2trunk.gameserver.model;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.listener.Listener;
import l2trunk.commons.listener.ListenerList;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.EventTrigger;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.FuncAdd;
import l2trunk.gameserver.templates.ZoneTemplate;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.PositionUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public final class Zone {
    public static final String BLOCKED_ACTION_PRIVATE_STORE = "open_private_store";
    public static final String BLOCKED_ACTION_PRIVATE_WORKSHOP = "open_private_workshop";
    public static final String BLOCKED_ACTION_DROP_MERCHANT_GUARD = "drop_merchant_guard";
    public static final String BLOCKED_ACTION_MINIMAP = "open_minimap";
    /**
     * Ордер в зонах, с ним мы и добавляем/убираем статы.
     * TODO: сравнить ордер с оффом, пока от фонаря
     */
    private final static int ZONE_STATS_ORDER = 0x40;
    private final StatsSet params;
    private final ZoneTemplate template;
    private final ZoneListenerList listeners = new ZoneListenerList();
    private final List<Creature> objects = new CopyOnWriteArrayList<>();
    // Ady - Better implementation for zone effects and damage threads
    private Future<?> _effectThread = null;
    private Future<?> damageThread = null;
    private ZoneType type;
    private boolean active;
    private Reflection _reflection;

    public Zone(ZoneTemplate template) {
        this(template.getType(), template);
    }

    private Zone(ZoneType type, ZoneTemplate template) {
        this.type = type;
        this.template = template;
        params = template.getParams();
    }

    public ZoneTemplate getTemplate() {
        return template;
    }

    public final String getName() {
        return getTemplate().getName();
    }

    public ZoneType getType() {
        return type;
    }

    public void setType(ZoneType type) {
        this.type = type;
    }

    public Territory getTerritory() {
        return getTemplate().getTerritory();
    }

    private int getEnteringMessageId() {
        return getTemplate().getEnteringMessageId();
    }

    private int getLeavingMessageId() {
        return getTemplate().getLeavingMessageId();
    }

    private Skill getZoneSkill() {
        return getTemplate().getZoneSkill();
    }

    private ZoneTarget getZoneTarget() {
        return getTemplate().getZoneTarget();
    }

    private Race getAffectRace() {
        return getTemplate().getAffectRace();
    }

    private int getDamageOnHP() {
        return getTemplate().getDamageOnHP();
    }

    private double getMoveBonus() {
        return getTemplate().getMoveBonus();
    }

    /**
     * Возвращает бонус регенерации хп в этой зоне
     *
     * @return Бонус регенарации хп в этой зоне
     */
    private double getRegenBonusHP() {
        return getTemplate().getRegenBonusHP();
    }

    private double getRegenBonusMP() {
        return getTemplate().getRegenBonusMP();
    }

    public long getRestartTime() {
        return getTemplate().getRestartTime();
    }

    public List<Location> getRestartPoints() {
        return getTemplate().getRestartPoints();
    }

    private List<Location> getPKRestartPoints() {
        return getTemplate().getPKRestartPoints();
    }

    public Location getSpawn() {
        if (getRestartPoints() == null)
            return null;
        Location loc = Rnd.get(getRestartPoints());
        return loc.clone();
    }

    public Location getPKSpawn() {
        if (getPKRestartPoints() == null)
            return getSpawn();
        Location loc = Rnd.get(getPKRestartPoints());
        return loc.clone();
    }

    public boolean checkIfInZone(int x, int y) {
        return getTerritory().isInside(x, y);
    }

    public boolean checkIfInZone(Location loc, Reflection reflection) {
        return isActive() && _reflection == reflection && getTerritory().isInside(loc);
    }

    public boolean checkIfInZone(int x, int y, int z) {
        return checkIfInZone(x, y, z, getReflection());
    }

    private boolean checkIfInZone(int x, int y, int z, Reflection reflection) {
        return isActive() && _reflection == reflection && getTerritory().isInside(Location.of(x, y, z));
    }

    public boolean checkIfInZone(Creature cha) {
        return objects.contains(cha);
    }

    public final double findDistanceToZone(GameObject obj, boolean includeZAxis) {
        return findDistanceToZone(obj.getX(), obj.getY(), obj.getZ(), includeZAxis);
    }

    public final double findDistanceToZone(int x, int y, int z, boolean includeZAxis) {
        return PositionUtils.calculateDistance(x, y, z, (getTerritory().getXmax() + getTerritory().getXmin()) / 2, (getTerritory().getYmax() + getTerritory().getYmin()) / 2, (getTerritory().getZmax() + getTerritory().getZmin()) / 2, includeZAxis);
    }

    /**
     * Обработка входа в территорию
     * Персонаж всегда добавляется в список вне зависимости от активности территории.
     * Если зона акивная, то обработается вход в зону
     *
     * @param cha кто входит
     */
    public void doEnter(Creature cha) {
        boolean added = false;

        if (!objects.contains(cha))
            added = objects.add(cha);

        if (added)
            onZoneEnter(cha);

        if ((cha instanceof Player) && ((Player) cha).isGM()) {
            ((Player)cha).sendMessage("Entered the zone " + getName());
        }
    }

    /**
     * Обработка входа в зону
     *
     * @param actor кто входит
     */
    private void onZoneEnter(Creature actor) {
        checkEffects(actor, true);
        addZoneStats(actor);

        if (actor instanceof Player) {
            Player player = (Player) actor;
            if (getType() == ZoneType.buff_store_only)
                player.sendMessage("You have entered a buff store zone!");
            if (getEnteringMessageId() != 0)
                player.sendPacket(new SystemMessage2(SystemMsg.valueOf(getEnteringMessageId())));
            if (getTemplate().getEventId() != 0)
                player.sendPacket(new EventTrigger(getTemplate().getEventId(), true));
            if (getTemplate().getBlockedActions() != null)
                player.blockActions(getTemplate().getBlockedActions());
            if (getType() == ZoneType.fix_beleth) {
                player.sendMessage("Anti-beleth exploit");
                player.teleToClosestTown();
            }
            listeners.onEnter(player);
        }

    }

    /**
     * Обработка выхода из зоны
     * Object всегда убирается со списка вне зависимости от зоны
     * Если зона активная, то обработается выход из зоны
     *
     * @param cha кто выходит
     */
    public void doLeave(Creature cha) {
        boolean removed;

        removed = objects.remove(cha);

        if (removed)
            onZoneLeave(cha);

        if ((cha instanceof Player) && ((Player) cha).isGM()) {
            ((Player)cha).sendMessage("Left the area " + getName());
        }
    }

    /**
     * Обработка выхода из зоны
     *
     * @param actor кто выходит
     */
    private void onZoneLeave(Creature actor) {
        checkEffects(actor, false);
        removeZoneStats(actor);

        if (actor instanceof Player) {
            Player player = (Player) actor;
            if (getType() == ZoneType.buff_store_only)
                player.sendMessage("You have left the Buff Store Only Zone");
            if (getLeavingMessageId() != 0)
                player.sendPacket(new SystemMessage2(SystemMsg.valueOf(getLeavingMessageId())));
            if (getTemplate().getEventId() != 0)
                player.sendPacket(new EventTrigger(getTemplate().getEventId(), false));
            if (getTemplate().getBlockedActions() != null)
                player.unblockActions(getTemplate().getBlockedActions());
            listeners.onLeave(player);
        }

    }

    private void addZoneStats(Creature cha) {
        // Проверка цели
        if (!checkTarget(cha))
            return;

        // Скорость движения накладывается только на L2Playable
        // affectRace в базе не указан, если надо будет влияние, то поправим
        if (getMoveBonus() != 0)
            if (cha instanceof Playable) {
                cha.addStatFunc(new FuncAdd(Stats.RUN_SPEED, ZONE_STATS_ORDER, this, getMoveBonus()));
                cha.sendChanges();
            }

        // Если у нас есть что регенить
        if (getRegenBonusHP() != 0)
            cha.addStatFunc(new FuncAdd(Stats.REGENERATE_HP_RATE, ZONE_STATS_ORDER, this, getRegenBonusHP()));

        // Если у нас есть что регенить
        if (getRegenBonusMP() != 0)
            cha.addStatFunc(new FuncAdd(Stats.REGENERATE_MP_RATE, ZONE_STATS_ORDER, this, getRegenBonusMP()));
    }

    /**
     * Убирает добавленые зоной статы
     *
     * @param cha персонаж у которого убирается
     */
    private void removeZoneStats(Creature cha) {
        if (getRegenBonusHP() == 0 && getRegenBonusMP() == 0 && getMoveBonus() == 0)
            return;

        cha.removeStatsOwner(this);

        cha.sendChanges();
    }

    /**
     * Применяет эффекты при входе/выходе из(в) зону
     *
     * @param cha   обьект
     * @param enter вошел или вышел
     */
    private void checkEffects(Creature cha, boolean enter) {
        if (checkTarget(cha)) {
            // Ady - New implementation of zone effect and damage threads
            if (enter) {
                if (getZoneSkill() != null) {
                    if (_effectThread == null) {
                        synchronized (this) {
                            if (_effectThread == null) {
                                // TODO: Reuse 30 hardcoded
                                _effectThread = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new SkillTimer(), getTemplate().getInitialDelay(), 30000);
                            }
                        }
                    }
                } else if (getDamageOnHP() > 0 || getDamageOnHP() > 0) {
                    if (damageThread == null) {
                        synchronized (this) {
                            if (damageThread == null) {
                                damageThread = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new DamageTimer(), getTemplate().getInitialDelay(), 30000);
                            }
                        }
                    }
                }
            } else {
                if (getZoneSkill() != null)
                    cha.getEffectList().stopEffect(getZoneSkill());
            }
        }
    }

    /**
     * Проверяет подходит ли персонаж для вызвавшего действия
     *
     * @param cha персонаж
     * @return подошел ли
     */
    private boolean checkTarget(Creature cha) {
        switch (getZoneTarget()) {
            case pc:
                if (!(cha instanceof Playable))
                    return false;
                break;
            case only_pc:
                if (!(cha instanceof Player))
                    return false;
                break;
            case npc:
                if (!(cha instanceof NpcInstance))
                    return false;
                break;
        }

        // Если у нас раса не "all"
        if (getAffectRace() != null) {
            if (cha instanceof Player) {
                // если раса не подходит
                return ((Player) cha).getRace() == getAffectRace();
            }
            //если не игровой персонаж
            return false;
        }

        return true;
    }

    public List<Creature> getObjects() {
        return objects;
    }

    public Stream<Player> getInsidePlayers() {
        return objects.stream()
                .filter(o -> o instanceof Player)
                .map(o -> (Player) o);
    }

    public Stream<Playable> getInsidePlayables() {
        return objects.stream()
                .filter(Objects::nonNull)
                .filter(o -> o instanceof Playable)
                .map(o -> (Playable) o);
    }

    public Stream<NpcInstance> getInsideNpcs() {
        return objects.stream()
                .filter(o ->o instanceof NpcInstance)
                .map(o -> (NpcInstance) o);

    }

    public boolean isActive() {
        return active;
    }

    /**
     * Установка активности зоны. При установки флага активности, зона добавляется в соотвествующие регионы. В случае сброса
     * - удаляется.
     *
     * @param active активна ли зона
     */
    public void setActive(boolean active) {
        if (this.active == active)
            return;

        this.active = active;

        if (isActive())
            World.addZone(Zone.this);
        else
            World.removeZone(Zone.this);
    }

    public Reflection getReflection() {
        return _reflection;
    }

    public void setReflection(Reflection reflection) {
        _reflection = reflection;
    }

    public void setParam(String name, Residence value) {
        params.set(name, value);
    }

    public StatsSet getParams() {
        return params;
    }

    public <T extends Listener> boolean addListener(T listener) {
        return listeners.add(listener);
    }

    public <T extends Listener> boolean removeListener(T listener) {
        return listeners.remove(listener);
    }

    @Override
    public final String toString() {
        return "[Zone " + getType() + " name: " + getName() + "]";
    }

    public void broadcastPacket(L2GameServerPacket packet, boolean toAliveOnly) {
        getInsidePlayers().forEach(player -> {
            if (toAliveOnly) {
                if (!player.isDead())
                    player.broadcastPacket(packet);
            } else
                player.broadcastPacket(packet);
        });
    }

    public enum ZoneType {
        AirshipController,

        SIEGE,
        RESIDENCE,
        HEADQUARTER,
        FISHING,
        UnderGroundColiseum,
        water,
        battle_zone,
        damage,
        instant_skill,
        mother_tree,
        peace_zone,
        poison,
        ssq_zone,
        swamp,
        no_escape,
        no_landing,
        no_restart,
        no_summon,
        dummy,
        offshore,
        epic,
        buff_store_only,
        fix_beleth
    }

    public enum ZoneTarget {
        pc,
        npc,
        only_pc
    }

    private final class SkillTimer implements Runnable {
        private final Skill skill;
        private final int zoneTime;
        private final int _randomTime;
        private long activateTime = 0;

        SkillTimer() {
            skill = getZoneSkill();
            zoneTime = getTemplate().getUnitTick() * 1000;
            _randomTime = getTemplate().getRandomTick() * 1000;
        }

        @Override
        public void run() {
            if (!isActive())
                return;

            if (skill == null)
                return;

            getObjects().stream()
                    .filter(Objects::nonNull)
                    .filter(target -> !target.isDead())
                    .filter(Zone.this::checkTarget)
                    .filter(t -> Rnd.chance(getTemplate().getSkillProb()))
                    .forEach(skill::getEffects);

            if (activateTime == 0) {
                activateTime = System.currentTimeMillis() + (zoneTime + Rnd.get(-_randomTime, _randomTime));
            } else if (isActive()) {
                if (activateTime < System.currentTimeMillis()) {
                    setActive(false);
                    activateTime = System.currentTimeMillis() + (zoneTime + Rnd.get(-_randomTime, _randomTime));
                }
            } else {
                if (activateTime < System.currentTimeMillis()) {
                    setActive(true);
                    activateTime = System.currentTimeMillis() + (zoneTime + Rnd.get(-_randomTime, _randomTime));
                }
            }
        }
    }

    private final class DamageTimer implements Runnable {
        private final int _hp;
        private final int _mp;
        private final int _message;
        private final int _zoneTime;
        private final int _randomTime;
        private long _activateTime = 0;

        DamageTimer() {
            _hp = getTemplate().getDamageOnHP();
            _mp = getTemplate().getDamageOnMP();
            _message = getTemplate().getDamageMessageId();
            _zoneTime = getTemplate().getUnitTick() * 1000;
            _randomTime = getTemplate().getRandomTick() * 1000;
        }

        @Override
        public void run() {
            if (!isActive())
                return;

            if (_hp == 0 || _mp == 0)
                return;

            for (Creature target : getObjects()) {
                if (target == null || target.isDead())
                    continue;

                if (!checkTarget(target))
                    continue;

                if (_hp > 0) {
                    target.reduceCurrentHp(_hp, target, null, false, false, true, false, false, false, true);
                    if (_message > 0)
                        target.sendPacket(new SystemMessage2(SystemMsg.valueOf(_message)).addInteger(_hp));
                }

                if (_mp > 0) {
                    target.reduceCurrentMp(_mp, null);
                    if (_message > 0)
                        target.sendPacket(new SystemMessage2(SystemMsg.valueOf(_message)).addInteger(_mp));
                }
            }

            // TODO: This is not the same as in l2j, as we dont have on, off times, only unit ticks, so we use the same for both
            if (_activateTime == 0) {
                _activateTime = System.currentTimeMillis() + (_zoneTime + Rnd.get(-_randomTime, _randomTime));
            }
            // Si la zona esta activada y el tiempo paso, desactivamos la zona y seteamos la proxima activacion
            else if (isActive()) {
                if (_activateTime < System.currentTimeMillis()) {
                    setActive(false);
                    _activateTime = System.currentTimeMillis() + (_zoneTime + Rnd.get(-_randomTime, _randomTime));
                }
            }
            // Si la zona esta desactivada y el tiempo ya paso, activamos la zona y seteamos la proxima desactivacion
            else {
                if (_activateTime < System.currentTimeMillis()) {
                    setActive(true);
                    _activateTime = System.currentTimeMillis() + (_zoneTime + Rnd.get(-_randomTime, _randomTime));
                }
            }
        }
    }

    private class ZoneListenerList extends ListenerList {
        void onEnter(Player actor) {
            getListeners().stream().filter(l -> l instanceof OnZoneEnterLeaveListener)
                    .map(l -> (OnZoneEnterLeaveListener) l)
                    .forEach(l -> l.onZoneEnter(Zone.this, actor));
        }

        void onLeave(Player actor) {
            getListeners().stream().filter(l -> l instanceof OnZoneEnterLeaveListener)
                    .map(l -> (OnZoneEnterLeaveListener) l)
                    .forEach(l -> l.onZoneLeave(Zone.this, actor));
        }
    }
}