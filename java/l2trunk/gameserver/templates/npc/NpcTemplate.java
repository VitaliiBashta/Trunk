package l2trunk.gameserver.templates.npc;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.ai.CharacterAI;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.TeleportLocation;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.instances.AllNpcInstances;
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
import java.util.*;

public final class NpcTemplate extends CharTemplate {
//    private  final Constructor<NpcInstance> DEFAULT_TYPE_CONSTRUCTOR = (Constructor<NpcInstance>) NpcInstance.class.getConstructors()[0];

//    private  final Constructor<NpcInstance> DEFAULT_TYPE_CONSTRUCTOR;

    private static final Logger LOG = LoggerFactory.getLogger(NpcTemplate.class);
    public final String type;
    public  int npcId;
    public  String name;
    public  String title;
    // не используется - public final String sex;
    public  int level;
    public  long rewardExp;
    public  int rewardSp;
    public  int rewardRp;
    public  int aggroRange;
    public  int rhand;
    public  int lhand;
    public  double rateHp;
    public  int displayId;
    public Class<? extends NpcInstance> classType;
    private  StatsSet AIParams;
    private  int castleId;
    private final Map<Integer, TeleportLocation[]> teleportList = new HashMap<>();
    private final Map<QuestEventType, List<Quest>> questEvents = new HashMap<>();
    private final Map<Integer, Skill> skills = new HashMap<>();
    private  String _htmRoot;
    public boolean isRaid;
    private Faction faction = Faction.NONE;
    private int race = 0;
    private Map<RewardType, RewardList> _rewards = Collections.emptyMap();
    private List<MinionData> _minions = Collections.emptyList();
    private List<AbsorbInfo> absorbInfo = Collections.emptyList();
    private List<ClassId> teachInfo = new ArrayList<>();
    private List<Skill> _damageSkills = new ArrayList<>();
    private List<Skill> _dotSkills = new ArrayList<>();
    private List<Skill> _debuffSkills = new ArrayList<>();
    private List<Skill> _buffSkills = new ArrayList<>();
    private List<Skill> _stunSkills = new ArrayList<>();
    private List<Skill> healSkills = new ArrayList<>();
//    private Constructor<? extends NpcInstance> constructor;


    private Constructor<? extends CharacterAI> classAI;

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
        _htmRoot = set.getString("htm_root", null);
        castleId = set.getInteger("castle_id", 0);
        AIParams = (StatsSet) set.getObject("aiParams", StatsSet.EMPTY);

        String type = set.getString("type", null);
        this.type = type;
        if (type.equals("Pet"))
            System.out.println("Pet found ");
        String ai = set.getString("ai_type", null);
//        try {
//            classType = Scripts.INSTANCE.getNpcInstance(type + "Instance");
//            if (!type.equalsIgnoreCase("Pet")
//                    && !type.equalsIgnoreCase("Trap"))
//                constructor = classType.getConstructor(int.class, NpcTemplate.class);
//        } catch (NoSuchMethodException e) {
//            throw new IllegalArgumentException("no NpcTemplate for type: " + type);
//        }

//        isRaid = isInstanceOf(RaidBossInstance.class) && !isInstanceOf(ReflectionBossInstance.class);
        Class<? extends CharacterAI> cls = Scripts.INSTANCE.getAI("ai." + ai);
        try {
            classAI = cls.getConstructor(Creature.class);
        } catch (NoSuchMethodException e) {
            try {
                classAI = cls.getConstructor(NpcInstance.class);
            } catch (NoSuchMethodException e1) {
                throw new IllegalArgumentException("no AI for type: " + ai);
            }

        }
    }

    public boolean isInstanceOf(Class<? extends Creature> clazz) {
        if (classType == null)
            return false;
        return clazz.isAssignableFrom(classType);
    }

    public NpcInstance getNewInstance(int id) {
        return AllNpcInstances.getInstance(id, type);

//        if (constructor == null)
//            return null;
//        try {
//            return constructor.newInstance(id, this);
//        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
//            LOG.error("Unable to create instance of NPC " + npcId, e);
//            throw new RuntimeException("Can't create instance " + e);
//        }
    }

    public NpcInstance getNewInstance() {
        return getNewInstance(IdFactory.getInstance().getNextId());
    }

    public CharacterAI getNewAI(NpcInstance npc) {
        try {
            return classAI.newInstance(npc);

        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            LOG.error("Unable to create ai of NPC " + npcId, e);
            throw new IllegalArgumentException("no Ai for npc:" + npc.getName());
        }
    }


    public void addTeachInfo(ClassId classId) {
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
        List<Quest> quests = questEvents.get(EventType);
        return quests == null ? List.of() : quests;
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