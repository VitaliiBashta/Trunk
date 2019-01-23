package l2trunk.gameserver.model;

import l2trunk.commons.lang.StringUtils;
import l2trunk.commons.lang.reference.HardReference;
import l2trunk.commons.lang.reference.HardReferences;
import l2trunk.commons.listener.Listener;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.commons.util.concurrent.atomic.AtomicState;
import l2trunk.gameserver.BalancerConfig;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CharacterAI;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.ai.PlayableAI.nextAction;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.geodata.GeoMove;
import l2trunk.gameserver.instancemanager.DimensionalRiftManager;
import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.GameObjectTasks.*;
import l2trunk.gameserver.model.Skill.SkillTargetType;
import l2trunk.gameserver.model.Skill.SkillType;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.actor.recorder.CharStatsChangeRecorder;
import l2trunk.gameserver.model.base.InvisibleType;
import l2trunk.gameserver.model.base.TeamType;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.olympiad.CompType;
import l2trunk.gameserver.model.instances.MinionInstance;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestEventType;
import l2trunk.gameserver.model.reference.L2Reference;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.skills.AbnormalEffect;
import l2trunk.gameserver.skills.EffectType;
import l2trunk.gameserver.skills.TimeStamp;
import l2trunk.gameserver.stats.*;
import l2trunk.gameserver.stats.Formulas.AttackInfo;
import l2trunk.gameserver.stats.funcs.Func;
import l2trunk.gameserver.stats.triggers.TriggerInfo;
import l2trunk.gameserver.stats.triggers.TriggerType;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2trunk.gameserver.taskmanager.RegenTaskManager;
import l2trunk.gameserver.templates.CharTemplate;
import l2trunk.gameserver.templates.item.WeaponTemplate;
import l2trunk.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.Log;
import l2trunk.gameserver.utils.PositionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static l2trunk.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

public abstract class Creature extends GameObject {
    public static final double HEADINGS_IN_PI = 10430.3783;
    public static final int INTERACTION_DISTANCE = 200;
    private final static int CLIENT_BAR_SIZE = 352; // 352 - размер полоски CP/HP/MP в клиенте, в пикселях
    private static final Logger _log = LoggerFactory.getLogger(Creature.class);
    private static final List<Double> POLE_VAMPIRIC_MOD = List.of(
            1.0, 0.9, 0.8, 0.7, 0.2, 0.01);
    /**
     * HashMap(Integer, L2Skill) containing all skills of the L2Character
     */
    protected final Map<Integer, Skill> skills = new ConcurrentSkipListMap<>();
    final Map<Integer, TimeStamp> skillReuses = new HashMap<>();
    final CharTemplate baseTemplate;
    private final Map<Integer, Long[]> receivedDebuffs;
    private final AtomicState afraid = new AtomicState();
    private final AtomicState muted = new AtomicState();
    private final AtomicState pmuted = new AtomicState();
    private final AtomicState amuted = new AtomicState();
    private final AtomicState paralyzed = new AtomicState();
    private final AtomicState rooted = new AtomicState();
    private final AtomicState sleeping = new AtomicState();
    private final AtomicState stunned = new AtomicState();
    private final AtomicState immobilized = new AtomicState();
    private final AtomicState confused = new AtomicState();
    private final AtomicState frozen = new AtomicState();
    private final AtomicState healBlocked = new AtomicState();
    private final AtomicState damageBlocked = new AtomicState();
    private final AtomicState buffImmunity = new AtomicState(); // Иммунитет к бафам
    private final AtomicState debuffImmunity = new AtomicState(); // Иммунитет к дебафам
    private final AtomicState effectImmunity = new AtomicState(); // Иммунитет ко всем эффектам
    private final AtomicState weaponEquipBlocked = new AtomicState();
    private final Lock moveLock = new ReentrantLock();
    /**
     * при moveToLocation используется для хранения геокоординат в которые мы двигаемся для того что бы избежать повторного построения одного и того же пути при followToCharacter используется для хранения мировых координат в которых находилась последний раз преследуемая цель для отслеживания
     * необходимости перестраивания пути
     */
    private final Location movingDestTempPos = new Location();
    private final List<List<Location>> _targetRecorder = new ArrayList<>();
    private final List<Calculator> calculators;
    private final Lock regenLock = new ReentrantLock();
    private final List<Zone> zones = new ArrayList<>();
    /**
     * Блокировка для чтения/записи объектов из региона
     */
    private final ReadWriteLock zonesLock = new ReentrantReadWriteLock();
    private final Lock zonesRead = zonesLock.readLock();
    private final Lock zonesWrite = zonesLock.writeLock();
    private final Lock statusListenersLock = new ReentrantLock();
    // Функция для дизактивации умений персонажа (если умение не активно, то он не дает статтов и имеет серую иконку).
    private final Set<Integer> unActiveSkills = new HashSet<>();
    private final AtomicBoolean isDead = new AtomicBoolean();
    private final AtomicBoolean isTeleporting = new AtomicBoolean();
    private final HardReference<? extends Creature> reference;
    public Future<?> _skillTask;
    public boolean isMoving;
    public boolean isFollow;
    protected volatile CharStatsChangeRecorder<? extends Creature> statsRecorder;
    protected boolean invul;
    protected CharTemplate template;
    protected String name;
    protected volatile CharListenerList listeners;
    protected int storedId;
    protected volatile CharacterAI ai;
    Future<?> skillGeoCheckTask;
    double _currentMp = 1;
    String title;
    private int _scheduledCastCount;
    private int _scheduledCastInterval;
    private Future<?> _skillLaunchedTask;

    private long reuseDelay = 0L;
    private double currentCp = 0;
    private double _currentHp = 1;
    private boolean isAttackAborted;
    private long _attackEndTime;
    private long _attackReuseEndTime;
    private Map<TriggerType, Set<TriggerInfo>> triggers = new ConcurrentHashMap<>();
    private volatile EffectList _effectList;
    private TeamType _team = TeamType.NONE;
    private Skill _castingSkill;
    private long _castInterruptTime;
    private long _animationEndTime;
    private Future<?> _stanceTask;
    private Runnable _stanceTaskRunnable;
    private long _stanceEndTime;
    private int _lastCpBarUpdate = -1;
    private int _lastHpBarUpdate = -1;
    private int _lastMpBarUpdate = -1;
    private int _poleAttackCount = 0;
    private List<Stats> _blockedStats;
    /**
     * Map 32 bits (0x00000000) containing all abnormal effect in progress
     */
    private int abnormalEffects;
    private int abnormalEffects2;
    private int _abnormalEffects3;
    private Map<Integer, Integer> _skillMastery;
    private boolean _fakeDeath;
    private boolean isblessedbynoblesse; // Восстанавливает все бафы после смерти
    private boolean _isSalvation; // Восстанавливает все бафы после смерти и полностью CP, MP, HP
    private boolean meditated;
    private boolean _lockedTarget;
    private boolean blocked;
    private boolean _flying;
    private boolean _running;
    private Future<?> _moveTask;
    private MoveNextTask _moveTaskRunnable;
    private List<Location> moveList;
    private Location destination;
    private int _offset;
    private boolean _forestalling;
    private volatile HardReference<? extends GameObject> target = HardReferences.emptyRef();
    private volatile HardReference<? extends Creature> castingTarget = HardReferences.emptyRef();
    private volatile HardReference<? extends Creature> followTarget = HardReferences.emptyRef();
    private volatile HardReference<? extends Creature> _aggressionTarget = HardReferences.emptyRef();
    private long _followTimestamp, _startMoveTime;
    private int _previousSpeed = 0;
    private int heading;
    private boolean _isRegenerating;
    private Future<?> _regenTask;
    private Runnable _regenTaskRunnable;
    /**
     * Список игроков, которым необходимо отсылать информацию об изменении состояния персонажа
     */
    private List<Player> _statusListeners;
    private Location _flyLoc;
    private List<ZoneType> restart_zones = List.of(ZoneType.battle_zone, ZoneType.peace_zone, ZoneType.offshore, ZoneType.dummy);

    protected Creature(int objectId, CharTemplate template) {
        super(objectId);

        receivedDebuffs = new HashMap<>();

        this.template = template;
        baseTemplate = template;

        calculators = new ArrayList<>();

        StatFunctions.addPredefinedFuncs(this);

        reference = new L2Reference<>(this);

        storedId = getObjectId();
        GameObjectsStorage.put(this);
    }

    public final int getStoredId() {
        return storedId;
//        return objectId;
    }

    @Override
    public HardReference<? extends Creature> getRef() {
        return reference;
    }

    public boolean isArtefact() {
        return false;
    }

    boolean isAttackAborted() {
        return isAttackAborted;
    }

    public final void abortAttack(boolean force, boolean message) {
        if (isAttackingNow()) {
            _attackEndTime = 0;
            if (force)
                isAttackAborted = true;

            getAI().setIntention(AI_INTENTION_ACTIVE);

            if (isPlayer() && message) {
                sendActionFailed();
                sendPacket(new SystemMessage2(SystemMsg.C1S_ATTACK_FAILED).addName(this));
            }
        }
    }

    public final void abortCast(boolean force, boolean message) {
        if (isCastingNow() && (force || canAbortCast())) {
            final Skill castingSkill = _castingSkill;
            final Future<?> skillTask = _skillTask;
            final Future<?> skillLaunchedTask = _skillLaunchedTask;
            final Future<?> skillGeoCheckTask = this.skillGeoCheckTask;

            finishFly(); // Броадкаст пакета FlyToLoc уже выполнен, устанавливаем координаты чтобы не было визуальных глюков
            clearCastVars();

            if (skillTask != null)
                skillTask.cancel(false); // cancels the skill hit scheduled task

            if (skillLaunchedTask != null)
                skillLaunchedTask.cancel(false); // cancels the skill hit scheduled task

            if (skillGeoCheckTask != null) {
                skillGeoCheckTask.cancel(false); // cancels the skill GeoCheck scheduled task
            }

            if (castingSkill != null) {
                if (castingSkill.isUsingWhileCasting) {
                    Creature target = getAI().getAttackTarget();
                    if (target != null)
                        target.getEffectList().stopEffect(castingSkill.id);
                }

                removeSkillMastery(castingSkill.id);
            }

            broadcastPacket(new MagicSkillCanceled(getObjectId())); // broadcast packet to stop animations client-side

            getAI().setIntention(AI_INTENTION_ACTIVE);

            if (isPlayer() && message)
                sendPacket(SystemMsg.YOUR_CASTING_HAS_BEEN_INTERRUPTED);
        }
    }

    private boolean canAbortCast() {
        return _castInterruptTime > System.currentTimeMillis();
    }

    private boolean absorbAndReflect(Creature target, Skill skill, double damage) {
        if (target.isDead())
            return false;

        boolean bow = getActiveWeaponItem() != null && (getActiveWeaponItem().getItemType() == WeaponType.BOW || getActiveWeaponItem().getItemType() == WeaponType.CROSSBOW);

        double value = 0;

        if (skill != null && skill.isMagic())
            value = target.calcStat(Stats.REFLECT_AND_BLOCK_MSKILL_DAMAGE_CHANCE, 0, this, skill);
        else if (skill != null && skill.castRange <= 200)
            value = target.calcStat(Stats.REFLECT_AND_BLOCK_PSKILL_DAMAGE_CHANCE, 0, this, skill);
        else if (skill == null && !bow)
            value = target.calcStat(Stats.REFLECT_AND_BLOCK_DAMAGE_CHANCE, 0, this, null);

        // The purpose of all damage recorded
        if (value > 0 && Rnd.chance(value)) {
            reduceCurrentHp(damage, target, null, true, true, false, false, false, false, true);
            return true;
        }

        if (skill != null && skill.isMagic())
            value = target.calcStat(Stats.REFLECT_MSKILL_DAMAGE_PERCENT, 0, this, skill);
        else if (skill != null) {
            if (skill.castRange >= 0 && skill.castRange <= 40)
                value = target.calcStat(Stats.REFLECT_PSKILL_DAMAGE_PERCENT, 0, this, skill);
        } else if (!bow)
            value = target.calcStat(Stats.REFLECT_DAMAGE_PERCENT, 0, this, null);

        if (value > 0) {
            // Purpose able to reflect some of the damage
            if (getCurrentHp() + getCurrentCp() > value * 0.01 * damage) {
                reduceCurrentHp(value * 0.01 * damage, target, null, true, true, false, false, false, false, true);
            } else {
                // if (!(this instanceof Player && ((Player)this).isInOlympiadMode()) && !(this instanceof Player && ((Player)this).isInDuel()))
                if (!isPlayer() || !(getPlayer().isInOlympiadMode() || getPlayer().isInDuel())) {
                    doDie(this);
                }
            }
        }

        if (skill != null || bow)
            return false;

        // вампирик
        damage = (int) (damage - target.getCurrentCp());

        if (damage <= 0)
            return false;

        final double poleMod = _poleAttackCount < POLE_VAMPIRIC_MOD.size() ? POLE_VAMPIRIC_MOD.get(_poleAttackCount) : 0;
        double absorb = poleMod * calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0, target, null);
        double limit;
        if (absorb > 0 && !target.isDamageBlocked()) {
            limit = calcStat(Stats.HP_LIMIT, null, null) * getMaxHp() / 100.;
            if (getCurrentHp() < limit)
                setCurrentHp(Math.min(_currentHp + damage * absorb * BalancerConfig.ALT_ABSORB_DAMAGE_MODIFIER / 100., limit), false);
        }

        absorb = poleMod * calcStat(Stats.ABSORB_DAMAGEMP_PERCENT, 0, target, null);
        if (absorb > 0 && !target.isDamageBlocked()) {
            limit = calcStat(Stats.MP_LIMIT, null, null) * getMaxMp() / 100.;
            if (getCurrentMp() < limit)
                setCurrentMp(Math.min(_currentMp + damage * absorb * BalancerConfig.ALT_ABSORB_DAMAGE_MODIFIER / 100., limit));
        }

        return false;
    }

    private double absorbToEffector(Creature attacker, double damage) {
        double transferToEffectorDam = calcStat(Stats.TRANSFER_TO_EFFECTOR_DAMAGE_PERCENT, 0.);
        if (transferToEffectorDam > 0) {
            Effect effect = getEffectList().getEffectByType(EffectType.AbsorbDamageToEffector);
            if (effect == null)
                return damage;

            Creature effector = effect.getEffector();
            // on dead char, not online player - do not give ABSORB, and not for himself
            if (effector == this || effector.isDead() || !isInRange(effector, 1200))
                return damage;

            Player thisPlayer = getPlayer();
            Player effectorPlayer = effector.getPlayer();
            if (thisPlayer != null && effectorPlayer != null) {
                if (thisPlayer != effectorPlayer && (!thisPlayer.isOnline() || !thisPlayer.isInParty() || thisPlayer.getParty() != effectorPlayer.getParty()))
                    return damage;
            } else
                return damage;

            double transferDamage = (damage * transferToEffectorDam) * .01;
            damage -= transferDamage;

            effector.reduceCurrentHp(transferDamage, effector, null, false, false, !attacker.isPlayable(), false, true, false, true);
        }
        return damage;
    }

    private double absorbToMp(double damage) {
        double transferToMpDamPercent = calcStat(Stats.TRANSFER_TO_MP_DAMAGE_PERCENT, 0.);
        if (transferToMpDamPercent > 0) {
            double transferDamage = (damage * transferToMpDamPercent) * .01;

            double currentMp = getCurrentMp();
            if (currentMp > transferDamage) {
                setCurrentMp(getCurrentMp() - transferDamage);
                return 0;
            } else {
                if (currentMp > 0) {
                    damage -= currentMp;
                    setCurrentMp(0);
                    sendPacket(SystemMsg.MP_BECAME_0_AND_THE_ARCANE_SHIELD_IS_DISAPPEARING);
                }
                getEffectList().stopEffects(EffectType.AbsorbDamageToMp);
            }

            return damage;
        }
        return damage;
    }

    private double absorbToSummon(double damage) {
        double transferToSummonDam = calcStat(Stats.TRANSFER_TO_SUMMON_DAMAGE_PERCENT, 0.);
        if (transferToSummonDam > 0) {
            Summon summon = getPet();
            double transferDamage = (damage * transferToSummonDam) * .01;
            if (summon == null || summon.isDead() || summon.getCurrentHp() < transferDamage)
                getEffectList().stopEffects(EffectType.AbsorbDamageToSummon);
            else if (summon.isSummon() && summon.isInRangeZ(this, 1200)) {
                damage -= transferDamage;

                summon.reduceCurrentHp(transferDamage, summon, null, false, false, false, false, true, false, true);
            }
        }
        return damage;
    }

    public void addBlockStats(List<Stats> stats) {
        if (_blockedStats == null)
            _blockedStats = new ArrayList<>();
        _blockedStats.addAll(stats);
    }

    public Skill addSkill(int newSkillId) {
        return addSkill(SkillTable.INSTANCE.getInfo(newSkillId));
    }

    public Skill addSkill(int newSkillId, int lvl) {
        return addSkill(SkillTable.INSTANCE.getInfo(newSkillId, lvl));
    }

    private Skill addSkill(Skill newSkill) {
        if (newSkill == null)
            return null;

        Skill oldSkill = skills.get(newSkill.id);

        if (oldSkill != null && oldSkill.level == newSkill.level)
            return newSkill;

        // Replace oldSkill by newSkill or Add the newSkill
        skills.put(newSkill.id, newSkill);

        // FIX for /useskill re-use exploit
        if (oldSkill != null) {
            TimeStamp sts = skillReuses.get(oldSkill.hashCode());
            if (sts != null && sts.hasNotPassed()) {
                skillReuses.put(newSkill.hashCode(), sts);
            }
        }

        if (oldSkill != null) {
            removeStatsOwner(oldSkill);
            removeTriggers(oldSkill);
        }

        addTriggers(newSkill);

        // Add Func objects of newSkill to the calculator set of the L2Character
        addStatFuncs(newSkill.getStatFuncs());

        return oldSkill;
    }

    public List<Calculator> getCalculators() {
        return calculators;
    }

    public final void addStatFunc(Func f) {
        if (f == null)
            return;
        synchronized (calculators) {
            Calculator calculator = new Calculator(f.stat, this);
            calculator.addFunc(f);
            if (!calculators.contains(calculator))
                calculators.add(calculator);
        }
    }

    public final void addStatFuncs(Stream<Func> funcs) {
        funcs.forEach(this::addStatFunc);
    }

    private void removeStatFunc(Func f) {
        if (f == null)
            return;
        int stat = f.stat.ordinal();
        synchronized (calculators) {
            if (calculators.get(stat) != null)
                calculators.get(stat).removeFunc(f);
        }
    }

    final void removeStatFuncs(Stream<Func> funcs) {
        funcs.forEach(this::removeStatFunc);
    }

    public final void removeStatsOwner(Object owner) {
        synchronized (calculators) {
            calculators.stream()
                    .filter(Objects::nonNull)
                    .forEach(c -> c.removeOwner(owner));
        }
    }

    public void altOnMagicUseTimer(Creature aimingTarget, int skillId, int skillLvl) {
        altOnMagicUseTimer(aimingTarget, SkillTable.INSTANCE.getInfo(skillId, skillLvl));
    }

    public void altOnMagicUseTimer(Creature aimingTarget, int skillId) {
        altOnMagicUseTimer(aimingTarget, skillId, 1);
    }

    private void altOnMagicUseTimer(Creature aimingTarget, Skill skill) {
        if (isAlikeDead())
            return;
        int magicId = skill.displayId;
        int level = Math.max(1, getSkillDisplayLevel(skill.id));
        List<Creature> targets = skill.getTargets(this, aimingTarget, true);
        broadcastPacket(new MagicSkillLaunched(getObjectId(), magicId, level, Collections.unmodifiableList(targets)));
        double mpConsume2 = skill.mpConsume2;
        if (mpConsume2 > 0) {
            if (_currentMp < mpConsume2) {
                sendPacket(SystemMsg.NOT_ENOUGH_MP);
                return;
            }
            if (skill.isMagic())
                reduceCurrentMp(calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsume2, aimingTarget, skill), null);
            else
                reduceCurrentMp(calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsume2, aimingTarget, skill), null);
        }
        callSkill(skill, targets, false);
    }

    public void altUseSkill(int skillId, int skillLvl, Creature target) {
        altUseSkill(SkillTable.INSTANCE.getInfo(skillId, skillLvl), target);
    }

    public void altUseSkill(int skillId, Creature target) {
        altUseSkill(skillId, 1, target);
    }

    private void altUseSkill(Skill skill, Creature target) {
        if (skill == null)
            return;

        int magicId = skill.id;
        if (isUnActiveSkill(magicId))
            return;
        if (isSkillDisabled(skill)) {
            sendReuseMessage(skill);
            return;
        }
        if (target == null) {
            target = skill.getAimingTarget(this, getTarget());
            if (target == null)
                return;
        }

        getListeners().onMagicUse(skill, target, true);

        List<Integer> itemConsume = skill.itemConsume;

        if (itemConsume.get(0) > 0)
            for (int i = 0; i < itemConsume.size(); i++)
                if (!consumeItem(skill.getItemConsumeId().get(i), itemConsume.get(i))) {
                    sendPacket(skill.isItemHandler() ? SystemMsg.INCORRECT_ITEM_COUNT : SystemMsg.THERE_ARE_NOT_ENOUGH_NECESSARY_ITEMS_TO_USE_THE_SKILL);
                    return;
                }

        if (skill.referenceItemId > 0)
            if (!consumeItemMp(skill.referenceItemId, skill.getReferenceItemMpConsume()))
                return;

        if (skill.soulsConsume > getConsumedSouls()) {
            sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SOULS);
            return;
        }

        if (skill.energyConsume() > getAgathionEnergy()) {
            sendPacket(SystemMsg.THE_SKILL_HAS_BEEN_CANCELED_BECAUSE_YOU_HAVE_INSUFFICIENT_ENERGY);
            return;
        }

        if (skill.soulsConsume > 0)
            setConsumedSouls(getConsumedSouls() - skill.soulsConsume, null);
        if (skill.energyConsume() > 0)
            setAgathionEnergy(getAgathionEnergy() - skill.energyConsume());

        int level = Math.max(1, getSkillDisplayLevel(magicId));
        Formulas.calcSkillMastery(skill, this);
        long reuseDelay = Formulas.calcSkillReuseDelay(this, skill);
        if (!skill.isToggle())
            broadcastPacket(new MagicSkillUse(this, target, skill.displayId, level, skill.hitTime, reuseDelay));
        // Не показывать сообщение для хербов и кубиков
        if (!skill.isHideUseMessage())
            if (skill.skillType == SkillType.PET_SUMMON)
                sendPacket(new SystemMessage2(SystemMsg.SUMMONING_YOUR_PET));
            else if (!skill.isItemHandler())
                sendPacket(new SystemMessage2(SystemMsg.YOU_USE_S1).addSkillName(magicId, level));
            else
                sendPacket(new SystemMessage2(SystemMsg.YOU_USE_S1).addItemName(skill.getItemConsumeId().get(0)));

        if (!skill.isItemHandler())
            disableSkill(skill, reuseDelay);

        ThreadPoolManager.INSTANCE.schedule(new AltMagicUseTask(this, target, skill.id), skill.hitTime);
    }

    public void sendReuseMessage(Skill skill) {
    }

    public void broadcastPacket(L2GameServerPacket... packets) {
        sendPacket(packets);
        broadcastPacketToOthers(packets);
    }

    void broadcastPacket(List<L2GameServerPacket> packets) {
        sendPacket(packets);
        broadcastPacketToOthers(packets);
    }

    public void broadcastPacketToOthers(L2GameServerPacket... packets) {
        if (!isVisible() || packets.length == 0)
            return;
        World.getAroundPlayers(this)
                .forEach(p -> p.sendPacket(packets));

    }

    void broadcastPacketToOthers(List<L2GameServerPacket> packets) {
        if (!isVisible() || packets.isEmpty())
            return;
        World.getAroundPlayers(this)
                .forEach(p -> p.sendPacket(packets));
    }

    void broadcastToStatusListeners(L2GameServerPacket... packets) {
        if (!isVisible() || packets.length == 0)
            return;

        statusListenersLock.lock();
        try {
            if (_statusListeners == null || _statusListeners.isEmpty())
                return;

            Player player;
            for (Player _statusListener : _statusListeners) {
                player = _statusListener;
                player.sendPacket(packets);
            }
        } finally {
            statusListenersLock.unlock();
        }
    }

    void addStatusListener(Player cha) {
        if (cha == this)
            return;

        statusListenersLock.lock();
        try {
            if (_statusListeners == null)
                _statusListeners = new ArrayList<>();
            if (!_statusListeners.contains(cha))
                _statusListeners.add(cha);
        } finally {
            statusListenersLock.unlock();
        }
    }

    void removeStatusListener(Creature cha) {
        statusListenersLock.lock();
        try {
            if (_statusListeners == null)
                return;
            _statusListeners.remove(cha);
        } finally {
            statusListenersLock.unlock();
        }
    }

    private void clearStatusListeners() {
        statusListenersLock.lock();
        try {
            if (_statusListeners == null)
                return;
            _statusListeners.clear();
        } finally {
            statusListenersLock.unlock();
        }
    }

    public StatusUpdate makeStatusUpdate(int... fields) {
        StatusUpdate su = new StatusUpdate(getObjectId());
        for (int field : fields)
            switch (field) {
                case StatusUpdate.CUR_HP:
                    su.addAttribute(field, (int) getCurrentHp());
                    break;
                case StatusUpdate.MAX_HP:
                    su.addAttribute(field, getMaxHp());
                    break;
                case StatusUpdate.CUR_MP:
                    su.addAttribute(field, (int) getCurrentMp());
                    break;
                case StatusUpdate.MAX_MP:
                    su.addAttribute(field, getMaxMp());
                    break;
                case StatusUpdate.KARMA:
                    su.addAttribute(field, getKarma());
                    break;
                case StatusUpdate.CUR_CP:
                    su.addAttribute(field, (int) getCurrentCp());
                    break;
                case StatusUpdate.MAX_CP:
                    su.addAttribute(field, getMaxCp());
                    break;
                case StatusUpdate.PVP_FLAG:
                    su.addAttribute(field, getPvpFlag());
                    break;
            }
        return su;
    }

    public void broadcastStatusUpdate() {
        if (!needStatusUpdate())
            return;

        StatusUpdate su = makeStatusUpdate(StatusUpdate.MAX_HP, StatusUpdate.MAX_MP, StatusUpdate.CUR_HP, StatusUpdate.CUR_MP);
        broadcastToStatusListeners(su);
    }

    public int calcHeading(int x_dest, int y_dest) {
        return (int) (Math.atan2(getY() - y_dest, getX() - x_dest) * HEADINGS_IN_PI) + 32768;
    }

    public final double calcStat(Stats stat, double init) {
        return calcStat(stat, init, null, null);
    }

    public final double calcStat(Stats stat, double init, Creature target, Skill skill) {

        Calculator c = new Calculator(stat, target);
        if (calculators.contains(c))
            return init;
        Env env = new Env();
        env.character = this;

        if ((skill != null) && (skill.id == 1557))
            env.target = getPlayer().getPet();
        else {
            env.target = target;
        }
        env.skill = skill;
        env.value = init;
        c.calc(env);
        return env.value;
    }

    public final double calcStat(Stats stat, Creature target, Skill skill) {
        if ((skill != null) && (skill.id == 1557)) {
            target = getPlayer().getPet();
        }
        Env env = new Env(this, target, skill);
        if (stat == null) {
            _log.warn("FIX ME FAST(!) My name IS " + getName() + " and I'm a player?(" + isPlayer() + ") my target is " + target.getName() + " skill:" + skill.name + "");
            return 0;
        }
        env.value = stat.getInit();
        int id = stat.ordinal();
        Calculator c = calculators.get(id);
        if (c != null)
            c.calc(env);
        return env.value;
    }

    /**
     * Return the Attack Speed of the L2Character (delay (in milliseconds) before next attack).
     */
    public int calculateAttackDelay() {
        return Formulas.calcPAtkSpd(getPAtkSpd());
    }

    public void callSkill(int skillId, int skillLvl, List<Creature> targets, boolean useActionSkills) {
        callSkill(SkillTable.INSTANCE.getInfo(skillId, skillLvl), targets, useActionSkills);
    }

    public void callSkill(int skillId, List<Creature> targets, boolean useActionSkills) {
        callSkill(skillId, 1, targets, useActionSkills);
    }

    public void callSkill(Skill skill, List<Creature> targets, boolean useActionSkills) {
        try {
            if (useActionSkills && !skill.isUsingWhileCasting)
                if (skill.isOffensive()) {
                    if (skill.isMagic())
                        useTriggers(getTarget(), TriggerType.OFFENSIVE_MAGICAL_SKILL_USE, null, skill, 0);
                    else
                        useTriggers(getTarget(), TriggerType.OFFENSIVE_PHYSICAL_SKILL_USE, null, skill, 0);
                } else if (skill.isMagic()) // для АоЕ, пати/клан бафов и селфов триггер накладывается на кастера
                {
                    final boolean targetSelf = skill.isAoE() || skill.isNotTargetAoE() || skill.targetType == Skill.SkillTargetType.TARGET_SELF;
                    useTriggers(targetSelf ? this : getTarget(), TriggerType.SUPPORT_MAGICAL_SKILL_USE, null, skill, 0);
                }

            Player pl = getPlayer();
            for (Creature target : targets) {

                // Фильтруем неуязвимые цели
                if (skill.isOffensive() && target.isInvul()) {
                    Player pcTarget = target.getPlayer();
                    if ((!skill.isIgnoreInvul || (pcTarget != null && pcTarget.isGM())) && !target.isArtefact()) {
                        continue;
                    }
                }

                // Рассчитываем игрорируемые скилы из спец.эффекта
                Effect ie = target.getEffectList().getEffectByType(EffectType.IgnoreSkill);
                if (ie != null)
                    if (ie.getTemplate().getParam().getIntegerList("skillId").contains(skill.id)) {
                        continue;
                    }

                target.getListeners().onMagicHit(skill, this);

                if (pl != null)
                    if (target.isNpc()) {
                        NpcInstance npc = (NpcInstance) target;
                        pl.getQuestsForEvent(npc, QuestEventType.MOB_TARGETED_BY_SKILL)
                                .forEach(qs -> qs.getQuest().notifySkillUse(npc, skill, qs));
                    }

                if (skill.negateSkill > 0)
                    target.getEffectList().getAllEffects()
                            .filter(e -> e.getSkill().id == skill.negateSkill)
                            .filter(Effect::isCancelable)
                            .filter(e -> (skill.negatePower <= 0 || e.getSkill().getPower() <= skill.negatePower))
                            .forEach(Effect::exit);

                if (skill.cancelTarget > 0)
                    if (Rnd.chance(skill.cancelTarget))
                        if ((target.getCastingSkill() == null || !(target.getCastingSkill().skillType == SkillType.TAKECASTLE || target.getCastingSkill().skillType == SkillType.TAKEFORTRESS || target.getCastingSkill().skillType == SkillType.TAKEFLAG)) && !target.isRaid()) {
                            target.abortAttack(true, true);
                            target.abortCast(true, true);
                            target.setTarget(null);
                        }
            }

            if (skill.isOffensive())
                startAttackStanceTask();

            // Применяем селфэффекты на кастера
            // Особое условие для атакующих аура-скиллов (Vengeance 368):
            // если ни одна цель не задета то селфэффекты не накладываются
            if (!(skill.isNotTargetAoE() && skill.isOffensive() && targets.size() == 0))
                skill.getEffects(this, false, true);

            skill.useSkill(this, targets);
        } catch (Exception e) {
            _log.warn("Error while Calling Skill! ");
            e.printStackTrace();
        }
    }

    private void useTriggers(GameObject target, TriggerType type, Skill ex, Skill owner, double damage) {
        Set<TriggerInfo> SkillsOnSkillAttack = triggers.get(type);
        if (SkillsOnSkillAttack != null) {
            SkillsOnSkillAttack.stream()
                    .filter(t -> t.getSkill() != ex)
                    .forEach(t ->
                            useTriggerSkill(target == null ? getTarget() : target, null, t, owner, damage));

        }
    }

    private void useTriggerSkill(GameObject target, List<Creature> targets, TriggerInfo trigger, Skill owner, double damage) {
        Skill skill = trigger.getSkill();
        if (skill.getReuseDelay(this) > 0 && isSkillDisabled(skill))
            return;

        Creature aimTarget = skill.getAimingTarget(this, target);
        // DS: Для шансовых скиллов с TARGET_SELF и условием "пвп" сам кастер будет являться aimTarget,
        // поэтому в условиях для триггера проверяем реальную цель.
        Creature realTarget = target != null && target.isCreature() ? (Creature) target : null;
        if (Rnd.get(140) < trigger.getChance() && trigger.checkCondition(this, realTarget, aimTarget, owner, damage) && skill.checkCondition(this, aimTarget, false, true, true)) {
            if (targets == null)
                targets = skill.getTargets(this, aimTarget, false);

            int displayId = 0, displayLevel = 0;

            if (skill.hasEffects()) {
                displayId = skill.getEffectTemplates().get(0)._displayId;
                displayLevel = skill.getEffectTemplates().get(0)._displayLevel;
            }

            if (displayId == 0)
                displayId = skill.displayId;
            if (displayLevel == 0)
                displayLevel = skill.getDisplayLevel();

            if (trigger.getType() != TriggerType.SUPPORT_MAGICAL_SKILL_USE) {
                for (Creature cha : targets)
                    broadcastPacket(new MagicSkillUse(this, cha, displayId, displayLevel));
            }

            Formulas.calcSkillMastery(skill, this);
            callSkill(skill, targets, false);
            disableSkill(skill, skill.getReuseDelay(this));
        }
    }

    public boolean checkBlockedStat(Stats stat) {
        return _blockedStats != null && _blockedStats.contains(stat);
    }

    public boolean checkReflectSkill(Creature attacker, Skill skill) {
        if (!skill.isReflectable)
            return false;
        // Does not reflect if there is invulnerable, or it may cancel
        if (isInvul() || attacker.isInvul() || !skill.isOffensive())
            return false;
        // Of the magical skills are reflected only damaging skills for CPs.
        if (skill.isMagic() && skill.skillType != SkillType.MDAM)
            return false;
        if (Rnd.chance(calcStat(skill.isMagic() ? Stats.REFLECT_MAGIC_SKILL : Stats.REFLECT_PHYSIC_SKILL, 0, attacker, skill))) {
            sendPacket(new SystemMessage2(SystemMsg.YOU_COUNTERED_C1S_ATTACK).addName(attacker));
            attacker.sendPacket(new SystemMessage2(SystemMsg.C1_DODGES_THE_ATTACK).addName(this));
            return true;
        }
        return false;
    }

    public void doCounterAttack(Skill skill, Creature attacker, boolean blow) {
        if (isDead()) // if the character is already dead, counter should not be
            return;
        if (isDamageBlocked() || attacker.isDamageBlocked()) // Не контратакуем, если есть неуязвимость, иначе она может отмениться
            return;
        if (skill == null || skill.hasEffects() || skill.isMagic() || !skill.isOffensive || skill.castRange > 200)
            return;
        if (Rnd.chance(calcStat(Stats.COUNTER_ATTACK, 0, attacker, skill))) {
            double damage = 1189. * getPAtk(attacker) / Math.max(attacker.getPDef(this), 1);
            attacker.sendPacket(new SystemMessage2(SystemMsg.C1_IS_PERFORMING_A_COUNTERATTACK).addName(this));
            if (blow) {// урон х2 для отражения blow скиллов
                sendPacket(new SystemMessage2(SystemMsg.C1_IS_PERFORMING_A_COUNTERATTACK).addName(this));
                sendPacket(new SystemMessage2(SystemMsg.C1_HAS_GIVEN_C2_DAMAGE_OF_S3).addName(this).addName(attacker).addInteger((long) damage));
                attacker.reduceCurrentHp(damage, this, skill, true, true, false, false, false, false, true);
            } else
                sendPacket(new SystemMessage2(SystemMsg.C1_IS_PERFORMING_A_COUNTERATTACK).addName(this));
            sendPacket(new SystemMessage2(SystemMsg.C1_HAS_GIVEN_C2_DAMAGE_OF_S3).addName(this).addName(attacker).addInteger((long) damage));
            attacker.reduceCurrentHp(damage, this, skill, true, true, false, false, false, false, true);
        }
    }

    /**
     * Disable this skill id for the duration of the delay in milliseconds.
     *
     * @param delay (seconds * 1000)
     */
    public void disableSkill(Skill skill, long delay) {
        skillReuses.put(skill.hashCode(), new TimeStamp(skill, delay));
    }

    public abstract boolean isAutoAttackable(Creature attacker);

    public void doAttack(Creature target) {
        if (target == null || isAMuted() || isAttackingNow() || isAlikeDead() || target.isAlikeDead() || !isInRange(target, 2000) || isPlayer() && getPlayer().isInMountTransform())
            return;

        if (target.isInvisible() && getAI() instanceof DefaultAI) {
            getAI().notifyEvent(CtrlEvent.EVT_THINK);
            return;
        }

        getListeners().onAttack(target);

        final WeaponTemplate weaponItem = getActiveWeaponItem();

        // Get the Attack Speed of the L2Character (delay (in milliseconds) before next attack)
        final int sAtk = calculateTimeBetweenAttacks(weaponItem);
        final int timeToHit = sAtk / 2;

        // Get the Attack Reuse Delay of the L2Weapon
        final int reuse = calculateReuseTime(target, weaponItem);

        final int ssGrade = (weaponItem != null ? weaponItem.getCrystalType().externalOrdinal : 0);

        // Reuse time
        _attackReuseEndTime = System.currentTimeMillis() + sAtk + reuse - 10;

        // Ready to act
        ThreadPoolManager.INSTANCE.schedule(new NotifyAITask(this, CtrlEvent.EVT_READY_TO_ACT), sAtk + reuse);

        // DS: adjusted by 1/100 of a second since the AI task is called with a small error
        // Especially on slower machines and is broken by autoattacks isAttackingNow () == true
        _attackEndTime = sAtk + System.currentTimeMillis() - 10;
        isAttackAborted = false;

        Attack attack = new Attack(this, target, getChargedSoulShot(), ssGrade);

        setHeading(PositionUtils.calculateHeadingFrom(this, target));

        // Select the type of attack to
        if (weaponItem == null)
            doAttackHitSimple(attack, target, 1., !isPlayer(), timeToHit, true);
        else
            switch (weaponItem.getItemType()) {
                case BOW:
                case CROSSBOW:
                    // Gauge
                    sendPacket(new SetupGauge(this, SetupGauge.RED, sAtk + reuse));

                    doAttackHitByBow(attack, target, timeToHit);
                    break;
                case POLE:
                    doAttackHitByPole(attack, target, timeToHit);
                    break;
                case DUAL:
                case DUALFIST:
                case DUALDAGGER:
                    doAttackHitByDual(attack, target, timeToHit);
                    break;
                default:
                    doAttackHitSimple(attack, target, 1., true, timeToHit, true);
            }

        if (attack.hasHits())
            broadcastPacket(attack);
    }

    private int calculateTimeBetweenAttacks(WeaponTemplate weapon) {
        if (weapon != null) {
            switch (weapon.getItemType()) {
                case BOW:
                    return (1500 * 345) / getPAtkSpd();
                case CROSSBOW:
                    return (1200 * 345) / getPAtkSpd();
            }
        }
        return Formulas.calcPAtkSpd(getPAtkSpd());
    }

    private int calculateReuseTime(Creature target, WeaponTemplate weapon) {
        if (weapon == null) {
            return 0;
        }

        int reuse = weapon.getAttackReuseDelay();
        // only bows should continue for now
        if (reuse == 0) {
            return 0;
        }

        reuse *= getReuseModifier(target);
        double atkSpd = getPAtkSpd();
        switch (weapon.getItemType()) {
            case BOW:
            case CROSSBOW:
                return (int) ((reuse * 345) / atkSpd);
            default:
                return (int) ((reuse * 312) / atkSpd);
        }
    }

    private void doAttackHitSimple(Attack attack, Creature target, double multiplier, boolean unchargeSS, int sAtk, boolean notify) {
        int damage1 = 0;
        boolean shld1 = false;
        boolean crit1 = false;
        boolean miss1 = Formulas.calcHitMiss(this, target);

        if (!miss1) {
            AttackInfo info = Formulas.calcPhysDam(this, target, null, false, false, attack._soulshot, false);
            damage1 = (int) (info.damage * multiplier);
            shld1 = info.shld;
            crit1 = info.crit;
        }

        ThreadPoolManager.INSTANCE.schedule(new HitTask(this, target, damage1, crit1, miss1, attack._soulshot, shld1, unchargeSS, notify), sAtk);

        attack.addHit(target, damage1, miss1, crit1, shld1);
    }

    private void doAttackHitByBow(Attack attack, Creature target, int sAtk) {
        WeaponTemplate activeWeapon = getActiveWeaponItem();
        if (activeWeapon == null)
            return;

        int damage1 = 0;
        boolean shld1 = false;
        boolean crit1 = false;

        // Calculate if hit is missed or not
        boolean miss1 = Formulas.calcHitMiss(this, target);

        reduceArrowCount();

        if (!miss1) {
            AttackInfo info = Formulas.calcPhysDam(this, target, null, false, false, attack._soulshot, false);
            damage1 = (int) info.damage;
            shld1 = info.shld;
            crit1 = info.crit;

            int range = activeWeapon.getAttackRange();
            damage1 *= Math.min(range, getDistance(target)) / range * .4 + 0.8; // разброс 20% в обе стороны
        }

        ThreadPoolManager.INSTANCE.schedule(new HitTask(this, target, damage1, crit1, miss1, attack._soulshot, shld1, true, true), sAtk);

        attack.addHit(target, damage1, miss1, crit1, shld1);
    }

    private void doAttackHitByDual(Attack attack, Creature target, int sAtk) {
        int damage1 = 0;
        int damage2 = 0;
        boolean shld1 = false;
        boolean shld2 = false;
        boolean crit1 = false;
        boolean crit2 = false;

        boolean miss1 = Formulas.calcHitMiss(this, target);
        boolean miss2 = Formulas.calcHitMiss(this, target);

        if (!miss1) {
            AttackInfo info = Formulas.calcPhysDam(this, target, null, true, false, attack._soulshot, false);
            damage1 = (int) info.damage;
            shld1 = info.shld;
            crit1 = info.crit;
        }

        if (!miss2) {
            AttackInfo info = Formulas.calcPhysDam(this, target, null, true, false, attack._soulshot, false);
            damage2 = (int) info.damage;
            shld2 = info.shld;
            crit2 = info.crit;
        }

        // Create a new hit task with Medium priority for hit 1 and for hit 2 with a higher delay
        ThreadPoolManager.INSTANCE.schedule(new HitTask(this, target, damage1, crit1, miss1, attack._soulshot, shld1, true, false), sAtk / 2);
        ThreadPoolManager.INSTANCE.schedule(new HitTask(this, target, damage2, crit2, miss2, attack._soulshot, shld2, false, true), sAtk);

        attack.addHit(target, damage1, miss1, crit1, shld1);
        attack.addHit(target, damage2, miss2, crit2, shld2);
    }

    private void doAttackHitByPole(Attack attack, Creature target, int sAtk) {
        int angle = (int) calcStat(Stats.POLE_ATTACK_ANGLE, 90, target, null);
        int range = (int) calcStat(Stats.POWER_ATTACK_RANGE, getTemplate().baseAtkRange, target, null);

        // Используем Math.round т.к. обычный кастинг обрезает к меньшему
        // double d = 2.95. int i = (int)d, выйдет что i = 2
        // если 1% угла или 1 дистанции не играет огромной роли, то для
        // количества целей это критично
        int attackcountmax = (int) Math.round(calcStat(Stats.POLE_TARGET_COUNT, 0, target, null));

        if (isBoss())
            attackcountmax += 27;
        else if (isRaid())
            attackcountmax += 12;
        else if (isMonster() && getLevel() > 0)
            attackcountmax += getLevel() / 7.5;

        double mult = 1.;
        _poleAttackCount = 1;

        if (!isInZonePeace())// Guard with a lance will attack only the single target in
            for (Creature t : getAroundCharacters(range, 200).collect(Collectors.toList()))
                if (_poleAttackCount <= attackcountmax) {
                    if (t == target || t.isDead() || !PositionUtils.isFacing(this, t, angle))
                        continue;

                    if (t.isAutoAttackable(this)) {
                        doAttackHitSimple(attack, t, mult, false, sAtk, false);
                        mult *= BalancerConfig.ALT_POLE_DAMAGE_MODIFIER;
                        _poleAttackCount++;
                    }
                } else
                    break;

        _poleAttackCount = 0;
        doAttackHitSimple(attack, target, 1., true, sAtk, true);
    }

    public long getAnimationEndTime() {
        return _animationEndTime;
    }

    public void doCast(int skillId, int skillLvl, Creature target, boolean forceUse) {
        doCast(SkillTable.INSTANCE.getInfo(skillId, skillLvl), target, forceUse);
    }

    public void doCast(int skillId, Creature target, boolean forceUse) {
        doCast(SkillTable.INSTANCE.getInfo(skillId), target, forceUse);
    }

    public void doCast(Skill skill, Creature target, boolean forceUse) {
        if (skill == null)
            return;
        List<Integer> itemConsume = skill.itemConsume;

        if (itemConsume.get(0) > 0)
            for (int i = 0; i < itemConsume.size(); i++)
                if (!consumeItem(skill.getItemConsumeId().get(i), itemConsume.get(i))) {
                    sendPacket(skill.isItemHandler() ? SystemMsg.INCORRECT_ITEM_COUNT : SystemMsg.THERE_ARE_NOT_ENOUGH_NECESSARY_ITEMS_TO_USE_THE_SKILL);
                    return;
                }

        if (skill.referenceItemId > 0)
            if (!consumeItemMp(skill.referenceItemId, skill.getReferenceItemMpConsume()))
                return;

        int magicId = skill.id;

        if (target == null)
            target = skill.getAimingTarget(this, getTarget());
        if (target == null)
            return;

        getListeners().onMagicUse(skill, target, false);

        if (this != target)
            setHeading(PositionUtils.calculateHeadingFrom(this, target));

        int level = Math.max(1, getSkillDisplayLevel(magicId));

        int skillTime = skill.isSkillTimePermanent() ? skill.hitTime : Formulas.calcMAtkSpd(this, skill, skill.hitTime);
        int skillInterruptTime = skill.isMagic() ? Formulas.calcMAtkSpd(this, skill, skill.skillInterruptTime) : 0;

        int minCastTime = Math.min(BalancerConfig.SKILLS_CAST_TIME_MIN, skill.hitTime);
        if (skillTime < minCastTime) {
            skillTime = minCastTime;
            skillInterruptTime = 0;
        }

        _animationEndTime = System.currentTimeMillis() + skillTime;

        if (skill.isMagic() && !skill.isSkillTimePermanent() && getChargedSpiritShot() > 0) {
            skillTime = (int) (0.70 * skillTime);
            skillInterruptTime = (int) (0.70 * skillInterruptTime);
        }

        Formulas.calcSkillMastery(skill, this); // Calculate skill mastery for current cast
        long reuseDelay = Math.max(0, Formulas.calcSkillReuseDelay(this, skill));

        broadcastPacket(new MagicSkillUse(this, target, skill.displayId, level, skillTime, reuseDelay));

        if (!skill.isItemHandler())
            disableSkill(skill, reuseDelay);

        if (isPlayer())
            if (skill.skillType == SkillType.PET_SUMMON)
                sendPacket(SystemMsg.SUMMONING_YOUR_PET);
            else if (!skill.isItemHandler())
                sendPacket(new SystemMessage2(SystemMsg.YOU_USE_S1).addSkillName(magicId, level));
            else
                sendPacket(new SystemMessage2(SystemMsg.YOU_USE_S1).addItemName(skill.getItemConsumeId().get(0)));

        if (skill.targetType == SkillTargetType.TARGET_HOLY)
            target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, this, 1);

        switch (skill.getFlyType()) {
            case DUMMY:
            case CHARGE:
                Location flyLoc = getFlyLocation(target, skill);
                if (flyLoc != null) {
                    _flyLoc = flyLoc;
                } else {
                    sendPacket(SystemMsg.CANNOT_SEE_TARGET);
                    return;
                }
                break;
            default:
                _flyLoc = null;
                break;
        }

        double mpConsume1 = skill.isUsingWhileCasting ? skill.getMpConsume() : skill.mpConsume1;
        if (mpConsume1 > 0) {
            if (_currentMp < mpConsume1) {
                sendPacket(SystemMsg.NOT_ENOUGH_MP);
                onCastEndTime();
                return;
            }
            reduceCurrentMp(mpConsume1, null);
        }

        if (_flyLoc != null) {
            broadcastPacket(new FlyToLocation(this, _flyLoc, skill.getFlyType()));
        }

        _castingSkill = skill;
        _castInterruptTime = System.currentTimeMillis() + skillInterruptTime;
        setCastingTarget(target);

        if (skill.isUsingWhileCasting)
            callSkill(skill, skill.getTargets(this, target, forceUse), true);

        if (isPlayer())
            sendPacket(new SetupGauge(this, SetupGauge.BLUE, skillTime));

        _scheduledCastCount = skill.castCount;
        _scheduledCastInterval = skill.castCount > 0 ? skillTime / _scheduledCastCount : skillTime;

        // Create a task MagicUseTask with Medium priority to launch the MagicSkill at the end of the casting time
        _skillLaunchedTask = ThreadPoolManager.INSTANCE.schedule(new MagicLaunchedTask(this, forceUse), skillInterruptTime);
        _skillTask = ThreadPoolManager.INSTANCE.schedule(new MagicUseTask(this, forceUse), skill.castCount > 0 ? skillTime / skill.castCount : skillTime);

        skillGeoCheckTask = null;
        if ((skill.castRange < 32767) && (skill.skillType != SkillType.TAKECASTLE) && (skill.skillType != SkillType.TAKEFORTRESS) && (_scheduledCastInterval > 600)) {
            skillGeoCheckTask = ThreadPoolManager.INSTANCE.schedule(new MagicGeoCheckTask(this), (long) (_scheduledCastInterval * 0.5));
        }
    }

    public void addReceivedDebuff(int skillId, long period) {
        if (!Config.DEBUFF_PROTECTION_SYSTEM)
            return;

        Long[] debuffInfo = new Long[2];
        debuffInfo[0] = System.currentTimeMillis() + period;// Time of Debuff Effect End
        debuffInfo[1] = period;
        receivedDebuffs.put(skillId, debuffInfo);
    }

    public double getReceivedDebuffMod(int skillId, double currentChance) {
        if (!Config.DEBUFF_PROTECTION_SYSTEM)
            return 1.0;

        if (currentChance >= 100.0)
            return 1.0;

        if (!receivedDebuffs.containsKey(skillId))
            return 1.0;

        long dateOfDebuffEnd = receivedDebuffs.get(skillId)[0];
        double lastDebuffPeriod = receivedDebuffs.get(skillId)[1] / 1000.;

        if (dateOfDebuffEnd == 0L)
            return 1.0;

        long secondsDifference = (System.currentTimeMillis() - dateOfDebuffEnd) / 1000L;

        if (secondsDifference <= 0L)
            return 0.0;

        return Math.min(1.0, (currentChance / 100.0) * (secondsDifference / lastDebuffPeriod));
    }

    private Location getFlyLocation(GameObject target, Skill skill) {
        if (target != null && target != this) {
            Location loc;

            double radian = PositionUtils.convertHeadingToRadian(target.getHeading());
            if (skill.isFlyToBack())
                loc = new Location(target.getX() + (int) (Math.sin(radian) * 40), target.getY() - (int) (Math.cos(radian) * 40), target.getZ());
            else
                loc = new Location(target.getX() - (int) (Math.sin(radian) * 40), target.getY() + (int) (Math.cos(radian) * 40), target.getZ());

            if (isFlying()) {
                if (isPlayer() && ((Player) this).isInFlyingTransform() && (loc.z <= 0 || loc.z >= 6000))
                    return null;
                if (GeoEngine.moveCheckInAir(getX(), getY(), getZ(), loc.x, loc.y, loc.z, getColRadius(), getGeoIndex()) == null)
                    return null;
            } else {
                loc.correctGeoZ();

                if (!GeoEngine.canMoveToCoord(this, loc)) {
                    loc = target.getLoc(); // Если не получается встать рядом с объектом, пробуем встать прямо в него
                    if (!GeoEngine.canMoveToCoord(this, loc))
                        return null;
                }
            }

            return loc;
        }

        double radian = PositionUtils.convertHeadingToRadian(getHeading());
        int x1 = -(int) (Math.sin(radian) * skill.flyRadius);
        int y1 = (int) (Math.cos(radian) * skill.flyRadius);

        if (isFlying())
            return GeoEngine.moveCheckInAir(getX(), getY(), getZ(), getX() + x1, getY() + y1, getZ(), getColRadius(), getGeoIndex());
        return GeoEngine.moveCheck(getX(), getY(), getZ(), getX() + x1, getY() + y1, getGeoIndex());
    }

    public final void doDie(Creature killer) {
        // killing is only possible one time
        if (!isDead.compareAndSet(false, true))
            return;
        onDeath(killer);
    }

    protected void onDeath(Creature killer) {
        if (killer != null) {
            Player killerPlayer = killer.getPlayer();
            if (killerPlayer != null)
                killerPlayer.getListeners().onKillIgnorePetOrSummon(this);

            killer.getListeners().onKill(this);

            if (isPlayer() && killer.isPlayable())
                currentCp = 0;
        }

        setTarget(null);
        stopMove();
        stopAttackStanceTask();
        stopRegeneration();

        _currentHp = 0;

        // unsummon pet when player dies
        //if (isPlayer() && getPlayer().getPet() != null)
        //{
        //   getPlayer().getPet().unSummon();
        // }

        boolean fightClubKeepBuffs = isPlayable();
        // Stop all active skills effects in progress on the L2Character
        if (isBlessedByNoblesse() || isSalvation() || fightClubKeepBuffs) {
            if (isSalvation() && isPlayer() && !getPlayer().isInOlympiadMode()) {
                getPlayer().reviveRequest(getPlayer(), 100, false);
            }
            getEffectList().getAllEffects()
                    .forEach(e -> {
                        if (e.getEffectType() == EffectType.BlessNoblesse
                                || e.getSkill().id == 1325
                                || e.getSkill().id == Skill.SKILL_RAID_BLESSING)
                            e.exit();
                        else if (e.getEffectType() == EffectType.AgathionResurrect && !isPlayable()) {
                            if (isPlayer())
                                getPlayer().setAgathionRes(true);
                            e.exit();
                        }
                    });
        } else {
            getEffectList().getAllEffects()
                    .filter(e -> e.getEffectType() != EffectType.Transformation && !e.getSkill().isPreservedOnDeath)
                    .forEach(Effect::exit);
        }

        ThreadPoolManager.INSTANCE.execute(new NotifyAITask(this, CtrlEvent.EVT_DEAD, killer));

        getListeners().onDeath(killer);

        updateEffectIcons();
        updateStats();
        broadcastStatusUpdate();
    }

    protected void onRevive() {

    }

    public void enableSkill(Skill skill) {
        skillReuses.remove(skill.hashCode());
    }

    /**
     * Return a map of 32 bits (0x00000000) containing all abnormal effects
     */
    public int getAbnormalEffect() {
        return abnormalEffects;
    }

    /**
     * Return a map of 32 bits (0x00000000) containing all special effects
     */
    public int getAbnormalEffect2() {
        return abnormalEffects2;
    }

    /**
     * Return a map of 32 bits (0x00000000) containing all event effects
     */
    public int getAbnormalEffect3() {
        return _abnormalEffects3;
    }

    public int getAccuracy() {
        return (int) calcStat(Stats.ACCURACY_COMBAT, 0, null, null);
    }

    public Collection<Skill> getAllSkills() {
        return skills.values();
    }

    public final double getAttackSpeedMultiplier() {
        return 1.1 * getPAtkSpd() / getTemplate().basePAtkSpd;
    }

    public int getBuffLimit() {
        return (int) calcStat(Stats.BUFF_LIMIT, Config.ALT_BUFF_LIMIT, null, null);
    }

    public Skill getCastingSkill() {
        return _castingSkill;
    }

    public int getCON() {
        return (int) calcStat(Stats.STAT_CON, template.baseCON, null, null);
    }

    /**
     * Возвращает шанс физического крита (1000 == 100%)
     */
    public int getCriticalHit(Creature target, Skill skill) {
        return (int) calcStat(Stats.CRITICAL_BASE, template.baseCritRate, target, skill);
    }

    /**
     * Возвращает шанс магического крита в процентах
     */
    public double getMagicCriticalRate(Creature target, Skill skill) {
        return calcStat(Stats.MCRITICAL_RATE, target, skill);
    }

    public final double getCurrentCp() {
        return currentCp;
    }

    public final void setCurrentCp(double newCp) {
        setCurrentCp(newCp, true);
    }

    public final double getCurrentCpRatio() {
        return currentCp / getMaxCp();
    }

    public final double getCurrentCpPercents() {
        return getCurrentCpRatio() * 100.;
    }

    public final boolean isCurrentCpZero() {
        return currentCp < 1;
    }

    public final double getCurrentHp() {
        return _currentHp;
    }

    public final double getCurrentHpRatio() {
        return getCurrentHp() / getMaxHp();
    }

    public final double getCurrentHpPercents() {
        return getCurrentHpRatio() * 100.;
    }

    public final boolean isCurrentHpFull() {
        return getCurrentHp() >= getMaxHp();
    }

    public final double getCurrentMp() {
        return _currentMp;
    }

    public final void setCurrentMp(double newMp) {
        setCurrentMp(newMp, true);
    }

    public final double getCurrentMpRatio() {
        return getCurrentMp() / getMaxMp();
    }

    public final double getCurrentMpPercents() {
        return getCurrentMpRatio() * 100.;
    }

    public Location getDestination() {
        return destination;
    }

    public int getDEX() {
        return (int) calcStat(Stats.STAT_DEX, template.baseDEX, null, null);
    }

    public int getEvasionRate(Creature target) {
        return (int) calcStat(Stats.EVASION_RATE, 0, target, null);
    }

    public int getINT() {
        return (int) calcStat(Stats.STAT_INT, template.baseINT, null, null);
    }

    public Stream<Creature> getAroundCharacters(int radius, int height) {
        if (!isVisible())
            return Stream.empty();
        return World.getAroundCharacters(this, radius, height);
    }

    public Stream<NpcInstance> getAroundNpc(int range, int height) {
        if (!isVisible())
            return Stream.empty();
        return World.getAroundNpc(this, range, height);
    }

    public boolean knowsObject(GameObject obj) {
        return World.getAroundObjectById(this, obj.getObjectId()) != null;
    }

    public final Skill getKnownSkill(int skillId) {
        return skills.get(skillId);
    }

    public final int getMagicalAttackRange(Skill skill) {
        if (skill != null)
            return (int) calcStat(Stats.MAGIC_ATTACK_RANGE, skill.castRange, null, skill);
        return getTemplate().baseAtkRange;
    }

    public int getMAtk(Creature target, Skill skill) {
        if (skill != null && skill.mAttack > 0)
            return skill.mAttack;
        return (int) calcStat(Stats.MAGIC_ATTACK, template.baseMAtk, target, skill);
    }

    public int getMAtkSpd() {
        return (int) (calcStat(Stats.MAGIC_ATTACK_SPEED, template.baseMAtkSpd, null, null));
    }

    public final int getMaxCp() {
        return (int) calcStat(Stats.MAX_CP, template.baseCpMax, null, null);
    }

    public int getMaxHp() {
        return (int) calcStat(Stats.MAX_HP, template.baseHpMax, null, null);
    }

    public int getMaxMp() {
        return (int) calcStat(Stats.MAX_MP, template.baseMpMax, null, null);
    }

    public int getMDef(Creature target, Skill skill) {
        return Math.max((int) calcStat(Stats.MAGIC_DEFENCE, template.baseMDef, target, skill), 1);
    }

    public int getMEN() {
        return (int) calcStat(Stats.STAT_MEN, template.baseMEN, null, null);
    }

    public double getMinDistance(GameObject obj) {
        double distance = getTemplate().collisionRadius;

        if (obj != null && obj.isCreature())
            distance += ((Creature) obj).getTemplate().collisionRadius;

        return distance;
    }

    public double getMovementSpeedMultiplier() {
        return getRunSpeed() * 1. / template.baseRunSpd;
    }

    @Override
    public int getMoveSpeed() {
        if (isRunning())
            return getRunSpeed();

        return getWalkSpeed();
    }

    @Override
    public String getName() {
        return StringUtils.defaultString(name);
    }

    public final Creature setName(String name) {
        this.name = name;
        return this;
    }

    public int getPAtk(Creature target) {
        return (int) calcStat(Stats.POWER_ATTACK, template.basePAtk, target, null);
    }

    public int getPAtkSpd() {
        return (int) calcStat(Stats.POWER_ATTACK_SPEED, template.basePAtkSpd, null, null);
    }

    public int getPDef(Creature target) {
        return (int) calcStat(Stats.POWER_DEFENCE, template.basePDef, target, null);
    }

    public final int getPhysicalAttackRange() {
        return (int) calcStat(Stats.POWER_ATTACK_RANGE, getTemplate().baseAtkRange, null, null);
    }

    public final int getRandomDamage() {
        WeaponTemplate weaponItem = getActiveWeaponItem();
        if (weaponItem == null)
            return 5 + (int) Math.sqrt(getLevel());
        return weaponItem.getRandomDamage();
    }

    private double getReuseModifier(Creature target) {
        return calcStat(Stats.ATK_REUSE, 1, target, null);
    }

    public int getRunSpeed() {
        return getSpeed(template.baseRunSpd);
    }

    public final int getShldDef() {
        if (isPlayer())
            return (int) calcStat(Stats.SHIELD_DEFENCE, 0, null, null);
        return (int) calcStat(Stats.SHIELD_DEFENCE, template.baseShldDef, null, null);
    }

    public final int getSkillDisplayLevel(Integer skillId) {
        Skill skill = skills.get(skillId);
        if (skill == null)
            return -1;
        return skill.getDisplayLevel();
    }

    public final int getSkillLevel(Integer skillId) {
        return getSkillLevel(skillId, -1);
    }

    public final int getSkillLevel(Integer skillId, int def) {
        Skill skill = skills.get(skillId);
        if (skill == null)
            return def;
        return skill.level;
    }

    public int getSkillMastery(Integer skillId) {
        if (_skillMastery == null)
            return 0;
        Integer val = _skillMastery.get(skillId);
        return val == null ? 0 : val;
    }

    public void removeSkillMastery(Integer skillId) {
        if (_skillMastery != null)
            _skillMastery.remove(skillId);
    }

    protected int getSpeed(int baseSpeed) {
        if (isInWater())
            return getSwimSpeed();
        return (int) calcStat(Stats.RUN_SPEED, baseSpeed, null, null);
    }

    public int getSTR() {
        return (int) calcStat(Stats.STAT_STR, template.baseSTR, null, null);
    }

    public int getSwimSpeed() {
        return (int) calcStat(Stats.RUN_SPEED, Config.SWIMING_SPEED, null, null);
    }

    public GameObject getTarget() {
        return target.get();
    }

    public void setTarget(GameObject object) {
        if (object != null && !object.isVisible())
            object = null;

        if (object == null)
            target = HardReferences.emptyRef();
        else
            target = object.getRef();
    }

    public final int getTargetId() {
        GameObject target = getTarget();
        return target == null ? -1 : target.getObjectId();
    }

    public CharTemplate getTemplate() {
        return template;
    }

    public CharTemplate getBaseTemplate() {
        return baseTemplate;
    }

    public String getTitle() {
        return StringUtils.defaultString(title);
    }

    public Creature setTitle(String title) {
        this.title = title;
        return this;
    }

    public final int getWalkSpeed() {
        if (isInWater())
            return getSwimSpeed();
        return getSpeed(template.baseWalkSpd);
    }

    public int getWIT() {
        return (int) calcStat(Stats.STAT_WIT, template.baseWIT, null, null);
    }

    public double headingToRadians(int heading) {
        return (heading - 32768) / HEADINGS_IN_PI;
    }

    public boolean isAlikeDead() {
        return _fakeDeath || isDead();
    }

    public final boolean isAttackingNow() {
        return _attackEndTime > System.currentTimeMillis();
    }

    private boolean isBlessedByNoblesse() {
        return isblessedbynoblesse;
    }

    final boolean isSalvation() {
        return _isSalvation;
    }

    public boolean isEffectImmune() {
        return effectImmunity.get();
    }

    public boolean isBuffImmune() {
        return buffImmunity.get();
    }

    public boolean isDebuffImmune() {
        return debuffImmunity.get();
    }

    public boolean isDead() {
        return _currentHp < 0.5 || isDead.get();
    }

    @Override
    public final boolean isFlying() {
        return _flying;
    }

    public final void setFlying(boolean mode) {
        _flying = mode;
    }

    /**
     * Находится ли персонаж в боевой позе
     *
     * @return true, если персонаж в боевой позе, атакован или атакует
     */
    public final boolean isInCombat() {
        return System.currentTimeMillis() < _stanceEndTime;
    }

    public boolean isInvul() {
        return invul;
    }

    public void setInvul(boolean invul) {
        this.invul = invul;
    }

    public boolean isMageClass() {
        return getTemplate().baseMAtk > 3;
    }

    public final boolean isRunning() {
        return _running;
    }

    public final long getReuseDelay() {
        return reuseDelay;
    }

    public final void setReuseDelay(long newReuseDelay) {
        reuseDelay = (newReuseDelay + System.currentTimeMillis());
    }

    public boolean isSkillDisabled(Skill skill) {
        TimeStamp sts = skillReuses.get(skill.hashCode());
        if (sts == null)
            return false;
        if (sts.hasNotPassed())
            return true;
        skillReuses.remove(skill.hashCode());
        return false;
    }

    public final boolean isTeleporting() {
        return isTeleporting.get();
    }

    private Location getIntersectionPoint(Creature target) {
        if (!PositionUtils.isFacing(this, target, 90))
            return new Location(target.getX(), target.getY(), target.getZ());
        double angle = PositionUtils.convertHeadingToDegree(target.getHeading()); // угол в градусах
        double radian = Math.toRadians(angle - 90); // угол в радианах
        double range = target.getMoveSpeed() / 2.; // расстояние, пройденное за 1 секунду, равно скорости. Берем половину.
        return new Location((int) (target.getX() - range * Math.sin(radian)), (int) (target.getY() + range * Math.cos(radian)), target.getZ());
    }

    public Location applyOffset(Location point, int offset) {
        if (offset <= 0)
            return point;

        long dx = point.x - getX();
        long dy = point.y - getY();
        long dz = point.z - getZ();

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance <= offset) {
            point.set(getX(), getY(), getZ());
            return point;
        }

        if (distance >= 1) {
            double cut = offset / distance;
            point.x -= (int) (dx * cut + 0.5);
            point.y -= (int) (dy * cut + 0.5);
            point.z -= (int) (dz * cut + 0.5);

            if (!isFlying() && !isInBoat() && !isInWater() && !isBoat())
                point.correctGeoZ();
        }

        return point;
    }

    private List<Location> applyOffset(List<Location> points, int offset) {
        offset = offset >> 4;
        if (offset <= 0)
            return points;

        long dx = points.get(points.size() - 1).x - points.get(0).x;
        long dy = points.get(points.size() - 1).y - points.get(0).y;
        long dz = points.get(points.size() - 1).z - points.get(0).z;

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance <= offset) {
            Location point = points.get(0);
            points.clear();
            points.add(point);
            return points;
        }

        if (distance >= 1) {
            double cut = offset / distance;
            int num = (int) (points.size() * cut + 0.5);
            for (int i = 1; i <= num && points.size() > 0; i++)
                points.remove(points.size() - 1);
        }

        return points;
    }

    private boolean setSimplePath(Location dest) {
        List<Location> moveList = GeoMove.constructMoveList(getLoc(), dest);
        if (moveList.isEmpty())
            return false;
        _targetRecorder.clear();
        _targetRecorder.add(moveList);
        return true;
    }

    private boolean buildPathTo(int x, int y, int z, int offset, boolean pathFind) {
        return buildPathTo(x, y, z, offset, null, false, pathFind);
    }

    private boolean buildPathTo(int x, int y, int z, int offset, Creature follow, boolean forestalling, boolean pathFind) {
        int geoIndex = getGeoIndex();

        Location dest;

        if (forestalling && follow != null && follow.isMoving)
            dest = getIntersectionPoint(follow);
        else
            dest = new Location(x, y, z);

        if (isInBoat() || isBoat() || !Config.ALLOW_GEODATA) {
            applyOffset(dest, offset);
            return setSimplePath(dest);
        }

        if (isFlying() || isInWater()) {
            applyOffset(dest, offset);

            Location nextloc;

            if (isFlying()) {
                if (GeoEngine.canSeeCoord(this, dest.x, dest.y, dest.z, true))
                    return setSimplePath(dest);

                nextloc = GeoEngine.moveCheckInAir(getX(), getY(), getZ(), dest.x, dest.y, dest.z, getColRadius(), geoIndex);
                if (nextloc != null && !nextloc.equals(getX(), getY(), getZ()))
                    return setSimplePath(nextloc);
            } else {
                int waterZ = getWaterZ();
                nextloc = GeoEngine.moveInWaterCheck(getX(), getY(), getZ(), dest.x, dest.y, dest.z, waterZ, geoIndex);
                if (nextloc == null)
                    return false;

                List<Location> moveList = GeoMove.constructMoveList(getLoc(), nextloc.clone());
                _targetRecorder.clear();
                if (!moveList.isEmpty())
                    _targetRecorder.add(moveList);

                int dz = dest.z - nextloc.z;
                // если пытаемся выбратся на берег, считаем путь с точки выхода до точки назначения
                if (dz > 0 && dz < 128) {
                    moveList = GeoEngine.MoveList(nextloc.x, nextloc.y, nextloc.z, dest.x, dest.y, geoIndex, false);
                    // try
                    // {
                    // moveList = GeoEngine.MoveList(nextloc.x, nextloc.y, nextloc.z, dest.x, dest.y, geoIndex, false);
                    // }
                    // catch (Exception e)
                    // {
                    // if (_moveExceptionsThrown < 3)
                    // _log.error("Building path has failed for character: " + this + " at location " + getTerritory() + ". Character will be teleported automatically nearby.", e);
                    //
                    // _moveExceptionsThrown++;
                    // teleToLocation(GeoEngine.moveCheck(getX(), getY(), getZ(), dest.x, dest.y, geoIndex));
                    // return false;
                    // }
                    if (moveList != null) // null - до конца пути дойти нельзя
                    {
                        if (!moveList.isEmpty()) // уже стоим на нужной клетке
                            _targetRecorder.add(moveList);
                    }
                }

                return !_targetRecorder.isEmpty();
            }
            return false;
        }

        List<Location> moveList = GeoEngine.MoveList(getX(), getY(), getZ(), dest.x, dest.y, geoIndex, true); // onlyFullPath = true - проверяем весь путь до конца

        if (moveList != null) // null - до конца пути дойти нельзя
        {
            if (moveList.isEmpty()) // уже стоим на нужной клетке
                return false;
            applyOffset(moveList, offset);
            if (moveList.isEmpty()) // уже стоим на нужной клетке
                return false;
            _targetRecorder.clear();
            _targetRecorder.add(moveList);
            return true;
        }

        if (pathFind) {
            List<List<Location>> targets = GeoMove.findMovePath(getX(), getY(), getZ(), dest.clone(), this, true, geoIndex);
            if (!targets.isEmpty()) {
                moveList = targets.remove(targets.size() - 1);
                applyOffset(moveList, offset);
                if (!moveList.isEmpty())
                    targets.add(moveList);
                if (!targets.isEmpty()) {
                    _targetRecorder.clear();
                    _targetRecorder.addAll(targets);
                    return true;
                }
            }
        }

        if (follow != null)
            return false;

        applyOffset(dest, offset);

        moveList = GeoEngine.MoveList(getX(), getY(), getZ(), dest.x, dest.y, geoIndex, false); // onlyFullPath = false - идем до куда можем
        if (moveList != null && !moveList.isEmpty()) // null - нет геодаты, empty - уже стоим на нужной клетке
        {
            _targetRecorder.clear();
            _targetRecorder.add(moveList);
            return true;
        }

        return false;
    }

    public Creature getFollowTarget() {
        return followTarget.get();
    }

    public void setFollowTarget(Creature target) {
        followTarget = target == null ? HardReferences.emptyRef() : target.getRef();
    }

    public boolean followToCharacter(Creature target, int offset, boolean forestalling) {
        return followToCharacter(target.getLoc(), target, offset, forestalling);
    }

    public boolean followToCharacter(Location loc, Creature target, int offset, boolean forestalling) {
        moveLock.lock();
        try {
            if (isMovementDisabled() || target == null || isInBoat() || target.isInvisible())
                return false;

            offset = Math.max(offset, 10);
            if (isFollow && target == getFollowTarget() && offset == _offset)
                return true;

            if (Math.abs(getZ() - target.getZ()) > 1000 && !isFlying()) {
                sendPacket(SystemMsg.CANNOT_SEE_TARGET);
                return false;
            }

            if (getAI() != null)
                getAI().clearNextAction();

            stopMove(false, false);

            if (buildPathTo(loc.x, loc.y, loc.z, offset, target, forestalling, !target.isDoor()))
                movingDestTempPos.set(loc.x, loc.y, loc.z);
            else
                return false;

            isMoving = true;
            isFollow = true;
            _forestalling = forestalling;
            _offset = offset;
            setFollowTarget(target);

            moveNext(true);

            return true;
        } finally {
            moveLock.unlock();
        }
    }

    public boolean moveToLocation(Location loc, int offset, boolean pathfinding) {
        return moveToLocation(loc.x, loc.y, loc.z, offset, pathfinding);
    }

    public boolean moveToLocation(int x_dest, int y_dest, int z_dest, int offset, boolean pathfinding) {
        moveLock.lock();
        try {
            offset = Math.max(offset, 0);
            Location dst_geoloc = new Location(x_dest, y_dest, z_dest).world2geo();
            if (isMoving && !isFollow && movingDestTempPos.equals(dst_geoloc)) {
                sendActionFailed();
                return true;
            }

            if (isMovementDisabled()) {
                getAI().setNextAction(nextAction.MOVE, new Location(x_dest, y_dest, z_dest), offset, pathfinding, false);
                sendActionFailed();
                return false;
            }

            getAI().clearNextAction();

            if (isPlayer())
                getAI().changeIntention(AI_INTENTION_ACTIVE);

            stopMove(false, false);

            if (buildPathTo(x_dest, y_dest, z_dest, offset, pathfinding))
                movingDestTempPos.set(dst_geoloc);
            else {
                sendActionFailed();
                return false;
            }

            isMoving = true;

            moveNext(true);

            return true;
        } finally {
            moveLock.unlock();
        }
    }

    private void moveNext(boolean firstMove) {
        if (!isMoving || isMovementDisabled()) {
            stopMove();
            return;
        }

        _previousSpeed = getMoveSpeed();
        if (_previousSpeed <= 0) {
            stopMove();
            return;
        }

        if (!firstMove) {
            Location dest = destination;
            if (dest != null)
                setLoc(dest, true);
        }

        if (_targetRecorder.isEmpty()) {
            CtrlEvent ctrlEvent = isFollow ? CtrlEvent.EVT_ARRIVED_TARGET : CtrlEvent.EVT_ARRIVED;
            stopMove(false, true);
            ThreadPoolManager.INSTANCE.execute(new NotifyAITask(this, ctrlEvent));
            return;
        }

        moveList = _targetRecorder.remove(0);
        Location begin = moveList.get(0).clone().geo2world();
        Location end = moveList.get(moveList.size() - 1).clone().geo2world();
        destination = end;
        double distance = (isFlying() || isInWater()) ? begin.distance3D(end) : begin.distance(end); // клиент при передвижении не учитывает поверхность

        if (distance != 0)
            setHeading(PositionUtils.calculateHeadingFrom(getX(), getY(), destination.x, destination.y));

        if (isPlayer())
            getPlayer().getCounters().distanceWalked += distance;

        broadcastMove();

        _startMoveTime = _followTimestamp = System.currentTimeMillis();
        if (_moveTaskRunnable == null)
            _moveTaskRunnable = new MoveNextTask();
        _moveTask = ThreadPoolManager.INSTANCE.schedule(_moveTaskRunnable.setDist(distance), getMoveTickInterval());
    }

    private int getMoveTickInterval() {
        return (isPlayer() ? 16000 : 32000) / Math.max(getMoveSpeed(), 1);
    }

    private void broadcastMove() {
        validateLocation(isPlayer() ? 2 : 1);
        broadcastPacket(movePacket());
    }

    /**
     * Останавливает движение и рассылает StopMove, ValidateLocation
     */
    public void stopMove() {
        stopMove(true, true);
    }

    /**
     * Останавливает движение и рассылает StopMove
     *
     * @param validate - рассылать ли ValidateLocation
     */
    public void stopMove(boolean validate) {
        stopMove(true, validate);
    }

    /**
     * Останавливает движение
     *
     * @param stop     - рассылать ли StopMove
     * @param validate - рассылать ли ValidateLocation
     */
    public void stopMove(boolean stop, boolean validate) {
        if (!isMoving)
            return;

        moveLock.lock();
        try {
            if (!isMoving)
                return;

            isMoving = false;
            isFollow = false;

            if (_moveTask != null) {
                _moveTask.cancel(false);
                _moveTask = null;
            }

            destination = null;
            moveList = null;

            _targetRecorder.clear();

            if (validate)
                validateLocation(isPlayer() ? 2 : 1);
            if (stop)
                broadcastPacket(stopMovePacket());
        } finally {
            moveLock.unlock();
        }
    }

    /**
     * Возвращает координаты поверхности воды, если мы находимся в ней, или над ней.
     */
    private int getWaterZ() {
        if (!isInWater())
            return Integer.MIN_VALUE;

        int waterZ = Integer.MIN_VALUE;
        zonesRead.lock();
        try {
            Zone zone;
            for (Zone _zone : zones) {
                zone = _zone;
                if (zone.getType() == ZoneType.water)
                    if (waterZ == Integer.MIN_VALUE || waterZ < zone.getTerritory().getZmax())
                        waterZ = zone.getTerritory().getZmax();
            }
        } finally {
            zonesRead.unlock();
        }

        return waterZ;
    }

    protected L2GameServerPacket stopMovePacket() {
        return new StopMove(this);
    }

    public L2GameServerPacket movePacket() {
        return new CharMoveToLocation(this);
    }

    void updateZones() {
        if (isInObserverMode())
            return;

        List<Zone> zones = isVisible() ? getCurrentRegion().getZones() : new CopyOnWriteArrayList<>();

        List<Zone> entering;
        List<Zone> leaving = new ArrayList<>();

        zonesWrite.lock();
        try {
            if (!this.zones.isEmpty()) {
                leaving = this.zones.stream()
                        .filter(z ->
                                // зоны больше нет в регионе, либо вышли за территорию зоны
                                (!zones.contains(z) || !z.checkIfInZone(getLoc(), getReflection())))
                        .collect(Collectors.toList());
                // Покинули зоны, убираем из списка зон персонажа
                this.zones.removeAll(leaving);
            }

            entering = zones.stream()
                    // в зону еще не заходили и зашли на территорию зоны
                    .filter(z -> !this.zones.contains(z))
                    .filter(z -> z.checkIfInZone(getLoc(), getReflection()))
                    .collect(Collectors.toList());

            // Вошли в зоны, добавим в список зон персонажа
            this.zones.addAll(entering);
        } finally {
            zonesWrite.unlock();
        }

        onUpdateZones(leaving, entering);
    }

    void onUpdateZones(List<Zone> leaving, List<Zone> entering) {
        leaving.forEach(z -> z.doLeave(this));
        entering.forEach(z -> z.doEnter(this));
    }

    public boolean isInZonePeace() {
        return isInZone(ZoneType.peace_zone) && !isInZoneBattle();
    }

    public boolean isInZoneBattle() {
        return isInZone(ZoneType.battle_zone);
    }

    /**
     * In this zone, players are not getting PvP Flags or Karma Neutral or Enemy players are Auto Attackable
     *
     * @return is Inside Valakas, Antharas or Baium zone
     */
    public boolean isInZonePvP() {
        return zones.stream()
                .anyMatch(zone -> zone.getTemplate().isEpicPvP());
    }

    public boolean isInWater() {
        return isInZone(ZoneType.water) && !(isInBoat() || isBoat() || isFlying());
    }

    public boolean isInZone(ZoneType type) {
        zonesRead.lock();
        try {
            return zones.stream().anyMatch(z -> z.getType() == type);
        } finally {
            zonesRead.unlock();
        }
    }

    public boolean isInZone(String name) {
        zonesRead.lock();
        try {
            return zones.stream().anyMatch(z -> z.getName().equals(name));
        } finally {
            zonesRead.unlock();
        }
    }

    public boolean isInZone(Zone zone) {
        zonesRead.lock();
        try {
            return zones.contains(zone);
        } finally {
            zonesRead.unlock();
        }
    }

    public synchronized Zone getZone(ZoneType type) {
        return zones.stream().filter(z -> z.getType() == type).findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public Location getRestartPoint() {
        zonesRead.lock();
        try {
            return zones.stream()
                    .filter(z -> z.getRestartPoints() != null)
                    .filter(z -> restart_zones.contains(z.getType()))
                    .map(Zone::getSpawn)
                    .findFirst().orElse(null);
        } finally {
            zonesRead.unlock();
        }
    }

    public Location getPKRestartPoint() {
        zonesRead.lock();
        try {
            return zones.stream()
                    .filter(z -> z.getRestartPoints() != null)
                    .filter(z -> restart_zones.contains(z.getType()))
                    .map(Zone::getPKSpawn)
                    .findFirst().orElse(null);
        } finally {
            zonesRead.unlock();
        }
    }

    @Override
    public int getGeoZ(Location loc) {
        if (isFlying() || isInWater() || isInBoat() || isBoat() || isDoor())
            return loc.z;

        return super.getGeoZ(loc);
    }

    boolean needStatusUpdate() {
        if (!isVisible())
            return false;

        boolean result = false;

        int bar;
        bar = (int) (getCurrentHp() * CLIENT_BAR_SIZE / getMaxHp());
        if (bar == 0 || bar != _lastHpBarUpdate) {
            _lastHpBarUpdate = bar;
            result = true;
        }

        bar = (int) (getCurrentMp() * CLIENT_BAR_SIZE / getMaxMp());
        if (bar == 0 || bar != _lastMpBarUpdate) {
            _lastMpBarUpdate = bar;
            result = true;
        }

        if (isPlayer()) {
            bar = (int) (getCurrentCp() * CLIENT_BAR_SIZE / getMaxCp());
            if (bar == 0 || bar != _lastCpBarUpdate) {
                _lastCpBarUpdate = bar;
                result = true;
            }
        }

        return result;
    }

    @Override
    public void onForcedAttack(Player player, boolean shift) {
        player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()));

        if (!isAttackable(player) || player.isConfused() || player.isBlocked()) {
            player.sendActionFailed();
            return;
        }

        player.getAI().Attack(this, true, shift);
    }

    void onHitTimer(Creature target, int damage, boolean crit, boolean miss, boolean soulshot, boolean shld, boolean unchargeSS) {
        if (isAlikeDead()) {
            sendActionFailed();
            return;
        }

        if (target.isDead() || !isInRange(target, 2000)) {
            sendActionFailed();
            return;
        }

        if (isPlayable() && target.isPlayable() && isInZoneBattle() != target.isInZoneBattle()) {
            Player player = getPlayer();
            if (player != null) {
                player.sendPacket(SystemMsg.INVALID_TARGET);
                player.sendActionFailed();
            }
            return;
        }

        target.getListeners().onAttackHit(this);

        // if hitted by a cursed weapon, Cp is reduced to 0, if a cursed weapon is hitted by a Hero, Cp is reduced to 0
        if (!miss && target.isPlayer() && (isCursedWeaponEquipped() || getActiveWeaponInstance() != null && getActiveWeaponInstance().isHeroWeapon() && target.isCursedWeaponEquipped()))
            target.setCurrentCp(0);

        if (target.isStunned() && Formulas.calcStunBreak(crit))
            target.getEffectList().stopEffects(EffectType.Stun);

        displayGiveDamageMessage(target, damage, crit, miss, shld, false);

        ThreadPoolManager.INSTANCE.execute(new NotifyAITask(target, CtrlEvent.EVT_ATTACKED, this, damage));

        boolean checkPvP = checkPvP(target, null);
        // Reduce HP of the target and calculate reflection damage to reduce HP of attacker if necessary
        if (!miss && damage > 0) {
            target.reduceCurrentHp(damage, this, null, true, true, false, true, false, false, true);

            // Skills cast by at physical attack
            if (!target.isDead()) {
                if (crit)
                    useTriggers(target, TriggerType.CRIT, null, null, damage);

                useTriggers(target, TriggerType.ATTACK, null, null, damage);

                // Manage attack or cast break of the target (calculating rate, sending message...)
                if (Formulas.calcCastBreak(target, crit))
                    target.abortCast(false, true);
            }

            if (soulshot && unchargeSS)
                unChargeShots(false);
        }

        if (miss)
            target.useTriggers(this, TriggerType.UNDER_MISSED_ATTACK, null, null, damage);

        startAttackStanceTask();

        if (checkPvP)
            startPvPFlag(target);
    }

    @SuppressWarnings("incomplete-switch")
    public void onMagicUseTimer(Creature aimingTarget, Skill skill, boolean forceUse) {
        _castInterruptTime = 0;

        if (skill.isUsingWhileCasting) {
            aimingTarget.getEffectList().stopEffect(skill.id);
            onCastEndTime();
            return;
        }

        if (!skill.isOffensive && getAggressionTarget() != null)
            forceUse = true;

        if (!skill.checkCondition(this, aimingTarget, forceUse, false, false)) {
            if (skill.skillType == SkillType.PET_SUMMON && isPlayer())
                getPlayer().setPetControlItem(null);
            onCastEndTime();
            return;
        }

        if ((skillGeoCheckTask != null) && !GeoEngine.canSeeTarget(this, aimingTarget, isFlying())) {
            sendPacket(SystemMsg.CANNOT_SEE_TARGET);
            broadcastPacket(new MagicSkillCanceled(getObjectId()));
            onCastEndTime();
            return;
        }

        List<Creature> targets = skill.getTargets(this, aimingTarget, forceUse);

        int vitalityConsume = skill.vitConsume;
        if ((vitalityConsume > 0) && (isPlayer())) {
            Player p = (Player) this;
            p.setVitality(Math.max(0.0D, p.getVitality() - vitalityConsume));
        }

        int hpConsume = skill.hpConsume;
        if (hpConsume > 0)
            setCurrentHp(Math.max(0, _currentHp - hpConsume), false);

        double mpConsume2 = skill.mpConsume2;
        if (mpConsume2 > 0) {
            if (skill.isMusic()) {
                double inc = mpConsume2 / 2;
                double add = inc * getEffectList().getAllEffects()
                        .filter(e -> e.getSkill().id != skill.id)
                        .filter(e -> e.getSkill().isMusic())
                        .filter(e -> e.getTimeLeft() > 30)
                        .count();
                mpConsume2 += add;
                mpConsume2 = calcStat(Stats.MP_DANCE_SKILL_CONSUME, mpConsume2, aimingTarget, skill);
            } else if (skill.isMagic())
                mpConsume2 = calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsume2, aimingTarget, skill);
            else
                mpConsume2 = calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsume2, aimingTarget, skill);

            if (_currentMp < mpConsume2 && isPlayable()) {
                sendPacket(SystemMsg.NOT_ENOUGH_MP);
                onCastEndTime();
                return;
            }
            reduceCurrentMp(mpConsume2, null);
        }

        callSkill(skill, targets, true);

        if (skill.getNumCharges() > 0)
            setIncreasedForce(getIncreasedForce() - skill.getNumCharges());

        if (skill.isSoulBoost)
            setConsumedSouls(getConsumedSouls() - Math.min(getConsumedSouls(), 5), null);
        else if (skill.soulsConsume > 0)
            setConsumedSouls(getConsumedSouls() - skill.soulsConsume, null);

        switch (skill.getFlyType()) {
            case THROW_UP:
            case THROW_HORIZONTAL:
                Location flyLoc;
                for (Creature target : targets) {
                    // target.setHeading(this, false); //TODO [VISTALL] set heading of target ? Oo
                    flyLoc = getFlyLocation(null, skill);
                    target.setLoc(flyLoc);
                    broadcastPacket(new FlyToLocation(target, flyLoc, skill.getFlyType()));
                }
                break;
        }

        if (_scheduledCastCount > 0) {
            _scheduledCastCount--;
            _skillLaunchedTask = ThreadPoolManager.INSTANCE.schedule(new MagicLaunchedTask(this, forceUse), _scheduledCastInterval);
            _skillTask = ThreadPoolManager.INSTANCE.schedule(new MagicUseTask(this, forceUse), _scheduledCastInterval);
            return;
        }

        int skillCoolTime = Formulas.calcMAtkSpd(this, skill, skill.coolTime);
        if (skillCoolTime > 0)
            ThreadPoolManager.INSTANCE.schedule(new CastEndTimeTask(this), skillCoolTime);
        else
            onCastEndTime();
    }

    public void onCastEndTime() {
        finishFly();
        clearCastVars();
        getAI().notifyEvent(CtrlEvent.EVT_FINISH_CASTING);
    }

    public void clearCastVars() {
        _animationEndTime = 0;
        _castInterruptTime = 0;
        _scheduledCastCount = 0;
        _castingSkill = null;
        _skillTask = null;
        _skillLaunchedTask = null;
        skillGeoCheckTask = null;
        _flyLoc = null;
    }

    private void finishFly() {
        Location flyLoc = _flyLoc;
        _flyLoc = null;
        if (flyLoc != null) {
            setLoc(flyLoc);
            validateLocation(1);
        }
    }

    public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage) {
        if (attacker == null || isDead() || (attacker.isDead() && !isDot))
            return;

        if (isDamageBlocked() && transferDamage)
            return;

        if (isDamageBlocked() && attacker != this) {
            if (sendMessage)
                attacker.sendPacket(SystemMsg.THE_ATTACK_HAS_BEEN_BLOCKED);
            return;
        }

        if (isSummon()) {
            damage *= BalancerConfig.ALT_SUMMONS_DAMAGE;
        }
        if (canReflect) {
            if (attacker.absorbAndReflect(this, skill, damage))
                return;

            damage = absorbToEffector(attacker, damage);
            damage = absorbToMp(damage);
            damage = absorbToSummon(damage);
        }

        getListeners().onCurrentHpDamage(damage, attacker, skill);

        if (attacker != this) {
            if (sendMessage)
                displayReceiveDamageMessage(attacker, (int) damage);

            if (!isDot)
                useTriggers(attacker, TriggerType.RECEIVE_DAMAGE, null, null, damage);
        }

        onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
    }

    protected void onReduceCurrentHp(final double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp) {


        if (awake && isSleeping())
            getEffectList().stopEffects(EffectType.Sleep);

        if (attacker != this || (skill != null && skill.isOffensive)) {
            if (meditated) {
                Effect effect = getEffectList().getEffectByType(EffectType.Meditation);
                if (effect != null)
                    getEffectList().stopEffect(effect.getSkill());
            }

            startAttackStanceTask();
            checkAndRemoveInvisible();

            if (getCurrentHp() - damage < 0.5)
                useTriggers(attacker, TriggerType.DIE, null, null, damage);
        }

        // GM undying mode
        if (isPlayer() && getPlayer().isGM() && getPlayer().isUndying() && damage + 0.5 >= getCurrentHp())
            return;

        setCurrentHp(Math.max(getCurrentHp() - damage, 0), false);

        if (getCurrentHp() < 0.5) {
            if (!isPlayer() || !(getPlayer().isInOlympiadMode() && getPlayer().getOlympiadGame().getType() != CompType.TEAM))
                doDie(attacker);
        }
    }

    public void reduceCurrentMp(double i, Creature attacker) {
        if (attacker != null && attacker != this) {
            if (isSleeping())
                getEffectList().stopEffects(EffectType.Sleep);

            if (meditated) {
                Effect effect = getEffectList().getEffectByType(EffectType.Meditation);
                if (effect != null)
                    getEffectList().stopEffect(effect.getSkill());
            }
        }

        if (isDamageBlocked() && attacker != null && attacker != this) {
            attacker.sendPacket(SystemMsg.THE_ATTACK_HAS_BEEN_BLOCKED);
            return;
        }

        // 5182 = Blessing of protection, работает если разница уровней больше 10 и не в зоне осады
        if (attacker != null && attacker.isPlayer() && Math.abs(attacker.getLevel() - getLevel()) > 10) {
            // ПК не может нанести урон чару с блессингом
            if (attacker.getKarma() > 0 && getEffectList().getEffectsBySkillId(5182) != null && !isInZone(ZoneType.SIEGE))
                return;
            // чар с блессингом не может нанести урон ПК
            if (getKarma() > 0 && attacker.getEffectList().getEffectsBySkillId(5182) != null && !attacker.isInZone(ZoneType.SIEGE))
                return;
        }

        i = _currentMp - i;

        if (i < 0)
            i = 0;

        setCurrentMp(i);

        if (attacker != null && attacker != this)
            startAttackStanceTask();
    }

    void removeAllSkills() {
        getAllSkills().forEach(this::removeSkill);
    }

    public void removeBlockStats(List<Stats> stats) {
        if (_blockedStats != null) {
            _blockedStats.removeAll(stats);
            if (_blockedStats.isEmpty())
                _blockedStats = null;
        }
    }

    public Skill removeSkill(Skill skill) {
        if (skill == null)
            return null;
        return removeSkill(skill.id);
    }

    public Skill removeSkill(int id) {
        // Remove the skill from the L2Character skills
        Skill oldSkill = skills.remove(id);

        // Remove all its Func objects from the L2Character calculator set
        if (oldSkill != null) {
            removeTriggers(oldSkill);
            removeStatsOwner(oldSkill);
            if (Config.ALT_DELETE_SA_BUFFS && (oldSkill.isItemSkill() || oldSkill.isItemHandler() || oldSkill.name().startsWith("Item Skill"))) {
                // Завершаем все эффекты, принадлежащие старому скиллу
                getEffectList().getEffectsBySkill(oldSkill)
                        .forEach(Effect::exit);
                // И с петов тоже
                Summon pet = getPet();
                if (pet != null) {
                    pet.getEffectList().getEffectsBySkill(oldSkill)
                            .forEach(Effect::exit);
                }
            }
        }
        return oldSkill;
    }

    public void addTriggers(StatTemplate f) {
        f.getTriggerList().forEach(this::addTrigger);
    }

    public void addTrigger(TriggerInfo t) {
        Set<TriggerInfo> hs = triggers.get(t.getType());
        if (hs == null) {
            hs = new CopyOnWriteArraySet<>();
            triggers.put(t.getType(), hs);
        }

        hs.add(t);

        if (t.getType() == TriggerType.ADD)
            useTriggerSkill(this, null, t, null, 0);
    }

    public void removeTriggers(StatTemplate f) {
        f.getTriggerList().forEach(this::removeTrigger);
    }

    public void removeTrigger(TriggerInfo t) {
        Set<TriggerInfo> hs = triggers.get(t.getType());
        if (hs == null)
            return;
        hs.remove(t);
    }

    public void sendActionFailed() {
        sendPacket(ActionFail.STATIC);
    }

    public boolean hasAI() {
        return ai != null;
    }

    public synchronized CharacterAI getAI() {
        if (ai == null)
            ai = new CharacterAI(this);
        return ai;
    }

    public void setAI(CharacterAI newAI) {
        if (newAI == null)
            return;
        CharacterAI oldAI = ai;

        synchronized (this) {
            ai = newAI;
        }

        if (oldAI != null) {
            if (oldAI.isActive()) {
                oldAI.stopAITask();
                newAI.startAITask();
                newAI.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            }
        }
    }

    public final void setCurrentHp(double newHp, boolean canRessurect, boolean sendInfo) {
        int maxHp = getMaxHp();

        newHp = Math.min(maxHp, Math.max(0, newHp));

        if (_currentHp == newHp)
            return;

        if (newHp >= 0.5 && isDead() && !canRessurect)
            return;

        double hpStart = _currentHp;

        _currentHp = newHp;

        if (isDead.compareAndSet(true, false))
            onRevive();

        checkHpMessages(hpStart, _currentHp);

        if (sendInfo) {
            broadcastStatusUpdate();
            sendChanges();
        }

        if (_currentHp < maxHp)
            startRegeneration();
    }

    public final void setCurrentHp(double newHp, boolean canRessurect) {
        setCurrentHp(newHp, canRessurect, true);
    }

    private void setCurrentMp(double newMp, boolean sendInfo) {
        int maxMp = getMaxMp();

        newMp = Math.min(maxMp, Math.max(0, newMp));

        if (_currentMp == newMp)
            return;

        if (newMp >= 0.5 && isDead())
            return;

        _currentMp = newMp;

        if (sendInfo) {
            broadcastStatusUpdate();
            sendChanges();
        }

        if (_currentMp < maxMp)
            startRegeneration();
    }

    private void setCurrentCp(double newCp, boolean sendInfo) {
        if (!isPlayer())
            return;

        int maxCp = getMaxCp();
        newCp = Math.min(maxCp, Math.max(0, newCp));

        if (currentCp == newCp)
            return;

        if (newCp >= 0.5 && isDead())
            return;

        currentCp = newCp;

        if (sendInfo) {
            broadcastStatusUpdate();
            sendChanges();
        }

        if (currentCp < maxCp)
            startRegeneration();
    }

    public Creature setFullHpMp() {
        setCurrentHpMp(getMaxHp(), getMaxMp(), true);
        return this;
    }

    private void setCurrentHpMp(double newHp, double newMp, boolean canRessurect) {
        int maxHp = getMaxHp();
        int maxMp = getMaxMp();

        newHp = Math.min(maxHp, Math.max(0, newHp));
        newMp = Math.min(maxMp, Math.max(0, newMp));

        if (_currentHp == newHp && _currentMp == newMp)
            return;

        if (newHp >= 0.5 && isDead() && !canRessurect)
            return;

        double hpStart = _currentHp;

        _currentHp = newHp;
        _currentMp = newMp;

        if (isDead.compareAndSet(true, false))
            onRevive();

        checkHpMessages(hpStart, _currentHp);

        broadcastStatusUpdate();
        sendChanges();

        if (_currentHp < maxHp || _currentMp < maxMp)
            startRegeneration();
    }

    public Creature setCurrentHpMp(double newHp, double newMp) {
        setCurrentHpMp(newHp, newMp, false);
        return this;
    }

    @Override
    public final int getHeading() {
        return heading;
    }

    public Creature setHeading(int heading) {
        this.heading = heading;
        return this;
    }

    public final void setIsTeleporting(boolean value) {
        isTeleporting.compareAndSet(!value, value);
    }

    public Creature getCastingTarget() {
        return castingTarget.get();
    }

    private void setCastingTarget(Creature target) {
        if (target == null)
            castingTarget = HardReferences.emptyRef();
        else
            castingTarget = target.getRef();
    }

    public final void setRunning() {
        if (!_running) {
            _running = true;
            broadcastPacket(new ChangeMoveType(this));
        }
    }

    public void setSkillMastery(Integer skill, int mastery) {
        if (_skillMastery == null)
            _skillMastery = new HashMap<>();
        _skillMastery.put(skill, mastery);
    }

    public Creature getAggressionTarget() {
        return _aggressionTarget.get();
    }

    public void setAggressionTarget(Creature target) {
        if (target == null)
            _aggressionTarget = HardReferences.emptyRef();
        else
            _aggressionTarget = target.getRef();
    }

    public void setWalking() {
        if (_running) {
            _running = false;
            broadcastPacket(new ChangeMoveType(this));
        }
    }

    public void startAbnormalEffect(AbnormalEffect ae) {
        if (ae == AbnormalEffect.NULL) {
            abnormalEffects = AbnormalEffect.NULL.getMask();
            abnormalEffects2 = AbnormalEffect.NULL.getMask();
            _abnormalEffects3 = AbnormalEffect.NULL.getMask();
        } else if (ae.isSpecial())
            abnormalEffects2 |= ae.getMask();
        else if (ae.isEvent())
            _abnormalEffects3 |= ae.getMask();
        else
            abnormalEffects |= ae.getMask();
        sendChanges();
    }

    void startAttackStanceTask() {
        startAttackStanceTask0();
    }

    /**
     * Запускаем задачу анимации боевой позы. Если задача уже запущена, увеличиваем время, которое персонаж будет в боевой позе на 15с
     */
    void startAttackStanceTask0() {
        // предыдущая задача еще не закончена, увеличиваем время
        if (isInCombat()) {
            _stanceEndTime = System.currentTimeMillis() + 15000L;
            return;
        }

        _stanceEndTime = System.currentTimeMillis() + 15000L;

        broadcastPacket(new AutoAttackStart(getObjectId()));

        // отменяем предыдущую
        final Future<?> task = _stanceTask;
        if (task != null)
            task.cancel(false);

        // Добавляем задачу, которая будет проверять, если истекло время нахождения персонажа в боевой позе,
        // отменяет задачу и останаливает анимацию.
        _stanceTask = LazyPrecisionTaskManager.getInstance().scheduleAtFixedRate(_stanceTaskRunnable == null ? _stanceTaskRunnable = new AttackStanceTask() : _stanceTaskRunnable, 1000L, 1000L);
    }

    /**
     * Останавливаем задачу анимации боевой позы.
     */
    public void stopAttackStanceTask() {
        _stanceEndTime = 0L;

        final Future<?> task = _stanceTask;
        if (task != null) {
            task.cancel(false);
            _stanceTask = null;

            broadcastPacket(new AutoAttackStop(getObjectId()));
        }
    }

    /**
     * Остановить регенерацию
     */
    private void stopRegeneration() {
        regenLock.lock();
        try {
            if (_isRegenerating) {
                _isRegenerating = false;

                if (_regenTask != null) {
                    _regenTask.cancel(false);
                    _regenTask = null;
                }
            }
        } finally {
            regenLock.unlock();
        }
    }

    /**
     * Запустить регенерацию
     */
    private void startRegeneration() {
        if (!isVisible() || isDead() || getRegenTick() == 0L)
            return;

        if (_isRegenerating)
            return;

        regenLock.lock();
        try {
            if (!_isRegenerating) {
                _isRegenerating = true;
                _regenTask = RegenTaskManager.getInstance().scheduleAtFixedRate(_regenTaskRunnable == null ? _regenTaskRunnable = new RegenTask() : _regenTaskRunnable, 0, getRegenTick());
            }
        } finally {
            regenLock.unlock();
        }
    }

    protected long getRegenTick() {
        return 3000L;
    }

    public void stopAbnormalEffect(AbnormalEffect ae) {
        if (ae.isSpecial())
            abnormalEffects2 &= ~ae.getMask();
        if (ae.isEvent())
            _abnormalEffects3 &= ~ae.getMask();
        else
            abnormalEffects &= ~ae.getMask();
        sendChanges();
    }

    public void setBlock(boolean state) {
        blocked = state;
    }

    public void setBlock() {
        blocked = false;
    }

    public void startConfused() {
        confused.getAndSet(true);
    }

    public boolean stopConfused() {
        return confused.setAndGet(false);
    }

    public boolean startFear() {
        return !afraid.getAndSet(true);
    }

    public void stopFear() {
        afraid.setAndGet(false);
    }

    public boolean startMuted() {
        return muted.getAndSet(true);
    }

    public void stopMuted() {
        muted.setAndGet(false);
    }

    public boolean startPMuted() {
        return pmuted.getAndSet(true);
    }

    public void stopPMuted() {
        pmuted.setAndGet(false);
    }

    public boolean startAMuted() {
        return amuted.getAndSet(true);
    }

    public void stopAMuted() {
        amuted.setAndGet(false);
    }

    public void startRooted() {
        rooted.getAndSet(true);
    }

    public void stopRooted() {
        rooted.setAndGet(false);
    }

    public void startSleeping() {
        sleeping.getAndSet(true);
    }

    public void stopSleeping() {
        sleeping.setAndGet(false);
    }

    public void startStunning() {
        stunned.getAndSet(true);
    }

    public void stopStunning() {
        stunned.setAndGet(false);
    }

    public void startParalyzed() {
        paralyzed.getAndSet(true);
    }

    public void stopParalyzed() {
        paralyzed.setAndGet(false);
    }

    public void startImmobilized() {
        /*return*/
        immobilized.getAndSet(true);
    }

    public void stopImmobilized() {
        immobilized.setAndGet(false);
    }

    public void startHealBlocked() {
        healBlocked.getAndSet(true);
    }

    public void stopHealBlocked() {
        healBlocked.setAndGet(false);
    }

    public void startDamageBlocked() {
        damageBlocked.getAndSet(true);
    }

    public void stopDamageBlocked() {
        damageBlocked.setAndGet(false);
    }

    public void startBuffImmunity() {
        buffImmunity.getAndSet(true);
    }

    public void stopBuffImmunity() {
        buffImmunity.setAndGet(false);
    }

    public void startDebuffImmunity() {
        debuffImmunity.getAndSet(true);
    }

    public void stopDebuffImmunity() {
        debuffImmunity.setAndGet(false);
    }

    public void startWeaponEquipBlocked() {
        weaponEquipBlocked.getAndSet(true);
    }

    public void stopWeaponEquipBlocked() {
        weaponEquipBlocked.getAndSet(false);
    }

    public void startFrozen() {
        frozen.getAndSet(true);
    }

    public void stopFrozen() {
        frozen.setAndGet(false);
    }

    public void breakFakeDeath() {
        getEffectList().stopAllSkillEffects(EffectType.FakeDeath);
    }

    public final void setIsBlessedByNoblesse(boolean value) {
        isblessedbynoblesse = value;
    }

    public final void setIsSalvation(boolean value) {
        _isSalvation = value;
    }

    public boolean isConfused() {
        return confused.get();
    }

    public boolean isFakeDeath() {
        return _fakeDeath;
    }

    public void setFakeDeath(boolean value) {
        _fakeDeath = value;
    }

    public boolean isAfraid() {
        return afraid.get();
    }

    public boolean isBlocked() {
        return blocked;
    }

    public boolean isMuted(Skill skill) {
        if (skill == null || skill.isNotAffectedByMute)
            return false;
        return muted.get() && skill.isMagic() || pmuted.get() && !skill.isMagic();
    }

    public boolean isAMuted() {
        return amuted.get();
    }

    public boolean isRooted() {
        return rooted.get();
    }

    public boolean isSleeping() {
        return sleeping.get();
    }

    public boolean isStunned() {
        return stunned.get();
    }

    public void setMeditated(boolean value) {
        meditated = value;
    }

    public boolean isWeaponEquipBlocked() {
        return weaponEquipBlocked.get();
    }

    public boolean isParalyzed() {
        return paralyzed.get();
    }

    public boolean isFrozen() {
        return frozen.get();
    }

    public boolean isImmobilized() {
        return immobilized.get() || getRunSpeed() < 1;
    }

    public boolean isHealBlocked() {
        return isAlikeDead() || healBlocked.get();
    }

    boolean isDamageBlocked() {
        return isInvul() || damageBlocked.get();
    }

    public boolean isCastingNow() {
        return _skillTask != null;
    }

    public boolean isLockedTarget() {
        return _lockedTarget;
    }

    public void setLockedTarget(boolean value) {
        _lockedTarget = value;
    }

    public boolean isMovementDisabled() {
        return isBlocked() || isRooted() || isImmobilized() || isAlikeDead() || isStunned() || isSleeping() || isParalyzed() || isAttackingNow() || isCastingNow() || isFrozen();
    }

    public boolean isActionsDisabled() {
        return isBlocked() || isAlikeDead() || isStunned() || isSleeping() || isParalyzed() || isAttackingNow() || isCastingNow() || isFrozen();
    }

    public final boolean isAttackingDisabled() {
        return _attackReuseEndTime > System.currentTimeMillis();
    }

    public boolean isOutOfControl() {
        return isBlocked() || isConfused() || isAfraid();
    }

    public void teleToLocation(Location loc) {
        teleToLocation(loc.x, loc.y, loc.z, getReflection());
    }

    public void teleToLocation(Location loc, int refId) {
        teleToLocation(loc.x, loc.y, loc.z, refId);
    }

    public void teleToLocation(Location loc, Reflection r) {
        teleToLocation(loc.x, loc.y, loc.z, r);
    }

    public void teleToLocation(int x, int y, int z) {
        teleToLocation(x, y, z, getReflection());
    }

    void checkAndRemoveInvisible() {
        if (getInvisibleType() == InvisibleType.EFFECT)
            getEffectList().stopEffects(EffectType.Invisible);
    }

    void teleToLocation(int x, int y, int z, int refId) {
        Reflection r = ReflectionManager.INSTANCE.get(refId);
        if (r == null)
            return;
        teleToLocation(x, y, z, r);
    }

    private void teleToLocation(int x, int y, int z, Reflection r) {
        if (!isTeleporting.compareAndSet(false, true))
            return;

        if (isFakeDeath())
            breakFakeDeath();

        abortCast(true, false);
        if (!isLockedTarget())
            setTarget(null);
        stopMove();

        if (!isBoat() && !isFlying() && !World.isWater(new Location(x, y, z), r))
            z = GeoEngine.getHeight(x, y, z, r.getGeoIndex());

        // TODO [G1ta0] убрать DimensionalRiftManager.teleToLocation
        if (isPlayer() && DimensionalRiftManager.INSTANCE.checkIfInRiftZone(getLoc(), true)) {
            Player player = (Player) this;
            if (player.isInParty() && player.getParty().isInDimensionalRift()) {
                Location newCoords = DimensionalRiftManager.INSTANCE.getRoom(0, 0).getTeleportCoords();
                x = newCoords.x;
                y = newCoords.y;
                z = newCoords.z;
                player.getParty().getDimensionalRift().usedTeleport();
            }
        }

        if (isPlayer()) {
            Player player = (Player) this;

            player.getListeners().onTeleport(new Location(x, y, z), r);

            // Alexander - Send a teleport event to the tutorial for the player
            Quest q = QuestManager.getQuest(255);
            if (q != null) {
                player.processQuestEvent(q.getName(), "CE42", null);
            }

            decayMe();

            setXYZ(x, y, z);

            setReflection(r);

            // It is necessary at the teleport from a higher point to a lower, otherwise harmed by the "fall"
            player.setLastClientPosition(null);
            player.setLastServerPosition(null);

            player.sendPacket(new TeleportToLocation(player, x, y, z));

            if (player.getPet() != null) {
                player.getPet().teleportToOwner();
            }
        } else {
            setXYZ(x, y, z);

            setReflection(r);

            broadcastPacket(new TeleportToLocation(this, x, y, z));
            onTeleported();
        }
    }

    boolean onTeleported() {
        return isTeleporting.compareAndSet(true, false);
    }

    public void sendMessage(CustomMessage message) {

    }

    public void sendChatMessage(int objectId, int messageType, String charName, String text) {

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getObjectId() + "]";
    }

    @Override
    public double getColRadius() {
        return getTemplate().collisionRadius;
    }

    @Override
    public double getColHeight() {
        return getTemplate().collisionHeight;
    }

    public EffectList getEffectList() {
        if (_effectList == null)
            synchronized (this) {
                if (_effectList == null)
                    _effectList = new EffectList(this);
            }

        return _effectList;
    }

    public boolean paralizeOnAttack(Creature attacker) {
        int max_attacker_level = 0xFFFF;

        MonsterInstance leader;
        if (isRaid() || isMinion() && (leader = ((MinionInstance) this).getLeader()) != null && leader.isRaid())
            max_attacker_level = getLevel() + Config.RAID_MAX_LEVEL_DIFF;
        else if (isNpc()) {
            int max_level_diff = ((NpcInstance) this).getParameter("ParalizeOnAttack", -1000);
            if (max_level_diff != -1000)
                max_attacker_level = getLevel() + max_level_diff;
        }

        return attacker.getLevel() > max_attacker_level;

    }

    @Override
    protected void onDelete() {
        GameObjectsStorage.remove(storedId);

        getEffectList().stopAllEffects();

        super.onDelete();
    }

    protected void addExpAndSp(long exp, long sp) {
    }

    public void broadcastCharInfo() {
    }

    // ---------------------------- Not Implemented -------------------------------

    void checkHpMessages(double currentHp, double newHp) {
    }

    boolean checkPvP(Creature target, Skill skill) {
        return false;
    }

    protected boolean consumeItem(int itemConsumeId, long itemCount) {
        return true;
    }

    boolean consumeItemMp(int itemId, int mp) {
        return true;
    }

    public boolean isFearImmune() {
        return false;
    }

    public boolean isLethalImmune() {
        return false;
    }

    public boolean getChargedSoulShot() {
        return false;
    }

    public int getChargedSpiritShot() {
        return 0;
    }

    public int getIncreasedForce() {
        return 0;
    }

    void setIncreasedForce(int i) {
    }

    public int getConsumedSouls() {
        return 0;
    }

    public int getAgathionEnergy() {
        return 0;
    }

    public void setAgathionEnergy(int val) {
        //
    }

    public int getKarma() {
        return 0;
    }

    public double getLevelMod() {
        return 1;
    }

    public int getNpcId() {
        return 0;
    }

    public Summon getPet() {
        return null;
    }

    public int getPvpFlag() {
        return 0;
    }

    public TeamType getTeam() {
        return _team;
    }

    public void setTeam(TeamType t) {
        _team = t;
        sendChanges();
    }

    public boolean isUndead() {
        return false;
    }

    public boolean isParalyzeImmune() {
        return false;
    }

    void reduceArrowCount() {
    }

    public void sendChanges() {
        getStatsRecorder().sendChanges();
    }

    public void sendMessage(String message) {
    }

    public void sendPacket(IStaticPacket mov) {
    }

    void sendPacket(IStaticPacket... mov) {
    }

    void sendPacket(List<? extends IStaticPacket> mov) {
    }

    public void setConsumedSouls(int i, NpcInstance monster) {
    }

    void startPvPFlag(Creature target) {
    }

    public boolean unChargeShots(boolean spirit) {
        return false;
    }

    public void updateEffectIcons() {
    }

    /**
     * Выставить предельные значения HP/MP/CP и запустить регенерацию, если в этом есть необходимость
     */
    private void refreshHpMpCp() {
        final int maxHp = getMaxHp();
        final int maxMp = getMaxMp();
        final int maxCp = isPlayer() ? getMaxCp() : 0;

        if (_currentHp > maxHp)
            setCurrentHp(maxHp, false);
        if (_currentMp > maxMp)
            setCurrentMp(maxMp, false);
        if (currentCp > maxCp)
            setCurrentCp(maxCp, false);

        if (_currentHp < maxHp || _currentMp < maxMp || currentCp < maxCp)
            startRegeneration();
    }

    public void updateStats() {
        refreshHpMpCp();
        sendChanges();
    }

    public void setOverhitAttacker(Creature attacker) {
    }

    public void setOverhitDamage(double damage) {
    }

    public boolean isCursedWeaponEquipped() {
        return false;
    }

    public boolean isHero() {
        return false;
    }

    public int getAccessLevel() {
        return 0;
    }

    public Clan getClan() {
        return null;
    }

    public double getRateAdena() {
        return 1.;
    }

    public double getRateItems() {
        return 1.;
    }

    public double getRateExp() {
        return 1.;
    }

    public double getRateSp() {
        return 1.;
    }

    public double getRateSpoil() {
        return 1.;
    }

    public int getFormId() {
        return 0;
    }

    public boolean isNameAbove() {
        return true;
    }

    @Override
    public Creature setLoc(Location loc) {
        setXYZ(loc.x, loc.y, loc.z);
        return this;
    }

    public void setLoc(Location loc, boolean MoveTask) {
        setXYZ(loc.x, loc.y, loc.z, MoveTask);
    }

    @Override
    public void setXYZ(int x, int y, int z) {
        setXYZ(x, y, z, false);
    }

    public void setXYZ(int x, int y, int z, boolean MoveTask) {
        if (!MoveTask)
            stopMove();

        moveLock.lock();
        try {
            super.setXYZ(x, y, z);
        } finally {
            moveLock.unlock();
        }

        updateZones();
    }

    @Override
    protected void onSpawn() {
        updateStats();
        updateZones();
    }


    @Override
    public void spawnMe(Location loc) {
        if (loc.h > 0)
            setHeading(loc.h);
        super.spawnMe(loc);
    }

    @Override
    protected void onDespawn() {
        if (!isLockedTarget())
            setTarget(null);
        stopMove();
        stopAttackStanceTask();
        stopRegeneration();

        updateZones();
        clearStatusListeners();

        super.onDespawn();
    }

    public final void doDecay() {
        if (!isDead())
            return;

        onDecay();
    }

    protected void onDecay() {
        decayMe();
    }

    public void validateLocation(int broadcast) {
        L2GameServerPacket sp = new ValidateLocation(this);
        if (broadcast == 0)
            sendPacket(sp);
        else if (broadcast == 1)
            broadcastPacket(sp);
        else
            broadcastPacketToOthers(sp);
    }

    public void addUnActiveSkill(Skill skill) {
        if (skill == null || isUnActiveSkill(skill.id))
            return;

        removeStatsOwner(skill);
        removeTriggers(skill);

        unActiveSkills.add(skill.id);
    }

    public void removeUnActiveSkill(Skill skill) {
        if (skill == null || !isUnActiveSkill(skill.id))
            return;

        addStatFuncs(skill.getStatFuncs());
        addTriggers(skill);

        unActiveSkills.remove(skill.id);
    }

    public boolean isUnActiveSkill(int id) {
        return unActiveSkills.contains(id);
    }

    public abstract int getLevel();

    public abstract ItemInstance getActiveWeaponInstance();

    public abstract WeaponTemplate getActiveWeaponItem();

    public abstract ItemInstance getSecondaryWeaponInstance();

    public abstract WeaponTemplate getSecondaryWeaponItem();

    public CharListenerList getListeners() {
        if (listeners == null)
            synchronized (this) {
                if (listeners == null)
                    listeners = new CharListenerList(this);
            }
        return listeners;
    }

    public <T extends Listener> boolean addListener(T listener) {
        return getListeners().add(listener);
    }

    public <T extends Listener> boolean removeListener(T listener) {
        return getListeners().remove(listener);
    }

    protected CharStatsChangeRecorder<? extends Creature> getStatsRecorder() {
        if (statsRecorder == null)
            synchronized (this) {
                if (statsRecorder == null)
                    statsRecorder = new CharStatsChangeRecorder<>(this);
            }

        return statsRecorder;
    }

    @Override
    public boolean isCreature() {
        return true;
    }

    public void displayGiveDamageMessage(Creature target, int damage, boolean crit, boolean miss, boolean shld, boolean magic) {
        if (miss && target.isPlayer() && !target.isDamageBlocked())
            target.sendPacket(new SystemMessage2(SystemMsg.C1_HAS_EVADED_C2S_ATTACK).addName(target).addName(this));
    }

    void displayReceiveDamageMessage(Creature attacker, int damage) {
        //
    }

    public Collection<TimeStamp> getSkillReuses() {
        return skillReuses.values();
    }

    TimeStamp getSkillReuse(Skill skill) {
        return skillReuses.get(skill.hashCode());
    }

    String getVisibleName() {
        return getName();
    }

    public class MoveNextTask extends RunnableImpl {
        private double alldist, donedist;

        MoveNextTask setDist(double dist) {
            alldist = dist;
            donedist = 0.;
            return this;
        }

        @Override
        public void runImpl() {
            if (!isMoving)
                return;

            moveLock.lock();
            try {
                if (!isMoving)
                    return;

                if (isMovementDisabled()) {
                    stopMove();
                    return;
                }

                Creature follow = null;
                int speed = getMoveSpeed();
                if (speed <= 0) {
                    stopMove();
                    return;
                }
                long now = System.currentTimeMillis();

                if (isFollow) {
                    follow = getFollowTarget();
                    if (follow == null || follow.isInvisible()) {
                        stopMove();
                        return;
                    }
                    if (isInRangeZ(follow, _offset) && GeoEngine.canSeeTarget(Creature.this, follow, false)) {
                        stopMove();
                        ThreadPoolManager.INSTANCE.execute(new NotifyAITask(Creature.this, CtrlEvent.EVT_ARRIVED_TARGET));
                        return;
                    }
                }

                if (alldist <= 0) {
                    moveNext(false);
                    return;
                }

                donedist += (now - _startMoveTime) * _previousSpeed / 1000.;
                double done = donedist / alldist;

                if (done < 0)
                    done = 0;
                if (done >= 1) {
                    moveNext(false);
                    return;
                }

                if (isMovementDisabled()) {
                    stopMove();
                    return;
                }

                Location loc;

                int index = (int) (moveList.size() * done);
                if (index >= moveList.size())
                    index = moveList.size() - 1;
                if (index < 0)
                    index = 0;

                loc = moveList.get(index).clone().geo2world();

                if (!isFlying() && !isInBoat() && !isInWater() && !isBoat())
                    if (loc.z - getZ() > 256) {
                        String bug_text = "geo bug 1 at: " + getLoc() + " => " + loc.x + "," + loc.y + "," + loc.z + "\tAll path: " + moveList.get(0) + " => " + moveList.get(moveList.size() - 1);
                        Log.add(bug_text, "geo");
                        stopMove();
                        return;
                    }

                // Проверяем, на всякий случай
                if (loc == null || isMovementDisabled()) {
                    stopMove();
                    return;
                }

                setLoc(loc, true);

                // В процессе изменения координат, мы остановились
                if (isMovementDisabled()) {
                    stopMove();
                    return;
                }

                if (isFollow && now - _followTimestamp > (_forestalling ? 500 : 1000) && follow != null && !follow.isInRange(movingDestTempPos, Math.max(100, _offset))) {
                    if (Math.abs(getZ() - loc.z) > 1000 && !isFlying()) {
                        sendPacket(SystemMsg.CANNOT_SEE_TARGET);
                        stopMove();
                        return;
                    }
                    if (buildPathTo(follow.getX(), follow.getY(), follow.getZ(), _offset, follow, true, true))
                        movingDestTempPos.set(follow.getX(), follow.getY(), follow.getZ());
                    else {
                        stopMove();
                        return;
                    }
                    moveNext(true);
                    return;
                }

                _previousSpeed = speed;
                _startMoveTime = now;
                _moveTask = ThreadPoolManager.INSTANCE.schedule(this, getMoveTickInterval());
            } catch (RuntimeException e) {
                _log.error("Error in Creature Moving! ", e);
            } finally {
                moveLock.unlock();
            }
        }
    }

    private class AttackStanceTask extends RunnableImpl {
        @Override
        public void runImpl() {
            if (!isInCombat())
                stopAttackStanceTask();
        }
    }

    private class RegenTask implements Runnable {
        @Override
        public void run() {
            if (isAlikeDead() || getRegenTick() == 0L)
                return;

            double hpStart = _currentHp;

            int maxHp = getMaxHp();
            int maxMp = getMaxMp();
            int maxCp = isPlayer() ? getMaxCp() : 0;

            double addHp = 0.;
            double addMp = 0.;

            regenLock.lock();
            try {
                if (_currentHp < maxHp)
                    addHp += Formulas.calcHpRegen(Creature.this);

                if (_currentMp < maxMp)
                    addMp += Formulas.calcMpRegen(Creature.this);

                // Added regen bonus when character is sitting
                if (isPlayer() && Config.REGEN_SIT_WAIT) {
                    Player pl = (Player) Creature.this;
                    if (pl.isSitting()) {
                        pl.updateWaitSitTime();
                        if (pl.getWaitSitTime() > 5) {
                            addHp += pl.getWaitSitTime();
                            addMp += pl.getWaitSitTime();
                        }
                    }
                } else if (isRaid()) {
                    addHp *= Config.RATE_RAID_REGEN;
                    addMp *= Config.RATE_RAID_REGEN;
                }

                _currentHp += Math.max(0, Math.min(addHp, calcStat(Stats.HP_LIMIT, null, null) * maxHp / 100. - _currentHp));
                _currentMp += Math.max(0, Math.min(addMp, calcStat(Stats.MP_LIMIT, null, null) * maxMp / 100. - _currentMp));

                _currentHp = Math.min(maxHp, _currentHp);
                _currentMp = Math.min(maxMp, _currentMp);

                if (isPlayer()) {
                    currentCp += Math.max(0, Math.min(Formulas.calcCpRegen(Creature.this), calcStat(Stats.CP_LIMIT, null, null) * maxCp / 100. - currentCp));
                    currentCp = Math.min(maxCp, currentCp);
                }

                // отрегенились, останавливаем задачу
                if (_currentHp == maxHp && _currentMp == maxMp && currentCp == maxCp)
                    stopRegeneration();
            } finally {
                regenLock.unlock();
            }

            broadcastStatusUpdate();
            sendChanges();

            checkHpMessages(hpStart, _currentHp);
        }
    }
}