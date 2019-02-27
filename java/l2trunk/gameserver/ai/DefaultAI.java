package l2trunk.gameserver.ai;

import l2trunk.commons.lang.ArrayUtils;
import l2trunk.commons.math.random.RndSelector;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.AggroList.AggroInfo;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.entity.SevenSigns;
import l2trunk.gameserver.model.instances.MinionInstance;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.QuestEventType;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.StatusUpdate;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.taskmanager.AiTaskManager;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.NpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultAI extends CharacterAI {
    protected static final Logger _log = LoggerFactory.getLogger(DefaultAI.class);
    private static final int TASK_DEFAULT_WEIGHT = 10000;
    public static String namechar;
    protected final List<Skill> damSkills;
    protected final List<Skill> healSkills;
    private final Comparator<Creature> nearestTargetComparator;
    /**
     * Список заданий
     */
    private final NavigableSet<Task> tasks = new ConcurrentSkipListSet<>();
    private final List<Skill> _dotSkills;
    private final List<Skill> _debuffSkills;
    private final List<Skill> _buffSkills;
    private final List<Skill> _stunSkills;
    protected long AI_TASK_ATTACK_DELAY = Config.AI_TASK_ATTACK_DELAY;
    public long AI_TASK_ACTIVE_DELAY = Config.AI_TASK_ACTIVE_DELAY;
    protected int MAX_PURSUE_RANGE;
    protected ScheduledFuture<?> _aiTask;
    protected ScheduledFuture<?> madnessTask;
    /**
     * Показывает, есть ли задания
     */
    protected boolean defThink = false;
    /**
     * The L2NpcInstance aggro counter
     */
    protected long globalAggro;
    protected int _pathfindFails;
    protected long _checkAggroTimestamp = 0;
    protected long _minFactionNotifyInterval;
    private long AI_TASK_DELAY_CURRENT = AI_TASK_ACTIVE_DELAY;
    private ScheduledFuture<?> runningTask;
    private long _randomAnimationEnd;
    private long _lastActiveCheck;
    /**
     * Время актуальности состояния атаки
     */
    private long attackTimeout;
    private long _lastFactionNotifyTime = 0;
    /**
     * The flag used to indicate that a thinking action is in progress
     */
    private boolean _thinking = false;

    public DefaultAI(NpcInstance actor) {
        super(actor);

        setAttackTimeout(Long.MAX_VALUE);

        NpcInstance npc = getActor();
        damSkills = npc.getTemplate().getDamageSkills();
        _dotSkills = npc.getTemplate().getDotSkills();
        _debuffSkills = npc.getTemplate().getDebuffSkills();
        _buffSkills = npc.getTemplate().getBuffSkills();
        _stunSkills = npc.getTemplate().getStunSkills();
        healSkills = npc.getTemplate().getHealSkills();

        nearestTargetComparator = new NearestTargetComparator(actor);

        // Preload some AI params
        MAX_PURSUE_RANGE = actor.getParameter("MaxPursueRange", actor.isRaid() ? Config.MAX_PURSUE_RANGE_RAID : npc.isUnderground() ? Config.MAX_PURSUE_UNDERGROUND_RANGE : Config.MAX_PURSUE_RANGE);
        _minFactionNotifyInterval = actor.getParameter("FactionNotifyInterval", 10000);
    }

    private static Skill selectTopSkillByDamage(Creature actor, Creature target, double distance, List<Skill> skills) {
        if ((skills == null) || (skills.size() == 0)) {
            return null;
        }

        if (skills.size() == 1) {
            return skills.get(0);
        }

        RndSelector<Skill> rnd = new RndSelector<>(skills.size());
        double weight;
        for (Skill skill : skills) {
            weight = (skill.getSimpleDamage(actor, target) * skill.getAOECastRange()) / distance;
            if (weight < 1.) {
                weight = 1.;
            }
            rnd.add(skill, (int) weight);
        }
        return rnd.select();
    }

    private static Skill selectTopSkillByDebuff(Creature actor, Creature target, double distance, List<Skill> skills) {
        if ((skills == null) || (skills.size() == 0)) {
            return null;
        }

        if (skills.size() == 1) {
            return skills.get(0);
        }

        RndSelector<Skill> rnd = new RndSelector<>(skills.size());
        double weight;
        for (Skill skill : skills) {
            if (skill.getSameByStackType(target) != null) {
                continue;
            }
            if ((weight = (100. * skill.getAOECastRange()) / distance) <= 0) {
                weight = 1;
            }
            rnd.add(skill, (int) weight);
        }
        return rnd.select();
    }

    private static Skill selectTopSkillByBuff(Creature target, List<Skill> skills) {
        if ((skills == null) || (skills.size() == 0)) {
            return null;
        }

        if (skills.size() == 1) {
            return skills.get(0);
        }

        RndSelector<Skill> rnd = new RndSelector<>(skills.size());
        double weight;
        for (Skill skill : skills) {
            if (skill.getSameByStackType(target) != null) {
                continue;
            }
            if ((weight = skill.power) <= 0) {
                weight = 1;
            }
            rnd.add(skill, (int) weight);
        }
        return rnd.select();
    }

    private static Skill selectTopSkillByHeal(Creature target, List<Skill> skills) {
        if ((skills == null) || (skills.size() == 0)) {
            return null;
        }

        double hpReduced = target.getMaxHp() - target.getCurrentHp();
        if (hpReduced < 1) {
            return null;
        }

        if (skills.size() == 1) {
            return skills.get(0);
        }

        RndSelector<Skill> rnd = new RndSelector<>(skills.size());
        double weight;
        for (Skill skill : skills) {
            if ((weight = Math.abs(skill.power - hpReduced)) <= 0) {
                weight = 1;
            }
            rnd.add(skill, (int) weight);
        }
        return rnd.select();
    }

    protected void addTaskCast(Creature target, int skillId) {
        addTaskCast(target, skillId, 1);
    }

    protected void addTaskCast(Creature target, int skillId, int skillLvl) {
        Task task = new Task();
        task.type = TaskType.CAST;
        task.target = target;
        task.skill = SkillTable.INSTANCE.getInfo(skillId, skillLvl);
        tasks.add(task);
        defThink = true;
    }

    protected void addTaskBuff(Creature target, int skillid) {
        addTaskBuff(target, SkillTable.INSTANCE.getInfo(skillid));
    }

    protected void addTaskBuff(Creature target, Skill skill) {
        Task task = new Task();
        task.type = TaskType.BUFF;
        task.target = target;
        task.skill = skill;
        tasks.add(task);
        defThink = true;
    }

    public void addTaskAttack(Creature target) {
        Task task = new Task();
        task.type = TaskType.ATTACK;
        task.target = target;
        tasks.add(task);
        defThink = true;
    }

    protected void addTaskAttack(Creature target, int skillId, int skillLvl) {
        Task task = new Task();
        task.type = SkillTable.INSTANCE.getInfo(skillId, skillLvl).isOffensive ? TaskType.CAST : TaskType.BUFF;
        task.target = target;
        task.skill = SkillTable.INSTANCE.getInfo(skillId, skillLvl);

        task.weight = 1000000;
        tasks.add(task);
        defThink = true;
    }

    public void addTaskMove(Location loc, boolean pathfind) {
        Task task = new Task();
        task.type = TaskType.MOVE;
        task.loc = loc;
        task.pathfind = pathfind;
        tasks.add(task);
        defThink = true;
    }

    public void addTaskMove(int locX, int locY, int locZ, boolean pathfind) {
        addTaskMove(new Location(locX, locY, locZ), pathfind);
    }

    @Override
    public void runImpl() {
        if (_aiTask == null) {
            return;
        }
        if (!Config.ALLOW_NPC_AIS && (getActor() == null)) {
            return;
        }
        // проверяем, если NPC вышел в неактивный регион, отключаем AI
        if (!isGlobalAI() && ((System.currentTimeMillis() - _lastActiveCheck) > 60000L)) {
            _lastActiveCheck = System.currentTimeMillis();
            NpcInstance actor = getActor();
            WorldRegion region = actor == null ? null : actor.getCurrentRegion();
            if ((region == null) || !region.isActive()) {
                stopAITask();
                return;
            }
        }
        onEvtThink();
    }

    @Override
    // public final synchronized void startAITask()
    public synchronized void startAITask() {
        if (_aiTask == null) {
            AI_TASK_DELAY_CURRENT = AI_TASK_ACTIVE_DELAY;
            _aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(this, 0L, AI_TASK_DELAY_CURRENT);
        }
    }

    // protected final synchronized void switchAITask(long NEW_DELAY)
    public synchronized void switchAITask(long NEW_DELAY) {
        if (_aiTask == null) {
            return;
        }

        if (AI_TASK_DELAY_CURRENT != NEW_DELAY) {
            _aiTask.cancel(false);
            AI_TASK_DELAY_CURRENT = NEW_DELAY;
            _aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(this, 0L, AI_TASK_DELAY_CURRENT);
        }
    }

    @Override
    public final synchronized void stopAITask() {
        if (_aiTask != null) {
            _aiTask.cancel(false);
            _aiTask = null;
        }
    }

    /**
     * Определяет, может ли этот тип АИ видеть персонажей в режиме Silent Move.
     *
     * @param target L2Playable цель
     * @return true если цель видна в режиме Silent Move
     */
    public boolean canSeeInSilentMove(Playable target) {
        if (getActor().getParameter("canSeeInSilentMove", false)) {
            return true;
        }
        return !target.isSilentMoving();
    }

    public boolean canSeeInHide(Playable target) {
        if (getActor().getParameter("canSeeInHide", false)) {
            return true;
        }

        return !target.isInvisible();
    }

    public boolean checkAggression(Playable target) {
        return checkAggression(target, false);
    }

    public boolean checkAggression(Playable target, boolean avoidAttack) {
        NpcInstance actor = getActor();
        if ((getIntention() != CtrlIntention.AI_INTENTION_ACTIVE) || !isGlobalAggro()) {
            return false;
        }

        if (target.isAlikeDead()) {
            return false;
        }

        if (!canSeeInSilentMove(target)) {
            return false;
        }
        if (!canSeeInHide(target)) {
            return false;
        }
        if ("varka_silenos_clan".equalsIgnoreCase(actor.getFaction().getName()) && (target.getPlayer().getVarka() > 0)) {
            return false;
        }
        if ("ketra_orc_clan".equalsIgnoreCase(actor.getFaction().getName()) && (target.getPlayer().getKetra() > 0)) {
            return false;
        }
        /*
         * if (target.isFollow && !target.isPlayer() && target.getFollowTarget() != null && target.getFollowTarget().isPlayer()) return;
         */
        if (target instanceof Player && ((Player) target).isGM() && target.isInvisible()) {
            return false;
        }
        if (target.getNonAggroTime() > System.currentTimeMillis()) {
            return false;
        }
        if (actor instanceof MonsterInstance && target.isInZonePeace()) {
            return false;
        }

        AggroInfo ai = actor.getAggroList().get(target);
        if ((ai != null) && (ai.hate > 0)) {
            if (!target.isInRangeZ(actor.getSpawnedLoc(), MAX_PURSUE_RANGE)) {
                return false;
            }
        } else if (!actor.isAggressive() || !target.isInRangeZ(actor.getSpawnedLoc(), actor.getAggroRange())) {
            return false;
        }

        if (!canAttackCharacter(target)) {
            return false;
        }
        if (!GeoEngine.canSeeTarget(actor, target, false)) {
            return false;
        }

        // Ady - Posibility to call checkAggresion as a check without action
        if (!avoidAttack) {
            actor.getAggroList().addDamageHate(target, 0, 2);

            if (target instanceof Summon) {
                actor.getAggroList().addDamageHate(((Summon) target).owner, 0, 1);
            }

            startRunningTask(AI_TASK_ATTACK_DELAY);
            setIntentionAttack(target);
        }

        return true;
    }

    public boolean randomAnimation() {
        NpcInstance actor = getActor();

        if (actor.getParameter("noRandomAnimation", false)) {
            return false;
        }

        if (actor.hasRandomAnimation() && !actor.isActionsDisabled() && !actor.isMoving && !actor.isInCombat() && Rnd.chance(Config.RND_ANIMATION_RATE)) {
            _randomAnimationEnd = System.currentTimeMillis() + (long) 3000;
            actor.onRandomAnimation();
            return true;
        }
        return false;
    }

    public boolean randomWalk() {
        NpcInstance actor = getActor();

        if (actor.getParameter("noRandomWalk", false)) {
            return false;
        }

        return !actor.isMoving && maybeMoveToHome();
    }

    /**
     * @return true если действие выполнено, false если нет
     */
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isActionsDisabled()) {
            return true;
        }

        if (_randomAnimationEnd > System.currentTimeMillis()) {
            return true;
        }

        if (defThink) {
            if (doTask()) {
                clearTasks();
            }
            return true;
        }

        long now = System.currentTimeMillis();
        if ((now - _checkAggroTimestamp) > Config.AGGRO_CHECK_INTERVAL) {
            _checkAggroTimestamp = now;

            boolean aggressive = Rnd.chance(actor.getParameter("SelfAggressive", actor.isAggressive() ? 100 : 0));
            if (!actor.getAggroList().isEmpty() || aggressive) {
                /*
                 * Ady - Changed completely the logic. Now we get the surroundings, then check the aggresion of everyone and make a final list of possible targets
                 * We call checkAggresion but without action, only checking, then if aggrolist is not empty then we sort it by distance and do the attack
                 * If done otherwise, the performance drop is huge
                 */
                List<Playable> knowns = World.getAroundPlayables(actor).collect(Collectors.toList());
                if (!knowns.isEmpty()) {
                    final List<Playable> aggroList = knowns.stream()
                            .filter(cha -> aggressive || actor.getAggroList().get(cha) != null)
                            .filter(cha -> checkAggression(cha, true))
                            .collect(Collectors.toList());


                    if (actor.isDead())
                        return true;

                    // Only sort if there is actually a target to attack
                    if (!aggroList.isEmpty()) {
                        aggroList.sort(nearestTargetComparator);

                        for (Playable target : aggroList) {
                            if (target == null || target.isAlikeDead())
                                continue;

							/*
							actor.getAggroList().addDamageHate(target, 0, 2);

							if ((target.SummonInstance() || target.PetInstance()))
							{
								actor.getAggroList().addDamageHate(target.getPlayer(), 0, 1);
							}

							startRunningTask(AI_TASK_ATTACK_DELAY);
							setIntentionAttack(CtrlIntention.AI_INTENTION_ATTACK, target);
							*/
                            if (checkAggression(target, false))
                                return true;
                        }
                    }
                }
            }
        }

        if (actor instanceof MinionInstance) {
            MonsterInstance leader = ((MinionInstance) actor).getLeader();
            if (leader != null) {
                double distance = actor.getDistance(leader.getX(), leader.getY());
                if (distance > 1000) {
                    actor.teleToLocation(leader.getMinionPosition());
                } else if (distance > 200) {
                    addTaskMove(leader.getMinionPosition(), false);
                }
                return true;
            }
        }

        if (randomAnimation()) {
            return true;
        }
        return randomWalk();
    }

    @Override
    public void onIntentionIdle() {
        NpcInstance actor = getActor();

        // Удаляем все задания
        clearTasks();

        actor.stopMove();
        actor.getAggroList().clear(true);
        setAttackTimeout(Long.MAX_VALUE);
        setAttackTarget(null);

        changeIntention(CtrlIntention.AI_INTENTION_IDLE);
    }

    @Override
    public void onIntentionActive() {
        NpcInstance actor = getActor();

        actor.stopMove();
        setAttackTimeout(Long.MAX_VALUE);

        if (getIntention() != CtrlIntention.AI_INTENTION_ACTIVE) {
            switchAITask(AI_TASK_ACTIVE_DELAY);
            changeIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }

        onEvtThink();
    }

    @Override
    public void onIntentionAttack(Creature target) {
        NpcInstance actor = getActor();

        // Removes all jobs
        clearTasks();

        actor.stopMove();
        setAttackTarget(target);
        setAttackTimeout(getMaxAttackTimeout() + System.currentTimeMillis());
        setGlobalAggro(0);

        if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK) {
            changeIntention(CtrlIntention.AI_INTENTION_ATTACK);
            switchAITask(AI_TASK_ATTACK_DELAY);
        }

        onEvtThink();
    }

    public boolean canAttackCharacter(Creature target) {
        return target instanceof Playable;
    }

    public boolean checkTarget(Creature target, int range) {
        NpcInstance actor = getActor();
        if ((target == null) || target.isAlikeDead() || !actor.isInRangeZ(target, range)) {
            return false;
        }

        // если не видим чаров в хайде - не атакуем их
        final boolean hided = target instanceof Playable && !canSeeInHide((Playable) target);

        if (!hided && actor.isConfused()) {
            return true;
        }

        // В состоянии атаки атакуем всех, на кого у нас есть хейт
        if (getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
            AggroInfo ai = actor.getAggroList().get(target);
            if (ai != null) {
                if (hided) {
                    ai.hate = 0; // очищаем хейт
                    return false;
                }
                return ai.hate > 0;
            }
            return false;
        }

        return canAttackCharacter(target);
    }

    private long getAttackTimeout() {
        return attackTimeout;
    }

    protected void setAttackTimeout(long time) {
        attackTimeout = time;
    }

    public void thinkAttack() {
        NpcInstance actor = getActor();
        if (actor.isDead()) {
            return;
        }

        Location loc = actor.getSpawnedLoc();
        if (!actor.isInRange(loc, MAX_PURSUE_RANGE)) {
            teleportHome();
            return;
        }

        if (doTask() && !actor.isAttackingNow() && !actor.isCastingNow()) {
            if (!createNewTask()) {
                if (System.currentTimeMillis() > getAttackTimeout()) {
                    returnHome();
                }
            }
        }
    }

    @Override
    public void onEvtSpawn() {
        setGlobalAggro(System.currentTimeMillis() + getActor().getParameter("globalAggro", 10000L));

        setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }

    @Override
    public void onEvtReadyToAct() {
        onEvtThink();
    }

    @Override
    public void onEvtArrivedTarget() {
        onEvtThink();
    }

    @Override
    public void onEvtArrived() {
        onEvtThink();
    }

    private void tryMoveToTarget(Creature target) {
        NpcInstance actor = getActor();

        if (!actor.followToCharacter(target, actor.getPhysicalAttackRange(), true)) {
            _pathfindFails++;
        }

        if ((_pathfindFails >= getMaxPathfindFails()) && (System.currentTimeMillis() > ((getAttackTimeout() - getMaxAttackTimeout()) + getTeleportTimeout())) && actor.isInRange(target, MAX_PURSUE_RANGE)) {
            _pathfindFails = 0;

            if (target instanceof Playable) {
                AggroInfo hate = actor.getAggroList().get(target);
                if ((hate == null) || (hate.hate < 100)) {
                    returnHome();
                    return;
                }
            }
            Location loc = GeoEngine.moveCheckForAI(target.getLoc(), actor.getLoc(), actor.getGeoIndex());
            if (!GeoEngine.canMoveToCoord(actor, loc)) {
                loc = target.getLoc();
            }
            if (canTeleWhenCannotSeeTarget())
                actor.teleToLocation(loc);
        }

    }

    public boolean canTeleWhenCannotSeeTarget() {
        return true;
    }

    private boolean maybeNextTask(Task currentTask) {
        // Следующее задание
        tasks.remove(currentTask);
        // Если заданий больше нет - определить новое
        return tasks.size() == 0;
    }

    protected boolean doTask() {
        NpcInstance actor = getActor();

        if (!defThink) {
            return true;
        }

        Task currentTask = tasks.pollFirst();
        if (currentTask == null) {
            clearTasks();
            return true;
        }

        if (actor.isDead() || actor.isAttackingNow() || actor.isCastingNow()) {
            return false;
        }

        switch (currentTask.type) {
            // Задание "прибежать в заданные координаты"
            case MOVE: {
                if (actor.isMovementDisabled() || !getIsMobile()) {
                    return true;
                }

                if (actor.isInRange(currentTask.loc, 100)) {
                    return maybeNextTask(currentTask);
                }

                if (actor.isMoving) {
                    return false;
                }

                if (!actor.moveToLocation(currentTask.loc, 0, currentTask.pathfind)) {
                    clientStopMoving();
                    _pathfindFails = 0;
                    actor.teleToLocation(currentTask.loc);
                    // actor.broadcastPacketToOthers(new MagicSkillUse(actor, actor, 2036, 1, 500, 600000));
                    // ThreadPoolManager.INSTANCE().scheduleAi(new teleport(currentTask.loc), 500, false);
                    return maybeNextTask(currentTask);
                }
            }
            break;
            // Задание "добежать - ударить"
            case ATTACK: {
                Creature target = currentTask.target;

                if (!checkTarget(target, MAX_PURSUE_RANGE)) {
                    return true;
                }

                setAttackTarget(target);

                if (actor.isMoving) {
                    return Rnd.chance(25);
                }

                if ((actor.getRealDistance3D(target) <= (actor.getPhysicalAttackRange() + 40)) && GeoEngine.canSeeTarget(actor, target, false)) {
                    clientStopMoving();
                    _pathfindFails = 0;
                    setAttackTimeout(getMaxAttackTimeout() + System.currentTimeMillis());
                    actor.doAttack(target);
                    return maybeNextTask(currentTask);
                }

                if (actor.isMovementDisabled() || !getIsMobile()) {
                    return true;
                }

                tryMoveToTarget(target);
            }
            break;
            // Задание "добежать - атаковать скиллом"
            case CAST: {
                Creature target = currentTask.target;

                if (actor.isMuted(currentTask.skill) || actor.isSkillDisabled(currentTask.skill) || actor.isUnActiveSkill(currentTask.skill.id)) {
                    return true;
                }

                boolean isAoE = currentTask.skill.targetType == Skill.SkillTargetType.TARGET_AURA;
                int castRange = currentTask.skill.getAOECastRange();

                if (!checkTarget(target, MAX_PURSUE_RANGE + castRange)) {
                    return true;
                }

                setAttackTarget(target);

                if ((actor.getRealDistance3D(target) <= (castRange + 60)) && GeoEngine.canSeeTarget(actor, target, false)) {
                    clientStopMoving();
                    _pathfindFails = 0;
                    setAttackTimeout(getMaxAttackTimeout() + System.currentTimeMillis());
                    actor.doCast(currentTask.skill, isAoE ? actor : target, !(target instanceof Playable));
                    return maybeNextTask(currentTask);
                }

                if (actor.isMoving) {
                    return Rnd.chance(10);
                }

                if (actor.isMovementDisabled() || !getIsMobile()) {
                    return true;
                }

                tryMoveToTarget(target);
            }
            break;
            // Task "to run - use skill"
            case BUFF: {
                Creature target = currentTask.target;

                if (actor.isMuted(currentTask.skill) || actor.isSkillDisabled(currentTask.skill) || actor.isUnActiveSkill(currentTask.skill.id)) {
                    return true;
                }

                if ((target == null) || target.isAlikeDead() || !actor.isInRange(target, 2000)) {
                    return true;
                }

                boolean isAoE = currentTask.skill.targetType == Skill.SkillTargetType.TARGET_AURA;
                int castRange = currentTask.skill.getAOECastRange();

                if (actor.isMoving) {
                    return Rnd.chance(10);
                }

                if ((actor.getRealDistance3D(target) <= (castRange + 60)) && GeoEngine.canSeeTarget(actor, target, false)) {
                    clientStopMoving();
                    _pathfindFails = 0;
                    actor.doCast(currentTask.skill, isAoE ? actor : target, !(target instanceof Playable));
                    return maybeNextTask(currentTask);
                }

                if (actor.isMovementDisabled() || !getIsMobile()) {
                    return true;
                }

                tryMoveToTarget(target);
            }
            break;
        }

        return false;
    }

    public boolean createNewTask() {
        return false;
    }

    protected boolean defaultNewTask() {
        clearTasks();

        NpcInstance actor = getActor();
        Creature target;
        if ((actor == null) || ((target = prepareTarget()) == null)) {
            return false;
        }

        double distance = actor.getDistance(target);
        return chooseTaskAndTargets(null, target, distance);
    }

    @Override
    public void onEvtThink() {
        NpcInstance actor = getActor();
        if (_thinking || (actor == null) || actor.isActionsDisabled() || actor.isAfraid()) {
            return;
        }

        if (_randomAnimationEnd > System.currentTimeMillis()) {
            return;
        }

        _thinking = true;
        try {
            switch (getIntention()) {
                case AI_INTENTION_ACTIVE:
                    if (!Config.BLOCK_ACTIVE_TASKS)
                        thinkActive();
                    break;
                case AI_INTENTION_ATTACK:
                    thinkAttack();
                    break;
            }
        } finally {
            _thinking = false;
        }
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();

        int transformer = actor.getParameter("transformOnDead", 0);
        int chance = actor.getParameter("transformChance", 100);
        if ((transformer > 0) && Rnd.chance(chance)) {
            NpcInstance npc = NpcUtils.spawnSingle(transformer, actor.getLoc(), actor.getReflection());

            if (killer instanceof Playable) {
                npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 100);
                killer.setTarget(npc);
                killer.sendPacket(npc.makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP));
            }
        }

        super.onEvtDead(killer);
    }

    @Override
    public void onEvtClanAttacked(Creature attacked, Creature attacker, int damage) {
        if (!isGlobalAggro()) {
            return;
        }

        if (damage > 0) {
            notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2);
        }
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if ((attacker == null) || actor.isDead()) {
            if (actor.isDead()) {
                notifyFriends(attacker, damage);
            }
            return;
        }

        int transformer = actor.getParameter("transformOnUnderAttack", 0);
        if (transformer > 0) {
            int chance = actor.getParameter("transformChance", 5);
            if ((chance == 100) || ((((MonsterInstance) actor).getChampion() == 0) && (actor.getCurrentHpPercents() > 50) && Rnd.chance(chance))) {
                MonsterInstance npc = (MonsterInstance) NpcHolder.getTemplate(transformer).getNewInstance();
                npc.setSpawnedLoc(actor.getLoc());
                npc.setReflection(actor.getReflection());
                npc.setChampion(((MonsterInstance) actor).getChampion());
                npc.setFullHpMp();
                npc.spawnMe(npc.getSpawnedLoc());
                npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 100);
                actor.doDie(actor);
                actor.decayMe();
                attacker.setTarget(npc);
                attacker.sendPacket(npc.makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP));
                return;
            }
        }

        Player player = attacker.getPlayer();

        if (player != null) {
            if (((SevenSigns.INSTANCE.isSealValidationPeriod()) || (SevenSigns.INSTANCE.isCompResultsPeriod())) && (actor.isSevenSignsMonster()) && (Config.RETAIL_SS)) {
                int pcabal = SevenSigns.INSTANCE.getPlayerCabal(player);
                int wcabal = SevenSigns.INSTANCE.getCabalHighestScore();
                if ((pcabal != wcabal) && (wcabal != SevenSigns.CABAL_NULL)) {
                    player.sendMessage("You have been teleported to the nearest town because you not signed for winning cabal.");
                    player.teleToClosestTown();
                    return;
                }
            }
            player.getQuestsForEvent(actor, QuestEventType.ATTACKED_WITH_QUEST)
                    .forEach(qs -> qs.quest.notifyAttack(actor, qs));
        }

        // Добавляем только хейт, урон, если атакующий - игровой персонаж, будет добавлен в L2NpcInstance.onReduceCurrentHp
        actor.getAggroList().addDamageHate(attacker, 0, damage);

        // Обычно 1 хейт добавляется хозяину суммона, чтобы после смерти суммона моб накинулся на хозяина.
        if (damage > 0 && attacker instanceof Summon) {
            actor.getAggroList().addDamageHate(((Summon) attacker).owner, 0, actor.getParameter("searchingMaster", false) ? damage : 1);
        }

        if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK) {
            if (!actor.isRunning()) {
                startRunningTask(AI_TASK_ATTACK_DELAY);
            }
            setIntentionAttack(attacker);
        }

        notifyFriends(attacker, damage);
    }

    @Override
    public void onEvtAggression(Creature attacker, int aggro) {
        NpcInstance actor = getActor();
        if ((attacker == null) || actor.isDead()) {
            return;
        }

        actor.getAggroList().addDamageHate(attacker, 0, aggro);

        // Обычно 1 хейт добавляется хозяину суммона, чтобы после смерти суммона моб накинулся на хозяина.
        if ((aggro > 0) && (attacker instanceof Summon)) {
            actor.getAggroList().addDamageHate(((Summon) attacker).owner, 0, actor.getParameter("searchingMaster", false) ? aggro : 1);
        }

        if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK) {
            if (!actor.isRunning()) {
                startRunningTask(AI_TASK_ATTACK_DELAY);
            }
            setIntentionAttack(attacker);
        }
    }

    public boolean maybeMoveToHome() {
        NpcInstance actor = getActor();
        if (actor.isDead()) {
            return false;
        }

        boolean randomWalk = actor.hasRandomWalk();
        Location sloc = actor.getSpawnedLoc();

        // Random walk or not?
        if (randomWalk && (!Config.RND_WALK || !Rnd.chance(Config.RND_WALK_RATE))) {
            return false;
        }

        boolean isInRange = actor.isInRangeZ(sloc, Config.MAX_DRIFT_RANGE);

        if (!randomWalk && isInRange) {
            return false;
        }

        Location pos = Location.findPointToStay(actor, sloc, 0, Config.MAX_DRIFT_RANGE);

        actor.setWalking();

        // Телепортируемся домой, только если далеко от дома
        if (!actor.moveToLocation(pos, 0, true) && !isInRange) {
            teleportHome();
        }

        return true;
    }

    public void returnHome() {
        returnHome(true, Config.ALWAYS_TELEPORT_HOME);
    }

    public void teleportHome() {
        returnHome(true, true);
    }

    public void returnHome(boolean clearAggro, boolean teleport) {
        NpcInstance actor = getActor();
        Location sloc = actor.getSpawnedLoc();

        // Removes all jobs
        clearTasks();
        actor.stopMove();

        if (clearAggro) {
            actor.getAggroList().clear(true);
        }

        setAttackTimeout(Long.MAX_VALUE);
        setAttackTarget(null);

        changeIntention(CtrlIntention.AI_INTENTION_ACTIVE);

        if (teleport) {
            actor.broadcastPacketToOthers(new MagicSkillUse(actor, 2036, 500));
            actor.teleToLocation(sloc.x, sloc.y, GeoEngine.getHeight(sloc, actor.getGeoIndex()));
        } else {
            if (!clearAggro) {
                actor.setRunning();
            } else {
                actor.setWalking();
            }

            addTaskMove(sloc, false);
        }
    }

    public Creature prepareTarget() {
        NpcInstance actor = getActor();

        if (actor.isConfused()) {
            return getAttackTarget();
        }

        // Для "двинутых" боссов, иногда, выбираем случайную цель
        if (Rnd.chance(actor.getParameter("isMadness", 0))) {
            Creature randomHated = actor.getAggroList().getRandomHated();
            if (randomHated != null) {
                setAttackTarget(randomHated);
                if ((madnessTask == null) && !actor.isConfused()) {
                    actor.startConfused();
                    madnessTask = ThreadPoolManager.INSTANCE.schedule(new MadnessTask(), 10000);
                }
                return randomHated;
            }
        }

        // Новая цель исходя из агрессивности
        List<Playable> hateList = actor.getAggroList().getHateList();
        Creature hated = null;
        for (Playable cha : hateList) {
            // Не подходит, очищаем хейт
            if (!checkTarget(cha, MAX_PURSUE_RANGE)) {
                actor.getAggroList().remove(cha, true);
                continue;
            }
            hated = cha;
            break;
        }

        if (hated != null) {
            setAttackTarget(hated);
            return hated;
        }

        return null;
    }

    protected boolean canUseSkill(Skill skill, Creature target, double distance) {
        NpcInstance actor = getActor();
        if ((skill == null) || skill.isNotUsedByAI) {
            return false;
        }

        if ((skill.targetType == Skill.SkillTargetType.TARGET_SELF) && (target != actor)) {
            return false;
        }

        int castRange = skill.getAOECastRange();
        if ((castRange <= 200) && (distance > 200)) {
            return false;
        }

        if (actor.isSkillDisabled(skill) || actor.isMuted(skill) || actor.isUnActiveSkill(skill.id)) {
            return false;
        }

        double mpConsume2 = skill.mpConsume2;
        if (skill.isMagic()) {
            mpConsume2 = actor.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsume2, target, skill);
        } else {
            mpConsume2 = actor.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsume2, target, skill);
        }
        if (actor.getCurrentMp() < mpConsume2) {
            return false;
        }

        return target.getEffectList().getEffectsCountForSkill(skill.id) == 0;
    }

    private boolean canUseSkill(Skill sk, Creature target) {
        return canUseSkill(sk, target, 0);
    }

    private List<Skill> selectUsableSkills(Creature target, double distance, List<Skill> skills) {
        if ((skills == null) || (skills.size() == 0) || (target == null)) {
            return null;
        }
        return skills.stream().filter(a -> canUseSkill(a, target, distance)).collect(Collectors.toList());
    }

    protected void addDesiredSkill(Map<Skill, Integer> skillMap, Creature target, double distance, int skillId, int skillLvl) {
        addDesiredSkill(skillMap, target, distance, SkillTable.INSTANCE.getInfo(skillId, skillLvl));
    }

    protected void addDesiredSkill(Map<Skill, Integer> skillMap, Creature target, double distance, int skillId) {
        addDesiredSkill(skillMap, target, distance, skillId, 1);
    }

    private void addDesiredSkill(Map<Skill, Integer> skillMap, Creature target, double distance, Skill skill) {
        if ((skill == null) || (target == null) || !canUseSkill(skill, target)) {
            return;
        }
        int weight = (int) -Math.abs(skill.getAOECastRange() - distance);
        if (skill.getAOECastRange() >= distance) {
            weight += 1000000;
        } else if (skill.isNotTargetAoE() && (skill.getTargets(getActor(), target, false).size() == 0)) {
            return;
        }
        skillMap.put(skill, weight);
    }

    protected int selectTopSkill(Map<Skill, Integer> skillMap) {
        if ((skillMap == null) || skillMap.isEmpty()) {
            return 0;
        }
        int nWeight, topWeight = Integer.MIN_VALUE;
        for (Skill next : skillMap.keySet()) {
            if ((nWeight = skillMap.get(next)) > topWeight) {
                topWeight = nWeight;
            }
        }
        if (topWeight == Integer.MIN_VALUE) {
            return 0;
        }

        int[] skills = new int[skillMap.size()];
        nWeight = 0;
        for (Map.Entry<Skill, Integer> e : skillMap.entrySet()) {
            if (e.getValue() < topWeight) {
                continue;
            }
            skills[nWeight++] = e.getKey().id;
        }
        return skills[Rnd.get(nWeight)];
    }

    protected boolean chooseTaskAndTargets(int skillId, Creature target, double distance) {
        return chooseTaskAndTargets(SkillTable.INSTANCE.getInfo(skillId), target, distance);
    }

    protected boolean chooseTaskAndTargets(Skill skill, Creature target, double distance) {
        NpcInstance actor = getActor();

        // Использовать скилл если можно, иначе атаковать
        if (skill != null) {
            // Проверка цели, и смена если необходимо
            if (actor.isMovementDisabled() && (distance > (skill.getAOECastRange() + 60))) {
                target = null;
                if (skill.isOffensive) {
                    ArrayList<Creature> targets = new ArrayList<>();
                    for (Creature cha : actor.getAggroList().getHateList()) {
                        if (!checkTarget(cha, skill.getAOECastRange() + 60) || !canUseSkill(skill, cha)) {
                            continue;
                        }
                        targets.add(cha);
                    }
                    if (!targets.isEmpty()) {
                        target = Rnd.get(targets);
                    }
                }
            }

            if (target == null) {
                return false;
            }

            // Добавить новое задание
            if (skill.isOffensive) {
                addTaskCast(target, skill.id);
            } else {
                addTaskBuff(target, skill);
            }
            return true;
        }

        // Смена цели, если необходимо
        if (actor.isMovementDisabled() && (distance > (actor.getPhysicalAttackRange() + 40))) {
            target = null;
            ArrayList<Creature> targets = new ArrayList<>();
            for (Creature cha : actor.getAggroList().getHateList()) {
                if (!checkTarget(cha, actor.getPhysicalAttackRange() + 40)) {
                    continue;
                }
                targets.add(cha);
            }
            if (!targets.isEmpty()) {
                target = Rnd.get(targets);
            }
        }

        if (target == null) {
            return false;
        }

        // Добавить новое задание
        addTaskAttack(target);
        return true;
    }

    @Override
    public boolean isActive() {
        return _aiTask != null;
    }

    protected void clearTasks() {
        defThink = false;
        tasks.clear();
    }

    /**
     * переход в режим бега через определенный интервал времени
     */
    protected void startRunningTask(long interval) {
        NpcInstance actor = getActor();
        if ((actor != null) && (runningTask == null) && !actor.isRunning()) {
            runningTask = ThreadPoolManager.INSTANCE.schedule(new RunningTask(), interval);
        }
    }

    public boolean isGlobalAggro() {
        if (globalAggro == 0) {
            return true;
        }
        if (globalAggro <= System.currentTimeMillis()) {
            globalAggro = 0;
            return true;
        }
        return false;
    }

    public void setGlobalAggro(long value) {
        globalAggro = value;
    }

    @Override
    public NpcInstance getActor() {
        return (NpcInstance) super.getActor();
    }

    boolean defaultThinkBuff() {
        return defaultThinkBuff(10, 0);
    }

    /**
     * Оповестить дружественные цели об атаке.
     */
    protected void notifyFriends(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if ((System.currentTimeMillis() - _lastFactionNotifyTime) > _minFactionNotifyInterval) {
            _lastFactionNotifyTime = System.currentTimeMillis();
            if (actor instanceof MinionInstance) {
                // Оповестить лидера об атаке
                MonsterInstance master = ((MinionInstance) actor).getLeader();
                if (master != null) {
                    if (!master.isDead() && master.isVisible()) {
                        master.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, damage);
                    }

                    // Оповестить минионов лидера об атаке
                    MinionList minionList = master.getMinionList();
                    if (minionList != null) {
                        for (MinionInstance minion : minionList.getAliveMinions()) {
                            if (minion != actor) {
                                minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, damage);
                            }
                        }
                    }
                }
            }

            // Оповестить своих минионов об атаке
            MinionList minionList = actor.getMinionList();
            if ((minionList != null) && minionList.hasAliveMinions()) {
                for (MinionInstance minion : minionList.getAliveMinions()) {
                    minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, damage);
                }
            }

            // Оповестить социальных мобов
            activeFactionTargets()
                    .forEach(npc -> npc.getAI().notifyEventClanAttack(actor, attacker, damage));
        }
    }

    private Stream<NpcInstance> activeFactionTargets() {
        NpcInstance actor = getActor();
        if (actor.getFaction().isNone()) {
            return Stream.empty();
        }
        return World.getAroundNpc(actor)
                .filter(npc -> !npc.isDead())
                .filter(npc -> npc.isInFaction(actor))
                .filter(npc -> npc.isInRangeZ(actor, npc.getFaction().getRange()))
                .filter(npc -> GeoEngine.canSeeTarget(npc, actor, false));

    }

    boolean defaultThinkBuff(int rateSelf, int rateFriends) {
        NpcInstance actor = getActor();
        if (actor.isDead()) {
            return true;
        }

        // TODO сделать более разумный выбор баффа, сначала выбирать подходящие а потом уже рандомно 1 из них
        if (Rnd.chance(rateSelf)) {
            double actorHp = actor.getCurrentHpPercents();

            List<Skill> skills = actorHp < 50 ? selectUsableSkills(actor, 0, healSkills) : selectUsableSkills(actor, 0, _buffSkills);
            if ((skills == null) || (skills.size() == 0)) {
                return false;
            }

            Skill skill = Rnd.get(skills);
            addTaskBuff(actor, skill);
            return true;
        }

        if (Rnd.chance(rateFriends)) {
            return activeFactionTargets()
                    .map(npc -> npc.getCurrentHpPercents() < 50 ? selectUsableSkills(actor, 0, healSkills) : selectUsableSkills(actor, 0, _buffSkills))
                    .filter(Objects::nonNull)
                    .peek(skills -> addTaskBuff(actor, Rnd.get(skills)))
                    .findFirst().isPresent();

        }
        return false;
    }

    protected boolean defaultFightTask() {
        clearTasks();

        NpcInstance actor = getActor();
        if (actor.isDead() || actor.isAMuted()) {
            return false;
        }

        Creature target;
        if ((target = prepareTarget()) == null) {
            return false;
        }

        double distance = actor.getDistance(target);
        double targetHp = target.getCurrentHpPercents();
        double actorHp = actor.getCurrentHpPercents();

        List<Skill> dam = Rnd.chance(getRateDAM()) ? selectUsableSkills(target, distance, damSkills) : null;
        List<Skill> dot = Rnd.chance(getRateDOT()) ? selectUsableSkills(target, distance, _dotSkills) : null;
        List<Skill> debuff = targetHp > 10 ? Rnd.chance(getRateDEBUFF()) ? selectUsableSkills(target, distance, _debuffSkills) : null : null;
        List<Skill> stun = Rnd.chance(getRateSTUN()) ? selectUsableSkills(target, distance, _stunSkills) : null;
        List<Skill> heal = actorHp < 50 ? Rnd.chance(getRateHEAL()) ? selectUsableSkills(actor, 0, healSkills) : null : null;
        List<Skill> buff = Rnd.chance(getRateBUFF()) ? selectUsableSkills(actor, 0, _buffSkills) : null;

        RndSelector<List<Skill>> rnd = new RndSelector<>();
        if (!actor.isAMuted()) {
            rnd.add(null, getRatePHYS());
        }
        rnd.add(dam, getRateDAM());
        rnd.add(dot, getRateDOT());
        rnd.add(debuff, getRateDEBUFF());
        rnd.add(heal, getRateHEAL());
        rnd.add(buff, getRateBUFF());
        rnd.add(stun, getRateSTUN());

        List<Skill> selected = rnd.select();
        if (selected != null) {
            if ((ArrayUtils.equalLists(selected, dam) || ArrayUtils.equalLists(selected, dot))) {
                return chooseTaskAndTargets(selectTopSkillByDamage(actor, target, distance, selected), target, distance);
            }

            if ((selected == debuff) || (selected == stun)) {
                return chooseTaskAndTargets(selectTopSkillByDebuff(actor, target, distance, selected), target, distance);
            }

            if (selected == buff) {
                return chooseTaskAndTargets(selectTopSkillByBuff(actor, selected), actor, distance);
            }

            if (selected == heal) {
                return chooseTaskAndTargets(selectTopSkillByHeal(actor, selected), actor, distance);
            }
        }

        // TODO сделать лечение и баф дружественных целей

        return chooseTaskAndTargets(null, target, distance);
    }

    int getRatePHYS() {
        return 100;
    }

    int getRateDOT() {
        return 0;
    }

    int getRateDEBUFF() {
        return 0;
    }

    int getRateDAM() {
        return 0;
    }

    int getRateSTUN() {
        return 0;
    }

    int getRateBUFF() {
        return 0;
    }

    int getRateHEAL() {
        return 0;
    }

    private boolean getIsMobile() {
        return !getActor().getParameter("isImmobilized", false);
    }

    public int getMaxPathfindFails() {
        return 3;
    }

    /**
     * Задержка, перед переключением в активный режим после атаки, если цель не найдена (вне зоны досягаемости, убита, очищен хейт)
     */
    public int getMaxAttackTimeout() {
        return 15000;
    }

    /**
     * Задержка, перед телепортом к цели, если не удается дойти
     */
    private int getTeleportTimeout() {
        return 10000;
    }

    public enum TaskType {
        MOVE,
        ATTACK,
        CAST,
        BUFF
    }

    private static class Task implements Comparable<Task> {
        TaskType type;
        Skill skill;
        Creature target;
        Location loc;
        boolean pathfind;
        int weight = TASK_DEFAULT_WEIGHT;

        @Override
        public int compareTo(Task o) {
            if (o == null) return 0;
            return o.weight - this.weight;
        }
    }

    private static class NearestTargetComparator implements Comparator<Creature> {
        private final Creature actor;

        NearestTargetComparator(Creature actor) {
            this.actor = actor;
        }

        @Override
        public int compare(Creature o1, Creature o2) {
            return  (int)(actor.getDistance3DNoRoot(o1) - actor.getDistance3DNoRoot(o2));
        }
    }

    protected class Teleport extends RunnableImpl {
        final Location destination;

        public Teleport(Location destination) {
            this.destination = destination;
        }

        @Override
        public void runImpl() {
            NpcInstance actor = getActor();
            if (actor != null) {
                actor.teleToLocation(destination);
            }
        }
    }

    protected class RunningTask extends RunnableImpl {
        @Override
        public void runImpl() {
            NpcInstance actor = getActor();
            if (actor != null) {
                actor.setRunning();
            }
            runningTask = null;
        }
    }

    protected class MadnessTask extends RunnableImpl {
        @Override
        public void runImpl() {
            NpcInstance actor = getActor();
            if (actor != null) {
                actor.stopConfused();
            }
            madnessTask = null;
        }
    }
}