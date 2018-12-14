package l2trunk.gameserver.templates.npc;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.ai.CharacterAI;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.TeleportLocation;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.RaidBossInstance;
import l2trunk.gameserver.model.instances.ReflectionBossInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestEventType;
import l2trunk.gameserver.model.reward.RewardList;
import l2trunk.gameserver.model.reward.RewardType;
import l2trunk.gameserver.scripts.Scripts;
import l2trunk.gameserver.skills.effects.EffectTemplate;
import l2trunk.gameserver.templates.CharTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

public final class NpcTemplate extends CharTemplate {
//    private  final Constructor<NpcInstance> DEFAULT_TYPE_CONSTRUCTOR = (Constructor<NpcInstance>) NpcInstance.class.getConstructors()[0];

//    private  final Constructor<NpcInstance> DEFAULT_TYPE_CONSTRUCTOR;

    private static final Logger LOG = LoggerFactory.getLogger(NpcTemplate.class);
    public final int npcId;
    public final String name;
    public final String title;
    // не используется - public final String sex;
    public final int level;
    public final long rewardExp;
    public final int rewardSp;
    public final int rewardRp;
    public final int aggroRange;
    public final int rhand;
    public final int lhand;
    public final double rateHp;
    public final int displayId;
    private final Constructor<CharacterAI> DEFAULT_AI_CONSTRUCTOR = (Constructor<CharacterAI>) CharacterAI.class.getConstructors()[0];
    private final String jClass;
    private final ShotsType shots;
    private final StatsSet AIParams;
    private final int castleId;
    private final Map<Integer, TeleportLocation[]> teleportList = new HashMap<>();
    private final Map<QuestEventType, List<Quest>> questEvents = new HashMap<>();
    private final Map<Integer, Skill> skills = new HashMap<>();
    private final String _htmRoot;
    public boolean isRaid = false;
    private Faction faction = Faction.NONE;
    /**
     * fixed skills
     */
    private int race = 0;
    private Map<RewardType, RewardList> _rewards = Collections.emptyMap();
    private List<MinionData> _minions = Collections.emptyList();
    private List<AbsorbInfo> absorbInfo = Collections.emptyList();
    private List<ClassId> teachInfo = Collections.emptyList();
    private List<Skill> _damageSkills = new ArrayList<>();
    private List<Skill> _dotSkills = new ArrayList<>();
    private List<Skill> _debuffSkills = new ArrayList<>();
    private List<Skill> _buffSkills = new ArrayList<>();
    private List<Skill> _stunSkills = new ArrayList<>();
    private List<Skill> healSkills = new ArrayList<>();
    private Class<? extends Creature> classType = NpcInstance.class;
    private Constructor<? extends Creature> _constructorType;
    private Class<? extends CharacterAI> _classAI = CharacterAI.class;
    private Constructor<CharacterAI> _constructorAI = DEFAULT_AI_CONSTRUCTOR;

    /**
     * Constructor<?> of L2Character.<BR><BR>
     *
     * @param set The StatsSet object to transfer data to the method
     */
    public NpcTemplate(StatsSet set) {
        super(set);
        npcId = set.getInteger("npcId");
        displayId = set.getInteger("displayId");

        name = set.getString("name");
        title = set.getString("title");
        // sex = set.getString("sex");
        level = set.getInteger("level");
        rewardExp = set.getLong("rewardExp");
        rewardSp = set.getInteger("rewardSp");
        rewardRp = set.getInteger("rewardRp");
        aggroRange = set.getInteger("aggroRange");
        rhand = set.getInteger("rhand", 0);
        lhand = set.getInteger("lhand", 0);
        rateHp = set.getDouble("baseHpRate");
        jClass = set.getString("texture", null);
        _htmRoot = set.getString("htm_root", null);
        shots = set.getEnum("shots", ShotsType.class, ShotsType.NONE);
        castleId = set.getInteger("castle_id", 0);
        AIParams = (StatsSet) set.getObject("aiParams", StatsSet.EMPTY);

        String type = set.getString("type", null);
        String ai = set.getString("ai_type", null);
        setType(type);
        setAI(ai);
//        try {
        _constructorType = (Constructor<? extends Creature>) NpcInstance.class.getConstructors()[0];
//        (int.class, NpcTemplate.class);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
    }

    public Class<? extends Creature> getInstanceClass() {
        return classType;
    }

    public Constructor<? extends Creature> getInstanceConstructor() {
        return _constructorType;
    }

    public boolean isInstanceOf(Class<?> clazz) {
        return clazz.isAssignableFrom(classType);
    }

    /**
     * Создает новый инстанс NPC. Для него следует вызывать (именно в этом порядке):
     * <br> setSpawnedLoc (обязательно)
     * <br> setReflection (если reflection не базовый)
     * <br> setChampion (опционально)
     * <br> setCurrentHpMp (если вызывался setChampion)
     * <br> spawnMe (в качестве параметра брать getSpawnedLoc)
     *
     * @return
     */
    public NpcInstance getNewInstance() {
        try {
            int nextId = IdFactory.getInstance().getNextId();
            return (NpcInstance) _constructorType.newInstance(nextId, this);
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
            LOG.error("Unable to create instance of NPC " + npcId, e);
            throw new RuntimeException("Can't create instance " + e);
        }
    }

    public CharacterAI getNewAI(NpcInstance npc) {
        try {
            return _constructorAI.newInstance(npc);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | IllegalArgumentException e) {
            LOG.error("Unable to create ai of NPC " + npcId, e);
        }

        return new CharacterAI(npc);
    }

    public void setType(String type) {
        classType = Scripts.INSTANCE.getNpcInstanceAI(type + "Instance");

        if (classType == null)
            LOG.error("Not found type class for type: " + type + ". NpcId: " + npcId);
        if (Modifier.isAbstract(classType.getModifiers())) return;
        try {
            _constructorType = (Constructor<? extends Creature>) classType.getConstructors()[0];
//                    (int.class, NpcTemplate.class);
//            if (constructors.length == 0) return;
//            _constructorType = (Constructor<NpcInstance>) constructors[0];
        } catch (IndexOutOfBoundsException e) {
            LOG.warn("found class without default constructor: '" + type + " For npc id " + npcId);
//        } catch (NoSuchMethodException e) {
//            LOG.error("no constructor (int,NpcTemplate) for class " + classType);
            e.printStackTrace();
        }
//        }

        if (classType.isAnnotationPresent(Deprecated.class))
            LOG.error("Npc type: " + type + ", is deprecated. NpcId: " + npcId);

        //TODO [G1ta0] сделать поле в соотвествующих классах
        isRaid = isInstanceOf(RaidBossInstance.class) && !isInstanceOf(ReflectionBossInstance.class);
    }

    private void setAI(String ai) {
        Class<? extends CharacterAI> classAI;
        classAI = Scripts.INSTANCE.getAI("ai." + ai);
        if (classAI == null) {
            LOG.error("Not found ai class for ai: " + ai + ". NpcId: " + npcId);
            System.exit(1);
        }
//            } else {
        _classAI = classAI;
        if (Modifier.isAbstract(_classAI.getModifiers())) return;
        try {
            Constructor<?>[] constructors = _classAI.getConstructors();
            if (constructors.length == 0) return;
            _constructorAI = (Constructor<CharacterAI>) constructors[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            LOG.error("no default constructors for: " + ai + ". NpcID:" + npcId);
//            }

            if (_classAI.isAnnotationPresent(Deprecated.class))
                LOG.error("Ai type: " + ai + ", is deprecated. NpcId: " + npcId);
        }
    }

    public void addTeachInfo(ClassId classId) {
        if (teachInfo.isEmpty())
            teachInfo = new ArrayList<>(1);
        teachInfo.add(classId);
    }

    public List<ClassId> getTeachInfo() {
        return teachInfo;
    }

    public boolean canTeach(ClassId classId) {
        return teachInfo.contains(classId);
    }

    public void addTeleportList(int id, TeleportLocation[] list) {
        teleportList.put(id, list);
    }

    public TeleportLocation[] getTeleportList(int id) {
        return teleportList.get(id);
    }

    public Map<Integer, TeleportLocation[]> getTeleportList() {
        return teleportList;
    }

    public void putRewardList(RewardType rewardType, RewardList list) {
        if (_rewards.isEmpty())
            _rewards = new HashMap<>(RewardType.values().length);
        _rewards.put(rewardType, list);
    }

    public RewardList getRewardList(RewardType t) {
        return _rewards.get(t);
    }

    public Map<RewardType, RewardList> getRewards() {
        return _rewards;
    }

    public void addAbsorbInfo(AbsorbInfo absorbInfo) {
        if (this.absorbInfo.isEmpty())
            this.absorbInfo = new ArrayList<>(1);

        this.absorbInfo.add(absorbInfo);
    }

    public void addMinion(MinionData minion) {
        if (_minions.isEmpty())
            _minions = new ArrayList<>(1);

        _minions.add(minion);
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    @SuppressWarnings("incomplete-switch")
    public void addSkill(Skill skill) {
        skills.put(skill.getId(), skill);

        //TODO [G1ta0] перенести в AI
        if (skill.isNotUsedByAI() || skill.getTargetType() == Skill.SkillTargetType.TARGET_NONE || skill.getSkillType() == Skill.SkillType.NOTDONE || !skill.isActive())
            return;

        switch (skill.getSkillType()) {
            case PDAM:
            case MANADAM:
            case MDAM:
            case DRAIN:
            case DRAIN_SOUL: {
                boolean added = false;

                if (skill.hasEffects())
                    for (EffectTemplate eff : skill.getEffectTemplates())
                        switch (eff.getEffectType()) {
                            case Stun:
                                _stunSkills.add(skill);
                                added = true;
                                break;
                            case DamOverTime:
                            case DamOverTimeLethal:
                            case ManaDamOverTime:
                            case LDManaDamOverTime:
                                _dotSkills.add(skill);
                                added = true;
                                break;
                        }

                if (!added)
                    _damageSkills.add(skill);

                break;
            }
            case DOT:
            case MDOT:
            case POISON:
            case BLEED:
                _dotSkills.add(skill);
                break;
            case DEBUFF:
            case SLEEP:
            case ROOT:
            case PARALYZE:
            case MUTE:
            case TELEPORT_NPC:
            case AGGRESSION:
                _debuffSkills.add(skill);
                break;
            case BUFF:
                _buffSkills.add(skill);
                break;
            case STUN:
                _stunSkills.add(skill);
                break;
            case HEAL:
            case HEAL_PERCENT:
            case HOT:
                healSkills.add(skill);
                break;
            default:
                break;
        }
    }

    public List<Skill> getDamageSkills() {
        return _damageSkills;
    }

    public List<Skill> getDotSkills() {
        return _dotSkills;
    }

    public List<Skill> getDebuffSkills() {
        return _debuffSkills;
    }

    public List<Skill> getBuffSkills() {
        return _buffSkills;
    }

    public List<Skill> getStunSkills() {
        return _stunSkills;
    }

    public List<Skill> getHealSkills() {
        return healSkills;
    }

    public List<MinionData> getMinionData() {
        return _minions;
    }

    public Map<Integer, Skill> getSkills() {
        return skills;
    }

    public void addQuestEvent(QuestEventType EventType, Quest q) {
        if (questEvents.get(EventType) == null) {
            List<Quest> newList = new ArrayList<>();
            newList.add(q);
            questEvents.put(EventType, newList);
        } else {
            List<Quest> _quests = questEvents.get(EventType);
            if (_quests.contains(q)) return;
            _quests.add(q);
        }
    }

    public List<Quest> getEventQuests(QuestEventType EventType) {
        return questEvents.get(EventType);
    }

    public int getRace() {
        return race;
    }

    public void setRace(int newrace) {
        race = newrace;
    }

    public boolean isUndead() {
        return race == 1;
    }

    @Override
    public String toString() {
        return "Npc template " + name + "[" + npcId + "]";
    }

    @Override
    public int getNpcId() {
        return npcId;
    }

    public String getName() {
        return name;
    }

    public final String getJClass() {
        return jClass;
    }

    public final StatsSet getAIParams() {
        return AIParams;
    }

    public List<AbsorbInfo> getAbsorbInfo() {
        return absorbInfo;
    }

    public int getCastleId() {
        return castleId;
    }

    public Map<QuestEventType, List<Quest>> getQuestEvents() {
        return questEvents;
    }

    public String getHtmRoot() {
        return _htmRoot;
    }

    public enum ShotsType {
        NONE,
        SOUL,
        SPIRIT,
        BSPIRIT,
        SOUL_SPIRIT,
        SOUL_BSPIRIT
    }
}