package l2trunk.gameserver.model;

import Elemental.datatables.OfflineBuffersTable;
import Elemental.managers.GmEventManager;
import Elemental.managers.OfflineBufferManager;
import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.commons.lang.Pair;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.GameTimeController;
import l2trunk.gameserver.PartyMatchingBBSManager;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.ai.PlayableAI.nextAction;
import l2trunk.gameserver.ai.PlayerAI;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.dao.CharacterDAO;
import l2trunk.gameserver.dao.CharacterGroupReuseDAO;
import l2trunk.gameserver.dao.CharacterPostFriendDAO;
import l2trunk.gameserver.dao.EffectsDAO;
import l2trunk.gameserver.data.xml.holder.*;
import l2trunk.gameserver.data.xml.holder.MultiSellHolder.MultiSellListContainer;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.database.mysql;
import l2trunk.gameserver.handler.bbs.CommunityBoardManager;
import l2trunk.gameserver.handler.bbs.ICommunityBoardHandler;
import l2trunk.gameserver.handler.items.IItemHandler;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.instancemanager.*;
import l2trunk.gameserver.instancemanager.BypassManager.BypassType;
import l2trunk.gameserver.instancemanager.BypassManager.DecodedBypass;
import l2trunk.gameserver.instancemanager.games.HandysBlockCheckerManager;
import l2trunk.gameserver.instancemanager.games.HandysBlockCheckerManager.ArenaParticipantsHolder;
import l2trunk.gameserver.listener.actor.player.OnAnswerListener;
import l2trunk.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2trunk.gameserver.listener.actor.player.impl.SummonAnswerListener;
import l2trunk.gameserver.model.GameObjectTasks.*;
import l2trunk.gameserver.model.Request.L2RequestType;
import l2trunk.gameserver.model.Skill.AddedSkill;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.actor.instances.player.*;
import l2trunk.gameserver.model.actor.instances.player.FriendList;
import l2trunk.gameserver.model.actor.listener.PlayerListenerList;
import l2trunk.gameserver.model.actor.recorder.PlayerStatsChangeRecorder;
import l2trunk.gameserver.model.base.*;
import l2trunk.gameserver.model.entity.CCPHelpers.itemLogs.ItemActionType;
import l2trunk.gameserver.model.entity.CCPHelpers.itemLogs.ItemLogHandler;
import l2trunk.gameserver.model.entity.DimensionalRift;
import l2trunk.gameserver.model.entity.Hero;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.SevenSignsFestival.DarknessFestival;
import l2trunk.gameserver.model.entity.achievements.Achievement;
import l2trunk.gameserver.model.entity.achievements.Achievements;
import l2trunk.gameserver.model.entity.achievements.PlayerCounters;
import l2trunk.gameserver.model.entity.auction.Auction;
import l2trunk.gameserver.model.entity.auction.AuctionManager;
import l2trunk.gameserver.model.entity.boat.Boat;
import l2trunk.gameserver.model.entity.boat.ClanAirShip;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.entity.events.impl.*;
import l2trunk.gameserver.model.entity.olympiad.CompType;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.model.entity.olympiad.OlympiadGame;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.model.entity.residence.Fortress;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.model.instances.*;
import l2trunk.gameserver.model.items.*;
import l2trunk.gameserver.model.items.Warehouse.WarehouseType;
import l2trunk.gameserver.model.items.attachment.FlagItemAttachment;
import l2trunk.gameserver.model.items.attachment.PickableAttachment;
import l2trunk.gameserver.model.matching.MatchingRoom;
import l2trunk.gameserver.model.pledge.*;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestEventType;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.GameClient;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;
import l2trunk.gameserver.network.serverpackets.components.SceneMovie;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Events;
import l2trunk.gameserver.skills.AbnormalEffect;
import l2trunk.gameserver.skills.EffectType;
import l2trunk.gameserver.skills.TimeStamp;
import l2trunk.gameserver.skills.effects.EffectCubic;
import l2trunk.gameserver.skills.effects.EffectTemplate;
import l2trunk.gameserver.skills.skillclasses.Charge;
import l2trunk.gameserver.skills.skillclasses.Transformation;
import l2trunk.gameserver.stats.Formulas;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.Func;
import l2trunk.gameserver.stats.funcs.FuncTemplate;
import l2trunk.gameserver.tables.ClanTable;
import l2trunk.gameserver.tables.PetDataTable;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.tables.SkillTreeTable;
import l2trunk.gameserver.taskmanager.AutoRechargeManager;
import l2trunk.gameserver.taskmanager.AutoSaveManager;
import l2trunk.gameserver.taskmanager.CancelTaskManager;
import l2trunk.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2trunk.gameserver.templates.FishTemplate;
import l2trunk.gameserver.templates.Henna;
import l2trunk.gameserver.templates.InstantZone;
import l2trunk.gameserver.templates.PlayerTemplate;
import l2trunk.gameserver.templates.item.ArmorTemplate;
import l2trunk.gameserver.templates.item.ArmorTemplate.ArmorType;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.templates.item.WeaponTemplate;
import l2trunk.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.*;
import l2trunk.scripts.quests._255_Tutorial;
import l2trunk.scripts.quests._350_EnhanceYourWeapon;
import l2trunk.scripts.quests._422_RepentYourSins;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static l2trunk.commons.lang.NumberUtils.*;
import static l2trunk.gameserver.model.base.ClassId.*;
import static l2trunk.gameserver.model.quest.Quest.ADENA_ID;
import static l2trunk.gameserver.network.serverpackets.ExSetCompassZoneCode.*;
import static l2trunk.scripts.quests._605_AllianceWithKetraOrcs.KETRA_MARKS;
import static l2trunk.scripts.quests._611_AllianceWithVarkaSilenos.VARKA_MARKS;

public final class Player extends Playable implements PlayerGroup {
    public static final int DEFAULT_TITLE_COLOR = 0xFFFF77;
    public static final int MAX_POST_FRIEND_SIZE = 100;
    public static final int MAX_FRIEND_SIZE = 128;
    public static final String NO_TRADERS_VAR = "notraders";
    public static final String NO_ANIMATION_OF_CAST_VAR = "notShowBuffAnim";
    public final static int OBSERVER_STARTING = 1;
    public final static int OBSERVER_STARTED = 3;
    public final static int OBSERVER_LEAVING = 2;
    public static final int STORE_PRIVATE_NONE = 0;
    public static final int STORE_PRIVATE_SELL = 1;
    public static final int STORE_PRIVATE_BUY = 3;
    public static final int STORE_PRIVATE_MANUFACTURE = 5;
    public static final int STORE_OBSERVING_GAMES = 7;
    public static final int STORE_PRIVATE_SELL_PACKAGE = 8;
    public static final int STORE_PRIVATE_BUFF = 20;
    public static final int RANK_KNIGHT = 3;
    public final static int autoMp = 728;
    public final static int autoCp = 5592;
    public final static int autoHp = 1539;
    static final int RANK_BARON = 5;
    private static final int RANK_WISEMAN = 4;
    private final static int OBSERVER_NONE = 0;
    private static final int RANK_VAGABOND = 0;
    private static final int RANK_VASSAL = 1;
    private static final int RANK_HEIR = 2;
    private static final int RANK_VISCOUNT = 6;
    private static final int RANK_COUNT = 7;
    private static final int RANK_MARQUIS = 8;
    private static final int RANK_DUKE = 9;
    private static final int RANK_GRAND_DUKE = 10;
    private static final int RANK_DISTINGUISHED_KING = 11;
    private static final List<Integer> EXPERTISE_LEVELS = List.of(
            0, 20, 40, 52, 61, 76, 80, 84, Integer.MAX_VALUE);
    private static final Logger LOG = LoggerFactory.getLogger(Player.class);
    private static final Map<ClassId, Integer> transformSkills = Map.of(
            cardinal, 24001,
            hierophant, 24002,
            swordMuse, 24003,
            evaSaint, 24004,
            spectralDancer, 24005,
            shillienSaint, 24006,
            dominator, 24007,
            doomcryer, 24008);
    public final BookMarkList bookmarks = new BookMarkList(this, 0);
    public final AntiFlood antiFlood = new AntiFlood();
    public final PcInventory inventory = new PcInventory(this);
    final Map<Integer, Skill> transformationSkills = new HashMap<>();
    private final Warehouse warehouse = new PcWarehouse(objectId);
    private final ItemContainer refund = new PcRefund(this);
    private final PcFreight freight = new PcFreight(objectId);
    /**
     * The table containing all l2fecipeList of the L2Player
     */
    private final Map<Integer, Recipe> recipebook = new TreeMap<>();
    private final Map<Integer, Recipe> commonrecipebook = new TreeMap<>();
    private final Map<String, String> quickVars = new ConcurrentHashMap<>();
    private final List<Integer> loadedImages = new ArrayList<>();

    private final Map<Integer, PremiumItem> premiumItems = new TreeMap<>();

    private final Set<QuestState> quests = new CopyOnWriteArraySet<>();

    private final ShortCutList shortCuts = new ShortCutList(this);


    private final MacroList _macroses = new MacroList(this);

    private final List<Henna> henna = new ArrayList<>(3);
    private final AtomicBoolean _isLogout = new AtomicBoolean();
    private final Set<Integer> activeSoulShots = new CopyOnWriteArraySet<>();
    private final AtomicInteger observerMode = new AtomicInteger(0);
    private final Map<Integer, String> blockList = new ConcurrentSkipListMap<>(); // characters blocked with '/setBlock <charname>' cmd
    private final FriendList friendList = new FriendList(this);
    private final Fishing fishing = new Fishing(this);
    private final Lock storeLock = new ReentrantLock();
    private final List<String> blockedActions = new ArrayList<>();
    private final List<SchemeBufferInstance.PlayerScheme> buffSchemes;
    private final Map<Integer, TimeStamp> sharedGroupReuses = new HashMap<>();
    // High Five: Navit's Bonus System
    private final NevitSystem _nevitSystem = new NevitSystem(this);
    private final Map<Integer, Long> _instancesReuses = new ConcurrentHashMap<>();
    private final Map<String, PlayerVar> user_variables = new ConcurrentHashMap<>();
    private final List<Player> snoopListener = new ArrayList<>();
    private final List<Player> snoopedPlayer = new ArrayList<>();
    private final Map<Integer, TamedBeastInstance> tamedBeasts = new ConcurrentHashMap<>();
    private final AtomicBoolean isActive = new AtomicBoolean();
    private final Map<Integer, Integer> _achievementLevels = new HashMap<>();
    private final Map<ClassId, SubClass> classlist = new HashMap<>(4);
    /**
     * new loto ticket *
     */
    private final int[] _loto = new int[5];
    /**
     * The current higher Expertise of the L2Player (None=0, D=1, C=2, B=3, A=4, S=5, S80=6, S84=7)
     */
    public int expertiseIndex = 0;
    public boolean _autoMp;
    public boolean _autoCp;
    public boolean _autoHp;
    public boolean entering = true;
    public Location stablePoint = null;
    boolean sittingTaskLaunched;
    private int incorrectValidateCount = 0;
    private int _telemode = 0;
    /**
     * ----------------- Hit Man System -------------------
     */
    private int _ordered;
    private ClassId baseClass;
    private SubClass activeClass = null;
    /**
     * 0=White, 1=Purple, 2=PurpleBlink
     */
    private int pvpFlag;
    private GameClient connection;
    private String login;
    private int karma, _pkKills, pvpKills;
    private int face, hairStyle, hairColor;
    private int _recomHave, recomLeftToday, fame;
    private int _recomLeft = 20;
    private int recomBonusTime = 3600;
    private boolean _isHourglassEffected, isRecomTimerActive;
    private boolean _isUndying = false;
    private int _deleteTimer;
    private NpcInstance lastAugmentNpc = null;
    private int ping = -1;
    private long _createTime,
            _onlineTime,
            _onlineBeginTime,
            _leaveClanTime,
            _deleteClanTime,
            _NoChannel,
            _NoChannelBegin;
    private long _uptime;
    /**
     * Time on login in game
     */
    private long _lastAccess;
    /**
     * The Color of players name / title (white is 0xFFFFFF)
     */
    private int _nameColor, _titlecolor;
    private int _vitalityLevel = -1;
    private double vitality = Config.VITALITY_LEVELS.get(4);
    private boolean overloaded;
    /**
     * Time counter when L2Player is sitting
     */
    private int _waitTimeWhenSit;
    private Warehouse _withdrawWarehouse = null; // Used for GMs withdrawing from CWH or other getPlayer warehouses
    /**
     * The Private Store type of the L2Player (STORE_PRIVATE_NONE=0, STORE_PRIVATE_SELL=1, sellmanage=2, STORE_PRIVATE_BUY=3, buymanage=4, STORE_PRIVATE_MANUFACTURE=5)
     */
    private int privatestore;
    private String manufactureName;
    private List<ManufactureItem> createList = List.of();
    private String _sellStoreName;
    private List<TradeItem> sellList = List.of();
    private List<TradeItem> packageSellList = List.of();
    private String _buyStoreName;
    private List<TradeItem> _buyList = List.of();
    private List<TradeItem> _tradeList = List.of();
    private int hennaSTR, hennaINT, hennaDEX, hennaMEN, hennaWIT, hennaCON;
    private Party party;
    private Location _lastPartyPosition;
    private Clan clan;
    private int _pledgeClass = 0, pledgeType = Clan.SUBUNIT_NONE, _powerGrade = 0, lvlJoinedAcademy = 0, apprentice = 0;
    /**
     * GM Stuff
     */
    private int _accessLevel;
    private PlayerAccess playerAccess = new PlayerAccess();
    private boolean _messageRefusal = false, tradeRefusal = false, _partyinviteRefusal = false, _friendinviteRefusal = false, blockAll = false;
    private boolean isPendingOlyEnd = false;
    private Skill macroSkill = null;
    /**
     * The L2Summon of the L2Player
     */
    private Summon summon = null;
    private boolean riding;
    private DecoyInstance _decoy = null;
    private Map<Integer, EffectCubic> cubics = null;
    private int _agathionId = 0;
    private Request _request;
    private ItemInstance _arrowItem;
    /**
     * The fists L2Weapon of the L2Player (used when no weapon is equipped)
     */
    private WeaponTemplate _fistsWeaponItem;
    private Map<Integer, String> _chars = new HashMap<>(8);
    private ItemInstance enchantScroll = null;
    private WarehouseType _usingWHType;
    private boolean isOnline = false;
    /**
     * The L2NpcInstance corresponding to the last Folk which one the getPlayer talked.
     */
    private NpcInstance lastNpc = null;
    private MultiSellListContainer _multisell = null;
    private WorldRegion _observerRegion;
    private int _handysBlockCheckerEventArena = -1;
    private boolean hero = false;
    /**
     * True if the L2Player is in a boat
     */
    private Boat boat;
    private Location _inBoatPosition;
    private boolean isSitting;
    private StaticObjectInstance sittingObject;
    private boolean noble = false;
    private boolean inOlympiadMode;
    private boolean _isOlympiadCompStarted = false;
    private OlympiadGame olympiadGame;
    private OlympiadGame olympiadObserveGame;
    private int _olympiadSide = -1;
    /**
     * ally with ketra or varka related wars
     */
    private int varka = 0;
    private int ketra = 0;
    private int ram = 0;
    private byte[] _keyBindings = new byte[0];
    private int cursedWeaponEquippedId = 0;
    private boolean isFishing;
    private Future<?> _taskWater;
    private Future<?> _autoSaveTask;
    private Future<?> _autoChargeTask;
    private Future<?> _kickTask;
    private Future<?> _vitalityTask;
    private Future<?> _pcCafePointsTask;
    private int _zoneMask;
    private int transformationId;
    private int transformationTemplate;
    private String transformationName;
    private int _pcBangPoints;
    private int _expandInventory = 0;
    private int _expandWarehouse = 0;
    private int battlefieldChatId;
    private int lectureMark;

    private InvisibleType _invisibleType = InvisibleType.NONE;

    private List<String> bypasses = null, bypasses_bbs = null;
    private Map<Integer, String> postFriends = new HashMap<>();
    private boolean _notShowBuffAnim = false;
    private boolean debug = false;
    private long _dropDisabled;
    private long _lastItemAuctionInfoRequest;
    private Pair<Integer, OnAnswerListener> _askDialog = null;
    private MatchingRoom _matchingRoom;
    // Ady
    private long _resurrectionMaxTime = 0;
    private long _resurrectionBuffBlockedTime = 0;
    private ScheduledFuture<?> recomBonusTask;
    private Future<?> updateEffectIconsTask;
    private boolean _isVitalityStop = false;
    private ScheduledFuture<?> broadcastCharInfoTask;
    private int polyNpcId;
    private Future<?> _userInfoTask;
    private Creature lastAttacker = null;
    private long lastAttackDate = 0L;
    private int raids;
    private int mountNpcId;
    private int _mountObjId;
    private int _mountLevel;
    private boolean _partyMatchingVisible = true;
    private boolean _charmOfCourage = false;
    private int increasedForce = 0;
    private int consumedSouls = 0;
    private long _lastFalling;
    private Location _lastClientPosition;
    private Location _lastServerPosition;

    // ------------------- Quest Engine ----------------------
    private int _useSeed = 0;
    private ScheduledFuture<?> _PvPRegTask;
    private long _lastPvpAttack;
    private long _lastAttackPacket = 0;
    private long _lastMovePacket = 0;
    private Location _groundSkillLoc;
    private int buyListId;
    private int movieId = 0;
    private boolean isInMovie;
    private ItemInstance _petControlItem = null;
    private Map<Integer, Integer> traps;
    private Future<?> _hourlyTask;

    // ----------------- End of Quest Engine -------------------
    private int _hoursInGame = 0;
    private boolean _agathionResAvailable = false;
    private Map<String, String> _userSession;
    private boolean is_bbs_use = false;
    // Ady - Support for visible non permanent colors
    private int _visibleNameColor = 0;
    private int _visibleTitleColor = 0;
    // Support for visible non permanent name and title
    private String _visibleName = null;
    private String _visibleTitle = null;
    // Alexander - Support for being able to enchant a weapon/armor using all attribute stones available
    private boolean _isEnchantAllAttribute = false;
    private PlayerCounters playerCountersExtension = null;
    private int soloInstance;
    private int partyInstance;


    public Player(final int objectId, final PlayerTemplate template, final String accountName) {
        super(objectId, template);

        login = accountName;
        _nameColor = 0xFFFFFF;
        _titlecolor = 0xFFFF77;
        baseClass = getClassId();
        buffSchemes = new CopyOnWriteArrayList<>();

//        for (Stats st : Stats.values())
//            addStatFunc(FuncClassesBalancer.INSTANCE(st, this));
    }

    /**
     * Constructor<?> of L2Player (use L2Character constructor).<BR>
     * <BR>
     * <p/>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Call the L2Character constructor to create an empty skills slot and copy basic Calculator set to this L2Player</li>
     * <li>Create a l2fadar object</li>
     * <li>Retrieve from the database all items of this L2Player and add them to inventory</li>
     * <p/>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SET the account name of the L2Player</B></FONT><BR>
     * <BR>
     *
     * @param objectId Identifier of the object to initialized
     * @param template The L2PlayerTemplate to apply to the L2Player
     */
    private Player(final int objectId, final PlayerTemplate template) {
        this(objectId, template, null);

        super.setAI(new PlayerAI(this));

        if (!Config.EVERYBODY_HAS_ADMIN_RIGHTS) {
            setPlayerAccess(Config.gmlist.get(objectId));
        } else {
            setPlayerAccess(Config.gmlist.get(0));
        }

        // Alexander - Create the statics holder for this pc
//		_stats = new PcStats(objectId());
    }

    /**
     * Create a new L2Player and add it in the characters table of the database.<BR>
     * <BR>
     * <p/>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Create a new L2Player with an account name</li>
     * <li>Set the name, the Hair Style, the Hair Color and the Face type of the L2Player</li>
     * <li>Add the getPlayer in the characters table of the database</li><BR>
     * <BR>
     *
     * @param accountName The name of the L2Player
     * @param name        The name of the L2Player
     * @param hairStyle   The hair style Identifier of the L2Player
     * @param hairColor   The hair color Identifier of the L2Player
     * @param face        The face type Identifier of the L2Player
     * @return The L2Player added to the database or null
     */
    public static Player create(int classId, int sex, String accountName, final String name, final int hairStyle, final int hairColor, final int face) {
        PlayerTemplate template = CharTemplateHolder.getTemplate(classId, sex != 0);

        // Create a new L2Player with an account name
        Player player = new Player(IdFactory.getInstance().getNextId(), template, accountName);

        player.setName(name)
                .setTitle("");
        player.setHairStyle(hairStyle);
        player.setHairColor(hairColor);
        player.setFace(face);
        player.setCreateTime(System.currentTimeMillis());

        // Add the getPlayer in the characters table of the database
        if (!CharacterDAO.insert(player)) {
            return null;
        }

        return player;
    }

    /**
     * Retrieve a L2Player from the characters table of the database and add it in _allObjects of the L2World
     *
     * @return The L2Player loaded from the database
     */
    public static Player restore(final int objectId) {
        Player player = null;

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement statement = con.createStatement();
             Statement statement2 = con.createStatement();
             ResultSet rset = statement.executeQuery("SELECT * FROM `characters` WHERE `obj_Id`=" + objectId + " LIMIT 1");
             ResultSet rset2 = statement2.executeQuery("SELECT `class_id` FROM `character_subclasses` WHERE `char_obj_id`=" + objectId + " AND `isBase`=1 LIMIT 1")) {
            if (rset.next() && rset2.next()) {
                final int classId = rset2.getInt("class_id");
                final boolean female = rset.getInt("sex") == 1;
                final PlayerTemplate template = CharTemplateHolder.getTemplate(classId, female);

                player = new Player(objectId, template);

                player.loadVariables(con);
                player.loadInstanceReuses(con);
                player.loadPremiumItemList(con);
                player.bookmarks.setCapacity(rset.getInt("bookmarks"));
                player.bookmarks.restore(con);
                player.friendList.restore(con);
                player.postFriends = CharacterPostFriendDAO.select(player, con);
                CharacterGroupReuseDAO.select(player, con);

                player.baseClass = ClassId.getById(classId);
                player.login = rset.getString("account_name");
                player.setName(rset.getString("char_name"));

                player.face = rset.getInt("face");
                player.hairStyle = rset.getInt("hairStyle");
                player.hairColor = rset.getInt("hairColor");
                player.setHeading(0);

                player.setKarma(rset.getInt("karma"));
                player.pvpKills = rset.getInt("pvpkills");
                player._pkKills = rset.getInt("pkkills");
                player.raids = rset.getInt("raidkills");
                player.soloInstance = rset.getInt("soloinstance");
                player.partyInstance = rset.getInt("partyinstance");
                player.setLeaveClanTime(rset.getLong("leaveclan") * 1000L);
                if (player.getLeaveClanTime() > 0 && player.canJoinClan()) {
                    player.setLeaveClanTime(0);
                }
                player.setDeleteClanTime(rset.getLong("deleteclan") * 1000L);
                if (player.getDeleteClanTime() > 0 && player.canCreateClan()) {
                    player.setDeleteClanTime(0);
                }
                player.setNoChannel(rset.getLong("nochannel") * 1000L);
                if ((player._NoChannel > 0L) && (player.getNoChannelRemained() < 0L)) {
                    player.setNoChannel(0L);
                }
                if (!player.isInBuffStore()) {
                    player.setOnlineTime(rset.getLong("onlinetime") * 1000L);
                }

                final int clanId = rset.getInt("clanid");
                if (clanId > 0) {
                    player.setClan(ClanTable.INSTANCE.getClan(clanId));
                    player.pledgeType = rset.getInt("pledge_type");
                    player._powerGrade = rset.getInt("pledge_rank");
                    player.lvlJoinedAcademy = rset.getInt("lvl_joined_academy");
                    player.apprentice = rset.getInt("apprentice");
                }

                player._createTime = rset.getLong("createtime") * 1000L;
                player._deleteTimer = rset.getInt("deletetime");

                SchemeBufferInstance.loadSchemes(player, con);

                player.setTitle(rset.getString("title"));

                if (player.isVarSet("titlecolor")) {
                    player.setTitleColor(Integer.decode("0x" + player.getVar("titlecolor")));
                }

                if (player.isVarSet("namecolor")) {
                    player.setNameColor(Integer.decode("0x" + player.getVar("namecolor")));
                } else {
                        player.setNameColor(Config.NORMAL_NAME_COLOUR);
                }

                player._fistsWeaponItem = player.findFistsWeaponItem(classId);
                player._uptime = System.currentTimeMillis();
                player._lastAccess = rset.getLong("lastAccess");

                player.setRecomHave(rset.getInt("rec_have"));
                player.setRecomLeft(rset.getInt("rec_left"));
                player.recomBonusTime = rset.getInt("rec_bonus_time");

                player.recomLeftToday = player.getVarInt("recLeftToday");

                player._nevitSystem.setPoints(rset.getInt("hunt_points"), rset.getInt("hunt_time"));

                player.setKeyBindings(rset.getBytes("key_bindings"));
                player._pcBangPoints = rset.getInt("pcBangPoints");

                player.fame = rset.getInt("fame");

                player.restoreRecipeBook(con);

                if (Config.ENABLE_OLYMPIAD) {
                    player.hero = Hero.INSTANCE.isHero(player.objectId());
                    player.noble = Olympiad.isNoble(player.objectId());
                }


                player.updatePledgeClass();

                int reflection = 0;

                if (player.isInJail()) {
                    // randomly spawn in prison
                    player.setLoc(new Location(Rnd.get(-114936, -114136), Rnd.get(-249768, -248952), -2984));

                    long period = player.getVarTimeToExpire("jailed");
                    player.updateNoChannel(period);
                    player.sitDown(null);
                    player.setBlock(true);

                } else if (player.isVarSet("jailedFrom")) {
                    String[] re = player.getVar("jailedFrom").split(";");

                    player.setLoc(new Location(toInt(re[0]), toInt(re[1]), toInt(re[2])));
                    player.setReflection(re.length > 3 ? toInt(re[3]) : 0);

                    player.unsetVar("jailedFrom");
                } else {
                    player.setLoc(new Location(rset.getInt("x"), rset.getInt("y"), rset.getInt("z")));
                    int ref = player.getVarInt("reflection");
                    if (ref != 0 && ref != ReflectionManager.JAIL.id) {
                        reflection = ref;
                        if (reflection > 0) // not the portal back of the GC Parnassus, Gila
                        {
                            String back = player.getVar("backCoords");
                            if (back != null) {
                                player.setLoc(Location.of(back));
                                player.unsetVar("backCoords");
                            }
                            reflection = 0;
                        }
                    }
                }

                player.setReflection(reflection);

                EventHolder.findEvent(player);

                Quest.restoreQuestStates(player, con);

                player.inventory.restore();

                restoreCharSubClasses(player, con);

                //4 points per minute based casino
                player.setVitality(rset.getInt("vitality") + (int) (((System.currentTimeMillis() / 1000L) -
                        rset.getLong("lastAccess")) / 15.));

                player._expandInventory = player.getVarInt("ExpandInventory");

                player._expandWarehouse = player.getVarInt("ExpandWarehouse");

                player._notShowBuffAnim = player.isVarSet(NO_ANIMATION_OF_CAST_VAR);


                player.setPetControlItem(player.getVarInt("pet"));

                if (player.isVarSet("isPvPevents")) {
                    player.unsetVar("isPvPevents");
                }

                try (PreparedStatement statement3 = con.prepareStatement("SELECT obj_Id, char_name FROM characters WHERE account_name=? AND obj_Id!=?")) {
                    statement3.setString(1, player.login);
                    statement3.setInt(2, objectId);
                    try (ResultSet rset3 = statement3.executeQuery()) {
                        while (rset3.next()) {
                            final Integer charId = rset3.getInt("obj_Id");
                            final String charName = rset3.getString("char_name");
                            player._chars.put(charId, charName);
                        }
                    }
                }

                // if (!getPlayer.isGM())
                {
                    List<Zone> zones = new ArrayList<>();

                    World.getZones(zones, player.getLoc(), player.getReflection());

                    if (!zones.isEmpty()) {
                        for (Zone zone : zones) {
                            if (zone.getType() == ZoneType.no_restart) {
                                if (((System.currentTimeMillis() / 1000L) - player.getLastAccess()) > zone.getRestartTime()) {
                                    player.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.EnterWorld.TeleportedReasonNoRestart"));
                                    player.setLoc(TeleportUtils.getRestartLocation(player, RestartType.TO_VILLAGE));
                                }
                            } else if (zone.getType() == ZoneType.SIEGE) {
                                SiegeEvent<?, ?> siegeEvent = player.getEvent(SiegeEvent.class);
                                if (siegeEvent != null) {
                                    player.setLoc(siegeEvent.getEnterLoc(player));
                                } else {
                                    Residence r = ResidenceHolder.getResidence(zone.getParams().getInteger("residence"));
                                    player.setLoc(r.getNotOwnerRestartPoint(player));
                                }
                            }
                        }
                    }

                    if (DimensionalRiftManager.INSTANCE.checkIfInRiftZone(player.getLoc(), false)) {
                        player.setLoc(DimensionalRiftManager.INSTANCE.getRoom(0, 0).getTeleportCoords());
                    }
                }

                player.restoreBlockList(con);
                player._macroses.restore(con);

                player.refreshExpertisePenalty();
                player.refreshOverloaded();

                player.warehouse.restore();
                player.freight.restore();

                player.restoreTradeList();
                if (player.isVarSet("storemode")) {
                    player.setPrivateStoreType(player.getVarInt("storemode"));
                    player.isSitting = true;

                    if (Config.AUCTION_PRIVATE_STORE_AUTO_ADDED) {
                        if (player.privatestore == STORE_PRIVATE_SELL) {
                            if (player.isVarSet("offline")) {
                                AuctionManager.getInstance().removePlayerStores(player);
                            }
                            for (TradeItem item : player.sellList) {
                                ItemInstance itemToSell = player.inventory.getItemByItemId(item.getItemId());
                                Auction a = AuctionManager.getInstance().addNewStore(player, itemToSell, item.getOwnersPrice(), item.getCount());
                                item.setAuctionId(a.auctionId());
                            }
                        }
                    }
                }

                player.updateKetraVarka();
                player.updateRam();
                player.checkRecom();
                if (player.isCursedWeaponEquipped()) {
                    player.restoreCursedWeapon();
                }

                //	getPlayer.getCounters().loadFile();

                if (Config.ENABLE_ACHIEVEMENTS)
                    player.loadAchivements();


            }
        } catch (IllegalArgumentException | SQLException e) {
            LOG.error("Could not restore char data! ", e);
        }

        if (player == null) throw new RuntimeException("Could not restore char data!");
        else return player;
    }

    public static String getVarFromPlayer(int objId, String var) {
        String value = null;

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement offline = con.prepareStatement("SELECT value FROM character_variables WHERE obj_id = ? AND name = ?")) {
            offline.setInt(1, objId);
            offline.setString(2, var);

            try (ResultSet rs = offline.executeQuery()) {
                if (rs.next()) {
                    value = Strings.stripSlashes(rs.getString("value"));
                }
            }
        } catch (SQLException e) {
            LOG.error("Error while getting Variable from Player", e);
        }
        return value;
    }

    /**
     * Restore list of character professions and set up active proof Used when character is loading
     */
    public static void restoreCharSubClasses(final Player player, Connection con) {
        try (PreparedStatement statement = con.prepareStatement("SELECT class_id,exp,sp,curHp,curCp,curMp,active,isBase,death_penalty,certification FROM character_subclasses WHERE char_obj_id=?")) {
            statement.setInt(1, player.objectId());
            SubClass activeSubclass = null;

            try (ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    final SubClass subClass = new SubClass();
                    subClass.setBase(rset.getInt("isBase") != 0);
                    subClass.setClassId(ClassId.getById(rset.getInt("class_id")));
                    subClass.setExp(rset.getLong("exp"));
                    subClass.setSp(rset.getInt("sp"));
                    subClass.setHp(rset.getDouble("curHp"));
                    subClass.setMp(rset.getDouble("curMp"));
                    subClass.setCp(rset.getDouble("curCp"));
                    subClass.setDeathPenalty(new DeathPenalty(player, rset.getInt("death_penalty")));
                    subClass.setCertification(rset.getInt("certification"));

                    boolean active = rset.getInt("active") != 0;
                    if (active) {
                        activeSubclass = subClass;
                    }
                    player.classlist.put(subClass.getClassId(), subClass);
                }
            }
            if (player.classlist.isEmpty()) {
                LOG.error("Error! There are no subclasses for getPlayer: " + player);
                return;
            }

            ClassId baseClassId = player.baseClass;
            if (baseClassId == null) {
                LOG.error("Error! There is no base class for getPlayer: " + player);
                return;
            }

            if (activeSubclass != null) {
                player.setActiveSubClass(activeSubclass.getClassId(), false);
            }

            if (player.activeClass == null) {
                final SubClass subClass = player.classlist.get(baseClassId);
                subClass.setActive(true);
                player.setActiveSubClass(subClass.getClassId(), false);
            }
        } catch (SQLException e) {
            LOG.warn("Could not restore char sub-classes: ", e);
        }
    }

    public boolean isPartyLeader() {
        if (party == null) return false;
        return party.getLeader() == this;
    }

//    @Override
//    public iHardReference<Player> getRef() {
//        return (iHardReference<Player>) super.getRef();
//    }

    public String getAccountName() {
        if (connection == null) {
            return login;
        }
        return connection.getLogin();
    }

    public String getIP() {
        if (connection == null) {
            return "<not connected>";
        }
        return connection.getIpAddr();
    }


    public Map<Integer, String> getAccountChars() {
        return _chars;
    }

    @Override
    public final PlayerTemplate getTemplate() {
        return (PlayerTemplate) template;
    }

    @Override
    public PlayerTemplate getBaseTemplate() {
        return (PlayerTemplate) baseTemplate;
    }

    public void changeSex() {
        setTransformation(251);
        template = CharTemplateHolder.getTemplate(getClassId(), isMale());
        setTransformation(0);
        this.sendPacket(new UserInfo(this));
    }

    @Override
    public PlayerAI getAI() {
        return (PlayerAI) super.getAI();
    }

    @Override
    public void doAttack(Creature target) {
        super.doAttack(target);
    }

    @Override
    public void sendReuseMessage(Skill skill) {
        if (isCastingNow()) {
            return;
        }
        TimeStamp sts = getSkillReuse(skill);
        if ((sts == null) || !sts.hasNotPassed()) {
            return;
        }
        long timeleft = sts.getReuseCurrent();
        if ((!Config.ALT_SHOW_REUSE_MSG && (timeleft < 10000)) || (timeleft < 500)) {
            return;
        }
        long hours = timeleft / 3600000;
        long minutes = (timeleft - (hours * 3600000)) / 60000;
        long seconds = (long) Math.ceil((timeleft - (hours * 3600000) - (minutes * 60000)) / 1000.);
        if (hours > 0) {
            sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_HOURS_S3_MINUTES_AND_S4_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.id, skill.getDisplayLevel()).addNumber(hours).addNumber(minutes).addNumber(seconds));
        } else if (minutes > 0) {
            sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.id, skill.getDisplayLevel()).addNumber(minutes).addNumber(seconds));
        } else {
            sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.id, skill.getDisplayLevel()).addNumber(seconds));
        }
    }

    @Override
    public final int getLevel() {
        return activeClass == null ? 1 : activeClass.getLevel();
    }

    public boolean isMale() {
        return getTemplate().isMale;
    }

    public int getFace() {
        return face;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public int getHairColor() {
        return hairColor;
    }

    public void setHairColor(int hairColor) {
        this.hairColor = hairColor;
    }

    public int getHairStyle() {
        return hairStyle;
    }

    public void setHairStyle(int hairStyle) {
        this.hairStyle = hairStyle;
    }

    public void kick() {
        if (connection != null) {
            connection.close(LeaveWorld.STATIC);
            setNetConnection(null);
        }
        stopAbnormalEffect(AbnormalEffect.FIREROOT_STUN);
        prepareToLogout();
        deleteMe();
    }

    public void restart() {
        if (connection != null) {
            connection.setActiveChar(null);
            setNetConnection(null);
        }
        prepareToLogout();
        deleteMe();
    }

    /**
     * The connection is closed, the client does not close, the character is saved and removed from the game Writing an inscription NO CARRIER
     */
    public void logout() {
        if (connection != null) {
            connection.close(ServerClose.STATIC);
            setNetConnection(null);
        }
        prepareToLogout();
        deleteMe();
    }

    private void prepareToLogout() {
        if (_isLogout.getAndSet(true)) {
            return;
        }

        setNetConnection(null);
        setIsOnline(false);

        getListeners().onExit();

        if (isFlying() && !checkLandingState()) {
            stablePoint = TeleportUtils.getRestartLocation(this, RestartType.TO_VILLAGE);
        }

        if (isCastingNow()) {
            abortCast(true, true);
        }

        if (getPrivateStoreType() == STORE_PRIVATE_SELL && isSitting()) {
            sellList.forEach(item ->
                    AuctionManager.getInstance().removeStore(this, item.getAuctionId()));
        }

        Party party = getParty();

        if ((party != null)) {
            if (isFestivalParticipant()) {
                party.getMembers().forEach(m -> m.sendMessage(getName() + " has been removed from the upcoming festival."));
            }
            leaveParty();
        }

        CursedWeaponsManager.INSTANCE.doLogout(this);

        if (olympiadObserveGame != null) {
            olympiadObserveGame.removeSpectator(this);
        }

        if (isInOlympiadMode() || (getOlympiadGame() != null)) {
            Olympiad.logoutPlayer(this);
        }

        stopFishing();

        if (isInObserverMode()) {
            if (getOlympiadObserveGame() == null) {
                leaveObserverMode();
            } else {
                leaveOlympiadObserverMode(true);
            }
            observerMode.set(OBSERVER_NONE);
        }

        if (stablePoint != null) {
            teleToLocation(stablePoint);
        }

        Summon pet = getPet();
        if (pet != null) {
            pet.saveEffects();
            pet.unSummon();
        }

        friendList.notifyFriends(false);

        if (isProcessingRequest()) {
            getRequest().cancel();
        }

        stopAllTimers();

        if (isInBoat()) {
            getBoat().removePlayer(this);
        }

        SubUnit unit = getSubUnit();
        UnitMember member = unit == null ? null : unit.getUnitMember(objectId());
        if (member != null) {
            int sponsor = member.getSponsor();
            int apprentice = getApprentice();
            PledgeShowMemberListUpdate memberUpdate = new PledgeShowMemberListUpdate(this);
            for (Player clanMember : clan.getOnlineMembers(objectId())) {
                clanMember.sendPacket(memberUpdate);
                if (clanMember.objectId() == sponsor) {
                    clanMember.sendPacket(new SystemMessage(SystemMessage.S1_YOUR_CLAN_ACADEMYS_APPRENTICE_HAS_LOGGED_OUT).addString(name));
                } else if (clanMember.objectId() == apprentice) {
                    clanMember.sendPacket(new SystemMessage(SystemMessage.S1_YOUR_CLAN_ACADEMYS_SPONSOR_HAS_LOGGED_OUT).addString(name));
                }
            }
            member.setPlayerInstance(this, true);
        }

        FlagItemAttachment attachment = getActiveWeaponFlagAttachment();
        if (attachment != null) {
            attachment.onLogout(this);
        }

        if (CursedWeaponsManager.INSTANCE.getCursedWeapon(getCursedWeaponEquippedId()) != null) {
            CursedWeaponsManager.INSTANCE.getCursedWeapon(getCursedWeaponEquippedId()).setPlayer(null);
        }

        MatchingRoom room = getMatchingRoom();
        if (room != null) {
            if (room.getLeader() == this) {
                room.disband();
            } else {
                room.removeMember(this, false);
            }
        }
        setMatchingRoom(null);

        MatchingRoomManager.INSTANCE.removeFromWaitingList(this);

        destroyAllTraps();

        if (_decoy != null) {
            _decoy.unSummon();
            _decoy = null;
        }

        stopPvPFlag();

        Reflection ref = getReflection();

        if (ref != ReflectionManager.DEFAULT) {
            if (ref.getReturnLoc() != null) {
                stablePoint = ref.getReturnLoc();
            }

            ref.removeObject(this);
        }
        getInventory().store();
        getRefund().clear();

        store(false);
    }

    public Collection<Recipe> getDwarvenRecipeBook() {
        return recipebook.values();
    }

    public Collection<Recipe> getCommonRecipeBook() {
        return commonrecipebook.values();
    }

    public boolean hasRecipe(final Recipe id) {
        return recipebook.containsValue(id) || commonrecipebook.containsValue(id);
    }

    public boolean findRecipe(final int id) {
        return recipebook.containsKey(id) || commonrecipebook.containsKey(id);
    }

    public void registerRecipe(final Recipe recipe, boolean saveDB) {
        if (recipe == null) {
            return;
        }
        if (recipe.isDwarvenRecipe()) recipebook.put(recipe.getId(), recipe);
        else commonrecipebook.put(recipe.getId(), recipe);
        if (saveDB) {
            mysql.set("REPLACE INTO character_recipebook (char_id, id) VALUES(?,?)", objectId(), recipe.getId());
        }
    }

    public void unregisterRecipe(final int RecipeID) {
        if (recipebook.containsKey(RecipeID)) {
            mysql.set("DELETE FROM `character_recipebook` WHERE `char_id`=? AND `id`=? LIMIT 1", objectId(), RecipeID);
            recipebook.remove(RecipeID);
        } else if (commonrecipebook.containsKey(RecipeID)) {
            mysql.set("DELETE FROM `character_recipebook` WHERE `char_id`=? AND `id`=? LIMIT 1", objectId(), RecipeID);
            commonrecipebook.remove(RecipeID);
        } else {
            LOG.warn("Attempted to remove unknown RecipeList" + RecipeID);
        }
    }

    public synchronized QuestState getQuestState(String questName) {
        return quests.stream()
                .filter(qs -> qs.quest.name.equals(questName))
                .findFirst().orElse(null);
    }

    public synchronized QuestState getQuestState(Quest quest) {
        return quests.stream()
                .filter(qs -> qs.quest == quest)
                .findFirst().orElse(null);
    }

    public QuestState getQuestState(Class<?> quest) {
        return getQuestState(quest.getSimpleName());
    }

    public boolean isQuestCompleted(Class<?> quest) {
        QuestState q = getQuestState(quest);
        return (q != null) && q.isCompleted();
    }

    public void setQuestState(QuestState qs) {
        quests.add(qs);
    }

    public void removeQuestState(String quest) {
        quests.remove(quest);
    }

    public List<Quest> getAllActiveQuests() {
        return this.quests.stream()
                .filter(QuestState::isStarted)
                .map(qs -> qs.quest)
                .collect(Collectors.toList());
    }

    public Collection<QuestState> getAllQuestsStates() {
        return quests;
    }

    public Stream<QuestState> getQuestsForEvent(NpcInstance npc, QuestEventType event) {
        return npc.getTemplate().getEventQuests(event).stream()
                .map(this::getQuestState)
                .filter(Objects::nonNull)
                .filter(qs -> !qs.isCompleted());
    }


    public void processQuestEvent(String quest, String event, NpcInstance npc, boolean... sendPacket) {
        Quest q = QuestManager.getQuest(quest);
        processQuestEvent(q, event, npc, sendPacket);
    }

    public void processQuestEvent(Quest quest, String event, NpcInstance npc, boolean... sendPacket) {
        if (event == null) {
            event = "";
        }
        QuestState qs = getQuestState(quest);
        if (qs == null) {
            qs = quest.newQuestState(this, Quest.CREATED);
        }
        if (qs.isCompleted()) {
            return;
        }
        qs.quest.notifyEvent(event, qs, npc);
        if (sendPacket.length == 0 || sendPacket[0])
            sendPacket(new QuestList(this));
    }

    public boolean isQuestContinuationPossible(boolean msg) {
        if ((getWeightPenalty() >= 3) || ((getInventoryLimit() * 0.9) < getInventory().getSize()) || ((Config.QUEST_INVENTORY_MAXIMUM * 0.9) < getInventory().getQuestSize())) {
            if (msg) {
                sendPacket(Msg.PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_VOLUME_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
            }
            return false;
        }
        return true;
    }

    private void stopQuestTimers() {
        getAllQuestsStates().forEach(qs -> {
            if (qs.isStarted()) qs.pauseQuestTimers();
            else qs.stopQuestTimers();
        });
    }

    private void resumeQuestTimers() {
        quests.forEach(QuestState::resumeQuestTimers);
    }

    public Collection<ShortCut> getAllShortCuts() {
        return shortCuts.getAllShortCuts();
    }

    public void registerShortCut(ShortCut shortcut) {
        shortCuts.registerShortCut(shortcut);
    }

    public void deleteShortCut(int slot, int page) {
        shortCuts.deleteShortCut(slot, page);
    }

    public void registerMacro(Macro macro) {
        _macroses.registerMacro(macro);
    }

    public void deleteMacro(int id) {
        _macroses.deleteMacro(id);
    }

    public MacroList getMacroses() {
        return _macroses;
    }

    public boolean isCastleLord(int castleId) {
        return (clan != null) && isClanLeader() && (clan.getCastle() == castleId);
    }

    public int getPkKills() {
        return _pkKills;
    }

    public void setPkKills(final int pkKills) {
        _pkKills = pkKills;
    }

    public long getCreateTime() {
        return _createTime;
    }

    private void setCreateTime(final long createTime) {
        _createTime = createTime;
    }

    public int getDeleteTimer() {
        return _deleteTimer;
    }

    public void setDeleteTimer(final int deleteTimer) {
        _deleteTimer = deleteTimer;
    }

    public int getCurrentLoad() {
        return getInventory().getTotalWeight();
    }

    public long getLastAccess() {
        return _lastAccess;
    }

    public int getRecomHave() {
        return _recomHave;
    }

    public void setRecomHave(int value) {
        if (value > 255) {
            _recomHave = 255;
        } else if (value < 0) {
            _recomHave = 0;
        } else {
            _recomHave = value;
        }
    }

    public int getRecomBonusTime() {
        if (recomBonusTask != null) {
            return (int) Math.max(0, recomBonusTask.getDelay(TimeUnit.SECONDS));
        }
        return recomBonusTime;
    }

    void setRecomBonusTime(int val) {
        recomBonusTime = val;
    }

    public int getRecomLeft() {
        return _recomLeft;
    }

    private void setRecomLeft(final int value) {
        _recomLeft = Math.min(999, value);
    }

    public boolean isHourglassEffected() {
        return _isHourglassEffected;
    }

    private void setHourlassEffected(boolean val) {
        _isHourglassEffected = val;
    }

    public void startHourglassEffect() {
        setHourlassEffected(true);
        stopRecomBonusTask(true);
        sendVoteSystemInfo();
    }

    public void stopHourglassEffect() {
        setHourlassEffected(false);
        startRecomBonusTask();
        sendVoteSystemInfo();
    }

    int addRecomLeft() {
        int recoms;
        if (getRecomLeftToday() < 20) {
            recoms = 10;
        } else {
            recoms = 1;
        }
        setRecomLeft(getRecomLeft() + recoms);
        setRecomLeftToday(getRecomLeftToday() + recoms);
        sendUserInfo(true);
        return recoms;
    }

    private int getRecomLeftToday() {
        return recomLeftToday;
    }

    private void setRecomLeftToday(final int value) {
        recomLeftToday = value;
        setVar("recLeftToday", recomLeftToday);
    }

    public void giveRecom(final Player target) {
        int targetRecom = target.getRecomHave();
        if (targetRecom < 255) {
            target.addRecomHave(1);
        }
        if (getRecomLeft() > 0) {
            setRecomLeft(getRecomLeft() - 1);
        }

        sendUserInfo(true);
    }

    public void addRecomHave(final int val) {
        setRecomHave(getRecomHave() + val);
        broadcastUserInfo(true);
        sendVoteSystemInfo();
    }

    public int getRecomBonus() {
        if ((getRecomBonusTime() > 0) || isHourglassEffected()) {
            return RecomBonus.getRecoBonus(this);
        }
        return 0;
    }

    private double getRecomBonusMul() {
        if ((getRecomBonusTime() > 0) || isHourglassEffected()) {
            return RecomBonus.getRecoMultiplier(this);
        }
        return 1;
    }

    public void sendVoteSystemInfo() {
        sendPacket(new ExVoteSystemInfo(this));
    }

    public boolean isRecomTimerActive() {
        return isRecomTimerActive;
    }

    private void setRecomTimerActive(boolean val) {
        if (isRecomTimerActive == val) {
            return;
        }

        isRecomTimerActive = val;

        if (val) {
            startRecomBonusTask();
        } else {
            stopRecomBonusTask(true);
        }

        sendVoteSystemInfo();
    }

    private void startRecomBonusTask() {
        if ((recomBonusTask == null) && (getRecomBonusTime() > 0) && isRecomTimerActive() && !isHourglassEffected()) {
            recomBonusTask = ThreadPoolManager.INSTANCE.schedule(new RecomBonusTask(this), getRecomBonusTime() * 1000);
        }
    }

    private void stopRecomBonusTask(boolean saveTime) {
        if (recomBonusTask != null) {
            if (saveTime) {
                setRecomBonusTime((int) Math.max(0, recomBonusTask.getDelay(TimeUnit.SECONDS)));
            }
            recomBonusTask.cancel(false);
            recomBonusTask = null;
        }
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        if (karma < 0) {
            karma = 0;
        }

        if (this.karma == karma) {
            return;
        }

        this.karma = karma;

        sendChanges();

        if (getPet() != null) {
            getPet().broadcastCharInfo();
        }
    }

    @Override
    public int getMaxLoad() {
        // Weight Limit = (CON Modifier*69000)*Skills
        // Source http://l2f.bravehost.com/weightlimit.html (May 2007)
        // Fitted exponential curve to the data
        int con = getCON();
        if (con < 1) {
            return (int) (31000 * Config.MAXLOAD_MODIFIER);
        } else if (con > 59) {
            return (int) (176000 * Config.MAXLOAD_MODIFIER);
        } else {
            return (int) calcStat(Stats.MAX_LOAD, Math.pow(1.029993928, con) * 30495.627366 * Config.MAXLOAD_MODIFIER, this, null);
        }
    }

    @Override
    public void updateEffectIcons() {
        if (entering || isLogoutStarted()) {
            return;
        }

        if (Config.USER_INFO_INTERVAL == 0) {
            if (updateEffectIconsTask != null) {
                updateEffectIconsTask.cancel(false);
                updateEffectIconsTask = null;
            }
            updateEffectIconsImpl();
            return;
        }

        if (updateEffectIconsTask != null) {
            return;
        }

        updateEffectIconsTask = ThreadPoolManager.INSTANCE.schedule(new UpdateEffectIcons(), Config.USER_INFO_INTERVAL);
    }

    private void updateEffectIconsImpl() {
        List<Effect> effects = getEffectList().getAllFirstEffects();
        effects.sort(EffectsComparator.getInstance());

        PartySpelled ps = new PartySpelled(this, false);
        AbnormalStatusUpdate mi = new AbnormalStatusUpdate();

        effects.stream()
                .filter(Effect::isInUse)
                .forEach(effect -> {
                    if (effect.getStackType().equals(EffectTemplate.HP_RECOVER_CAST)) {
                        sendPacket(new ShortBuffStatusUpdate(effect));
                    } else {
                        effect.addIcon(mi);
                    }
                    if (party != null) {
                        effect.addPartySpelledIcon(ps);
                    }
                });

        sendPacket(mi);
        if (party != null) {
            party.sendPacket(ps);
        }

        if (isInOlympiadMode() && isOlympiadCompStarted()) {
            OlympiadGame olymp_game = olympiadGame;
            if (olymp_game != null) {
                ExOlympiadSpelledInfo olympiadSpelledInfo = new ExOlympiadSpelledInfo();

                for (Effect effect : effects) {
                    if ((effect != null) && effect.isInUse()) {
                        effect.addOlympiadSpelledIcon(this, olympiadSpelledInfo);
                    }
                }

                if ((olymp_game.getType() == CompType.CLASSED) || (olymp_game.getType() == CompType.NON_CLASSED)) {
                    olymp_game.getTeamMembers(this).forEach(member -> member.sendPacket(olympiadSpelledInfo));
                }

                olymp_game.getSpectators().forEach(member -> member.sendPacket(olympiadSpelledInfo));
            }
        }
    }

    public int getWeightPenalty() {
        return getSkillLevel(4270);
    }

    public void refreshOverloaded() {
        if (isLogoutStarted() || (getMaxLoad() <= 0)) {
            return;
        }

        setOverloaded(getCurrentLoad() > getMaxLoad());
        double weightproc = (100. * (getCurrentLoad() - calcStat(Stats.MAX_NO_PENALTY_LOAD, 0, this, null))) / getMaxLoad();
        int newWeightPenalty;

        if (weightproc < 50) {
            newWeightPenalty = 0;
        } else if (weightproc < 66.6) {
            newWeightPenalty = 1;
        } else if (weightproc < 80) {
            newWeightPenalty = 2;
        } else if (weightproc < 100) {
            newWeightPenalty = 3;
        } else {
            newWeightPenalty = 4;
        }

        int current = getWeightPenalty();
        if (current == newWeightPenalty) {
            return;
        }

        if (newWeightPenalty > 0) {
            super.addSkill(4270, newWeightPenalty);
        } else {
            super.removeSkill(4270);
        }

        sendPacket(new SkillList(this));
        sendEtcStatusUpdate();
        updateStats();
    }

    public int getArmorsExpertisePenalty() {
        return getSkillLevel(6213);
    }

    public int getWeaponsExpertisePenalty() {
        return getSkillLevel(6209);
    }

    public int getExpertisePenalty(ItemInstance item) {
        if (item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON) {
            return getWeaponsExpertisePenalty();
        } else if ((item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR) || (item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY)) {
            return getArmorsExpertisePenalty();
        }
        return 0;
    }

    private void refreshExpertisePenalty() {
        if (isLogoutStarted()) {
            return;
        }

        boolean skillUpdate = false;

        int level = (int) calcStat(Stats.GRADE_EXPERTISE_LEVEL, getLevel());
        int i = EXPERTISE_LEVELS.stream()
                .filter(l -> l > level)
                .findFirst().orElse(0);
        if (expertiseIndex != i) {
            expertiseIndex = i;
            if ((expertiseIndex > 0) && Config.EXPERTISE_PENALTY) {
                addSkill(239, expertiseIndex, false);
                skillUpdate = true;
            }
        }

        int newWeaponPenalty = 0;
        int newArmorPenalty = 0;
        getInventory().getPaperdollItems().stream()
                .filter(Objects::nonNull)
                .forEach(item -> {
                    int crystaltype = item.getTemplate().getCrystalType().ordinal();
                    if (item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON) {
//                        if (crystaltype > newWeaponPenalty) {
//                            newWeaponPenalty = crystaltype;
//                        }
                    } else if ((item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR) || (item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY)) {
//                        if (crystaltype > newArmorPenalty) {
//                            newArmorPenalty = crystaltype;
//                        }
                    }
                });


        newWeaponPenalty = newWeaponPenalty - expertiseIndex;
        if (newWeaponPenalty <= 0) {
            newWeaponPenalty = 0;
        } else if (newWeaponPenalty >= 4) {
            newWeaponPenalty = 4;
        }

        newArmorPenalty = newArmorPenalty - expertiseIndex;
        if (newArmorPenalty <= 0) {
            newArmorPenalty = 0;
        } else if (newArmorPenalty >= 4) {
            newArmorPenalty = 4;
        }

        int weaponExpertise = getWeaponsExpertisePenalty();
        int armorExpertise = getArmorsExpertisePenalty();

        if (weaponExpertise != newWeaponPenalty) {
            weaponExpertise = newWeaponPenalty;
            if ((newWeaponPenalty > 0) && Config.EXPERTISE_PENALTY) {
                addSkill(6209, weaponExpertise);
            } else {
                removeSkill(6209);
            }
            skillUpdate = true;
        }
        if (armorExpertise != newArmorPenalty) {
            armorExpertise = newArmorPenalty;
            if ((newArmorPenalty > 0) && Config.EXPERTISE_PENALTY) {
                addSkill(6213, armorExpertise);
            } else {
                removeSkill(6213);
            }
            skillUpdate = true;
        }

        if (skillUpdate) {
            inventory.validateItemsSkills();

            sendPacket(new SkillList(this));
            sendEtcStatusUpdate();
            updateStats();
        }
    }

    public int getPvpKills() {
        return pvpKills;
    }

    public void setPvpKills(int pvpKills) {
        this.pvpKills = pvpKills;
    }

    public ClassId getClassId() {
        return getTemplate().classId;
    }

    public boolean isPendingOlyEnd() {
        return isPendingOlyEnd;
    }

    public void setPendingOlyEnd(boolean val) {
        isPendingOlyEnd = val;
    }

    private void addClanPointsOnProfession(final ClassId id) {
        if ((getLvlJoinedAcademy() != 0) && (clan != null) && (clan.getLevel() >= 5) && (id.occupation() == 1)) {
            clan.incReputation(100, true, "Academy");
        } else if ((getLvlJoinedAcademy() != 0) && (clan != null) && (clan.getLevel() >= 5) && (id.occupation() == 2)) {
            int earnedPoints;
            if (getLvlJoinedAcademy() <= 16) {
                earnedPoints = Config.MAX_ACADEM_POINT;
            } else if (getLvlJoinedAcademy() >= 39) {
                earnedPoints = Config.MIN_ACADEM_POINT;
            } else {
                earnedPoints = Config.MAX_ACADEM_POINT - ((getLvlJoinedAcademy() - 16) * 20);
            }

            clan.removeClanMember(objectId());

            SystemMessage sm = new SystemMessage(SystemMessage.CLAN_ACADEMY_MEMBER_S1_HAS_SUCCESSFULLY_COMPLETED_THE_2ND_CLASS_TRANSFER_AND_OBTAINED_S2_CLAN_REPUTATION_POINTS);
            sm.addString(getName());
            sm.addNumber(clan.incReputation(earnedPoints, true, "Academy"));
            clan.broadcastToOnlineMembers(sm);
            clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListDelete(getName()), this);

            setClan(null);
            setTitle("");
            sendPacket(Msg.CONGRATULATIONS_YOU_WILL_NOW_GRADUATE_FROM_THE_CLAN_ACADEMY_AND_LEAVE_YOUR_CURRENT_CLAN_AS_A_GRADUATE_OF_THE_ACADEMY_YOU_CAN_IMMEDIATELY_JOIN_A_CLAN_AS_A_REGULAR_MEMBER_WITHOUT_BEING_SUBJECT_TO_ANY_PENALTIES);
            setLeaveClanTime(0);

            broadcastCharInfo();

            sendPacket(PledgeShowMemberListDeleteAll.STATIC);

            ItemFunctions.addItem(this, 8181, 1, "Academy");
        }
    }

    public synchronized void setClassId(final ClassId id, boolean noban, boolean fromQuest) {
        boolean cannotChangeClass = !getPlayerAccess().CanChangeClass && !Config.EVERYBODY_HAS_ADMIN_RIGHTS;
        if (!noban && !id.equalsOrChildOf(getActiveClassId()) && cannotChangeClass) {
            LOG.error("Error while setting new class as :" + id + " Player:" + getName() + " current Class:" + getActiveClassId() + "|cannot change class? " + cannotChangeClass);
            Thread.dumpStack();
            return;
        }

        if (!classlist.containsKey(id)) {
            final SubClass cclass = getActiveClass();
            getSubClasses().remove(getActiveClassId());
            changeClassInDb(cclass.getClassId().id, id.id);
            if (cclass.isBase()) {
                setBaseClass(id);
                addClanPointsOnProfession(id);
                ItemInstance coupons = null;
                if (id.occupation() == 1) {
                    if (fromQuest && Config.ALT_ALLOW_SHADOW_WEAPONS) {
                        coupons = ItemFunctions.createItem(8869);
                    }
                    unsetVar("newbieweapon");
                    unsetVar("p1q2");
                    unsetVar("p1q3");
                    unsetVar("p1q4");
                    unsetVar("prof1");
                    unsetVar("ng1");
                    unsetVar("ng2");
                    unsetVar("ng3");
                    unsetVar("ng4");
                } else if (id.occupation() == 2) {
                    if (fromQuest && Config.ALT_ALLOW_SHADOW_WEAPONS) {
                        coupons = ItemFunctions.createItem(8870);
                    }
                    unsetVar("newbiearmor");
                    unsetVar("dd1");
                    unsetVar("dd2");
                    unsetVar("dd3");
                    unsetVar("prof2.1");
                    unsetVar("prof2.2");
                    unsetVar("prof2.3");
                }

                if (coupons != null) {
                    coupons.setCount(15);
                    sendPacket(SystemMessage2.obtainItems(coupons));
                    getInventory().addItem(coupons, "Class Change");
                }
            }

            //Holy Pomander
            switch (id) {
                case cardinal:
                    ItemFunctions.addItem(this, 15307, 1, "Class Change");
                    break;
                case evaSaint:
                    ItemFunctions.addItem(this, 15308, 1, "Class Change");
                    break;
                case shillienSaint:
                    ItemFunctions.addItem(this, 15309, 4, "Class Change");
                    break;
            }

            cclass.setClassId(id);
            classlist.put(id, cclass);
            rewardSkills(true);
            storeCharSubClasses();

            if (fromQuest) {
                broadcastPacket(new MagicSkillUse(this, 5103, 1000));
                sendPacket(new PlaySound("ItemSound.quest_fanfare_2"));
            }
            broadcastCharInfo();
        }

        PlayerTemplate t = CharTemplateHolder.getTemplate(id, !isMale());
        if (t == null) {
            LOG.error("Missing template for classId: " + id);
            // do not throw error - only print error
            return;
        }

        // Set the template of the L2Player
        template = t;

        // Update class icon in party and clan
        if (isInParty()) {
            party.sendPacket(new PartySmallWindowUpdate(this));
        }
        if (clan != null) {
            clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(this));
        }
        if (_matchingRoom != null) {
            _matchingRoom.broadcastPlayerUpdate(this);
        }
    }

    public long getExp() {
        return activeClass == null ? 0 : activeClass.getExp();
    }

    public void setExp(long exp) {
        int oldLvl = activeClass.getLevel();

        activeClass.setExp(exp);

        int level = activeClass.getLevel();
        if (level != oldLvl) {
            int levels = level - oldLvl;
            if (levels > 0)
                getNevitSystem().addPoints(1950);
            levelSet(levels);
        }

        updateStats();
    }

    private long getMaxExp() {
        return activeClass == null ? Experience.LEVEL[Experience.getMaxLevel() + 1] : activeClass.getMaxExp();
    }

    public ItemInstance getEnchantScroll() {
        return enchantScroll;
    }

    public void setEnchantScroll(final ItemInstance scroll) {
        enchantScroll = scroll;
    }

    private WeaponTemplate getFistsWeaponItem() {
        return _fistsWeaponItem;
    }

    public void setFistsWeaponItem(final WeaponTemplate weaponItem) {
        _fistsWeaponItem = weaponItem;
    }

    private WeaponTemplate findFistsWeaponItem(final int classId) {
        // human fighter fists
        if ((classId >= 0x00) && (classId <= 0x09)) {
            return (WeaponTemplate) ItemHolder.getTemplate(246);
        }

        // human mage fists
        if ((classId >= 0x0a) && (classId <= 0x11)) {
            return (WeaponTemplate) ItemHolder.getTemplate(251);
        }

        // elven fighter fists
        if ((classId >= 0x12) && (classId <= 0x18)) {
            return (WeaponTemplate) ItemHolder.getTemplate(244);
        }

        // elven mage fists
        if ((classId >= 0x19) && (classId <= 0x1e)) {
            return (WeaponTemplate) ItemHolder.getTemplate(249);
        }

        // dark elven fighter fists
        if ((classId >= 0x1f) && (classId <= 0x25)) {
            return (WeaponTemplate) ItemHolder.getTemplate(245);
        }

        // dark elven mage fists
        if ((classId >= 0x26) && (classId <= 0x2b)) {
            return (WeaponTemplate) ItemHolder.getTemplate(250);
        }

        // orc fighter fists
        if ((classId >= 0x2c) && (classId <= 0x30)) {
            return (WeaponTemplate) ItemHolder.getTemplate(248);
        }

        // orc mage fists
        if ((classId >= 0x31) && (classId <= 0x34)) {
            return (WeaponTemplate) ItemHolder.getTemplate(252);
        }

        // dwarven fists
        if ((classId >= 0x35) && (classId <= 0x39)) {
            return (WeaponTemplate) ItemHolder.getTemplate(247);
        }

        return null;
    }

    public void addExpAndCheckBonus(MonsterInstance mob, final double noRateExp, double noRateSp, double partyVitalityMod) {
        if (activeClass == null || isVarSet("NoExp")) {
            return;
        }

        double neededExp = calcStat(Stats.SOULS_CONSUME_EXP, 0.0D, mob, null);
        if ((neededExp > 0.0D) && (noRateExp > neededExp)) {
            mob.broadcastPacket(new SpawnEmitter(mob, this));
            ThreadPoolManager.INSTANCE.schedule(() -> {
                this.setConsumedSouls(this.getConsumedSouls() + 1, null);
            }, 1000L);
        }

        double vitalityBonus = 0.0D;
        int npcLevel = mob.getLevel();
        if (Config.ALT_VITALITY_ENABLED) {
            boolean blessActive = getNevitSystem().isBlessingActive();
            vitalityBonus = mob.isRaid() ? 0.0D : getVitalityLevel(blessActive) / 2.0D;
            vitalityBonus *= Config.ALT_VITALITY_RATE;

            if (noRateExp > 0.0D) {
                if (!mob.isRaid()) {
                    if ((blessActive) && ((!isVarSet("NoExp")) || (getExp() != (l2trunk.gameserver.model.base.Experience.LEVEL[(getLevel() + 1)] - 1L)))) {
                        double points = ((noRateExp / (npcLevel * npcLevel)) * 100.0D) / 9.0D;
                        points *= Config.ALT_VITALITY_CONSUME_RATE;
                        vitalityBonus = 4.0D * Config.ALT_VITALITY_RATE;
                        setVitality(getVitality() + (points * partyVitalityMod));
                    } else if ((!blessActive) && ((!isVarSet("NoExp")) || (getExp() != (l2trunk.gameserver.model.base.Experience.LEVEL[(getLevel() + 1)] - 1L)))) {
                        double points = ((noRateExp / (npcLevel * npcLevel)) * 100.0D) / 9.0D;
                        points *= Config.ALT_VITALITY_CONSUME_RATE;

                        if (getEffectList().getEffectByType(EffectType.Vitality) != null) {
                            points *= -1.0D;
                        }
                        if (getEffectList().getEffectByType(EffectType.VitalityMaintenance) == null) {
                            setVitality(getVitality() - (points * partyVitalityMod));
                        }
                    }
                } else {
                    setVitality(getVitality() + Config.ALT_VITALITY_RAID_BONUS);
                }
            }
        }

        // In the first call, activate the timer bonuses.
        if (!isInPeaceZone()) {
            setRecomTimerActive(true);
            getNevitSystem().startAdventTask();
            if ((getLevel() - npcLevel) <= 9) {
                int nevitPoints = (int) Math.round(((noRateExp / (npcLevel * npcLevel)) * 100) / 20); // TODO: Formula from the bulldozer.
                getNevitSystem().addPoints(nevitPoints);
            }
        }

        long normalExp = (long) (noRateExp * (((Config.RATE_XP * getRateExp()) + vitalityBonus) * getRecomBonusMul()));
        long normalSp = (long) (noRateSp * ((Config.RATE_SP * getRateSp()) + vitalityBonus));

        long expWithoutBonus = (long) (noRateExp * Config.RATE_XP * getRateExp());
        long spWithoutBonus = (long) (noRateSp * Config.RATE_SP * getRateSp());

        // Alexander - Add the exp acquired to the stats
//		if (normalExp > 0)
//			addPlayerStats(Ranking.STAT_TOP_EXP_ACQUIRED, normalExp);

        addExpAndSp(normalExp, normalSp, normalExp - expWithoutBonus, normalSp - spWithoutBonus, false, true);
    }

    public void VitalityStop(boolean stop) {
        _isVitalityStop = stop;
    }

    @SuppressWarnings("unused")
    private boolean isVitalityStop() {
        return _isVitalityStop;
    }

    @Override
    public void addExpAndSp(long exp, long sp) {
        addExpAndSp(exp, sp, 0, 0, false, false);
    }

    public void addExpAndSp(long addToExp, long addToSp, long bonusAddExp, long bonusAddSp, boolean applyRate, boolean applyToPet) {
        if (activeClass == null || isVarSet("NoExp")) {
            return;
        }

        if (applyRate) {
            addToExp *= Config.RATE_XP * getRateExp();
            addToSp *= Config.RATE_SP * getRateSp();
        }

        Summon pet = getPet();
        if (addToExp > 0) {
            if (applyToPet) {
                if ((pet != null) && !pet.isDead() && !PetDataTable.isVitaminPet(pet.getNpcId())) {
                    if (pet.getNpcId() == PetDataTable.SIN_EATER_ID) {
                        pet.addExpAndSp(addToExp, 0);
                        addToExp = 0;
                    } else if (pet instanceof PetInstance && (pet.getExpPenalty() > 0f)) {
                        if ((pet.getLevel() > (getLevel() - 20)) && (pet.getLevel() < (getLevel() + 5))) {
                            pet.addExpAndSp((long) (addToExp * pet.getExpPenalty()), 0);
                            addToExp *= 1. - pet.getExpPenalty();
                        } else {
                            pet.addExpAndSp((long) ((addToExp * pet.getExpPenalty()) / 5.), 0);
                            addToExp *= 1. - (pet.getExpPenalty() / 5.);
                        }
                    } else if (pet instanceof SummonInstance) {
                        addToExp *= 1. - pet.getExpPenalty();
                    }
                }
            }

            // Ady fix - establish if karma had before
            boolean hadKarma = karma > 0;

            // Remove Karma when the getPlayer kills L2MonsterInstance
            if (!isCursedWeaponEquipped() && (addToSp > 0) && (karma > 0)) {
                int toDecrease = Config.KARMA_MIN_KARMA / 10 + getPkKills() * Config.KARMA_SP_DIVIDER;
                setKarma(karma - Rnd.get(toDecrease / 2, toDecrease * 2));
            }

            // Ady PK fix
            if (karma <= 0) {
                karma = 0;
                if (hadKarma)
                    startPvPFlag(this);
            }

            getCounters().expAcquired += addToExp;

            long max_xp = isVarSet("NoExp") ? Experience.LEVEL[getLevel() + 1] - 1 : getMaxExp();
            addToExp = Math.min(addToExp, max_xp - getExp());
        }

        int oldLvl = activeClass.getLevel();
        long oldExp = activeClass.getExp();

        activeClass.addExp(addToExp);
        activeClass.addSp(addToSp);

        // Alexander - Add the exp lost to the getPlayer stats
//		if (addToExp < 0)
//			addPlayerStats(Ranking.STAT_TOP_EXP_LOST, -addToExp);

        if ((addToExp > 0) && (addToSp > 0) && ((bonusAddExp > 0) || (bonusAddSp > 0))) {
            sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_ACQUIRED_S1_EXP_BONUS_S2_AND_S3_SP_BONUS_S4).addLong(addToExp).addLong(bonusAddExp).addInteger(addToSp).addInteger((int) bonusAddSp));
        } else if ((addToSp > 0) && (addToExp == 0)) {
            sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_ACQUIRED_S1_SP).addNumber(addToSp));
        } else if ((addToSp > 0) && (addToExp > 0)) {
            sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1_EXPERIENCE_AND_S2_SP).addNumber(addToExp).addNumber(addToSp));
        } else if ((addToSp == 0) && (addToExp > 0)) {
            sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1_EXPERIENCE).addNumber(addToExp));
        }

        // Alexander - Custom tutorial event for the first exp got and then in lvl 6
        if (addToExp > 0 && (oldExp < 100 || (activeClass.getLevel() >= 6 && activeClass.getLevel() <= 10))) {
            Quest q = QuestManager.getQuest(_255_Tutorial.class);
            processQuestEvent(q, "CE41", null);
        }

        int level = activeClass.getLevel();
        if (level != oldLvl) {
            int levels = level - oldLvl;
            if (levels > 0) {
                getNevitSystem().addPoints(1950);
            }
            levelSet(levels);
        }
        // Custom Level Up Soul Crystals
        if (Config.AUTO_SOUL_CRYSTAL_QUEST) {
            Quest q = QuestManager.getQuest(_350_EnhanceYourWeapon.class);
            if (level >= 45 && getQuestState(_350_EnhanceYourWeapon.class) == null)
                processQuestEvent(q, "30115-04.htm", null, false);
        }

        if (pet instanceof PetInstance && PetDataTable.isVitaminPet(pet.getNpcId())) {
            PetInstance _pet = (PetInstance) pet;
            _pet.setLevel(getLevel());
            _pet.setExp(_pet.getExpForNextLevel());
            _pet.broadcastStatusUpdate();
        }

        if (getNevitSystem().isBlessingActive()) {
            addVitality(Config.ALT_VITALITY_NEVIT_POINT);
        }

        updateStats();
    }

    /**
     * Give Expertise skill of this occupation.<BR>
     * <BR>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Get the Level of the L2Player</li> <li>Add the Expertise skill corresponding to its Expertise occupation</li> <li>Update the overloaded status of the L2Player</li><BR>
     * <BR>
     * <p/>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T give other free skills (SP needed = 0)</B></FONT><BR>
     * <BR>
     */
    private void rewardSkills(boolean send) {
        boolean update = false;
        if (Config.AUTO_LEARN_SKILLS) {
            int unLearnable = 0;
            Collection<SkillLearn> skills = SkillAcquireHolder.getAvailableSkills(this, AcquireType.NORMAL);
            while (skills.size() > unLearnable) {
                unLearnable = 0;
                for (SkillLearn s : skills) {
                    Skill sk = SkillTable.INSTANCE.getInfo(s.id, s.getLevel());
                    if ((sk == null) || sk.cantLearn(getClassId()) || (!Config.AUTO_LEARN_FORGOTTEN_SKILLS && s.isClicked())) {
                        unLearnable++;
                        continue;
                    }
                    addSkill(sk, true);
                }
                skills = SkillAcquireHolder.getAvailableSkills(this, AcquireType.NORMAL);
            }
            update = true;
        } else {
            // Skills gives subscription-free does not need to be studied
            for (SkillLearn skill : SkillAcquireHolder.getAvailableSkills(this, AcquireType.NORMAL)) {
                if ((skill.getCost() == 0) && (skill.getItemId() == 0)) {
                    Skill sk = SkillTable.INSTANCE.getInfo(skill.id(), skill.getLevel());
                    addSkill(sk, true);
                    if ((getAllShortCuts().size() > 0) && (sk.level > 1)) {
                        for (ShortCut sc : getAllShortCuts()) {
                            if ((sc.getId() == sk.id) && (sc.getType() == ShortCut.TYPE_SKILL)) {
                                ShortCut newsc = new ShortCut(sc.getSlot(), sc.getPage(), sc.getType(), sc.getId(), sk.level, 1);
                                sendPacket(new ShortCutRegister(this, newsc));
                                registerShortCut(newsc);
                            }
                        }
                    }
                    update = true;
                }
            }
        }

        if (send && update) {
            sendPacket(new SkillList(this));
        }

        updateStats();
    }

    public Race getRace() {
        return getBaseTemplate().race;
    }

    public int getIntSp() {
        return (int) getSp();
    }

    public long getSp() {
        return activeClass == null ? 0 : activeClass.getSp();
    }

    public void setSp(long sp) {
        if (activeClass != null) {
            activeClass.setSp(sp);
        }
    }

    public int getClanId() {
        return clan == null ? 0 : clan.clanId();
    }

    public long getLeaveClanTime() {
        return _leaveClanTime;
    }

    public void setLeaveClanTime(final long time) {
        _leaveClanTime = time;
    }

    public long getDeleteClanTime() {
        return _deleteClanTime;
    }

    private void setDeleteClanTime(final long time) {
        _deleteClanTime = time;
    }

    public long getOnlineTime() {
        return _onlineTime + getUptime();
    }

    public void setOnlineTime(final long time) {
        _onlineTime = time;
        _onlineBeginTime = System.currentTimeMillis();
    }

    /**
     * @return Time since logging in in seconds
     */
    public long getOnlineBeginTime() {
        return _onlineBeginTime / 1000L;
    }

    public long getNoChannel() {
        return _NoChannel;
    }

    public void setNoChannel(final long time) {
        _NoChannel = time;
        if ((_NoChannel > 2145909600000L) || (_NoChannel < 0)) {
            _NoChannel = -1;
        }

        if (_NoChannel > 0) {
            _NoChannelBegin = System.currentTimeMillis();
        } else {
            _NoChannelBegin = 0;
        }
    }

    public long getNoChannelRemained() {
        if (_NoChannel == 0) {
            return 0;
        } else if (_NoChannel < 0) {
            return -1;
        } else {
            long remained = (_NoChannel - System.currentTimeMillis()) + _NoChannelBegin;
            if (remained < 0) {
                return 0;
            }

            return remained;
        }
    }

    public void setLeaveClanCurTime() {
        if (Config.CLAN_LEAVE_PENALTY == 0)
            return;

        _leaveClanTime = System.currentTimeMillis();
    }

    public boolean canJoinClan() {
        if (_leaveClanTime == 0) {
            return true;
        }
        if (System.currentTimeMillis() - _leaveClanTime >= Config.CLAN_LEAVE_PENALTY * 60 * 60 * 1000L) {
            _leaveClanTime = 0;
            return true;
        }
        return false;
    }

    public boolean canCreateClan() {
        if (_deleteClanTime == 0) {
            return true;
        }
        if (System.currentTimeMillis() - _deleteClanTime >= 10 * 24 * 60 * 60 * 1000L) {
            _deleteClanTime = 0;
            return true;
        }
        return false;
    }

    public IStaticPacket canJoinParty(Player inviter) {
        Request request = getRequest();
        if ((request != null) && request.isInProgress() && (request.getOtherPlayer(this) != inviter)) {
            return SystemMsg.WAITING_FOR_ANOTHER_REPLY.packet(inviter);
        }
        if (blockAll || getMessageRefusal()) {
            return SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE.packet(inviter);
        }
        if (isInParty()) {
            return new SystemMessage2(SystemMsg.C1_IS_A_MEMBER_OF_ANOTHER_PARTY_AND_CANNOT_BE_INVITED).addName(this);
        }
        if (inviter.getReflection() != getReflection()) {
            if ((inviter.getReflection() != ReflectionManager.DEFAULT) && (getReflection() != ReflectionManager.DEFAULT)) {
                return SystemMsg.INVALID_TARGET.packet(inviter);
            }
        }
        if (isCursedWeaponEquipped() || inviter.isCursedWeaponEquipped()) {
            return SystemMsg.INVALID_TARGET.packet(inviter);
        }
        if (inviter.isInOlympiadMode() || isInOlympiadMode()) {
            return SystemMsg.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS.packet(inviter);
        }
        if (!inviter.getPlayerAccess().CanJoinParty || !getPlayerAccess().CanJoinParty) {
            return SystemMsg.INVALID_TARGET.packet(inviter);
        }
        if (getTeam() != TeamType.NONE) {
            return SystemMsg.INVALID_TARGET.packet(inviter);
        }
        return null;
    }


    public PcInventory getInventory() {
        return inventory;
    }

    @Override
    public long getWearedMask() {
        return inventory.getWearedMask();
    }

    public PcFreight getFreight() {
        return freight;
    }

    public void removeItemFromShortCut(final int objectId) {
        shortCuts.deleteShortCutByObjectId(objectId);
    }

    private void removeSkillFromShortCut(final int skillId) {
        shortCuts.deleteShortCutBySkillId(skillId);
    }

    public boolean isSitting() {
        return isSitting;
    }

    public void setSitting(boolean val) {
        isSitting = val;
    }

    public boolean getSittingTask() {
        return sittingTaskLaunched;
    }

    @Override
    public void sitDown(StaticObjectInstance throne, boolean... force) {
        if (isSitting() || sittingTaskLaunched || isAlikeDead()) {
            return;
        }
        if (force.length == 0 || !force[0]) {
            if (sittingTaskLaunched) {
                return;
            }

            if (isStunned() || isSleeping() || isParalyzed() || isAttackingNow() || isCastingNow() || isMoving) {
                getAI().setNextAction(nextAction.REST, null, null, false, false);
                return;
            }
        }

        resetWaitSitTime();
        getAI().setIntention(CtrlIntention.AI_INTENTION_REST);

        if (throne == null) {
            broadcastPacket(new ChangeWaitType(this, ChangeWaitType.WT_SITTING));
        } else {
            broadcastPacket(new ChairSit(this, throne));
        }

        sittingObject = throne;
        setSitting(true);
        sittingTaskLaunched = true;
        ThreadPoolManager.INSTANCE.schedule(new EndSitDownTask(this), 2500);
    }

    @Override
    public void standUp() {
        if (!isSitting() || sittingTaskLaunched || isInStoreMode() || isAlikeDead()) {
            return;
        }

        getEffectList().stopAllSkillEffects(EffectType.Relax);

        getEffectList().stopAllSkillEffects(EffectType.SilentMove);

        getAI().clearNextAction();
        broadcastPacket(new ChangeWaitType(this, ChangeWaitType.WT_STANDING));

        sittingObject = null;
        sittingTaskLaunched = true;
        ThreadPoolManager.INSTANCE.schedule(new EndStandUpTask(this), 2500);
    }

    public void updateWaitSitTime() {
        if (_waitTimeWhenSit < 200) {
            _waitTimeWhenSit += 2;
        }
    }

    public int getWaitSitTime() {
        return _waitTimeWhenSit;
    }

    public void resetWaitSitTime() {
        _waitTimeWhenSit = 0;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public ItemContainer getRefund() {
        return refund;
    }

    public long getAdena() {
        return getInventory().getAdena();
    }

    public boolean reduceAdena(long adena, String log) {
        return reduceAdena(adena, false, log);
    }

    public boolean reduceAdena(long adena, boolean notify, String log) {
        if (adena < 0) {
            return false;
        }
        if (adena == 0) {
            return true;
        }
        boolean result = getInventory().reduceAdena(adena, log);
        if (notify && result) {
            sendPacket(SystemMessage2.removeItems(ItemTemplate.ITEM_ID_ADENA, adena));
        }
        return result;
    }

    public void addAdena(long adena, String log) {
        addAdena(adena, false, log);
    }

    public void addAdena(long adena, boolean notify, String log) {
        if (adena < 1) return;
        inventory.addAdena(adena, log);
        if (notify) sendPacket(SystemMessage2.obtainItems(ItemTemplate.ITEM_ID_ADENA, adena, 0));
    }

    public GameClient getNetConnection() {
        return connection;
    }

    public void setNetConnection(final GameClient connection) {
        this.connection = connection;
    }

    public boolean isConnected() {
        return (connection != null) && connection.isConnected();
    }

    @Override
    public void onAction(final Player player, boolean shift) {
        if (isFrozen()) {
            player.sendPacket(ActionFail.STATIC);
            return;
        }

        if (Events.onAction(player, this, shift)) {
            player.sendPacket(ActionFail.STATIC);
            return;
        }
        // Check if the other getPlayer already target this L2Player
        if (player.getTarget() != this) {
            player.setTarget(this);
            if (player.getTarget() == this) {
                player.sendPacket(new MyTargetSelected(objectId(), 0)); // The color to display in the getBonuses window is White
            } else {
                player.sendPacket(ActionFail.STATIC);
            }
        } else if (getPrivateStoreType() != Player.STORE_PRIVATE_NONE) {
            if ((getDistance(player) > INTERACTION_DISTANCE) && (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT)) {
                if (!shift) {
                    player.getAI().setIntentionInteract(CtrlIntention.AI_INTENTION_INTERACT, this);
                } else {
                    player.sendPacket(ActionFail.STATIC);
                }
            } else {
                player.doInteract(this);
            }
        } else if (isAutoAttackable(player)) {
            player.getAI().Attack(this, false, shift);
        } else if (player != this) {
            if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_FOLLOW) {
                if (!shift) {
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this, Config.FOLLOW_RANGE);
                } else {
                    player.sendPacket(ActionFail.STATIC);
                }
            } else {
                player.sendPacket(ActionFail.STATIC);
            }
        } else {
            player.sendPacket(ActionFail.STATIC);
        }
    }

    @Override
    public void broadcastStatusUpdate() {
        if (!needStatusUpdate()) {
            return;
        }

        StatusUpdate su = makeStatusUpdate(StatusUpdate.MAX_HP, StatusUpdate.MAX_MP, StatusUpdate.MAX_CP, StatusUpdate.CUR_HP, StatusUpdate.CUR_MP, StatusUpdate.CUR_CP);
        sendPacket(su);

        // Check if a party is in progress
        if (isInParty()) {
            // Send the Server->Client packet PartySmallWindowUpdate with current HP, MP and Level to all other L2Player of the Party
            getParty().sendPacket(this, new PartySmallWindowUpdate(this));
        }

        DuelEvent duelEvent = getEvent(DuelEvent.class);
        if (duelEvent != null) {
            duelEvent.sendPacket(new ExDuelUpdateUserInfo(this), getTeam().revert().name());
        }

        if (isInOlympiadMode() && isOlympiadCompStarted()) {
            if (olympiadGame != null) {
                olympiadGame.broadcastInfo(this, null, false);
            }
        }
    }

    @Override
    public void broadcastCharInfo() {
        broadcastUserInfo(false);
    }

    public void broadcastUserInfo(boolean force) {
        sendUserInfo(force);

        if (Config.BROADCAST_CHAR_INFO_INTERVAL == 0) {
            force = true;
        }

        if (force) {
            if (broadcastCharInfoTask != null) {
                broadcastCharInfoTask.cancel(false);
                broadcastCharInfoTask = null;
            }
            broadcastCharInfoImpl();
            return;
        }

        if (broadcastCharInfoTask != null) {
            return;
        }

        broadcastCharInfoTask = ThreadPoolManager.INSTANCE.schedule(new BroadcastCharInfoTask(), Config.BROADCAST_CHAR_INFO_INTERVAL);
    }

    public boolean isPolymorphed() {
        return polyNpcId != 0;
    }

    public int getPolyId() {
        return polyNpcId;
    }

    public void setPolyId(int polyid) {
        polyNpcId = polyid;

        //teleToLocation(getTerritory());
        broadcastUserInfo(true);
    }

    private void broadcastCharInfoImpl() {
        L2GameServerPacket exCi = new ExBR_ExtraUserInfo(this);
        L2GameServerPacket dominion = getEvent(DominionSiegeEvent.class) != null ? new ExDominionWarStart(this) : null;
        World.getAroundPlayers(this)
                .forEach(p -> {
                    p.sendPacket(isPolymorphed() ? new NpcInfoPoly(this) : new CharInfo(this), exCi);
                    p.sendPacket(RelationChanged.update(p, this, p));
                    if (dominion != null) {
                        p.sendPacket(dominion);
                    }
                });
    }

    public void broadcastRelationChanged() {
        World.getAroundPlayers(this)
                .forEach(p -> p.sendPacket(RelationChanged.update(p, this, p)));
    }

    public void sendEtcStatusUpdate() {
        if (!isVisible()) {
            return;
        }

        sendPacket(new EtcStatusUpdate(this));
    }

    private void sendUserInfoImpl() {
        sendPacket(new UserInfo(this), new ExBR_ExtraUserInfo(this));
        DominionSiegeEvent siegeEvent = getEvent(DominionSiegeEvent.class);
        if (siegeEvent != null) {
            sendPacket(new ExDominionWarStart(this));
        }
    }

    public void sendUserInfo() {
        sendUserInfo(false);
    }

    public void sendUserInfo(boolean force) {
        if (!isVisible() || entering || isLogoutStarted()) {
            return;
        }

        if ((Config.USER_INFO_INTERVAL == 0) || force) {
            if (_userInfoTask != null) {
                _userInfoTask.cancel(false);
                _userInfoTask = null;
            }
            sendUserInfoImpl();
            return;
        }

        if (_userInfoTask != null) {
            return;
        }

        _userInfoTask = ThreadPoolManager.INSTANCE.schedule(() -> {
            sendUserInfoImpl();
            _userInfoTask = null;
        }, Config.USER_INFO_INTERVAL);
    }

    @Override
    public StatusUpdate makeStatusUpdate(int... fields) {
        StatusUpdate su = new StatusUpdate(objectId());
        for (int field : fields) {
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
                case StatusUpdate.CUR_LOAD:
                    su.addAttribute(field, getCurrentLoad());
                    break;
                case StatusUpdate.MAX_LOAD:
                    su.addAttribute(field, getMaxLoad());
                    break;
                case StatusUpdate.PVP_FLAG:
                    su.addAttribute(field, pvpFlag);
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
            }
        }
        return su;
    }

    public void sendStatusUpdate(boolean broadCast, boolean withPet, int... fields) {
        if ((fields.length == 0) || (entering && !broadCast)) {
            return;
        }

        StatusUpdate su = makeStatusUpdate(fields);
        if (!su.hasAttributes()) {
            return;
        }

        List<L2GameServerPacket> packets = new ArrayList<>(withPet ? 2 : 1);
        if (withPet && (getPet() != null)) {
            packets.add(getPet().makeStatusUpdate(fields));
        }

        packets.add(su);

        if (!broadCast) {
            sendPacket(packets);
        } else if (entering) {
            broadcastPacketToOthers(packets);
        } else {
            broadcastPacket(packets);
        }
    }

    public int getAllyId() {
        return clan == null ? 0 : clan.getAllyId();
    }

    @Override
    public void sendPacket(IStaticPacket p) {
        if (!isConnected()) {
            return;
        }

        if (isPacketIgnored(p.packet(this))) {
            return;
        }

        connection.sendPacket(p.packet(this));
    }

    @Override
    public void sendPacket(IStaticPacket... packets) {
        if (!isConnected()) {
            return;
        }

        for (IStaticPacket p : packets) {
            if (isPacketIgnored(p)) {
                continue;
            }

            connection.sendPacket(p.packet(this));
        }
    }

    private boolean isPacketIgnored(IStaticPacket p) {
        if (p == null) return true;
        return _notShowBuffAnim && ((p.getClass() == MagicSkillLaunched.class) || (p.getClass() == SocialAction.class));
    }

    @Override
    public void sendPacket(List<? extends IStaticPacket> packets) {
        if (!isConnected()) return;
        packets.forEach(p -> connection.sendPacket(p.packet(this)));

    }

    public void doInteract(GameObject target) {
        if ((target == null) || isActionsDisabled()) {
            sendActionFailed();
            return;
        }
        if (target instanceof Player) {
            if (target.getDistance(this) <= INTERACTION_DISTANCE) {
                Player temp = (Player) target;

                if ((temp.getPrivateStoreType() == STORE_PRIVATE_SELL) || (temp.getPrivateStoreType() == STORE_PRIVATE_SELL_PACKAGE)) {
                    sendPacket(new PrivateStoreListSell(this, temp));
                } else if (temp.getPrivateStoreType() == STORE_PRIVATE_BUY) {
                    sendPacket(new PrivateStoreListBuy(this, temp));
                } else if (temp.getPrivateStoreType() == STORE_PRIVATE_MANUFACTURE) {
                    sendPacket(new RecipeShopSellList(this, temp));
                }
                // Ady - Support for buff stores
                else if (temp.getPrivateStoreType() == STORE_PRIVATE_BUFF) {
                    OfflineBufferManager.INSTANCE.processBypass(this, "BuffStore bufflist " + temp.objectId());
                }
                sendActionFailed();
            } else if (getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT) {
                getAI().setIntentionInteract(CtrlIntention.AI_INTENTION_INTERACT, this);
            }
        } else {
            target.onAction(this, false);
        }
    }

    public void doAutoLootOrDrop(ItemInstance item, NpcInstance fromNpc) {
        boolean forceAutoloot = fromNpc.isFlying() || getReflection().isAutolootForced();

        if ((fromNpc.isRaid() || (fromNpc instanceof ReflectionBossInstance)) && !item.isHerb() && !forceAutoloot) {
            item.dropToTheGround(this, fromNpc);
            return;
        }

        if (!item.isAdena()) {
            if ((item.isHerb()) || (!item.isHerb())) {
                item.dropToTheGround(this, fromNpc);
                return;
            }
        }
        // Herbs
        if (item.isHerb()) {
            if (!forceAutoloot) {
                item.dropToTheGround(this, fromNpc);
                return;
            }
            List<Skill> skills = item.getTemplate().getAttachedSkills();
            skills.stream()
                    .mapToInt(s -> s.id)
                    .forEach(skill -> {
                        altUseSkill(skill, this);
                        Summon pet = getPet();
                        if (pet instanceof SummonInstance && !pet.isDead()) {
                            pet.altUseSkill(skill, pet);
                        }
                    });
            item.deleteMe();
        }

        if (!forceAutoloot) {
            item.dropToTheGround(this, fromNpc);
            return;
        }

        // Check if the L2Player is in a Party
        if (!isInParty() || item.isCursed()) {
            if (!pickupItem(item, Log.Pickup)) {
                item.dropToTheGround(this, fromNpc);
                return;
            }
        } else {
            getParty().distributeItem(this, item, fromNpc);
        }

        broadcastPickUpMsg(item);
    }

    @Override
    public void doPickupItem(final ItemInstance item) {
        // Check if the L2Object to pick up is a L2ItemInstance
        sendActionFailed();
        stopMove();
        synchronized (item) {
            if (!item.isVisible()) {
                return;
            }

            // Check if me not owner of item and, if in party, not in owner party and nonowner pickup delay still active
            if (!ItemFunctions.checkIfCanPickup(this, item)) {
                SystemMessage sm;
                if (item.getItemId() == 57) {
                    sm = new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA);
                    sm.addNumber(item.getCount());
                } else {
                    sm = new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_PICK_UP_S1);
                    sm.addItemName(item.getItemId());
                }
                sendPacket(sm);
                return;
            }

            // Herbs
            if (item.isHerb()) {
                item.getTemplate().getAttachedSkills().stream()
                        .mapToInt(skill -> skill.id)
                        .forEach(skill -> {
                            altUseSkill(skill, this);
                            Summon pet = getPet();
                            if (pet instanceof SummonInstance && !pet.isDead()) {
                                pet.altUseSkill(skill, pet);
                            }
                        });

                broadcastPacket(new GetItem(item, objectId()));
                item.deleteMe();
                return;
            }

            FlagItemAttachment attachment = item.getAttachment() instanceof FlagItemAttachment ? (FlagItemAttachment) item.getAttachment() : null;

            if (!isInParty() || (attachment != null) || item.isCursed()) {
                if (pickupItem(item, Log.Pickup)) {
                    broadcastPacket(new GetItem(item, objectId()));
                    broadcastPickUpMsg(item);
                    item.pickupMe();
                }
            } else {
                getParty().distributeItem(this, item, null);
            }
        }
    }


    public boolean pickupItem(ItemInstance item, String log) {
        PickableAttachment attachment = item.getAttachment() instanceof PickableAttachment ? (PickableAttachment) item.getAttachment() : null;

        if (!ItemFunctions.canAddItem(this, item)) {
            return false;
        }

        if ((item.getItemId() == ItemTemplate.ITEM_ID_ADENA) || (item.getItemId() == 6353)) {
            Quest q = QuestManager.getQuest(_255_Tutorial.class);
            processQuestEvent(q, "CE" + item.getItemId(), null);
        }

        sendPacket(SystemMessage2.obtainItems(item));
        inventory.addItem(item, log);

        if (attachment != null) {
            attachment.pickUp(this);
        }

        sendChanges();
        return true;
    }

    public void setObjectTarget(GameObject target) {
        setTarget(target);
        if (target == null) {
            return;
        }

        if (target == getTarget()) {
            if (target instanceof NpcInstance) {
                NpcInstance npc = (NpcInstance) target;
                sendPacket(new MyTargetSelected(npc.objectId(), getLevel() - npc.getLevel()));
                sendPacket(npc.makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP));
                sendPacket(new ValidateLocation(npc), ActionFail.STATIC);
            } else {
                sendPacket(new MyTargetSelected(target.objectId(), 0));
            }
        }
    }

    @Override
    public void setTarget(GameObject newTarget) {
        // Check if the new target is visible
        if ((newTarget != null) && !newTarget.isVisible()) {
            newTarget = null;
        }

        // Can't target and attack festival monsters if not participant
        if ((newTarget instanceof FestivalMonsterInstance) && !isFestivalParticipant()) {
            newTarget = null;
        }

        Party party = getParty();

        // Can't target and attack rift invaders if not in the same room
        if ((party != null) && party.isInDimensionalRift()) {
            int riftType = party.getDimensionalRift().getType();
            int riftRoom = party.getDimensionalRift().getCurrentRoom();
            if ((newTarget != null) && !DimensionalRiftManager.INSTANCE.getRoom(riftType, riftRoom).checkIfInZone(newTarget.getLoc())) {
                newTarget = null;
            }
        }

        GameObject oldTarget = getTarget();

        if (oldTarget != null) {
            if (oldTarget.equals(newTarget)) {
                return;
            }

            // Remove the L2Player from the _statusListener of the old target if it was a L2Character
            if (oldTarget instanceof Creature) {
                ((Creature) oldTarget).removeStatusListener(this);
            }

//			if(newTarget == null)
//			{
//				broadcastPacket(new TargetUnselected(this));
//			}
            broadcastPacket(new TargetUnselected(this));
        }

        if (newTarget != null) {
            // Add the L2Player to the _statusListener of the new target if it's a L2Character
            if (newTarget instanceof Creature) {
                ((Creature) newTarget).addStatusListener(this);
            }

            //broadcastPacketToOthers(new TargetSelected(objectId(), newTarget.objectId(), getTerritory()));
            broadcastPacket(new TargetSelected(objectId(), newTarget.objectId(), getLoc()));
        }

        super.setTarget(newTarget);
    }

    /**
     * @return the active weapon instance (always equipped in the right hand).<BR>
     * <BR>
     */
    @Override
    public ItemInstance getActiveWeaponInstance() {
        return getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
    }

    /**
     * @return the active weapon item (always equipped in the right hand).<BR>
     * <BR>
     */
    @Override
    public WeaponTemplate getActiveWeaponItem() {
        final ItemInstance weapon = getActiveWeaponInstance();

        if (weapon == null) {
            return getFistsWeaponItem();
        }

        return (WeaponTemplate) weapon.getTemplate();
    }

    /**
     * @return the secondary weapon instance (always equipped in the left hand).<BR>
     * <BR>
     */
    @Override
    public ItemInstance getSecondaryWeaponInstance() {
        return getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
    }

    /**
     * @return the secondary weapon item (always equipped in the left hand) or the fists weapon.<BR>
     * <BR>
     */
    @Override
    public WeaponTemplate getSecondaryWeaponItem() {
        final ItemInstance weapon = getSecondaryWeaponInstance();

        if (weapon == null) {
            return getFistsWeaponItem();
        }

        final ItemTemplate item = weapon.getTemplate();

        if (item instanceof WeaponTemplate) {
            return (WeaponTemplate) item;
        }

        return null;
    }

    public boolean isWearingArmor(final ArmorType armorType) {
        final ItemInstance chest = getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);

        if (chest == null) {
            return armorType == ArmorType.NONE;
        }

        if (chest.getItemType() != armorType) {
            return false;
        }

        if (chest.getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR) {
            return true;
        }

        final ItemInstance legs = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);

        return legs == null ? armorType == ArmorType.NONE : legs.getItemType() == armorType;
    }

    @Override
    public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage) {
        if ((attacker == null) || isDead() || (attacker.isDead() && !isDot)) {
            return;
        }

        // 5182 = Blessing of protection
        if (attacker instanceof Player && (Math.abs(attacker.getLevel() - getLevel()) > 10)) {
            if ((((Player) attacker).getKarma() > 0) && (getEffectList().getEffectsBySkillId(5182) != null) && !isInZone(ZoneType.SIEGE)) {
                return;
            }

            if ((getKarma() > 0) && (attacker.getEffectList().getEffectsBySkillId(5182) != null) && !attacker.isInZone(ZoneType.SIEGE)) {
                return;
            }
        }

        // Reduce the current HP of the L2Player
        super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
    }

    public Creature getLastAttacker() {
        return lastAttacker;
    }

    public long getLastAttackDate() {
        return lastAttackDate;
    }

    @Override
    protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp) {
        if (standUp) {
            standUp();
            if (isFakeDeath()) {
                breakFakeDeath();
            }
        }

        lastAttacker = attacker;
        lastAttackDate = System.currentTimeMillis();

        if (attacker instanceof Playable) {
            if (!directHp && (getCurrentCp() > 0)) {
                double cp = getCurrentCp();
                if (isInOlympiadMode()) {
                    addDamageOnOlympiad(attacker, skill, damage, cp);
                }

                if (cp >= damage) {
                    cp -= damage;
                    damage = 0;
                } else {
                    damage -= cp;
                    cp = 0;
                }

                setCurrentCp(cp);
            }
        }

        double hp = getCurrentHp();

        DuelEvent duelEvent = getEvent(DuelEvent.class);
        if (duelEvent != null) {
            if (hp <= damage) {
                setCurrentHp(1, true);
                duelEvent.onDie(this);
                return;
            }
        }

        if (isInOlympiadMode()) {
            addDamageOnOlympiad(attacker, skill, damage, hp);

            if (hp <= damage) {// it was if (hp + 0.5 <= damage)
                if (olympiadGame.getType() != CompType.TEAM) {
                    setCurrentHp(1, true);
                    olympiadGame.setWinner(getOlympiadSide() == 1 ? 2 : 1);
                    olympiadGame.endGame(20, false);
                    attacker.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                    attacker.sendActionFailed();
                    return;
                } else if (olympiadGame.doDie(this)) {
                    olympiadGame.setWinner(getOlympiadSide() == 1 ? 2 : 1);
                    olympiadGame.endGame(20, false);
                }
            }
        }

        super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
    }

    private void addDamageOnOlympiad(Creature attacker, Skill skill, double damage, double hpcp) {
        if ((this != attacker) && ((skill == null) || skill.isOffensive)) {
            olympiadGame.addDamage(this, Math.min(hpcp, damage));
        }
    }

    private void altDeathPenalty(final Creature killer) {
        // Reduce the Experience of the L2Player in function of the calculated Death Penalty
        if (!Config.ALT_GAME_DELEVEL) {
            return;
        }
        if (isInZoneBattle()) {
            return;
        }
        if (isInZonePvP()) {
            return;
        }
        if (getNevitSystem().isBlessingActive()) {
            return;
        }
        deathPenalty(killer);
    }

    private boolean atWarWith(final Player player) {
        return (clan != null) && (player.getClan() != null) && (getPledgeType() != -1) && (player.getPledgeType() != -1) && clan.isAtWarWith(player.getClan().clanId());
    }

    public boolean atMutualWarWith(Player player) {
        return (clan != null) && (player.getClan() != null) && (getPledgeType() != -1) && (player.getPledgeType() != -1) && clan.isAtWarWith(player.getClan().clanId()) && player.getClan().isAtWarWith(clan.clanId());
    }

    private void doPurePk(final Player killer) {
        if (killer.getKarma() > 0)
            killer.getCounters().pkInARowKills++;
        else
            killer.getCounters().pkInARowKills = 1;

        // Check if the attacker has a PK counter greater than 0
        final int pkCountMulti = Math.max(killer.getPkKills() / 2, 1);


        // Calculate the new Karma of the attacker : newKarma = baseKarma*pkCountMulti*lvlDiffMulti
        // Add karma to attacker and increase its PK counter
        killer.increaseKarma(Config.KARMA_MIN_KARMA * pkCountMulti); // * lvlDiffMulti);
        killer.setPkKills(killer.getPkKills() + 1);

    }

    private void doKillInPeace(final Player killer) // Check if the L2Player killed haven't Karma
    {
        if (karma <= 0) {
            if (Config.SERVICES_PK_PVP_KILL_ENABLE) {
                if (Config.SERVICES_PK_PVP_TIE_IF_SAME_IP) {
                    ItemFunctions.addItem(killer, Config.SERVICES_PK_KILL_REWARD_ITEM, Config.SERVICES_PK_KILL_REWARD_COUNT, "Pk");
                } else {
                    ItemFunctions.addItem(killer, Config.SERVICES_PK_KILL_REWARD_ITEM, Config.SERVICES_PK_KILL_REWARD_COUNT, "Pk");
                }
            }
            doPurePk(killer);
        } else {
            killer.setPvpKills(killer.getPvpKills() + 1);
        }
    }

    private void checkAddItemToDrop(List<ItemInstance> array, List<ItemInstance> items, int maxCount) {
        for (int i = 0; (i < maxCount) && !items.isEmpty(); i++) {
            ItemInstance item = Rnd.get(items);
            items.remove(item);
            array.add(item);
        }
    }

    public FlagItemAttachment getActiveWeaponFlagAttachment() {
        ItemInstance item = getActiveWeaponInstance();
        if ((item == null) || !(item.getAttachment() instanceof FlagItemAttachment)) {
            return null;
        }
        return (FlagItemAttachment) item.getAttachment();
    }

    private void doPKPVPManage(Creature killer) {
        FlagItemAttachment attachment = getActiveWeaponFlagAttachment();
        if (attachment != null) {
            attachment.onDeath(this, killer);
        }

        if ((killer == null) || (killer == summon) || (killer == this)) {
            return;
        }

        if ((isInZoneBattle() || killer.isInZoneBattle()) && !Config.ZONE_PVP_COUNT) {
            // Alexander - Add the arena kill to the stats
//			if (killer.isPlayer())
//			{
//				addPlayerStats(Ranking.STAT_TOP_ARENA_DEATHS);
//
//				killer.getPlayer().addPlayerStats(Ranking.STAT_TOP_ARENA_KILLS);
//			}

            return;
        }

        if ((killer instanceof Summon) && ((killer = killer.getPlayer()) == null)) {
            return;
        }


        // Processing Karma/PKCount/PvPCount for killer
        if (killer instanceof Player) {
            Player pk = (Player) killer;
            int repValue = (getLevel() - pk.getLevel()) >= 20 ? 2 : 1;
            boolean war = atMutualWarWith(pk);

            if ((war) && (pk.getClan().getReputationScore() > 0) && (clan.getLevel() >= 5) && (clan.getReputationScore() > 0) && (pk.getClan().getLevel() >= 5)) {
                clan.broadcastToOtherOnlineMembers(new SystemMessage(1782).addString(getName()).addNumber(-clan.incReputation(-repValue, true, "ClanWar")), this);
                pk.getClan().broadcastToOtherOnlineMembers(new SystemMessage(1783).addNumber(pk.getClan().incReputation(repValue, true, "ClanWar")), pk);
            }

            CastleSiegeEvent siegeEvent = getEvent(CastleSiegeEvent.class);
            CastleSiegeEvent siegeEventPk = pk.getEvent(CastleSiegeEvent.class);
            if (siegeEvent != null && (siegeEvent == siegeEventPk)) {
                pk.getClan().incSiegeKills();
                if (((siegeEventPk.getSiegeClan("defenders", pk.getClan()) != siegeEvent.getSiegeClan("attackers", getClan())) || (siegeEventPk.getSiegeClan("attackers", pk.getClan()) != siegeEvent.getSiegeClan("defenders", getClan()))) && (pk.getClan().getReputationScore() > 0) && (clan.getLevel() >= 5) && (clan.getReputationScore() > 0) && (pk.getClan().getLevel() >= 5)) {
                    clan.broadcastToOtherOnlineMembers(new SystemMessage(1782).addString(getName()).addNumber(-clan.incReputation(-repValue, true, "ClanWar")), this);
                    pk.getClan().broadcastToOtherOnlineMembers(new SystemMessage(1783).addNumber(pk.getClan().incReputation(repValue, true, "ClanWar")), pk);
                }
            }
            DominionSiegeEvent dominionEvent = getEvent(DominionSiegeEvent.class);
            DominionSiegeEvent dominionEventPk = pk.getEvent(DominionSiegeEvent.class);
            if ((dominionEvent != null) && (dominionEventPk != null)) {
                pk.getClan().incSiegeKills();
            }
            FortressSiegeEvent fsiegeEvent = getEvent(FortressSiegeEvent.class);
            FortressSiegeEvent fsiegeEventPk = pk.getEvent(FortressSiegeEvent.class);
            if ((fsiegeEvent != null) && (fsiegeEvent == fsiegeEventPk) && (pk.getClan() != null) && (clan != null) && ((fsiegeEventPk.getSiegeClan("defenders", pk.getClan()) != fsiegeEvent.getSiegeClan("attackers", getClan())) || (fsiegeEventPk.getSiegeClan("attackers", pk.getClan()) != fsiegeEvent.getSiegeClan("defenders", getClan()))) && (pk.getClan().getReputationScore() > 0) && (clan.getLevel() >= 5) && (clan.getReputationScore() > 0) && (pk.getClan().getLevel() >= 5)) {
                clan.broadcastToOtherOnlineMembers(new SystemMessage(1782).addString(getName()).addNumber(-clan.incReputation(-repValue, true, "ClanWar")), this);
                pk.getClan().broadcastToOtherOnlineMembers(new SystemMessage(1783).addNumber(pk.getClan().incReputation(repValue, true, "ClanWar")), pk);
            }
            ClanHallSiegeEvent chsiegeEvent = getEvent(ClanHallSiegeEvent.class);
            ClanHallSiegeEvent chsiegeEventPk = pk.getEvent(ClanHallSiegeEvent.class);
            if ((chsiegeEvent != null) && (chsiegeEvent == chsiegeEventPk) && ((chsiegeEventPk.getSiegeClan("defenders", pk.getClan()) != chsiegeEvent.getSiegeClan("attackers", getClan())) || (chsiegeEventPk.getSiegeClan("attackers", pk.getClan()) != chsiegeEvent.getSiegeClan("defenders", getClan()))) && (pk.getClan().getReputationScore() > 0) && (clan.getLevel() >= 5) && (clan.getReputationScore() > 0) && (pk.getClan().getLevel() >= 5)) {
                clan.broadcastToOtherOnlineMembers(new SystemMessage(1782).addString(getName()).addNumber(-clan.incReputation(-repValue, true, "ClanWar")), this);
                pk.getClan().broadcastToOtherOnlineMembers(new SystemMessage(1783).addNumber(pk.getClan().incReputation(repValue, true, "ClanWar")), pk);
            }
            if ((isOnSiegeField()) && (!Config.SIEGE_PVP_COUNT)) {
                // Alexander - Add the siege kill to the stats
//				if (killer.isPlayer())
//				{
//					addPlayerStats(Ranking.STAT_TOP_SIEGE_DEATHS);
//
//					killer.getPlayer().addPlayerStats(Ranking.STAT_TOP_SIEGE_KILLS);
//				}
                return;
            }
            if ((pvpFlag > 0) || (war) || (Config.SIEGE_PVP_COUNT) || (Config.ZONE_PVP_COUNT) || isInZonePvP()) {
                if (Config.SERVICES_PK_PVP_KILL_ENABLE) {
                    if (Config.SERVICES_PK_PVP_TIE_IF_SAME_IP) {
                        ItemFunctions.addItem(pk, Config.SERVICES_PVP_KILL_REWARD_ITEM, Config.SERVICES_PVP_KILL_REWARD_COUNT, "PvP");
                    } else {
                        ItemFunctions.addItem(pk, Config.SERVICES_PVP_KILL_REWARD_ITEM, Config.SERVICES_PVP_KILL_REWARD_COUNT, "PvP");
                    }
                }
                pk.setPvpKills(pk.getPvpKills() + 1);

                // Alexander - Add the pvp kill to the stats
//				if (killer.isPlayer())
//				{
//					addPlayerStats(Ranking.STAT_TOP_PVP_DEATHS);
//
//					killer.getPlayer().addPlayerStats(Ranking.STAT_TOP_PVP_KILLS);
//
//					// Alexander - If the getPlayer has a clan, then we must add a new pvp kill to the clan stats
//					if (killer.getPlayer().getClan() != null)
//						killer.getPlayer().getClan().getStats().addClanStats(Ranking.STAT_TOP_CLAN_PVP_KILLS);
//				}
            } else {
                doKillInPeace(pk);

                // Alexander - Add the pk kill to the stats
//				if (killer.isPlayer())
//				{
//					addPlayerStats(Ranking.STAT_TOP_PK_DEATHS);
//
//					killer.getPlayer().addPlayerStats(Ranking.STAT_TOP_PK_KILLS);
//				}
            }

            // Achievement system, increase pvp kills! Not sure if here is the place...
            if (getCounters().pvpKills < getPvpKills())
                getCounters().pvpKills = getPvpKills();

            pk.sendChanges();
        }

        int karma = this.karma;
        decreaseKarma(Config.KARMA_LOST_BASE);

        // under normal conditions, things are lost with the death of the guard tower or getPlayer
        // In addition, the loss of viola at things smetri can lose things in the monster smteri
        boolean isPvP = killer instanceof Playable || (killer instanceof GuardInstance);

        if ((killer instanceof MonsterInstance && !Config.DROP_ITEMS_ON_DIE // if you kill the monster and viola off
        ) || (isPvP // if you kill a getPlayer or the Guard and
                && ((_pkKills < Config.MIN_PK_TO_ITEMS_DROP // number of PCs too little
        ) || ((karma == 0) && Config.KARMA_NEEDED_TO_DROP)) // karma is not
        ) || isFestivalParticipant() // the festival things are not lost
                || (!(killer instanceof MonsterInstance) && !isPvP)) {
            return;
        }

        // No drop from GM's
        if (!Config.KARMA_DROP_GM && isGM()) {
            return;
        }

        final int max_drop_count = isPvP ? Config.KARMA_DROP_ITEM_LIMIT : 1;

        double dropRate; // base percentage chance
        if (isPvP) {
            dropRate = (_pkKills * Config.KARMA_DROPCHANCE_MOD) + Config.KARMA_DROPCHANCE_BASE;
        } else {
            dropRate = Config.NORMAL_DROPCHANCE_BASE;
        }

        int dropEquipCount = 0, dropWeaponCount = 0, dropItemCount = 0;

        for (int i = 0; (i < Math.ceil(dropRate / 100)) && (i < max_drop_count); i++) {
            if (Rnd.chance(dropRate)) {
                int rand = Rnd.get(Config.DROPCHANCE_EQUIPPED_WEAPON + Config.DROPCHANCE_EQUIPMENT + Config.DROPCHANCE_ITEM) + 1;
                if (rand > (Config.DROPCHANCE_EQUIPPED_WEAPON + Config.DROPCHANCE_EQUIPMENT)) {
                    dropItemCount++;
                } else if (rand > Config.DROPCHANCE_EQUIPPED_WEAPON) {
                    dropEquipCount++;
                } else {
                    dropWeaponCount++;
                }
            }
        }

        List<ItemInstance> drop = new ArrayList<>(), // total array with the results of the choice
                dropItem = new ArrayList<>(), dropEquip = new ArrayList<>(), dropWeapon = new ArrayList<>();

        getInventory().writeLock();
        try {
            for (ItemInstance item : getInventory().getItems()) {
                if (!item.canBeDropped(this, true) || Config.KARMA_LIST_NONDROPPABLE_ITEMS.contains(item.getItemId())) {
                    continue;
                }

                if (item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON) {
                    dropWeapon.add(item);
                } else if ((item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR) || (item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY)) {
                    dropEquip.add(item);
                } else if (item.getTemplate().getType2() == ItemTemplate.TYPE2_OTHER) {
                    dropItem.add(item);
                }
            }

            checkAddItemToDrop(drop, dropWeapon, dropWeaponCount);
            checkAddItemToDrop(drop, dropEquip, dropEquipCount);
            checkAddItemToDrop(drop, dropItem, dropItemCount);

            // Dropping items, if present
            if (drop.isEmpty()) {
                return;
            }

            for (ItemInstance item : drop) {
                if (item.isAugmented() && !Config.ALT_ALLOW_DROP_AUGMENTED) {
                    item.setAugmentationId(0);
                }

                item = inventory.removeItem(item, "Karma Drop");

                if (item.getEnchantLevel() > 0) {
                    sendPacket(new SystemMessage(SystemMessage.DROPPED__S1_S2).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()));
                } else {
                    sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_DROPPED_S1).addItemName(item.getItemId()));
                }

                if (killer instanceof Playable && isInFlyingTransform()) {
                    killer.getPlayer().inventory.addItem(item, Log.Pickup);

                    killer.getPlayer().sendPacket(SystemMessage2.obtainItems(item));
                } else {
                    item.dropToTheGround(this, Location.findAroundPosition(this, Config.KARMA_RANDOM_DROP_LOCATION_LIMIT));
                }
            }

            ItemLogHandler.INSTANCE.addLog(this, drop, ItemActionType.DROPPED_BY_KARMA);
        } finally {
            getInventory().writeUnlock();
        }
    }

    @Override
    protected void onDeath(Creature killer) {
        // Check for active charm of luck for death penalty
        getDeathPenalty().checkCharmOfLuck();

        if (isInStoreMode()) {
            setPrivateStoreType(Player.STORE_PRIVATE_NONE);
        }
        if (isProcessingRequest()) {
            Request request = getRequest();
            if (isInTrade()) {
                Player parthner = request.getOtherPlayer(this);
                sendPacket(SendTradeDone.FAIL);
                parthner.sendPacket(SendTradeDone.FAIL);
            }
            request.cancel();
        }

        if (cubics != null) {
            getEffectList().stopAllSkillEffects(EffectType.Cubic);
        }

        setAgathion(0);

        boolean checkPvp = true;
        if (Config.ALLOW_CURSED_WEAPONS) {
            if (isCursedWeaponEquipped()) {
                CursedWeaponsManager.INSTANCE.dropPlayer(this);
                checkPvp = false;
            } else if (killer instanceof Player && ((Player) killer).isCursedWeaponEquipped()) {
                CursedWeaponsManager.INSTANCE.increaseKills(((Player) killer).getCursedWeaponEquippedId());
                checkPvp = false;
            }
        }

        if (checkPvp) {
            doPKPVPManage(killer);

            altDeathPenalty(killer);
        }

        // And in the end of process notify death penalty that owner died :)
        getDeathPenalty().notifyDead(killer);

        setIncreasedForce(0);

        if (isInParty() && getParty().isInReflection() && (getParty().getReflection() instanceof DimensionalRift)) {
            ((DimensionalRift) getParty().getReflection()).memberDead();
        }

        stopWaterTask();

        if (!isSalvation() && isOnSiegeField() && isCharmOfCourage()) {
            ask(new ConfirmDlg(SystemMsg.YOUR_CHARM_OF_COURAGE_IS_TRYING_TO_RESURRECT_YOU, 60000), new ReviveAnswerListener(this, 100, false));
            setCharmOfCourage(false);
        }

        if (getLevel() < 6) {
            Quest q = QuestManager.getQuest(_255_Tutorial.class);
            processQuestEvent(q, "CE30", null);
        }

        if (isInOlympiadMode() && (killer instanceof Playable && killer.getPlayer().isInOlympiadMode())) {
            LOG.warn("Player: " + getName() + " DIED in olympiad from: " + killer.getName());
            Thread.dumpStack();
        }

        // Ady - Call the gm event manager due to this death
        GmEventManager.INSTANCE.onPlayerKill(this, killer);

        super.onDeath(killer);
    }

    public void restoreExp() {
        restoreExp(100.);
    }

    public void restoreExp(double percent) {
        if (percent == 0) {
            return;
        }

        unsetVar("lostexp");

        addExpAndSp((long) ((getVarInt("lostexp") * percent) / 100), 0);
    }

    private void deathPenalty(Creature killer) {
        if (killer == null) {
            return;
        }
        final boolean atwar = (killer instanceof Playable) && atWarWith(killer.getPlayer());

        double deathPenaltyBonus = getDeathPenalty().getLevel() * Config.ALT_DEATH_PENALTY_C5_EXPERIENCE_PENALTY;
        if (deathPenaltyBonus < 2) {
            deathPenaltyBonus = 1;
        } else {
            deathPenaltyBonus = deathPenaltyBonus / 2;
        }

        // The death steal you some Exp: 10-40 lvl 8% loose
        double percentLost = 8.0;

        int level = getLevel();
        if (level >= 79) {
            percentLost = 1.0;
        } else if (level >= 78) {
            percentLost = 1.5;
        } else if (level >= 76) {
            percentLost = 2.0;
        } else if (level >= 40) {
            percentLost = 4.0;
        }

        if (Config.ALT_DEATH_PENALTY) {
            percentLost = (percentLost * Config.RATE_XP) + (_pkKills * Config.ALT_PK_DEATH_RATE);
        }

        if (isFestivalParticipant() || atwar) {
            percentLost = percentLost / 4.0;
        }

        // Calculate the Experience loss
        int lostexp = (int) Math.round(((Experience.LEVEL[level + 1] - Experience.LEVEL[level]) * percentLost) / 100);
        lostexp *= deathPenaltyBonus;

        lostexp = (int) calcStat(Stats.EXP_LOST, lostexp, killer, null);

        if (isOnSiegeField()) {
            SiegeEvent<?, ?> siegeEvent = getEvent(SiegeEvent.class);
            if (siegeEvent != null) {
                lostexp = 0;
            }

            if (siegeEvent != null) {
                Optional<Effect> effects = getEffectList().getEffectsBySkillId(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME).findFirst();
                if (effects.isPresent()) {
                    int syndromeLvl = effects.get().skill.level;
                    if (syndromeLvl < 5) {
                        getEffectList().stopEffect(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
                        Skill skill = SkillTable.INSTANCE.getInfo(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME, syndromeLvl + 1);
                        skill.getEffects(this);
                    } else if (syndromeLvl == 5) {
                        getEffectList().stopEffect(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
                        SkillTable.INSTANCE.getInfo(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME, 5).getEffects(this);
                    }
                } else {
                    SkillTable.INSTANCE.getInfo(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME).getEffects(this);
                }
            }
        }

        if (getNevitSystem().isBlessingActive()) {
            return;
        }

        long before = getExp();
        addExpAndSp(-lostexp, 0);
        long lost = before - getExp();

        if (lost > 0) {
            setVar("lostexp", lost);
        }
    }

    public Request getRequest() {
        return _request;
    }

    public void setRequest(Request transaction) {
        _request = transaction;
    }

    public boolean isBusy() {
        return isProcessingRequest()
                || isOutOfControl()
                || isInOlympiadMode()
                || (getTeam() != TeamType.NONE)
                || isInStoreMode()
                || isInDuel()
                || getMessageRefusal()
                || isBlockAll()
                || isInvisible();
    }

    public boolean isProcessingRequest() {
        if (_request == null) {
            return false;
        }
        return _request.isInProgress();
    }

    public boolean isInTrade() {
        return isProcessingRequest() && getRequest().isTypeOf(L2RequestType.TRADE);
    }

    List<L2GameServerPacket> addVisibleObject(GameObject object, Creature dropper) {
        if (isLogoutStarted() || (object == null) || (object.objectId() == objectId()) || !object.isVisible()) {
            return Collections.emptyList();
        }

        return object.addPacketList(this, dropper);
    }

    @Override
    public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
        if (getPrivateStoreType() != STORE_PRIVATE_NONE && !isInBuffStore() && forPlayer.isVarSet("notraders")) {
            return Collections.emptyList();
        }

        // If this is fake an Observer - do not show.
        if (isInObserverMode() && (getCurrentRegion() != getObserverRegion()) && (getObserverRegion() == forPlayer.getCurrentRegion())) {
            return Collections.emptyList();
        }

        List<L2GameServerPacket> list = new ArrayList<>();
        if (forPlayer.objectId() != objectId()) {
            list.add(isPolymorphed() ? new NpcInfoPoly(this) : new CharInfo(this));
        }

        list.add(new ExBR_ExtraUserInfo(this));

        if (isSitting() && (sittingObject != null)) {
            list.add(new ChairSit(this, sittingObject));
        }

        if (getPrivateStoreType() != STORE_PRIVATE_NONE) {
            if (getPrivateStoreType() == STORE_PRIVATE_BUY) {
                list.add(new PrivateStoreMsgBuy(this));
            } else if ((getPrivateStoreType() == STORE_PRIVATE_SELL) || (getPrivateStoreType() == STORE_PRIVATE_SELL_PACKAGE)) {
                list.add(new PrivateStoreMsgSell(this));
            } else if (getPrivateStoreType() == STORE_PRIVATE_MANUFACTURE) {
                list.add(new RecipeShopMsg(this));
            }
            if (forPlayer.isInZonePeace()) {
                return list;
            }
        }

        if (isCastingNow()) {
            Creature castingTarget = getCastingTarget();
            Skill castingSkill = getCastingSkill();
            long animationEndTime = getAnimationEndTime();
            if ((castingSkill != null) && castingTarget != null && (getAnimationEndTime() > 0)) {
                list.add(new MagicSkillUse(this, castingTarget, castingSkill.id, castingSkill.level, (int) (animationEndTime - System.currentTimeMillis()), 0));
            }
        }

        if (isInCombat()) {
            list.add(new AutoAttackStart(objectId()));
        }

        list.add(RelationChanged.update(forPlayer, this, forPlayer));
        DominionSiegeEvent dominionSiegeEvent = getEvent(DominionSiegeEvent.class);
        if (dominionSiegeEvent != null) {
            list.add(new ExDominionWarStart(this));
        }

        if (isInBoat()) {
            list.add(getBoat().getOnPacket(this, getInBoatPosition()));
        } else {
            if (isMoving || isFollow) {
                list.add(movePacket());
            }
        }
        return list;
    }

    List<L2GameServerPacket> removeVisibleObject(GameObject object, List<L2GameServerPacket> list) {
        if (isLogoutStarted() || (object == null) || (object.objectId() == objectId())) {
            return null;
        }

        List<L2GameServerPacket> result = list == null ? object.deletePacketList() : list;

        getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, object);
        return result;
    }

    public void levelSet(int levels) {
        if (levels > 0) {
            sendPacket(Msg.YOU_HAVE_INCREASED_YOUR_LEVEL);
            broadcastPacket(new SocialAction(objectId(), SocialAction.LEVEL_UP));

            setFullHpMp();
            setFullCp();

            Quest q = QuestManager.getQuest(_255_Tutorial.class);
            processQuestEvent(q, "CE40", null);
            processQuestEvent(q, "OpenClassMaster", null);
        } else if (levels < 0) {
            if (Config.ALT_REMOVE_SKILLS_ON_DELEVEL) {
                checkSkills();
            }
        }

        // Recalculate the party occupation
        if (isInParty()) {
            getParty().recalculatePartyData();
        }

        if (clan != null) {
            clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(this));
        }

        if (_matchingRoom != null) {
            _matchingRoom.broadcastPlayerUpdate(this);
        }

        // Give Expertise skill of this occupation
        rewardSkills(true);
    }

    public void checkSkills() {
        getAllSkills().forEach(sk ->
                SkillTreeTable.checkSkill(this, sk));
    }

    public void startTimers() {
        startAutoSaveTask();
        startAutoChargeTask();
        startPcBangPointsTask();
        startHourlyTask();
        //startBonusTask();
        resumeQuestTimers();
    }

    private void stopAllTimers() {
        setAgathion(0);
        stopWaterTask();
        stopHourlyTask();
        stopKickTask();
        stopVitalityTask();
        stopPcBangPointsTask();
        stopAutoSaveTask();
        stopRecomBonusTask(true);
        getInventory().stopAllTimers();
        stopQuestTimers();
        getNevitSystem().stopTasksOnLogout();
    }

    public Summon getPet() {
        return summon;
    }

    public void setPet(Summon s) {
        boolean isPet = false;
        if (summon != null && summon instanceof PetInstance) {
            isPet = true;
        }
        unsetVar("pet");
        this.summon = s;
        autoShot();
        if (s == null) {
            if (isPet) {
                if (isLogoutStarted()) {
                    if (getPetControlItem() != null) {
                        setVar("pet", getPetControlItem().objectId());
                    }
                }
                setPetControlItem(null);
            }
            getEffectList().stopEffect(4140);
        }
    }

    public void scheduleDelete() {
        long time = 0;

        if (Config.SERVICES_ENABLE_NO_CARRIER && !isOnSiegeField()) {
            time = 180;
            setNonAggroTime(System.currentTimeMillis() + time * 1000L);
            setInvul(true);
        }
        scheduleDelete(time * 1000);
    }

    /**
     * Removes the character of the world in the specified time, if at the time of the expiry of the time it will not be connected. <br>
     * <br>
     * a minute to make him invulnerable. <br>
     * make a binding time to the context for areas with a time limit to leave the game on all the time in the zone. <br>
     * <br>
     */
    private void scheduleDelete(long time) {
        broadcastCharInfo();

        ThreadPoolManager.INSTANCE.schedule(() -> {
            if (!isConnected()) {
                prepareToLogout();
                deleteMe();
            }
        }, time);
    }

    @Override
    protected void onDelete() {
        super.onDelete();

        // Remove the fake at the observation point
        WorldRegion observerRegion = getObserverRegion();
        if (observerRegion != null) {
            observerRegion.removeObject(this);
        }

        // Send friendlists to friends that this getPlayer has logged off
        friendList.notifyFriends(false);

        bookmarks.clear();

        inventory.clear();
        warehouse.clear();
        summon = null;
        _arrowItem = null;
        _fistsWeaponItem = null;
        _chars = null;
        enchantScroll = null;
        lastNpc = null;
        _observerRegion = null;
    }

    public List<TradeItem> getTradeList() {
        return _tradeList;
    }

    public void setTradeList(List<TradeItem> list) {
        _tradeList = list;
    }

    public String getSellStoreName() {
        return _sellStoreName;
    }

    public void setSellStoreName(String name) {
        _sellStoreName = Strings.stripToSingleLine(name);
    }

    public void setSellList(boolean packageSell, List<TradeItem> list) {
        if (packageSell) {
            packageSellList = list;
        } else {
            sellList = list;
        }
    }

    public List<TradeItem> getSellList() {
        return getSellList(privatestore == STORE_PRIVATE_SELL_PACKAGE);
    }

    public List<TradeItem> getSellList(boolean packageSell) {
        return packageSell ? packageSellList : sellList;
    }

    public String getBuyStoreName() {
        return _buyStoreName;
    }

    public void setBuyStoreName(String name) {
        _buyStoreName = Strings.stripToSingleLine(name);
    }

    public List<TradeItem> getBuyList() {
        return _buyList;
    }

    public void setBuyList(List<TradeItem> list) {
        _buyList = list;
    }

    public String getManufactureName() {
        return manufactureName;
    }

    public void setManufactureName(String name) {
        manufactureName = Strings.stripToSingleLine(name);
    }

    public List<ManufactureItem> getCreateList() {
        return createList;
    }

    public void setCreateList(List<ManufactureItem> list) {
        createList = list;
    }

    public boolean isInStoreMode() {
        return privatestore != STORE_PRIVATE_NONE;
    }

    public boolean isInBuffStore() {
        return (getPrivateStoreType() == STORE_PRIVATE_BUFF);
    }

    public int getPrivateStoreType() {
        return privatestore;
    }

    public void setPrivateStoreType(final int type) {
        privatestore = type;
        if (type != STORE_PRIVATE_NONE) {
            setVar("storemode", type);
        } else {
            sellList.forEach(item ->
                    AuctionManager.getInstance().removeStore(this, item.getAuctionId()));
            unsetVar("storemode");
        }
    }


    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        if ((this.clan != clan) && (this.clan != null)) {
            unsetVar("canWhWithdraw");
        }

        Clan oldClan = this.clan;
        if ((oldClan != null) && (clan == null)) {
            // Remove clan skills
            oldClan.getSkills().forEach(skill ->
                    removeSkill(skill.id, false));

            // Also remove subunit skills
            oldClan.getAllSubUnits().forEach(su -> su.getSkills().stream()
                    .mapToInt(s -> s.id)
                    .forEach(sk -> removeSkill(sk, false)));
        }

        this.clan = clan;

        if (clan == null) {
            pledgeType = Clan.SUBUNIT_NONE;
            _pledgeClass = 0;
            _powerGrade = 0;
            apprentice = 0;
            inventory.validateItems();

            if (getEvent(CastleSiegeEvent.class) != null)
                removeEvent(getEvent(CastleSiegeEvent.class));
            return;
        }

        if (!clan.isAnyMember(objectId)) {
            clan.restartMembers();
        }

        if (!clan.isAnyMember(objectId)) {
            setClan(null);
            if (!noble) setTitle("");
        }
    }

    public SubUnit getSubUnit() {
        return clan == null ? null : clan.getSubUnit(pledgeType);
    }

    public ClanHall getClanHall() {
        int id = clan != null ? clan.getHasHideout() : 0;
        return ResidenceHolder.getResidence(ClanHall.class, id);
    }

    public Castle getCastle() {
        int id = clan != null ? clan.getCastle() : 0;
        return ResidenceHolder.getResidence(Castle.class, id);
    }

    public Fortress getFortress() {
        int id = clan != null ? clan.getHasFortress() : 0;
        return ResidenceHolder.getResidence(Fortress.class, id);
    }

    public Alliance getAlliance() {
        return clan == null ? null : clan.getAlliance();
    }

    public boolean isClanLeader() {
        return (clan != null) && (objectId() == clan.getLeaderId());
    }

    public boolean isAllyLeader() {
        return (getAlliance() != null) && (getAlliance().getLeader().getLeaderId() == objectId());
    }

    @Override
    public void reduceArrowCount() {
        sendPacket(SystemMsg.YOU_CAREFULLY_NOCK_AN_ARROW);
        if ((_arrowItem != null) && !Config.ALLOW_ARROW_INFINITELY) {
            if (!getInventory().destroyItemByObjectId(getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND), 1L, null, null)) {
                getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, null);
                _arrowItem = null;
            }
        }
    }

    /**
     * Equip arrows needed in left hand and send a Server->Client packet ItemList to the L2Player then return True.
     */
    boolean checkAndEquipArrows() {
        // Check if nothing is equipped in left hand
        if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND) == null) {
            ItemInstance activeWeapon = getActiveWeaponInstance();
            if (activeWeapon != null) {
                if (activeWeapon.getItemType() == WeaponType.BOW) {
                    _arrowItem = getInventory().findArrowForBow(activeWeapon.getTemplate());
                } else if (activeWeapon.getItemType() == WeaponType.CROSSBOW) {
                    getInventory().findArrowForCrossbow(activeWeapon.getTemplate());
                }
            }

            // Equip arrows needed in left hand
            if (_arrowItem != null) {
                getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, _arrowItem);
            }
        } else {
            // Get the L2ItemInstance of arrows equipped in left hand
            _arrowItem = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
        }

        return _arrowItem != null;
    }

    private long getUptime() {
        return _uptime == 0L ? 0L : System.currentTimeMillis() - _uptime;
    }

    public void setUptime(final long time) {
        _uptime = time;
    }

    public boolean isInParty() {
        return party != null;
    }

    public void joinParty(final Party party) {
        if (party != null) {
            party.addPartyMember(this);
            party.getMembers().stream()
                    .filter(member -> PartyMatchingBBSManager.getInstance().partyMatchingPlayersList.contains(member))
                    .forEach(member -> {
                        PartyMatchingBBSManager.getInstance().partyMatchingPlayersList.remove(member);
                        PartyMatchingBBSManager.getInstance().partyMatchingDescriptionList.remove(member.objectId());
                        member.sendMessage("Now that you have found a party, you've been removed from the Party Matching list.");
                    });
        }
    }

    public void leaveParty() {
        if (isInParty()) {
            party.removePartyMember(this, false);
        }
    }

    public Party getParty() {
        return party;
    }

    public void setParty(final Party party) {
        this.party = party;
    }


    public Location getLastPartyPosition() {
        return _lastPartyPosition;
    }

    public void setLastPartyPosition(Location loc) {
        _lastPartyPosition = loc;
    }

    public boolean isGM() {
        return playerAccess.IsGM;
    }

    @Override
    public int getAccessLevel() {
        return _accessLevel;
    }

    public void setAccessLevel(final int level) {
        _accessLevel = level;
    }

    public PlayerAccess getPlayerAccess() {
        return playerAccess;
    }

    public void setPlayerAccess(final PlayerAccess pa) {
        playerAccess = Objects.requireNonNullElseGet(pa, PlayerAccess::new);

        setAccessLevel(isGM() || playerAccess.Menu ? 100 : 0);
    }

    @Override
    public double getLevelMod() {
        return (89. + getLevel()) / 100.0;
    }

    /**
     * Update Stats of the Player client side by sending Server->Client packet UserInfo/StatusUpdate to this L2Player and CharInfo/StatusUpdate to all players around (broadcast).<BR>
     * <BR>
     */
    @Override
    public void updateStats() {
        if (entering || isLogoutStarted()) {
            return;
        }

        refreshOverloaded();
        if (Config.EXPERTISE_PENALTY) {
            refreshExpertisePenalty();
        }
        super.updateStats();
    }

    @Override
    public void addStatFunc(Func f) {
        super.addStatFunc(f);
    }

    @Override
    public void sendChanges() {
        if (entering || isLogoutStarted()) {
            return;
        }
        super.sendChanges();
    }

    /**
     * Send a Server->Client StatusUpdate packet with Karma to the L2Player and all L2Player to inform (broadcast).
     */
    private void updateKarma(boolean flagChanged) {
        sendStatusUpdate(true, true, StatusUpdate.KARMA);
        if (flagChanged)
            broadcastRelationChanged();
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public void setOnlineStatus(boolean isOnline) {
        this.isOnline = isOnline;
        updateOnlineStatus();
    }

    public void updateOnlineStatus() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE characters SET online=?, lastAccess=? WHERE obj_id=?")) {
            statement.setInt(1, (isOnline() || Config.SHOW_OFFLINE_MODE_IN_ONLINE) ? 1 : 0);
            statement.setLong(2, System.currentTimeMillis() / 1000L);
            statement.setInt(3, objectId());
            statement.execute();
        } catch (SQLException e) {
            LOG.error("Error while updating Online Status", e);
        }
    }

    /**
     * Decrease Karma of the L2Player and Send it StatusUpdate packet with Karma and PvP Flag (broadcast).
     */
    void increaseKarma(final long add_karma) {
        boolean flagChanged = karma == 0;
        long newKarma = karma + add_karma;

        if (newKarma > Integer.MAX_VALUE) {
            newKarma = Integer.MAX_VALUE;
        }

        if ((karma == 0) && (newKarma > 0)) {
            if (pvpFlag > 0) {
                pvpFlag = 0;
                if (_PvPRegTask != null) {
                    _PvPRegTask.cancel(true);
                    _PvPRegTask = null;
                }
                sendStatusUpdate(true, true, StatusUpdate.PVP_FLAG);
            }

            karma = (int) newKarma;
        } else {
            karma = (int) newKarma;
        }

        if (getCounters().highestKarma < newKarma)
            getCounters().highestKarma = (int) newKarma;

        updateKarma(flagChanged);
    }

    /**
     * Decrease Karma of the L2Player and Send it StatusUpdate packet with Karma and PvP Flag (broadcast).
     */
    private void decreaseKarma(final int i) {
        boolean flagChanged = karma > 0;
        karma -= i;
        if (karma <= 0) {
            karma = 0;
            updateKarma(flagChanged);
        } else {
            updateKarma(false);
        }
    }

    private void loadPremiumItemList(Connection con) {
        try (PreparedStatement statement = con.prepareStatement("SELECT itemNum, itemId, itemCount, itemSender FROM character_premium_items WHERE charId=?")) {
            statement.setInt(1, objectId());

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int itemNum = rs.getInt("itemNum");
                    int itemId = rs.getInt("itemId");
                    long itemCount = rs.getLong("itemCount");
                    String itemSender = rs.getString("itemSender");
                    PremiumItem item = new PremiumItem(itemId, itemCount, itemSender);
                    premiumItems.put(itemNum, item);
                }
            }
        } catch (SQLException e) {
            LOG.error("Error while loading Premium Item List for Id " + objectId(), e);
        }
    }

    public void updatePremiumItem(int itemNum, long newcount) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE character_premium_items SET itemCount=? WHERE charId=? AND itemNum=?")) {
            statement.setLong(1, newcount);
            statement.setInt(2, objectId());
            statement.setInt(3, itemNum);
            statement.execute();
        } catch (SQLException e) {
            LOG.error("Error while updating Premium Items", e);
        }
    }

    public void deletePremiumItem(int itemNum) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("DELETE FROM character_premium_items WHERE charId=? AND itemNum=?")) {
            statement.setInt(1, objectId());
            statement.setInt(2, itemNum);
            statement.execute();
        } catch (SQLException e) {
            LOG.error("Error while deleting Premium Item", e);
        }
    }

    public Map<Integer, PremiumItem> getPremiumItemList() {
        return premiumItems;
    }

    /**
     * Update L2Player stats in the characters table of the database.
     */
    public void store(boolean fast) {
        if (!storeLock.tryLock()) {
            return;
        }

        try {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement(//
                         "UPDATE characters SET face=?,hairStyle=?,hairColor=?,x=?,y=?,z=?" + //
                                 ",karma=?,pvpkills=?,pkkills=?,rec_have=?,rec_left=?,rec_bonus_time=?,hunt_points=?,hunt_time=?,clanid=?,deletetime=?," + //
                                 "title=?,accesslevel=?,online=?,leaveclan=?,deleteclan=?,nochannel=?," + //
                                 "onlinetime=?,pledge_type=?,pledge_rank=?,lvl_joined_academy=?,apprentice=?,key_bindings=?,pcBangPoints=?,char_name=?,vitality=?,fame=?,bookmarks=?,hwid_lock=?,raidkills=?,soloinstance=?,partyinstance=? WHERE obj_Id=? LIMIT 1")) {
                statement.setInt(1, getFace());
                statement.setInt(2, getHairStyle());
                statement.setInt(3, getHairColor());
                if (stablePoint == null) {
                    statement.setInt(4, getX());
                    statement.setInt(5, getY());
                    statement.setInt(6, getZ());
                } else {
                    statement.setInt(4, stablePoint.x);
                    statement.setInt(5, stablePoint.y);
                    statement.setInt(6, stablePoint.z);
                }
                statement.setInt(7, getKarma());
                statement.setInt(8, getPvpKills());
                statement.setInt(9, getPkKills());
                statement.setInt(10, getRecomHave());
                statement.setInt(11, getRecomLeft());
                statement.setInt(12, getRecomBonusTime());
                statement.setInt(13, getNevitSystem().getPoints());
                statement.setInt(14, getNevitSystem().getTime());
                statement.setInt(15, getClanId());
                statement.setInt(16, getDeleteTimer());
                statement.setString(17, title);
                statement.setInt(18, _accessLevel);
                statement.setInt(19, isOnline() ? 1 : 0);
                statement.setLong(20, getLeaveClanTime() / 1000L);
                statement.setLong(21, getDeleteClanTime() / 1000L);
                statement.setLong(22, _NoChannel > 0 ? getNoChannelRemained() / 1000 : _NoChannel);
                statement.setInt(23, (int) (getOnlineTime() / 1000L));
                statement.setInt(24, getPledgeType());
                statement.setInt(25, getPowerGrade());
                statement.setInt(26, getLvlJoinedAcademy());
                statement.setInt(27, getApprentice());
                statement.setBytes(28, getKeyBindings());
                statement.setInt(29, getPcBangPoints());
                statement.setString(30, getName());
                statement.setInt(31, (int) getVitality());
                statement.setInt(32, getFame());
                statement.setInt(33, bookmarks.getCapacity());
                statement.setString(34, "");
                statement.setInt(35, getRaidKills());
                statement.setInt(36, soloInstance);
                statement.setInt(37, getPartyInstance());
                statement.setInt(38, objectId());

                statement.executeUpdate();
                if (Config.RATE_DROP_ADENA < 20) {
                    GameStats.increaseUpdatePlayerBase();
                }

                if (!fast) {
                    EffectsDAO.INSTANCE.insert(this);
                    CharacterGroupReuseDAO.getInstance().insert(this);
                    storeDisableSkills();
                    storeBlockList();
                }

                storeCharSubClasses();
                bookmarks.store();

                if (Config.ENABLE_ACHIEVEMENTS)
                    saveAchivements();

            } catch (SQLException e) {
                LOG.error("Could not store char data: " + this + '!', e);
            }
        } finally {
            storeLock.unlock();
        }
    }

    public void updateRaidKills() {
        this.raids++;
    }

    private int getRaidKills() {
        return raids;
    }

    public void addSkill(int skillId, int skillLvl, final boolean store) {
        addSkill(SkillTable.INSTANCE.getInfo(skillId, skillLvl), store);
    }

    public void addSkill(int skillId, final boolean store) {
        addSkill(skillId, 1, store);
    }

    public void addSkill(final Skill newSkill, final boolean store) {
        if (newSkill == null) return;

        // Fix If the skill existed before, then we must transfer its reuse to the new level. Its a known exploit of enchant a skill to reset its reuse
        if (getKnownSkill(newSkill.id) != null) {
            disableSkillByNewLvl(SkillTable.INSTANCE.getInfo(newSkill.id, getKnownSkill(newSkill.id).level).hashCode(),
                    SkillTable.INSTANCE.getInfo(newSkill.id, newSkill.level).hashCode());
        }
        // Add or update a L2Player skill in the character_skills table of the database
        if (store) storeSkill(newSkill);
    }

    /**
     * Remove a skill from the L2Character and its Func objects from calculator set of the L2Character and save update in the character_skills table of the database.
     */
    public Skill removeSkill(int id, boolean fromDB) {
        // Remove a skill from the L2Character and its Func objects from calculator set of the L2Character
        Skill oldSkill = super.removeSkill(id);

        if (!fromDB) {
            return oldSkill;
        }

        if (oldSkill != null) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement("DELETE FROM character_skills WHERE skill_id=? AND char_obj_id=? AND class_index=?")) {
                // Remove or update a L2Player skill from the character_skills table of the database
                statement.setInt(1, oldSkill.id);
                statement.setInt(2, objectId());
                statement.setInt(3, getActiveClassId().id);
                statement.execute();
            } catch (SQLException e) {
                LOG.error("Could not delete skill!", e);
            }
        }
        return oldSkill;
    }

    /**
     * Add or update a L2Player skill in the character_skills table of the database.
     */
    private void storeSkill(final Skill newSkill) {
        if (newSkill == null) {
            LOG.warn("could not store new skill. its NULL");
            return;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("REPLACE INTO character_skills (char_obj_id,skill_id,skill_level,class_index) values(?,?,?,?)")) {
            statement.setInt(1, objectId());
            statement.setInt(2, newSkill.id);
            statement.setInt(3, newSkill.level);
            statement.setInt(4, getActiveClassId().id);
            statement.execute();
        } catch (SQLException e) {
            LOG.error("Error could not store skills!", e);
        }
    }

    /**
     * Retrieve from the database all skills of this L2Player and add them to skills.
     */
    private void restoreSkills() {
        PreparedStatement statement;
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            // Retrieve all skills of this L2Player from the database
            // Send the SQL query : SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=? to the database
            if (Config.ALT_ENABLE_MULTI_PROFA) {
                statement = con.prepareStatement("SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=?");
                statement.setInt(1, objectId());
            } else {
                statement = con.prepareStatement("SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=? AND class_index=?");
                statement.setInt(1, objectId());
                statement.setInt(2, getActiveClassId().id);
            }

            try (ResultSet rset = statement.executeQuery()) {
                // Go though the recordset of this SQL query
                while (rset.next()) {
                    final int id = rset.getInt("skill_id");
                    final int level = rset.getInt("skill_level");

                    // Create a L2Skill object for each record
                    final Skill skill = SkillTable.INSTANCE.getInfo(id, level);

                    if (skill == null) {
                        continue;
                    }

                    // Remove skill if not possible
                    if (!isGM() && !SkillAcquireHolder.isSkillPossible(this, skill)) {
                        // int ReturnSP = SkillTreeTable.INSTANCE().getSkillCost(this, skill);
                        // if (ReturnSP == Integer.MAX_VALUE || ReturnSP < 0)
                        // ReturnSP = 0;
                        removeSkill(id, true);
                        removeSkillFromShortCut(skill.id);
                        // if (ReturnSP > 0)
                        // setSp(getSp() + ReturnSP);
                        continue;
                    }

                    super.addSkill(id, level);
                }
            }

            // Restore noble skills
            if (isNoble()) {
                updateNobleSkills();
            }

            // Restore Hero skills at main class only
            if (hero && (getBaseClassId() == getActiveClassId())) {
                Hero.addSkills(this);
            }

            // Restore clan skills
            if (clan != null) {
                clan.addSkillsQuietly(this);

                // Restore clan leader siege skills
                if ((clan.getLeaderId() == objectId()) && (clan.getLevel() >= 5)) {
                    SiegeUtils.addSiegeSkills(this);
                }
            }

            // Give dwarven craft skill
            if (getActiveClassId().race == Race.dwarf) {
                super.addSkill(1321);
            }

            super.addSkill(1322);

            if (Config.UNSTUCK_SKILL && (getSkillLevel(1050) < 0)) {
                super.addSkill(2099);
            }
        } catch (SQLException e) {
            LOG.warn("Could not restore skills for getPlayer objId: " + objectId(), e);
        }
    }

    private void storeDisableSkills() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement statement = con.createStatement()) {
            statement.executeUpdate("DELETE FROM character_skills_save WHERE char_obj_id = " + objectId() + " AND class_index=" + getActiveClassId() + " AND `end_time` < " + System.currentTimeMillis());

            if (skillReuses.isEmpty()) {
                return;
            }

            SqlBatch b = new SqlBatch("REPLACE INTO `character_skills_save` (`char_obj_id`,`skill_id`,`skill_level`,`class_index`,`end_time`,`reuse_delay_org`) VALUES");
            synchronized (skillReuses) {
                StringBuilder sb;
                for (TimeStamp timeStamp : skillReuses.values()) {
                    if (timeStamp.hasNotPassed()) {
                        sb = new StringBuilder("(");
                        sb.append(objectId()).append(",");
                        sb.append(timeStamp.id).append(",");
                        sb.append(timeStamp.level).append(",");
                        sb.append(getActiveClassId()).append(",");
                        sb.append(timeStamp.endTime()).append(",");
                        sb.append(timeStamp.getReuseBasic()).append(")");
                        b.write(sb.toString());
                    }
                }
            }
            if (!b.isEmpty()) {
                statement.executeUpdate(b.close());
            }
        } catch (final Exception e) {
            LOG.warn("Could not store disable skills data: " + e);
        }
    }

    public void restoreDisableSkills() {
        skillReuses.clear();

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement statement = con.createStatement()) {

            try (ResultSet rset = statement.executeQuery("SELECT skill_id,skill_level,end_time,reuse_delay_org FROM character_skills_save WHERE char_obj_id=" + objectId() + " AND class_index=" + getActiveClassId())) {
                while (rset.next()) {
                    int skillId = rset.getInt("skill_id");
                    int skillLevel = rset.getInt("skill_level");
                    long endTime = rset.getLong("end_time");
                    long rDelayOrg = rset.getLong("reuse_delay_org");
                    long curTime = System.currentTimeMillis();

                    Skill skill = SkillTable.INSTANCE.getInfo(skillId, skillLevel);

                    if ((skill != null) && ((endTime - curTime) > 500)) {
                        skillReuses.put(skill.hashCode(), new TimeStamp(skill, endTime, rDelayOrg));
                    }
                }
            }

            statement.executeUpdate("DELETE FROM character_skills_save WHERE char_obj_id = " + objectId() + " AND class_index=" + getActiveClassId() + " AND `end_time` < " + System.currentTimeMillis());
        } catch (Exception e) {
            LOG.error("Could not restore active skills data!", e);
        }
    }

    /**
     * Retrieve from the database all Henna of this L2Player, add them to henna and calculate stats of the L2Player.<BR>
     * <BR>
     */
    private void restoreHenna() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("select slot, symbol_id from character_hennas where char_obj_id=? AND class_index=?")) {
            statement.setInt(1, objectId());
            statement.setInt(2, getActiveClassId().id);

            try (ResultSet rset = statement.executeQuery()) {
                henna.clear();
                for (int i = 0; i < 3; i++) {
                    henna.add(null);
                }

                while (rset.next()) {
                    final int slot = rset.getInt("slot");
                    if ((slot < 1) || (slot > 3)) {
                        continue;
                    }

                    final int symbol_id = rset.getInt("symbol_id");

                    if (symbol_id != 0) {
                        final Henna tpl = HennaHolder.getHenna(symbol_id);
                        if (tpl != null) {
                            henna.set(slot - 1, tpl);
                        }
                    }
                }
            }
        } catch (final SQLException e) {
            LOG.warn("could not restore henna: " + e);
        }

        // Calculate Henna modifiers of this L2Player
        recalcHennaStats();

    }

    public int getHennaEmptySlots() {
        int totalSlots = 1 + getClassId().occupation();
        for (int i = 0; i < 3; i++) {
            if (henna.get(i) != null) {
                totalSlots--;
            }
        }

        if (totalSlots <= 0) {
            return 0;
        }

        return totalSlots;

    }

    /**
     * Remove a Henna of the L2Player, save update in the character_hennas table of the database and send Server->Client HennaInfo/UserInfo packet to this L2Player.<BR>
     * <BR>
     */
    public void removeHenna(int slot) {
        if ((slot < 1) || (slot > 3)) {
            return;
        }

        slot--;

        if (henna.get(slot) == null) {
            return;
        }

        final Henna henna = this.henna.get(slot);
        final int dyeID = henna.dyeId;

        this.henna.set(slot, null);

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("DELETE FROM character_hennas where char_obj_id=? and slot=? and class_index=?")) {
            statement.setInt(1, objectId());
            statement.setInt(2, slot + 1);
            statement.setInt(3, getActiveClassId().id);
            statement.execute();
        } catch (final Exception e) {
            LOG.warn("could not remove char henna: " + e, e);
        }

        // Calculate Henna modifiers of this L2Player
        recalcHennaStats();

        // Send Server->Client HennaInfo packet to this L2Player
        sendPacket(new HennaInfo(this));
        // Send Server->Client UserInfo packet to this L2Player
        sendUserInfo(true);

        // Add the recovered dyes to the getPlayer's inventory and notify them.
        ItemFunctions.addItem(this, dyeID, henna.getDrawCount() / 2, "removeHenna");

    }

    /**
     * Add a Henna to the L2Player, save update in the character_hennas table of the database and send Server->Client HennaInfo/UserInfo packet to this L2Player.<BR>
     */
    public void addHenna(Henna henna) {
        if (getHennaEmptySlots() == 0) {
            sendPacket(SystemMsg.NO_SLOT_EXISTS_TO_DRAW_THE_SYMBOL);
            return;
        }

        // int slot = 0;
        for (int i = 0; i < 3; i++) {
            if (this.henna.get(i) == null) {
                this.henna.set(i, henna);

                // Calculate Henna modifiers of this L2Player
                recalcHennaStats();

                try (Connection con = DatabaseFactory.getInstance().getConnection();
                     PreparedStatement statement = con.prepareStatement("INSERT INTO `character_hennas` (char_obj_id, symbol_id, slot, class_index) VALUES (?,?,?,?)")) {
                    statement.setInt(1, objectId());
                    statement.setInt(2, henna.getSymbolId());
                    statement.setInt(3, i + 1);
                    statement.setInt(4, getActiveClassId().id);
                    statement.execute();
                } catch (Exception e) {
                    LOG.warn("could not save char henna: " + e);
                }

                sendPacket(new HennaInfo(this));
                sendUserInfo(true);

                return;
            }
        }

    }

    /**
     * Calculate Henna modifiers of this L2Player.
     */
    private void recalcHennaStats() {
        hennaINT = 0;
        hennaSTR = 0;
        hennaCON = 0;
        hennaMEN = 0;
        hennaWIT = 0;
        hennaDEX = 0;

        for (int i = 0; i < 3; i++) {
            Henna henna = this.henna.get(i);
            if (henna != null && henna.isForThisClass(this)) {
                hennaINT += henna.statINT;
                hennaSTR += henna.statSTR;
                hennaMEN += henna.statMEN;
                hennaCON += henna.statCON;
                hennaWIT += henna.statWIT;
                hennaDEX += henna.statDEX;
            }
        }

        hennaINT = Math.min(Config.HENNA_STATS, hennaINT);
        hennaSTR = Math.min(Config.HENNA_STATS, hennaSTR);
        hennaMEN = Math.min(Config.HENNA_STATS, hennaMEN);
        hennaCON = Math.min(Config.HENNA_STATS, hennaCON);
        hennaWIT = Math.min(Config.HENNA_STATS, hennaWIT);
        hennaDEX = Math.min(Config.HENNA_STATS, hennaDEX);

    }

    public Henna getHenna(final int slot) {
        if ((slot < 1) || (slot > 3)) {
            return null;
        }
        return henna.get(slot - 1);
    }

    public int getHennaStatINT() {
        return hennaINT;
    }

    public int getHennaStatSTR() {
        return hennaSTR;
    }

    public int getHennaStatCON() {
        return hennaCON;
    }

    public int getHennaStatMEN() {
        return hennaMEN;
    }

    public int getHennaStatWIT() {
        return hennaWIT;
    }

    public int getHennaStatDEX() {
        return hennaDEX;
    }

    @Override
    public boolean consumeItem(int itemConsumeId, long itemCount) {
        if (inventory.destroyItemByItemId(itemConsumeId, itemCount, "Consume")) {
            sendPacket(SystemMessage2.removeItems(itemConsumeId, itemCount));
            return true;
        }
        return false;
    }

    @Override
    public boolean consumeItemMp(int itemId, int mp) {
        Optional<ItemInstance> first = inventory.getPaperdollItems().stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getItemId() == itemId)
                .findFirst();

        if (first.isPresent()) {
            ItemInstance item = first.get();
            if (item.getLifeTime() - mp >= 0) {
                item.setLifeTime(item.getLifeTime() - mp);
                sendPacket(new InventoryUpdate().addModifiedItem(item));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMageClass() {
        return template.baseMAtk > 3;
    }

    public boolean isMounted() {
        return mountNpcId > 0;
    }

    public final boolean isRiding() {
        return riding;
    }

    private void setRiding(boolean mode) {
        riding = mode;
    }

    public boolean checkLandingState() {
        if (isInZone(ZoneType.no_landing)) {
            return false;
        }

        SiegeEvent<?, ?> siege = getEvent(SiegeEvent.class);
        if (siege != null) {
            Residence unit = siege.getResidence();
            return (unit != null) && (getClan() != null) && isClanLeader() && ((getClan().getCastle() == unit.getId()) || (getClan().getHasFortress() == unit.getId()));
        }

        return true;
    }

    public void dismount() {
        setMount(0, 0, 0);
    }

    public void setMount(int npcId, int obj_id, int level) {
        if (isCursedWeaponEquipped())
            return;

        switch (npcId) {
            case 0: // Dismount
                setFlying(false);
                setRiding(false);
                if (isTrasformed()) setTransformation(0);
                removeSkill(325);
                removeSkill(4289);
                getEffectList().stopEffect(Skill.SKILL_HINDER_STRIDER);
                break;
            case PetDataTable.STRIDER_WIND_ID:
            case PetDataTable.STRIDER_STAR_ID:
            case PetDataTable.STRIDER_TWILIGHT_ID:
            case PetDataTable.RED_STRIDER_WIND_ID:
            case PetDataTable.RED_STRIDER_STAR_ID:
            case PetDataTable.RED_STRIDER_TWILIGHT_ID:
            case PetDataTable.GUARDIANS_STRIDER_ID:
                setRiding(true);
                if (isNoble()) {
                    addSkill(325, false);
                }
                break;
            case PetDataTable.WYVERN_ID:
                setFlying(true);
                setLoc(getLoc().addZ(32));
                addSkill(4289, false);
                break;
            case PetDataTable.WGREAT_WOLF_ID:
            case PetDataTable.FENRIR_WOLF_ID:
            case PetDataTable.WFENRIR_WOLF_ID:
                setRiding(true);
                break;
        }

        if (npcId > 0) {
            unEquipWeapon();
        }

        mountNpcId = npcId;
        _mountObjId = obj_id;
        _mountLevel = level;

        broadcastUserInfo(true);
        broadcastPacket(new Ride(this));
        broadcastUserInfo(true);

        sendPacket(new SkillList(this));
    }

    public void unEquipWeapon() {
        ItemInstance wpn = getSecondaryWeaponInstance();
        if (wpn != null) {
            sendDisarmMessage(wpn);
            getInventory().unEquipItem(wpn);
        }

        wpn = getActiveWeaponInstance();
        if (wpn != null) {
            sendDisarmMessage(wpn);
            getInventory().unEquipItem(wpn);
        }

        abortAttack(true, true);
        abortCast(true, true);
    }

    @Override
    public int getSpeed(int baseSpeed) {
        if (isMounted()) {
            PetData petData = PetDataTable.INSTANCE.getInfo(mountNpcId, _mountLevel);
            int speed = 187;
            if (petData != null) {
                speed = petData.speed;
            }
            double mod = 1.;
            int level = getLevel();
            if ((_mountLevel > level) && ((level - _mountLevel) > 10)) {
                mod = 0.5;
            }
            baseSpeed = (int) (mod * speed);
        }
        return super.getSpeed(baseSpeed);
    }

    public int getMountNpcId() {
        return mountNpcId;
    }

    public int getMountObjId() {
        return _mountObjId;
    }

    public int getMountLevel() {
        return _mountLevel;
    }

    public void sendDisarmMessage(ItemInstance wpn) {
        if (wpn.getEnchantLevel() > 0) {
            SystemMessage sm = new SystemMessage(SystemMessage.EQUIPMENT_OF__S1_S2_HAS_BEEN_REMOVED);
            sm.addNumber(wpn.getEnchantLevel());
            sm.addItemName(wpn.getItemId());
            sendPacket(sm);
        } else {
            SystemMessage sm = new SystemMessage(SystemMessage.S1__HAS_BEEN_DISARMED);
            sm.addItemName(wpn.getItemId());
            sendPacket(sm);
        }
    }

    public WarehouseType getUsingWarehouseType() {
        return _usingWHType;
    }

    public void setUsingWarehouseType(final WarehouseType type) {
        _usingWHType = type;
    }

    public Warehouse getWithdrawWarehouse() {
        return _withdrawWarehouse;
    }

    public void setWithdrawWarehouse(Warehouse withdrawWarehouse) {
        _withdrawWarehouse = withdrawWarehouse;
    }

    public List<EffectCubic> getCubics() {
        return cubics == null ? List.of() : new ArrayList<>(cubics.values());
    }

    public void addCubic(EffectCubic cubic) {
        if (cubics == null) {
            cubics = new ConcurrentHashMap<>(3);
        }
        cubics.put(cubic.getId(), cubic);
    }

    public void removeCubic(int id) {
        if (cubics != null) {
            cubics.remove(id);
        }
    }

    public EffectCubic getCubic(int id) {
        return cubics == null ? null : cubics.get(id);
    }

    @Override
    public String toString() {
        return getName() + "[" + objectId() + "]";
    }

    public int getEnchantEffect() {
        final ItemInstance wpn = getActiveWeaponInstance();

        if (wpn == null) {
            return 0;
        }

        return Math.min(127, wpn.getEnchantLevel());
    }

    public NpcInstance getLastNpc() {
        return lastNpc;
    }

    public void setLastNpc(final NpcInstance npc) {
        lastNpc = npc;
    }

    public MultiSellListContainer getMultisell() {
        return _multisell;
    }

    public void setMultisell(MultiSellListContainer multisell) {
        _multisell = multisell;
    }

    /**
     * @return True if L2Player is a participant in the Festival of Darkness.<BR>
     * <BR>
     */
    public boolean isFestivalParticipant() {
        return getReflection() instanceof DarknessFestival;
    }

    @Override
    public boolean unChargeShots(boolean spirit) {
        ItemInstance weapon = getActiveWeaponInstance();
        if (weapon == null) {
            return false;
        }

        if (spirit) {
            weapon.setChargedSpiritshot(ItemInstance.CHARGED_NONE);
        } else {
            weapon.setChargedSoulshot(ItemInstance.CHARGED_NONE);
        }

        autoShot();
        return true;
    }

    public boolean unChargeFishShot() {
        ItemInstance weapon = getActiveWeaponInstance();
        if (weapon == null) {
            return false;
        }
        weapon.setChargedFishshot(false);
        autoShot();
        return true;
    }

    public void autoShot() {
        activeSoulShots.forEach(shotId -> {
            ItemInstance item = inventory.getItemByItemId(shotId);
            if (item != null) {
                IItemHandler handler = item.getTemplate().getHandler();
                if (handler != null) handler.useItem(this, item, false);
            } else {
                removeAutoSoulShot(shotId);
            }
        });
    }

    public boolean getChargedFishShot() {
        ItemInstance weapon = getActiveWeaponInstance();
        return (weapon != null) && weapon.getChargedFishshot();
    }

    @Override
    public boolean getChargedSoulShot() {
        ItemInstance weapon = getActiveWeaponInstance();
        return (weapon != null) && (weapon.getChargedSoulshot() == ItemInstance.CHARGED_SOULSHOT);
    }

    @Override
    public int getChargedSpiritShot() {
        ItemInstance weapon = getActiveWeaponInstance();
        if (weapon == null) {
            return 0;
        }
        return weapon.getChargedSpiritshot();
    }

    public void addAutoSoulShot(Integer itemId) {
        activeSoulShots.add(itemId);
    }

    public void removeAutoSoulShot(Integer itemId) {
        activeSoulShots.remove(itemId);
    }

    public Set<Integer> getAutoSoulShot() {
        return activeSoulShots;
    }

    @Override
    public InvisibleType getInvisibleType() {
        return _invisibleType;
    }

    public void setInvisibleType(InvisibleType vis) {
        _invisibleType = vis;
    }

    public int getClanPrivileges() {
        if (clan == null) {
            return 0;
        }
        if (isClanLeader()) {
            return Clan.CP_ALL;
        }
        if ((_powerGrade < 1) || (_powerGrade > 9)) {
            return 0;
        }
        RankPrivs privs = clan.getRankPrivs(_powerGrade);
        if (privs != null) {
            return privs.getPrivs();
        }
        return 0;
    }

    public void teleToClosestTown() {
        teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_VILLAGE), ReflectionManager.DEFAULT);
    }

    public void teleToCastle() {
        teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_CASTLE), ReflectionManager.DEFAULT);
    }

    public void teleToFortress() {
        teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_FORTRESS), ReflectionManager.DEFAULT);
    }

    public void teleToClanhall() {
        teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_CLANHALL), ReflectionManager.DEFAULT);
    }

    @Override
    public void sendMessage(CustomMessage message) {
        sendMessage(message.toString());
    }

    @Override
    public void sendChatMessage(int objectId, int messageType, String charName, String text) {
        sendPacket(new CreatureSay(objectId, messageType, charName, text));
    }

    @Override
    public void teleToLocation(Location loc, int refId) {
        if (isDeleted()) {
            return;
        }

        super.teleToLocation(loc, refId);
    }

    @Override
    public boolean onTeleported() {
        if (!super.onTeleported()) {
            return false;
        }

        if (isFakeDeath()) {
            breakFakeDeath();
        }

        if (isInBoat()) {
            setLoc(getBoat().getLoc());
        }

        // 15 seconds after teleport the character does not cast agr
        setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);

        spawnMe();

        setLastClientPosition(getLoc());
        setLastServerPosition(getLoc());

        if (isPendingRevive()) {
            doRevive();
        }

        sendActionFailed();

        getAI().notifyEvent(CtrlEvent.EVT_TELEPORTED);

        if (isLockedTarget() && (getTarget() != null)) {
            sendPacket(new MyTargetSelected(getTarget().objectId(), 0));
        }

        sendUserInfo(true);
        if (getPet() != null) {
            if (!getPet().isInRange(getLoc(), Config.FOLLOW_RANGE)) {
                getPet().teleportToOwner();
            }
        }

        return true;
    }

    public void setPartyMatchingVisible() {
        _partyMatchingVisible = (!(_partyMatchingVisible));
    }

    public boolean isPartyMatchingVisible() {
        return _partyMatchingVisible;
    }

    public boolean enterObserverMode(Location loc) {
        WorldRegion observerRegion = World.getRegion(loc);
        if (observerRegion == null) {
            return false;
        }
        if (!observerMode.compareAndSet(OBSERVER_NONE, OBSERVER_STARTING)) {
            return false;
        }

        setTarget(null);
        stopMove();
        sitDown(null);
        setFlying(true);

        World.removeObjectsFromPlayer(this);

        _observerRegion = observerRegion;

        broadcastCharInfo();

        sendPacket(new ObserverStart(loc));

        return true;
    }

    public void appearObserverMode() {
        if (!observerMode.compareAndSet(OBSERVER_STARTING, OBSERVER_STARTED)) {
            return;
        }

        WorldRegion currentRegion = getCurrentRegion();

        // Add a fake to the point of observation
        if (!_observerRegion.equals(currentRegion)) {
            _observerRegion.addObject(this);
        }

        World.showObjectsToPlayer(this);

        if (olympiadObserveGame != null) {
            olympiadObserveGame.addSpectator(this);
            olympiadObserveGame.broadcastInfo(null, this, true);
        }
    }

    public void leaveObserverMode() {
        if (!observerMode.compareAndSet(OBSERVER_STARTED, OBSERVER_LEAVING)) {
            return;
        }

        WorldRegion currentRegion = getCurrentRegion();

        if (!_observerRegion.equals(currentRegion)) {
            _observerRegion.removeObject(this);
        }

        // Clear all visible objects
        World.removeObjectsFromPlayer(this);

        _observerRegion = null;

        setTarget(null);
        stopMove();

        // Exit the mode observing
        sendPacket(new ObserverEnd(getLoc()));
    }

    public void returnFromObserverMode() {
        if (!observerMode.compareAndSet(OBSERVER_LEAVING, OBSERVER_NONE)) {
            return;
        }

        // It is necessary when teleport from a higher point to a lower, or harmed by the "fall"
        _lastClientPosition = null;
        _lastServerPosition = null;

        setBlock(false);
        standUp();
        setFlying(false);

        broadcastCharInfo();

        World.showObjectsToPlayer(this);
    }

    public void enterOlympiadObserverMode(Location loc, OlympiadGame game, Reflection reflect) {
        WorldRegion observerRegion = World.getRegion(loc);
        //WorldRegion currentRegion = getCurrentRegion();
        WorldRegion oldObserver = _observerRegion;
        if (observerRegion == null) {
            return;
        }
        OlympiadGame oldGame = olympiadObserveGame;
        if (!observerMode.compareAndSet(oldGame != null ? OBSERVER_STARTED : OBSERVER_NONE, OBSERVER_STARTING)) {
            return;
        }

        setTarget(null);
        stopMove();

        World.removeObjectsFromPlayer(this);
        _observerRegion = observerRegion;

        if (oldGame != null) {
            if (isInObserverMode() && (oldObserver != null))
                oldObserver.removeObject(this);
            oldGame.removeSpectator(this);
            sendPacket(ExOlympiadMatchEnd.STATIC);
        } else {
            setBlock(true);

            broadcastCharInfo();

            sendPacket(new ExOlympiadMode(3));
        }

        olympiadObserveGame = game;

        setReflection(reflect);
        sendPacket(new TeleportToLocation(this, loc));
    }

    public void leaveOlympiadObserverMode(boolean removeFromGame) {
        if (olympiadObserveGame == null)
            return;

        if (!observerMode.compareAndSet(OBSERVER_STARTED, OBSERVER_LEAVING)) {
            return;
        }

        if (removeFromGame) {
            olympiadObserveGame.removeSpectator(this);
        }
        olympiadObserveGame = null;

        WorldRegion currentRegion = getCurrentRegion();

        if ((_observerRegion != null) && (currentRegion != null) && !_observerRegion.equals(currentRegion)) {
            _observerRegion.removeObject(this);
        }

        World.removeObjectsFromPlayer(this);

        _observerRegion = null;

        setTarget(null);
        stopMove();

        sendPacket(new ExOlympiadMode(0));
        sendPacket(ExOlympiadMatchEnd.STATIC);

        setReflection(ReflectionManager.DEFAULT);

        sendPacket(new TeleportToLocation(this, getLoc()));
    }

    public int getOlympiadSide() {
        return _olympiadSide;
    }

    public void setOlympiadSide(final int i) {
        _olympiadSide = i;
    }


    public boolean isInObserverMode() {
        return observerMode.get() > OBSERVER_NONE;//So it can be OBSERVER_STARTING(1), OBSERVER_LEAVING(2) or OBSERVER_STARTED(3)
    }

    public int getObserverMode() {
        return observerMode.get();
    }

    public WorldRegion getObserverRegion() {
        return _observerRegion;
    }

    public int getTeleMode() {
        return _telemode;
    }

    public void setTeleMode(final int mode) {
        _telemode = mode;
    }

    public void setLoto(final int i, final int val) {
        _loto[i] = val;
    }

    public int getLoto(final int i) {
        return _loto[i];
    }

    public boolean getMessageRefusal() {
        return _messageRefusal;
    }

    public void setMessageRefusal(final boolean mode) {
        _messageRefusal = mode;
    }

    public boolean getTradeRefusal() {
        return tradeRefusal;
    }

    public void setTradeRefusal(final boolean mode) {
        tradeRefusal = mode;
    }

    public boolean getPartyInviteRefusal() {
        return _partyinviteRefusal;
    }

    public void setPartyInviteRefusal(final boolean mode) {
        _partyinviteRefusal = mode;
    }

    public boolean getFriendInviteRefusal() {
        return _friendinviteRefusal;
    }

    public void setFriendInviteRefusal(final boolean mode) {
        _friendinviteRefusal = mode;
    }

    public void addToBlockList(final String charName) {
        if ((charName == null) || charName.equalsIgnoreCase(getName()) || isInBlockList(charName)) {
            sendPacket(Msg.YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST);
            return;
        }

        Player block_target = World.getPlayer(charName);

        if (block_target != null) {
            if (block_target.isGM()) {
                sendPacket(Msg.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM);
                return;
            }
            blockList.put(block_target.objectId(), block_target.getName());
            sendPacket(new SystemMessage(SystemMessage.S1_HAS_BEEN_ADDED_TO_YOUR_IGNORE_LIST).addString(block_target.getName()));
            block_target.sendPacket(new SystemMessage(SystemMessage.S1__HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST).addString(getName()));
            return;
        }

        int charId = CharacterDAO.getObjectIdByName(charName);

        if (charId == 0) {
            sendPacket(Msg.YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST);
            return;
        }

        if (Config.gmlist.containsKey(charId) && Config.gmlist.get(charId).IsGM) {
            sendPacket(Msg.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM);
            return;
        }
        blockList.put(charId, charName);
        sendPacket(new SystemMessage(SystemMessage.S1_HAS_BEEN_ADDED_TO_YOUR_IGNORE_LIST).addString(charName));
    }

    public void removeFromBlockList(final String charName) {
        int charId = 0;
        for (int blockId : blockList.keySet()) {
            if (charName.equalsIgnoreCase(blockList.get(blockId))) {
                charId = blockId;
                break;
            }
        }
        if (charId == 0) {
            sendPacket(Msg.YOU_HAVE_FAILED_TO_DELETE_THE_CHARACTER_FROM_IGNORE_LIST);
            return;
        }
        sendPacket(new SystemMessage(SystemMessage.S1_HAS_BEEN_REMOVED_FROM_YOUR_IGNORE_LIST).addString(blockList.remove(charId)));
        Player block_target = GameObjectsStorage.getPlayer(charId);
        if (block_target != null) {
            block_target.sendMessage(getName() + " has removed you from his/her Ignore List.");
        }
    }

    public boolean isInBlockList(final Player player) {
        return blockList.containsKey(player.objectId());
    }

    public boolean isInBlockList(final String charName) {
        return blockList.values().stream()
                .anyMatch(charName::equalsIgnoreCase);
    }

    private void restoreBlockList(Connection con) {
        blockList.clear();

        try (PreparedStatement statement = con.prepareStatement("SELECT target_Id, char_name FROM character_blocklist LEFT JOIN characters ON ( character_blocklist.target_Id = characters.obj_Id ) WHERE character_blocklist.obj_Id = ?")) {
            statement.setInt(1, objectId());

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int targetId = rs.getInt("target_Id");
                    String name = rs.getString("char_name");
                    if (name == null) {
                        continue;
                    }
                    blockList.put(targetId, name);
                }
            }
        } catch (SQLException e) {
            LOG.warn("Can't restore getPlayer blocklist " + e, e);
        }
    }

    private void storeBlockList() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement statement = con.createStatement()) {
            statement.executeUpdate("DELETE FROM character_blocklist WHERE obj_Id=" + objectId());

            if (blockList.isEmpty()) {
                return;
            }

            SqlBatch b = new SqlBatch("INSERT IGNORE INTO `character_blocklist` (`obj_Id`,`target_Id`) VALUES");

            synchronized (blockList) {
                StringBuilder sb;
                for (Entry<Integer, String> e : blockList.entrySet()) {
                    sb = new StringBuilder("(");
                    sb.append(objectId()).append(",");
                    sb.append(e.getKey()).append(")");
                    b.write(sb.toString());
                }
            }
            if (!b.isEmpty()) {
                statement.executeUpdate(b.close());
            }
        } catch (SQLException e) {
            LOG.warn("Can't store getPlayer blocklist " + e);
        }
    }

    public boolean isBlockAll() {
        return blockAll;
    }

    public void setBlockAll(final boolean state) {
        blockAll = state;
    }

    public Collection<String> getBlockList() {
        return blockList.values();
    }

    public boolean isHero() {
        return hero;
    }

    public void setHero(Player player) {
        StatsSet hero = new StatsSet();
        hero.set(Olympiad.CLASS_ID, player.baseClass);
        hero.set(Olympiad.CHAR_ID, player.objectId());
        hero.set(Olympiad.CHAR_NAME, player.getName());
        hero.set(Hero.ACTIVE, 1);

        List<StatsSet> heroesToBe = new ArrayList<>();
        heroesToBe.add(hero);

        Hero.INSTANCE.computeNewHeroes(heroesToBe);
        player.setHero(true);
        Hero.addSkills(player);
        player.updatePledgeClass();
        if (player.isHero()) {
            player.broadcastPacket(new SocialAction(player.objectId(), 16));
        }
        player.broadcastUserInfo(true);
    }

    public void setHero(final boolean hero) {
        this.hero = hero;
    }

    public int getPing() {
        return ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    public void setIsInOlympiadMode(final boolean b) {
        inOlympiadMode = b;
    }

    public boolean isInOlympiadMode() {
        return inOlympiadMode;
    }

    public boolean isOlympiadCompStarted() {
        return _isOlympiadCompStarted;
    }

    public void setOlympiadCompStarted(final boolean b) {
        _isOlympiadCompStarted = b;
    }

    public void updateNobleSkills() {
        if (isNoble()) {
            if (isClanLeader() && (getClan().getCastle() > 0)) {
                super.addSkill(327);
            }
            super.addSkill(1374);
            super.addSkill(1324);
            super.addSkill(1325);
            super.addSkill(1326);
            super.addSkill(1327);
        } else {
            super.removeSkill(327);
            super.removeSkill(1374);
            super.removeSkill(1324);
            super.removeSkill(1325);
            super.removeSkill(1326);
            super.removeSkill(1327);
        }
    }

    public boolean isNoble() {
        return noble;
    }

    public void setNoble(boolean noble) {
        if (noble) {
            broadcastPacket(new MagicSkillUse(this, 6673, 1000));
        }
        this.noble = noble;
    }

    public int getSubLevel() {
        return isSubClassActive() ? getLevel() : 0;
    }

    /* varka silenos and ketra orc quests related functions */
    public void updateKetraVarka() {
        varka = 0;
        ketra = 0;
        for (int i = KETRA_MARKS.size(); i > 0; i--)
            if (this.haveItem(KETRA_MARKS.get(i - 1))) {
                ketra = i;
                break;
            }
        for (int i = VARKA_MARKS.size(); i > 0; i--)
            if (this.haveItem(KETRA_MARKS.get(i - 1))) {
                varka = i;
                break;
            }

    }

    public int getVarka() {
        return varka;
    }

    public int getKetra() {
        return ketra;
    }

    public void updateRam() {
        if (haveItem(7247))
            ram = 2;
        else if (haveItem(7246))
            ram = 1;
        else ram = 0;
    }

    public int getRam() {
        return ram;
    }

    public int getPledgeType() {
        return pledgeType;
    }

    public void setPledgeType(final int typeId) {
        pledgeType = typeId;
    }

    public int getLvlJoinedAcademy() {
        return lvlJoinedAcademy;
    }

    public void setLvlJoinedAcademy(int lvl) {
        lvlJoinedAcademy = lvl;
    }

    public int getPledgeClass() {
        return _pledgeClass;
    }

    public void updatePledgeClass() {
        int clanLevel = clan == null ? -1 : clan.getLevel();
        boolean inAcademy = (clan != null) && Clan.isAcademy(pledgeType);
        boolean isGuard = (clan != null) && Clan.isRoyalGuard(pledgeType);
        boolean isKnight = (clan != null) && Clan.isOrderOfKnights(pledgeType);

        boolean isGuardCaptain = false, isKnightCommander = false, isLeader = false;

        SubUnit unit = getSubUnit();
        if (unit != null) {
            UnitMember unitMember = unit.getUnitMember(objectId());
            if (unitMember == null) {
                LOG.warn("Player: unitMember null, clan: " + clan.clanId() + "; pledgeType: " + unit.type());
                return;
            }
            isGuardCaptain = Clan.isRoyalGuard(unitMember.getLeaderOf());
            isKnightCommander = Clan.isOrderOfKnights(unitMember.getLeaderOf());
            isLeader = unitMember.getLeaderOf() == Clan.SUBUNIT_MAIN_CLAN;
        }

        switch (clanLevel) {
            case -1:
                _pledgeClass = RANK_VAGABOND;
                break;
            case 0:
            case 1:
            case 2:
            case 3:
                if (isLeader) {
                    _pledgeClass = RANK_HEIR;
                } else {
                    _pledgeClass = RANK_VASSAL;
                }
                break;
            case 4:
                if (isLeader) {
                    _pledgeClass = RANK_KNIGHT;
                } else {
                    _pledgeClass = RANK_HEIR;
                }
                break;
            case 5:
                if (isLeader) {
                    _pledgeClass = RANK_WISEMAN;
                } else if (inAcademy) {
                    _pledgeClass = RANK_VASSAL;
                } else {
                    _pledgeClass = RANK_HEIR;
                }
                break;
            case 6:
                if (isLeader) {
                    _pledgeClass = RANK_BARON;
                } else if (inAcademy) {
                    _pledgeClass = RANK_VASSAL;
                } else if (isGuardCaptain) {
                    _pledgeClass = RANK_WISEMAN;
                } else if (isGuard) {
                    _pledgeClass = RANK_HEIR;
                } else {
                    _pledgeClass = RANK_KNIGHT;
                }
                break;
            case 7:
                if (isLeader) {
                    _pledgeClass = RANK_COUNT;
                } else if (inAcademy) {
                    _pledgeClass = RANK_VASSAL;
                } else if (isGuardCaptain) {
                    _pledgeClass = RANK_VISCOUNT;
                } else if (isGuard) {
                    _pledgeClass = RANK_KNIGHT;
                } else if (isKnightCommander) {
                    _pledgeClass = RANK_BARON;
                } else if (isKnight) {
                    _pledgeClass = RANK_HEIR;
                } else {
                    _pledgeClass = RANK_WISEMAN;
                }
                break;
            case 8:
                if (isLeader) {
                    _pledgeClass = RANK_MARQUIS;
                } else if (inAcademy) {
                    _pledgeClass = RANK_VASSAL;
                } else if (isGuardCaptain) {
                    _pledgeClass = RANK_COUNT;
                } else if (isGuard) {
                    _pledgeClass = RANK_WISEMAN;
                } else if (isKnightCommander) {
                    _pledgeClass = RANK_VISCOUNT;
                } else if (isKnight) {
                    _pledgeClass = RANK_KNIGHT;
                } else {
                    _pledgeClass = RANK_BARON;
                }
                break;
            case 9:
                if (isLeader) {
                    _pledgeClass = RANK_DUKE;
                } else if (inAcademy) {
                    _pledgeClass = RANK_VASSAL;
                } else if (isGuardCaptain) {
                    _pledgeClass = RANK_MARQUIS;
                } else if (isGuard) {
                    _pledgeClass = RANK_BARON;
                } else if (isKnightCommander) {
                    _pledgeClass = RANK_COUNT;
                } else if (isKnight) {
                    _pledgeClass = RANK_WISEMAN;
                } else {
                    _pledgeClass = RANK_VISCOUNT;
                }
                break;
            case 10:
                if (isLeader) {
                    _pledgeClass = RANK_GRAND_DUKE;
                } else if (inAcademy) {
                    _pledgeClass = RANK_VASSAL;
                } else if (isGuard) {
                    _pledgeClass = RANK_VISCOUNT;
                } else if (isKnight) {
                    _pledgeClass = RANK_BARON;
                } else if (isGuardCaptain) {
                    _pledgeClass = RANK_DUKE;
                } else if (isKnightCommander) {
                    _pledgeClass = RANK_MARQUIS;
                } else {
                    _pledgeClass = RANK_COUNT;
                }
                break;
            case 11:
                if (isLeader) {
                    _pledgeClass = RANK_DISTINGUISHED_KING;
                } else if (inAcademy) {
                    _pledgeClass = RANK_VASSAL;
                } else if (isGuard) {
                    _pledgeClass = RANK_COUNT;
                } else if (isKnight) {
                    _pledgeClass = RANK_VISCOUNT;
                } else if (isGuardCaptain) {
                    _pledgeClass = RANK_GRAND_DUKE;
                } else if (isKnightCommander) {
                    _pledgeClass = RANK_DUKE;
                } else {
                    _pledgeClass = RANK_MARQUIS;
                }
                break;
        }

        if (hero && (_pledgeClass < RANK_MARQUIS)) {
            _pledgeClass = RANK_MARQUIS;
        } else if (noble && (_pledgeClass < RANK_BARON)) {
            _pledgeClass = RANK_BARON;
        }
    }

    public int getPowerGrade() {
        return _powerGrade;
    }

    public void setPowerGrade(final int grade) {
        _powerGrade = grade;
    }

    public int getApprentice() {
        return apprentice;
    }

    public void setApprentice(final int apprentice) {
        this.apprentice = apprentice;
    }

    public int getSponsor() {
        return clan == null ? 0 : clan.getAnyMember(objectId()).getSponsor();
    }

    private int getNameColor() {
        if (isInObserverMode()) {
            return Color.black.getRGB();
        }

        return _nameColor;
    }

    public void setNameColor(final int nameColor) {
        if ((nameColor != Config.NORMAL_NAME_COLOUR)) {
            setVar("namecolor", Integer.toHexString(nameColor));
        } else
            unsetVar("namecolor");
        _nameColor = nameColor;
    }

    public void setVar(String name, String value) {
        setVar(name, value, -1);
    }

    public void setVar(String name, int value, long expireDate) {
        setVar(name, String.valueOf(value), expireDate);
    }

    public void setVar(String name, long value, long expireDate) {
        setVar(name, String.valueOf(value), expireDate);
    }

    public void setVar(String name) {
        setVar(name, 1);
    }

    public void setVar(String name, long value) {
        setVar(name, String.valueOf(value), -1);
    }

    public void incVar(String name) {
        if (getVarInt(name) > 0)
            setVar(name, getVarInt(name) + 1);
        else
            setVar(name);
    }

    public void setVar(String name, String value, long expireDate) {
        if (user_variables.containsKey(name)) {
            getVarObject(name).stopExpireTask();
        }

        user_variables.put(name, new PlayerVar(this, name, value, expireDate));
        mysql.set("REPLACE INTO character_variables (obj_id, type, name, value, expire_time) VALUES (?,'user-var',?,?,?)", objectId(), name, value, expireDate);
    }

    public void unsetVar(String name) {
        if (name == null)
            return;

        PlayerVar pv = user_variables.remove(name);

        if (pv != null) {
            pv.stopExpireTask();
            mysql.set("DELETE FROM `character_variables` WHERE `obj_id`=? AND `type`='user-var' AND `name`=? LIMIT 1", objectId(), name);
        }
    }

    public String getVar(String name) {
        PlayerVar pv = getVarObject(name);

        if (pv == null) {
            return null;
        }

        return pv.getValue();
    }

    public long getVarTimeToExpire(String name) {
        try {
            return getVarObject(name).getTimeToExpire();
        } catch (NullPointerException ignored) {
        }
        return 0;
    }

    private PlayerVar getVarObject(String name) {
        return user_variables.get(name);
    }

    public boolean isVarSet(String name) {
        PlayerVar pv = getVarObject(name);
        if (pv == null) return false;

        return pv.getValueBoolean();
    }

    public long getVarLong(String name) {
        long result = 0;
        String var = getVar(name);
        if (var != null) {
            result = Long.parseLong(var);
        }
        return result;
    }

    public int getVarInt(String name) {
        String var = getVar(name);
        if (var != null) return toInt(var);
        return 0;
    }

    public Map<String, PlayerVar> getVars() {
        return user_variables;
    }

    private void loadVariables(Connection con) {
        try (PreparedStatement offline = con.prepareStatement("SELECT * FROM character_variables WHERE obj_id = ?")) {
            offline.setInt(1, objectId());

            try (ResultSet rs = offline.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    String value = Strings.stripSlashes(rs.getString("value"));
                    long expire_time = rs.getLong("expire_time");
                    long curtime = System.currentTimeMillis();

                    if ((expire_time <= curtime) && (expire_time > 0)) {
                        continue;
                    }

                    user_variables.put(name, new PlayerVar(this, name, value, expire_time));
                }
            }
        } catch (SQLException e) {
            LOG.error("Error while loading Character_variables for Id " + objectId(), e);
        }
    }

    public void addQuickVar(String name, boolean value) {
        addQuickVar(name, "" + value);
    }

    public void addQuickVar(String name, long value) {
        addQuickVar(name, "" + value);
    }

    public void addQuickVar(String name, String value) {
        quickVars.put(name, value);
    }

    public String getQuickVarS(String name) {
        if (!quickVars.containsKey(name)) return "";
        return quickVars.get(name);
    }

    /**
     * Getting back String Value located in quickVars Map<Name, Value>.
     * If value doesn't exist, defaultValue is returned.
     * If value isn't Boolean type, throws Error
     *
     * @param name         key
     * @param defaultValue Value returned when <code>name</code> key doesn't exist
     * @return value
     */
    public boolean getQuickVarB(String name, boolean defaultValue) {
        if (!quickVars.containsKey(name)) {
            return defaultValue;
        }
        return toBoolean(quickVars.get(name));
    }

    public int getQuickVarI(String name) {
        return getQuickVarI(name, -1);
    }

    public int getQuickVarI(String name, int defaultValue) {
        if (!quickVars.containsKey(name)) return defaultValue;
        return toInt(quickVars.get(name));
    }

    /**
     * Getting back Long Value located in quickVars Map<Name, Value>.
     * If value doesn't exist, defaultValue is returned.
     * If value isn't Long type, throws Error
     *
     * @param name key
     * @return value
     */
    public long getQuickVarL(String name) {
        if (!quickVars.containsKey(name)) return 0;
        return toLong(quickVars.get(name));
    }

    /**
     * Checking if quickVars Map<Name, Value> contains a name as a Key
     *
     * @param name key
     * @return contains name
     */
    public boolean containsQuickVar(String name) {
        return quickVars.containsKey(name);
    }

    public void deleteQuickVar(String name) {
        quickVars.remove(name);
    }

    public void addLoadedImage(int id) {
        loadedImages.add(id);
    }

    /**
     * Did Game Client already receive Custom Image from the server?
     *
     * @param id of the image
     * @return client received image
     */
    public boolean wasImageLoaded(int id) {
        return loadedImages.contains(id);
    }

    public int getLoadedImagesSize() {
        return loadedImages.size();
    }

    public int isAtWarWith(final Integer id) {
        return (clan == null) || !clan.isAtWarWith(id) ? 0 : 1;
    }

    void stopWaterTask() {
        if (_taskWater != null) {
            _taskWater.cancel(false);
            _taskWater = null;
            sendPacket(new SetupGauge(this, SetupGauge.CYAN, 0));
            sendChanges();
        }
    }

    private void startWaterTask() {
        if (isDead()) {
            stopWaterTask();
        } else if (Config.ALLOW_WATER && (_taskWater == null)) {
            int timeinwater = (int) (calcStat(Stats.BREATH, 86) * 1000L);
            sendPacket(new SetupGauge(this, SetupGauge.CYAN, timeinwater));
            if ((isTrasformed()) && (getTransformationTemplate() > 0) && !isCursedWeaponEquipped()) {
                setTransformation(0);
            }
            _taskWater = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new WaterTask(this), timeinwater, 1000L);
            sendChanges();
        }
    }

    public void doRevive(double percent) {
        restoreExp(percent);
        doRevive();
    }

    @Override
    public void doRevive() {
        super.doRevive();
        setAgathionRes(false);
        unsetVar("lostexp");
        updateEffectIcons();
        autoShot();

        // Ady - Block the community buffer 10 seconds so the getPlayer cannot buff when resurrected
        _resurrectionBuffBlockedTime = System.currentTimeMillis() + 10 * 1000;
    }

    public void reviveRequest(Player reviver, double percent, boolean pet) {
        ReviveAnswerListener reviveAsk = (_askDialog != null) && (_askDialog.getValue() instanceof ReviveAnswerListener) ? (ReviveAnswerListener) _askDialog.getValue() : null;
        if (reviveAsk != null) {
            if ((reviveAsk.isForPet() == pet) && (reviveAsk.getPower() >= percent)) {
                reviver.sendPacket(Msg.BETTER_RESURRECTION_HAS_BEEN_ALREADY_PROPOSED);
                return;
            }
            if (pet && !reviveAsk.isForPet()) {
                reviver.sendPacket(Msg.SINCE_THE_MASTER_WAS_IN_THE_PROCESS_OF_BEING_RESURRECTED_THE_ATTEMPT_TO_RESURRECT_THE_PET_HAS_BEEN_CANCELLED);
                return;
            }
            if (pet && isDead()) {
                reviver.sendPacket(Msg.WHILE_A_PET_IS_ATTEMPTING_TO_RESURRECT_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER);
                return;
            }
        }

        if ((pet && getPet() != null && getPet().isDead()) || (!pet && isDead())) {
            ConfirmDlg pkt = new ConfirmDlg(SystemMsg.C1_IS_MAKING_AN_ATTEMPT_TO_RESURRECT_YOU_IF_YOU_CHOOSE_THIS_PATH_S2_EXPERIENCE_WILL_BE_RETURNED_FOR_YOU, (pet ? 0 : 5 * 60 * 1000));
            pkt.addName(reviver).addString(Math.round(percent) + " percent");

            reviver.getCounters().playersRessurected++;

            ask(pkt, new ReviveAnswerListener(this, percent, pet));
        }
    }

    /**
     * @return Ady - Max time for the getPlayer to accept the resurrection request
     */
    public long getResurrectionMaxTime() {
        return _resurrectionMaxTime;
    }

    /**
     * @return Ady - Block time that the getPlayer cannot use the community buffer
     */
    public long getResurrectionBuffBlockedTime() {
        return _resurrectionBuffBlockedTime;
    }

    public void summonCharacterRequest(final Creature summoner, final Location loc, final int summonConsumeCrystal) {
        ConfirmDlg cd = new ConfirmDlg(SystemMsg.C1_WISHES_TO_SUMMON_YOU_FROM_S2, 60000);
        cd.addName(summoner).addZoneName(loc);

        ask(cd, new SummonAnswerListener(this, loc, summonConsumeCrystal));
    }

    public void updateNoChannel(final long time) {
        setNoChannel(time);

        final String stmt = "UPDATE characters SET nochannel = ? WHERE obj_Id=?";

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(stmt)) {
            statement.setLong(1, _NoChannel > 0 ? _NoChannel / 1000 : _NoChannel);
            statement.setInt(2, objectId());
            statement.executeUpdate();
        } catch (final Exception e) {
            LOG.warn("Could not activate nochannel:" + e);
        }

        sendPacket(new EtcStatusUpdate(this));
    }

    public boolean isJailed() {
        return isVarSet("jailed");
    }

    private void checkRecom() {
        Calendar temp = Calendar.getInstance();
        temp.set(Calendar.HOUR_OF_DAY, 6);
        temp.set(Calendar.MINUTE, 30);
        temp.set(Calendar.SECOND, 0);
        temp.set(Calendar.MILLISECOND, 0);
        long count = Math.round(((System.currentTimeMillis() / 1000.) - _lastAccess) / 86400.);
        if ((count == 0) && (_lastAccess < (temp.getTimeInMillis() / 1000)) && (System.currentTimeMillis() > temp.getTimeInMillis())) {
            count++;
        }

        for (int i = 1; i < count; i++) {
            setRecomHave(getRecomHave() - 20);
        }

        if (count > 0) {
            restartRecom();
        }
    }

    public void restartRecom() {
        setRecomBonusTime(3600);
        setRecomLeftToday(0);
        setRecomLeft(20);
        setRecomHave(getRecomHave() - 20);
        stopRecomBonusTask(false);
        startRecomBonusTask();
        sendUserInfo(true);
        sendVoteSystemInfo();
    }

    @Override
    public boolean isInBoat() {
        return boat != null;
    }

    public Boat getBoat() {
        return boat;
    }

    public void setBoat(Boat boat) {
        this.boat = boat;
    }

    public Location getInBoatPosition() {
        return _inBoatPosition;
    }

    public void setInBoatPosition(Location loc) {
        _inBoatPosition = loc;
    }

    public Map<ClassId, SubClass> getSubClasses() {
        return classlist;
    }

    public void setBaseClass(final ClassId baseClass) {
        this.baseClass = baseClass;
    }

    public ClassId getBaseClassId() {
        return baseClass;
    }

    public SubClass getActiveClass() {
        return activeClass;
    }

    public ClassId getActiveClassId() {
        if (getActiveClass() == null)
            return baseClass;

        return getActiveClass().getClassId();
    }

    /**
     * Changing index of class in DB, used for changing class when finished professional quests
     */
    private synchronized void changeClassInDb(final int oldclass, final int newclass) {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement statement = con.prepareStatement("UPDATE character_subclasses SET class_id=? WHERE char_obj_id=? AND class_id=?")) {
                statement.setInt(1, newclass);
                statement.setInt(2, objectId());
                statement.setInt(3, oldclass);
                statement.executeUpdate();
            }

            try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=?")) {
                statement.setInt(1, objectId());
                statement.setInt(2, newclass);
                statement.executeUpdate();
            }

            try (PreparedStatement statement = con.prepareStatement("UPDATE character_hennas SET class_index=? WHERE char_obj_id=? AND class_index=?")) {
                statement.setInt(1, newclass);
                statement.setInt(2, objectId());
                statement.setInt(3, oldclass);
                statement.executeUpdate();
            }

            try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE object_id=? AND class_index=?")) {
                statement.setInt(1, objectId());
                statement.setInt(2, newclass);
                statement.executeUpdate();
            }

            try (PreparedStatement statement = con.prepareStatement("UPDATE character_shortcuts SET class_index=? WHERE object_id=? AND class_index=?")) {
                statement.setInt(1, newclass);
                statement.setInt(2, objectId());
                statement.setInt(3, oldclass);
                statement.executeUpdate();
            }

            try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND class_index=?")) {
                statement.setInt(1, objectId());
                statement.setInt(2, newclass);
                statement.executeUpdate();
            }

            try (PreparedStatement statement = con.prepareStatement("UPDATE character_skills SET class_index=? WHERE char_obj_id=? AND class_index=?")) {
                statement.setInt(1, newclass);
                statement.setInt(2, objectId());
                statement.setInt(3, oldclass);
                statement.executeUpdate();
            }

            try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_effects_save WHERE object_id=? AND id=?")) {
                statement.setInt(1, objectId());
                statement.setInt(2, newclass);
                statement.executeUpdate();
            }

            try (PreparedStatement statement = con.prepareStatement("UPDATE character_effects_save SET id=? WHERE object_id=? AND id=?")) {
                statement.setInt(1, newclass);
                statement.setInt(2, objectId());
                statement.setInt(3, oldclass);
                statement.executeUpdate();
            }

            try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=? AND class_index=?")) {
                statement.setInt(1, objectId());
                statement.setInt(2, newclass);
                statement.executeUpdate();
            }

            try (PreparedStatement statement = con.prepareStatement("UPDATE character_skills_save SET class_index=? WHERE char_obj_id=? AND class_index=?")) {
                statement.setInt(1, newclass);
                statement.setInt(2, objectId());
                statement.setInt(3, oldclass);
                statement.executeUpdate();
            }
        } catch (final SQLException e) {
            LOG.error("Error while changing Class in Database", e);
        }
    }

    private void storeCharSubClasses() {
        SubClass main = getActiveClass();
        if (main != null) {
            main.setCp(getCurrentCp());
            // main.setExp(exp());
            // main.setLevel(occupation());
            // main.setSp(getSp());
            main.setHp(getCurrentHp());
            main.setMp(getCurrentMp());
            main.setActive(true);
            getSubClasses().put(getActiveClassId(), main);
        } else {
            LOG.warn("Could not store char sub data, main class " + getActiveClassId() + " not found for " + this);
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement statement = con.createStatement()) {

            StringBuilder sb;
            for (SubClass subClass : getSubClasses().values()) {
                sb = new StringBuilder("UPDATE character_subclasses SET ");
                sb.append("exp=").append(subClass.getExp()).append(",");
                sb.append("sp=").append(subClass.getSp()).append(",");
                sb.append("curHp=").append(subClass.getHp()).append(",");
                sb.append("curMp=").append(subClass.getMp()).append(",");
                sb.append("curCp=").append(subClass.getCp()).append(",");
                sb.append("occupation=").append(subClass.getLevel()).append(",");
                sb.append("active=").append(subClass.isActive() ? 1 : 0).append(",");
                sb.append("isBase=").append(subClass.isBase() ? 1 : 0).append(",");
                sb.append("death_penalty=").append(subClass.getDeathPenalty(this).getLevelOnSaveDB()).append(",");
                sb.append("certification='").append(subClass.getCertification()).append("'");
                sb.append(" WHERE char_obj_id=").append(objectId()).append(" AND class_id=").append(subClass.getClassId()).append(" LIMIT 1");
                statement.executeUpdate(sb.toString());
            }

            sb = new StringBuilder("UPDATE character_subclasses SET ");
            sb.append("maxHp=").append(getMaxHp()).append(",");
            sb.append("maxMp=").append(getMaxMp()).append(",");
            sb.append("maxCp=").append(getMaxCp());
            sb.append(" WHERE char_obj_id=").append(objectId()).append(" AND active=1 LIMIT 1");
            statement.executeUpdate(sb.toString());
        } catch (SQLException e) {
            LOG.warn("Error while storing Char Subclasses", e);
        }
    }

    public boolean addSubClass(final ClassId classId, boolean storeOld, int certification) {
        if (classlist.size() >= (4 + Config.ALT_GAME_SUB_ADD)) {
            return false;
        }

        final ClassId newId = classId;

        final SubClass newClass = new SubClass();
        newClass.setBase(false);
        if (newId.race == null) {
            return false;
        }

        newClass.setClassId(classId);
        newClass.setCertification(certification);

        classlist.put(classId, newClass);

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("INSERT INTO character_subclasses (char_obj_id, class_id, exp, sp, curHp, curMp, curCp, maxHp, maxMp, maxCp, level, active, isBase, death_penalty, certification) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
            // Store the basic info about this new sub-class.
            statement.setInt(1, objectId());
            statement.setInt(2, newClass.getClassId().id);
            statement.setLong(3, Experience.LEVEL[40]);
            statement.setInt(4, 0);
            statement.setDouble(5, getCurrentHp());
            statement.setDouble(6, getCurrentMp());
            statement.setDouble(7, getCurrentCp());
            statement.setDouble(8, getCurrentHp());
            statement.setDouble(9, getCurrentMp());
            statement.setDouble(10, getCurrentCp());
            statement.setInt(11, 40);
            statement.setInt(12, 0);
            statement.setInt(13, 0);
            statement.setInt(14, 0);
            statement.setInt(15, certification);
            statement.execute();
        } catch (final Exception e) {
            LOG.warn("Could not add character sub-class: " + e, e);
            return false;
        }

        setActiveSubClass(classId, storeOld);

        boolean countUnlearnable = true;
        int unLearnable = 0;

        Collection<SkillLearn> skills = SkillAcquireHolder.getAvailableSkills(this, AcquireType.NORMAL);
        while (skills.size() > unLearnable) {
            for (final SkillLearn s : skills) {
                final Skill sk = SkillTable.INSTANCE.getInfo(s.id(), s.getLevel());
                if ((sk == null) || sk.cantLearn(newId)) {
                    if (countUnlearnable) {
                        unLearnable++;
                    }
                    continue;
                }
                addSkill(sk, true);
            }
            countUnlearnable = false;
            skills = SkillAcquireHolder.getAvailableSkills(this, AcquireType.NORMAL);
        }

        sendPacket(new SkillList(this));
        setFullHpMp();
        setFullCp();
        return true;
    }

    public boolean modifySubClass(final ClassId oldClassId, final ClassId newClassId) {
        final SubClass originalClass = classlist.get(oldClassId);
        if ((originalClass == null) || originalClass.isBase()) {
            return false;
        }

        final int certification = originalClass.getCertification();

        try (Connection con = DatabaseFactory.getInstance().getConnection()) {

            // Remove all basic info stored about this sub-class.
            try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_subclasses WHERE char_obj_id=? AND class_id=? AND isBase = 0")) {
                statement.setInt(1, objectId());
                statement.setInt(2, oldClassId.id);
                statement.execute();
            }

            // Remove all skill info stored for this sub-class.
            try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND class_index=? ")) {
                statement.setInt(1, objectId());
                statement.setInt(2, oldClassId.id);
                statement.execute();
            }

            // Remove all saved skills info stored for this sub-class.
            try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=? AND class_index=? ")) {
                statement.setInt(1, objectId());
                statement.setInt(2, oldClassId.id);
                statement.execute();
            }

            // Remove all saved effects stored for this sub-class.
            try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_effects_save WHERE object_id=? AND id=? ")) {
                statement.setInt(1, objectId());
                statement.setInt(2, oldClassId.id);
                statement.execute();
            }

            // Remove all henna info stored for this sub-class.
            try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=? ")) {
                statement.setInt(1, objectId());
                statement.setInt(2, oldClassId.id);
                statement.execute();
            }

            // Remove all shortcuts info stored for this sub-class.
            try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE object_id=? AND class_index=? ")) {
                statement.setInt(1, objectId());
                statement.setInt(2, oldClassId.id);
                statement.execute();
            }
        } catch (final Exception e) {
            LOG.warn("Error while deleting char sub-class", e);
        }

        classlist.remove(oldClassId);

        return (newClassId != null) || addSubClass(newClassId, false, certification);
    }

    public void setActiveSubClass(final ClassId subId, final boolean store) {
        final SubClass sub = classlist.get(subId);
        if (sub == null) {
            return;
        }

        if (isInDuel()) {
            sendMessage("Unable to perform during a duel!");
            return;
        }

        //Fix for Cancel exploit
        CancelTaskManager.INSTANCE.cancelPlayerTasks(this);

        if (activeClass != null) {
            EffectsDAO.INSTANCE.insert(this);
            storeDisableSkills();

            QuestState st = getQuestState(_422_RepentYourSins.class);
            if (st != null) st.exitCurrentQuest();
        }

        if (store) {
            final SubClass oldsub = activeClass;
            oldsub.setCp(getCurrentCp());
            // oldsub.setExp(exp());
            // oldsub.setLevel(occupation());
            // oldsub.setSp(getSp());
            oldsub.setHp(getCurrentHp());
            oldsub.setMp(getCurrentMp());
            oldsub.setActive(false);
            classlist.put(getActiveClassId(), oldsub);
        }

        sub.setActive(true);
        activeClass = sub;
        classlist.put(getActiveClassId(), sub);

        setClassId(subId, false, false);

        removeAllSkills();

        getEffectList().stopAllEffects();

        if ((summon != null) && (summon instanceof SummonInstance || (Config.ALT_IMPROVED_PETS_LIMITED_USE && (((summon.getNpcId() == PetDataTable.IMPROVED_BABY_KOOKABURRA_ID) && !isMageClass()) || ((summon.getNpcId() == PetDataTable.IMPROVED_BABY_BUFFALO_ID) && isMageClass()))))) {
            summon.unSummon();
        }

        setAgathion(0);

        restoreSkills();
        rewardSkills(false);
        checkSkills();
        sendPacket(new ExStorageMaxCount(this));

        refreshExpertisePenalty();

        sendPacket(new SkillList(this));

        inventory.refreshEquip();
        inventory.validateItems();
        henna.clear();
        for (int i = 0; i < 3; i++) {
            henna.add(null);
        }

        restoreHenna();
        sendPacket(new HennaInfo(this));

        EffectsDAO.INSTANCE.restoreEffects(this, true, sub.getHp(), sub.getCp(), sub.getMp());
        restoreDisableSkills();

        setCurrentHpMp(sub.getHp(), sub.getMp());
        setCurrentCp(sub.getCp());

        shortCuts.restore();
        sendPacket(new ShortCutInit(this));
        activeSoulShots.forEach(shotId -> sendPacket(new ExAutoSoulShot(shotId, true)));

        sendPacket(new SkillCoolTime(this));

        broadcastPacket(new SocialAction(objectId(), SocialAction.LEVEL_UP));

        getDeathPenalty().restore(this);

        setIncreasedForce(0);

        startHourlyTask();

        broadcastCharInfo();
        updateEffectIcons();
        updateStats();
    }

    private void stopKickTask() {
        if (_kickTask != null) {
            _kickTask.cancel(false);
            _kickTask = null;
        }
    }

    @Override
    public int getInventoryLimit() {
        return (int) calcStat(Stats.INVENTORY_LIMIT, 0.0);
    }

    public int getWarehouseLimit() {
        return (int) calcStat(Stats.STORAGE_LIMIT, 0.0);
    }

    public int getTradeLimit() {
        return (int) calcStat(Stats.TRADE_LIMIT, 0.0);
    }

    public int getDwarvenRecipeLimit() {
        return (int) calcStat(Stats.DWARVEN_RECIPE_LIMIT, 50.0) + Config.ALT_ADD_RECIPES;
    }

    public int getCommonRecipeLimit() {
        return (int) calcStat(Stats.COMMON_RECIPE_LIMIT, 50.0) + Config.ALT_ADD_RECIPES;
    }

    public Element getAttackElement() {
        return Formulas.getAttackElement(this, null);
    }

    public int getAttack(Element element) {
        if (element == Element.NONE) {
            return 0;
        }
        return (int) calcStat(element.getAttack(), 0.);
    }

    public int getDefence(Element element) {
        if (element == Element.NONE) {
            return 0;
        }
        return (int) calcStat(element.getDefence(), 0.);
    }

    public boolean getAndSetLastItemAuctionRequest() {
        if ((_lastItemAuctionInfoRequest + 2000L) < System.currentTimeMillis()) {
            _lastItemAuctionInfoRequest = System.currentTimeMillis();
            return true;
        } else {
            _lastItemAuctionInfoRequest = System.currentTimeMillis();
            return false;
        }
    }

    @Override
    public int getNpcId() {
        return -2;
    }

    public GameObject getVisibleObject(int id) {
        if (objectId() == id) {
            return this;
        }

        GameObject target = null;

        if (getTargetId() == id) {
            target = getTarget();
        }

        if ((target == null) && (party != null)) {
            target = party.getMembers().stream()
                    .filter(Objects::nonNull)
                    .filter(p -> p.objectId() == id)
                    .findFirst().orElse(null);
        }

        if (target == null) {
            target = World.getAroundObjectById(this, id);
        }

        return (target == null) || target.isInvisible() ? null : target;
    }

    @Override
    public int getPAtk(final Creature target) {
        double init = getActiveWeaponInstance() == null ? (isMageClass() ? 3 : 4) : 0;
//        return (int) calcStat(Stats.POWER_ATTACK,  target, null);
        return (int) calcStat(Stats.POWER_ATTACK, init, target, null);
    }

    @Override
    public int getPDef(final Creature target) {
        double init = 4.; // empty cloak and underwear slots

        final ItemInstance chest = getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
        if (chest == null) {
            init += isMageClass() ? ArmorTemplate.EMPTY_BODY_MYSTIC : ArmorTemplate.EMPTY_BODY_FIGHTER;
        }
        if ((getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS) == null) && ((chest == null) || (chest.getBodyPart() != ItemTemplate.SLOT_FULL_ARMOR))) {
            init += isMageClass() ? ArmorTemplate.EMPTY_LEGS_MYSTIC : ArmorTemplate.EMPTY_LEGS_FIGHTER;
        }

        if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD) == null) {
            init += ArmorTemplate.EMPTY_HELMET;
        }
        if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES) == null) {
            init += ArmorTemplate.EMPTY_GLOVES;
        }
        if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET) == null) {
            init += ArmorTemplate.EMPTY_BOOTS;
        }

        return (int) calcStat(Stats.POWER_DEFENCE, init, target, null);
    }

    @Override
    public int getMDef(final Creature target, final Skill skill) {
        double init = 0.;

        if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR) == null) {
            init += ArmorTemplate.EMPTY_EARRING;
        }
        if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR) == null) {
            init += ArmorTemplate.EMPTY_EARRING;
        }
        if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK) == null) {
            init += ArmorTemplate.EMPTY_NECKLACE;
        }
        if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER) == null) {
            init += ArmorTemplate.EMPTY_RING;
        }
        if (getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER) == null) {
            init += ArmorTemplate.EMPTY_RING;
        }

        return (int) calcStat(Stats.MAGIC_DEFENCE, init, target, skill);
    }

    public boolean isSubClassActive() {
        return getBaseClassId() != getActiveClassId();
    }

    @Override
    public String getTitle() {
        return super.getTitle();
    }

    public int getTitleColor() {
        return _titlecolor;
    }

    public void setTitleColor(final int titlecolor) {
        if (titlecolor != DEFAULT_TITLE_COLOR) {
            setVar("titlecolor", Integer.toHexString(titlecolor));
        } else {
            unsetVar("titlecolor");
        }
        _titlecolor = titlecolor;
    }

    public boolean isCursedWeaponEquipped() {
        return cursedWeaponEquippedId != 0;
    }

    public int getCursedWeaponEquippedId() {
        return cursedWeaponEquippedId;
    }

    public void setCursedWeaponEquippedId(int value) {
        cursedWeaponEquippedId = value;
    }

    @Override
    public boolean isImmobilized() {
        return super.isImmobilized() || isOverloaded() || isSitting() || isFishing();
    }

    @Override
    public boolean isBlocked() {
        return super.isBlocked() || isInMovie() || isInObserverMode() || isTeleporting() || isLogoutStarted();
    }

    @Override
    public boolean isInvul() {
        return super.isInvul() || isInMovie();
    }

    private boolean isOverloaded() {
        return overloaded;
    }

    /**
     * if True, the L2Player can't take more item
     *
     * @param overloaded
     */
    private void setOverloaded(boolean overloaded) {
        this.overloaded = overloaded;
    }

    public boolean isFishing() {
        return isFishing;
    }

    public Fishing getFishing() {
        return fishing;
    }

    public void setFishing(boolean value) {
        isFishing = value;
    }

    public void startFishing(FishTemplate fish, int lureId) {
        fishing.setFish(fish);
        fishing.setLureId(lureId);
        fishing.startFishing();
    }

    public void stopFishing() {
        fishing.stopFishing();
    }

    public Location getFishLoc() {
        return fishing.getFishLoc();
    }


    public double getRateAdena() {
        return party == null ? 1. : party.rateAdena;
    }


    public double getRateItems() {
        return party == null ? 1. : party.rateDrop;
    }

    public double getRateExp() {
        return calcStat(Stats.EXP, (party == null ? 1. : party.rateExp));
    }

    @Override
    public double getRateSp() {
        return calcStat(Stats.SP, (party == null ? 1. : party.rateSp));
    }

    @Override
    public double getRateSpoil() {
        return party == null ? 1. : party.rateSpoil;
    }

    public boolean isUndying() {
        return _isUndying;
    }

    public void setUndying(boolean val) {
        if (!isGM()) {
            return;
        }
        _isUndying = val;
    }

    public void broadcastSnoop(int type, String name, String _text) {
        if (snoopListener.size() > 0) {
            Snoop sn = new Snoop(objectId(), getName(), type, name, _text);
            for (Player pci : snoopListener) {
                if (pci != null) {
                    pci.sendPacket(sn);
                }
            }
        }
    }

    public void addSnooper(Player pci) {
        if (!snoopListener.contains(pci)) {
            snoopListener.add(pci);
        }
    }

    public void removeSnooper(Player pci) {
        snoopListener.remove(pci);
    }

    public void addSnooped(Player pci) {
        if (!snoopedPlayer.contains(pci)) {
            snoopedPlayer.add(pci);
        }
    }

    public void removeSnooped(Player pci) {
        snoopedPlayer.remove(pci);
    }

    public void resetReuse() {
        skillReuses.clear();
        sharedGroupReuses.clear();
    }

    public DeathPenalty getDeathPenalty() {
        return activeClass.getDeathPenalty(this);
    }

    public boolean isCharmOfCourage() {
        return _charmOfCourage;
    }

    public void setCharmOfCourage(boolean val) {
        _charmOfCourage = val;

        if (!val) {
            getEffectList().stopEffect(5041);
        }

        sendEtcStatusUpdate();
    }

    @Override
    public int getIncreasedForce() {
        return increasedForce;
    }

    @Override
    public void setIncreasedForce(int i) {
        int numForce = (i < 0 ? 0 : Math.min(i, Charge.MAX_CHARGE));

        if ((numForce != 0) && (numForce > increasedForce))
            sendPacket(new SystemMessage(SystemMessage.YOUR_FORCE_HAS_INCREASED_TO_S1_LEVEL).addNumber(i));

        increasedForce = numForce;
        sendEtcStatusUpdate();
    }

    @Override
    public int getConsumedSouls() {
        return consumedSouls;
    }

    @Override
    public void setConsumedSouls(int i, NpcInstance monster) {
        if (i == consumedSouls) {
            return;
        }

        int max = (int) calcStat(Stats.SOULS_LIMIT, 0, monster, null);

        if (i > max) {
            i = max;
        }

        if (i <= 0) {
            consumedSouls = 0;
            sendEtcStatusUpdate();
            return;
        }

        if (consumedSouls != i) {
            int diff = i - consumedSouls;
            if (diff > 0) {
                SystemMessage sm = new SystemMessage(SystemMessage.YOUR_SOUL_HAS_INCREASED_BY_S1_SO_IT_IS_NOW_AT_S2);
                sm.addNumber(diff);
                sm.addNumber(i);
                sendPacket(sm);
            }
        } else if (max == i) {
            sendPacket(Msg.SOUL_CANNOT_BE_ABSORBED_ANY_MORE);
            return;
        }

        consumedSouls = i;
        sendPacket(new EtcStatusUpdate(this));
    }

    public boolean isFalling() {
        return (System.currentTimeMillis() - _lastFalling) < 5000;
    }

    public void falling(int height) {
        if (!Config.DAMAGE_FROM_FALLING || isDead() || isFlying() || isInWater() || isInBoat()) {
            return;
        }
        _lastFalling = System.currentTimeMillis();
        int damage = (int) calcStat(Stats.FALL, (getMaxHp() / 2000.) * height);
        if (damage > 0) {
            int curHp = (int) getCurrentHp();
            if ((curHp - damage) < 1) {
                setCurrentHp(1, false);
            } else {
                setCurrentHp(curHp - damage, false);
            }
            sendPacket(new SystemMessage(SystemMessage.YOU_RECEIVED_S1_DAMAGE_FROM_TAKING_A_HIGH_FALL).addNumber(damage));
        }
    }

    @Override
    public void checkHpMessages(double curHp, double newHp) {
        int[] _hp =
                {
                        30,
                        30
                };
        int[] skills =
                {
                        290,
                        291
                };

        int[] _effects_skills_id =
                {
                        139,
                        176,
                        292,
                        292,
                        420
                };
        int[] _effects_hp =
                {
                        30,
                        30,
                        30,
                        60,
                        30
                };

        double percent = getMaxHp() / 100.;
        double _curHpPercent = curHp / percent;
        double _newHpPercent = newHp / percent;
        boolean needsUpdate = false;

        // check for passive skills
        for (int i = 0; i < skills.length; i++) {
            int level = getSkillLevel(skills[i]);
            if (level > 0) {
                if ((_curHpPercent > _hp[i]) && (_newHpPercent <= _hp[i])) {
                    sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_DECREASED_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(skills[i], level));
                    needsUpdate = true;
                } else if ((_curHpPercent <= _hp[i]) && (_newHpPercent > _hp[i])) {
                    sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_INCREASED_THE_EFFECT_OF_S1_WILL_DISAPPEAR).addSkillName(skills[i], level));
                    needsUpdate = true;
                }
            }
        }

        // check for active effects
        for (int i = 0; i < _effects_skills_id.length; i++) {
            if (getEffectList().getEffectsBySkillId(_effects_skills_id[i]) != null) {
                if ((_curHpPercent > _effects_hp[i]) && (_newHpPercent <= _effects_hp[i])) {
                    sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_DECREASED_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(_effects_skills_id[i], 1));
                    needsUpdate = true;
                } else if ((_curHpPercent <= _effects_hp[i]) && (_newHpPercent > _effects_hp[i])) {
                    sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_INCREASED_THE_EFFECT_OF_S1_WILL_DISAPPEAR).addSkillName(_effects_skills_id[i], 1));
                    needsUpdate = true;
                }
            }
        }

        if (needsUpdate) {
            sendChanges();
        }
    }

    public void checkDayNightMessages() {
        int level = getSkillLevel(294);
        if (level > 0) {
            if (GameTimeController.INSTANCE.isNowNight()) {
                sendPacket(new SystemMessage(SystemMessage.IT_IS_NOW_MIDNIGHT_AND_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(294, level));
            } else {
                sendPacket(new SystemMessage(SystemMessage.IT_IS_DAWN_AND_THE_EFFECT_OF_S1_WILL_NOW_DISAPPEAR).addSkillName(294, level));
            }
        }
        sendChanges();
    }

    public int getZoneMask() {
        return _zoneMask;
    }

    @Override
    protected void onUpdateZones(List<Zone> leaving, List<Zone> entering) {
        super.onUpdateZones(leaving, entering);

        if (((leaving == null) || leaving.isEmpty()) && ((entering == null) || entering.isEmpty())) {
            return;
        }

        boolean lastInCombatZone = (_zoneMask & ZONE_PVP_FLAG) == ZONE_PVP_FLAG;
        boolean lastInDangerArea = (_zoneMask & ZONE_ALTERED_FLAG) == ZONE_ALTERED_FLAG;
        boolean lastOnSiegeField = (_zoneMask & ZONE_SIEGE_FLAG) == ZONE_SIEGE_FLAG;
        boolean lastInPeaceZone = (_zoneMask & ZONE_PEACE_FLAG) == ZONE_PEACE_FLAG;
        boolean lastInSSQZone = (_zoneMask & ZONE_SSQ_FLAG) == ZONE_SSQ_FLAG;

        boolean isInCombatZone = isInCombatZone();
        boolean isInDangerArea = isInDangerArea();
        boolean isOnSiegeField = isOnSiegeField();
        boolean isInPeaceZone = isInPeaceZone();
        boolean isInSSQZone = isInSSQZone();

        // update the compass, only if the character in the world
        int lastZoneMask = _zoneMask;
        _zoneMask = 0;

        if (isInCombatZone) {
            _zoneMask |= ZONE_PVP_FLAG;
        }
        if (isInDangerArea) {
            _zoneMask |= ZONE_ALTERED_FLAG;
        }
        if (isOnSiegeField) {
            _zoneMask |= ZONE_SIEGE_FLAG;
        }
        if (isInPeaceZone) {
            _zoneMask |= ZONE_PEACE_FLAG;
        }
        if (isInSSQZone) {
            _zoneMask |= ZONE_SSQ_FLAG;
        }

        if (lastZoneMask != _zoneMask) {
            sendPacket(new ExSetCompassZoneCode(this));
        }

        if (lastInCombatZone != isInCombatZone) {
            broadcastRelationChanged();
        }

        if (lastInDangerArea != isInDangerArea) {
            sendPacket(new EtcStatusUpdate(this));
        }

        if (lastOnSiegeField != isOnSiegeField) {
            broadcastRelationChanged();
            if (isOnSiegeField) {
                sendPacket(Msg.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
            } else {
                sendPacket(Msg.YOU_HAVE_LEFT_A_COMBAT_ZONE);
                if (!isTeleporting() && (getPvpFlag() == 0)) {
                    startPvPFlag(null);
                }
            }
        }

        if (lastInPeaceZone != isInPeaceZone) {
            if (isInPeaceZone) {
                setRecomTimerActive(false);
                if (getNevitSystem().isActive()) {
                    getNevitSystem().stopAdventTask(true);
                }
                startVitalityTask();
            } else {
                stopVitalityTask();
            }
        }

        if (isInWater()) startWaterTask();
        else stopWaterTask();
    }

    private void startAutoSaveTask() {
        if (!Config.AUTOSAVE) {
            return;
        }
        if (_autoSaveTask == null) {
            _autoSaveTask = AutoSaveManager.getInstance().addAutoSaveTask(this);
        }
    }

    private void stopAutoSaveTask() {
        if (_autoSaveTask != null) {
            _autoSaveTask.cancel(false);
        }
        _autoSaveTask = null;
    }

    private void startAutoChargeTask() {
        if (_autoChargeTask == null) {
            _autoChargeTask = AutoRechargeManager.getInstance().addAutoChargeTask(this);
        }
    }

    private void stopAutoChargeTask() {
        if (_autoChargeTask != null) {
            _autoChargeTask.cancel(false);
        }
        _autoChargeTask = null;
    }

    private void startVitalityTask() {
        if (!Config.ALT_VITALITY_ENABLED) {
            return;
        }
        if (_vitalityTask == null) {
            _vitalityTask = LazyPrecisionTaskManager.getInstance().addVitalityRegenTask(this);
        }
    }

    private void stopVitalityTask() {
        if (_vitalityTask != null) {
            _vitalityTask.cancel(false);
        }
        _vitalityTask = null;
    }

    private void startPcBangPointsTask() {
        if (!Config.ALT_PCBANG_POINTS_ENABLED || (Config.ALT_PCBANG_POINTS_DELAY <= 0)) {
            return;
        }
        if (_pcCafePointsTask == null) {
            _pcCafePointsTask = LazyPrecisionTaskManager.getInstance().addPCCafePointsTask(this);
        }
    }

    private void stopPcBangPointsTask() {
        if (_pcCafePointsTask != null) {
            _pcCafePointsTask.cancel(false);
        }
        _pcCafePointsTask = null;
    }

    public final boolean isInJail() {
        return isVarSet("jailed");
    }

    public void sendMessage(String message) {
        sendPacket(new SystemMessage(message));
    }

    public Location getLastClientPosition() {
        return _lastClientPosition;
    }

    public void setLastClientPosition(Location position) {
        _lastClientPosition = position;
    }

    public Location getLastServerPosition() {
        return _lastServerPosition;
    }

    public void setLastServerPosition(Location position) {
        _lastServerPosition = position;
    }

    public int getUseSeed() {
        return _useSeed;
    }

    public void setUseSeed(int id) {
        _useSeed = id;
    }

    private int getFriendRelation() {
        int result = 0;

        result |= RelationChanged.RELATION_CLAN_MEMBER;
        result |= RelationChanged.RELATION_CLAN_MATE;

        return result;
    }

    private int getWarRelation() {
        int result = 0;

        result |= RelationChanged.RELATION_CLAN_MEMBER;
        result |= RelationChanged.RELATION_1SIDED_WAR;
        result |= RelationChanged.RELATION_MUTUAL_WAR;

        return result;
    }

    public int getRelation(Player target) {
        if (getTeam() != TeamType.NONE && target.getTeam() != TeamType.NONE)
            return getTeam() == target.getTeam() ? getFriendRelation() : getWarRelation();

        int result = 0;

        if (getClan() != null) {
            result |= RelationChanged.RELATION_CLAN_MEMBER;
            if (getClan() == target.getClan()) {
                result |= RelationChanged.RELATION_CLAN_MATE;
            }
            if (getClan().getAllyId() != 0) {
                result |= RelationChanged.RELATION_ALLY_MEMBER;
            }
        }

        if (isClanLeader()) {
            result |= RelationChanged.RELATION_LEADER;
        }

        Party party = getParty();
        if ((party != null) && (party == target.getParty())) {
            result |= RelationChanged.RELATION_HAS_PARTY;

            switch (party.getMembers().indexOf(this)) {
                case 0:
                    result |= RelationChanged.RELATION_PARTYLEADER; // 0x10
                    break;
                case 1:
                    result |= RelationChanged.RELATION_PARTY4; // 0x8
                    break;
                case 2:
                    result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY2 + RelationChanged.RELATION_PARTY1; // 0x7
                    break;
                case 3:
                    result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY2; // 0x6
                    break;
                case 4:
                    result |= RelationChanged.RELATION_PARTY3 + RelationChanged.RELATION_PARTY1; // 0x5
                    break;
                case 5:
                    result |= RelationChanged.RELATION_PARTY3; // 0x4
                    break;
                case 6:
                    result |= RelationChanged.RELATION_PARTY2 + RelationChanged.RELATION_PARTY1; // 0x3
                    break;
                case 7:
                    result |= RelationChanged.RELATION_PARTY2; // 0x2
                    break;
                case 8:
                    result |= RelationChanged.RELATION_PARTY1; // 0x1
                    break;
            }
        }

        Clan clan1 = getClan();
        Clan clan2 = target.getClan();
        if ((clan1 != null) && (clan2 != null)) {
            if ((target.getPledgeType() != Clan.SUBUNIT_ACADEMY) && (getPledgeType() != Clan.SUBUNIT_ACADEMY)) {
                if (clan2.isAtWarWith(clan1.clanId())) {
                    result |= RelationChanged.RELATION_1SIDED_WAR;
                    if (clan1.isAtWarWith(clan2.clanId())) {
                        result |= RelationChanged.RELATION_MUTUAL_WAR;
                    }
                }
            }
            if (getBlockCheckerArena() != -1) {
                result |= RelationChanged.RELATION_INSIEGE;
                ArenaParticipantsHolder holder = HandysBlockCheckerManager.INSTANCE.getHolder(getBlockCheckerArena());
                if (holder.getPlayerTeam(this) == 0) {
                    result |= RelationChanged.RELATION_ENEMY;
                } else {
                    result |= RelationChanged.RELATION_ALLY;
                }
                result |= RelationChanged.RELATION_ATTACKER;
            }
        }

        for (GlobalEvent e : getEvents()) {
            result = e.getRelation(this, target, result);
        }

        return result;
    }

    public long getlastPvpAttack() {
        return _lastPvpAttack;
    }

    @Override
    public void startPvPFlag(Creature target) {
        if (karma > 0)
            return;

        long startTime = System.currentTimeMillis();
        if (target != null && (target.getPvpFlag() != 0 || target instanceof MonsterInstance))
            startTime -= Config.PVP_TIME / 2;

        if (pvpFlag != 0 && _lastPvpAttack > startTime)
            return;

        _lastPvpAttack = startTime;

        updatePvPFlag(1);

        if (_PvPRegTask == null)
            _PvPRegTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new PvPFlagTask(this), 1000, 1000);
    }

    public void stopPvPFlag() {
        if (_PvPRegTask != null) {
            _PvPRegTask.cancel(false);
            _PvPRegTask = null;
        }
        updatePvPFlag(0);
    }

    public void updatePvPFlag(int value) {
        if (_handysBlockCheckerEventArena != -1) {
            return;
        }
        if (pvpFlag == value) {
            return;
        }

        setPvpFlag(value);

        sendStatusUpdate(true, true, StatusUpdate.PVP_FLAG);

        broadcastRelationChanged();
    }

    @Override
    public int getPvpFlag() {
        return pvpFlag;
    }

    public void setPvpFlag(int pvpFlag) {
        this.pvpFlag = pvpFlag;
    }

    public boolean isInDuel() {
        return getEvent(DuelEvent.class) != null;
    }

    public Map<Integer, TamedBeastInstance> getTrainedBeasts() {
        return tamedBeasts;
    }

    public void addTrainedBeast(TamedBeastInstance tamedBeast) {
        tamedBeasts.put(tamedBeast.objectId(), tamedBeast);
    }

    public void removeTrainedBeast(int npcId) {
        tamedBeasts.remove(npcId);
    }

    public long getLastAttackPacket() {
        return _lastAttackPacket;
    }

    public void setLastAttackPacket() {
        _lastAttackPacket = System.currentTimeMillis();
    }

    public long getLastMovePacket() {
        return _lastMovePacket;
    }

    public void setLastMovePacket() {
        _lastMovePacket = System.currentTimeMillis();
    }

    public byte[] getKeyBindings() {
        return _keyBindings;
    }

    public void setKeyBindings(byte[] keyBindings) {
        _keyBindings = Objects.requireNonNullElseGet(keyBindings, () -> new byte[0]);
    }

    private void preparateToTransform(Skill transSkill) {
        if ((transSkill == null) || !transSkill.isBaseTransformation()) {
            getEffectList().getAllEffects().stream()
                    .filter(e -> e.skill.isToggle())
                    .forEach(Effect::exit);
        }
    }

    public boolean isInFlyingTransform() {
        return (transformationId == 8) || (transformationId == 9) || (transformationId == 260);
    }

    boolean isInMountTransform() {
        return (transformationId == 106) || (transformationId == 109) || (transformationId == 110) || (transformationId == 20001);
    }

    public boolean isTrasformed() {
        return transformationId != 0;
    }

    public int getTransformation() {
        return transformationId;
    }

    public void setTransformation(int transformationId) {
        if ((transformationId == this.transformationId) || ((this.transformationId != 0) && (transformationId != 0))) {
            return;
        }

        if (transformationId == 0) {
            getEffectList().getAllEffects().stream()
                    .filter(Objects::nonNull)
                    .filter(effect -> effect.getEffectType() == EffectType.Transformation)
                    .filter(e -> e.calc() != 0)
                    .peek(Effect::exit)
                    .map(e -> e.skill)
                    .findFirst().ifPresent(this::preparateToTransform);

            if (!transformationSkills.isEmpty()) {
                for (Skill s : transformationSkills.values()) {
                    if (!s.common && !SkillAcquireHolder.isSkillPossible(this, s) && !s.isHeroic) {
                        super.removeSkill(s);
                    }
                }
                transformationSkills.clear();
            }
        } else {
            int id = 0;
            if (transformSkills.containsKey(baseClass))
                transformSkills.get(baseClass);

            Skill skill = SkillTable.INSTANCE.getInfo(id);
            if (skill != null) {
                super.removeSkill(skill);
                removeSkillFromShortCut(skill.id);
            }

            if (!isCursedWeaponEquipped()) getEffectList().getAllEffects().stream()
                    .filter(effect -> effect.getEffectType() == EffectType.Transformation)
                    .findFirst().ifPresent(effect -> {
                        if ((effect.skill instanceof Transformation) && ((Transformation) effect.skill).isDisguise) {
                            for (Skill s : getAllSkills())
                                if ((s != null) && (s.isActive() || s.isToggle()))
                                    transformationSkills.put(s.id, s);
                        } else for (AddedSkill s : effect.skill.getAddedSkills())
                            if (s.level == 0) {
                                if (getSkillLevel(s.id) > 0)
                                    transformationSkills.put(s.id, SkillTable.INSTANCE.getInfo(s.id, getSkillLevel(s.id)));
                            } else if (s.level == -2) {// XXX: wild heartburn for skills depending on the getPlayer's occupation
                                int learnLevel = Math.max(effect.skill.magicLevel, 40);
                                int maxLevel = SkillTable.INSTANCE.getBaseLevel(s.id);
                                int curSkillLevel = 1;
                                if (maxLevel > 3) curSkillLevel += getLevel() - learnLevel;
                                else curSkillLevel += (getLevel() - learnLevel) / ((76 - learnLevel) / maxLevel);
                                curSkillLevel = Math.min(Math.max(curSkillLevel, 1), maxLevel);
                                transformationSkills.put(s.id, SkillTable.INSTANCE.getInfo(s.id, curSkillLevel));
                            } else transformationSkills.put(s.id, s.skill);
                        preparateToTransform(effect.skill);

                    });
            else {
                preparateToTransform(null);
            }

            if (!isInOlympiadMode() && !isCursedWeaponEquipped() && hero && (getBaseClassId() == getActiveClassId())) {
                transformationSkills.put(395, SkillTable.INSTANCE.getInfo(395));
                transformationSkills.put(396, SkillTable.INSTANCE.getInfo(396));
                transformationSkills.put(1374, SkillTable.INSTANCE.getInfo(1374));
                transformationSkills.put(1375, SkillTable.INSTANCE.getInfo(1375));
                transformationSkills.put(1376, SkillTable.INSTANCE.getInfo(1376));
            }

            for (Skill s : transformationSkills.values()) {
                addSkill(s, false);
            }
        }

        this.transformationId = transformationId;

        sendPacket(new ExBasicActionList(this));
        sendPacket(new SkillList(this));
        sendPacket(new ShortCutInit(this));
        getAutoSoulShot().forEach(shotId ->
                sendPacket(new ExAutoSoulShot(shotId, true)));

        broadcastUserInfo(true);
    }

    public String getTransformationName() {
        return transformationName;
    }

    public void setTransformationName(String name) {
        transformationName = name;
    }

    public int getTransformationTemplate() {
        return transformationTemplate;
    }

    public void setTransformationTemplate(int template) {
        transformationTemplate = template;
    }

    @Override
    public final Collection<Skill> getAllSkills() {
        if (transformationId == 0) return super.getAllSkills();

        Map<Integer, Skill> tempSkills = new HashMap<>();
        for (Skill s : super.getAllSkills()) {
            if ((s != null) && !s.isActive() && !s.isToggle()) {
                tempSkills.put(s.id, s);
            }
        }
        tempSkills.putAll(transformationSkills);
        return tempSkills.values();
    }

    public void setAgathion(int id) {
        if (_agathionId == id) {
            return;
        }

        _agathionId = id;
        broadcastCharInfo();
    }

    public int getAgathionId() {
        return _agathionId;
    }

    public int getPcBangPoints() {
        return _pcBangPoints;
    }

    public void setPcBangPoints(int val) {
        _pcBangPoints = val;
    }

    public void addPcBangPoints(int count, boolean doublePoints) {
        if (doublePoints) {
            count *= 2;
        }

        _pcBangPoints += count;

        sendPacket(new SystemMessage(doublePoints ? SystemMessage.DOUBLE_POINTS_YOU_AQUIRED_S1_PC_BANG_POINT : SystemMessage.YOU_ACQUIRED_S1_PC_BANG_POINT).addNumber(count));
        sendPacket(new ExPCCafePointInfo(this, count, 1, 2, 12));
    }

    public boolean reducePcBangPoints(int count) {
        if (_pcBangPoints < count) {
            return false;
        }

        _pcBangPoints -= count;
        sendPacket(new SystemMessage(SystemMessage.YOU_ARE_USING_S1_POINT).addNumber(count));
        sendPacket(new ExPCCafePointInfo(this, 0, 1, 2, 12));
        return true;
    }

    public Location getGroundSkillLoc() {
        return _groundSkillLoc;
    }

    public void setGroundSkillLoc(Location location) {
        _groundSkillLoc = location;
    }

    public boolean isLogoutStarted() {
        return _isLogout.get();
    }

    public void saveTradeList() {
        StringBuilder tradeListBuilder = new StringBuilder();

        if ((sellList == null) || sellList.isEmpty()) {
            unsetVar("selllist");
        } else {
            sellList.forEach(item -> tradeListBuilder.append(item.getObjectId()).append(";").append(item.getCount()).append(";").append(item.getOwnersPrice()).append(":"));
            setVar("selllist", tradeListBuilder.toString());
            tradeListBuilder.delete(0, tradeListBuilder.length());
            if ((_tradeList != null) && (getSellStoreName() != null)) {
                setVar("sellstorename", getSellStoreName());
            }
        }

        if ((packageSellList == null) || packageSellList.isEmpty()) {
            unsetVar("packageselllist");
        } else {
            packageSellList.forEach(item ->
                    tradeListBuilder.append(item.getObjectId()).append(";").append(item.getCount()).append(";").append(item.getOwnersPrice()).append(":"));
            setVar("packageselllist", tradeListBuilder.toString());
            tradeListBuilder.delete(0, tradeListBuilder.length());
            if ((_tradeList != null) && (getSellStoreName() != null)) {
                setVar("sellstorename", getSellStoreName());
            }
        }

        if ((_buyList == null) || _buyList.isEmpty()) {
            unsetVar("buylist");
        } else {
            for (TradeItem i : _buyList) {
                tradeListBuilder.append(i.getItemId()).append(";").append(i.getCount()).append(";").append(i.getOwnersPrice()).append(":");
            }
            setVar("buylist", tradeListBuilder.toString());
            tradeListBuilder.delete(0, tradeListBuilder.length());
            if ((_tradeList != null) && (getBuyStoreName() != null)) {
                setVar("buystorename", getBuyStoreName());
            }
        }

        if ((createList == null) || createList.isEmpty()) {
            unsetVar("createlist");
        } else {
            createList.forEach(item ->
                    tradeListBuilder.append(item.getRecipeId()).append(";").append(item.getCost()).append(":"));

            setVar("createlist", tradeListBuilder.toString());
            if (getManufactureName() != null) {
                setVar("manufacturename", getManufactureName());
            }
        }
    }

    private void restoreTradeList() {
        String var;
        var = getVar("selllist");
        if (var != null) {
            sellList = new CopyOnWriteArrayList<>();
            String[] items = var.split(":");
            for (String item : items) {
                if (item.equals("")) {
                    continue;
                }
                String[] values = item.split(";");
                if (values.length < 3) {
                    continue;
                }

                int oId = toInt(values[0]);
                long count = Long.parseLong(values[1]);
                long price = Long.parseLong(values[2]);

                ItemInstance itemToSell = getInventory().getItemByObjectId(oId);

                if ((count < 1) || (itemToSell == null)) {
                    continue;
                }

                if (count > itemToSell.getCount()) {
                    count = itemToSell.getCount();
                }

                TradeItem i = new TradeItem(itemToSell);
                i.setCount(count);
                i.setOwnersPrice(price);

                sellList.add(i);
            }
            var = getVar("sellstorename");
            if (var != null) {
                setSellStoreName(var);
            }
        }
        var = getVar("packageselllist");
        if (var != null) {
            packageSellList = new CopyOnWriteArrayList<>();
            String[] items = var.split(":");
            for (String item : items) {
                if (item.equals("")) {
                    continue;
                }
                String[] values = item.split(";");
                if (values.length < 3) {
                    continue;
                }

                int oId = toInt(values[0]);
                long count = Long.parseLong(values[1]);
                long price = Long.parseLong(values[2]);

                ItemInstance itemToSell = getInventory().getItemByObjectId(oId);

                if ((count < 1) || (itemToSell == null)) {
                    continue;
                }

                if (count > itemToSell.getCount()) {
                    count = itemToSell.getCount();
                }

                TradeItem i = new TradeItem(itemToSell);
                i.setCount(count);
                i.setOwnersPrice(price);

                packageSellList.add(i);
            }
            var = getVar("sellstorename");
            if (var != null) {
                setSellStoreName(var);
            }
        }
        var = getVar("buylist");
        if (var != null) {
            _buyList = new CopyOnWriteArrayList<>();
            String[] items = var.split(":");
            for (String item : items) {
                if (item.equals("")) {
                    continue;
                }
                String[] values = item.split(";");
                if (values.length < 3) {
                    continue;
                }
                TradeItem i = new TradeItem();
                i.setItemId(toInt(values[0]));
                i.setCount(Long.parseLong(values[1]));
                i.setOwnersPrice(Long.parseLong(values[2]));
                _buyList.add(i);
            }
            var = getVar("buystorename");
            if (var != null) {
                setBuyStoreName(var);
            }
        }
        var = getVar("createlist");
        if (var != null) {
            createList = new CopyOnWriteArrayList<>();
            String[] items = var.split(":");
            for (String item : items) {
                if (item.equals("")) {
                    continue;
                }
                String[] values = item.split(";");
                if (values.length < 2) {
                    continue;
                }
                int recId = toInt(values[0]);
                long price = Long.parseLong(values[1]);
                if (findRecipe(recId)) {
                    createList.add(new ManufactureItem(recId, price));
                }
            }
            var = getVar("manufacturename");
            if (var != null) {
                setManufactureName(var);
            }
        }
    }

    private void restoreRecipeBook(Connection con) {
        try (PreparedStatement statement = con.prepareStatement("SELECT id FROM character_recipebook WHERE char_id=?")) {
            statement.setInt(1, objectId());

            try (ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    int id = rset.getInt("id");
                    Recipe recipe = RecipeHolder.getInstance().getRecipeByRecipeId(id);
                    registerRecipe(recipe, false);
                }
            }
        } catch (SQLException e) {
            LOG.error("Error while restoring Recipe Book for Id " + objectId(), e);
        }
    }

    public DecoyInstance getDecoy() {
        return _decoy;
    }

    public void setDecoy(DecoyInstance decoy) {
        _decoy = decoy;
    }

    public int getMountType() {
        switch (getMountNpcId()) {
            case PetDataTable.STRIDER_WIND_ID:
            case PetDataTable.STRIDER_STAR_ID:
            case PetDataTable.STRIDER_TWILIGHT_ID:
            case PetDataTable.RED_STRIDER_WIND_ID:
            case PetDataTable.RED_STRIDER_STAR_ID:
            case PetDataTable.RED_STRIDER_TWILIGHT_ID:
            case PetDataTable.GUARDIANS_STRIDER_ID:
                return 1;
            case PetDataTable.WYVERN_ID:
                return 2;
            case PetDataTable.WGREAT_WOLF_ID:
            case PetDataTable.FENRIR_WOLF_ID:
            case PetDataTable.WFENRIR_WOLF_ID:
                return 3;
        }
        return 0;
    }

    @Override
    public double getColRadius() {
        if (isTrasformed()) {
            if (transformationTemplate == 32) {
                setTransformation(0);
            } else {
                final int template = transformationTemplate;
                if (template != 0) {
                    final NpcTemplate npcTemplate = NpcHolder.getTemplate(template);
                    if (npcTemplate != null) {
                        return npcTemplate.collisionRadius;
                    }
                }
            }
        } else if (isMounted()) {
            final int mountTemplate = getMountNpcId();
            if (mountTemplate != 0) {
                final NpcTemplate mountNpcTemplate = NpcHolder.getTemplate(mountTemplate);
                if (mountNpcTemplate != null) {
                    return mountNpcTemplate.collisionRadius;
                }
            }
        }
        return getBaseTemplate().collisionRadius;
    }

    @Override
    public double getColHeight() {
        if (isTrasformed()) {
            if (transformationTemplate == 32) {
                setTransformation(0);
            } else {
                final int template = transformationTemplate;
                if (template != 0) {
                    final NpcTemplate npcTemplate = NpcHolder.getTemplate(template);
                    if (npcTemplate != null) {
                        return npcTemplate.collisionHeight;
                    }
                }
            }
        } else if (isMounted()) {
            final int mountTemplate = getMountNpcId();
            if (mountTemplate != 0) {
                final NpcTemplate mountNpcTemplate = NpcHolder.getTemplate(mountTemplate);
                if (mountNpcTemplate != null) {
                    return mountNpcTemplate.collisionHeight;
                }
            }
        }
        return getBaseTemplate().collisionHeight;
    }

    @Override
    public Player setReflection(Reflection reflection) {
        if (getReflection() == reflection) {
            return this;
        }

        super.setReflection(reflection);

        if ((summon != null) && !summon.isDead()) {
            summon.setReflection(reflection);
        }

        if (reflection != ReflectionManager.DEFAULT) {
            int var = getVarInt("reflection");
            if (var == 0 || var != reflection.id) {
                setVar("reflection", reflection.id);
            }
        } else {
            unsetVar("reflection");
        }

        if (getActiveClass() != null) {
            inventory.validateItems();
            //_129_PailakaDevilsLegacy
            if ((getPet() != null) && ((getPet().getNpcId() == 14916) || (getPet().getNpcId() == 14917))) {
                getPet().unSummon();
            }
        }
        return this;
    }

    public boolean isTerritoryFlagEquipped() {
        ItemInstance weapon = getActiveWeaponInstance();
        return (weapon != null) && weapon.getTemplate().isTerritoryFlag();
    }

    public int getBuyListId() {
        return buyListId;
    }

    public void setBuyListId(int listId) {
        buyListId = listId;
    }

    public int getFame() {
        return fame;
    }

    public void setFame(int fame) {
        if (fame > this.fame) {
            sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_ACQUIRED_S1_REPUTATION_SCORE).addNumber(fame - this.fame));
        }

        // Alexander - Add the fame acquired to the stats
//		if (fame > fame)
//			addPlayerStats(Ranking.STAT_TOP_FAME_ACQUIRED, fame - fame);

        this.fame = fame;
        sendChanges();
    }

    public void addFame(int fame, String log) {
        fame = fame + this.fame;
        if ((log != null) && !log.isEmpty()) {
            Log.add(name + "|" + this.fame + "| +" + fame + "|" + log, "fame");
        }
        if (fame > this.fame) {
            int added = fame - this.fame;
            getCounters().fameAcquired += added;
            sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_ACQUIRED_S1_REPUTATION_SCORE).addNumber(added));
        }
        this.fame = fame;
        sendChanges();
    }

    private int getVitalityLevel(boolean blessActive) {
        return Config.ALT_VITALITY_ENABLED ? (blessActive ? 4 : _vitalityLevel) : 0;
    }

    public double getVitality() {
        return Config.ALT_VITALITY_ENABLED ? vitality : 0;
    }

    public void setVitality(double newVitality) {
        if (!Config.ALT_VITALITY_ENABLED) {
            return;
        }

        newVitality = Math.max(Math.min(newVitality, Config.VITALITY_LEVELS.get(4)), 0);

        if ((newVitality >= vitality) || (getLevel() >= 10)) {
            if (newVitality != vitality) {
                if (newVitality == 0) {
                    sendPacket(Msg.VITALITY_IS_FULLY_EXHAUSTED);
                } else if (newVitality == Config.VITALITY_LEVELS.get(4)) {
                    sendPacket(Msg.YOUR_VITALITY_IS_AT_MAXIMUM);
                }
            }

            vitality = newVitality;
        }

        int newLevel = 0;
        if (vitality >= Config.VITALITY_LEVELS.get(3)) {
            newLevel = 4;
        } else if (vitality >= Config.VITALITY_LEVELS.get(2)) {
            newLevel = 3;
        } else if (vitality >= Config.VITALITY_LEVELS.get(1)) {
            newLevel = 2;
        } else if (vitality >= Config.VITALITY_LEVELS.get(0)) {
            newLevel = 1;
        }

        if (_vitalityLevel > newLevel) {
            getNevitSystem().addPoints(1500);
        }

        if (_vitalityLevel != newLevel) {
            if (_vitalityLevel != -1) {
                sendPacket(newLevel < _vitalityLevel ? Msg.VITALITY_HAS_DECREASED : Msg.VITALITY_HAS_INCREASED);
            }
            _vitalityLevel = newLevel;
        }

        sendPacket(new ExVitalityPointInfo((int) vitality));
    }

    public void addVitality(double val) {
        setVitality(getVitality() + val);
    }

    public int getIncorrectValidateCount() {
        return incorrectValidateCount;
    }

    public void incIncorrectValidate() {
        incorrectValidateCount++;
    }

    public void resetIncorrectValidateCount() {
        incorrectValidateCount = 0;
    }

    public int getExpandInventory() {
        return _expandInventory;
    }

    public void setExpandInventory(int inventory) {
        _expandInventory = inventory;
    }

    public int getExpandWarehouse() {
        return _expandWarehouse;
    }

    public void setExpandWarehouse(int warehouse) {
        _expandWarehouse = warehouse;
    }

    public boolean isNotShowBuffAnim() {
        return _notShowBuffAnim;
    }

    public void setNotShowBuffAnim(boolean value) {
        _notShowBuffAnim = value;
    }

    public List<SchemeBufferInstance.PlayerScheme> getBuffSchemes() {
        return buffSchemes;
    }

    public SchemeBufferInstance.PlayerScheme getBuffSchemeById(int id) {
        return buffSchemes.stream()
                .filter(scheme -> scheme.schemeId == id)
                .findFirst().orElse(null);
    }

    public void enterMovieMode() {
        if (isInMovie()) {
            return;
        }

        setTarget(null);
        stopMove();
        setIsInMovie(true);
        sendPacket(new CameraMode(1));
    }

    public void leaveMovieMode() {
        setIsInMovie(false);
        sendPacket(new CameraMode(0));
        broadcastCharInfo();
    }

    public void specialCamera(GameObject target, int dist, int yaw, int pitch, int time, int duration) {
        sendPacket(new SpecialCamera(target.objectId(), dist, yaw, pitch, time, duration));
    }

    public void specialCamera(GameObject target, int dist, int yaw, int pitch, int time, int duration, int turn, int rise, int widescreen, int unk) {
        sendPacket(new SpecialCamera(target.objectId(), dist, yaw, pitch, time, duration, turn, rise, widescreen, unk));
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int id) {
        movieId = id;
    }

    public boolean isInMovie() {
        return isInMovie;
    }

    public void setIsInMovie(boolean state) {
        isInMovie = state;
    }

    public void showQuestMovie(SceneMovie movie) {
        if (isInMovie()) {
            return;
        }

        sendActionFailed();
        setTarget(null);
        stopMove();
        setMovieId(movie.getId());
        setIsInMovie(true);
        sendPacket(movie.packet(this));
    }

    public void showQuestMovie(int movieId) {
        if (isInMovie) return;

        sendActionFailed();
        setTarget(null);
        stopMove();
        setMovieId(movieId);
        setIsInMovie(true);
        sendPacket(new ExStartScenePlayer(movieId));
    }


    public final void reName(String name, boolean saveToDB) {
        setName(name);
        if (saveToDB) {
            saveNameToDB();
        }
        broadcastCharInfo();
    }

    public final void reName(String name) {
        reName(name, false);
    }

    private void saveNameToDB() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement st = con.prepareStatement("UPDATE characters SET char_name = ? WHERE obj_Id = ?")) {
            st.setString(1, getName());
            st.setInt(2, objectId());
            st.executeUpdate();
        } catch (SQLException e) {
            LOG.error("Error while saving Char Name", e);
        }
    }

    @Override
    public Player getPlayer() {
        return this;
    }

    private List<String> getStoredBypasses(boolean bbs) {
        if (bbs) {
            if (bypasses_bbs == null) {
                bypasses_bbs = new ArrayList<>();
            }
            return bypasses_bbs;
        }
        if (bypasses == null) {
            bypasses = new ArrayList<>();
        }
        return bypasses;
    }

    public void cleanBypasses(boolean bbs) {
        List<String> bypassStorage = getStoredBypasses(bbs);
        synchronized (bypassStorage) {
            bypassStorage.clear();
        }
    }

    public String encodeBypasses(String htmlCode, boolean bbs) {
        List<String> bypassStorage = getStoredBypasses(bbs);
        synchronized (bypassStorage) {
            return BypassManager.encode(htmlCode, bypassStorage, bbs);
        }
    }

    public DecodedBypass decodeBypass(String bypass) {
        BypassType bpType = BypassManager.getBypassType(bypass);
        boolean bbs = (bpType == BypassType.ENCODED_BBS) || (bpType == BypassType.SIMPLE_BBS);
        List<String> bypassStorage = getStoredBypasses(bbs);
        if ((bpType == BypassType.ENCODED) || (bpType == BypassType.ENCODED_BBS)) {
            return BypassManager.decode(bypass, bypassStorage, bbs, this);
        }
        if (bpType == BypassType.SIMPLE) {
            return new DecodedBypass(bypass, false).trim();
        }
        if ((bpType == BypassType.SIMPLE_BBS) && !bypass.startsWith("_bbsscripts")) {
            return new DecodedBypass(bypass, true).trim();
        }

        ICommunityBoardHandler handler = CommunityBoardManager.getCommunityHandler(bypass);
        if (handler != null) {
            return new DecodedBypass(bypass, handler).trim();
        }

        LOG.warn("Direct access to bypass: " + bypass + " / Player: " + getName());
        return null;
    }

    public int getTalismanCount() {
        return (int) calcStat(Stats.TALISMANS_LIMIT, 0);
    }

    public boolean getOpenCloak() {
        if (Config.ALT_OPEN_CLOAK_SLOT || isGM()) {
            return true;
        }
        return (int) calcStat(Stats.CLOAK_SLOT, 0) > 0;
    }

    public final void disableDrop(int time) {
        _dropDisabled = System.currentTimeMillis() + time;
    }

    public final boolean isDropDisabled() {
        return _dropDisabled > System.currentTimeMillis();
    }

    public ItemInstance getPetControlItem() {
        return _petControlItem;
    }

    public void setPetControlItem(ItemInstance item) {
        _petControlItem = item;
    }

    private void setPetControlItem(int itemObjId) {
        setPetControlItem(getInventory().getItemByObjectId(itemObjId));
    }

    public boolean isActive() {
        return isActive.get();
    }

    public void setActive() {
        setNonAggroTime(0);

        if (isActive.getAndSet(true)) {
            return;
        }

        onActive();
    }

    private void onActive() {
        setNonAggroTime(0L);

        if (getPetControlItem() != null) {
            ThreadPoolManager.INSTANCE.execute(() -> {
                if (getPetControlItem() != null) {
                    summonPet();
                }
            });
        }
    }

    public void summonPet() {
        if (getPet() != null) {
            return;
        }

        ItemInstance controlItem = getPetControlItem();
        if (controlItem == null) {
            return;
        }

        int npcId = PetDataTable.getSummonId(controlItem);
        if (npcId == 0) {
            return;
        }

        NpcTemplate petTemplate = NpcHolder.getTemplate(npcId);
        if (petTemplate == null) {
            return;
        }

        PetInstance pet = PetInstance.restore(controlItem, petTemplate, this);
        if (pet == null) {
            return;
        }

        setPet(pet);
        pet.setTitle(getName());

        if (!pet.isRespawned()) {
            pet.setCurrentHp(pet.getMaxHp(), false);
            pet.setCurrentMp(pet.getMaxMp());
            pet.setCurrentFed(pet.getMaxFed());
            pet.updateControlItem();
            pet.store();
        }

        pet.getInventory().restore();

        pet.setReflection(getReflection());
        pet.spawnMe(Location.findPointToStay(this, 50, 70));
        pet.setRunning();
        pet.setFollowMode(true);
        pet.getInventory().validateItems();

        if (pet instanceof PetBabyInstance) {
            ((PetBabyInstance) pet).startBuffTask();
        }
    }

    public Collection<TrapInstance> getTraps() {
        if (traps == null) {
            return null;
        }
        Collection<TrapInstance> result = new ArrayList<>();
        TrapInstance trap;
        for (Integer trapId : traps.keySet()) {
            if ((trap = (TrapInstance) GameObjectsStorage.getNpc(trapId)) != null) {
                result.add(trap);
            } else {
                traps.remove(trapId);
            }
        }
        return result;
    }

    public int getTrapsCount() {
        return traps == null ? 0 : traps.size();
    }

    public void addTrap(TrapInstance trap) {
        if (traps == null) {
            traps = new HashMap<>();
        }
        traps.put(trap.objectId(), trap.getStoredId());
    }

    public void removeTrap(TrapInstance trap) {
        Map<Integer, Integer> traps = this.traps;
        if ((traps == null) || traps.isEmpty()) {
            return;
        }
        traps.remove(trap.objectId());
    }

    public void destroyFirstTrap() {
        Map<Integer, Integer> traps = this.traps;
        if ((traps == null) || traps.isEmpty()) {
            return;
        }
        traps.keySet().stream()
                .filter(trapId -> Objects.nonNull(GameObjectsStorage.get(traps.get(trapId))))
                .map(trapId -> (TrapInstance) GameObjectsStorage.get(traps.get(trapId)))
                .findFirst().ifPresent(GameObject::deleteMe);
    }

    private void destroyAllTraps() {
        if ((traps == null) || traps.isEmpty()) {
            return;
        }
        traps.keySet().stream()
                .map(trapId -> (TrapInstance) GameObjectsStorage.get(traps.get(trapId)))
                .filter(Objects::nonNull)
                .forEach(GameObject::deleteMe);
    }

    public int getBlockCheckerArena() {
        return _handysBlockCheckerEventArena;
    }

    public void setBlockCheckerArena(byte arena) {
        _handysBlockCheckerEventArena = arena;
    }

    @Override
    public PlayerListenerList getListeners() {
        if (listeners == null) {
            synchronized (this) {
                if (listeners == null) {
                    listeners = new PlayerListenerList(this);
                }
            }
        }
        return (PlayerListenerList) listeners;
    }

    @Override
    public PlayerStatsChangeRecorder getStatsRecorder() {
        if (statsRecorder == null) {
            synchronized (this) {
                if (statsRecorder == null) {
                    statsRecorder = new PlayerStatsChangeRecorder(this);
                }
            }
        }
        return (PlayerStatsChangeRecorder) statsRecorder;
    }

    int getHoursInGame() {
        _hoursInGame++;
        return _hoursInGame;
    }

    public int getHoursInGames() {
        return _hoursInGame;
    }

    private void startHourlyTask() {
        _hourlyTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new HourlyTask(this), 3600000L, 3600000L);
    }

    private void stopHourlyTask() {
        if (_hourlyTask != null) {
            _hourlyTask.cancel(false);
            _hourlyTask = null;
        }
    }

    @Override
    public void setTeam(TeamType t) {
        super.setTeam(t);

        if (getPet() != null)
            getPet().sendChanges();
    }

    public long getPremiumPoints() {
        if (Config.GAME_POINT_ITEM_ID != -1)
            return inventory.getCountOf(Config.GAME_POINT_ITEM_ID);
        return 0;
    }

    public void reducePremiumPoints(final int val) {
        if (Config.GAME_POINT_ITEM_ID != -1) {
            ItemFunctions.removeItem(this, Config.GAME_POINT_ITEM_ID, val, "PremiumPoints");
        }
    }

    public void addPremiumPoints(final int val) {
        if (Config.GAME_POINT_ITEM_ID != -1) {
            ItemFunctions.addItem(this, Config.GAME_POINT_ITEM_ID, val, "PremiumPoints");
        }
    }

    public boolean isAgathionResAvailable() {
        return _agathionResAvailable;
    }

    public void setAgathionRes(boolean val) {
        _agathionResAvailable = val;
    }

    public boolean isClanAirShipDriver() {
        return isInBoat() && getBoat() instanceof ClanAirShip && (((ClanAirShip) getBoat()).getDriver() == this);
    }

    public String getSessionVar(String key) {
        if (_userSession == null) {
            return null;
        }
        return _userSession.get(key);
    }

    public void setSessionVar(String key, String val) {
        if (_userSession == null) {
            _userSession = new ConcurrentHashMap<>();
        }

        if ((val == null) || val.isEmpty()) {
            _userSession.remove(key);
        } else {
            _userSession.put(key, val);
        }
    }

    public FriendList getFriendList() {
        return friendList;
    }

    public void setNotShowTraders() {
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean b) {
        debug = b;
    }

    public void sendItemList(boolean show) {
        List<ItemInstance> items = getInventory().getItems();
        LockType lockType = getInventory().getLockType();
        List<Integer> lockItems = getInventory().getLockItems();

        int allSize = items.size();
        int questItemsSize = 0;
        int agathionItemsSize = 0;
        for (ItemInstance item : items) {
            if (item.getTemplate().isQuest()) {
                questItemsSize++;
            }
            if (item.getTemplate().getAgathionEnergy() > 0) {
                agathionItemsSize++;
            }
        }

        sendPacket(new ItemList(allSize - questItemsSize, items, show, lockType, lockItems));
        if (questItemsSize > 0) {
            sendPacket(new ExQuestItemList(questItemsSize, items, lockType, lockItems));
        }
        if (agathionItemsSize > 0) {
            sendPacket(new ExBR_AgathionEnergyInfo(agathionItemsSize, items));
        }
    }

    public int getBeltInventoryIncrease() {
        ItemInstance item = getInventory().getPaperdollItem(Inventory.PAPERDOLL_BELT);
        if ((item != null) && (item.getTemplate().getAttachedSkills() != null)) {
            for (Skill skill : item.getTemplate().getAttachedSkills()) {
                for (FuncTemplate func : skill.getAttachedFuncs()) {
                    if (func.stat == Stats.INVENTORY_LIMIT) {
                        return (int) func.value;
                    }
                }
            }
        }
        return 0;
    }

    public boolean checkCoupleAction(Player target) {
        if (target.getPrivateStoreType() != Player.STORE_PRIVATE_NONE) {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IN_PRIVATE_STORE).addName(target));
            return false;
        }
        if (target.isFishing) {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_FISHING).addName(target));
            return false;
        }
        if (target.isInCombat()) {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_COMBAT).addName(target));
            return false;
        }
        if (target.isCursedWeaponEquipped()) {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_CURSED_WEAPON_EQUIPED).addName(target));
            return false;
        }
        if (target.isInOlympiadMode()) {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_OLYMPIAD).addName(target));
            return false;
        }
        if (target.isOnSiegeField()) {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_SIEGE).addName(target));
            return false;
        }
        if (target.isInBoat() || (target.getMountNpcId() != 0)) {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_VEHICLE_MOUNT_OTHER).addName(target));
            return false;
        }
        if (target.isTeleporting()) {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_TELEPORTING).addName(target));
            return false;
        }
        if (target.isTrasformed()) {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_TRANSFORM).addName(target));
            return false;
        }
        if (target.isDead()) {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_DEAD).addName(target));
            return false;
        }
        return true;
    }

    @Override
    public void startAttackStanceTask() {
        startAttackStanceTask0();
        Summon summon = getPet();
        if (summon != null) {
            summon.startAttackStanceTask0();
        }
    }

    @Override
    public void displayGiveDamageMessage(Creature target, int damage, boolean crit, boolean miss, boolean shld, boolean magic) {
        super.displayGiveDamageMessage(target, damage, crit, miss, shld, magic);
        if (crit) {
            if (magic) {
                getCounters().mcritsDone++;
                sendPacket(new SystemMessage(SystemMessage.MAGIC_CRITICAL_HIT).addName(this));
            } else {
                getCounters().critsDone++;
                sendPacket(new SystemMessage(SystemMessage.C1_HAD_A_CRITICAL_HIT).addName(this));
            }
        }

        if (miss) {
            sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_WENT_ASTRAY).addName(this));
        } else if (!target.isDamageBlocked()) {
            sendPacket(new SystemMessage(SystemMessage.C1_HAS_GIVEN_C2_DAMAGE_OF_S3).addName(this).addName(target).addNumber(damage));
        }

        if (target instanceof Player) {
            if (shld && (damage > 1)) {
                target.sendPacket(SystemMsg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
            } else if (shld && (damage == 1)) {
                target.sendPacket(SystemMsg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
            }
        }
    }

    @Override
    public void displayReceiveDamageMessage(Creature attacker, int damage) {
        if (attacker != this) {
            sendPacket(new SystemMessage(SystemMessage.C1_HAS_RECEIVED_DAMAGE_OF_S3_FROM_C2).addString(getVisibleName()).addString(attacker.getVisibleName()).addNumber((long) damage));
        }
    }

    public Map<Integer, String> getPostFriends() {
        return postFriends;
    }

    public boolean isSharedGroupDisabled(int groupId) {
        TimeStamp sts = sharedGroupReuses.get(groupId);
        if (sts == null) {
            return false;
        }
        if (sts.hasNotPassed()) {
            return true;
        }
        sharedGroupReuses.remove(groupId);
        return false;
    }

    public TimeStamp getSharedGroupReuse(int groupId) {
        return sharedGroupReuses.get(groupId);
    }

    public void addSharedGroupReuse(int group, TimeStamp stamp) {
        sharedGroupReuses.put(group, stamp);
    }

    public Collection<Map.Entry<Integer, TimeStamp>> getSharedGroupReuses() {
        return sharedGroupReuses.entrySet();
    }

    public void sendReuseMessage(ItemInstance item) {
        TimeStamp sts = getSharedGroupReuse(item.getTemplate().getReuseGroup());
        if ((sts == null) || !sts.hasNotPassed()) {
            return;
        }

        long timeleft = sts.getReuseCurrent();
        long hours = timeleft / 3600000;
        long minutes = (timeleft - (hours * 3600000)) / 60000;
        long seconds = (long) Math.ceil((timeleft - (hours * 3600000) - (minutes * 60000)) / 1000.);

        if (hours > 0) {
            sendPacket(new SystemMessage2(item.getTemplate().getReuseType().getMessages()[2]).addItemName(item.getTemplate().itemId()).addInteger(hours).addInteger(minutes).addInteger(seconds));
        } else if (minutes > 0) {
            sendPacket(new SystemMessage2(item.getTemplate().getReuseType().getMessages()[1]).addItemName(item.getTemplate().itemId()).addInteger(minutes).addInteger(seconds));
        } else {
            sendPacket(new SystemMessage2(item.getTemplate().getReuseType().getMessages()[0]).addItemName(item.getTemplate().itemId()).addInteger(seconds));
        }
    }

    public NevitSystem getNevitSystem() {
        return _nevitSystem;
    }

    public void ask(ConfirmDlg dlg, OnAnswerListener listener) {
        if (_askDialog != null)
            return;

        int rnd = Rnd.nextInt();
        _askDialog = Pair.of(rnd, listener);
        dlg.setRequestId(rnd);
        sendPacket(dlg);

        // Ady - Set the resurrection max time to accept it to 5 minutes. After that it will be rejected. Only for players
        if (listener instanceof ReviveAnswerListener && !((ReviveAnswerListener) listener).isForPet())
            _resurrectionMaxTime = System.currentTimeMillis() + 5 * 60 * 1000;
    }

    public Pair<Integer, OnAnswerListener> getAskListener(boolean clear) {
        if (!clear) {
            return _askDialog;
        } else {
            Pair<Integer, OnAnswerListener> ask = _askDialog;
            _askDialog = null;
            return ask;
        }
    }

    public boolean hasDialogAskActive() {
        return _askDialog != null;
    }

    @Override
    public boolean isDead() {
        return (isInOlympiadMode() || isInDuel()) ? getCurrentHp() <= 1. : super.isDead();
    }

    @Override
    public int getAgathionEnergy() {
        ItemInstance item = inventory.getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
        return item == null ? 0 : item.getAgathionEnergy();
    }

    @Override
    public void setAgathionEnergy(int val) {
        ItemInstance item = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
        if (item == null) {
            return;
        }
        item.setAgathionEnergy(val);
        item.setJdbcState(JdbcEntityState.UPDATED);

        sendPacket(new ExBR_AgathionEnergyInfo(1, List.of(item)));
    }

    public boolean hasPrivilege(Privilege privilege) {
        return (clan != null) && ((getClanPrivileges() & privilege.mask()) == privilege.mask());
    }

    public MatchingRoom getMatchingRoom() {
        return _matchingRoom;
    }

    public void setMatchingRoom(MatchingRoom matchingRoom) {
        _matchingRoom = matchingRoom;
    }

    public void dispelBuffs() {
        getEffectList().getAllEffects().stream()
                .filter(e -> !e.skill.isOffensive)
                .filter(e -> !e.skill.isNewbie)
                .filter(Effect::isCancelable)
                .filter(e -> !e.skill.isPreservedOnDeath)
                .peek(e -> sendPacket(new SystemMessage(SystemMessage.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(e.skill.id, e.skill.level)))
                .forEach(Effect::exit);


        if (getPet() != null) {
            getPet().getEffectList().getAllEffects().stream()
                    .filter(e -> !e.skill.isOffensive)
                    .filter(e -> !e.skill.isNewbie)
                    .filter(Effect::isCancelable)
                    .filter(e -> !e.skill.isPreservedOnDeath)
                    .forEach(Effect::exit);
        }
    }

    public void setInstanceReuse(int id, long time) {
        final SystemMessage msg = new SystemMessage(SystemMessage.INSTANT_ZONE_FROM_HERE__S1_S_ENTRY_HAS_BEEN_RESTRICTED_YOU_CAN_CHECK_THE_NEXT_ENTRY_POSSIBLE).addString(getName());
        sendPacket(msg);
        _instancesReuses.put(id, time);
        mysql.set("REPLACE INTO character_instances (obj_id, id, reuse) VALUES (?,?,?)", objectId(), id, time);
    }

    public void removeInstanceReuse(int id) {
        if (_instancesReuses.remove(id) != null) {
            mysql.set("DELETE FROM `character_instances` WHERE `obj_id`=? AND `id`=? LIMIT 1", objectId(), id);
        }
    }

    public void removeAllInstanceReuses() {
        _instancesReuses.clear();
        mysql.set("DELETE FROM `character_instances` WHERE `obj_id`=?", objectId());
    }

    public void removeInstanceReusesByGroupId(int groupId) {
        InstantZoneHolder.getSharedReuseInstanceIdsByGroup(groupId).forEach(this::removeInstanceReuse);
    }

    public Long getInstanceReuse(int id) {
        return _instancesReuses.get(id);
    }

    public Map<Integer, Long> getInstanceReuses() {
        return _instancesReuses;
    }

    private void loadInstanceReuses(Connection con) {
        try (PreparedStatement offline = con.prepareStatement("SELECT * FROM character_instances WHERE obj_id = ?")) {
            offline.setInt(1, objectId());

            try (ResultSet rs = offline.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    long reuse = rs.getLong("reuse");
                    _instancesReuses.put(id, reuse);
                }
            }
        } catch (SQLException e) {
            LOG.error("Error while loading Instance Reuses for Id " + objectId(), e);
        }
    }

    public Reflection getActiveReflection() {
        return ReflectionManager.INSTANCE.getAll().stream()
                .filter(r -> r.getVisitors().contains(objectId()))
                .findFirst().orElse(null);
    }

    public boolean canEnterInstance(int instancedZoneId) {
        InstantZone iz = InstantZoneHolder.getInstantZone(instancedZoneId);

        if (isDead()) {
            return false;
        }

        if (ReflectionManager.INSTANCE.size() > Config.MAX_REFLECTIONS_COUNT) {
            sendPacket(SystemMsg.THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED);
            return false;
        }

        if (iz == null) {
            sendPacket(SystemMsg.SYSTEM_ERROR);
            return false;
        }

        if (ReflectionManager.INSTANCE.getCountByIzId(instancedZoneId) >= iz.getMaxChannels()) {
            sendPacket(SystemMsg.THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED);
            return false;
        }

        return iz.getEntryType().canEnter(this, iz);
    }

    public boolean canReenterInstance(int instancedZoneId) {
        InstantZone iz = InstantZoneHolder.getInstantZone(instancedZoneId);

        if ((getActiveReflection() != null) && (getActiveReflection().getInstancedZoneId() != instancedZoneId)) {
            sendPacket(SystemMsg.YOU_HAVE_ENTERED_ANOTHER_INSTANCE_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON);
            return false;
        }
        if (iz.isDispelBuffs()) {
            dispelBuffs();
        }
        return iz.getEntryType().canReEnter(this, iz);
    }

    public int getBattlefieldChatId() {
        return battlefieldChatId;
    }

    public void setBattlefieldChatId(int battlefieldChatId) {
        this.battlefieldChatId = battlefieldChatId;
    }

    @Override
    public Iterator<Player> iterator() {
        return Collections.singleton(this).iterator();
    }

    public PlayerGroup getPlayerGroup() {
        if (getParty() != null) {
            if (getParty().getCommandChannel() != null) {
                return getParty().getCommandChannel();
            } else {
                return getParty();
            }
        } else {
            return this;
        }
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public Player getLeader() {
        return this;
    }


    public List<Player> getMembers() {
        return List.of(this);
    }

    @Override
    public boolean containsMember(Player player) {
        return this == player;
    }

    public boolean isActionBlocked(String action) {
        return blockedActions.contains(action);
    }

    void blockActions(List<String> actions) {
        blockedActions.addAll(actions);
    }

    void unblockActions(List<String> actions) {
        blockedActions.removeAll(actions);
    }

    public OlympiadGame getOlympiadGame() {
        return olympiadGame;
    }

    public void setOlympiadGame(OlympiadGame olympiadGame) {
        this.olympiadGame = olympiadGame;
    }

    public boolean isInOlympiadObserverMode() {
        return olympiadObserveGame != null;
    }

    public OlympiadGame getOlympiadObserveGame() {
        return olympiadObserveGame;
    }

    public void addRadar(Location loc) {
        sendPacket(new RadarControl(0, 1, loc));
    }

    public void addRadarWithMap(Location loc) {
        sendPacket(new RadarControl(0, 2, loc));
    }

    public int getLectureMark() {
        return lectureMark;
    }

    public void setLectureMark(int lectureMark) {
        this.lectureMark = lectureMark;
    }

    public void setIsBBSUse(boolean value) {
        is_bbs_use = value;
    }

    public boolean isBBSUse() {
        return is_bbs_use;
    }

    private void restoreCursedWeapon() {
        for (ItemInstance item : getInventory().getItems()) {
            if (item.isCursed()) {
                int skillLvl = CursedWeaponsManager.INSTANCE.getLevel(item.getItemId());
                if (item.getItemId() == 8190) {
                    addSkill(3603, skillLvl, false);
                } else if (item.getItemId() == 8689) {
                    addSkill(3629, skillLvl, false);
                }
            }
        }
        updateStats();
    }

    public boolean isInSameClan(Player target) {
        return getClanId() != 0 && getClanId() == target.getClanId();
    }

    public Skill getMacroSkill() {
        return macroSkill;
    }

    public void setMacroSkill(Skill skill) {
        macroSkill = skill;
    }

    public int getVisibleNameColor() {
        if (_visibleNameColor != 0)
            return _visibleNameColor;

        return getNameColor();
    }

    public void setVisibleNameColor(final int nameColor) {
        _visibleNameColor = nameColor;
    }

    public int getVisibleTitleColor() {
        if (_visibleTitleColor != 0)
            return _visibleTitleColor;

        return getTitleColor();
    }

    public void setVisibleTitleColor(final int nameColor) {
        _visibleTitleColor = nameColor;
    }

    public void setLastAugmentNpc(NpcInstance npc) {
        lastAugmentNpc = npc;
    }

    public boolean checkLastAugmentNpc() {
        if (lastAugmentNpc == null)
            return false;

        if (!PositionUtils.checkIfInRange(300, this, lastAugmentNpc, true)) {
            lastAugmentNpc = null;
            return false;
        }

        return true;
    }

    @Override
    public String getVisibleName() {
        if (_visibleName != null)
            return _visibleName;

        return getName();
    }

    public String getVisibleTitle() {
        if (_visibleTitle != null)
            return _visibleTitle;

        return getTitle();
    }

    public void setVisibleTitle(final String title) {
        _visibleTitle = title;
    }

    // Offline buff store function
    public void offlineBuffStore() {
        if (connection != null) {
            connection.setActiveChar(null);
            connection.close(ServerClose.STATIC);
            setNetConnection(null);
        }
        setOnlineTime(getOnlineTime());
        setUptime(0);

        Party party = getParty();
        if (party != null) {
            if (isFestivalParticipant()) {
                party.getMembers().forEach(m -> m.sendMessage(getName() + " has been removed from the upcoming festival."));
            }
            leaveParty();
        }

        if (getPet() != null) {
            getPet().unSummon();
        }

        CursedWeaponsManager.INSTANCE.doLogout(this);

        if (isInOlympiadMode() || (getOlympiadGame() != null)) {
            Olympiad.logoutPlayer(this);
        }

        if (isInObserverMode()) {
            if (getOlympiadObserveGame() == null) {
                leaveObserverMode();
            } else {
                leaveOlympiadObserverMode(true);
            }
            observerMode.set(OBSERVER_NONE);
        }

        setVisibleNameColor(Config.BUFF_STORE_OFFLINE_NAME_COLOR);
        broadcastCharInfo();

        // Guardamos el offline buffer en la db al salir
        OfflineBuffersTable.INSTANCE.onLogout(this);

        // Stop all tasks
        stopWaterTask();
        stopHourlyTask();
        stopVitalityTask();
        stopPcBangPointsTask();
        stopAutoSaveTask();
        stopAutoChargeTask();
        stopRecomBonusTask(true);
        stopQuestTimers();
        getNevitSystem().stopTasksOnLogout();

        getInventory().store();

        store(false);
    }

    @Override
    public boolean isInZoneBattle() {
        // Ady - If the getPlayer is in a Gm Event and is a pvp event, then its in a zone battle also
        if (GmEventManager.INSTANCE.isParticipating(this) && GmEventManager.INSTANCE.isPvPEvent())
            return true;

        return super.isInZoneBattle();
    }

    @Override
    public boolean isInZonePeace() {
        // Ady - If the getPlayer is in a Gm Event and is a peace event, then its in a peace zone
        if (GmEventManager.INSTANCE.isParticipating(this) && GmEventManager.INSTANCE.isPeaceEvent())
            return true;

        return super.isInZonePeace();
    }

    public int getOrdered() {
        return _ordered;
    }

    public void setOrdered(int ordered) {
        _ordered = ordered;
        broadcastUserInfo(true);
    }


    /**
     * Alexander - This is used to transfer the skill reuse to a new skill.
     * This happens when a getPlayer occupation up or enchants an skill, its reused is lost due to its hashCode
     */
    private void disableSkillByNewLvl(int oldSkillReuseHashCode, int newSkillReuseHashCode) {
        if (oldSkillReuseHashCode == newSkillReuseHashCode)
            return;

        final TimeStamp timeStamp = skillReuses.get(oldSkillReuseHashCode);
        if (timeStamp == null)
            return;

        skillReuses.remove(oldSkillReuseHashCode);

        if (timeStamp.endTime() <= 0 || timeStamp.endTime() < System.currentTimeMillis())
            return;

        skillReuses.put(newSkillReuseHashCode, timeStamp);
    }

    public void setIsEnchantAllAttribute(boolean isEnchantAllAttribute) {
        _isEnchantAllAttribute = isEnchantAllAttribute;
    }

    public boolean isEnchantAllAttribute() {
        return _isEnchantAllAttribute;
    }

    public Map<Integer, Integer> getAchievements(int category) {
        Map<Integer, Integer> result = new HashMap<>();
        for (Entry<Integer, Integer> entry : _achievementLevels.entrySet()) {
            int achievementId = entry.getKey();
            int achievementLevel = entry.getValue();
            Achievement ach = Achievements.INSTANCE.getAchievement(achievementId, Math.max(1, achievementLevel));
            if (ach != null && ach.getCategoryId() == category)
                result.put(achievementId, achievementLevel);
        }
        return result;
    }

    public Map<Integer, Integer> getAchievements() {
        return _achievementLevels;
    }

    private void loadAchivements() {
        String achievements = getVar("achievements");
        if (achievements != null && !achievements.isEmpty()) {
            List<String> levels = List.of(achievements.split(";"));
            for (String ach : levels) {
                String[] lvl = ach.split(",");

                // Check if achievement exists.
                if (Achievements.INSTANCE.getMaxLevel(toInt(lvl[0])) > 0)
                    _achievementLevels.put(toInt(lvl[0]), toInt(lvl[1]));
            }
        }

        for (int achievementId : Achievements.INSTANCE.getAchievementIds())
            if (!_achievementLevels.containsKey(achievementId))
                _achievementLevels.put(achievementId, 0);
    }

    private void saveAchivements() {
        StringBuilder str = new StringBuilder();
        _achievementLevels.forEach((k, v) ->
                str.append(k).append(",").append(v).append(";"));

        setVar("achievements", str.toString());
    }

    public boolean haveAllItems(Collection<Integer> itemIds) {
        return itemIds.stream().allMatch(this::haveItem);
    }

    public boolean haveAllItems(int... itemIds) {
        return Arrays.stream(itemIds).allMatch(this::haveItem);
    }

    public boolean haveAnyItem(int... itemIds) {
        return Arrays.stream(itemIds).anyMatch(this::haveItem);
    }

    public boolean haveItem(int itemId) {
        return inventory.isItemPresent(itemId);
    }

    public boolean haveItem(int itemId, long count) {
        return inventory.getCountOf(itemId) >= count;
    }

    public boolean haveAdena(long count) {
        return haveItem(ADENA_ID, count);
    }

    public PlayerCounters getCounters() {
        if (playerCountersExtension == null) {
            synchronized (this) {
                if (playerCountersExtension == null)
                    playerCountersExtension = new PlayerCounters(this);
            }
        }
        return playerCountersExtension;
    }

    public void broadcastSkillOrSocialAnimation(int id, int level, int hitTime) {
        if (isAlikeDead())
            return;

        boolean performSocialAction = (level < 1);

        if (!performSocialAction)
            broadcastPacket(new MagicSkillUse(this, id, level, hitTime));
        else
            broadcastPacket(new SocialAction(objectId(), id));
    }

    public void updateSoloInstance() {
        this.soloInstance++;
    }

    public void updatePartyInstance() {
        this.partyInstance++;
    }

    private int getPartyInstance() {
        return partyInstance;
    }

    //automp
    public void AutoMp(boolean flag) {
        _autoMp = flag;
        sendPacket(new ExAutoSoulShot(autoMp, flag));
    }

    public void AutoCp(boolean flag) {
        _autoCp = flag;
        sendPacket(new ExAutoSoulShot(autoCp, flag));
    }

    public void AutoHp(boolean flag) {
        _autoHp = flag;
        sendPacket(new ExAutoSoulShot(autoHp, flag));
    }

    public void setLastMonsterDamageTime() {
    }

    private class UpdateEffectIcons extends RunnableImpl {
        @Override
        public void runImpl() {
            updateEffectIconsImpl();
            updateEffectIconsTask = null;
        }
    }

    public class BroadcastCharInfoTask extends RunnableImpl {
        @Override
        public void runImpl() {
            broadcastCharInfoImpl();
            broadcastCharInfoTask = null;
        }
    }

}
