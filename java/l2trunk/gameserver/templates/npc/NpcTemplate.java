package l2trunk.gameserver.templates.npc;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.ai.AIs;
import l2trunk.gameserver.ai.CharacterAI;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.TeleportLocation;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.instances.AllNpcInstances;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestEventType;
import l2trunk.gameserver.model.reward.RewardList;
import l2trunk.gameserver.model.reward.RewardType;
import l2trunk.gameserver.skills.effects.EffectTemplate;
import l2trunk.gameserver.templates.CharTemplate;

import java.util.*;

public final class NpcTemplate extends CharTemplate {
    public final String type;
    public final int npcId;
    public final String name;
    public final int displayId;
    public final boolean isRaid = false;
    private final Map<Integer, List<TeleportLocation>> teleportList = new HashMap<>();
    private final Map<QuestEventType, Set<Quest>> questEvents = new HashMap<>();
    private final Map<Integer, Skill> skills = new HashMap<>();
    public String title;
    // не используется - public final String sex;
    public int level;
    public long rewardExp;
    public int rewardSp;
    public int rewardRp;
    public int aggroRange;
    public int rhand;
    public int lhand;
    public double rateHp;
    private StatsSet aiParams;
    private int castleId;
    private String htmRoot;
    private Faction faction = Faction.NONE;
    private int race = 0;
    private Map<RewardType, RewardList> _rewards = Collections.emptyMap();
    private List<MinionData> minions = new ArrayList<>();
    private List<AbsorbInfo> absorbInfo = new ArrayList<>();
    private List<ClassId> teachInfo = new ArrayList<>();
    private List<Skill> damageSkills = new ArrayList<>();
    private List<Skill> dotSkills = new ArrayList<>();
    private List<Skill> debuffSkills = new ArrayList<>();
    private List<Skill> buffSkills = new ArrayList<>();
    private List<Skill> stunSkills = new ArrayList<>();
    private List<Skill> healSkills = new ArrayList<>();
//    private Constructor<? extends NpcInstance> constructor;


    private String aiName;

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
        rhand = set.getInteger("rhand");
        lhand = set.getInteger("lhand");
        rateHp = set.getDouble("baseHpRate");
        htmRoot = set.getString("htm_root");
        castleId = set.getInteger("castle_id");
        aiParams = set.getStats("aiParams");
        type = set.getString("type");
        aiName = set.getString("ai_type");
    }

    public NpcInstance getNewInstance(int id) {
        return AllNpcInstances.getInstance(id, type, name);
    }

    public NpcInstance getNewInstance() {
        return getNewInstance(IdFactory.getInstance().getNextId());
    }

    public CharacterAI getNewAI(NpcInstance npc) {
        return AIs.getNewAI(aiName, npc);
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

    public void addTeleportList(int id, List<TeleportLocation> list) {
        teleportList.put(id, list);
    }

    public List<TeleportLocation> getTeleportList(int id) {
        return teleportList.get(id);
    }

    public Map<Integer, List<TeleportLocation>> getTeleportList() {
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
        this.absorbInfo.add(absorbInfo);
    }

    public void addMinion(MinionData minion) {
        minions.add(minion);
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    @SuppressWarnings("incomplete-switch")
    public void addSkill(Skill skill) {
        skills.put(skill.id, skill);

        //TODO [G1ta0] перенести в AI
        if (skill.isNotUsedByAI || skill.targetType == Skill.SkillTargetType.TARGET_NONE || skill.skillType == Skill.SkillType.NOTDONE || !skill.isActive())
            return;

        switch (skill.skillType) {
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
                                stunSkills.add(skill);
                                added = true;
                                break;
                            case DamOverTime:
                            case DamOverTimeLethal:
                            case ManaDamOverTime:
                            case LDManaDamOverTime:
                                dotSkills.add(skill);
                                added = true;
                                break;
                        }

                if (!added)
                    damageSkills.add(skill);

                break;
            }
            case DOT:
            case MDOT:
            case POISON:
            case BLEED:
                dotSkills.add(skill);
                break;
            case DEBUFF:
            case SLEEP:
            case ROOT:
            case PARALYZE:
            case MUTE:
            case TELEPORT_NPC:
            case AGGRESSION:
                debuffSkills.add(skill);
                break;
            case BUFF:
                buffSkills.add(skill);
                break;
            case STUN:
                stunSkills.add(skill);
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
        return damageSkills;
    }

    public List<Skill> getDotSkills() {
        return dotSkills;
    }

    public List<Skill> getDebuffSkills() {
        return debuffSkills;
    }

    public List<Skill> getBuffSkills() {
        return buffSkills;
    }

    public List<Skill> getStunSkills() {
        return stunSkills;
    }

    public List<Skill> getHealSkills() {
        return healSkills;
    }

    public List<MinionData> getMinionData() {
        return minions;
    }

    public Map<Integer, Skill> getSkills() {
        return skills;
    }

    public void addQuestEvent(QuestEventType EventType, Quest q) {
        if (questEvents.get(EventType) == null) {
            Set<Quest> newList = new HashSet<>();
            newList.add(q);
            questEvents.put(EventType, newList);
        } else
            questEvents.get(EventType).add(q);
    }

    public Set<Quest> getEventQuests(QuestEventType eventType) {
        Set<Quest> quests = questEvents.get(eventType);
        return quests == null ? Set.of() : quests;
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

    public String name() {
        return name;
    }

    public final StatsSet getAiParams() {
        return aiParams;
    }

    public List<AbsorbInfo> getAbsorbInfo() {
        return absorbInfo;
    }

    public int getCastleId() {
        return castleId;
    }

    public Map<QuestEventType, Set<Quest>> getQuestEvents() {
        return questEvents;
    }

    public String getHtmRoot() {
        return htmRoot;
    }

}