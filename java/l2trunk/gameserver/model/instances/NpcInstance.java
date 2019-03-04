package l2trunk.gameserver.model.instances;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CharacterAI;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.data.xml.holder.MultiSellHolder;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.data.xml.holder.SkillAcquireHolder;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.handler.bbs.CommunityBoardManager;
import l2trunk.gameserver.handler.bbs.ICommunityBoardHandler;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.instancemanager.DimensionalRiftManager;
import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.listener.NpcListener;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.GameObjectTasks.NotifyAITask;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.actor.listener.NpcListenerList;
import l2trunk.gameserver.model.actor.recorder.NpcStatsChangeRecorder;
import l2trunk.gameserver.model.base.AcquireType;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.entity.DimensionalRift;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.SevenSigns;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.entity.events.objects.TerritoryWardObject;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.model.entity.residence.Dominion;
import l2trunk.gameserver.model.entity.residence.Fortress;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.SubUnit;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestEventType;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Events;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.FuncMonsterBalancer;
import l2trunk.gameserver.tables.ClanTable;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.taskmanager.DecayTaskManager;
import l2trunk.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.templates.item.WeaponTemplate;
import l2trunk.gameserver.templates.npc.Faction;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.templates.spawn.SpawnRange;
import l2trunk.gameserver.utils.*;
import l2trunk.scripts.quests._136_MoreThanMeetsTheEye;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

public class NpcInstance extends Creature {
    private static final String NO_CHAT_WINDOW = "noChatWindow";
    private static final String NO_RANDOM_WALK = "noRandomWalk";
    private static final String NO_RANDOM_ANIMATION = "noRandomAnimation";
    private static final String TARGETABLE = "TargetEnabled";
    private static final String SHOW_NAME = "showName";
    private static final String SHOW_BOARD = "showBoard";
    private static final Logger _log = LoggerFactory.getLogger(NpcInstance.class);
    private final AggroList aggroList;
    private final String _showBoard;
    protected int _spawnAnimation = 2;
    protected boolean _hasRandomAnimation;
    protected boolean _hasRandomWalk;
    long _lastSocialAction;
    private boolean _hasChatWindow;
    private boolean _unAggred = false;
    private int _personalAggroRange = -1;
    private int level = 0;
    private long _dieTime = 0L;
    private int aiSpawnParam;
    private int currentLHandId;
    private int currentRHandId;
    private double _currentCollisionRadius;
    private double _currentCollisionHeight;
    private int npcState = 0;
    private Future<?> _animationTask;
    private boolean _isTargetable;
    private boolean _showName;
    private Castle nearestCastle;
    private Fortress _nearestFortress;
    private ClanHall _nearestClanHall;
    private Dominion nearestDominion;
    private NpcString nameNpcString = NpcString.NONE;
    private NpcString _titleNpcString = NpcString.NONE;
    private Spawner spawn;
    private Location spawnedLoc = new Location();
    private SpawnRange _spawnRange;
    private StatsSet parameters = StatsSet.EMPTY;
    private int displayId = 0;
    private ScheduledFuture<?> _broadcastCharInfoTask;
    private boolean _isBusy;
    private String _busyMessage = "";
    private boolean isUnderground = false;

    public NpcInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setParameters(template.getAiParams());

        _hasRandomAnimation = !getParameter(NO_RANDOM_ANIMATION, false) && (Config.MAX_NPC_ANIMATION > 0);
        _hasRandomWalk = !getParameter(NO_RANDOM_WALK, false);
        setHasChatWindow(!getParameter(NO_CHAT_WINDOW, false));
        setTargetable(getParameter(TARGETABLE, true));
        setShowName(getParameter(SHOW_NAME, true));
        _showBoard = getParameter(SHOW_BOARD, "");

        template.getSkills().values().stream()
                .mapToInt(s -> s.id)
                .forEach(this::addSkill);

        setName(template.name);

        String customTitle = template.title;
        if (this instanceof MonsterInstance) {
            customTitle = "LvL: " + this.getLevel();
            if (this.isAggressive()) customTitle += " *";
        }
        setTitle(customTitle);

        // инициализация параметров оружия
        setLHandId(getTemplate().lhand);
        setRHandId(getTemplate().rhand);

        // инициализация коллизий
        setCollisionHeight(getTemplate().collisionHeight);
        setCollisionRadius(getTemplate().collisionRadius);

        aggroList = new AggroList(this);

        setFlying(getParameter("isFlying", false));

        for (Stats st : Stats.values())
            addStatFunc(FuncMonsterBalancer.getInstance(st));
    }

    public static boolean canBypassCheck(Player player, NpcInstance npc) {
        if ((npc == null) || player.isActionsDisabled() || (!Config.ALLOW_TALK_WHILE_SITTING && player.isSitting()) || !npc.isInRange(player, INTERACTION_DISTANCE)) {
            player.sendActionFailed();
            return false;
        }
        return true;
    }

    public static void showCollectionSkillList(Player player) {
        showAcquireList(AcquireType.COLLECTION, player);
    }

    public static void showFishingSkillList(Player player) {
        showAcquireList(AcquireType.FISHING, player);
    }

    public static void showClanSkillList(Player player) {
        if ((player.getClan() == null) || !player.isClanLeader()) {
            player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
            player.sendActionFailed();
            return;
        }

        showAcquireList(AcquireType.CLAN, player);
    }

    private static void showAcquireList(AcquireType t, Player player) {
        final Collection<SkillLearn> skills = SkillAcquireHolder.getAvailableSkills(player, t);

        final AcquireSkillList asl = new AcquireSkillList(t, skills.size());

        for (SkillLearn s : skills) {
            asl.addSkill(s.id, s.level, s.level, s.cost);
        }

        if (skills.size() == 0) {
            player.sendPacket(AcquireSkillDone.STATIC);
            player.sendPacket(SystemMsg.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
        } else {
            player.sendPacket(asl);
        }

        player.sendActionFailed();
    }

    public static void showSubUnitSkillList(Player player) {
        Clan clan = player.getClan();
        if (clan == null) {
            return;
        }

        if ((player.getClanPrivileges() & Clan.CP_CL_TROOPS_FAME) != Clan.CP_CL_TROOPS_FAME) {
            player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        Set<SkillLearn> learns = new TreeSet<>();
        for (SubUnit sub : player.getClan().getAllSubUnits()) {
            learns.addAll(SkillAcquireHolder.getAvailableSkills(player, AcquireType.SUB_UNIT, sub));
        }

        final AcquireSkillList asl = new AcquireSkillList(AcquireType.SUB_UNIT, learns.size());

        for (SkillLearn s : learns) {
            asl.addSkill(s.id, s.level, s.level, s.cost, 1, Clan.SUBUNIT_KNIGHT4);
        }

        if (learns.size() == 0) {
            player.sendPacket(AcquireSkillDone.STATIC);
            player.sendPacket(SystemMsg.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
        } else {
            player.sendPacket(asl);
        }

        player.sendActionFailed();
    }

    public static int calculateLevelDiffForDrop(int mobLevel, int charLevel, boolean boss) {
        if (!Config.DEEPBLUE_DROP_RULES) {
            return 0;
        }
        // According to official data (Prima), deep blue mobs are 9 or more levels below players
        int deepblue_maxdiff = boss ? Config.DEEPBLUE_DROP_RAID_MAXDIFF : Config.DEEPBLUE_DROP_MAXDIFF;

        return Math.max(charLevel - mobLevel - deepblue_maxdiff, 0);
    }

    @Override
    public synchronized CharacterAI getAI() {
        if (super.getAI() == null) {
            setAI(getTemplate().getNewAI(this));
        }
        return super.getAI();
    }

    public Location getSpawnedLoc() {
        return spawnedLoc;
    }

    public NpcInstance setSpawnedLoc(Location loc) {
        spawnedLoc = loc;
        return this;
    }

    public int getRightHandItem() {
        return currentRHandId;
    }

    public int getLeftHandItem() {
        return currentLHandId;
    }

    private void setLHandId(int newWeaponId) {
        currentLHandId = newWeaponId;
    }

    public void setRHandId(int newWeaponId) {
        currentRHandId = newWeaponId;
    }

    public double getCollisionHeight() {
        return _currentCollisionHeight;
    }

    public void setCollisionHeight(double offset) {
        _currentCollisionHeight = offset;
    }

    public double getCollisionRadius() {
        return _currentCollisionRadius;
    }

    public void setCollisionRadius(double collisionRadius) {
        _currentCollisionRadius = collisionRadius;
    }

    @Override
    protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp) {
        if (attacker instanceof Playable) {
            getAggroList().addDamageHate(attacker, (int) damage, 0);
        }

        super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
    }

    @Override
    protected void onDeath(Creature killer) {
        _dieTime = System.currentTimeMillis();

        if (this instanceof MonsterInstance && (((MonsterInstance) this).isSeeded() || ((MonsterInstance) this).isSpoiled())) {
            startDecay(20000L);
        } else if (isBoss()) {
            startDecay(20000L);
        } else if (isFlying()) {
            startDecay(4500L);
        } else {
            startDecay(8500L);
        }

        // установка параметров оружия и коллизий по умолчанию
        setLHandId(getTemplate().lhand);
        setRHandId(getTemplate().rhand);
        setCollisionHeight(getTemplate().collisionHeight);
        setCollisionRadius(getTemplate().collisionRadius);

        getAI().stopAITask();
        stopRandomAnimation();

        // Alexander - Add a new mob kill to the stats
//		if (killer != null && killer.isPlayable() && MonsterInstance() && !isRaid())
//		{
//			// Only consider getPlayer with max 9 lvls of difference
//			if (killer.getPlayer().occupation() <= occupation() + 9)
//				killer.getPlayer().addPlayerStats(Ranking.STAT_TOP_MOBS_KILLS);
//		}

        super.onDeath(killer);
    }

    public long getDeadTime() {
        if (_dieTime <= 0L) {
            return 0L;
        }
        return System.currentTimeMillis() - _dieTime;
    }

    public final AggroList getAggroList() {
        return aggroList;
    }

    public MinionList getMinionList() {
        return null;
    }

    public boolean hasMinions() {
        return false;
    }

    public void dropItem(Player lastAttacker, int itemId, long itemCount) {
        if ((itemCount == 0) || (lastAttacker == null)) {
            return;
        }

        ItemInstance item;

        for (long i = 0; i < itemCount; i++) {
            item = ItemFunctions.createItem(itemId);
            if (item == null)
                continue;

            for (GlobalEvent e : getEvents()) {
                item.addEvent(e);
            }

            // Set the Item quantity dropped if L2ItemInstance is stackable
            if (item.isStackable()) {
                i = itemCount; // Set so loop won't happent again
                item.setCount(itemCount); // Set item count
            }

            if (this instanceof MonsterInstance && ((MonsterInstance) this).isChampion()) {
                if (item.isAdena()) {
                    item.setCount(itemCount * (long) Config.RATE_CHAMPION_DROP_ADENA);
                } else if (!item.isAdena()) {
                    item.setCount(itemCount * (long) Config.RATE_DROP_CHAMPION);
                }
            }

            if (isRaid() || (this instanceof ReflectionBossInstance)) {
                SystemMessage2 sm;
                if (itemId == 57) {
                    sm = new SystemMessage2(SystemMsg.C1_HAS_DIED_AND_DROPPED_S2_ADENA);
                    sm.addName(this);
                    sm.addLong(item.getCount());
                } else {
                    sm = new SystemMessage2(SystemMsg.C1_DIED_AND_DROPPED_S3_S2);
                    sm.addName(this);
                    sm.addItemName(itemId);
                    sm.addLong(item.getCount());
                }
                broadcastPacket(sm);
            }

            lastAttacker.doAutoLootOrDrop(item, this);
        }
    }

    public void dropItem(Player lastAttacker, ItemInstance item) {
        if (item.getCount() == 0) {
            return;
        }

        if (isRaid() || (this instanceof ReflectionBossInstance)) {
            SystemMessage2 sm;
            if (item.getItemId() == 57) {
                sm = new SystemMessage2(SystemMsg.C1_HAS_DIED_AND_DROPPED_S2_ADENA);
                sm.addName(this);
                sm.addLong(item.getCount());
            } else {
                sm = new SystemMessage2(SystemMsg.C1_DIED_AND_DROPPED_S3_S2);
                sm.addName(this);
                sm.addItemName(item.getItemId());
                sm.addLong(item.getCount());
            }
            broadcastPacket(sm);
        }

        lastAttacker.doAutoLootOrDrop(item, this);
    }

    @Override
    public boolean isAttackable(Creature attacker) {
        return true;
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return false;
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();

        _dieTime = 0L;
        _spawnAnimation = 0;

        if (getAI().isGlobalAI() || ((getCurrentRegion() != null) && getCurrentRegion().isActive())) {
            getAI().startAITask();
            startRandomAnimation();
        }

        ThreadPoolManager.INSTANCE.execute(new NotifyAITask(this, CtrlEvent.EVT_SPAWN));

        getListeners().onSpawn();
    }

    @Override
    protected void onDespawn() {
        getAggroList().clear();

        getAI().onEvtDeSpawn();
        getAI().stopAITask();
        getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        stopRandomAnimation();

        super.onDespawn();
    }

    @Override
    public final NpcTemplate getTemplate() {
        return (NpcTemplate) template;
    }

    @Override
    public final int getNpcId() {
        return getTemplate().npcId;
    }

    public void setUnAggred(boolean state) {
        _unAggred = state;
    }

    /**
     * Return True if the L2NpcInstance is aggressive (ex : L2MonsterInstance in function of aggroRange).<BR>
     * <BR>
     */
    public boolean isAggressive() {
        return getAggroRange() > 0;
    }

    public int getAggroRange() {
        if (_unAggred) {
            return 0;
        }

        if (_personalAggroRange >= 0) {
            return _personalAggroRange;
        }

        return getTemplate().aggroRange;
    }

    public void setAggroRange(int aggroRange) {
        _personalAggroRange = aggroRange;
    }

    public Faction getFaction() {
        return getTemplate().getFaction();
    }

    public boolean isInFaction(NpcInstance npc) {
        return getFaction().equals(npc.getFaction()) && !getFaction().isIgnoreNpcId(npc.getNpcId());
    }

    public long getExpReward() {
        return (long) calcStat(Stats.EXP, getTemplate().rewardExp);
    }

    public long getSpReward() {
        return (long) calcStat(Stats.SP, getTemplate().rewardSp);
    }


    @Override
    protected void onDelete() {
        stopDecay();
        if (spawn != null) {
            spawn.stopRespawn();
        }
        setSpawn(null);

        super.onDelete();
    }

    public Spawner getSpawn() {
        return spawn;
    }

    public void setSpawn(Spawner spawn) {
        this.spawn = spawn;
    }

    @Override
    protected void onDecay() {
        super.onDecay();

        _spawnAnimation = 2;

        if (spawn != null) {
            spawn.decreaseCount(this);
        } else {
            deleteMe(); // Если этот моб заспавнен не через стандартный механизм спавна значит посмертие ему не положено и он умирает насовсем
        }
    }

    /**
     * Запустить задачу "исчезновения" после смерти
     */
    private void startDecay(long delay) {
        stopDecay();
        DecayTaskManager.INSTANCE.addDecayTask(this, delay);
    }

    public void stopDecay() {
        DecayTaskManager.INSTANCE.cancelDecayTask(this);
    }

    /**
     * Отменить и завершить задачу "исчезновения" после смерти
     */
    public void endDecayTask() {
        stopDecay();
        doDecay();
    }

    @Override
    public boolean isUndead() {
        return getTemplate().isUndead();
    }

    @Override
    public int getLevel() {
        return level == 0 ? getTemplate().level : level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getDisplayId() {
        return displayId > 0 ? displayId : getTemplate().displayId;
    }

    public void setDisplayId(int displayId) {
        this.displayId = displayId;
    }

    @Override
    public ItemInstance getActiveWeaponInstance() {
        // regular NPCs dont have weapons instancies
        return null;
    }

    @Override
    public WeaponTemplate getActiveWeaponItem() {
        // Get the weapon identifier equipped in the right hand of the L2NpcInstance
        int weaponId = getTemplate().rhand;

        if (weaponId < 1) {
            return null;
        }

        // Get the weapon item equipped in the right hand of the L2NpcInstance
        ItemTemplate item = ItemHolder.getTemplate(getTemplate().rhand);

        if (!(item instanceof WeaponTemplate)) {
            return null;
        }

        return (WeaponTemplate) item;
    }

    @Override
    public ItemInstance getSecondaryWeaponInstance() {
        // regular NPCs dont have weapons instances
        return null;
    }

    @Override
    public WeaponTemplate getSecondaryWeaponItem() {
        // Get the weapon identifier equipped in the right hand of the L2NpcInstance
        int weaponId = getTemplate().lhand;

        if (weaponId < 1) {
            return null;
        }

        // Get the weapon item equipped in the right hand of the L2NpcInstance
        ItemTemplate item = ItemHolder.getTemplate(getTemplate().lhand);

        if (!(item instanceof WeaponTemplate)) {
            return null;
        }

        return (WeaponTemplate) item;
    }

    @Override
    public void sendChanges() {
        if (isFlying()) {
            return;
        }
        super.sendChanges();
    }

    @Override
    public void broadcastCharInfo() {
        if (!isVisible()) {
            return;
        }

        if (_broadcastCharInfoTask != null) {
            return;
        }

        _broadcastCharInfoTask = ThreadPoolManager.INSTANCE.schedule(() -> {
            broadcastCharInfoImpl();
            _broadcastCharInfoTask = null;
        }, Config.BROADCAST_CHAR_INFO_INTERVAL);
    }

    public void broadcastCharInfoImpl() {
        World.getAroundPlayers(this)
                .forEach(p -> p.sendPacket(new NpcInfo(this, p).update(1)));
    }

    // I always NPC 2
    public void onRandomAnimation() {
        if ((System.currentTimeMillis() - _lastSocialAction) > 10000L) {
            broadcastPacket(new SocialAction(objectId(), 2));
            _lastSocialAction = System.currentTimeMillis();
        }
    }

    public void startRandomAnimation() {
        if (!hasRandomAnimation()) {
            return;
        }
        _animationTask = LazyPrecisionTaskManager.getInstance().addNpcAnimationTask(this);
    }

    public void stopRandomAnimation() {
        if (_animationTask != null) {
            _animationTask.cancel(false);
            _animationTask = null;
        }
    }

    public boolean hasRandomAnimation() {
        return _hasRandomAnimation;
    }

    public boolean hasRandomWalk() {
        return _hasRandomWalk;
    }

    public Castle getCastle() {
        if ((getReflection() == ReflectionManager.PARNASSUS) && Config.SERVICES_PARNASSUS_NOTAX) {
            return null;
        }
        if (Config.SERVICES_OFFSHORE_NO_CASTLE_TAX && (getReflection() == ReflectionManager.GIRAN_HARBOR)) {
            return null;
        }
        if (Config.SERVICES_OFFSHORE_NO_CASTLE_TAX && (getReflection() == ReflectionManager.PARNASSUS)) {
            return null;
        }
        if (Config.SERVICES_OFFSHORE_NO_CASTLE_TAX && isInZone(ZoneType.offshore)) {
            return null;
        }
        if (nearestCastle == null) {
            nearestCastle = ResidenceHolder.getCastle(getTemplate().getCastleId());
        }
        return nearestCastle;
    }

    public Castle getCastle(Player player) {
        return getCastle();
    }

    public Fortress getFortress() {
        if (_nearestFortress == null) {
            _nearestFortress = ResidenceHolder.findNearestResidence(Fortress.class, getLoc(), getReflection(), 32768);
        }

        return _nearestFortress;
    }

    protected ClanHall getClanHall() {
        if (_nearestClanHall == null) {
            _nearestClanHall = ResidenceHolder.findNearestResidence(ClanHall.class, getLoc(), getReflection(), 32768);
        }

        return _nearestClanHall;
    }

    public Dominion getDominion() {
        if (getReflection() != ReflectionManager.DEFAULT) {
            return null;
        }

        if (nearestDominion == null) {
            if (getTemplate().getCastleId() == 0) {
                return null;
            }

            Castle castle = ResidenceHolder.getCastle(getTemplate().getCastleId());
            nearestDominion = castle.getDominion();
        }

        return nearestDominion;
    }

    @Override
    public void onAction(Player player, boolean shift) {
        if (!isTargetable()) {
            player.sendActionFailed();
            return;
        }

        if (player.getTarget() != this) {
            player.setTarget(this);
            if (player.getTarget() == this) {
                player.sendPacket(new MyTargetSelected(objectId(), player.getLevel() - getLevel()), makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP));
            }

            player.sendPacket(new ValidateLocation(this), ActionFail.STATIC);
            return;
        }

        if (Events.onAction(player, this, shift)) {
            player.sendActionFailed();
            return;
        }

        if (isAutoAttackable(player)) {
            player.getAI().Attack(this, false, shift);
            return;
        }

        if (!isInRangeZ(player, INTERACTION_DISTANCE)) { // Nik: Changed to isInRangeZ because players can exploit it like waking Baium from TOI 13
            if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT) {
                player.getAI().setIntentionInteract(CtrlIntention.AI_INTENTION_INTERACT, this);
            }
            return;
        }

        if (!Config.ALLOW_TALK_TO_NPCS) {
            player.sendActionFailed();
            return;
        }

        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && (player.getKarma() > 0) && !player.isGM() && !(this instanceof WarehouseInstance)) {
            player.sendActionFailed();
            return;
        }

        // С NPC нельзя разговаривать мертвым и сидя
        if ((!Config.ALLOW_TALK_WHILE_SITTING && player.isSitting()) || player.isAlikeDead()) {
            return;
        }

        if (hasRandomAnimation()) {
            onRandomAnimation();
        }

        player.sendActionFailed();
        player.stopMove(false);

        if (_isBusy) {
            showBusyWindow(player);
        } else if (isHasChatWindow()) {
            boolean flag = false;
            Set<Quest> qlst = getTemplate().getEventQuests(QuestEventType.NPC_FIRST_TALK);
            for (Quest element : qlst) {
                QuestState qs = player.getQuestState(element);
                if (((qs == null) || !qs.isCompleted()) && element.notifyFirstTalk(this, player)) {
                    flag = true;
                }
            }
            if (!flag) {
                showChatWindow(player, 0);
            }
        }
    }

    private void showQuestWindow(Player player, String questName) {
        if (!player.isQuestContinuationPossible(true)) {
            return;
        }

        int count = (int) player.getAllQuestsStates().stream()
                .filter(Objects::nonNull)
                .filter(quest -> quest.quest.isVisible())
                .filter(QuestState::isStarted)
                .filter(quest -> quest.getCond() > 0)
                .count();


        if (count > 40) {
            showChatWindow(player, "quest-limit.htm");
            return;
        }

        try {
            // Get the state of the selected quest
            QuestState qs = player.getQuestState(questName);
            if (qs != null) {
                if (qs.isCompleted()) {
                    showChatWindow(player, "completed-quest.htm");
                    return;
                }
                if (qs.quest.notifyTalk(this, qs)) {
                    return;
                }
            } else {
                Quest q = QuestManager.getQuest(questName);
                if (q != null) {
                    // check for start point
                    for (Quest element : getTemplate().getEventQuests(QuestEventType.QUEST_START)) {
                        if (element == q) {
                            qs = q.newQuestState(player, Quest.CREATED);
                            if (qs.quest.notifyTalk(this, qs)) {
                                return;
                            }
                            break;
                        }
                    }
                }
            }

            showChatWindow(player, "no-quest.htm");
        } catch (RuntimeException e) {
            _log.warn("problem with npc text(questName: " + questName + ')', e);
        }

        player.sendActionFailed();
    }

    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if ((!getTemplate().getTeleportList().isEmpty()) && checkForDominionWard(player)) {
            return;
        }

        try {
            if ("TerritoryStatus".equalsIgnoreCase(command)) {
                NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                html.setFile("merchant/territorystatus.htm");
                html.replace("%npcname%", getName());

                Castle castle = getCastle(player);
                if ((castle != null) && (castle.getId() > 0)) {
                    html.replace("%castlename%", HtmlUtils.htmlResidenceName(castle.getId()));
                    html.replace("%taxpercent%", castle.getTaxPercent());

                    if (castle.getOwnerId() > 0) {
                        Clan clan = ClanTable.INSTANCE.getClan(castle.getOwnerId());
                        if (clan != null) {
                            html.replace("%clanname%", clan.getName());
                            html.replace("%clanleadername%", clan.getLeaderName());
                        } else {
                            html.replace("%clanname%", "unexistant clan");
                            html.replace("%clanleadername%", "None");
                        }
                    } else {
                        html.replace("%clanname%", "NPC");
                        html.replace("%clanleadername%", "None");
                    }
                } else {
                    html.replace("%castlename%", "Open");
                    html.replace("%taxpercent%", 0);

                    html.replace("%clanname%", "No");
                    html.replace("%clanleadername%", getName());
                }

                player.sendPacket(html);
            } else if (command.startsWith("Quest")) {
                String quest = command.substring(5).trim();
                if (quest.isEmpty()) {
                    showQuestWindow(player);
                } else {
                    showQuestWindow(player, quest);
                }
            } else if (command.startsWith("Chat")) {
                try {
                    int val = Integer.parseInt(command.substring(5));
                    showChatWindow(player, val);
                } catch (NumberFormatException e) {
                    String filename = command.substring(5).trim();
                    if (filename.isEmpty()) {
                        showChatWindow(player, "npcdefault.htm");
                    } else {
                        showChatWindow(player, filename);
                    }
                }
            } else if (command.startsWith("AttributeCancel")) {
                player.sendPacket(new ExShowBaseAttributeCancelWindow(player));
            } else if (command.startsWith("NpcLocationInfo")) {
                int val = Integer.parseInt(command.substring(16));
                NpcInstance npc = GameObjectsStorage.getByNpcId(val);
                if (npc != null) {
                    // Убираем флажок на карте и стрелку на компасе
                    player.sendPacket(new RadarControl(2, 2, npc.getLoc()));
                    // Ставим флажок на карте и стрелку на компасе
                    player.sendPacket(new RadarControl(0, 1, npc.getLoc()));
                }
            } else if (command.startsWith("Multisell") || command.startsWith("multisell")) {
                String listId = command.substring(9).trim();
                Castle castle = getCastle(player);
                MultiSellHolder.INSTANCE.SeparateAndSend(Integer.parseInt(listId), player, castle != null ? castle.getTaxRate() : 0);
            } else if (command.startsWith("EnterRift")) {
                if (checkForDominionWard(player)) {
                    return;
                }

                StringTokenizer st = new StringTokenizer(command);
                st.nextToken(); // no need for "enterRift"

                int b1 = Integer.parseInt(st.nextToken()); // type

                DimensionalRiftManager.INSTANCE.start(player, b1, this);
            } else if (command.startsWith("ChangeRiftRoom")) {
                if (player.isInParty() && player.getParty().isInReflection() && (player.getParty().getReflection() instanceof DimensionalRift)) {
                    ((DimensionalRift) player.getParty().getReflection()).manualTeleport(player, this);
                } else {
                    DimensionalRiftManager.INSTANCE.teleportToWaitingRoom(player);
                }
            } else if (command.startsWith("ExitRift")) {
                if (player.isInParty() && player.getParty().isInReflection() && (player.getParty().getReflection() instanceof DimensionalRift)) {
                    ((DimensionalRift) player.getParty().getReflection()).manualExitRift(player, this);
                } else {
                    DimensionalRiftManager.INSTANCE.teleportToWaitingRoom(player);
                }
            } else if (command.equalsIgnoreCase("SkillList")) {
                showSkillList(player);
            } else if (command.equalsIgnoreCase("ClanSkillList")) {
                showClanSkillList(player);
            } else if (command.startsWith("SubUnitSkillList")) {
                showSubUnitSkillList(player);
            } else if (command.equalsIgnoreCase("TransformationSkillList")) {
                showTransformationSkillList(player, AcquireType.TRANSFORMATION);
            } else if (command.equalsIgnoreCase("CertificationSkillList")) {
                showTransformationSkillList(player, AcquireType.CERTIFICATION);
            } else if (command.equalsIgnoreCase("CollectionSkillList")) {
                showCollectionSkillList(player);
            } else if (command.equalsIgnoreCase("BuyTransformation")) {
                showTransformationMultisell(player);
            } else if (command.startsWith("Augment")) {
                int cmdChoice = Integer.parseInt(command.substring(8, 9).trim());
                player.setLastAugmentNpc(this);
                if (cmdChoice == 1) {
                    player.sendPacket(Msg.SELECT_THE_ITEM_TO_BE_AUGMENTED, ExShowVariationMakeWindow.STATIC);
                } else if (cmdChoice == 2) {
                    player.sendPacket(Msg.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION, ExShowVariationCancelWindow.STATIC);
                }
            } else if (command.startsWith("Link")) {
                showChatWindow(player, command.substring(5));
            } else if (command.startsWith("Teleport")) {
                int cmdChoice = Integer.parseInt(command.substring(9, 10).trim());
                List<TeleportLocation> list = getTemplate().getTeleportList(cmdChoice);
                if (list != null) {
                    showTeleportList(player, list);
                } else {
                    player.sendMessage("Link is faulty, contact an administrator.");
                }
            } else if (command.startsWith("Tele20Lvl")) {
                int cmdChoice = Integer.parseInt(command.substring(10, 11).trim());
                List<TeleportLocation> list = getTemplate().getTeleportList(cmdChoice);
                if (player.getLevel() > 20) {
                    showChatWindow(player, "teleporter/" + getNpcId() + "-no.htm");
                } else if (list != null) {
                    showTeleportList(player, list);
                } else {
                    player.sendMessage("Link is broken, contact an administrator.");
                }
            } else if (command.startsWith("open_gate")) {
                int val = Integer.parseInt(command.substring(10));
                ReflectionUtils.getDoor(val).openMe();
                player.sendActionFailed();
            } else if (command.equalsIgnoreCase("TransferSkillList")) {
                showTransferSkillList(player);
            } else if ("CertificationCancel".equalsIgnoreCase(command)) {
                CertificationFunctions.cancelCertification(player);
            } else if (command.startsWith("RemoveTransferSkill")) {
                AcquireType type = AcquireType.transferType(player.getActiveClassId());
                if (type == null) {
                    return;
                }

                Collection<SkillLearn> skills = SkillAcquireHolder.getAvailableSkills(null, type);
                if (skills.isEmpty()) {
                    player.sendActionFailed();
                    return;
                }

                boolean reset = skills.stream()
                        .anyMatch(skill -> player.getKnownSkill(skill.id) != null);

                if (!reset) {
                    player.sendActionFailed();
                    return;
                }

                if (!player.reduceAdena(10000000L, true, "RemoveTransferSkill")) {
                    showChatWindow(player, "common/skill_share_healer_no_adena.htm");
                    return;
                }

                for (SkillLearn skill : skills) {
                    if (player.removeSkill(skill.id, true) != null) {
                        ItemFunctions.addItem(player, skill.itemId, skill.itemCount, "RemoveTransferSkill");
                    }
                }
            } else if (command.startsWith("ExitFromQuestInstance")) {
                Reflection r = player.getReflection();
                r.startCollapseTimer(60000);
                player.teleToLocation(r.getReturnLoc(), 0);
                if (command.length() > 22) {
                    try {
                        int val = Integer.parseInt(command.substring(22));
                        showChatWindow(player, val);
                    } catch (NumberFormatException nfe) {
                        String filename = command.substring(22).trim();
                        if (filename.length() > 0) {
                            showChatWindow(player, filename);
                        }
                    }
                }
            }
        } catch (StringIndexOutOfBoundsException sioobe) {
            _log.info("Incorrect htm bypass! npcId=" + getTemplate().npcId + " command=[" + command + "]");
        } catch (NumberFormatException nfe) {
            _log.info("Invalid bypass to Server command parameter! npcId=" + getTemplate().npcId + " command=[" + command + "]");
        }
    }

    private void showTeleportList(Player player, List<TeleportLocation> list) {
        StringBuilder sb = new StringBuilder();

        sb.append("&$556;").append("<br><br>");

        if ((list != null) && player.getPlayerAccess().UseTeleport) {
            for (TeleportLocation tl : list) {
                if (tl.getItem().itemId() == ItemTemplate.ITEM_ID_ADENA) {
                    double pricemod = player.getLevel() <= Config.GATEKEEPER_FREE ? 0. : Config.GATEKEEPER_MODIFIER;
                    if ((tl.getPrice() > 0) && (pricemod > 0)) {
                        // On Saturdays and Sundays from 8 PM to 12 AM, gatekeeper teleport fees decrease by 50%.
                        Calendar calendar = Calendar.getInstance();
                        int day = calendar.get(Calendar.DAY_OF_WEEK);
                        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                        if (((day == Calendar.SUNDAY) || (day == Calendar.SATURDAY)) && ((hour >= 20) && (hour <= 12))) {
                            pricemod /= 2;
                        }
                    }
                    sb.append("[scripts_Util:Gatekeeper ").append(tl.getX()).append(" ").append(tl.getY()).append(" ").append(tl.getZ());
                    if (tl.getCastleId() != 0) {
                        sb.append(" ").append(tl.getCastleId());
                    }
                    sb.append(" ").append((long) (tl.getPrice() * pricemod)).append(" @811;F;").append(tl.getName()).append("|").append(tl.getStringName());
                    // sb.append(" ").append((long) (tl.price() * pricemod)).append(" @811;F;").append(tl.name()).append("|").append(HtmlUtils.htmlNpcString(tl.name()));
                    if ((tl.getPrice() * pricemod) > 0) {
                        sb.append(" - ").append((long) (tl.getPrice() * pricemod)).append(" ").append(HtmlUtils.htmlItemName(ItemTemplate.ITEM_ID_ADENA));
                    }
                    sb.append("]<br1>\n");
                } else {
                    sb.append("[scripts_Util:QuestGatekeeper ").append(tl.getX()).append(" ").append(tl.getY()).append(" ").append(tl.getZ()).append(" ").append(tl.getPrice()).append(" ").append(tl.getItem().itemId()).append(" @811;F;").append("|").append(tl.getStringName()).append(" - ").append(tl.getPrice()).append(" ").append(HtmlUtils.htmlItemName(tl.getItem().itemId())).append("]<br1>\n");
                }
            }
        } else {
            sb.append("No teleports available for you.");
        }

        NpcHtmlMessage html = new NpcHtmlMessage(player, this);
        html.setHtml(Strings.bbParse(sb.toString()));
        player.sendPacket(html);
    }

    private void showQuestWindow(Player player) {
        // collect awaiting quests and start points
        Set<Quest> options = player.getQuestsForEvent(this, QuestEventType.QUEST_TALK)
                .map(qs -> qs.quest)
                .collect(Collectors.toSet());


        options.addAll(getTemplate().getEventQuests(QuestEventType.QUEST_START).stream()
                .filter(x -> x.id > 0)
                .collect(Collectors.toSet()));


        // Display a QuestChooseWindow (if several quests are available) or QuestWindow
        if (options.size() > 1) {
            showQuestChooseWindow(player, options);
        } else {
            showQuestWindow(player, "");
        }
    }

    private void showQuestChooseWindow(Player player, Set<Quest> quests) {
        StringBuilder sb = new StringBuilder();

        sb.append("<html><body><title>Talk about:</title><br>");

        for (Quest q : quests) {
            if (q.isVisible()) {
                sb.append("<a action=\"bypass -h npc_")
                        .append(objectId())
                        .append("_Quest ")
                        .append(q.name)
                        .append("\">[")
                        .append(q.getDescr(player))
                        .append("]</a><br>");
            }
        }

        sb.append("</body></html>");

        NpcHtmlMessage html = new NpcHtmlMessage(player, this);
        html.setHtml(sb.toString());
        player.sendPacket(html);
    }

    public void showChatWindow(Player player, int val) {
        if ((getTemplate().getTeleportList().size() > 0) && checkForDominionWard(player)) {
            return;
        }

        if (!_showBoard.isEmpty()) {
            ICommunityBoardHandler handler = CommunityBoardManager.getCommunityHandler(_showBoard);
            if (handler != null)
                handler.onBypassCommand(player, _showBoard);
            return;
        }

        String filename = SevenSigns.SEVEN_SIGNS_HTML_PATH;
        int npcId = getNpcId();
        switch (npcId) {
            case 31111: // Gatekeeper Spirit (Disciples)
                int sealAvariceOwner = SevenSigns.INSTANCE.getSealOwner(SevenSigns.SEAL_AVARICE);
                int playerCabal = SevenSigns.INSTANCE.getPlayerCabal(player);
                int compWinner = SevenSigns.INSTANCE.getCabalHighestScore();
                if ((playerCabal == sealAvariceOwner) && (playerCabal == compWinner)) {
                    switch (sealAvariceOwner) {
                        case SevenSigns.CABAL_DAWN:
                            filename += "spirit_dawn.htm";
                            break;
                        case SevenSigns.CABAL_DUSK:
                            filename += "spirit_dusk.htm";
                            break;
                        case SevenSigns.CABAL_NULL:
                            filename += "spirit_null.htm";
                            break;
                    }
                } else {
                    filename += "spirit_null.htm";
                }
                break;
            case 31112: // Gatekeeper Spirit (Disciples)
                filename += "spirit_exit.htm";
                break;
            case 30298:
                if (player.getPledgeType() == Clan.SUBUNIT_ACADEMY) {
                    filename = getHtmlPath(npcId, 1, player);
                } else {
                    filename = getHtmlPath(npcId, 0, player);
                }
                break;
            default:
                if (((npcId >= 31093) && (npcId <= 31094)) || ((npcId >= 31172) && (npcId <= 31201)) || ((npcId >= 31239) && (npcId <= 31254))) {
                    return;
                }
                // Get the text of the selected HTML file in function of the npcId and of the page number
                filename = getHtmlPath(npcId, val, player);
                break;
        }

        NpcHtmlMessage packet = new NpcHtmlMessage(player, this, filename, val);
//        if ((replace.length % 2) == 0) {
//            for (int i = 0; i < replace.length; i += 2) {
//                packet.replace(String.valueOf(replace[i]), String.valueOf(replace[i + 1]));
//            }
//        }
        player.sendPacket(packet);
    }

    public void showChatWindow(Player player, String filename) {
        showChatWindow(player, filename, Map.of());
    }

    public void showChatWindow(Player player, String filename, Map<String, String> replaces) {
        NpcHtmlMessage packet = new NpcHtmlMessage(player, this, filename, 0);
        replaces.forEach(packet::replace);
        player.sendPacket(packet);
    }

    public String getHtmlPath(int npcId, int val, Player player) {
        String pom;
        if (val == 0) {
            pom = "" + npcId;
        } else {
            pom = npcId + "-" + val;
        }

        if (getTemplate().getHtmRoot() != null) {
            return getTemplate().getHtmRoot() + pom + ".htm";
        }

        String temp = "default/" + pom + ".htm";
        if (HtmCache.INSTANCE.getNullable(temp) != null) {
            return temp;
        }

        temp = "trainer/" + pom + ".htm";
        if (HtmCache.INSTANCE.getNullable(temp) != null) {
            return temp;
        }

        // If the file is not found, the standard message "I have nothing to say to you" is returned
        return "npcdefault.htm";
    }

    public final boolean isBusy() {
        return _isBusy;
    }

    public void setBusy(boolean isBusy) {
        _isBusy = isBusy;
    }

    public final String getBusyMessage() {
        return _busyMessage;
    }

    public void setBusyMessage(String message) {
        _busyMessage = message;
    }

    private void showBusyWindow(Player player) {
        NpcHtmlMessage html = new NpcHtmlMessage(player, this);
        html.setFile("npcbusy.htm");
        html.replace("%npcname%", getName());
        html.replace("%playername%", player.getName());
        html.replace("%busymessage%", _busyMessage);
        player.sendPacket(html);
    }

    public void showSkillList(Player player) {
        ClassId classId = player.getClassId();

        if (classId == null) {
            return;
        }

        int npcId = getTemplate().npcId;

        if (getTemplate().getTeachInfo().isEmpty()) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            String sb = "<html><head><body>" +
                    "I cannot teach you. My class list is empty.<br> Ask admin to fix it. <br>NpcId:" + npcId + ", Your classId:" + player.getClassId().id + "<br>" +
                    "</body></html>";
            html.setHtml(sb);
            player.sendPacket(html);

            return;
        }

        if (!(getTemplate().canTeach(classId) || getTemplate().canTeach(classId.parent))) {
            if (this instanceof WarehouseInstance) {
                showChatWindow(player, "warehouse/" + getNpcId() + "-noteach.htm");
            } else if (this instanceof TrainerInstance) {
                showChatWindow(player, "trainer/" + getNpcId() + "-noteach.htm");
            } else {
                NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                String sb = "<html><head><body>" +
                        new CustomMessage("l2trunk.gameserver.model.instances.L2NpcInstance.WrongTeacherClass") +
                        "</body></html>";
                html.setHtml(sb);
                player.sendPacket(html);
            }
            return;
        }

        final Collection<SkillLearn> skills = SkillAcquireHolder.getAvailableSkills(player, AcquireType.NORMAL);

        final AcquireSkillList asl = new AcquireSkillList(AcquireType.NORMAL, skills.size());
        int counts = 0;

        for (SkillLearn s : skills) {
            if (!s.isClicked) {
                Skill sk = SkillTable.INSTANCE.getInfo(s.id, s.level);
                if ((sk != null) && !sk.cantLearn(player.getClassId()) && sk.canTeachBy(npcId)) {
                    counts++;
                    asl.addSkill(s.id, s.level, s.level, s.cost);
                }

            }

        }

        if (counts == 0) {
            int minlevel = SkillAcquireHolder.getMinLevelForNewSkill(player, AcquireType.NORMAL);

            if (minlevel > 0) {
                SystemMessage2 sm = new SystemMessage2(SystemMsg.YOU_DO_NOT_HAVE_ANY_FURTHER_SKILLS_TO_LEARN__COME_BACK_WHEN_YOU_HAVE_REACHED_LEVEL_S1);
                sm.addInteger(minlevel);
                player.sendPacket(sm);
            } else {
                player.sendPacket(SystemMsg.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
            }
            player.sendPacket(AcquireSkillDone.STATIC);
        } else {
            player.sendPacket(asl);
        }

        player.sendActionFailed();
    }

    public void showTransferSkillList(Player player) {
        ClassId classId = player.getClassId();
        if (classId == null) {
            return;
        }

        if ((player.getLevel() < 76) || (classId.occupation() < 3)) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            String sb = "<html><head><body>" +
                    "You must have 3rd class change quest completed." +
                    "</body></html>";
            html.setHtml(sb);
            player.sendPacket(html);
            return;
        }

        AcquireType type = AcquireType.transferType(player.getActiveClassId());
        if (type == null) {
            return;
        }

        showAcquireList(type, player);
    }

    private void showTransformationMultisell(Player player) {
        if (!Config.ALLOW_LEARN_TRANS_SKILLS_WO_QUEST) {
            if (!player.isQuestCompleted(_136_MoreThanMeetsTheEye.class)) {
                showChatWindow(player, "trainer/" + getNpcId() + "-nobuy.htm");
            }
        }

        Castle castle = getCastle(player);
        MultiSellHolder.INSTANCE.SeparateAndSend(32323, player, castle != null ? castle.getTaxRate() : 0);
        player.sendActionFailed();
    }

    public void showTransformationSkillList(Player player, AcquireType type) {
        if (!Config.ALLOW_LEARN_TRANS_SKILLS_WO_QUEST) {
            if (!player.isQuestCompleted(_136_MoreThanMeetsTheEye.class)) {
                showChatWindow(player, "trainer/" + getNpcId() + "-noquest.htm");
                return;
            }
        }

        showAcquireList(type, player);
    }

    /**
     * Нужно для отображения анимации спауна, используется в пакете NpcInfo: 0=false, 1=true, 2=summoned (only works if model has a summon animation)
     **/
    public int getSpawnAnimation() {
        return _spawnAnimation;
    }

    @Override
    public double getColRadius() {
        return getCollisionRadius();
    }

    @Override
    public double getColHeight() {
        return getCollisionHeight();
    }

    public int calculateLevelDiffForDrop(int charLevel) {
        return calculateLevelDiffForDrop(getLevel(), charLevel, this instanceof RaidBossInstance);
    }

    public boolean isSevenSignsMonster() {
        return getFaction().getName().equalsIgnoreCase("c_dungeon_clan");
    }

    @Override
    public String toString() {
        return getNpcId() + " " + getName();
    }

    public void refreshID() {
        objectId = IdFactory.getInstance().getNextId();
        storedId = objectId;
    }

    public boolean isUnderground() {
        return isUnderground;
    }

    public void setUnderground(boolean b) {
        isUnderground = b;
    }

    protected boolean isTargetable() {
        return _isTargetable;
    }

    public void setTargetable(boolean value) {
        _isTargetable = value;
    }

    public boolean isShowName() {
        return _showName;
    }

    protected void setShowName(boolean value) {
        _showName = value;
    }

    @Override
    public NpcListenerList getListeners() {
        if (listeners == null) {
            synchronized (this) {
                if (listeners == null) {
                    listeners = new NpcListenerList(this);
                }
            }
        }

        return (NpcListenerList) listeners;
    }

    public <T extends NpcListener> boolean addListener(T listener) {
        return getListeners().add(listener);
    }

    public <T extends NpcListener> boolean removeListener(T listener) {
        return getListeners().remove(listener);
    }

    @Override
    public NpcStatsChangeRecorder getStatsRecorder() {
        if (statsRecorder == null) {
            synchronized (this) {
                if (statsRecorder == null) {
                    statsRecorder = new NpcStatsChangeRecorder(this);
                }
            }
        }

        return (NpcStatsChangeRecorder) statsRecorder;
    }

    public int getNpcState() {
        return npcState;
    }

    public void setNpcState(int stateId) {
        broadcastPacket(new ExChangeNpcState(objectId(), stateId));
        npcState = stateId;
    }

    @Override
    public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
        List<L2GameServerPacket> list = new ArrayList<>(3);
        list.add(new NpcInfo(this, forPlayer));

        if (isInCombat()) {
            list.add(new AutoAttackStart(objectId()));
        }

        if (isMoving || isFollow) {
            list.add(movePacket());
        }

        return list;
    }

    @Override
    public int getGeoZ(Location loc) {
        if (isFlying() || isInWater() || isInBoat()) return loc.z;
        if (_spawnRange instanceof Territory) return GeoEngine.getHeight(loc, getGeoIndex());
        return loc.z;

    }


    public Clan getClan() {
        Dominion dominion = getDominion();
        if (dominion == null) {
            return null;
        }
        int lordObjectId = dominion.getLordObjectId();
        return lordObjectId == 0 ? null : dominion.getOwner();
    }

    public NpcString getNameNpcString() {
        return nameNpcString;
    }

    public void setNameNpcString(NpcString nameNpcString) {
        this.nameNpcString = nameNpcString;
    }

    public NpcString getTitleNpcString() {
        return _titleNpcString;
    }

    public boolean isMerchantNpc() {
        return false;
    }

    public SpawnRange getSpawnRange() {
        return _spawnRange;
    }

    public void setSpawnRange(SpawnRange spawnRange) {
        _spawnRange = spawnRange;
    }

    protected boolean checkForDominionWard(Player player) {
        ItemInstance item = getActiveWeaponInstance();
        if ((item != null) && (item.getAttachment() instanceof TerritoryWardObject)) {
            showChatWindow(player, "flagman.htm");
            return true;
        }
        return false;
    }

    public void setParameter(String str, boolean val) {
        setParameter(str, "" + val);
    }

    public void setParameter(String str, double val) {
        setParameter(str, "" + val);
    }

    public void setParameter(String str, String val) {
        if (parameters == StatsSet.EMPTY) {
            parameters = new StatsSet();
        }

        parameters.set(str, val);
    }

    public int getParameter(String str, int val) {
        return parameters.getInteger(str, val);
    }

    public long getParameter(String str) {
        return parameters.getLong(str);
    }

    public boolean getParameter(String str, boolean val) {
        return parameters.getBool(str, val);
    }

    public String getParameter(String str, String val) {
        return parameters.getString(str, val);
    }

    public StatsSet getParameters() {
        return parameters;
    }

    public void setParameters(StatsSet set) {
        if (set.isEmpty()) return;

        if (parameters == StatsSet.EMPTY) {
            parameters = new StatsSet(set);
        }
    }

    @Override
    public boolean isInvul() {
        return true;
    }

    private boolean isHasChatWindow() {
        return _hasChatWindow;
    }

    public void setHasChatWindow(boolean hasChatWindow) {
        _hasChatWindow = hasChatWindow;
    }

    public int getAISpawnParam() {
        return aiSpawnParam;
    }

}