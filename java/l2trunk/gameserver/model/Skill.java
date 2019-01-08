package l2trunk.gameserver.model;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.geometry.Polygon;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.BalancerConfig;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.instancemanager.games.HandysBlockCheckerManager;
import l2trunk.gameserver.instancemanager.games.HandysBlockCheckerManager.ArenaParticipantsHolder;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.base.BaseStats;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.base.Element;
import l2trunk.gameserver.model.base.SkillTrait;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.instances.ChestInstance;
import l2trunk.gameserver.model.instances.DecoyInstance;
import l2trunk.gameserver.model.instances.FeedableBeastInstance;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.items.Inventory;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.FlyToLocation.FlyType;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.skills.effects.EffectTemplate;
import l2trunk.gameserver.skills.skillclasses.*;
import l2trunk.gameserver.skills.skillclasses.DeathPenalty;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Formulas;
import l2trunk.gameserver.stats.StatTemplate;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.conditions.Condition;
import l2trunk.gameserver.stats.funcs.Func;
import l2trunk.gameserver.stats.funcs.FuncTemplate;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.PositionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static l2trunk.commons.lang.NumberUtils.toInt;

public abstract class Skill extends StatTemplate implements Cloneable, Comparable<Skill> {
    // public static final int SKILL_CUBIC_MASTERY = 143;
    public static final int SKILL_CRAFTING = 172;
    public static final int SKILL_POLEARM_MASTERY = 216;
    public static final int SKILL_CRYSTALLIZE = 248;
    public static final int SKILL_WEAPON_MAGIC_MASTERY1 = 249;
    public static final int SKILL_WEAPON_MAGIC_MASTERY2 = 250;
    public static final int SKILL_BLINDING_BLOW = 321;
    public static final int SKILL_BLUFF = 358;
    public static final int SKILL_SOUL_MASTERY = 467;
    public static final int SKILL_RECHARGE = 1013;
    public static final int SKILL_TRANSFER_PAIN = 1262;
    public static final int SKILL_MYSTIC_IMMUNITY = 1411;
    public static final int SKILL_RAID_BLESSING = 2168;
    public static final int SKILL_HINDER_STRIDER = 4258;
    public static final int SKILL_RAID_CURSE_ID = 4515;
    public static final int SKILL_RAID_CURSE_MUTE = 4215;
    public static final int SKILL_EVENT_TIMER = 5239;
    public static final int SKILL_BATTLEFIELD_DEATH_SYNDROME = 5660;
    public static final int SKILL_SERVITOR_SHARE = 1557;
    public static final int SKILL_BETRAY = 1380;
    protected static final int SKILL_TRANSFORM_DISPEL = 619;
    protected static final int SKILL_FINAL_FLYING_FORM = 840;
    protected static final int SKILL_AURA_BIRD_FALCON = 841;
    protected static final int SKILL_AURA_BIRD_OWL = 842;
    protected static final int SKILL_FISHING_MASTERY = 1315;
    private static final int SKILL_DETECTION = 933;
    private static final Logger _log = LoggerFactory.getLogger(Skill.class);
    private static final String OLYMPIAD_KEYS_START_WORD = "Olympiad";
    protected final int[] _itemConsumeId;
    protected final boolean _isItemHandler;
    protected final SkillType _skillType;
    protected final SkillTargetType targetType;
    protected final int _level;
    protected final int _displayId;
    protected final int _effectPoint;
    protected final int _npcId;
    protected final int skillRadius;
    protected final double _absorbPart;
    private final int[] _itemConsume;
    private final int _referenceItemId;
    private final int _referenceItemMpConsume;
    private final Map<String, String> olympiadValues;
    private final int hashCode;
    private final List<Integer> _teachers; // which NPC teaches
    private final List<ClassId> _canLearn; // which classes can learn
    private final boolean isAltUse;
    private final boolean isBehind;
    private final boolean isCancelable;
    private final boolean isCorpse;
    private final boolean common;
    private final boolean offensive;
    private final boolean _isBuff;
    private final boolean _isPvpSkill;
    private final boolean _isNotUsedByAI;
    private final boolean _isFishingSkill;
    private final boolean _isPvm;
    private final boolean _isForceUse;
    private final boolean _isNewbie;
    private final boolean _isPreservedOnDeath;
    private final boolean _isHeroic;
    private final boolean _isSaveable;
    private final boolean _isSkillTimePermanent;
    private final boolean _isReuseDelayPermanent;
    private final boolean _isReflectable;
    private final boolean _isSuicideAttack;
    private final boolean isShieldignore;
    private final boolean _isUndeadOnly;
    private final Ternary _isUseSS;
    private final boolean _isSoulBoost;
    private final boolean _isChargeBoost;
    private final boolean _isUsingWhileCasting;
    private final boolean _isIgnoreResists;
    private final boolean _isIgnoreInvul;
    private final boolean _isTrigger;
    private final boolean _isNotAffectedByMute;
    private final boolean _basedOnTargetDebuff;
    private final boolean deathlink;
    private final boolean _hideStartMessage;
    private final boolean _hideUseMessage;
    private final boolean _flyingTransformUsage;
    private final boolean _canUseTeleport;
    private final boolean _isProvoke;
    private final boolean _isSelfDispellable;
    private final boolean _ignoreSkillMastery;
    private final SkillTrait _traitType;
    private final BaseStats _saveVs;
    private final FlyType _flyType;
    private final boolean _flyToBack;
    private final int _activateRate;
    private final List<Integer> _affectLimit;
    private final int _cancelTarget;
    private final int _condCharges;
    private final int _coolTime;
    private final int _delayedEffect;
    private final int energyConsume;
    private final int _elementPower;
    private final int _flyRadius;
    private final int vitConsume;
    private final int _levelModifier;
    private final int _matak;
    private final int _minPledgeClass;
    private final int _minRank;
    private final int _negatePower;
    private final int _negateSkill;
    private final int _numCharges;
    private final int soulsConsume;
    private final int _symbolId;
    private final int _weaponsAllowed;
    private final int _castCount;
    private final int criticalRate;
    private final double powerPvP;
    private final double powerPvE;
    private final String _icon;
    protected int id;
    protected int _displayLevel;
    protected double power;
    protected double _lethal1;
    protected double _lethal2;
    private List<EffectTemplate> _effectTemplates = new ArrayList<>();
    private List<AddedSkill> addedSkills = new ArrayList<>();
    private boolean _isOverhit;
    private boolean _isCubicSkill = false;
    private SkillOpType _operateType;
    private SkillMagicType _magicType;
    private NextAction _nextAction;
    private Element element;
    private List<Condition> preCondition = new ArrayList<>();
    private int _baseLevel;
    private int _castRange;
    private int hitTime;
    private int _hpConsume;
    private int _magicLevel;
    private int skillInterruptTime;
    private int _enchantLevelCount;
    private long _reuseDelay;
    private double _mpConsume1;
    private double _mpConsume2;
    private String _name;
    private boolean _hasNotSelfEffects = false;

    protected Skill(StatsSet set) {
        // _set = set;
        id = set.getInteger("skill_id");
        _level = set.getInteger("level");
        _displayId = set.getInteger("displayId", id);
        _displayLevel = set.getInteger("displayLevel", _level);
        _baseLevel = set.getInteger("base_level");
        _name = set.getString("name");
        _operateType = set.getEnum("operateType", SkillOpType.class);
        _isNewbie = set.getBool("isNewbie", false);
        _isSelfDispellable = set.getBool("isSelfDispellable", true);
        _ignoreSkillMastery = set.getBool("ignoreSkillMastery", false);
        _isPreservedOnDeath = set.getBool("isPreservedOnDeath", false);
        _isHeroic = set.getBool("isHeroic", false);
        isAltUse = set.getBool("altUse", false);
        _mpConsume1 = set.getInteger("mpConsume1", 0);
        _mpConsume2 = set.getInteger("mpConsume2", 0);
        energyConsume = set.getInteger("energyConsume", 0);
        vitConsume = set.getInteger("vitConsume", 0);
        _hpConsume = set.getInteger("hpConsume", 0);
        soulsConsume = set.getInteger("soulsConsume", 0);
        _isSoulBoost = set.getBool("soulBoost", false);
        _isChargeBoost = set.getBool("chargeBoost", false);
        _isProvoke = set.getBool("provoke", false);
        _isUsingWhileCasting = set.getBool("isUsingWhileCasting", false);
        _matak = set.getInteger("mAtk", 0);
        _isUseSS = Ternary.valueOf(set.getString("useSS", Ternary.DEFAULT.toString()).toUpperCase());
        _magicLevel = set.getInteger("magicLevel", 0);
        _castCount = set.getInteger("castCount", 0);
        _castRange = set.getInteger("castRange", 40);
        String _baseValues = set.getString("baseValues", null);

        String s1 = set.getString("itemConsumeCount", "");
        String s2 = set.getString("itemConsumeId", "");

        if (s1.length() == 0) {
            _itemConsume = new int[]
                    {
                            0
                    };
        } else {
            String[] s = s1.split(" ");
            _itemConsume = new int[s.length];
            for (int i = 0; i < s.length; i++) {
                _itemConsume[i] = toInt(s[i]);
            }
        }

        if (s2.length() == 0) {
            _itemConsumeId = new int[]
                    {
                            0
                    };
        } else {
            String[] s = s2.split(" ");
            _itemConsumeId = new int[s.length];
            for (int i = 0; i < s.length; i++) {
                _itemConsumeId[i] = toInt(s[i]);
            }
        }

        _referenceItemId = set.getInteger("referenceItemId", 0);
        _referenceItemMpConsume = set.getInteger("referenceItemMpConsume", 0);

        _isItemHandler = set.getBool("isHandler", false);
        common = set.getBool("common", false);
        _isSaveable = set.getBool("isSaveable", true);
        _coolTime = set.getInteger("coolTime", 0);
        skillInterruptTime = set.getInteger("hitCancelTime", 0);
        _reuseDelay = set.getLong("reuseDelay", 0);
        hitTime = set.getInteger("hitTime", 0);
        skillRadius = set.getInteger("skillRadius", 80);
        targetType = set.getEnum("target", SkillTargetType.class);
        _magicType = set.getEnum("magicType", SkillMagicType.class, SkillMagicType.PHYSIC);
        _traitType = set.getEnum("trait", SkillTrait.class, null);
        _saveVs = set.getEnum("saveVs", BaseStats.class, null);
        _hideStartMessage = set.getBool("isHideStartMessage", false);
        _hideUseMessage = set.getBool("isHideUseMessage", false);
        _isUndeadOnly = set.getBool("undeadOnly", false);
        isCorpse = set.getBool("corpse", false);
        power = set.getDouble("power", 0.);
        powerPvP = set.getDouble("powerPvP", 0.);
        powerPvE = set.getDouble("powerPvE", 0.);
        _effectPoint = set.getInteger("effectPoint", 0);
        _nextAction = NextAction.valueOf(set.getString("nextAction", "DEFAULT").toUpperCase());
        _skillType = set.getEnum("skillType", SkillType.class);
        _isSuicideAttack = set.getBool("isSuicideAttack", false);
        _isSkillTimePermanent = set.getBool("isSkillTimePermanent", false);
        _isReuseDelayPermanent = set.getBool("isReuseDelayPermanent", false);
        deathlink = set.getBool("deathlink", false);
        _basedOnTargetDebuff = set.getBool("basedOnTargetDebuff", false);
        _isNotUsedByAI = set.getBool("isNotUsedByAI", false);
        _isIgnoreResists = set.getBool("isIgnoreResists", false);
        _isIgnoreInvul = set.getBool("isIgnoreInvul", false);
        _isTrigger = set.getBool("isTrigger", false);
        _isNotAffectedByMute = set.getBool("isNotAffectedByMute", false);
        _flyingTransformUsage = set.getBool("flyingTransformUsage", false);
        _canUseTeleport = set.getBool("canUseTeleport", true);

        element = Element.getElement(set.getString("element", "NONE"));

        _elementPower = set.getInteger("elementPower", 0);

        _activateRate = set.getInteger("activateRate", -1);
        _affectLimit = set.getIntegerList("affectLimit", Arrays.asList(0, 0));
        _levelModifier = set.getInteger("levelModifier", 1);
        isCancelable = set.getBool("cancelable", true);
        _isReflectable = set.getBool("reflectable", true);
        isShieldignore = set.getBool("shieldignore", false);
        criticalRate = set.getInteger("criticalRate", 0);
        _isOverhit = set.getBool("overHit", false);
        _weaponsAllowed = set.getInteger("weaponsAllowed", 0);
        _minPledgeClass = set.getInteger("minPledgeClass", 0);
        _minRank = set.getInteger("minRank", 0);
        offensive = set.getBool("offensive", _skillType.isOffensive());
        _isBuff = set.getBool("offensive", _skillType.isBuff());
        _isPvpSkill = set.getBool("isPvpSkill", _skillType.isPvpSkill());
        _isFishingSkill = set.getBool("isFishingSkill", false);
        _isPvm = set.getBool("isPvm", _skillType.isPvM());
        _isForceUse = set.getBool("isForceUse", false);
        isBehind = set.getBool("behind", false);
        _symbolId = set.getInteger("symbolId", 0);
        _npcId = set.getInteger("npcId", 0);
        _flyType = FlyType.valueOf(set.getString("flyType", "NONE").toUpperCase());
        _flyToBack = set.getBool("flyToBack", false);
        _flyRadius = set.getInteger("flyRadius", 200);
        _negateSkill = set.getInteger("negateSkill", 0);
        _negatePower = set.getInteger("negatePower", Integer.MAX_VALUE);
        _numCharges = set.getInteger("num_charges", 0);
        _condCharges = set.getInteger("cond_charges", 0);
        _delayedEffect = set.getInteger("delayedEffect", 0);
        _cancelTarget = set.getInteger("cancelTarget", 0);
        boolean _skillInterrupt = set.getBool("skillInterrupt", false);
        _lethal1 = set.getDouble("lethal1", 0.);
        _lethal2 = set.getDouble("lethal2", 0.);
        _absorbPart = set.getDouble("absorbPart", 0.);
        _icon = set.getString("icon", "");

        StringTokenizer st = new StringTokenizer(set.getString("addSkills", ""), ";");
        while (st.hasMoreTokens()) {
            int id = toInt(st.nextToken());
            int level = toInt(st.nextToken());
            if (level == -1) {
                level = _level;
            }
            addedSkills.add(new AddedSkill(id, level));
        }

        if (_nextAction == NextAction.DEFAULT) {
            switch (_skillType) {
                case PDAM:
                case CPDAM:
                case LETHAL_SHOT:
                case SPOIL:
                case SOWING:
                case STUN:
                case DRAIN_SOUL:
                    _nextAction = NextAction.ATTACK;
                    break;
                default:
                    _nextAction = NextAction.NONE;
            }
        }

        String canLearn = set.getString("canLearn", null);
        if (canLearn == null) {
            _canLearn = null;
        } else {
            _canLearn = new ArrayList<>();
            st = new StringTokenizer(canLearn, " \r\n\t,;");
            while (st.hasMoreTokens()) {
                String cls = st.nextToken();
                _canLearn.add(ClassId.valueOf(cls));
            }
        }

        String teachers = set.getString("teachers", null);
        if (teachers == null) {
            _teachers = null;
        } else {
            _teachers = new ArrayList<>();
            st = new StringTokenizer(teachers, " \r\n\t,;");
            while (st.hasMoreTokens()) {
                String npcid = st.nextToken();
                _teachers.add(toInt(npcid));
            }
        }

        hashCode = (id * 1023) + _level;

        //Custom values when player is in Olympiad
        olympiadValues = new HashMap<>();
        for (Map.Entry<String, Object> entry : set.entrySet()) {
            if (entry.getKey().startsWith(OLYMPIAD_KEYS_START_WORD)) {
                olympiadValues.put(entry.getKey().substring(OLYMPIAD_KEYS_START_WORD.length()), String.valueOf(entry.getValue()));
            }
        }
    }

    public boolean isPenalty() {
        return (_skillType == SkillType.DEATH_PENALTY) || (id == 4267) || (id == 4270);
    }

    private boolean getWeaponDependancy(Creature activeChar) {
        if (_weaponsAllowed == 0) {
            return true;
        }

        if ((activeChar.getActiveWeaponInstance() != null) && (activeChar.getActiveWeaponItem() != null)) {
            if ((activeChar.getActiveWeaponItem().getItemType().mask() & _weaponsAllowed) != 0) {
                return true;
            }
        }

        if ((activeChar.getSecondaryWeaponInstance() != null) && (activeChar.getSecondaryWeaponItem() != null)) {
            if ((activeChar.getSecondaryWeaponItem().getItemType().mask() & _weaponsAllowed) != 0) {
                return true;
            }
        }

        activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(_displayId, _displayLevel));

        return false;
    }

    public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        Player player = activeChar.getPlayer();

        if (activeChar.isDead()) {
            return false;
        }

        if ((target != null) && (activeChar.getReflection() != target.getReflection())) {
            activeChar.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
            return false;
        }

        if (!getWeaponDependancy(activeChar)) {
            return false;
        }

        if (activeChar.isUnActiveSkill(id)) {
            return false;
        }

        if (first && activeChar.isSkillDisabled(this)) {
            activeChar.sendReuseMessage(this);
            return false;
        }

        // DS: Clarity does not affect mpConsume1
        if (first && (activeChar.getCurrentMp() < (isMagic() ? _mpConsume1 + activeChar.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, _mpConsume2, target, this) : _mpConsume1 + activeChar.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, _mpConsume2, target, this)))) {
            activeChar.sendPacket(SystemMsg.NOT_ENOUGH_MP);
            return false;
        }

        if (activeChar.getCurrentHp() < (_hpConsume + 1)) {
            activeChar.sendPacket(SystemMsg.NOT_ENOUGH_HP);
            return false;
        }

        if ((activeChar.isPlayer()) && (vitConsume > 0)) {
            Player p = (Player) activeChar;
            if (p.getVitality() < (vitConsume + 1)) {
                p.sendPacket(Msg.NOT_ENOUGH_MATERIALS);
                return false;
            }
        } else if ((!activeChar.isPlayer()) && (vitConsume > 0)) {
            return false;
        }

        if (!(_isItemHandler || isAltUse) && activeChar.isMuted(this)) {
            return false;
        }

        if (soulsConsume > activeChar.getConsumedSouls()) {
            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SOULS);
            return false;
        }

        // TODO move the consumption of the formulas here
        if ((activeChar.getIncreasedForce() < _condCharges) || (activeChar.getIncreasedForce() < _numCharges)) {
            activeChar.sendPacket(SystemMsg.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY_);
            return false;
        }

        if (player != null) {
            if (player.isInFlyingTransform() && _isItemHandler && !flyingTransformUsage()) {
                player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(getItemConsumeId()[0]));
                return false;
            }

            if (player.isInBoat()) {
                // On airships can use skills-handlers
                if (player.getBoat().isAirShip() && !_isItemHandler) {
                    return false;
                }

                // With sea vessels can fish
                if (player.getBoat().isVehicle() && !((this instanceof FishingSkill) || (this instanceof ReelingPumping))) {
                    return false;
                }
            }

            if (player.isInObserverMode()) {
                activeChar.sendPacket(SystemMsg.OBSERVERS_CANNOT_PARTICIPATE);
                return false;
            }

            if (first && (_itemConsume[0] > 0)) {
                for (int i = 0; i < _itemConsume.length; i++) {
                    Inventory inv = ((Playable) activeChar).getInventory();
                    if (inv == null) {
                        inv = player.getInventory();
                    }
                    ItemInstance requiredItems = inv.getItemByItemId(_itemConsumeId[i]);
                    if ((requiredItems == null) || (requiredItems.getCount() < _itemConsume[i])) {
                        if (activeChar == player) {
                            player.sendPacket(isHandler() ? SystemMsg.INCORRECT_ITEM_COUNT : SystemMsg.THERE_ARE_NOT_ENOUGH_NECESSARY_ITEMS_TO_USE_THE_SKILL);
                        }
                        return false;
                    }
                }
            }

            if (player.isFishing() && !isFishingSkill() && !altUse() && !(activeChar.isSummon() || activeChar.isPet())) {
                if (activeChar == player) {
                    player.sendPacket(SystemMsg.ONLY_FISHING_SKILLS_MAY_BE_USED_AT_THIS_TIME);
                }
                return false;
            }
        }

        if (getFlyType() != FlyType.NONE && ((getId() != 628 && getId() != 821)) && (activeChar.isImmobilized() || activeChar.isRooted())) {
            activeChar.getPlayer().sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
            return false;
        }

        // Fly skill can not be used too close
        if (first && (target != null) && (getFlyType() == FlyType.CHARGE) && activeChar.isInRange(target.getLoc(), Math.min(150, getFlyRadius()))) {
            activeChar.getPlayer().sendPacket(SystemMsg.THERE_IS_NOT_ENOUGH_SPACE_TO_MOVE_THE_SKILL_CANNOT_BE_USED);
            return false;
        }

        SystemMsg msg = checkTarget(activeChar, target, target, forceUse, first);
        if ((msg != null) && (activeChar.getPlayer() != null)) {
            activeChar.getPlayer().sendPacket(msg);
            return false;
        }

        if (preCondition.size() == 0) {
            return true;
        }

        Env env = new Env();
        env.character = activeChar;
        env.skill = this;
        if (getId() == Skill.SKILL_SERVITOR_SHARE)
            env.target = activeChar.getPet();
        else {
            env.target = target;
        }

        if (first) {
            for (Condition n : preCondition) {
                if (!n.test(env)) {
                    SystemMsg cond_msg = n.getSystemMsg();
                    if (cond_msg != null) {
                        if (cond_msg.size() > 0) {
                            activeChar.sendPacket(new SystemMessage2(cond_msg).addSkillName(this));
                        } else {
                            activeChar.sendPacket(cond_msg);
                        }
                    }
                    return false;
                }
            }
        }

        return true;
    }

    public SystemMsg checkTarget(Creature activeChar, Creature target, Creature aimingTarget, boolean forceUse, boolean first) {
        if (getId() == Skill.SKILL_SERVITOR_SHARE) {
            if (activeChar.getPet() == null)
                return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
            if (!activeChar.getPet().isSummon()) {
                return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
            }
            return null;
        }
        if (target == activeChar && isNotTargetAoE() || target == activeChar.getPet() && targetType == SkillTargetType.TARGET_PET_AURA)
            return null;
        if (target == null || isOffensive() && target == activeChar)
            return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
        if (activeChar.getReflection() != target.getReflection())
            return SystemMsg.CANNOT_SEE_TARGET;
        // Whether the target gets in range at the end of caste
        if (!first && target != activeChar && target == aimingTarget && getCastRange() > 0 && getCastRange() != 32767 && !activeChar.isInRange(target.getLoc(), getCastRange() + (getCastRange() < 200 ? 400 : 500)))
            return SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE;
        // For these skills further checks are needed
        if (_skillType == SkillType.TAKECASTLE || _skillType == SkillType.TAKEFORTRESS || _skillType == SkillType.TAKEFLAG)
            return null;
        // Cone skills
        if (!first && target != activeChar && (targetType == SkillTargetType.TARGET_MULTIFACE || targetType == SkillTargetType.TARGET_MULTIFACE_AURA || targetType == SkillTargetType.TARGET_TUNNEL) && (isBehind ? PositionUtils.isFacing(activeChar, target, 120) : !PositionUtils.isFacing(activeChar, target, 60)))
            return SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE;

        // Check on castes over the corpse
        if (target.isDead() != isCorpse && targetType != SkillTargetType.TARGET_AREA_AIM_CORPSE || _isUndeadOnly && !target.isUndead())
            return SystemMsg.INVALID_TARGET;
        // For various bottles and feeding skill, further checks are needed
        if (isAltUse || targetType == SkillTargetType.TARGET_FEEDABLE_BEAST || targetType == SkillTargetType.TARGET_UNLOCKABLE || targetType == SkillTargetType.TARGET_CHEST)
            return null;
        Player player = activeChar.getPlayer();
        if (player != null) {
            // The prohibition to attack civilians in the siege NPC zone on TW. Otherwise way stuffed glasses.
            //if (player.getTerritorySiege() > -1 && target.isNpc() && !(target instanceof L2TerritoryFlagInstance) && !(target.getAI() instanceof DefaultAI) && player.isInZone(ZoneType.Siege))
            //	return Msg.INVALID_TARGET;

            Player pcTarget = target.getPlayer();
            if (pcTarget != null) {
                if (isPvM())
                    return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;

                if (player.isInZone(ZoneType.epic) != pcTarget.isInZone(ZoneType.epic))
                    return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;

                if (pcTarget.isInOlympiadMode() && (!player.isInOlympiadMode() || player.getOlympiadGame() != pcTarget.getOlympiadGame())) // Ð�Ð° Ð²Ñ�Ñ�ÐºÐ¸Ð¹ Ñ�Ð»ÑƒÑ‡Ð°Ð¹
                    return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;

                if (pcTarget.getNonAggroTime() > System.currentTimeMillis())
                    return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;

                if (player.getBlockCheckerArena() > -1 && pcTarget.getBlockCheckerArena() > -1 && targetType == SkillTargetType.TARGET_EVENT)
                    return null;

                if (isOffensive()) {
                    if (player.isInOlympiadMode() && !player.isOlympiadCompStarted()) // Ð‘Ð¾Ð¹ ÐµÑ‰Ðµ Ð½Ðµ Ð½Ð°Ñ‡Ð°Ð»Ñ�Ñ�
                        return SystemMsg.INVALID_TARGET;
                    if (player.isInOlympiadMode() && player.getOlympiadSide() == pcTarget.getOlympiadSide() && !forceUse) // Ð¡Ð²Ð¾ÑŽ ÐºÐ¾Ð¼Ð°Ð½Ð´Ñƒ Ð°Ñ‚Ð°ÐºÐ¾Ð²Ð°Ñ‚ÑŒ Ð½ÐµÐ»ÑŒÐ·Ñ�
                        return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;

                    if (isAoE() && getCastRange() < Integer.MAX_VALUE && !GeoEngine.canSeeTarget(activeChar, target, activeChar.isFlying()))
                        return SystemMsg.CANNOT_SEE_TARGET;
                    //if (!isBuff() && !GeoEngine.canSeeTarget(activeChar, target, activeChar.isFlying()))
                    //	return SystemMsg.CANNOT_SEE_TARGET;
                    if (activeChar.isInZoneBattle() != target.isInZoneBattle() && !player.getPlayerAccess().PeaceAttack)
                        return SystemMsg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE;
                    if ((activeChar.isInZonePeace() || target.isInZonePeace()) && !player.getPlayerAccess().PeaceAttack)
                        return SystemMsg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE;

                    if (activeChar.isInZoneBattle()) {
                        if (!forceUse && !isForceUse() && player.getParty() != null && player.getParty() == pcTarget.getParty())
                            return SystemMsg.INVALID_TARGET;
                        return null; //The remaining conditions in the arenas and on the Olympic Games need not be checked
                    }

                    // Once the enemy and only if it is not lost.
					/*Duel duel1 = player.getDuel();
					Duel duel2 = pcTarget.getDuel();
					if (player != pcTarget && duel1 != null && duel1 == duel2)
					{
						if (duel1.getTeamForPlayer(pcTarget) == duel1.getTeamForPlayer(player))
							return SystemMsg.INVALID_TARGET;
						if (duel1.getDuelState(player.getStoredId()) != Duel.DuelState.Fighting)
							return SystemMsg.INVALID_TARGET;
						if (duel1.getDuelState(pcTarget.getStoredId()) != Duel.DuelState.Fighting)
							return SystemMsg.INVALID_TARGET;
						return null;
					}  */

                    SystemMsg msg;
                    for (GlobalEvent e : player.getEvents()) {
                        if ((msg = e.checkForAttack(target, activeChar, this, forceUse)) != null)
                            return msg;

                        if (e.canAttack(target, activeChar, this, forceUse))
                            return null;
                    }

                    if (isProvoke()) {
                        if (!forceUse && player.getPlayerGroup() == pcTarget.getPlayerGroup())
                            return SystemMsg.INVALID_TARGET;
                        return null;
                    }

                    if (isPvpSkill() || !forceUse || isAoE()) {
                        if (player == pcTarget)
                            return SystemMsg.INVALID_TARGET;
                        if (player.getParty() != null && player.getPlayerGroup() == pcTarget.getPlayerGroup()) // Party and Command Channel check.
                            return SystemMsg.INVALID_TARGET;
                        if (player.getClanId() != 0 && player.getClanId() == pcTarget.getClanId())
                            return SystemMsg.INVALID_TARGET;
                        if ((player.isInParty()) && (player.getParty().getCommandChannel() != null) && (pcTarget.isInParty()) && (pcTarget.getParty().getCommandChannel() != null) && (player.getParty().getCommandChannel() == pcTarget.getParty().getCommandChannel()))
                            return SystemMsg.INVALID_TARGET;
                        if ((player.getClan() != null) && (player.getClan().getAlliance() != null) && (pcTarget.getClan() != null) && (pcTarget.getClan().getAlliance() != null) && (player.getClan().getAlliance() == pcTarget.getClan().getAlliance()))
                            return SystemMsg.INVALID_TARGET;
                    }

                    if (activeChar.isInZone(ZoneType.SIEGE) && target.isInZone(ZoneType.SIEGE))
                        return null;

                    if (activeChar.isInZonePvP() && target.isInZonePvP())
                        return null;

                    if (player.atMutualWarWith(pcTarget))
                        return null;
                    if (isForceUse())
                        return null;
                    // DS: Removed. Protection from divorce to the flag with a spear
					/*if (!forceUse && player.getPvpFlag() == 0 && pcTarget.getPvpFlag() != 0 && aimingTarget != target)
						return SystemMsg.INVALID_TARGET;*/
                    if (pcTarget.getPvpFlag() != 0)
                        return null;
                    if (pcTarget.getKarma() > 0)
                        return null;
                    if (forceUse && !isPvpSkill() && (!isAoE() || aimingTarget == target))
                        return null;

                    return SystemMsg.INVALID_TARGET;
                }

                if (pcTarget == player)
                    return null;

                if (player.isInOlympiadMode() && !forceUse && player.getOlympiadSide() != pcTarget.getOlympiadSide()) // Ð§ÑƒÐ¶Ð¾Ð¹ ÐºÐ¾Ð¼Ð°Ð½Ð´Ðµ Ð¿Ð¾Ð¼Ð¾Ð³Ð°Ñ‚ÑŒ Ð½ÐµÐ»ÑŒÐ·Ñ�
                    return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;

                if (!activeChar.isInZoneBattle() && target.isInZoneBattle())
                    return SystemMsg.INVALID_TARGET;
                // DS: Off can not use attacking skills of the peace zone in the field.
				/*if (activeChar.isInZonePeace() && !target.isInZonePeace())
					return SystemMsg.INVALID_TARGET;*/

                // Alexander - Dont allow non offensive skills casted on events to enemies
                if (!isOffensive()) {
                    for (GlobalEvent e : player.getEvents())
                        if (e.canAttack(target, activeChar, this, false))
                            return null;
                }

                if (forceUse || isForceUse())
                    return null;

				/*if (player.getDuel() != null && pcTarget.getDuel() != player.getDuel())
					return SystemMsg.INVALID_TARGET;
				if (player != pcTarget && player.getDuel() != null && pcTarget.getDuel() != null && pcTarget.getDuel() == pcTarget.getDuel())
					return SystemMsg.INVALID_TARGET;         */

                if (player.getParty() != null && player.getParty() == pcTarget.getParty())
                    return null;
                if (player.getClanId() != 0 && player.getClanId() == pcTarget.getClanId())
                    return null;

                if (player.atMutualWarWith(pcTarget))
                    return SystemMsg.INVALID_TARGET;
                if (pcTarget.getPvpFlag() != 0)
                    return SystemMsg.INVALID_TARGET;
                if (pcTarget.getKarma() > 0)
                    return SystemMsg.INVALID_TARGET;

                return null;
            }
        }

        if (isAoE() && isOffensive() && getCastRange() < Integer.MAX_VALUE && !GeoEngine.canSeeTarget(activeChar, target, activeChar.isFlying()))
            return SystemMsg.CANNOT_SEE_TARGET;
        if (!forceUse && !isForceUse() && !isOffensive() && target.isAutoAttackable(activeChar))
            return SystemMsg.INVALID_TARGET;
        if (!forceUse && !isForceUse() && isOffensive() && !target.isAutoAttackable(activeChar))
            return SystemMsg.INVALID_TARGET;
        if (!target.isAttackable(activeChar))
            return SystemMsg.INVALID_TARGET;

        return null;
    }

    public final Creature getAimingTarget(Creature activeChar, GameObject obj) {
        Creature target = (obj == null) || !obj.isCreature() ? null : (Creature) obj;
        switch (targetType) {
            case TARGET_ALLY:
            case TARGET_CLAN:
            case TARGET_PARTY:
            case TARGET_PARTY_NO_ME:
            case TARGET_PARTY_NO_SUMMON:
            case TARGET_CLAN_ONLY:
            case TARGET_FRIEND:
            case TARGET_SELF:
                return activeChar;
            case TARGET_AURA:
            case TARGET_COMMCHANNEL:
            case TARGET_MULTIFACE_AURA:
                return activeChar;
            case TARGET_HOLY:
                return (target != null) && activeChar.isPlayer() && target.isArtefact() ? target : null;
            case TARGET_FLAGPOLE:
                return activeChar;
            case TARGET_UNLOCKABLE:
                return ((target != null) && target.isDoor()) || (target instanceof ChestInstance) ? target : null;
            case TARGET_CHEST:
                return target instanceof ChestInstance ? target : null;
            case TARGET_FEEDABLE_BEAST:
                return target instanceof FeedableBeastInstance ? target : null;
            case TARGET_PET:
            case TARGET_PET_AURA:
                target = activeChar.getPet();
                return (target != null) && (target.isDead() == isCorpse) ? target : null;
            case TARGET_OWNER:
                if (activeChar.isSummon() || activeChar.isPet()) {
                    target = activeChar.getPlayer();
                } else {
                    return null;
                }
                return (target != null) && (target.isDead() == isCorpse) ? target : null;
            case TARGET_ENEMY_PET:
                if ((target == null) || (target == activeChar.getPet()) || !target.isPet()) {
                    return null;
                }
                return target;
            case TARGET_ENEMY_SUMMON:
                if ((target == null) || (target == activeChar.getPet()) || !target.isSummon()) {
                    return null;
                }
                return target;
            case TARGET_ENEMY_SERVITOR:
                if ((target == null) || (target == activeChar.getPet()) || !(target instanceof Summon)) {
                    return null;
                }
                return target;
            case TARGET_EVENT:
                return (target != null) && !target.isDead() && (target.getPlayer().getBlockCheckerArena() > -1) ? target : null;
            case TARGET_ONE:
                return (target != null) && (target.isDead() == isCorpse) && !((target == activeChar) && isOffensive()) && (!_isUndeadOnly || target.isUndead()) ? target : null;
            case TARGET_PARTY_ONE:
                if (target == null) {
                    return null;
                }
                Player player = activeChar.getPlayer();
                Player ptarget = target.getPlayer();
                // self or self pet.
                if ((ptarget != null) && (ptarget == activeChar)) {
                    return target;
                }
                // olympiad party member or olympiad party member pet.
                if ((player != null) && player.isInOlympiadMode() && (ptarget != null) && (player.getOlympiadSide() == ptarget.getOlympiadSide()) && (player.getOlympiadGame() == ptarget.getOlympiadGame()) && (target.isDead() == isCorpse) && !((target == activeChar) && isOffensive()) && (!_isUndeadOnly || target.isUndead())) {
                    return target;
                }
                // party member or party member pet.
                if ((ptarget != null) && (player != null) && (player.getParty() != null) && player.getParty().containsMember(ptarget) && (target.isDead() == isCorpse) && !((target == activeChar) && isOffensive()) && (!_isUndeadOnly || target.isUndead())) {
                    return target;
                }
                return null;
            case TARGET_AREA:
            case TARGET_MULTIFACE:
            case TARGET_TUNNEL:
                return (target != null) && (target.isDead() == isCorpse) && !((target == activeChar) && isOffensive()) && (!_isUndeadOnly || target.isUndead()) ? target : null;
            case TARGET_AREA_AIM_CORPSE:
                return (target != null) && target.isDead() ? target : null;
            case TARGET_CORPSE:
                if ((target == null) || !target.isDead()) {
                    return null;
                }
                if (target.isSummon() && (target != activeChar.getPet())) {
                    return target;
                }
                return target.isNpc() ? target : null;
            case TARGET_CORPSE_PLAYER:
                return (target != null) && target.isPlayable() && target.isDead() ? target : null;
            case TARGET_SIEGE:
                return (target != null) && !target.isDead() && target.isDoor() ? target : null;
            default:
                activeChar.sendMessage("Target type of skill is not currently handled");
                return null;
        }
    }

    @SuppressWarnings("incomplete-switch")
    public List<Creature> getTargets(Creature activeChar, Creature aimingTarget, boolean forceUse) {
        List<Creature> targets;
        if (oneTarget()) {
            targets = new ArrayList<>(1);
            targets.add(aimingTarget);
            return targets;
        } else {
            targets = new ArrayList<>();
        }

        switch (targetType) {
            case TARGET_EVENT: {
                if (activeChar.isPlayer()) {
                    Player player = activeChar.getPlayer();
                    int playerArena = player.getBlockCheckerArena();

                    if (playerArena != -1) {
                        ArenaParticipantsHolder holder = HandysBlockCheckerManager.INSTANCE.getHolder(playerArena);
                        int team = holder.getPlayerTeam(player);
                        // Aura attack
                        for (Player actor : World.getAroundPlayers(activeChar, 250, 100)) {
                            if (holder.getAllPlayers().contains(actor) && (holder.getPlayerTeam(actor) != team)) {
                                targets.add(actor);
                            }
                        }
                    }
                }
                break;
            }
            case TARGET_AREA_AIM_CORPSE:
            case TARGET_AREA:
            case TARGET_MULTIFACE:
            case TARGET_TUNNEL: {
                if ((aimingTarget.isDead() == isCorpse) && (!_isUndeadOnly || aimingTarget.isUndead())) {
                    targets.add(aimingTarget);
                }
                addTargetsToList(targets, aimingTarget, activeChar, forceUse);
                break;
            }
            case TARGET_AURA:
            case TARGET_MULTIFACE_AURA: {
                addTargetsToList(targets, activeChar, activeChar, forceUse);
                break;
            }
            case TARGET_COMMCHANNEL: {
                if (activeChar.getPlayer() != null) {
                    if (activeChar.getPlayer().isInParty()) {
                        if (activeChar.getPlayer().getParty().isInCommandChannel()) {
                            for (Player p : activeChar.getPlayer().getParty().getCommandChannel()) {
                                if (!p.isDead() && p.isInRange(activeChar, skillRadius == 0 ? 600 : skillRadius)) {
                                    targets.add(p);
                                }
                            }
                            addTargetAndPetToList(targets, activeChar.getPlayer(), activeChar.getPlayer());
                            break;
                        }
                        for (Player p : activeChar.getPlayer().getParty().getMembers()) {
                            if (!p.isDead() && p.isInRange(activeChar, skillRadius == 0 ? 600 : skillRadius)) {
                                targets.add(p);
                            }
                        }
                        addTargetAndPetToList(targets, activeChar.getPlayer(), activeChar.getPlayer());
                        break;
                    }
                    targets.add(activeChar);
                    addTargetAndPetToList(targets, activeChar.getPlayer(), activeChar.getPlayer());
                }
                break;
            }
            case TARGET_PET_AURA: {
                addTargetsToList(targets, activeChar.getPet(), activeChar, forceUse);
                break;
            }
            case TARGET_PARTY:
            case TARGET_PARTY_NO_ME:
            case TARGET_PARTY_NO_SUMMON:
            case TARGET_CLAN:
            case TARGET_CLAN_ONLY:
            case TARGET_ALLY: {
                if (activeChar.isMonster() || activeChar.isSiegeGuard()) {
                    targets.add(activeChar);
                    for (Creature c : World.getAroundCharacters(activeChar, skillRadius, 600)) {
                        if (!c.isDead() && (c.isMonster() || c.isSiegeGuard()) /* && ((L2MonsterInstance) c).getFactionId().equals(mob.getFactionId()) */) {
                            targets.add(c);
                        }
                    }
                    break;
                }
                Player player = activeChar.getPlayer();
                if (player == null) {
                    break;
                }
                for (Player target : World.getAroundPlayers(player, skillRadius, 600)) {
                    boolean check = false;
                    switch (targetType) {
                        case TARGET_PARTY:
                        case TARGET_PARTY_NO_ME:
                            check = (player.getParty() != null) && (player.getParty() == target.getParty());
                            break;
                        case TARGET_PARTY_NO_SUMMON:
                            check = (player.getParty() != null) && (player.getParty() == target.getParty()) && (!target.isSummon() || !target.isPet());
                            break;
                        case TARGET_CLAN:
                            check = ((player.getClanId() != 0) && (target.getClanId() == player.getClanId())) || ((player.getParty() != null) && (target.getParty() == player.getParty()));
                            break;
                        case TARGET_CLAN_ONLY:
                            check = (player.getClanId() != 0) && (target.getClanId() == player.getClanId());
                            break;
                        case TARGET_ALLY:
                            check = ((player.getClanId() != 0) && (target.getClanId() == player.getClanId())) || ((player.getAllyId() != 0) && (target.getAllyId() == player.getAllyId()));
                            break;
                    }
                    if (!check) {
                        continue;
                    }

                    // if it is in duel
                    if (player.isInDuel() && target.isInDuel()) {
                        continue;
                    }

                    // for olympiad
                    if (player.isInOlympiadMode() && target.isInOlympiadMode() && (player.getOlympiadSide() != target.getOlympiadSide())) {
                        continue;
                    }
                    if (checkTarget(player, target, aimingTarget, forceUse, false) != null) {
                        continue;
                    }
                    addTargetAndPetToList(targets, player, target);
                }
                if (targetType != SkillTargetType.TARGET_PARTY_NO_ME)
                    addTargetAndPetToList(targets, player, player);
                break;
            }
            case TARGET_FRIEND: {
                if (activeChar.isMonster() || activeChar.isSiegeGuard()) {
                    targets.add(activeChar);
                    for (Creature c : World.getAroundCharacters(activeChar, skillRadius, 900)) {
                        if (!c.isDead() && (c.isMonster() || c.isSiegeGuard())) {
                            targets.add(c);
                        }
                    }
                    break;
                }
                Player player = activeChar.getPlayer();
                for (Player target : World.getAroundPlayers(player, skillRadius, 350)) {
                    boolean check;
                    if (check = (player.getParty() != null) && ((player.getParty() == target.getParty()) || ((player.getClanId() != 0) && (target.getClanId() == player.getClanId())) || ((player.getAllyId() != 0) && (target.getAllyId() == player.getAllyId())))) {
                        if (!check) {
                            continue;
                        }
                    }
                    if (checkTarget(player, target, aimingTarget, forceUse, false) != null) {
                        continue;
                    }
                    addTargetAndPetToList(targets, player, target);
                }
                addTargetAndPetToList(targets, player, player);
                break;
            }
        }
        return targets;
    }

    private void addTargetAndPetToList(List<Creature> targets, Player actor, Player target) {
        // FIXED - Buffs from walls, Resurrect from walls, Heall from walls.
        //if (!GeoEngine.canSeeTarget(actor, target, false) && (getSkillType() != SkillType.RECALL || getSkillType() != SkillType.RESURRECT))
        if (!GeoEngine.canSeeTarget(actor, target, false) && isNotTargetAoE() && getSkillType() != SkillType.RECALL)
            return;

        if (((actor == target) || actor.isInRange(target, skillRadius)) && (target.isDead() == isCorpse)) {
            targets.add(target);
        }
        Summon pet = target.getPet();
        if (actor.isInRange(pet, skillRadius) && (pet.isDead() == isCorpse)) {
            targets.add(pet);
        }
    }

    private void addTargetsToList(List<Creature> targets, Creature aimingTarget, final Creature activeChar, boolean forceUse) {
        int count = 0;
        Polygon terr = null;
        if (targetType == SkillTargetType.TARGET_TUNNEL) {
            // Create a box ("skew" Vertical)

            int radius = 100;
            int zmin1 = activeChar.getZ() - 200;
            int zmax1 = activeChar.getZ() + 200;
            int zmin2 = aimingTarget.getZ() - 200;
            int zmax2 = aimingTarget.getZ() + 200;

            double angle = PositionUtils.convertHeadingToDegree(activeChar.getHeading());
            double radian1 = Math.toRadians(angle - 90);
            double radian2 = Math.toRadians(angle + 90);

            terr = new Polygon().add(activeChar.getX() + (int) (Math.cos(radian1) * radius), activeChar.getY() + (int) (Math.sin(radian1) * radius)).add(activeChar.getX() + (int) (Math.cos(radian2) * radius), activeChar.getY() + (int) (Math.sin(radian2) * radius)).add(aimingTarget.getX() + (int) (Math.cos(radian2) * radius), aimingTarget.getY() + (int) (Math.sin(radian2) * radius)).add(aimingTarget.getX() + (int) (Math.cos(radian1) * radius), aimingTarget.getY() + (int) (Math.sin(radian1) * radius)).setZmin(Math.min(zmin1, zmin2)).setZmax(Math.max(zmax1, zmax2));
        }

        final int affectLimit = _affectLimit.get(0) <= 0 ? Integer.MAX_VALUE : Rnd.get(_affectLimit.get(0), _affectLimit.get(_affectLimit.size() - 1));
        List<Creature> list = aimingTarget.getAroundCharacters(skillRadius, 300);
        if (_skillType == SkillType.AGGRESSION) {
            list.sort((c1, c2) -> Boolean.compare(c1.getAI().getAttackTarget() == activeChar, c2.getAI().getAttackTarget() == activeChar));
        } else if (_skillType == SkillType.SPOIL) {
            list.sort((c1, c2) -> Boolean.compare(c1.isMonster() && ((MonsterInstance) c1).isSpoiled(), c2.isMonster() && ((MonsterInstance) c2).isSpoiled()));
        }

        for (Creature target : list) {
            if ((terr != null) && !terr.isInside(target.getX(), target.getY(), target.getZ()))
                continue;
            if ((target == null) || (activeChar == target) || ((activeChar.getPlayer() != null) && (activeChar.getPlayer() == target.getPlayer())))
                continue;

            if (getId() == SKILL_DETECTION) {
                target.checkAndRemoveInvisible();
            }
            if (checkTarget(activeChar, target, aimingTarget, forceUse, false) != null)
                continue;
            if (!(activeChar instanceof DecoyInstance) && activeChar.isNpc() && target.isNpc())
                continue;

            targets.add(target);
            count++;
            if (isOffensive() && count > affectLimit && !activeChar.isRaid()) {
                break;
            }
        }
    }

    public final void getEffects(Creature effector) {
        getEffects(effector, effector, false, false, false);

    }
    public final void getEffects(Creature effector, boolean calcChance, boolean applyOnCaster) {
        getEffects(effector, effector, calcChance, applyOnCaster, false);
    }

    public final void getEffects(Creature effector, Creature effected) {
        getEffects(effector, effected, false, false, false);
    }

    public final void getEffects(Creature effector, Creature effected, boolean calcChance, boolean applyOnCaster) {
        getEffects(effector, effected, calcChance, applyOnCaster, false);
    }

    private void getEffects(Creature effector, Creature effected, boolean calcChance, boolean applyOnCaster, boolean skillReflected, boolean inNewThread) {
        double timeMult = 1.0;

        if (isMusic()) {
            timeMult = Config.SONGDANCETIME_MODIFIER;
        } else if ((getId() >= 4342) && (getId() <= 4360)) {
            timeMult = Config.CLANHALL_BUFFTIME_MODIFIER;
        } else if (Config.ENABLE_MODIFY_SKILL_DURATION && Config.SKILL_DURATION_LIST.containsKey(getId())) {
            timeMult = Config.SKILL_DURATION_LIST.get(getId());
        }
        getEffects(effector, effected, calcChance, applyOnCaster, 0, timeMult, skillReflected, false);
    }

    protected final void getEffects(Creature effector, Creature effected, boolean calcChance, boolean applyOnCaster, boolean skillReflected) {
        double timeMult = 1.0;

        if (isMusic()) {
            timeMult = Config.SONGDANCETIME_MODIFIER;
        } else if ((getId() >= 4342) && (getId() <= 4360)) {
            timeMult = Config.CLANHALL_BUFFTIME_MODIFIER;
        } else if (Config.ENABLE_MODIFY_SKILL_DURATION && Config.SKILL_DURATION_LIST.containsKey(getId())) {
            timeMult = Config.SKILL_DURATION_LIST.get(getId());
        }
        getEffects(effector, effected, calcChance, applyOnCaster, 0, timeMult, skillReflected);
    }

    /**
     * Apply effects skill @ Param effector character, from which comes the action skill, caster @ Param effected character, on which the skill @ Param calcChance if true, expect a chance to apply effects @ Param applyOnCaster if true, apply effects only to the caster prednazanchennye @ Param
     * timeConst change the duration of the effects to this constant (in milliseconds) @ Param timeMult change the duration of the effects of this factor with the @ Param skillReflected means that skill was recognized and the effects also need to reflect
     */
    public final void getEffects(final Creature effector, final Creature effected, final boolean calcChance, final boolean applyOnCaster, final long timeConst, final double timeMult, final boolean skillReflected) {
        this.getEffects(effector, effected, calcChance, applyOnCaster, timeConst, timeMult, skillReflected, true);
    }

    /**
     * Apply effects skill @ Param effector character, from which comes the action skill, caster @ Param effected character, on which the skill @ Param calcChance if true, expect a chance to apply effects @ Param applyOnCaster if true, apply effects only to the caster prednazanchennye @ Param
     * timeConst change the duration of the effects to this constant (in milliseconds) @ Param timeMult change the duration of the effects of this factor with the @ Param skillReflected means that skill was recognized and the effects also need to reflect
     */
    private void getEffects(final Creature effector, final Creature effected, final boolean calcChance, final boolean applyOnCaster, final long timeConst, final double timeMult, final boolean skillReflected, boolean inNewThread) {
        if (isPassive() || !hasEffects() || (effector == null) || (effected == null)) {
            return;
        }

        if ((effected.isEffectImmune() || (effected.isInvul() && isOffensive() && !isIgnoreInvul())) && (effector != effected)) {
            if (effector.isPlayer()) {
                effector.sendPacket(new SystemMessage2(SystemMsg.C1_HAS_RESISTED_YOUR_S2).addName(effected).addSkillName(_displayId, _displayLevel));
            }
            return;
        }

        if (effected.isDoor() || (effected.isAlikeDead() && !isPreservedOnDeath())) {
            return;
        }

        Runnable effectRunnable = () -> {
            boolean success = false;
            boolean skillMastery = false;
            int sps = effector.getChargedSpiritShot();

            // Check for skill mastery duration time increase
            if (effector.getSkillMastery(getId()) == 2) {
                skillMastery = true;
                effector.removeSkillMastery(getId());
            }

            for (EffectTemplate et : getEffectTemplates()) {
                if ((applyOnCaster != et._applyOnCaster) || (et._count == 0)) {
                    continue;
                }

                Creature character = et._applyOnCaster || (et._isReflectable && skillReflected) ? effector : effected;
                List<Creature> targets = new ArrayList<>(1);
                targets.add(character);

                if (et._applyOnSummon && character.isPlayer()) {
                    Summon summon = character.getPlayer().getPet();
                    if ((summon != null) && summon.isSummon() && !isOffensive() && !isToggle() && !isCubicSkill()) {
                        targets.add(summon);
                    }
                }

                loop:
                for (Creature target : targets) {
                    if (target.isAlikeDead() && !isPreservedOnDeath()) {
                        continue;
                    }

                    if (target.isRaid() && et.getEffectType().isRaidImmune()) {
                        continue;
                    }

                    if (((effected.isBuffImmune() && !isOffensive()) || (effected.isDebuffImmune() && isOffensive())) && (et.getPeriod() > 0) && (effector != effected)) {
                        continue;
                    }

                    if (isBlockedByChar(target, et)) {
                        continue;
                    }

                    if (et._stackOrder == -1) {
                        if (!et._stackType.equals(EffectTemplate.NO_STACK)) {
                            for (Effect e : target.getEffectList().getAllEffects()) {
                                if (e.getStackType().equalsIgnoreCase(et._stackType)) {
                                    continue loop;
                                }
                            }
                        } else if (target.getEffectList().getEffectsBySkillId(getId()) != null) {
                            continue;
                        }
                    }

                    if (getId() == Skill.SKILL_SERVITOR_SHARE) {
                        target = effector.getPlayer().getPet();
                    }

                    if (applyOnCaster && getId() == Skill.SKILL_SERVITOR_SHARE) {
                        target = effector.getPlayer();
                    }

                    Env env = new Env(effector, target, Skill.this);

                    int chance = et.chance(getActivateRate());
                    if ((calcChance || (chance >= 0)) && !et._applyOnCaster) {
                        env.value = chance;
                        if (!Formulas.calcSkillSuccess(env, et, sps)) {
                            // effector.sendPacket(new SystemMessage(SystemMessage.C1_HAS_RESISTED_YOUR_S2).addString(effected.getName()).addSkillName(_displayId, _displayLevel));
                            continue;
                        }
                    }

                    if (_isReflectable && et._isReflectable && isOffensive() && (target != effector) && !effector.isTrap()) {
                        if (Rnd.chance(target.calcStat(isMagic() ? Stats.REFLECT_MAGIC_DEBUFF : Stats.REFLECT_PHYSIC_DEBUFF, 0, effector, Skill.this))) {
                            target.sendPacket(new SystemMessage2(SystemMsg.YOU_COUNTERED_C1S_ATTACK).addName(effector));
                            effector.sendPacket(new SystemMessage2(SystemMsg.C1_DODGES_THE_ATTACK).addName(target));
                            target = effector;
                            env.target = target;
                        }
                    }

                    if (success) {
                        env.value = Integer.MAX_VALUE;
                    }

                    final Effect e = et.getEffect(env);
                    if (e != null) {
                        if (chance > 0) {
                            success = true;
                        }
                        if (e.isOneTime()) {
                            if (e.checkCondition()) {
                                e.onStart();
                                e.onActionTime();
                                e.onExit();
                            }
                        } else {
                            int count = et.getCount();
                            long period = et.getPeriod();

                            // Check for skill mastery duration time increase
                            if (skillMastery) {
                                if (count > 1) {
                                    count *= 2;
                                } else {
                                    period *= 2;
                                }
                            }

                            if (!et._applyOnCaster && isOffensive() && !isIgnoreResists() && !effector.isRaid()) {
                                double res = 0;
                                if (et.getEffectType().getResistType() != null) {
                                    res += effected.calcStat(et.getEffectType().getResistType(), effector, Skill.this);
                                }
                                if (et.getEffectType().getAttributeType() != null) {
                                    res -= effector.calcStat(et.getEffectType().getAttributeType(), effected, Skill.this);
                                }

                                res += effected.calcStat(Stats.DEBUFF_RESIST, effector, Skill.this);

                                if (res != 0) {
                                    double mod = 1 + Math.abs(0.005 * res);
                                    if (res > 0) {
                                        mod = 1. / mod;
                                    }

                                    if (count > 1) {
                                        count = (int) Math.round(Math.max(count * mod, 1));
                                    } else {
                                        period = Math.round(Math.max(period * mod, 1));
                                    }
                                }
                            }

                            if (timeConst > 0L) {
                                if (count > 1) {
                                    period = Math.max(timeConst / count, 1);
                                } else {
                                    period = timeConst;
                                }
                            } else if (timeMult > 1.0) {
                                if (count > 1) {
                                    count *= timeMult;
                                } else {
                                    period *= timeMult;
                                }
                            }

                            if (e.isOffensive())
                                effected.addReceivedDebuff(getId(), period * count);

                            e.setCount(count);
                            e.setPeriod(period);
                            e.schedule();
                        }
                    }
                }
            }
            if (calcChance) {
                if (success) {
                    effector.sendPacket(new SystemMessage2(SystemMsg.S1_HAS_SUCCEEDED).addSkillName(_displayId, _displayLevel));
                } else {
                    effector.sendPacket(new SystemMessage2(SystemMsg.S1_HAS_FAILED).addSkillName(_displayId, _displayLevel));
                }
            }
        };

        if (inNewThread)
            ThreadPoolManager.INSTANCE.execute(effectRunnable);
        else
            effectRunnable.run();
    }

    public final void attach(EffectTemplate effect) {
        _effectTemplates.add(effect);
        if (!effect._applyOnCaster)
            _hasNotSelfEffects = true;
    }

    public List<EffectTemplate> getEffectTemplates() {
        return _effectTemplates;
    }

    public boolean hasEffects() {
        return _effectTemplates.size() > 0;
    }

    /**
     * Возвращает true если у скилла есть эффекты без флага applyOnCaster
     */
    public boolean hasNotSelfEffects() {
        return _hasNotSelfEffects;
    }

    final List<Func> getStatFuncs() {
        return getStatFuncs(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        return hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    public final void attach(Condition c) {
        preCondition.add(c);
    }

    public final boolean altUse() {
        return isAltUse;
    }

    public final boolean canTeachBy(int npcId) {
        return (_teachers == null) || _teachers.contains(npcId);
    }

    public final int getActivateRate() {
        return _activateRate;
    }

    public List<AddedSkill> getAddedSkills() {
        return addedSkills;
    }

    public final boolean getCanLearn(ClassId cls) {
        return (_canLearn == null) || _canLearn.contains(cls);
    }

    /**
     * @return Returns the castRange.
     */
    public final int getCastRange() {
        return _castRange;
    }

    public void setCastRange(int castRange) {
        _castRange = castRange;
    }

    public final int getAOECastRange() {
        return Math.max(_castRange, skillRadius);
    }

    public int getCondCharges() {
        return _condCharges;
    }

    public final int getCoolTime() {
        return _coolTime;
    }

    public boolean getCorpse() {
        return isCorpse;
    }

    public int getDelayedEffect() {
        return _delayedEffect;
    }

    public final int getDisplayId() {
        return _displayId;
    }

    public int getDisplayLevel() {
        return _displayLevel;
    }

    public void setDisplayLevel(int lvl) {
        _displayLevel = lvl;
    }

    public int getEffectPoint() {
        return _effectPoint;
    }

    private Effect getSameByStackType(List<Effect> list) {
        Effect ret;
        for (EffectTemplate et : getEffectTemplates()) {
            if ((et != null) && ((ret = et.getSameByStackType(list)) != null)) {
                return ret;
            }
        }
        return null;
    }

    public Effect getSameByStackType(EffectList list) {
        return getSameByStackType(list.getAllEffects());
    }

    public Effect getSameByStackType(Creature actor) {
        return getSameByStackType(actor.getEffectList().getAllEffects());
    }

    public final Element getElement() {
        return element;
    }

    public final int getElementPower() {
        return _elementPower;
    }

    public Skill getFirstAddedSkill() {
        if (addedSkills.size() == 0) {
            return null;
        }
        return addedSkills.get(0).getSkill();
    }

    public int getFlyRadius() {
        return _flyRadius;
    }

    public FlyType getFlyType() {
        return _flyType;
    }

    public boolean isFlyToBack() {
        return _flyToBack;
    }

    public final int getHitTime() {
        return hitTime;
    }

    public void setHitTime(int hitTime) {
        this.hitTime = hitTime;
    }

    final int getVitConsume() {
        return vitConsume;
    }

    /**
     * @return Returns the hpConsume.
     */
    public final int getHpConsume() {
        return _hpConsume;
    }

    public void setHpConsume(int hpConsume) {
        _hpConsume = hpConsume;
    }

    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return Returns the itemConsume.
     */
    public final int[] getItemConsume() {
        return _itemConsume;
    }

    /**
     * @return Returns the itemConsumeId.
     */
    public final int[] getItemConsumeId() {
        return _itemConsumeId;
    }

    /**
     * @return
     */
    public final int getReferenceItemId() {
        return _referenceItemId;
    }

    /**
     * @return
     */
    public final int getReferenceItemMpConsume() {
        return _referenceItemMpConsume;
    }

    /**
     * @return Returns the level.
     */
    public final int getLevel() {
        return _level;
    }

    public final int getBaseLevel() {
        return _baseLevel;
    }

    public final void setBaseLevel(int baseLevel) {
        _baseLevel = baseLevel;
    }

    public final int getLevelModifier() {
        return _levelModifier;
    }

    public final int getMagicLevel() {
        return _magicLevel;
    }

    public final void setMagicLevel(int newlevel) {
        _magicLevel = newlevel;
    }

    public int getMatak() {
        return _matak;
    }

    public int getMinPledgeClass() {
        return _minPledgeClass;
    }

    public int getMinRank() {
        return _minRank;
    }

    /**
     * @return Returns the mpConsume as _mpConsume1 + _mpConsume2.
     */
    public final double getMpConsume() {
        return _mpConsume1 + _mpConsume2;
    }

    /**
     * @return Returns the mpConsume1.
     */
    public final double getMpConsume1() {
        return _mpConsume1;
    }

    public void setMpConsume1(double mpConsume1) {
        _mpConsume1 = mpConsume1;
    }

    /**
     * @return Returns the mpConsume2.
     */
    public final double getMpConsume2() {
        return _mpConsume2;
    }

    public void setMpConsume2(double mpConsume2) {
        _mpConsume2 = mpConsume2;
    }

    /**
     * @return Returns the name.
     */
    public final String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public int getNegatePower() {
        return _negatePower;
    }

    public int getNegateSkill() {
        return _negateSkill;
    }

    public NextAction getNextAction() {
        return _nextAction;
    }

    public int getNpcId() {
        return _npcId;
    }

    public int getNumCharges() {
        return _numCharges;
    }

    public final double getPower(Creature target) {
        if (target != null) {
            if (target.isPlayable()) {
                if (!BalancerConfig.CUSTOM_POWER_SKILLS_ENABLED)
                    return getPowerPvP();

                double skillPower = getPowerPvP();
                int skillId = getId();
                boolean debug = BalancerConfig.CUSTOM_POWER_SKILLS_DEBUG;

                if (debug) {
                    System.out.println("==== Custom CalculatorPower Start ====");
                    System.out.println("ID: " + skillId + " Base Power " + skillPower);
                }

                if (BalancerConfig.CUSTOM_POWER_SKILLS.containsKey(skillId)) {
                    skillPower = BalancerConfig.CUSTOM_POWER_SKILLS.get(skillId);

                    int baseLevel = getBaseLevel();
                    int currentSkillLevel = getLevel();

                    Skill skillDatabase = SkillTable.INSTANCE.getInfo(skillId, getBaseLevel());
                    Skill skillDatabaseDecreased = SkillTable.INSTANCE.getInfo(skillId, getBaseLevel() - 1);
                    double skillDatabaseBasePower = skillDatabase.getPower();

                    double differentPower = skillDatabaseBasePower - (skillDatabaseDecreased == null ? skillDatabaseBasePower : skillDatabaseDecreased.getPower());
                    int differentCurrentLevelWithBase = baseLevel - currentSkillLevel;

                    if (differentPower < 0) {
                        System.out.println("Problem on xml Skill Power normally increase " + getId() + " LoL.");
                        differentPower = 1;
                    }

                    if (debug)
                        System.out.println("[Inside Config] Power " + skillPower + " different Power " + differentPower + " different Level " + differentCurrentLevelWithBase);


                    if (differentCurrentLevelWithBase > 0) {
                        skillPower -= differentPower * differentCurrentLevelWithBase;

                        if (debug)
                            System.out.println("[Cause DifferentCurrentLevel] Power " + skillPower);
                    }

                    int currentEnchantLevel = getCurrentEnchantLevel();
                    if (currentEnchantLevel > 0) {
                        Skill enchantSkillDatabase = SkillTable.INSTANCE.getInfo(skillId, currentSkillLevel);
                        double enchantSkillDatabasePower = enchantSkillDatabase.getPower();
                        if (enchantSkillDatabasePower > skillDatabaseBasePower) {
                            if (debug)
                                System.out.println("[Current Enchant] Power " + enchantSkillDatabasePower + " level " + currentSkillLevel);

                            if (currentEnchantLevel > 1) {
                                Skill enchantSkillDatabaseDecreased = SkillTable.INSTANCE.getInfo(skillId, currentSkillLevel - 1);

                                double differentEnchantPower = enchantSkillDatabasePower - enchantSkillDatabaseDecreased.getPower();

                                skillPower += currentEnchantLevel * differentEnchantPower;

                                if (debug)
                                    System.out.println("[Cause EnchantLevel Up to 1] Power " + skillPower + " different Power " + differentEnchantPower);
                            } else {
                                skillPower += enchantSkillDatabasePower;
                                if (debug)
                                    System.out.println("[Cause EnchantLevel] Power " + skillPower);
                            }
                        }
                    }
                }

                if (debug) {
                    System.out.println("ID: " + skillId + " Base Power " + skillPower);
                    System.out.println("==== Custom CalculatorPower Finish ====");
                }

                return skillPower;

                //return getPowerPvP();
            }
            if (target.isMonster()) {
                return getPowerPvE();
            }
        }
        return getPower();
    }

    public final double getPower() {
        return power;
    }

    public final void setPower(double power) {
        this.power = power;
    }

    private double getPowerPvP() {
        return powerPvP != 0 ? powerPvP : power;
    }

    private double getPowerPvE() {
        return powerPvE != 0 ? powerPvE : power;
    }

    public final long getReuseDelay(Creature actor) {
        if (actor.isPlayable() && actor.getPlayer().isInOlympiadMode())
            if (olympiadValues.containsKey("reuseDelay"))
                return Long.parseLong(olympiadValues.get("reuseDelay"));
        return _reuseDelay;
    }

    public final void setReuseDelay(long newReuseDelay) {
        _reuseDelay = newReuseDelay;
    }

    public final boolean getShieldIgnore() {
        return isShieldignore;
    }

    public final boolean isReflectable() {
        return _isReflectable;
    }

    public final int getSkillInterruptTime() {
        return skillInterruptTime;
    }

    public final int getSkillRadius() {
        return skillRadius;
    }

    public final SkillType getSkillType() {
        return _skillType;
    }

    public int getSoulsConsume() {
        return soulsConsume;
    }

    public int getSymbolId() {
        return _symbolId;
    }

    public final SkillTargetType getTargetType() {
        return targetType;
    }

    public final SkillTrait getTraitType() {
        return _traitType;
    }

    public final BaseStats getSaveVs() {
        return _saveVs;
    }

    public final int getWeaponsAllowed() {
        return _weaponsAllowed;
    }

    public double getLethal1() {
        return _lethal1;
    }

    public double getLethal2() {
        return _lethal2;
    }

    private boolean isBlockedByChar(Creature effected, EffectTemplate et) {
        if (et.getAttachedFuncs() == null) {
            return false;
        }
        for (FuncTemplate func : et.getAttachedFuncs()) {
            if ((func != null) && effected.checkBlockedStat(func.stat)) {
                return true;
            }
        }
        return false;
    }

    public final boolean isCancelable() {
        return isCancelable && (getSkillType() != SkillType.TRANSFORMATION) && !isToggle();
    }

    public final boolean isCommon() {
        return common;
    }

    public final int getCriticalRate() {
        return criticalRate;
    }

    public final boolean isHandler() {
        return _isItemHandler;
    }

    public final boolean isMagic() {
        return _magicType == SkillMagicType.MAGIC;
    }

    public final SkillMagicType getMagicType() {
        return _magicType;
    }

    public void setMagicType(SkillMagicType type) {
        _magicType = type;
    }

    public final boolean isNewbie() {
        return _isNewbie;
    }

    public final boolean isPreservedOnDeath() {
        return _isPreservedOnDeath;
    }

    public final boolean isHeroic() {
        return _isHeroic;
    }

    public final boolean isSelfDispellable() {
        return _isSelfDispellable;
    }

    public final boolean isIgnoreSkillMastery() {
        return _ignoreSkillMastery;
    }

    public void setOperateType(SkillOpType type) {
        _operateType = type;
    }

    public final boolean isOverhit() {
        return _isOverhit;
    }

    public void setOverhit(final boolean isOverhit) {
        _isOverhit = isOverhit;
    }

    public final boolean isActive() {
        return _operateType == SkillOpType.OP_ACTIVE;
    }

    public final boolean isPassive() {
        return _operateType == SkillOpType.OP_PASSIVE;
    }

    public boolean isSaveable() {
        if (!Config.ALT_SAVE_UNSAVEABLE && _name.startsWith("Herb of")) {
            return false;
        }
        return _isSaveable;
    }

    public final boolean isSkillTimePermanent() {
        return _isSkillTimePermanent || _isItemHandler || _name.contains("Talisman");
    }

    public final boolean isReuseDelayPermanent() {
        return _isReuseDelayPermanent || _isItemHandler;
    }

    public boolean isDeathlink() {
        return deathlink;
    }

    public boolean isBasedOnTargetDebuff() {
        return _basedOnTargetDebuff;
    }

    public boolean isSoulBoost() {
        return _isSoulBoost;
    }

    public boolean isChargeBoost() {
        return _isChargeBoost;
    }

    public boolean isUsingWhileCasting() {
        return _isUsingWhileCasting;
    }

    public boolean isBehind() {
        return isBehind;
    }

    public boolean isHideStartMessage() {
        return _hideStartMessage;
    }

    public boolean isHideUseMessage() {
        return _hideUseMessage;
    }

    public boolean isSSPossible() {
        return (_isUseSS == Ternary.TRUE) || ((_isUseSS == Ternary.DEFAULT) && !_isItemHandler && !isMusic() && isActive() && !((getTargetType() == SkillTargetType.TARGET_SELF) && !isMagic()));
    }

    protected final boolean isSuicideAttack() {
        return _isSuicideAttack;
    }

    public final boolean isToggle() {
        return _operateType == SkillOpType.OP_TOGGLE;
    }

    public boolean isItemSkill() {
        return _name.contains("Item Skill") || _name.contains("Talisman");
    }

    @Override
    public String toString() {
        return _name + "[id=" + id + ",lvl=" + _level + "]";
    }

    public abstract void useSkill(Creature activeChar, List<Creature> targets);

    public boolean isAoE() {
        switch (targetType) {
            case TARGET_AREA:
            case TARGET_AREA_AIM_CORPSE:
            case TARGET_AURA:
            case TARGET_PET_AURA:
            case TARGET_MULTIFACE:
            case TARGET_MULTIFACE_AURA:
            case TARGET_TUNNEL:
                return true;
            default:
                return false;
        }
    }

    public boolean isNotTargetAoE() {
        switch (targetType) {
            case TARGET_AURA:
            case TARGET_MULTIFACE_AURA:
            case TARGET_ALLY:
            case TARGET_CLAN:
            case TARGET_CLAN_ONLY:
            case TARGET_PARTY:
            case TARGET_PARTY_NO_ME:
            case TARGET_PARTY_NO_SUMMON:
            case TARGET_FRIEND:
                return true;
            default:
                return false;
        }
    }

    public boolean isOffensive() {
        return offensive;
    }

    public boolean isBuff() {
        return _isBuff;
    }

    private boolean isForceUse() {
        return _isForceUse;
    }

    public boolean isAI() {
        return _skillType.isAI();
    }

    private boolean isPvM() {
        return _isPvm;
    }

    private boolean isPvpSkill() {
        return _isPvpSkill;
    }

    private boolean isFishingSkill() {
        return _isFishingSkill;
    }

    public boolean isMusic() {
        return _magicType == SkillMagicType.MUSIC;
    }

    public boolean isTrigger() {
        return _isTrigger;
    }

    protected boolean oneTarget() {
        switch (targetType) {
            case TARGET_CORPSE:
            case TARGET_CORPSE_PLAYER:
            case TARGET_HOLY:
            case TARGET_FLAGPOLE:
            case TARGET_ITEM:
            case TARGET_NONE:
            case TARGET_ONE:
            case TARGET_PARTY_ONE:
            case TARGET_PET:
            case TARGET_OWNER:
            case TARGET_ENEMY_PET:
            case TARGET_ENEMY_SUMMON:
            case TARGET_ENEMY_SERVITOR:
            case TARGET_SELF:
            case TARGET_UNLOCKABLE:
            case TARGET_CHEST:
            case TARGET_FEEDABLE_BEAST:
            case TARGET_SIEGE:
                return true;
            default:
                return false;
        }
    }

    public int getCancelTarget() {
        return _cancelTarget;
    }

    public boolean isNotUsedByAI() {
        return _isNotUsedByAI;
    }

    public boolean isIgnoreResists() {
        return _isIgnoreResists;
    }

    public boolean isIgnoreInvul() {
        return _isIgnoreInvul;
    }

    public boolean isNotAffectedByMute() {
        return _isNotAffectedByMute;
    }

    private boolean flyingTransformUsage() {
        return _flyingTransformUsage;
    }

    public boolean canUseTeleport() {
        return _canUseTeleport;
    }

    public int getCastCount() {
        return _castCount;
    }

    public int getEnchantLevelCount() {
        return _enchantLevelCount;
    }

    public void setEnchantLevelCount(int count) {
        _enchantLevelCount = count;
    }

    public boolean isClanSkill() {
        return ((id >= 370) && (id <= 391)) || ((id >= 611) && (id <= 616));
    }

    public boolean isBaseTransformation() {// Inquisitor, Vanguard, Final Form...
        return ((id >= 810) && (id <= 813)) || ((id >= 1520) && (id <= 1522)) || (id == 538);
    }

    protected boolean isSummonerTransformation() // Spirit of the Cat etc
    {
        return (id >= 929) && (id <= 931);
    }

    protected boolean isCursedTransformation() // zarich etc
    {
        return id == 3603 || id == 3629;
    }

    public double getSimpleDamage(Creature attacker, Creature target) {
        if (isMagic()) {
            double mAtk = attacker.getMAtk(target, this);
            double mdef = target.getMDef(null, this);
            double power = getPower();
            int sps = (attacker.getChargedSpiritShot() > 0) && isSSPossible() ? attacker.getChargedSpiritShot() * 2 : 1;
            return (91 * power * Math.sqrt(sps * mAtk)) / mdef;
        }
        double pAtk = attacker.getPAtk(target);
        double pdef = target.getPDef(attacker);
        double power = getPower();
        int ss = attacker.getChargedSoulShot() && isSSPossible() ? 2 : 1;
        return (ss * (pAtk + power) * 70.) / pdef;
    }

    public long getReuseForMonsters() {
        long min = 1000;
        switch (_skillType) {
            case PARALYZE:
            case DEBUFF:
            case NEGATE_EFFECTS:
            case NEGATE_STATS:
            case STEAL_BUFF:
                min = 10000;
                break;
            case MUTE:
            case ROOT:
            case SLEEP:
            case STUN:
                min = 5000;
                break;
        }
        return Math.max(Math.max(hitTime + _coolTime, _reuseDelay), min);
    }

    public double getAbsorbPart() {
        return _absorbPart;
    }

    private boolean isProvoke() {
        return _isProvoke;
    }

    public String getIcon() {
        return _icon;
    }

    public int getEnergyConsume() {
        return energyConsume;
    }

    private boolean isCubicSkill() {
        return _isCubicSkill;
    }

    public void setCubicSkill(boolean value) {
        _isCubicSkill = value;
    }

    private int getCurrentEnchantLevel() {
        int baseMaxLvl = SkillTable.INSTANCE.getBaseLevel(getId());
        int enchantLvl = (getLevel() - baseMaxLvl);

        if (enchantLvl == 0)
            return 0;

        enchantLvl = enchantLvl % getEnchantLevelCount();
        enchantLvl = (enchantLvl == 0 ? getEnchantLevelCount() : enchantLvl);

        return enchantLvl < 0 ? 0 : enchantLvl;
    }

    @Override
    public int compareTo(Skill o) {
        return o.id - this.id;
    }

    public enum NextAction {
        ATTACK,
        CAST,
        DEFAULT,
        MOVE,
        NONE
    }

    public enum SkillOpType {
        OP_ACTIVE,
        OP_PASSIVE,
        OP_TOGGLE
    }

    public enum Ternary {
        TRUE,
        FALSE,
        DEFAULT
    }

    public enum SkillMagicType {
        PHYSIC,
        MAGIC,
        SPECIAL,
        MUSIC
    }

    public enum SkillTargetType {
        TARGET_ALLY,
        TARGET_FRIEND,
        TARGET_AREA,
        TARGET_AREA_AIM_CORPSE,
        TARGET_AURA,
        TARGET_PET_AURA,
        TARGET_CHEST,
        TARGET_FEEDABLE_BEAST,
        TARGET_CLAN,
        TARGET_CLAN_ONLY,
        TARGET_CORPSE,
        TARGET_CORPSE_PLAYER,
        TARGET_ENEMY_PET,
        TARGET_ENEMY_SUMMON,
        TARGET_ENEMY_SERVITOR,
        TARGET_EVENT,
        TARGET_FLAGPOLE,
        TARGET_COMMCHANNEL,
        TARGET_HOLY,
        TARGET_ITEM,
        TARGET_MULTIFACE,
        TARGET_MULTIFACE_AURA,
        TARGET_TUNNEL,
        TARGET_NONE,
        TARGET_ONE,
        TARGET_OWNER,
        TARGET_PARTY,
        TARGET_PARTY_ONE,
        TARGET_PARTY_NO_SUMMON,
        TARGET_PARTY_NO_ME,
        TARGET_PET,
        TARGET_SELF,
        TARGET_SIEGE,
        TARGET_UNLOCKABLE
    }

    public enum SkillType {
        AGGRESSION(Aggression.class),
        AIEFFECTS(AIeffects.class),
        BALANCE(Balance.class),
        BEAST_FEED(BeastFeed.class),
        BLEED(Continuous.class),
        BUFF(Continuous.class),
        BUFF_CHARGER(BuffCharger.class),
        CALL(Call.class),
        CHAIN_HEAL(ChainHeal.class),
        CHARGE(Charge.class),
        CHARGE_SOUL(ChargeSoul.class),
        CLAN_GATE(ClanGate.class),
        COMBATPOINTHEAL(CombatPointHeal.class),
        CONT(Toggle.class),
        CPDAM(CPDam.class),
        CPHOT(Continuous.class),
        CRAFT(Craft.class),
        DEATH_PENALTY(DeathPenalty.class),
        DECOY(Decoy.class),
        DEBUFF(Continuous.class),
        DELETE_HATE(DeleteHate.class),
        DELETE_HATE_OF_ME(DeleteHateOfMe.class),
        DESTROY_SUMMON(DestroySummon.class),
        DEFUSE_TRAP(DefuseTrap.class),
        DETECT_TRAP(DetectTrap.class),
        DISCORD(Continuous.class),
        DOT(Continuous.class),
        DRAIN(Drain.class),
        DRAIN_SOUL(DrainSoul.class),
        EFFECT(l2trunk.gameserver.skills.skillclasses.Effect.class),
        EFFECTS_FROM_SKILLS(EffectsFromSkills.class),
        ENERGY_REPLENISH(EnergyReplenish.class),
        ENCHANT_ARMOR,
        ENCHANT_WEAPON,
        EXTRACT_STONE(ExtractStone.class),
        FEED_PET,
        FISHING(FishingSkill.class),
        HARDCODED(l2trunk.gameserver.skills.skillclasses.Effect.class),
        HARVESTING(Harvesting.class),
        HEAL(Heal.class),
        HEAL_PERCENT(HealPercent.class),
        SUMMON_HEAL_PERCENT(SummonHealPercent.class),
        HOT(Continuous.class),
        INSTANT_JUMP(InstantJump.class),
        KAMAEL_WEAPON_EXCHANGE(KamaelWeaponExchange.class),
        LEARN_SKILL(LearnSkill.class),
        LETHAL_SHOT(LethalShot.class),
        LUCK,
        MANADAM(ManaDam.class),
        MANAHEAL(ManaHeal.class),
        MANAHEAL_PERCENT(ManaHealPercent.class),
        SUMMON_MANAHEAL_PERCENT(SummonManaHealPercent.class),
        MDAM(MDam.class),
        MDOT(Continuous.class),
        MPHOT(Continuous.class),
        MUTE(Disablers.class),
        NEGATE_EFFECTS(NegateEffects.class),
        NEGATE_STATS(NegateStats.class),
        ADD_PC_BANG(PcBangPointsAdd.class),
        NOTDONE,
        NOTUSED,
        PARALYZE(Disablers.class),
        PASSIVE,
        PDAM(PDam.class),
        PET_SUMMON(PetSummon.class),
        POISON(Continuous.class),
        PUMPING(ReelingPumping.class),
        RECALL(Recall.class),
        REELING(ReelingPumping.class),
        REFILL(Refill.class),
        RESURRECT(Resurrect.class),
        RIDE(Ride.class),
        ROOT(Disablers.class),
        SELF_SACRIFICE(SelfSacrifice.class),
        SHIFT_AGGRESSION(ShiftAggression.class),
        SLEEP(Disablers.class),
        SOULSHOT,
        SOWING(Sowing.class),
        SPHEAL(SPHeal.class),
        SPIRITSHOT,
        SPOIL(Spoil.class),
        STEAL_BUFF(StealBuff.class),
        SPAWN(Spawn.class),
        CURSE_DIVINITY(CurseDivinity.class),
        STUN(Disablers.class),
        SUMMON(l2trunk.gameserver.skills.skillclasses.Summon.class),
        SUMMON_FLAG(SummonSiegeFlag.class),
        SUMMON_ITEM(SummonItem.class),
        SWEEP(Sweep.class),
        TAKECASTLE(TakeCastle.class),
        TAKEFORTRESS(TakeFortress.class),
        TAMECONTROL(TameControl.class),
        TAKEFLAG(TakeFlag.class),
        TELEPORT_NPC(TeleportNpc.class),
        TRANSFORMATION(Transformation.class),
        UNLOCK(Unlock.class),
        WATCHER_GAZE(Continuous.class),
        VITALITY_HEAL(VitalityHeal.class),
        IMPRISON(VitalityHeal.class);

        private final Class<? extends Skill> clazz;

        SkillType() {
            clazz = Default.class;
        }

        SkillType(Class<? extends Skill> clazz) {
            this.clazz = clazz;
        }

        public Skill makeSkill(StatsSet set) {
            try {
                Constructor<? extends Skill> c = clazz.getConstructor(StatsSet.class);
                return c.newInstance(set);
            } catch (IllegalAccessException | SecurityException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalArgumentException e) {
                _log.error("Error while making Skill", e);
                throw new RuntimeException(e);
            }
        }

        final boolean isPvM() {
            switch (this) {
                case DISCORD:
                    return true;
                default:
                    return false;
            }
        }

        boolean isAI() {
            switch (this) {
                case AGGRESSION:
                case AIEFFECTS:
                case SOWING:
                case DELETE_HATE:
                case DELETE_HATE_OF_ME:
                    return true;
                default:
                    return false;
            }
        }

        final boolean isPvpSkill() {
            switch (this) {
                case BLEED:
                case AGGRESSION:
                case DEBUFF:
                case DOT:
                case MDOT:
                case MUTE:
                case PARALYZE:
                case POISON:
                case ROOT:
                case SLEEP:
                case MANADAM:
                case DESTROY_SUMMON:
                case NEGATE_STATS:
                case NEGATE_EFFECTS:
                case STEAL_BUFF:
                case CURSE_DIVINITY:
                case DELETE_HATE:
                case DELETE_HATE_OF_ME:
                    return true;
                default:
                    return false;
            }
        }

        boolean isBuff() {
            switch (this) {
                case BUFF:
                    return true;
                default:
                    return false;
            }
        }

        boolean isOffensive() {
            switch (this) {
                case AGGRESSION:
                case AIEFFECTS:
                case BLEED:
                case DEBUFF:
                case DOT:
                case DRAIN:
                case DRAIN_SOUL:
                case LETHAL_SHOT:
                case MANADAM:
                case MDAM:
                case MDOT:
                case MUTE:
                case PARALYZE:
                case PDAM:
                case CPDAM:
                case POISON:
                case ROOT:
                case SLEEP:
                case SOULSHOT:
                case SPIRITSHOT:
                case SPOIL:
                case STUN:
                case SWEEP:
                case HARVESTING:
                case TELEPORT_NPC:
                case SOWING:
                case DELETE_HATE:
                case DELETE_HATE_OF_ME:
                case DESTROY_SUMMON:
                case STEAL_BUFF:
                case CURSE_DIVINITY:
                case DISCORD:
                case INSTANT_JUMP:
                    return true;
                default:
                    return false;
            }
        }
    }

    public static class AddedSkill {
        public final int id;
        public final int level;
        private Skill skill;

        protected AddedSkill(int id, int level) {
            this.id = id;
            this.level = level;
        }

        public int getSkillId() {
            return id;
        }

        public int getSkillLevel() {
            return level;
        }

        public Skill getSkill() {
            if (skill == null) {
                skill = SkillTable.INSTANCE.getInfo(id, level);
            }
            return skill;
        }
    }
}