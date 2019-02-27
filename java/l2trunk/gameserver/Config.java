package l2trunk.gameserver;

import l2trunk.commons.configuration.ExProperties;
import l2trunk.commons.lang.FileUtils;
import l2trunk.commons.net.AdvIP;
import l2trunk.commons.net.nio.impl.SelectorConfig;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.model.base.PlayerAccess;
import l2trunk.gameserver.network.loginservercon.ServerType;
import l2trunk.gameserver.utils.AddonsConfig;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class Config {

    public static final Path CONFIG = Paths.get("config");
    public static final Path OLYMPIAD = CONFIG.resolve("olympiad.ini");
    /**
     * events
     */
    public static final List<Integer> VITALITY_LEVELS = List.of(240, 2000, 13000, 17000, 20000);
    public static final List<AdvIP> GAMEIPS = new ArrayList<>();
    public static final int CHATFILTER_WORK_TYPE = 1;
    public static final List<Location> HEIN_FIELDS_LOCATIONS = new ArrayList<>();
    public static final Map<Integer, Integer> NpcBuffer_BuffSetMage = new ConcurrentHashMap<>();
    public static final Map<Integer, Integer> NpcBuffer_BuffSetFighter = new ConcurrentHashMap<>();
    public static final Map<Integer, Integer> NpcBuffer_BuffSetDagger = new ConcurrentHashMap<>();
    public static final Map<Integer, Integer> NpcBuffer_BuffSetSupport = new ConcurrentHashMap<>();
    public static final Map<Integer, Integer> NpcBuffer_BuffSetTank = new ConcurrentHashMap<>();
    public static final Map<Integer, Integer> NpcBuffer_BuffSetArcher = new ConcurrentHashMap<>();
    public static final List<Integer> SERVICES_AUGMENTATION_DISABLED_LIST = new ArrayList<>();
    public static final int CNAME_MAXLEN = 32;
    public static final List<Integer> ALT_OLY_DATE_END = new ArrayList<>();
    public static final Map<Integer, PlayerAccess> gmlist = new HashMap<>();
    public static final List<Integer> ALLOW_CLASS_MASTERS_LIST = new ArrayList<>();
    public static final int[] CLASS_MASTERS_PRICE_LIST = new int[4];
    public static final boolean GOODS_INVENTORY_ENABLED = false;
    public static final int[] RWHO_ARRAY = new int[13];
    public final static List<String> TRADE_WORDS = new ArrayList<>();
    /**
     * Network settings
     */
    static final SelectorConfig SELECTOR_CONFIG = new SelectorConfig();
    private static final int NCPUS = Runtime.getRuntime().availableProcessors();
    private static final Path OTHER_CONFIG_FILE = CONFIG.resolve("mod/other.ini");
    private static final Path RESIDENCE_CONFIG_FILE = CONFIG.resolve("residence.ini");
    private static final Path SPOIL_CONFIG_FILE = CONFIG.resolve("spoil.ini");
    private static final Path ALT_SETTINGS_FILE = CONFIG.resolve("altsettings.ini");
    private static final Path PVP_CONFIG_FILE = CONFIG.resolve("pvp.ini");
    private static final Path TELNET_CONFIGURATION_FILE = CONFIG.resolve("telnet.ini");
    private static final Path CONFIGURATION_FILE = CONFIG.resolve("server.ini");
    private static final Path AI_CONFIG_FILE = CONFIG.resolve("ai.ini");
    private static final Path GEODATA_CONFIG_FILE = CONFIG.resolve("geodata.ini");
    private static final Path EVENTS_CONFIG_FILE = CONFIG.resolve("events/events.ini");
    private static final Path SERVICES_FILE = CONFIG.resolve("services/services.ini");
    private static final Path DEVELOP_FILE = CONFIG.resolve("develop.ini");
    private static final Path EXT_FILE = CONFIG.resolve("ext.ini");
    private static final Path RATES_FILE = CONFIG.resolve("rates.ini");
    private static final Path CHAT_FILE = CONFIG.resolve("chat.ini");
    private static final Path NPC_FILE = CONFIG.resolve("npc.ini");
    private static final Path BOSS_FILE = CONFIG.resolve("boss.ini");
    private static final Path EPIC_BOSS_FILE = CONFIG.resolve("epic.ini");
    private static final Path ITEM_USE_FILE = CONFIG.resolve("UseItems.ini");
    private static final Path INSTANCES_FILE = CONFIG.resolve("instances.ini");
    private static final Path ITEMS_FILE = CONFIG.resolve("items.ini");
    private static final Path NPCBUFFER_CONFIG_FILE = CONFIG.resolve("npcbuffer.ini");
    private static final Path l2f_TEAM_CONFIG_FILE = CONFIG.resolve("DonatorManager.ini");
    private static final Path GM_PERSONAL_ACCESS_FILE = CONFIG.resolve("GMAccess.xml");
    private static final Path GM_ACCESS_FILES_DIR = CONFIG.resolve("GMAccess.d/");
    private static final Path COMMANDS_CONFIG_FILE = CONFIG.resolve("mod/commands.ini");
    private static final Path TALKING_GUARD_CONFIG_FILE = CONFIG.resolve("mod/TalkingGuard.ini");
    private static final Path VIKTORINA_CONFIG_FILE = CONFIG.resolve("events/Victorina.ini");
    private static final Path PVP_MOD_CONFIG_FILE = CONFIG.resolve("mod/PvPmod.ini");
    /**
     * Community PvP
     */
    private static final Path BOARD_MANAGER_CONFIG_FILE = CONFIG.resolve("CommunityPvP/board_manager.ini");
    private static final Path CLASS_MASTER_CONFIG_FILE = CONFIG.resolve("CommunityPvP/class_master.ini");
    private static final Path SHOP_MANAGER_CONFIG_FILE = CONFIG.resolve("CommunityPvP/shop_manager.ini");
    private static final Path BUFF_STORE_CONFIG_FILE = CONFIG.resolve("mod/OfflineBuffer.ini");
    private static final Path FORGE_CONFIG_FILE = CONFIG.resolve("services/forge.ini");
    private static final Logger _log = LoggerFactory.getLogger(Config.class);
    public static boolean EVENT_HITMAN_ENABLED;
    public static int EVENT_HITMAN_COST_ITEM_ID;
    public static int EVENT_HITMAN_COST_ITEM_COUNT;
    public static int EVENT_HITMAN_TASKS_PER_PAGE;
    public static List<Integer> EVENT_HITMAN_ALLOWED_ITEM_LIST;
    public static int HTM_CACHE_MODE;
    public static boolean LOG_SERVICES;
    public static boolean ALLOW_ADDONS_CONFIG;
    public static List<Integer> GAME_PORT;
    public static String DATABASE_DRIVER;
    public static int DATABASE_MAX_CONNECTIONS;
    public static int DATABASE_MAX_IDLE_TIMEOUT;
    public static int DATABASE_IDLE_TEST_PERIOD;
    public static String DATABASE_GAME_URL = "jdbc:mysql://localhost/l2mythras?UseUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
    public static String DATABASE_GAME_USER = "root";
    public static String DATABASE_GAME_PASSWORD = "root";
    public static String DATABASE_LOGIN_URL;
    public static String DATABASE_LOGIN_USER;
    public static String DATABASE_LOGIN_PASSWORD;
    // Database additional options
    public static boolean AUTOSAVE;
    public static long USER_INFO_INTERVAL;
    public static long BROADCAST_CHAR_INFO_INTERVAL;
    public static int EFFECT_TASK_MANAGER_COUNT;
    public static int MAXIMUM_ONLINE_USERS;
    public static boolean DONTLOADSPAWN;
    public static boolean DONTLOADQUEST;

    // Donation Store
    public static int MAX_REFLECTIONS_COUNT;
    public static long ALT_AFTER_CANCEL_RETURN_SKILLS_TIME;
    public static int SHIFT_BY;
    public static int SHIFT_BY_Z;
    public static int MAP_MIN_Z;
    public static int MAP_MAX_Z;
    /**
     * ChatBan
     */
    public static int CHAT_MESSAGE_MAX_LEN;
    public static int CHATFILTER_MIN_LEVEL = 0;
    public static int SHOUT_REQUIRED_LEVEL;
    public static int ALT_MAIL_MIN_LVL;
    public static int ALT_ADD_RECIPES;
    public static int ALT_MAX_ALLY_SIZE;
    public static int ALT_LEVEL_DIFFERENCE_PROTECTION;
    public static int ALT_PARTY_DISTRIBUTION_RANGE;
    public static List<Integer> ALT_PARTY_BONUS;
    public static boolean ALT_REMOVE_SKILLS_ON_DELEVEL;
    public static boolean ALT_VITALITY_ENABLED;
    public static double ALT_VITALITY_RATE;
    public static double ALT_VITALITY_CONSUME_RATE;
    public static int ALT_VITALITY_RAID_BONUS;
    public static boolean SERVICES_EXCHANGE_EQUIP;
    // L2Mythras Configs
    public static int DONATOR_NPC_ITEM;
    public static String DONATOR_NPC_ITEM_NAME;
    public static int DONATOR_NPC_COUNT_FAME;
    public static int DONATOR_NPC_FAME;
    public static int DONATOR_NPC_COUNT_REP;
    public static int DONATOR_NPC_REP;
    public static int DONATOR_NPC_COUNT_NOBLESS;
    public static int DONATOR_NPC_COUNT_SEX;
    public static int DONATOR_NPC_COUNT_LEVEL;
    /**
     * Ancient Herb
     */
    public static int ANCIENT_HERB_SPAWN_RADIUS;
    public static int ANCIENT_HERB_SPAWN_CHANCE;
    public static int ANCIENT_HERB_SPAWN_COUNT;
    public static int ANCIENT_HERB_RESPAWN_TIME = 60 * 1000;
    public static int ANCIENT_HERB_DESPAWN_TIME = 60 * 1000;
    public static int NECROMANCER_MS_CHANCE;
    public static double DWARRIOR_MS_CHANCE;
    public static double DHUNTER_MS_CHANCE;
    public static int BDRAKE_MS_CHANCE;
    public static int EDRAKE_MS_CHANCE;
    public static int VITAMIN_PETS_FOOD_ID;
    public static int VITAMIN_DESELOT_FOOD_ID;
    public static int VITAMIN_SUPERPET_FOOD_ID;
    public static boolean ALLOW_PET_ATTACK_MASTER;
    // Scheme Buffer
    public static boolean NpcBuffer_VIP;
    public static int NpcBuffer_VIP_ALV;
    public static boolean NpcBuffer_EnableScheme;
    public static boolean NpcBuffer_EnableHeal;
    public static boolean NpcBuffer_EnableBuffs;
    public static boolean NpcBuffer_EnableResist;
    public static boolean NpcBuffer_EnableSong;
    public static boolean NpcBuffer_EnableDance;
    public static boolean NpcBuffer_EnableChant;
    public static boolean NpcBuffer_EnableOther;
    public static boolean NpcBuffer_EnableSpecial;
    public static boolean NpcBuffer_EnableCubic;
    public static boolean NpcBuffer_EnableCancel;
    public static boolean NpcBuffer_EnableBuffSet;
    public static boolean NpcBuffer_EnableBuffPK;
    public static boolean NpcBuffer_EnableFreeBuffs;
    public static int NpcBuffer_MinLevel;
    public static int NpcBuffer_PriceCancel;
    public static int NpcBuffer_PriceHeal;
    public static int NpcBuffer_PriceBuffs;
    public static int NpcBuffer_PriceResist;
    public static int NpcBuffer_PriceSong;
    public static int NpcBuffer_PriceDance;
    public static int NpcBuffer_PriceChant;
    public static int NpcBuffer_PriceOther;
    public static int NpcBuffer_PriceSpecial;
    public static int NpcBuffer_PriceCubic;
    public static int NpcBuffer_PriceSet;
    public static int NpcBuffer_PriceScheme;
    public static int NpcBuffer_MaxScheme;
    public static boolean SCHEME_ALLOW_FLAG;
    /**
     * Community Board PVP
     */
    public static boolean ALLOW_BBS_WAREHOUSE;
    public static boolean BBS_WAREHOUSE_ALLOW_PK;
    public static boolean BBS_PVP_ALLOW_SELL;
    public static boolean BBS_PVP_ALLOW_BUY;
    public static boolean BBS_PVP_ALLOW_AUGMENT;
    public static boolean ENABLE_AUCTION_SYSTEM;
    public static long AUCTION_FEE;
    public static boolean ALLOW_AUCTION_OUTSIDE_TOWN;
    public static int SECONDS_BETWEEN_ADDING_AUCTIONS;
    public static boolean AUCTION_PRIVATE_STORE_AUTO_ADDED;
    public static boolean ALLOW_DROP_CALCULATOR;
    public static boolean ALLOW_SENDING_IMAGES;
    public static int TalkGuardChance;
    public static int TalkNormalChance = 0;
    public static int TalkNormalPeriod = 0;
    public static int TalkAggroPeriod = 0;
    public static boolean SERVICES_RIDE_HIRE_ENABLED;
    public static boolean SERVICES_DELEVEL_ENABLED;
    public static boolean ALLOW_MAIL_OPTION;
    public static int SERVICES_DELEVEL_ITEM;
    public static int SERVICES_DELEVEL_COUNT;
    public static int SERVICES_DELEVEL_MIN_LEVEL;
    public static int SERVICES_HAIR_CHANGE_ITEM_ID;
    public static int SERVICES_HAIR_CHANGE_COUNT;
    public static boolean SERVICES_LEVEL_UP_ENABLE;
    public static int[] SERVICES_LEVEL_UP;
    public static boolean SERVICES_DELEVEL_ENABLE;
    public static int[] SERVICES_DELEVEL;
    public static int SERVICES_CHANGE_Title_COLOR_PRICE;
    public static int SERVICES_CHANGE_Title_COLOR_ITEM;
    public static String[] SERVICES_CHANGE_Title_COLOR_LIST;
    public static int SERVICES_AUGMENTATION_PRICE;
    public static int SERVICES_AUGMENTATION_ITEM;
    // Vote System
    // individual
    public static Calendar CASTLE_VALIDATION_DATE;
    public static boolean ALT_PCBANG_POINTS_ENABLED;
    public static double ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE;
    public static int ALT_PCBANG_POINTS_BONUS;
    public static int ALT_PCBANG_POINTS_DELAY;
    public static int ALT_PCBANG_POINTS_MIN_LVL;
    public static boolean ALT_DEBUG_ENABLED;
    public static boolean ALT_DEBUG_PVP_ENABLED;
    public static boolean ALT_DEBUG_PVP_DUEL_ONLY;
    public static boolean ALT_DEBUG_PVE_ENABLED;
    public static double CRAFT_MASTERWORK_CHANCE;
    public static double CRAFT_DOUBLECRAFT_CHANCE;
    /**
     * Clan name template
     */
    public static String CLAN_NAME_TEMPLATE;
    /**
     * Clan title template
     */
    public static String CLAN_TITLE_TEMPLATE;
    /**
     * Ally name template
     */
    public static String ALLY_NAME_TEMPLATE;
    /**
     * Global chat state
     */
    public static boolean GLOBAL_SHOUT;
    public static boolean GLOBAL_TRADE_CHAT;
    public static int CHAT_RANGE;
    public static int SHOUT_OFFSET;
    public static boolean TRADE_CHATS_REPLACE;
    /**
     * For test servers - evrybody has admin rights
     */
    public static boolean EVERYBODY_HAS_ADMIN_RIGHTS;
    public static double ALT_RAID_RESPAWN_MULTIPLIER;
    public static boolean ALT_ALLOW_AUGMENT_ALL;
    public static boolean ALT_ALLOW_DROP_AUGMENTED;
    public static boolean ALT_GAME_UNREGISTER_RECIPE;
    /**
     * Delay for announce SS period (in minutes)
     */
    public static int SS_ANNOUNCE_PERIOD;
    /**
     * Petition manager
     */
    public static boolean PETITIONING_ALLOWED;
    public static int MAX_PETITIONS_PER_PLAYER;
    public static int MAX_PETITIONS_PENDING;
    /**
     * Show mob stats/droplist to players?
     */
    public static boolean ALT_GAME_SHOW_DROPLIST;
    public static boolean ALT_FULL_NPC_STATS_PAGE;
    public static boolean ALLOW_NPC_SHIFTCLICK;
    public static boolean ALT_ALLOW_SELL_COMMON;
    public static boolean ALT_ALLOW_SHADOW_WEAPONS;
    public static List<Integer> ALT_DISABLED_MULTISELL;
    public static List<Integer> ALT_SHOP_PRICE_LIMITS;
    public static List<Integer> ALT_SHOP_UNALLOWED_ITEMS;
    public static List<Integer> ALT_ALLOWED_PET_POTIONS;
    public static boolean SHIELD_SLAM_BLOCK_IS_MUSIC;
    public static boolean ALT_SAVE_UNSAVEABLE;
    public static int ALT_SAVE_EFFECTS_REMAINING_TIME;
    public static boolean ALT_SHOW_REUSE_MSG;
    public static boolean ALT_DELETE_SA_BUFFS;
    public static List<Integer> ITEM_USE_LIST_ID;
    public static boolean ITEM_USE_IS_COMBAT_FLAG;
    public static boolean ITEM_USE_IS_ATTACK;
    public static boolean CHAR_TITLE;
    public static String ADD_CHAR_TITLE;
    public static boolean ALT_SOCIAL_ACTION_REUSE;
    public static boolean ALT_DISABLE_SPELLBOOKS;
    /**
     * Alternative gameing - loss of XP on death
     */
    public static boolean ALT_GAME_DELEVEL;
    public static boolean AUTO_SOUL_CRYSTAL_QUEST;
    public static boolean ALT_GAME_SUBCLASS_WITHOUT_QUESTS;
    public static boolean ALT_ALLOW_SUBCLASS_WITHOUT_BAIUM;
    public static int ALT_GAME_START_LEVEL_TO_SUBCLASS;
    public static int ALT_GAME_LEVEL_TO_GET_SUBCLASS;
    public static int ALT_MAX_LEVEL;
    public static int ALT_MAX_SUB_LEVEL;
    public static int ALT_GAME_SUB_ADD;
    public static boolean ALT_GAME_SUB_BOOK;
    public static boolean ALT_NO_LASTHIT;
    public static boolean ALT_KAMALOKA_NIGHTMARES_PREMIUM_ONLY;
    public static boolean ALT_PET_HEAL_BATTLE_ONLY;
    public static boolean ALT_SIMPLE_SIGNS;
    public static boolean ALT_TELE_TO_CATACOMBS;
    public static boolean ALT_BS_CRYSTALLIZE;
    public static boolean ALT_ALLOW_TATTOO;
    public static int ALT_BUFF_LIMIT;
    public static int MULTISELL_SIZE;
    public static boolean SERVICES_CHANGE_NICK_ENABLED;
    // global
    public static boolean SERVICES_CHANGE_CLAN_NAME_ENABLED;
    public static int SERVICES_CHANGE_CLAN_NAME_PRICE;
    public static int SERVICES_CHANGE_CLAN_NAME_ITEM;
    public static boolean SERVICES_CHANGE_PET_NAME_ENABLED;
    public static int SERVICES_CHANGE_PET_NAME_PRICE;
    public static int SERVICES_CHANGE_PET_NAME_ITEM;
    public static boolean SERVICES_EXCHANGE_BABY_PET_ENABLED;
    public static int SERVICES_EXCHANGE_BABY_PET_PRICE;
    public static int SERVICES_EXCHANGE_BABY_PET_ITEM;
    public static int SERVICES_CHANGE_BASE_PRICE;
    public static int SERVICES_CHANGE_BASE_ITEM;
    public static boolean SERVICES_SEPARATE_SUB_ENABLED;
    public static int SERVICES_SEPARATE_SUB_PRICE;
    public static int SERVICES_SEPARATE_SUB_ITEM;
    public static boolean SERVICES_CHANGE_NICK_COLOR_ENABLED;
    public static int SERVICES_CHANGE_NICK_COLOR_PRICE;
    public static int SERVICES_CHANGE_NICK_COLOR_ITEM;
    public static String[] SERVICES_CHANGE_NICK_COLOR_LIST;
    public static boolean SERVICES_NOBLESS_SELL_ENABLED;
    public static int SERVICES_NOBLESS_SELL_PRICE;
    public static int SERVICES_NOBLESS_SELL_ITEM;
    public static boolean SERVICES_WASH_PK_ENABLED;
    public static int SERVICES_WASH_PK_ITEM;
    public static int SERVICES_WASH_PK_PRICE;
    // Service PK Clear from community board.
    public static int SERVICES_CLEAR_PK_PRICE;
    public static int SERVICES_CLEAR_PK_PRICE_ITEM_ID;
    public static int SERVICES_CLEAR_PK_COUNT;
    public static boolean SERVICES_EXPAND_INVENTORY_ENABLED;
    public static int SERVICES_EXPAND_INVENTORY_PRICE;
    public static int SERVICES_EXPAND_INVENTORY_ITEM;
    public static int SERVICES_EXPAND_INVENTORY_MAX;
    public static boolean SERVICES_EXPAND_WAREHOUSE_ENABLED;
    public static int SERVICES_EXPAND_WAREHOUSE_PRICE;
    public static int SERVICES_EXPAND_WAREHOUSE_ITEM;
    public static boolean SERVICES_EXPAND_CWH_ENABLED;
    public static int SERVICES_EXPAND_CWH_PRICE;
    public static int SERVICES_EXPAND_CWH_ITEM;
    public static String SERVICES_SELLPETS;
    public static boolean SERVICES_GIRAN_HARBOR_ENABLED;
    public static boolean SERVICES_PARNASSUS_ENABLED;
    public static boolean SERVICES_PARNASSUS_NOTAX;
    public static long SERVICES_PARNASSUS_PRICE;
    public static boolean SERVICES_ALLOW_LOTTERY;
    public static int SERVICES_LOTTERY_PRIZE;
    public static int SERVICES_ALT_LOTTERY_PRICE;
    public static int SERVICES_LOTTERY_TICKET_PRICE;
    public static double SERVICES_LOTTERY_5_NUMBER_RATE;
    public static double SERVICES_LOTTERY_4_NUMBER_RATE;
    public static double SERVICES_LOTTERY_3_NUMBER_RATE;
    public static int SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE;
    public static boolean SERVICES_ALLOW_ROULETTE;
    public static long SERVICES_ROULETTE_MIN_BET;
    public static long SERVICES_ROULETTE_MAX_BET;
    public static boolean ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE;
    public static boolean ALT_ALLOW_CLAN_COMMAND_ONLY_FOR_CLAN_LEADER;
    public static boolean ALT_GAME_REQUIRE_CLAN_CASTLE;
    public static boolean ALT_GAME_REQUIRE_CASTLE_DAWN;
    public static boolean ALT_GAME_ALLOW_ADENA_DAWN;
    public static boolean RETAIL_SS;
    // -------------------------------------------------------------------------------------------------------
    // PvP MOD
    // -------------------------------------------------------------------------------------------------------
    public static int ATT_MOD_ARMOR;
    public static int ATT_MOD_WEAPON;
    public static int ATT_MOD_WEAPON1;
    public static int ATT_MOD_MAX_ARMOR;
    public static int ATT_MOD_MAX_WEAPON;
    public static boolean SPAWN_CITIES_TREE;
    public static boolean SPAWN_NPC_BUFFER;
    public static boolean SPAWN_scrubwoman;
    public static boolean ADEPT_ENABLE;
    // By SmokiMo
    public static int HENNA_STATS;

    /* .km-all-to-me */
    public static boolean ENABLE_KM_ALL_TO_ME;
    public static int FARM_TELEPORT_ITEM_ID;
    public static int PRICE_FARM;
    public static int FARM_X;
    public static int FARM_Y;
    public static int FARM_Z;
    /* .farm_hard */
    public static int FARM_HARD_TELEPORT_ITEM_ID;
    public static int PRICE_FARM_HARD;
    public static int FARM_HARD_X;
    public static int FARM_HARD_Y;
    public static int FARM_HARD_Z;
    /* .farm_low */
    public static int FARM_LOW_TELEPORT_ITEM_ID;
    public static int PRICE_FARM_LOW;
    public static int FARM_LOW_X;
    public static int FARM_LOW_Y;
    public static int FARM_LOW_Z;
    /* .pvp */
    public static int PVP_TELEPORT_ITEM_ID;
    public static int PRICE_PVP;
    public static int PVP_X;
    public static int PVP_Y;
    public static int PVP_Z;

    public static int ALT_OLY_START_TIME;

    public static int ALT_OLY_MIN;

    public static long ALT_OLY_CPERIOD;
    /**
     * Olympiad Manager Shout Just One Time CUSTOM MESSAGE
     */
    public static boolean OLYMPIAD_SHOUT_ONCE_PER_START;
    /**
     * Olympaid Weekly Period
     */
    public static long ALT_OLY_WPERIOD;
    /**
     * Olympaid Validation Period
     */
    public static long ALT_OLY_VPERIOD;
    public static boolean ENABLE_OLYMPIAD;
    public static boolean ENABLE_OLYMPIAD_SPECTATING;
    public static int CLASS_GAME_MIN;
    public static int NONCLASS_GAME_MIN;
    public static int TEAM_GAME_MIN;
    public static int GAME_MAX_LIMIT;
    public static int GAME_CLASSES_COUNT_LIMIT;
    public static int GAME_NOCLASSES_COUNT_LIMIT;
    public static int GAME_TEAM_COUNT_LIMIT;
    public static int ALT_OLY_BATTLE_REWARD_ITEM;
    public static int ALT_OLY_CLASSED_RITEM_C;
    public static int ALT_OLY_NONCLASSED_RITEM_C;
    public static int ALT_OLY_TEAM_RITEM_C;
    public static int ALT_OLY_COMP_RITEM;
    public static int ALT_OLY_GP_PER_POINT;
    public static int ALT_OLY_HERO_POINTS;
    public static int ALT_OLY_RANK1_POINTS;
    public static int ALT_OLY_RANK2_POINTS;
    public static int ALT_OLY_RANK3_POINTS;
    public static int ALT_OLY_RANK4_POINTS;
    public static int ALT_OLY_RANK5_POINTS;
    public static int OLYMPIAD_STADIAS_COUNT;
    public static int OLYMPIAD_BATTLES_FOR_REWARD;
    public static int OLYMPIAD_POINTS_DEFAULT;
    public static int OLYMPIAD_POINTS_WEEKLY;
    public static boolean OLYMPIAD_OLDSTYLE_STAT;
    public static int ALT_OLY_WAIT_TIME;
    public static int ALT_OLY_PORT_BACK_TIME;
    public static long NONOWNER_ITEM_PICKUP_DELAY;
    /**
     * Logging Chat Window
     */
    public static boolean LOG_CHAT;
    public static int LATEST_SYSTEM_VER;
    /**
     * Rate control
     */
    public static double RATE_XP;
    public static double RATE_SP;
    public static double RATE_QUESTS_REWARD;
    public static double RATE_QUESTS_DROP;
    public static double RATE_CLAN_REP_SCORE;
    public static int RATE_CLAN_REP_SCORE_MAX_AFFECTED;
    public static double RATE_DROP_ADENA;
    public static double RATE_DROP_CHAMPION;
    public static double RATE_CHAMPION_DROP_ADENA;
    public static double RATE_DROP_ITEMS;
    public static double RATE_CHANCE_GROUP_DROP_ITEMS;
    public static double RATE_CHANCE_DROP_ITEMS;
    public static double RATE_CHANCE_DROP_HERBS;
    public static double RATE_CHANCE_SPOIL;
    public static double RATE_CHANCE_SPOIL_WEAPON_ARMOR_ACCESSORY;
    public static double RATE_CHANCE_DROP_WEAPON_ARMOR_ACCESSORY;
    public static double RATE_CHANCE_DROP_EPOLET;
    public static boolean NO_RATE_KEY_MATERIAL;
    public static boolean NO_RATE_RECIPES;
    public static double RATE_DROP_COMMON_ITEMS;
    public static double RATE_DROP_RAIDBOSS;
    public static double RATE_DROP_SPOIL;
    public static List<Integer> NO_RATE_ITEMS;
    public static double RATE_DROP_SIEGE_GUARD;
    public static double RATE_MANOR;
    public static double RATE_FISH_DROP_COUNT;
    public static boolean RATE_PARTY_MIN;
    public static double RATE_HELLBOUND_CONFIDENCE;
    public static boolean NO_RATE_EQUIPMENT;
    public static int RATE_MOB_SPAWN;
    public static int RATE_MOB_SPAWN_MIN_LEVEL;
    public static int RATE_MOB_SPAWN_MAX_LEVEL;
    /**
     * Player Drop Rate control
     */
    public static boolean KARMA_DROP_GM;
    public static boolean KARMA_NEEDED_TO_DROP;
    public static int KARMA_DROP_ITEM_LIMIT;
    public static int KARMA_RANDOM_DROP_LOCATION_LIMIT;
    public static double KARMA_DROPCHANCE_BASE;
    public static double KARMA_DROPCHANCE_MOD;
    public static double NORMAL_DROPCHANCE_BASE;
    public static int DROPCHANCE_EQUIPMENT;
    public static int DROPCHANCE_EQUIPPED_WEAPON;
    public static int DROPCHANCE_ITEM;
    public static int AUTODESTROY_ITEM_AFTER;
    public static int AUTODESTROY_PLAYER_ITEM_AFTER;
    public static int DELETE_DAYS;
    /**
     * Datapack root directory
     */
    public static Path DATAPACK_ROOT = Paths.get(".");
    public static double CLANHALL_BUFFTIME_MODIFIER;
    public static double SONGDANCETIME_MODIFIER;
    public static double MAXLOAD_MODIFIER;
    public static double GATEKEEPER_MODIFIER;
    public static boolean ALT_IMPROVED_PETS_LIMITED_USE;
    public static int GATEKEEPER_FREE;
    public static int CRUMA_GATEKEEPER_LVL;
    public static double ALT_CHAMPION_CHANCE1;
    public static double ALT_CHAMPION_CHANCE2;
    public static boolean ALT_CHAMPION_CAN_BE_AGGRO;
    public static boolean ALT_CHAMPION_CAN_BE_SOCIAL;
    public static int ALT_CHAMPION_TOP_LEVEL;
    public static int ALT_CHAMPION_MIN_LEVEL;
    public static boolean ALLOW_DISCARDITEM;
    public static boolean ALLOW_DISCARDITEM_AT_PEACE;
    public static boolean ALLOW_MAIL;
    public static boolean ALLOW_WAREHOUSE;
    public static boolean ALLOW_WATER;
    public static boolean ALLOW_CURSED_WEAPONS;
    public static boolean DROP_CURSED_WEAPONS_ON_KICK;
    public static boolean ALLOW_NOBLE_TP_TO_ALL;
    public static boolean ALLOW_PRIVATE_STORES;
    public static boolean ALLOW_TALK_TO_NPCS;
    public static boolean ALLOW_SKILLS_STATS_LOGGER;
    public static boolean ALLOW_ITEMS_LOGGING;
    public static boolean ALLOW_SPAWN_PROTECTION;
    public static boolean SELL_ALL_ITEMS_FREE;
    /**
     * Pets
     */
    public static int SWIMING_SPEED;
    /**
     * protocol revision
     */
    public static int MIN_PROTOCOL_REVISION;
    public static int MAX_PROTOCOL_REVISION;
    /**
     * random animation interval
     */
    public static int MIN_NPC_ANIMATION;
    public static int MAX_NPC_ANIMATION;
    public static int GAME_SERVER_LOGIN_PORT;
    public static String GAME_SERVER_LOGIN_HOST;
    public static String INTERNAL_HOSTNAME;
    public static String EXTERNAL_HOSTNAME;
    public static boolean SERVER_SIDE_NPC_NAME;
    public static boolean SERVER_SIDE_NPC_TITLE;
    public static boolean SERVER_SIDE_NPC_TITLE_ETC;
    public static int CLASS_MASTERS_PRICE_ITEM;
    public static boolean ALLOW_EVENT_GATEKEEPER;
    public static boolean ITEM_BROKER_ITEM_SEARCH;
    /**
     * Inventory slots limits
     */
    public static int INVENTORY_MAXIMUM_NO_DWARF;
    public static int INVENTORY_MAXIMUM_DWARF;
    public static int INVENTORY_MAXIMUM_GM;
    public static int QUEST_INVENTORY_MAXIMUM;
    /**
     * Warehouse slots limits
     */
    public static int WAREHOUSE_SLOTS_NO_DWARF;
    public static int WAREHOUSE_SLOTS_DWARF;
    public static int WAREHOUSE_SLOTS_CLAN;
    public static int FREIGHT_SLOTS;
    /**
     * Spoil Rates
     */
    public static double BASE_SPOIL_RATE;
    public static double MINIMUM_SPOIL_RATE;
    public static boolean ALT_SPOIL_FORMULA;
    /**
     * Manor Config
     */
    public static double MANOR_SOWING_BASIC_SUCCESS;
    public static double MANOR_SOWING_ALT_BASIC_SUCCESS;
    public static double MANOR_HARVESTING_BASIC_SUCCESS;
    public static int MANOR_DIFF_PLAYER_TARGET;
    public static double MANOR_DIFF_PLAYER_TARGET_PENALTY;
    public static int MANOR_DIFF_SEED_TARGET;
    public static double MANOR_DIFF_SEED_TARGET_PENALTY;
    /**
     * Karma System Variables
     */
    public static int KARMA_MIN_KARMA;
    public static int KARMA_SP_DIVIDER;
    public static int KARMA_LOST_BASE;
    public static int MIN_PK_TO_ITEMS_DROP;
    public static boolean DROP_ITEMS_ON_DIE;
    public static boolean DROP_ITEMS_AUGMENTED;
    public static List<Integer> KARMA_LIST_NONDROPPABLE_ITEMS = new ArrayList<>();
    public static int PVP_TIME;
    /**
     * Karma Punishment
     */
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_SHOP;
    /**
     * Chance that an item will succesfully be enchanted
     */
    public static int ENCHANT_MAX_WEAPON;
    public static int ENCHANT_MAX_ARMOR;
    public static int ENCHANT_MAX_JEWELRY;
    public static int ENCHANT_ATTRIBUTE_STONE_CHANCE;
    public static int ENCHANT_ATTRIBUTE_CRYSTAL_CHANCE;
    public static boolean SHOW_ENCHANT_EFFECT_RESULT;
    public static boolean ENABLE_ACHIEVEMENTS;
    public static boolean REGEN_SIT_WAIT;
    public static double RATE_RAID_REGEN;
    public static double RATE_RAID_DEFENSE;
    public static double RATE_RAID_ATTACK;
    public static double RATE_EPIC_DEFENSE;
    public static double RATE_EPIC_ATTACK;
    public static int RAID_MAX_LEVEL_DIFF;
    public static boolean PARALIZE_ON_RAID_DIFF;
    public static boolean FRINTEZZA_ALL_MEMBERS_NEED_SCROLL;
    public static double ALT_PK_DEATH_RATE;
    public static int STARTING_ADENA;
    public static int STARTING_LVL;
    public static boolean HTML_WELCOME;
    public static boolean ENTER_WORLD_ANNOUNCEMENTS_HERO_LOGIN;
    public static boolean ENTER_WORLD_ANNOUNCEMENTS_LORD_LOGIN;
    public static boolean ENTER_WORLD_SHOW_HTML_PREMIUM_BUY;
    public static boolean DEEPBLUE_DROP_RULES;
    public static int DEEPBLUE_DROP_MAXDIFF;
    public static int DEEPBLUE_DROP_RAID_MAXDIFF;
    public static boolean UNSTUCK_SKILL;

    /**
     * Maximum number of available slots for pvt stores (sell/buy) - Dwarves
     */
    public static int MAX_PVTSTORE_SLOTS_DWARF;
    /**
     * Maximum number of available slots for pvt stores (sell/buy) - Others
     */
    public static int MAX_PVTSTORE_SLOTS_OTHER;
    public static int MAX_PVTCRAFT_SLOTS;
    public static double SENDSTATUS_TRADE_MOD;
    public static boolean SHOW_OFFLINE_MODE_IN_ONLINE;
    public static boolean ALT_CH_ALL_BUFFS;
    public static boolean ALT_CH_ALLOW_1H_BUFFS;
    public static double RESIDENCE_LEASE_FUNC_MULTIPLIER;
    // Fame Reward
    public static boolean ENABLE_ALT_FAME_REWARD;
    public static long ALT_FAME_CASTLE;
    public static long ALT_FAME_FORTRESS;
    public static int INTERVAL_FLAG_DROP;
    // Alexander
    public static int SIEGE_WINNER_REPUTATION_REWARD;
    public static boolean ACCEPT_ALTERNATE_ID;
    public static int REQUEST_ID;
    public static boolean ANNOUNCE_MAMMON_SPAWN;
    public static int NORMAL_NAME_COLOUR;
    public static boolean VIKTORINA_ENABLED;// false;
    public static boolean VIKTORINA_REMOVE_QUESTION;// false;;
    public static boolean VIKTORINA_REMOVE_QUESTION_NO_ANSWER;// = false;
    public static int VIKTORINA_START_TIME_HOUR;// 16;
    public static int VIKTORINA_START_TIME_MIN;// 16;
    public static int VIKTORINA_WORK_TIME;// 2;
    public static int VIKTORINA_TIME_ANSER;// 1;
    public static int VIKTORINA_TIME_PAUSE;// 1;
    /**
     * AI
     */
    public static boolean ALLOW_NPC_AIS;
    public static int AI_TASK_MANAGER_COUNT;
    public static long AI_TASK_ATTACK_DELAY;
    public static long AI_TASK_ACTIVE_DELAY;
    public static boolean BLOCK_ACTIVE_TASKS;
    public static boolean ALWAYS_TELEPORT_HOME;
    public static boolean RND_WALK;
    public static int RND_WALK_RATE;
    public static int RND_ANIMATION_RATE;
    public static int AGGRO_CHECK_INTERVAL;
    public static long NONAGGRO_TIME_ONTELEPORT;
    /**
     * Maximum range mobs can randomly go from spawn point
     */
    public static int MAX_DRIFT_RANGE;
    /**
     * Maximum range mobs can pursue agressor from spawn point
     */
    public static int MAX_PURSUE_RANGE;
    public static int MAX_PURSUE_UNDERGROUND_RANGE;
    public static int MAX_PURSUE_RANGE_RAID;
    public static boolean ALT_DEATH_PENALTY;
    public static boolean ALLOW_DEATH_PENALTY_C5;
    public static int ALT_DEATH_PENALTY_C5_CHANCE;
    public static boolean ALT_DEATH_PENALTY_C5_CHAOTIC_RECOVERY;
    public static int ALT_DEATH_PENALTY_C5_EXPERIENCE_PENALTY;
    public static int ALT_DEATH_PENALTY_C5_KARMA_PENALTY;
    public static boolean SHOW_GM_LOGIN;
    public static boolean SAVE_GM_EFFECTS; // Silence, gmspeed, etc...
    public static boolean AUTO_LEARN_SKILLS;
    public static boolean AUTO_LEARN_FORGOTTEN_SKILLS;
    public static int MOVE_PACKET_DELAY;
    public static int ATTACK_PACKET_DELAY;
    public static boolean DAMAGE_FROM_FALLING;
    /**
     * Community Board
     */
    public static boolean USE_BBS_PROF_IS_COMBAT;
    public static boolean COMMUNITYBOARD_ENABLED;
    public static String BBS_DEFAULT;
    public static String BBS_HOME_DIR;
    /**
     * Augmentations
     **/
    public static int AUGMENTATION_NG_SKILL_CHANCE; // Chance to get a skill while using a NoGrade Life Stone
    public static int AUGMENTATION_NG_GLOW_CHANCE; // Chance to get a Glow effect while using a NoGrade Life Stone(only if you get a skill)
    public static int AUGMENTATION_MID_SKILL_CHANCE; // Chance to get a skill while using a MidGrade Life Stone
    public static int AUGMENTATION_MID_GLOW_CHANCE; // Chance to get a Glow effect while using a MidGrade Life Stone(only if you get a skill)
    public static int AUGMENTATION_HIGH_SKILL_CHANCE; // Chance to get a skill while using a HighGrade Life Stone
    public static int AUGMENTATION_HIGH_GLOW_CHANCE; // Chance to get a Glow effect while using a HighGrade Life Stone
    public static int AUGMENTATION_TOP_SKILL_CHANCE; // Chance to get a skill while using a TopGrade Life Stone
    public static int AUGMENTATION_TOP_GLOW_CHANCE; // Chance to get a Glow effect while using a TopGrade Life Stone
    public static int AUGMENTATION_BASESTAT_CHANCE; // Chance to get a BaseStatModifier in the augmentation process
    public static int AUGMENTATION_ACC_SKILL_CHANCE;
    public static int FOLLOW_RANGE;
    public static boolean ALT_ENABLE_MULTI_PROFA;
    public static boolean ALT_ITEM_AUCTION_ENABLED;
    public static boolean ALT_ITEM_AUCTION_CAN_REBID;
    public static boolean ALT_ITEM_AUCTION_START_ANNOUNCE;
    public static int ALT_ITEM_AUCTION_BID_ITEM_ID;
    public static long ALT_ITEM_AUCTION_MAX_BID;
    public static int ALT_ITEM_AUCTION_MAX_CANCEL_TIME_IN_MILLIS;
    public static boolean ALT_FISH_CHAMPIONSHIP_ENABLED;
    public static int ALT_FISH_CHAMPIONSHIP_REWARD_ITEM;
    public static int ALT_FISH_CHAMPIONSHIP_REWARD_1;
    public static int ALT_FISH_CHAMPIONSHIP_REWARD_2;
    public static int ALT_FISH_CHAMPIONSHIP_REWARD_3;
    public static int ALT_FISH_CHAMPIONSHIP_REWARD_4;
    public static int ALT_FISH_CHAMPIONSHIP_REWARD_5;
    public static boolean ALT_ENABLE_BLOCK_CHECKER_EVENT;
    public static int ALT_MIN_BLOCK_CHECKER_TEAM_MEMBERS;
    public static double ALT_RATE_COINS_REWARD_BLOCK_CHECKER;
    public static boolean ALT_HBCE_FAIR_PLAY;
    public static int ALT_PET_INVENTORY_LIMIT;
    public static int ALT_CLAN_LEVEL_CREATE;
    /**
     * Enchant Config
     **/
    public static int SAFE_ENCHANT_LVL;
    public static int FESTIVAL_MIN_PARTY_SIZE;
    public static double FESTIVAL_RATE_PRICE;
    public static boolean ENABLE_POLL_SYSTEM;
    public static int ANNOUNCE_POLL_EVERY_X_MIN;
    /**
     * DimensionalRift Config
     **/
    public static int RIFT_MIN_PARTY_SIZE;
    public static int RIFT_SPAWN_DELAY; // Time in ms the party has to wait until the mobs spawn
    public static int RIFT_MAX_JUMPS;
    public static int RIFT_AUTO_JUMPS_TIME;
    public static int RIFT_AUTO_JUMPS_TIME_RAND;
    public static int RIFT_ENTER_COST_RECRUIT;
    public static int RIFT_ENTER_COST_SOLDIER;
    public static int RIFT_ENTER_COST_OFFICER;
    public static int RIFT_ENTER_COST_CAPTAIN;
    public static int RIFT_ENTER_COST_COMMANDER;
    public static int RIFT_ENTER_COST_HERO;
    public static boolean ALLOW_TALK_WHILE_SITTING;
    public static boolean PARTY_LEADER_ONLY_CAN_INVITE;
    /**
     * Ð Ð°Ð·Ñ€ÐµÑˆÐµÐ½Ð¾ Ð»Ð¸ Ð¸Ð·ÑƒÑ‡ÐµÐ½Ð¸Ðµ Ñ�ÐºÐ¸Ð»Ð¾Ð² Ñ‚Ñ€Ð°Ð½Ñ�Ñ„Ð¾Ñ€Ð¼Ð°Ñ†Ð¸Ð¸ Ð¸ Ñ�Ð°Ð± ÐºÐ»Ð°Ñ�Ñ�Ð¾Ð² Ð±ÐµÐ· Ð½Ð°Ð»Ð¸Ñ‡Ð¸Ñ� Ð²Ñ‹Ð¿Ð¾Ð»Ð½ÐµÐ½Ð½Ð¾Ð³Ð¾ ÐºÐ²ÐµÑ�Ñ‚Ð°
     */
    public static boolean ALLOW_LEARN_TRANS_SKILLS_WO_QUEST;
    /**
     * Allow Manor system
     */
    public static boolean ALLOW_MANOR;
    /**
     * Manor Refresh Starting time
     */
    public static int MANOR_REFRESH_TIME;
    /**
     * Manor Refresh Min
     */
    public static int MANOR_REFRESH_MIN;
    /**
     * Manor Next Period Approve Starting time
     */
    public static int MANOR_APPROVE_TIME;
    /**
     * Manor Next Period Approve Min
     */
    public static int MANOR_APPROVE_MIN;
    /**
     * Manor Maintenance Time
     */
    public static int MANOR_MAINTENANCE_PERIOD;
    public static double EVENT_CofferOfShadowsPriceRate;
    public static double EVENT_CofferOfShadowsRewardRate;
    public static double EVENT_APIL_FOOLS_DROP_CHANCE;
    /**
     * Master Yogi event enchant config
     */
    public static int ENCHANT_CHANCE_MASTER_YOGI_STAFF;
    public static int ENCHANT_MAX_MASTER_YOGI_STAFF;
    public static int SAFE_ENCHANT_MASTER_YOGI_STAFF;
    public static boolean AllowCustomDropItems;
    public static List<Integer> CDItemsId;
    public static List<Integer> CDItemsCountDropMin;
    public static List<Integer> CDItemsCountDropMax;
    public static List<Integer> CustomDropItemsChance;
    public static boolean CDItemsAllowMinMaxPlayerLvl;
    public static int CDItemsMinPlayerLvl;
    public static int CDItemsMaxPlayerLvl;
    public static boolean CDItemsAllowMinMaxMobLvl;
    public static int CDItemsMinMobLvl;
    public static int CDItemsMaxMobLvl;
    public static boolean CDItemsAllowOnlyRbDrops;
    public static double EVENT_TFH_POLLEN_CHANCE;
    public static double EVENT_GLITTMEDAL_NORMAL_CHANCE;
    public static double EVENT_GLITTMEDAL_GLIT_CHANCE;
    public static double EVENT_L2DAY_LETTER_CHANCE;
    public static double EVENT_CHANGE_OF_HEART_CHANCE;
    public static double EVENT_TRICK_OF_TRANS_CHANCE;
    public static double EVENT_MARCH8_DROP_CHANCE;
    public static double EVENT_MARCH8_PRICE_RATE;
    public static boolean EVENT_BOUNTY_HUNTERS_ENABLED;
    public static long EVENT_SAVING_SNOWMAN_LOTERY_PRICE;
    public static int EVENT_SAVING_SNOWMAN_REWARDER_CHANCE;
    // RandomBoss Event
    public static boolean RANDOM_BOSS_ENABLE;
    public static int RANDOM_BOSS_ID;
    public static int RANDOM_BOSS_TIME;
    public static int RANDOM_BOSS_X;
    public static int RANDOM_BOSS_Y;
    public static int RANDOM_BOSS_Z;
    //Santa Event
    public static boolean EVENT_SANTA_ALLOW;
    public static double EVENT_SANTA_CHANCE_MULT;
    public static double SERVICES_TRADE_TAX;
    public static double SERVICES_OFFSHORE_TRADE_TAX;
    public static boolean SERVICES_OFFSHORE_NO_CASTLE_TAX;
    public static boolean SERVICES_TRADE_TAX_ONLY_OFFLINE;
    public static boolean SERVICES_TRADE_ONLY_FAR;
    public static int SERVICES_TRADE_RADIUS;
    public static int SERVICES_TRADE_MIN_LEVEL;
    public static boolean SERVICES_ENABLE_NO_CARRIER;
    public static boolean SERVICES_PK_PVP_KILL_ENABLE;
    public static int SERVICES_PVP_KILL_REWARD_ITEM;
    public static long SERVICES_PVP_KILL_REWARD_COUNT;
    public static int SERVICES_PK_KILL_REWARD_ITEM;
    public static long SERVICES_PK_KILL_REWARD_COUNT;
    public static boolean SERVICES_PK_PVP_TIE_IF_SAME_IP;
    public static boolean ALT_OPEN_CLOAK_SLOT;
    public static boolean ALT_SHOW_SERVER_TIME;
    /**
     * Geodata config
     */
    public static int GEO_X_FIRST, GEO_Y_FIRST, GEO_X_LAST, GEO_Y_LAST;
    public static String GEOFILES_PATTERN;
    public static boolean ALLOW_GEODATA;
    public static boolean ALLOW_FALL_FROM_WALLS;
    public static boolean COMPACT_GEO;
    public static int CLIENT_Z_SHIFT;
    public static int MAX_Z_DIFF;
    public static int MIN_LAYER_HEIGHT;
    /**
     * Geodata (Pathfind) config
     */
    public static int PATHFIND_BOOST;
    public static boolean PATHFIND_DIAGONAL;
    public static boolean PATH_CLEAN;
    public static int PATHFIND_MAX_Z_DIFF;
    public static long PATHFIND_MAX_TIME;
    public static String PATHFIND_BUFFERS;
    public static boolean DEBUG;
    /* Item-Mall Configs */
    public static int GAME_POINT_ITEM_ID;
    public static int WEAR_DELAY;
    public static boolean EX_JAPAN_MINIGAME;
    public static boolean EX_LECTURE_MARK;
    /* Top's Config */
    public static boolean AUTH_SERVER_GM_ONLY;
    public static boolean AUTH_SERVER_BRACKETS;
    public static boolean AUTH_SERVER_IS_PVP;
    public static int AUTH_SERVER_AGE_LIMIT;
    public static int AUTH_SERVER_SERVER_TYPE;
    public static int PASSWORD_PAY_ID;
    public static long PASSWORD_PAY_COUNT;
    public static String APASSWD_TEMPLATE;
    // Bot Report
    public static long MAX_PLAYER_CONTRIBUTION;
    /* Epics */
    public static int ANTHARAS_DEFAULT_SPAWN_HOURS;
    public static int ANTHARAS_RANDOM_SPAWN_HOURS;
    public static int VALAKAS_DEFAULT_SPAWN_HOURS;
    public static int VALAKAS_RANDOM_SPAWN_HOURS;
    public static int BAIUM_DEFAULT_SPAWN_HOURS;
    public static int BAIUM_RANDOM_SPAWN_HOURS;
    public static int FIXINTERVALOFBAYLORSPAWN_HOUR;
    public static int RANDOMINTERVALOFBAYLORSPAWN;
    public static int FIXINTERVALOFBELETHSPAWN_HOUR;
    public static int BELETH_CLONES_RESPAWN_TIME;
    public static int FIXINTERVALOFSAILRENSPAWN_HOUR;
    public static int RANDOMINTERVALOFSAILRENSPAWN;
    public static int MIN_PLAYERS_TO_SPAWN_BELETH;
    public static int CLAN_LEVEL_6_COST;
    public static int CLAN_LEVEL_7_COST;
    public static int CLAN_LEVEL_8_COST;
    public static int CLAN_LEVEL_9_COST;
    public static int CLAN_LEVEL_10_COST;
    public static int CLAN_LEVEL_11_COST;
    public static int CLAN_LEVEL_6_REQUIREMEN;
    public static int CLAN_LEVEL_7_REQUIREMEN;
    public static int CLAN_LEVEL_8_REQUIREMEN;
    public static int CLAN_LEVEL_9_REQUIREMEN;
    public static int CLAN_LEVEL_10_REQUIREMEN;
    public static int CLAN_LEVEL_11_REQUIREMEN;
    public static int BLOOD_OATHS;
    public static int BLOOD_PLEDGES;
    public static int MIN_ACADEM_POINT;
    public static int MAX_ACADEM_POINT;
    public static boolean ZONE_PVP_COUNT;
    public static boolean SIEGE_PVP_COUNT;
    public static boolean EXPERTISE_PENALTY;
    // Remove dance and songs shot click
    public static boolean ALT_DISPEL_MUSIC;
    public static int ALT_MUSIC_LIMIT;
    public static int ALT_DEBUFF_LIMIT;
    public static int ALT_TRIGGER_LIMIT;
    public static boolean ENABLE_MODIFY_SKILL_DURATION;
    public static Map<Integer, Integer> SKILL_DURATION_LIST;
    public static double ALT_VITALITY_NEVIT_UP_POINT;
    public static double ALT_VITALITY_NEVIT_POINT;
    public static boolean SERVICES_LVL_ENABLED;
    public static int SERVICES_LVL_UP_MAX;
    public static int SERVICES_LVL_UP_PRICE;
    public static int SERVICES_LVL_UP_ITEM;
    public static int SERVICES_LVL_DOWN_MAX;
    public static int SERVICES_LVL_DOWN_PRICE;
    public static int SERVICES_LVL_DOWN_ITEM;
    public static boolean ALLOW_INSTANCES_LEVEL_MANUAL;
    public static boolean ALLOW_INSTANCES_PARTY_MANUAL;
    public static int INSTANCES_LEVEL_MIN;
    public static int INSTANCES_LEVEL_MAX;
    public static int INSTANCES_PARTY_MIN;
    public static int INSTANCES_PARTY_MAX;
    // Items setting
    public static boolean CAN_BE_TRADED_NO_TARADEABLE;
    public static boolean CAN_BE_TRADED_NO_SELLABLE;
    public static boolean CAN_BE_TRADED_NO_STOREABLE;
    public static boolean CAN_BE_TRADED_SHADOW_ITEM;
    public static boolean CAN_BE_TRADED_HERO_WEAPON;
    public static boolean CAN_BE_CWH_IS_AUGMENTED;
    public static boolean ALLOW_SOUL_SPIRIT_SHOT_INFINITELY;
    public static boolean ALLOW_ARROW_INFINITELY;
    public static boolean ALLOW_START_ITEMS;
    public static List<Integer> START_ITEMS_MAGE;
    public static List<Integer> START_ITEMS_MAGE_COUNT;
    public static List<Integer> START_ITEMS_FITHER;
    public static List<Integer> START_ITEMS_FITHER_COUNT;
    public static int HELLBOUND_LEVEL;
    public static int CLAN_LEAVE_PENALTY;
    public static int ALLY_LEAVE_PENALTY;
    public static int DISSOLVED_ALLY_PENALTY;
    public static boolean LOAD_CUSTOM_SPAWN;
    public static boolean SAVE_GM_SPAWN;
    // Log items
    public static boolean ENABLE_PLAYER_ITEM_LOGS;
    public static boolean DEBUFF_PROTECTION_SYSTEM;
    public static int _coinID;
    public static boolean ALLOW_UPDATE_ANNOUNCER;
    public static boolean NOT_USE_USER_VOICED;
    public static boolean show_rates;
    public static int RWHO_KEEP_STAT;
    public static boolean RWHO_SEND_TRASH;
    public static boolean BUFF_STORE_ENABLED;
    public static boolean BUFF_STORE_MP_ENABLED;
    public static double BUFF_STORE_MP_CONSUME_MULTIPLIER;
    public static int BUFF_STORE_NAME_COLOR;
    public static int BUFF_STORE_TITLE_COLOR;
    public static int BUFF_STORE_OFFLINE_NAME_COLOR;
    public static List<Integer> BUFF_STORE_ALLOWED_CLASS_LIST;
    public static List<Integer> BUFF_STORE_FORBIDDEN_SKILL_LIST;
    public static boolean BBS_FORGE_ENABLED;
    public static int BBS_FORGE_ENCHANT_ITEM;
    public static int BBS_FORGE_FOUNDATION_ITEM;
    public static List<Integer> BBS_FORGE_FOUNDATION_PRICE_ARMOR;
    public static List<Integer> BBS_FORGE_FOUNDATION_PRICE_WEAPON;
    public static List<Integer> BBS_FORGE_FOUNDATION_PRICE_JEWEL;
    public static List<Integer> BBS_FORGE_ENCHANT_MAX;
    public static List<Integer> BBS_FORGE_WEAPON_ENCHANT_LVL;
    public static List<Integer> BBS_FORGE_ARMOR_ENCHANT_LVL;
    public static List<Integer> BBS_FORGE_JEWELS_ENCHANT_LVL;
    public static List<Integer> BBS_FORGE_ENCHANT_PRICE_WEAPON;
    public static List<Integer> BBS_FORGE_ENCHANT_PRICE_ARMOR;
    public static List<Integer> BBS_FORGE_ENCHANT_PRICE_JEWELS;
    public static int BBS_FORGE_WEAPON_ATTRIBUTE_MAX;
    public static int BBS_FORGE_ARMOR_ATTRIBUTE_MAX;
    public static List<Integer> BBS_FORGE_ATRIBUTE_LVL_WEAPON;
    public static List<Integer> BBS_FORGE_ATRIBUTE_LVL_ARMOR;
    public static List<Integer> BBS_FORGE_ATRIBUTE_PRICE_ARMOR;
    public static List<Integer> BBS_FORGE_ATRIBUTE_PRICE_WEAPON;
    static String GAMESERVER_HOSTNAME;
    /**
     * Thread pools size
     */
    static int SCHEDULED_THREAD_POOL_SIZE;
    static int EXECUTOR_THREAD_POOL_SIZE;
    static boolean ENABLE_RUNNABLE_STATS;
    static String RESTART_AT_TIME;
    /**
     * telnet enabled
     */
    static boolean IS_TELNET_ENABLED;

    private static String CLASS_MASTERS_PRICE;

    private Config() {
    }

    private static void loadVIKTORINAsettings() {
        ExProperties VIKTORINASettings = load(VIKTORINA_CONFIG_FILE);

        VIKTORINA_ENABLED = VIKTORINASettings.getProperty("Victorina_Enabled", false);
        VIKTORINA_REMOVE_QUESTION = VIKTORINASettings.getProperty("Victorina_Remove_Question", false);
        VIKTORINA_REMOVE_QUESTION_NO_ANSWER = VIKTORINASettings.getProperty("Victorina_Remove_Question_No_Answer", false);
        VIKTORINA_START_TIME_HOUR = VIKTORINASettings.getProperty("Victorina_Start_Time_Hour", 16);
        VIKTORINA_START_TIME_MIN = VIKTORINASettings.getProperty("Victorina_Start_Time_Minute", 16);
        VIKTORINA_WORK_TIME = VIKTORINASettings.getProperty("Victorina_Work_Time", 2);
        VIKTORINA_TIME_ANSER = VIKTORINASettings.getProperty("Victorina_Time_Answer", 1);
        VIKTORINA_TIME_PAUSE = VIKTORINASettings.getProperty("Victorina_Time_Pause", 1);

    }

    private static void loadServerConfig() {
        ExProperties serverSettings = load(CONFIGURATION_FILE);

        LOG_SERVICES = serverSettings.getProperty("Services", false);
        GAME_SERVER_LOGIN_HOST = serverSettings.getProperty("LoginHost", "127.0.0.1");
        GAME_SERVER_LOGIN_PORT = serverSettings.getProperty("LoginPort", 9013);

        AUTH_SERVER_AGE_LIMIT = serverSettings.getProperty("ServerAgeLimit", 0);
        AUTH_SERVER_GM_ONLY = serverSettings.getProperty("ServerGMOnly", false);
        AUTH_SERVER_BRACKETS = serverSettings.getProperty("ServerBrackets", false);
        AUTH_SERVER_IS_PVP = serverSettings.getProperty("PvPServer", false);
        for (String a : serverSettings.getProperty("ServerType", new String[0])) {
            if (a.trim().isEmpty()) {
                continue;
            }

            ServerType t = ServerType.valueOf(a.toUpperCase());
            AUTH_SERVER_SERVER_TYPE |= t.getMask();
        }

        INTERNAL_HOSTNAME = serverSettings.getProperty("InternalHostname", "*");
        EXTERNAL_HOSTNAME = serverSettings.getProperty("ExternalHostname", "*");
        REQUEST_ID = serverSettings.getProperty("RequestServerID", 0);
        ACCEPT_ALTERNATE_ID = serverSettings.getProperty("AcceptAlternateID", true);

        GAMESERVER_HOSTNAME = serverSettings.getProperty("GameserverHostname");
        GAME_PORT = serverSettings.getProperty("GameserverPort", List.of(7777));

        EVERYBODY_HAS_ADMIN_RIGHTS = serverSettings.getProperty("EverybodyHasAdminRights", false);

        SHOW_GM_LOGIN = serverSettings.getProperty("ShowGMLogin", true);
        SAVE_GM_EFFECTS = serverSettings.getProperty("SaveGMEffects", false);

        CLAN_NAME_TEMPLATE = serverSettings.getProperty("ClanNameTemplate", "[A-Za-z0-9\u0410-\u042f\u0430-\u044f]{3,16}");
        CLAN_TITLE_TEMPLATE = serverSettings.getProperty("ClanTitleTemplate", "[A-Za-z0-9\u0410-\u042f\u0430-\u044f \\p{Punct}]{1,16}");
        ALLY_NAME_TEMPLATE = serverSettings.getProperty("AllyNameTemplate", "[A-Za-z0-9\u0410-\u042f\u0430-\u044f]{3,16}");

        PARALIZE_ON_RAID_DIFF = serverSettings.getProperty("ParalizeOnRaidLevelDiff", true);

        AUTODESTROY_ITEM_AFTER = serverSettings.getProperty("AutoDestroyDroppedItemAfter", 0);
        AUTODESTROY_PLAYER_ITEM_AFTER = serverSettings.getProperty("AutoDestroyPlayerDroppedItemAfter", 0);
        DELETE_DAYS = serverSettings.getProperty("DeleteCharAfterDays", 7);

        DATAPACK_ROOT = Paths.get(serverSettings.getProperty("DatapackRoot", "."));

        ALLOW_DISCARDITEM = serverSettings.getProperty("AllowDiscardItem", true);
        ALLOW_DISCARDITEM_AT_PEACE = serverSettings.getProperty("AllowDiscardItemInTown", true);
        ALLOW_MAIL = serverSettings.getProperty("AllowMail", true);
        ALLOW_WAREHOUSE = serverSettings.getProperty("AllowWarehouse", true);
        ALLOW_WATER = serverSettings.getProperty("AllowWater", true);
        ALLOW_CURSED_WEAPONS = serverSettings.getProperty("AllowCursedWeapons", false);
        DROP_CURSED_WEAPONS_ON_KICK = serverSettings.getProperty("DropCursedWeaponsOnKick", false);
        ALLOW_PRIVATE_STORES = serverSettings.getProperty("AllowStores", true);
        ALLOW_TALK_TO_NPCS = serverSettings.getProperty("AllowTalkToNpcs", true);
        ALLOW_SKILLS_STATS_LOGGER = serverSettings.getProperty("AllowSkillStatsLogger", true);
        ALLOW_ITEMS_LOGGING = serverSettings.getProperty("AllowItemsLogging", true);
        ALLOW_SPAWN_PROTECTION = serverSettings.getProperty("AllowSpawnProtection", true);

        MIN_PROTOCOL_REVISION = serverSettings.getProperty("MinProtocolRevision", 267);
        MAX_PROTOCOL_REVISION = serverSettings.getProperty("MaxProtocolRevision", 271);

        AUTOSAVE = serverSettings.getProperty("Autosave", true);

        MAXIMUM_ONLINE_USERS = serverSettings.getProperty("MaximumOnlineUsers", 3000);
        DATABASE_DRIVER = serverSettings.getProperty("Driver", "com.mysql.jdbc.Driver");
        DATABASE_MAX_CONNECTIONS = serverSettings.getProperty("MaximumDbConnections", 10);
        DATABASE_MAX_IDLE_TIMEOUT = serverSettings.getProperty("MaxIdleConnectionTimeout", 600);
        DATABASE_IDLE_TEST_PERIOD = serverSettings.getProperty("IdleConnectionTestPeriod", 60);

        DATABASE_GAME_URL = serverSettings.getProperty("GameURL", "jdbc:mysql://localhost/l2jdb");
        DATABASE_GAME_USER = serverSettings.getProperty("GameUser", "root");
        DATABASE_GAME_PASSWORD = serverSettings.getProperty("GamePassword", "");
        DATABASE_LOGIN_URL = serverSettings.getProperty("LoginURL", "jdbc:mysql://localhost/l2jdb");
        DATABASE_LOGIN_USER = serverSettings.getProperty("LoginUser", "root");
        DATABASE_LOGIN_PASSWORD = serverSettings.getProperty("LoginPassword", "");
        USER_INFO_INTERVAL = serverSettings.getProperty("UserInfoInterval", 100L);
        BROADCAST_CHAR_INFO_INTERVAL = serverSettings.getProperty("BroadcastCharInfoInterval", 100L);

        EFFECT_TASK_MANAGER_COUNT = serverSettings.getProperty("EffectTaskManagers", 2);

        SCHEDULED_THREAD_POOL_SIZE = serverSettings.getProperty("ScheduledThreadPoolSize", NCPUS * 4);
        EXECUTOR_THREAD_POOL_SIZE = serverSettings.getProperty("ExecutorThreadPoolSize", NCPUS * 2);

        ENABLE_RUNNABLE_STATS = serverSettings.getProperty("EnableRunnableStats", false);

        SELECTOR_CONFIG.SLEEP_TIME = serverSettings.getProperty("SelectorSleepTime", 10L);
        SELECTOR_CONFIG.INTEREST_DELAY = serverSettings.getProperty("InterestDelay", 30L);
        SELECTOR_CONFIG.MAX_SEND_PER_PASS = serverSettings.getProperty("MaxSendPerPass", 32);
        SELECTOR_CONFIG.READ_BUFFER_SIZE = serverSettings.getProperty("ReadBufferSize", 65536);
        SELECTOR_CONFIG.WRITE_BUFFER_SIZE = serverSettings.getProperty("WriteBufferSize", 131072);
        SELECTOR_CONFIG.HELPER_BUFFER_COUNT = serverSettings.getProperty("BufferPoolSize", 64);

        RESTART_AT_TIME = serverSettings.getProperty("AutoRestartAt", "0 5 * * *");
        SHIFT_BY = serverSettings.getProperty("HShift", 12);
        SHIFT_BY_Z = serverSettings.getProperty("VShift", 11);
        MAP_MIN_Z = serverSettings.getProperty("MapMinZ", -32768);
        MAP_MAX_Z = serverSettings.getProperty("MapMaxZ", 32767);

        MOVE_PACKET_DELAY = serverSettings.getProperty("MovePacketDelay", 100);
        ATTACK_PACKET_DELAY = serverSettings.getProperty("AttackPacketDelay", 500);

        DAMAGE_FROM_FALLING = serverSettings.getProperty("DamageFromFalling", true);

        MAX_REFLECTIONS_COUNT = serverSettings.getProperty("MaxReflectionsCount", 300);

        WEAR_DELAY = serverSettings.getProperty("WearDelay", 5);

        HTM_CACHE_MODE = serverSettings.getProperty("HtmCacheMode", HtmCache.LAZY);

        ALT_VITALITY_NEVIT_UP_POINT = serverSettings.getProperty("WebServerDelay", 10);
        ALT_VITALITY_NEVIT_POINT = serverSettings.getProperty("WebServerDelay", 10);

        ALLOW_ADDONS_CONFIG = serverSettings.getProperty("AllowAddonsConfig", false);
    }

    private static void loadHitmanSettings() {
        ExProperties eventHitmanSettings = load(CONFIG.resolve("events/Hitman.ini"));

        EVENT_HITMAN_ENABLED = eventHitmanSettings.getProperty("HitmanEnabled", false);
        EVENT_HITMAN_COST_ITEM_ID = eventHitmanSettings.getProperty("CostItemId", 57);
        EVENT_HITMAN_COST_ITEM_COUNT = eventHitmanSettings.getProperty("CostItemCount", 1000);
        EVENT_HITMAN_TASKS_PER_PAGE = eventHitmanSettings.getProperty("TasksPerPage", 7);
        EVENT_HITMAN_ALLOWED_ITEM_LIST = eventHitmanSettings.getProperty("AllowedItems", List.of(4037, 57));
    }

    private static void loadChatConfig() {
        ExProperties chatSettings = load(CHAT_FILE);

        GLOBAL_SHOUT = chatSettings.getProperty("GlobalShout", false);
        GLOBAL_TRADE_CHAT = chatSettings.getProperty("GlobalTradeChat", false);
        CHAT_RANGE = chatSettings.getProperty("ChatRange", 1250);
        SHOUT_OFFSET = chatSettings.getProperty("ShoutOffset", 0);

        String T_WORLD = chatSettings.getProperty("TradeWords", "trade,sell,selling,buy,exchange,barter,Ð’Ð¢Ð¢,Ð’Ð¢S,WTB,WTB,WTT,WTS");
        String[] T_WORLDS = T_WORLD.split(",", -1);
        Collections.addAll(TRADE_WORDS, T_WORLDS);
        _log.info("Loaded " + TRADE_WORDS.size() + " trade words.");

        LOG_CHAT = chatSettings.getProperty("LogChat", false);
        CHAT_MESSAGE_MAX_LEN = chatSettings.getProperty("ChatMessageLimit", 1000);

        CHATFILTER_MIN_LEVEL = chatSettings.getProperty("ChatFilterMinLevel", 0);

        SHOUT_REQUIRED_LEVEL = chatSettings.getProperty("ShoutingInChat", 61);

    }

    private static void loadTelnetConfig() {
        ExProperties telnetSettings = load(TELNET_CONFIGURATION_FILE);

        IS_TELNET_ENABLED = telnetSettings.getProperty("EnableTelnet", false);
    }

    private static void loadResidenceConfig() {
        ExProperties residenceSettings = load(RESIDENCE_CONFIG_FILE);

        RESIDENCE_LEASE_FUNC_MULTIPLIER = residenceSettings.getProperty("ResidenceLeaseFuncMultiplier", 1.);

        List<Integer> tempCastleValidatonTime = residenceSettings.getProperty("CastleValidationDate", List.of(2, 4, 2003));
        CASTLE_VALIDATION_DATE = Calendar.getInstance();
        CASTLE_VALIDATION_DATE.set(Calendar.DAY_OF_MONTH, tempCastleValidatonTime.get(0));
        CASTLE_VALIDATION_DATE.set(Calendar.MONTH, tempCastleValidatonTime.get(1) - 1);
        CASTLE_VALIDATION_DATE.set(Calendar.YEAR, tempCastleValidatonTime.get(2));
        CASTLE_VALIDATION_DATE.set(Calendar.HOUR_OF_DAY, 0);
        CASTLE_VALIDATION_DATE.set(Calendar.MINUTE, 0);
        CASTLE_VALIDATION_DATE.set(Calendar.SECOND, 0);
        CASTLE_VALIDATION_DATE.set(Calendar.MILLISECOND, 0);

        ENABLE_ALT_FAME_REWARD = residenceSettings.getProperty("AltEnableCustomFame", false);
        ALT_FAME_CASTLE = residenceSettings.getProperty("CastleFame", 125);
        ALT_FAME_FORTRESS = residenceSettings.getProperty("FortressFame", 31);

        INTERVAL_FLAG_DROP = residenceSettings.getProperty("IntervalFlagDrop", 60);
        SIEGE_WINNER_REPUTATION_REWARD = residenceSettings.getProperty("SiegeWinnerReputationReward", 0);
    }

    private static void loadItemsUseConfig() {
        ExProperties itemsUseSettings = load(ITEM_USE_FILE);

        ITEM_USE_LIST_ID = itemsUseSettings.getProperty("ItemUseListId", List.of(
                725, 726, 727, 728));
        ITEM_USE_IS_COMBAT_FLAG = itemsUseSettings.getProperty("ItemUseIsCombatFlag", true);
        ITEM_USE_IS_ATTACK = itemsUseSettings.getProperty("ItemUseIsAttack", true);
    }

    private static void loadSchemeBuffer() {
        ExProperties npcbuffer = load(NPCBUFFER_CONFIG_FILE);

        NpcBuffer_VIP = npcbuffer.getProperty("EnableVIP", false);
        NpcBuffer_VIP_ALV = npcbuffer.getProperty("VipAccesLevel", 1);
        NpcBuffer_EnableScheme = npcbuffer.getProperty("EnableScheme", true);
        NpcBuffer_EnableHeal = npcbuffer.getProperty("EnableHeal", true);
        NpcBuffer_EnableBuffs = npcbuffer.getProperty("EnableBuffs", true);
        NpcBuffer_EnableResist = npcbuffer.getProperty("EnableResist", true);
        NpcBuffer_EnableSong = npcbuffer.getProperty("EnableSongs", true);
        NpcBuffer_EnableDance = npcbuffer.getProperty("EnableDances", true);
        NpcBuffer_EnableChant = npcbuffer.getProperty("EnableChants", true);
        NpcBuffer_EnableOther = npcbuffer.getProperty("EnableOther", true);
        NpcBuffer_EnableSpecial = npcbuffer.getProperty("EnableSpecial", true);
        NpcBuffer_EnableCubic = npcbuffer.getProperty("EnableCubic", false);
        NpcBuffer_EnableCancel = npcbuffer.getProperty("EnableRemoveBuffs", true);
        NpcBuffer_EnableBuffSet = npcbuffer.getProperty("EnableBuffSet", true);
        NpcBuffer_EnableBuffPK = npcbuffer.getProperty("EnableBuffForPK", false);
        NpcBuffer_EnableFreeBuffs = npcbuffer.getProperty("EnableFreeBuffs", true);
        SCHEME_ALLOW_FLAG = npcbuffer.getProperty("EnableBuffforFlag", false);
        NpcBuffer_MinLevel = npcbuffer.getProperty("MinimumLevel", 20);
        NpcBuffer_PriceCancel = npcbuffer.getProperty("RemoveBuffsPrice", 100000);
        NpcBuffer_PriceHeal = npcbuffer.getProperty("HealPrice", 100000);
        NpcBuffer_PriceBuffs = npcbuffer.getProperty("BuffsPrice", 100000);
        NpcBuffer_PriceResist = npcbuffer.getProperty("ResistPrice", 100000);
        NpcBuffer_PriceSong = npcbuffer.getProperty("SongPrice", 100000);
        NpcBuffer_PriceDance = npcbuffer.getProperty("DancePrice", 100000);
        NpcBuffer_PriceChant = npcbuffer.getProperty("ChantsPrice", 100000);
        NpcBuffer_PriceOther = npcbuffer.getProperty("OtherPrice", 100000);
        NpcBuffer_PriceSpecial = npcbuffer.getProperty("SpecialPrice", 100000);
        NpcBuffer_PriceCubic = npcbuffer.getProperty("CubicPrice", 100000);
        NpcBuffer_PriceSet = npcbuffer.getProperty("SetPrice", 100000);
        NpcBuffer_PriceScheme = npcbuffer.getProperty("SchemePrice", 100000);
        NpcBuffer_MaxScheme = npcbuffer.getProperty("MaxScheme", 4);


        String[] parts;
        String[] skills = npcbuffer.getProperty("BuffSetMage", "192,1").split(";");
        for (String sk : skills) {
            parts = sk.split(",");
            NpcBuffer_BuffSetMage.put(toInt(parts[0]), toInt(parts[1]));
        }

        skills = npcbuffer.getProperty("BuffSetFighter", "192,1").split(";");
        for (String sk : skills) {
            parts = sk.split(",");
            NpcBuffer_BuffSetFighter.put(toInt(parts[0]), toInt(parts[1]));
        }

        skills = npcbuffer.getProperty("BuffSetDagger", "192,1").split(";");
        for (String sk : skills) {
            parts = sk.split(",");
            NpcBuffer_BuffSetDagger.put(toInt(parts[0]), toInt(parts[1]));
        }

        skills = npcbuffer.getProperty("BuffSetSupport", "192,1").split(";");
        for (String sk : skills) {
            parts = sk.split(",");
            NpcBuffer_BuffSetSupport.put(toInt(parts[0]), toInt(parts[1]));
        }

        skills = npcbuffer.getProperty("BuffSetTank", "192,1").split(";");
        for (String sk : skills) {
            parts = sk.split(",");
            NpcBuffer_BuffSetTank.put(toInt(parts[0]), toInt(parts[1]));
        }

        skills = npcbuffer.getProperty("BuffSetArcher", "192,1").split(";");
        for (String sk : skills) {
            parts = sk.split(",");
            NpcBuffer_BuffSetArcher.put(toInt(parts[0]), toInt(parts[1]));
        }
    }

    private static void loadRatesConfig() {
        ExProperties ratesSettings = load(RATES_FILE);

        RATE_XP = ratesSettings.getProperty("RateXp", 1.);
        RATE_SP = ratesSettings.getProperty("RateSp", 1.);
        RATE_QUESTS_REWARD = ratesSettings.getProperty("RateQuestsReward", 1.);
        RATE_QUESTS_DROP = ratesSettings.getProperty("RateQuestsDrop", 1.);
        RATE_DROP_CHAMPION = ratesSettings.getProperty("RateDropChampion", 1.);
        RATE_CLAN_REP_SCORE = ratesSettings.getProperty("RateClanRepScore", 1.);
        RATE_CLAN_REP_SCORE_MAX_AFFECTED = ratesSettings.getProperty("RateClanRepScoreMaxAffected", 2);
        RATE_DROP_ADENA = ratesSettings.getProperty("RateDropAdena", 1.);
        RATE_CHAMPION_DROP_ADENA = ratesSettings.getProperty("RateChampionDropAdena", 1.);
        RATE_DROP_ITEMS = ratesSettings.getProperty("RateDropItems", 1.);
        RATE_CHANCE_GROUP_DROP_ITEMS = ratesSettings.getProperty("RateChanceGroupDropItems", 1.);
        RATE_CHANCE_DROP_ITEMS = ratesSettings.getProperty("RateChanceDropItems", 1.);
        RATE_CHANCE_DROP_HERBS = ratesSettings.getProperty("RateChanceDropHerbs", 1.);
        RATE_CHANCE_SPOIL = ratesSettings.getProperty("RateChanceSpoil", 1.);
        RATE_CHANCE_SPOIL_WEAPON_ARMOR_ACCESSORY = ratesSettings.getProperty("RateChanceSpoilWAA", 1.);
        RATE_CHANCE_DROP_WEAPON_ARMOR_ACCESSORY = ratesSettings.getProperty("RateChanceDropWAA", 1.);
        RATE_CHANCE_DROP_EPOLET = ratesSettings.getProperty("RateChanceDropEpolets", 1.);
        NO_RATE_KEY_MATERIAL = ratesSettings.getProperty("NoRateKeyMaterial", true);
        NO_RATE_RECIPES = ratesSettings.getProperty("NoRateRecipes", true);
        RATE_DROP_COMMON_ITEMS = ratesSettings.getProperty("RateDropCommonItems", 1.);
        RATE_DROP_RAIDBOSS = ratesSettings.getProperty("RateRaidBoss", 1.);
        RATE_DROP_SPOIL = ratesSettings.getProperty("RateDropSpoil", 1.);
        NO_RATE_ITEMS = ratesSettings.getProperty("NoRateItemIds", List.of(
                6660, 6662, 6661, 6659, 6656, 6658, 8191, 6657, 10170, 10314, 16025, 16026));
        NO_RATE_EQUIPMENT = ratesSettings.getProperty("NoRateEquipment", true);
        RATE_DROP_SIEGE_GUARD = ratesSettings.getProperty("RateSiegeGuard", 1.);
        RATE_MANOR = ratesSettings.getProperty("RateManor", 1.);
        RATE_FISH_DROP_COUNT = ratesSettings.getProperty("RateFishDropCount", 1.);
        RATE_PARTY_MIN = ratesSettings.getProperty("RatePartyMin", false);
        RATE_HELLBOUND_CONFIDENCE = ratesSettings.getProperty("RateHellboundConfidence", 1.);

        RATE_MOB_SPAWN = ratesSettings.getProperty("RateMobSpawn", 1);
        RATE_MOB_SPAWN_MIN_LEVEL = ratesSettings.getProperty("RateMobMinLevel", 1);
        RATE_MOB_SPAWN_MAX_LEVEL = ratesSettings.getProperty("RateMobMaxLevel", 100);
    }

    private static void loadBossConfig() {
        ExProperties bossSettings = load(BOSS_FILE);

        RATE_RAID_REGEN = bossSettings.getProperty("RateRaidRegen", 1.);
        RATE_RAID_DEFENSE = bossSettings.getProperty("RateRaidDefense", 1.);
        RATE_RAID_ATTACK = bossSettings.getProperty("RateRaidAttack", 1.);
        RATE_EPIC_DEFENSE = bossSettings.getProperty("RateEpicDefense", RATE_RAID_DEFENSE);
        RATE_EPIC_ATTACK = bossSettings.getProperty("RateEpicAttack", RATE_RAID_ATTACK);
        RAID_MAX_LEVEL_DIFF = bossSettings.getProperty("RaidMaxLevelDiff", 8);

        FRINTEZZA_ALL_MEMBERS_NEED_SCROLL = bossSettings.getProperty("FrintezzaAllMembersNeedScroll", true);
    }

    private static void loadl2fConfig() {
        ExProperties l2fConfig = load(l2f_TEAM_CONFIG_FILE);

        DONATOR_NPC_ITEM = l2fConfig.getProperty("DonatorNPCitem", 6673);
        DONATOR_NPC_ITEM_NAME = l2fConfig.getProperty("DonatorNPCitemName", "Donator Coin");
        DONATOR_NPC_COUNT_FAME = l2fConfig.getProperty("DonateFame", 10000);
        DONATOR_NPC_FAME = l2fConfig.getProperty("DonateCountFame", 5);
        DONATOR_NPC_COUNT_REP = l2fConfig.getProperty("DonateRep", 10000);
        DONATOR_NPC_REP = l2fConfig.getProperty("DonateCountClanRep", 5);
        DONATOR_NPC_COUNT_NOBLESS = l2fConfig.getProperty("DonateCountNobless", 5);
        DONATOR_NPC_COUNT_SEX = l2fConfig.getProperty("DonateCountChangeSex", 5);
        DONATOR_NPC_COUNT_LEVEL = l2fConfig.getProperty("DonateCountMaxLevel", 5);

    }

    private static void loadNpcConfig() {
        ExProperties npcSettings = load(NPC_FILE);

        MIN_NPC_ANIMATION = npcSettings.getProperty("MinNPCAnimation", 5);
        MAX_NPC_ANIMATION = npcSettings.getProperty("MaxNPCAnimation", 90);
        SERVER_SIDE_NPC_NAME = npcSettings.getProperty("ServerSideNpcName", false);
        SERVER_SIDE_NPC_TITLE = npcSettings.getProperty("ServerSideNpcTitle", false);
        SERVER_SIDE_NPC_TITLE_ETC = npcSettings.getProperty("ServerSideNpcTitleEtc", false);
    }

    private static void loadOtherConfig() {
        ExProperties otherSettings = load(OTHER_CONFIG_FILE);

        DEEPBLUE_DROP_RULES = otherSettings.getProperty("UseDeepBlueDropRules", true);
        DEEPBLUE_DROP_MAXDIFF = otherSettings.getProperty("DeepBlueDropMaxDiff", 8);
        DEEPBLUE_DROP_RAID_MAXDIFF = otherSettings.getProperty("DeepBlueDropRaidMaxDiff", 2);

        SWIMING_SPEED = otherSettings.getProperty("SwimingSpeedTemplate", 50);
        /* All item price 1 adena */
        SELL_ALL_ITEMS_FREE = otherSettings.getProperty("SellAllItemsFree", false);
        /* Inventory slots limits */
        INVENTORY_MAXIMUM_NO_DWARF = otherSettings.getProperty("MaximumSlotsForNoDwarf", 80);
        INVENTORY_MAXIMUM_DWARF = otherSettings.getProperty("MaximumSlotsForDwarf", 100);
        INVENTORY_MAXIMUM_GM = otherSettings.getProperty("MaximumSlotsForGMPlayer", 250);
        QUEST_INVENTORY_MAXIMUM = otherSettings.getProperty("MaximumSlotsForQuests", 100);

        MULTISELL_SIZE = otherSettings.getProperty("MultisellPageSize", 10);

        /* Warehouse slots limits */
        WAREHOUSE_SLOTS_NO_DWARF = otherSettings.getProperty("BaseWarehouseSlotsForNoDwarf", 100);
        WAREHOUSE_SLOTS_DWARF = otherSettings.getProperty("BaseWarehouseSlotsForDwarf", 120);
        WAREHOUSE_SLOTS_CLAN = otherSettings.getProperty("MaximumWarehouseSlotsForClan", 200);
        FREIGHT_SLOTS = otherSettings.getProperty("MaximumFreightSlots", 10);

        /* chance to enchant an item over safe level */
        SAFE_ENCHANT_LVL = otherSettings.getProperty("SafeEnchant", 0);
        SHOW_ENCHANT_EFFECT_RESULT = otherSettings.getProperty("ShowEnchantEffectResult", false);

        ENCHANT_ATTRIBUTE_STONE_CHANCE = otherSettings.getProperty("EnchantAttributeChance", 50);
        ENCHANT_ATTRIBUTE_CRYSTAL_CHANCE = otherSettings.getProperty("EnchantAttributeCrystalChance", 30);

        REGEN_SIT_WAIT = otherSettings.getProperty("RegenSitWait", false);
        HTML_WELCOME = otherSettings.getProperty("ShowHTMLWelcome", false);
        STARTING_ADENA = otherSettings.getProperty("StartingAdena", 0);

        UNSTUCK_SKILL = otherSettings.getProperty("UnstuckSkill", true);

        /* Maximum number of available slots for pvt stores */
        MAX_PVTSTORE_SLOTS_DWARF = otherSettings.getProperty("MaxPvtStoreSlotsDwarf", 5);
        MAX_PVTSTORE_SLOTS_OTHER = otherSettings.getProperty("MaxPvtStoreSlotsOther", 4);
        MAX_PVTCRAFT_SLOTS = otherSettings.getProperty("MaxPvtManufactureSlots", 20);

        SENDSTATUS_TRADE_MOD = otherSettings.getProperty("SendStatusTradeMod", 1.);
        SHOW_OFFLINE_MODE_IN_ONLINE = otherSettings.getProperty("ShowOfflineTradeInOnline", false);

        ANNOUNCE_MAMMON_SPAWN = otherSettings.getProperty("AnnounceMammonSpawn", true);

        NORMAL_NAME_COLOUR = Integer.decode("0x" + otherSettings.getProperty("NormalNameColour", "FFFFFF"));

        GAME_POINT_ITEM_ID = otherSettings.getProperty("GamePointItemId", -1);
        STARTING_LVL = otherSettings.getProperty("StartingLvL", 0);
        MAX_PLAYER_CONTRIBUTION = otherSettings.getProperty("MaxPlayerContribution", 1000000);

        ENCHANT_MAX_WEAPON = otherSettings.getProperty("EnchantMaxWeapon", 20);
        ENCHANT_MAX_ARMOR = otherSettings.getProperty("EnchantMaxArmor", 20);
        ENCHANT_MAX_JEWELRY = otherSettings.getProperty("EnchantMaxJewelry", 20);

        ENABLE_ACHIEVEMENTS = otherSettings.getProperty("EnableAchievements", true);

        ENABLE_PLAYER_ITEM_LOGS = otherSettings.getProperty("EnablePlayerItemLogs", false);


        DEBUFF_PROTECTION_SYSTEM = otherSettings.getProperty("DebuffProtectionSystem", false);
    }

    private static void loadSpoilConfig() {
        ExProperties spoilSettings = load(SPOIL_CONFIG_FILE);

        BASE_SPOIL_RATE = spoilSettings.getProperty("BasePercentChanceOfSpoilSuccess", 78.);
        MINIMUM_SPOIL_RATE = spoilSettings.getProperty("MinimumPercentChanceOfSpoilSuccess", 1.);
        ALT_SPOIL_FORMULA = spoilSettings.getProperty("AltFormula", false);
        MANOR_SOWING_BASIC_SUCCESS = spoilSettings.getProperty("BasePercentChanceOfSowingSuccess", 100.);
        MANOR_SOWING_ALT_BASIC_SUCCESS = spoilSettings.getProperty("BasePercentChanceOfSowingAltSuccess", 10.);
        MANOR_HARVESTING_BASIC_SUCCESS = spoilSettings.getProperty("BasePercentChanceOfHarvestingSuccess", 90.);
        MANOR_DIFF_PLAYER_TARGET = spoilSettings.getProperty("MinDiffPlayerMob", 5);
        MANOR_DIFF_PLAYER_TARGET_PENALTY = spoilSettings.getProperty("DiffPlayerMobPenalty", 5.);
        MANOR_DIFF_SEED_TARGET = spoilSettings.getProperty("MinDiffSeedMob", 5);
        MANOR_DIFF_SEED_TARGET_PENALTY = spoilSettings.getProperty("DiffSeedMobPenalty", 5.);
        ALLOW_MANOR = spoilSettings.getProperty("AllowManor", true);
        MANOR_REFRESH_TIME = spoilSettings.getProperty("AltManorRefreshTime", 20);
        MANOR_REFRESH_MIN = spoilSettings.getProperty("AltManorRefreshMin", 0);
        MANOR_APPROVE_TIME = spoilSettings.getProperty("AltManorApproveTime", 6);
        MANOR_APPROVE_MIN = spoilSettings.getProperty("AltManorApproveMin", 0);
        MANOR_MAINTENANCE_PERIOD = spoilSettings.getProperty("AltManorMaintenancePeriod", 360000);
    }

    private static void loadInstancesConfig() {
        ExProperties instancesSettings = load(INSTANCES_FILE);

        ALLOW_INSTANCES_LEVEL_MANUAL = instancesSettings.getProperty("AllowInstancesLevelManual", false);
        ALLOW_INSTANCES_PARTY_MANUAL = instancesSettings.getProperty("AllowInstancesPartyManual", false);
        INSTANCES_LEVEL_MIN = instancesSettings.getProperty("InstancesLevelMin", 1);
        INSTANCES_LEVEL_MAX = instancesSettings.getProperty("InstancesLevelMax", 85);
        INSTANCES_PARTY_MIN = instancesSettings.getProperty("InstancesPartyMin", 2);
        INSTANCES_PARTY_MAX = instancesSettings.getProperty("InstancesPartyMax", 100);
    }

    private static void loadEpicBossConfig() {
        ExProperties epicBossSettings = load(EPIC_BOSS_FILE);

        ANTHARAS_DEFAULT_SPAWN_HOURS = epicBossSettings.getProperty("AntharasDefaultSpawnHours", 168);
        ANTHARAS_RANDOM_SPAWN_HOURS = epicBossSettings.getProperty("AntharasRandomSpawnHours", 8);
        VALAKAS_DEFAULT_SPAWN_HOURS = epicBossSettings.getProperty("ValakasDefaultSpawnHours", 240);
        VALAKAS_RANDOM_SPAWN_HOURS = epicBossSettings.getProperty("ValakasRandomSpawnHours", 24);
        BAIUM_DEFAULT_SPAWN_HOURS = epicBossSettings.getProperty("BaiumDefaultSpawnHours", 120);
        BAIUM_RANDOM_SPAWN_HOURS = epicBossSettings.getProperty("BaiumRandomSpawnHours", 8);

        FIXINTERVALOFBAYLORSPAWN_HOUR = epicBossSettings.getProperty("BaylorDefaultSpawnHours", 24);
        RANDOMINTERVALOFBAYLORSPAWN = epicBossSettings.getProperty("BaylorRandomSpawnHours", 24);
        FIXINTERVALOFBELETHSPAWN_HOUR = epicBossSettings.getProperty("BelethDefaultSpawnHours", 48);
        BELETH_CLONES_RESPAWN_TIME = epicBossSettings.getProperty("BelethClonesRespawnTime", 40);
        MIN_PLAYERS_TO_SPAWN_BELETH = epicBossSettings.getProperty("MinPlayersToSpawnBeleth", 18);
        FIXINTERVALOFSAILRENSPAWN_HOUR = epicBossSettings.getProperty("SailrenDefaultSpawnHours", 24);
        RANDOMINTERVALOFSAILRENSPAWN = epicBossSettings.getProperty("SailrenRandomSpawnHours", 24);
    }

    private static void loadDevelopSettings() {
        ExProperties DevelopSettings = load(DEVELOP_FILE);

        ALT_DEBUG_ENABLED = DevelopSettings.getProperty("AltDebugEnabled", false);
        ALT_DEBUG_PVP_ENABLED = DevelopSettings.getProperty("AltDebugPvPEnabled", false);
        ALT_DEBUG_PVP_DUEL_ONLY = DevelopSettings.getProperty("AltDebugPvPDuelOnly", true);
        ALT_DEBUG_PVE_ENABLED = DevelopSettings.getProperty("AltDebugPvEEnabled", false);

        DONTLOADSPAWN = DevelopSettings.getProperty("StartWithoutSpawn", false);
        DONTLOADQUEST = DevelopSettings.getProperty("StartWithoutQuest", false);
        LOAD_CUSTOM_SPAWN = DevelopSettings.getProperty("LoadAddGmSpawn", false);
        SAVE_GM_SPAWN = DevelopSettings.getProperty("SaveGmSpawn", false);
    }

    private static void loadExtSettings() {
        ExProperties properties = load(EXT_FILE);

        EX_JAPAN_MINIGAME = properties.getProperty("JapanMinigame", false);
        EX_LECTURE_MARK = properties.getProperty("LectureMark", false);

        Random ppc = new Random();
        int z = ppc.nextInt(6);
        if (z == 0) {
            z += 2;
        }
        for (int x = 0; x < 8; x++) {
            if (x == 4) {
                RWHO_ARRAY[x] = 44;
            } else {
                RWHO_ARRAY[x] = 51 + ppc.nextInt(z);
            }
        }
        RWHO_ARRAY[11] = 37265 + ppc.nextInt((z * 2) + 3);
        RWHO_ARRAY[8] = 51 + ppc.nextInt(z);
        z = 36224 + ppc.nextInt(z * 2);
        RWHO_ARRAY[9] = z;
        RWHO_ARRAY[10] = z;
        RWHO_ARRAY[12] = 1;
        RWHO_SEND_TRASH = properties.getProperty("RemoteWhoSendTrash", false);
        RWHO_KEEP_STAT = properties.getProperty("RemoteOnlineKeepStat", 5);
    }

    private static void loadItemsSettings() {
        ExProperties itemsProperties = load(ITEMS_FILE);

        CAN_BE_TRADED_NO_TARADEABLE = itemsProperties.getProperty("CanBeTradedNoTradeable", false);
        CAN_BE_TRADED_NO_SELLABLE = itemsProperties.getProperty("CanBeTradedNoSellable", false);
        CAN_BE_TRADED_NO_STOREABLE = itemsProperties.getProperty("CanBeTradedNoStoreable", false);
        CAN_BE_TRADED_SHADOW_ITEM = itemsProperties.getProperty("CanBeTradedShadowItem", false);
        CAN_BE_TRADED_HERO_WEAPON = itemsProperties.getProperty("CanBeTradedHeroWeapon", false);
        CAN_BE_CWH_IS_AUGMENTED = itemsProperties.getProperty("CanBeCwhIsAugmented", false);
        ALLOW_SOUL_SPIRIT_SHOT_INFINITELY = itemsProperties.getProperty("AllowSoulSpiritShotInfinitely", false);
        ALLOW_ARROW_INFINITELY = itemsProperties.getProperty("AllowArrowInfinitely", false);
        ALLOW_START_ITEMS = itemsProperties.getProperty("AllowStartItems", false);
        START_ITEMS_MAGE = itemsProperties.getProperty("StartItemsMageIds", List.of(57));
        START_ITEMS_MAGE_COUNT = itemsProperties.getProperty("StartItemsMageCount", List.of(1));
        START_ITEMS_FITHER = itemsProperties.getProperty("StartItemsFigtherIds", List.of(57));
        START_ITEMS_FITHER_COUNT = itemsProperties.getProperty("StartItemsFigtherCount", List.of(1));
    }


    private static void loadAltSettings() {
        ExProperties altSettings = load(ALT_SETTINGS_FILE);
        AUTO_SOUL_CRYSTAL_QUEST = altSettings.getProperty("AutoSoulCrystalQuest", true);
        ALT_GAME_DELEVEL = altSettings.getProperty("Delevel", true);
        ALT_MAIL_MIN_LVL = altSettings.getProperty("MinLevelToSendMail", 0);
        VITAMIN_PETS_FOOD_ID = altSettings.getProperty("VitaminPetsFoodID", -1);
        VITAMIN_DESELOT_FOOD_ID = altSettings.getProperty("VitaminDeselotFoodID", -1);
        ALT_AFTER_CANCEL_RETURN_SKILLS_TIME = altSettings.getProperty("RestoreCanceledBuffs", 0);
        VITAMIN_SUPERPET_FOOD_ID = altSettings.getProperty("VitaminSuperPetID", -1);
        ALT_SAVE_UNSAVEABLE = altSettings.getProperty("AltSaveUnsaveable", false);
        SHIELD_SLAM_BLOCK_IS_MUSIC = altSettings.getProperty("ShieldSlamBlockIsMusic", false);
        ALT_SAVE_EFFECTS_REMAINING_TIME = altSettings.getProperty("AltSaveEffectsRemainingTime", 5);
        ALLOW_PET_ATTACK_MASTER = altSettings.getProperty("allowPetAttackMaster", true);
        ALT_SHOW_REUSE_MSG = altSettings.getProperty("AltShowSkillReuseMessage", true);
        ALT_DELETE_SA_BUFFS = altSettings.getProperty("AltDeleteSABuffs", false);
        ALT_GAME_KARMA_PLAYER_CAN_SHOP = altSettings.getProperty("AltKarmaPlayerCanShop", false);
        CRAFT_MASTERWORK_CHANCE = altSettings.getProperty("CraftMasterworkChance", 3.);
        CRAFT_DOUBLECRAFT_CHANCE = altSettings.getProperty("CraftDoubleCraftChance", 3.);
        ALT_RAID_RESPAWN_MULTIPLIER = altSettings.getProperty("AltRaidRespawnMultiplier", 1.0);
        ALT_ALLOW_AUGMENT_ALL = altSettings.getProperty("AugmentAll", false);
        ALT_ALLOW_DROP_AUGMENTED = altSettings.getProperty("AlowDropAugmented", false);
        ALT_GAME_UNREGISTER_RECIPE = altSettings.getProperty("AltUnregisterRecipe", true);
        ALT_GAME_SHOW_DROPLIST = altSettings.getProperty("AltShowDroplist", true);
        ALLOW_NPC_SHIFTCLICK = altSettings.getProperty("AllowShiftClick", true);
        ALT_FULL_NPC_STATS_PAGE = altSettings.getProperty("AltFullStatsPage", false);
        ALT_GAME_SUBCLASS_WITHOUT_QUESTS = altSettings.getProperty("AltAllowSubClassWithoutQuest", false);
        ALT_ALLOW_SUBCLASS_WITHOUT_BAIUM = altSettings.getProperty("AltAllowSubClassWithoutBaium", true);
        ALT_GAME_LEVEL_TO_GET_SUBCLASS = altSettings.getProperty("AltLevelToGetSubclass", 75);
        ALT_GAME_START_LEVEL_TO_SUBCLASS = altSettings.getProperty("AltStartLevelToSubclass", 40);
        ALT_GAME_SUB_ADD = altSettings.getProperty("AltSubAdd", 0);
        ALT_GAME_SUB_BOOK = altSettings.getProperty("AltSubBook", false);
        ALT_MAX_LEVEL = Math.min(altSettings.getProperty("AltMaxLevel", 85), Experience.LEVEL.length - 1);
        ALT_MAX_SUB_LEVEL = Math.min(altSettings.getProperty("AltMaxSubLevel", 80), Experience.LEVEL.length - 1);
        ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE = altSettings.getProperty("AltAllowOthersWithdrawFromClanWarehouse", false);
        ALT_ALLOW_CLAN_COMMAND_ONLY_FOR_CLAN_LEADER = altSettings.getProperty("AltAllowClanCommandOnlyForClanLeader", true);
        ALT_GAME_REQUIRE_CLAN_CASTLE = altSettings.getProperty("AltRequireClanCastle", false);
        ALT_GAME_REQUIRE_CASTLE_DAWN = altSettings.getProperty("AltRequireCastleDawn", true);
        ALT_GAME_ALLOW_ADENA_DAWN = altSettings.getProperty("AltAllowAdenaDawn", true);
        RETAIL_SS = altSettings.getProperty("Retail_SevenSigns", true);
        ALT_ADD_RECIPES = altSettings.getProperty("AltAddRecipes", 0);
        SS_ANNOUNCE_PERIOD = altSettings.getProperty("SSAnnouncePeriod", 0);
        PETITIONING_ALLOWED = altSettings.getProperty("PetitioningAllowed", true);
        MAX_PETITIONS_PER_PLAYER = altSettings.getProperty("MaxPetitionsPerPlayer", 5);
        MAX_PETITIONS_PENDING = altSettings.getProperty("MaxPetitionsPending", 25);
        AUTO_LEARN_SKILLS = altSettings.getProperty("AutoLearnSkills", false);
        AUTO_LEARN_FORGOTTEN_SKILLS = altSettings.getProperty("AutoLearnForgottenSkills", false);
        ALT_SOCIAL_ACTION_REUSE = altSettings.getProperty("AltSocialActionReuse", false);
        ALT_DISABLE_SPELLBOOKS = altSettings.getProperty("AltDisableSpellbooks", false);
        ALT_SIMPLE_SIGNS = altSettings.getProperty("PushkinSignsOptions", false);
        ALT_TELE_TO_CATACOMBS = altSettings.getProperty("TeleToCatacombs", false);
        ALT_BS_CRYSTALLIZE = altSettings.getProperty("BSCrystallize", false);
        ALT_ALLOW_TATTOO = altSettings.getProperty("AllowTattoo", false);
        ALT_BUFF_LIMIT = altSettings.getProperty("BuffLimit", 20);
        ALT_DEATH_PENALTY = altSettings.getProperty("EnableAltDeathPenalty", false);
        ALLOW_DEATH_PENALTY_C5 = altSettings.getProperty("EnableDeathPenaltyC5", true);
        ALT_DEATH_PENALTY_C5_CHANCE = altSettings.getProperty("DeathPenaltyC5Chance", 10);
        ALT_DEATH_PENALTY_C5_CHAOTIC_RECOVERY = altSettings.getProperty("ChaoticCanUseScrollOfRecovery", false);
        ALT_DEATH_PENALTY_C5_EXPERIENCE_PENALTY = altSettings.getProperty("DeathPenaltyC5RateExpPenalty", 1);
        ALT_DEATH_PENALTY_C5_KARMA_PENALTY = altSettings.getProperty("DeathPenaltyC5RateKarma", 1);
        ALT_PK_DEATH_RATE = altSettings.getProperty("AltPKDeathRate", 0.);
        NONOWNER_ITEM_PICKUP_DELAY = altSettings.getProperty("NonOwnerItemPickupDelay", 15L) * 1000L;
        ALT_NO_LASTHIT = altSettings.getProperty("NoLasthitOnRaid", false);
        ALT_DISPEL_MUSIC = altSettings.getProperty("AltDispelDanceSong", false);
        ALT_KAMALOKA_NIGHTMARES_PREMIUM_ONLY = altSettings.getProperty("KamalokaNightmaresPremiumOnly", false);
        ALT_PET_HEAL_BATTLE_ONLY = altSettings.getProperty("PetsHealOnlyInBattle", true);
        CHAR_TITLE = altSettings.getProperty("CharTitle", false);
        ADD_CHAR_TITLE = altSettings.getProperty("CharAddTitle", "");

        ALT_ALLOW_SELL_COMMON = altSettings.getProperty("AllowSellCommon", true);
        ALT_ALLOW_SHADOW_WEAPONS = altSettings.getProperty("AllowShadowWeapons", true);
        ALT_DISABLED_MULTISELL = altSettings.getProperty("DisabledMultisells", new ArrayList<>());
        ALT_SHOP_PRICE_LIMITS = altSettings.getProperty("ShopPriceLimits", new ArrayList<>());
        ALT_SHOP_UNALLOWED_ITEMS = altSettings.getProperty("ShopUnallowedItems", new ArrayList<>());

        ALT_ALLOWED_PET_POTIONS = altSettings.getProperty("AllowedPetPotions", List.of(
                735, 1060, 1061, 1062, 1374, 1375, 1539, 1540, 6035, 6036));

        FESTIVAL_MIN_PARTY_SIZE = altSettings.getProperty("FestivalMinPartySize", 5);
        FESTIVAL_RATE_PRICE = altSettings.getProperty("FestivalRatePrice", 1.0);

        ENABLE_POLL_SYSTEM = altSettings.getProperty("EnablePoll", true);
        ANNOUNCE_POLL_EVERY_X_MIN = altSettings.getProperty("AnnounceToVoteInMin", 10);

        RIFT_MIN_PARTY_SIZE = altSettings.getProperty("RiftMinPartySize", 5);
        RIFT_SPAWN_DELAY = altSettings.getProperty("RiftSpawnDelay", 10000);
        RIFT_MAX_JUMPS = altSettings.getProperty("MaxRiftJumps", 4);
        RIFT_AUTO_JUMPS_TIME = altSettings.getProperty("AutoJumpsDelay", 8);
        RIFT_AUTO_JUMPS_TIME_RAND = altSettings.getProperty("AutoJumpsDelayRandom", 120000);

        RIFT_ENTER_COST_RECRUIT = altSettings.getProperty("RecruitFC", 18);
        RIFT_ENTER_COST_SOLDIER = altSettings.getProperty("SoldierFC", 21);
        RIFT_ENTER_COST_OFFICER = altSettings.getProperty("OfficerFC", 24);
        RIFT_ENTER_COST_CAPTAIN = altSettings.getProperty("CaptainFC", 27);
        RIFT_ENTER_COST_COMMANDER = altSettings.getProperty("CommanderFC", 30);
        RIFT_ENTER_COST_HERO = altSettings.getProperty("HeroFC", 33);
        ALLOW_LEARN_TRANS_SKILLS_WO_QUEST = altSettings.getProperty("AllowLearnTransSkillsWOQuest", false);
        PARTY_LEADER_ONLY_CAN_INVITE = altSettings.getProperty("PartyLeaderOnlyCanInvite", true);
        ALLOW_TALK_WHILE_SITTING = altSettings.getProperty("AllowTalkWhileSitting", true);
        ALLOW_NOBLE_TP_TO_ALL = altSettings.getProperty("AllowNobleTPToAll", false);

        CLANHALL_BUFFTIME_MODIFIER = altSettings.getProperty("ClanHallBuffTimeModifier", 1.0);
        SONGDANCETIME_MODIFIER = altSettings.getProperty("SongDanceTimeModifier", 1.0);
        MAXLOAD_MODIFIER = altSettings.getProperty("MaxLoadModifier", 1.0);
        GATEKEEPER_MODIFIER = altSettings.getProperty("GkCostMultiplier", 1.0);
        GATEKEEPER_FREE = altSettings.getProperty("GkFree", 40);
        CRUMA_GATEKEEPER_LVL = altSettings.getProperty("GkCruma", 65);
        ALT_IMPROVED_PETS_LIMITED_USE = altSettings.getProperty("ImprovedPetsLimitedUse", false);

        ALT_CHAMPION_CHANCE1 = altSettings.getProperty("AltChampionChance1", 0.);
        ALT_CHAMPION_CHANCE2 = altSettings.getProperty("AltChampionChance2", 0.);
        ALT_CHAMPION_CAN_BE_AGGRO = altSettings.getProperty("AltChampionAggro", false);
        ALT_CHAMPION_CAN_BE_SOCIAL = altSettings.getProperty("AltChampionSocial", false);
        ALT_CHAMPION_TOP_LEVEL = altSettings.getProperty("AltChampionTopLevel", 75);
        ALT_CHAMPION_MIN_LEVEL = altSettings.getProperty("AltChampionMinLevel", 20);

        ALT_VITALITY_ENABLED = altSettings.getProperty("AltVitalityEnabled", true);
        ALT_VITALITY_RATE = altSettings.getProperty("AltVitalityRate", 1.);
        ALT_VITALITY_CONSUME_RATE = altSettings.getProperty("AltVitalityConsumeRate", 1.);
        ALT_VITALITY_RAID_BONUS = altSettings.getProperty("AltVitalityRaidBonus", 2000);

        ALT_PCBANG_POINTS_ENABLED = altSettings.getProperty("AltPcBangPointsEnabled", false);
        ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE = altSettings.getProperty("AltPcBangPointsDoubleChance", 10.);
        ALT_PCBANG_POINTS_BONUS = altSettings.getProperty("AltPcBangPointsBonus", 0);
        ALT_PCBANG_POINTS_DELAY = altSettings.getProperty("AltPcBangPointsDelay", 20);
        ALT_PCBANG_POINTS_MIN_LVL = altSettings.getProperty("AltPcBangPointsMinLvl", 1);

        ALT_MAX_ALLY_SIZE = altSettings.getProperty("AltMaxAllySize", 3);
        ALT_PARTY_DISTRIBUTION_RANGE = altSettings.getProperty("AltPartyDistributionRange", 1500);
        ALT_PARTY_BONUS = altSettings.getProperty("AltPartyBonus", List.of(
                100, 110, 120, 130, 140, 150, 200, 210, 220));

        ALT_LEVEL_DIFFERENCE_PROTECTION = altSettings.getProperty("LevelDifferenceProtection", -100);

        ALT_REMOVE_SKILLS_ON_DELEVEL = altSettings.getProperty("AltRemoveSkillsOnDelevel", true);
        ALT_CH_ALL_BUFFS = altSettings.getProperty("AltChAllBuffs", false);
        ALT_CH_ALLOW_1H_BUFFS = altSettings.getProperty("AltChAllowHourBuff", false);

        AUGMENTATION_NG_SKILL_CHANCE = altSettings.getProperty("AugmentationNGSkillChance", 15);
        AUGMENTATION_NG_GLOW_CHANCE = altSettings.getProperty("AugmentationNGGlowChance", 0);
        AUGMENTATION_MID_SKILL_CHANCE = altSettings.getProperty("AugmentationMidSkillChance", 30);
        AUGMENTATION_MID_GLOW_CHANCE = altSettings.getProperty("AugmentationMidGlowChance", 40);
        AUGMENTATION_HIGH_SKILL_CHANCE = altSettings.getProperty("AugmentationHighSkillChance", 45);
        AUGMENTATION_HIGH_GLOW_CHANCE = altSettings.getProperty("AugmentationHighGlowChance", 70);
        AUGMENTATION_TOP_SKILL_CHANCE = altSettings.getProperty("AugmentationTopSkillChance", 60);
        AUGMENTATION_TOP_GLOW_CHANCE = altSettings.getProperty("AugmentationTopGlowChance", 100);
        AUGMENTATION_BASESTAT_CHANCE = altSettings.getProperty("AugmentationBaseStatChance", 1);
        AUGMENTATION_ACC_SKILL_CHANCE = altSettings.getProperty("AugmentationAccSkillChance", 10);

        ALT_OPEN_CLOAK_SLOT = altSettings.getProperty("OpenCloakSlot", false);

        FOLLOW_RANGE = altSettings.getProperty("FollowRange", 100);

        ALT_ENABLE_MULTI_PROFA = altSettings.getProperty("AltEnableMultiProfa", false);

        ALT_ITEM_AUCTION_ENABLED = altSettings.getProperty("AltItemAuctionEnabled", true);
        ALT_ITEM_AUCTION_CAN_REBID = altSettings.getProperty("AltItemAuctionCanRebid", false);
        ALT_ITEM_AUCTION_START_ANNOUNCE = altSettings.getProperty("AltItemAuctionAnnounce", true);
        ALT_ITEM_AUCTION_BID_ITEM_ID = altSettings.getProperty("AltItemAuctionBidItemId", 57);
        ALT_ITEM_AUCTION_MAX_BID = altSettings.getProperty("AltItemAuctionMaxBid", 1000000L);
        ALT_ITEM_AUCTION_MAX_CANCEL_TIME_IN_MILLIS = altSettings.getProperty("AltItemAuctionMaxCancelTimeInMillis", 604800000);

        ENABLE_AUCTION_SYSTEM = altSettings.getProperty("EnableAuctionSystem", true);
        AUCTION_FEE = toInt(altSettings.getProperty("AuctionFee", "10000"));
        ALLOW_AUCTION_OUTSIDE_TOWN = altSettings.getProperty("AuctionOutsideTown", false);
        SECONDS_BETWEEN_ADDING_AUCTIONS = toInt(altSettings.getProperty("AuctionAddDelay", "30"));
        AUCTION_PRIVATE_STORE_AUTO_ADDED = altSettings.getProperty("AuctionPrivateStoreAutoAdded", true);

        ALT_FISH_CHAMPIONSHIP_ENABLED = altSettings.getProperty("AltFishChampionshipEnabled", true);
        ALT_FISH_CHAMPIONSHIP_REWARD_ITEM = altSettings.getProperty("AltFishChampionshipRewardItemId", 57);
        ALT_FISH_CHAMPIONSHIP_REWARD_1 = altSettings.getProperty("AltFishChampionshipReward1", 800000);
        ALT_FISH_CHAMPIONSHIP_REWARD_2 = altSettings.getProperty("AltFishChampionshipReward2", 500000);
        ALT_FISH_CHAMPIONSHIP_REWARD_3 = altSettings.getProperty("AltFishChampionshipReward3", 300000);
        ALT_FISH_CHAMPIONSHIP_REWARD_4 = altSettings.getProperty("AltFishChampionshipReward4", 200000);
        ALT_FISH_CHAMPIONSHIP_REWARD_5 = altSettings.getProperty("AltFishChampionshipReward5", 100000);

        ALT_ENABLE_BLOCK_CHECKER_EVENT = altSettings.getProperty("EnableBlockCheckerEvent", true);
        ALT_MIN_BLOCK_CHECKER_TEAM_MEMBERS = Math.min(Math.max(altSettings.getProperty("BlockCheckerMinTeamMembers", 1), 1), 6);
        ALT_RATE_COINS_REWARD_BLOCK_CHECKER = altSettings.getProperty("BlockCheckerRateCoinReward", 1.);

        ALT_HBCE_FAIR_PLAY = altSettings.getProperty("HBCEFairPlay", false);

        ALT_PET_INVENTORY_LIMIT = altSettings.getProperty("AltPetInventoryLimit", 12);
        ALT_CLAN_LEVEL_CREATE = altSettings.getProperty("ClanLevelCreate", 0);
        CLAN_LEVEL_6_COST = altSettings.getProperty("ClanLevel6Cost", 5000);
        CLAN_LEVEL_7_COST = altSettings.getProperty("ClanLevel7Cost", 10000);
        CLAN_LEVEL_8_COST = altSettings.getProperty("ClanLevel8Cost", 20000);
        CLAN_LEVEL_9_COST = altSettings.getProperty("ClanLevel9Cost", 40000);
        CLAN_LEVEL_10_COST = altSettings.getProperty("ClanLevel10Cost", 40000);
        CLAN_LEVEL_11_COST = altSettings.getProperty("ClanLevel11Cost", 75000);
        CLAN_LEVEL_6_REQUIREMEN = altSettings.getProperty("ClanLevel6Requirement", 30);
        CLAN_LEVEL_7_REQUIREMEN = altSettings.getProperty("ClanLevel7Requirement", 50);
        CLAN_LEVEL_8_REQUIREMEN = altSettings.getProperty("ClanLevel8Requirement", 80);
        CLAN_LEVEL_9_REQUIREMEN = altSettings.getProperty("ClanLevel9Requirement", 120);
        CLAN_LEVEL_10_REQUIREMEN = altSettings.getProperty("ClanLevel10Requirement", 140);
        CLAN_LEVEL_11_REQUIREMEN = altSettings.getProperty("ClanLevel11Requirement", 170);
        BLOOD_OATHS = altSettings.getProperty("BloodOaths", 150);
        BLOOD_PLEDGES = altSettings.getProperty("BloodPledges", 5);
        MIN_ACADEM_POINT = altSettings.getProperty("MinAcademPoint", 190);
        MAX_ACADEM_POINT = altSettings.getProperty("MaxAcademPoint", 650);

        HELLBOUND_LEVEL = altSettings.getProperty("HellboundLevel", 0);

        CLAN_LEAVE_PENALTY = altSettings.getProperty("ClanLeavePenalty", 24);
        ALLY_LEAVE_PENALTY = altSettings.getProperty("AllyLeavePenalty", 24);
        DISSOLVED_ALLY_PENALTY = altSettings.getProperty("DissolveAllyPenalty", 24);

        SIEGE_PVP_COUNT = altSettings.getProperty("SiegePvpCount", false);
        ZONE_PVP_COUNT = altSettings.getProperty("ZonePvpCount", false);
        EXPERTISE_PENALTY = altSettings.getProperty("ExpertisePenalty", true);
        ALT_MUSIC_LIMIT = altSettings.getProperty("MusicLimit", 12);
        ALT_DEBUFF_LIMIT = altSettings.getProperty("DebuffLimit", 8);
        ALT_TRIGGER_LIMIT = altSettings.getProperty("TriggerLimit", 12);
        ENABLE_MODIFY_SKILL_DURATION = altSettings.getProperty("EnableSkillDuration", false);
        if (ENABLE_MODIFY_SKILL_DURATION) {
            String[] propertySplit = altSettings.getProperty("SkillDurationList", "").split(";");
            SKILL_DURATION_LIST = new HashMap<>(propertySplit.length);
            for (String skill : propertySplit) {
                String[] skillSplit = skill.split(",");
                if (skillSplit.length != 2) {
                    _log.warn("[SkillDurationList]: invalid config property -> SkillDurationList \"" + skill + "\"");
                } else {
                    try {
                        SKILL_DURATION_LIST.put(toInt(skillSplit[0]), toInt(skillSplit[1]));
                    } catch (NumberFormatException nfe) {
                        if (!skill.isEmpty()) {
                            _log.warn("[SkillDurationList]: invalid config property -> SkillList \"" + skillSplit[0] + "\"" + skillSplit[1]);
                        }
                    }
                }
            }
        }

        ANCIENT_HERB_SPAWN_RADIUS = altSettings.getProperty("AncientHerbSpawnRadius", 600);
        ANCIENT_HERB_SPAWN_CHANCE = altSettings.getProperty("AncientHerbSpawnChance", 3);
        ANCIENT_HERB_SPAWN_COUNT = altSettings.getProperty("AncientHerbSpawnCount", 5);
        ANCIENT_HERB_RESPAWN_TIME = altSettings.getProperty("AncientHerbRespawnTime", 60) * 1000;
        ANCIENT_HERB_DESPAWN_TIME = altSettings.getProperty("AncientHerbDespawnTime", 60) * 1000;
        String[] locs = altSettings.getProperty("AncientHerbSpawnPoints", "").split(";");
        for (String string : locs) {
            if (string != null) {
                String[] cords = string.split(",");
                int x = toInt(cords[0]);
                int y = toInt(cords[1]);
                int z = toInt(cords[2]);
                HEIN_FIELDS_LOCATIONS.add(new Location(x, y, z));
            }
        }
    }

    private static void loadPvPmodConfig() {
        ExProperties PvPmodConfig = load(PVP_MOD_CONFIG_FILE);

        ATT_MOD_ARMOR = PvPmodConfig.getProperty("att_mod_Armor", 6);
        ATT_MOD_WEAPON = PvPmodConfig.getProperty("att_mod_Weapon", 5);
        ATT_MOD_WEAPON1 = PvPmodConfig.getProperty("att_mod_Weapon1", 20);

        ATT_MOD_MAX_ARMOR = PvPmodConfig.getProperty("att_mod_max_armor", 60);
        ATT_MOD_MAX_WEAPON = PvPmodConfig.getProperty("att_mod_max_weapon", 150);

        // by Grivesky
        HENNA_STATS = PvPmodConfig.getProperty("HennaStats", 5);
        ADEPT_ENABLE = PvPmodConfig.getProperty("ADEPT_ENABLE", true);

        SPAWN_CITIES_TREE = PvPmodConfig.getProperty("SPAWN_CITIES_TREE", true);
        SPAWN_NPC_BUFFER = PvPmodConfig.getProperty("SPAWN_NPC_BUFFER", true);
        SPAWN_scrubwoman = PvPmodConfig.getProperty("SPAWN_scrubwoman", true);

    }

    private static void loadServicesSettings() {
        ExProperties servicesSettings = load(SERVICES_FILE);

        _coinID = servicesSettings.getProperty("Id_Item_Mall", 57);
        ENTER_WORLD_ANNOUNCEMENTS_HERO_LOGIN = servicesSettings.getProperty("AnnounceHero", false);
        ENTER_WORLD_ANNOUNCEMENTS_LORD_LOGIN = servicesSettings.getProperty("AnnounceLord", false);
        SERVICES_DELEVEL_ENABLED = servicesSettings.getProperty("AllowDelevel", false);
        SERVICES_DELEVEL_ITEM = servicesSettings.getProperty("DelevelItem", 57);
        SERVICES_DELEVEL_COUNT = servicesSettings.getProperty("DelevelCount", 1000);
        SERVICES_DELEVEL_MIN_LEVEL = servicesSettings.getProperty("DelevelMinLevel", 1);
        ALLOW_MAIL_OPTION = servicesSettings.getProperty("AllowMailOption", false);

        servicesSettings.getProperty("AllowClassMasters", new ArrayList<>()).stream()
                .filter(id -> id != 0)
                .forEach(ALLOW_CLASS_MASTERS_LIST::add);


        CLASS_MASTERS_PRICE = servicesSettings.getProperty("ClassMastersPrice", "0,0,0");
        setClassMasterPriceList();
        SERVICES_RIDE_HIRE_ENABLED = servicesSettings.getProperty("RideHireEnabled", false);
        CLASS_MASTERS_PRICE_ITEM = servicesSettings.getProperty("ClassMastersPriceItem", 57);

        SERVICES_CHANGE_PET_NAME_ENABLED = servicesSettings.getProperty("PetNameChangeEnabled", false);
        SERVICES_CHANGE_PET_NAME_PRICE = servicesSettings.getProperty("PetNameChangePrice", 100);
        SERVICES_CHANGE_PET_NAME_ITEM = servicesSettings.getProperty("PetNameChangeItem", 4037);

        SERVICES_EXCHANGE_BABY_PET_ENABLED = servicesSettings.getProperty("BabyPetExchangeEnabled", false);
        SERVICES_EXCHANGE_BABY_PET_PRICE = servicesSettings.getProperty("BabyPetExchangePrice", 100);
        SERVICES_EXCHANGE_BABY_PET_ITEM = servicesSettings.getProperty("BabyPetExchangeItem", 4037);

        SERVICES_CHANGE_NICK_COLOR_ENABLED = servicesSettings.getProperty("NickColorChangeEnabled", false);
        SERVICES_CHANGE_NICK_COLOR_PRICE = servicesSettings.getProperty("NickColorChangePrice", 100);
        SERVICES_CHANGE_NICK_COLOR_ITEM = servicesSettings.getProperty("NickColorChangeItem", 4037);
        SERVICES_CHANGE_NICK_COLOR_LIST = servicesSettings.getProperty("NickColorChangeList", new String[]{"00FF00"});

        SERVICES_CHANGE_Title_COLOR_PRICE = servicesSettings.getProperty("TitleColorChangePrice", 100);
        SERVICES_CHANGE_Title_COLOR_ITEM = servicesSettings.getProperty("TitleColorChangeItem", 4037);
        SERVICES_CHANGE_Title_COLOR_LIST = servicesSettings.getProperty("TitleColorChangeList", new String[]{"00FF00"});



        SERVICES_WASH_PK_ENABLED = servicesSettings.getProperty("WashPkEnabled", false);
        SERVICES_WASH_PK_ITEM = servicesSettings.getProperty("WashPkItem", 4037);
        SERVICES_WASH_PK_PRICE = servicesSettings.getProperty("WashPkPrice", 5);
        // Service PK Clear from community board
        SERVICES_CLEAR_PK_PRICE = servicesSettings.getProperty("ClearPkPrice", 10000);
        SERVICES_CLEAR_PK_PRICE_ITEM_ID = servicesSettings.getProperty("ClearPkPriceID", 57);
        SERVICES_CLEAR_PK_COUNT = servicesSettings.getProperty("ClearPkCount", 1);

        SERVICES_EXPAND_INVENTORY_ENABLED = servicesSettings.getProperty("ExpandInventoryEnabled", false);
        SERVICES_EXPAND_INVENTORY_PRICE = servicesSettings.getProperty("ExpandInventoryPrice", 1000);
        SERVICES_EXPAND_INVENTORY_ITEM = servicesSettings.getProperty("ExpandInventoryItem", 4037);
        SERVICES_EXPAND_INVENTORY_MAX = servicesSettings.getProperty("ExpandInventoryMax", 250);

        SERVICES_EXPAND_WAREHOUSE_ENABLED = servicesSettings.getProperty("ExpandWarehouseEnabled", false);
        SERVICES_EXPAND_WAREHOUSE_PRICE = servicesSettings.getProperty("ExpandWarehousePrice", 1000);
        SERVICES_EXPAND_WAREHOUSE_ITEM = servicesSettings.getProperty("ExpandWarehouseItem", 4037);

        SERVICES_EXPAND_CWH_ENABLED = servicesSettings.getProperty("ExpandCWHEnabled", false);
        SERVICES_EXPAND_CWH_PRICE = servicesSettings.getProperty("ExpandCWHPrice", 1000);
        SERVICES_EXPAND_CWH_ITEM = servicesSettings.getProperty("ExpandCWHItem", 4037);

        SERVICES_SELLPETS = servicesSettings.getProperty("SellPets", "");

        SERVICES_TRADE_TAX = servicesSettings.getProperty("TradeTax", 0.0);
        SERVICES_OFFSHORE_TRADE_TAX = servicesSettings.getProperty("OffshoreTradeTax", 0.0);
        SERVICES_TRADE_TAX_ONLY_OFFLINE = servicesSettings.getProperty("TradeTaxOnlyOffline", false);
        SERVICES_OFFSHORE_NO_CASTLE_TAX = servicesSettings.getProperty("NoCastleTaxInOffshore", false);
        SERVICES_TRADE_ONLY_FAR = servicesSettings.getProperty("TradeOnlyFar", false);
        SERVICES_TRADE_MIN_LEVEL = servicesSettings.getProperty("MinLevelForTrade", 0);
        SERVICES_TRADE_RADIUS = servicesSettings.getProperty("TradeRadius", 30);

        SERVICES_GIRAN_HARBOR_ENABLED = servicesSettings.getProperty("GiranHarborZone", false);
        SERVICES_PARNASSUS_ENABLED = servicesSettings.getProperty("ParnassusZone", false);
        SERVICES_PARNASSUS_NOTAX = servicesSettings.getProperty("ParnassusNoTax", false);
        SERVICES_PARNASSUS_PRICE = servicesSettings.getProperty("ParnassusPrice", 500000);

        SERVICES_ALLOW_LOTTERY = servicesSettings.getProperty("AllowLottery", false);
        SERVICES_LOTTERY_PRIZE = servicesSettings.getProperty("LotteryPrize", 50000);
        SERVICES_ALT_LOTTERY_PRICE = servicesSettings.getProperty("AltLotteryPrice", 2000);
        SERVICES_LOTTERY_TICKET_PRICE = servicesSettings.getProperty("LotteryTicketPrice", 2000);
        SERVICES_LOTTERY_5_NUMBER_RATE = servicesSettings.getProperty("Lottery5NumberRate", 0.6);
        SERVICES_LOTTERY_4_NUMBER_RATE = servicesSettings.getProperty("Lottery4NumberRate", 0.4);
        SERVICES_LOTTERY_3_NUMBER_RATE = servicesSettings.getProperty("Lottery3NumberRate", 0.2);
        SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE = servicesSettings.getProperty("Lottery2and1NumberPrize", 200);

        SERVICES_ALLOW_ROULETTE = servicesSettings.getProperty("AllowRoulette", false);
        SERVICES_ROULETTE_MIN_BET = servicesSettings.getProperty("RouletteMinBet", 1L);
        SERVICES_ROULETTE_MAX_BET = servicesSettings.getProperty("RouletteMaxBet", Long.MAX_VALUE);

        SERVICES_ENABLE_NO_CARRIER = servicesSettings.getProperty("EnableNoCarrier", false);

        SERVICES_PK_PVP_KILL_ENABLE = servicesSettings.getProperty("PkPvPKillEnable", false);
        SERVICES_PVP_KILL_REWARD_ITEM = servicesSettings.getProperty("PvPkillRewardItem", 4037);
        SERVICES_PVP_KILL_REWARD_COUNT = servicesSettings.getProperty("PvPKillRewardCount", 1L);
        SERVICES_PK_KILL_REWARD_ITEM = servicesSettings.getProperty("PkkillRewardItem", 4037);
        SERVICES_PK_KILL_REWARD_COUNT = servicesSettings.getProperty("PkKillRewardCount", 1L);
        SERVICES_PK_PVP_TIE_IF_SAME_IP = servicesSettings.getProperty("PkPvPTieifSameIP", true);


        ITEM_BROKER_ITEM_SEARCH = servicesSettings.getProperty("UseItemBrokerItemSearch", false);

        /* Password changer */
        PASSWORD_PAY_ID = servicesSettings.getProperty("ChangePasswordPayId", 0);
        PASSWORD_PAY_COUNT = servicesSettings.getProperty("ChangePassowrdPayCount", 0);
        APASSWD_TEMPLATE = servicesSettings.getProperty("ApasswdTemplate", "[A-Za-z0-9]{5,16}");

        ALLOW_EVENT_GATEKEEPER = servicesSettings.getProperty("AllowEventGatekeeper", false);
        SERVICES_LVL_ENABLED = servicesSettings.getProperty("LevelChangeEnabled", false);
        SERVICES_LVL_UP_MAX = servicesSettings.getProperty("LevelUPChangeMax", 85);
        SERVICES_LVL_UP_PRICE = servicesSettings.getProperty("LevelUPChangePrice", 1000);
        SERVICES_LVL_UP_ITEM = servicesSettings.getProperty("LevelUPChangeItem", 4037);
        SERVICES_LVL_DOWN_MAX = servicesSettings.getProperty("LevelDownChangeMax", 1);
        SERVICES_LVL_DOWN_PRICE = servicesSettings.getProperty("LevelDownChangePrice", 1000);
        SERVICES_LVL_DOWN_ITEM = servicesSettings.getProperty("LevelDownChangeItem", 4037);

        ALLOW_UPDATE_ANNOUNCER = servicesSettings.getProperty("AllowUpdateAnnouncer", false);

        SERVICES_HAIR_CHANGE_ITEM_ID = servicesSettings.getProperty("HairChangeItemID", 4037);
        SERVICES_HAIR_CHANGE_COUNT = servicesSettings.getProperty("HairChangeItemCount", 10);
    }

    private static void loadCommandssettings() {
        ExProperties CommandsSettings = load(COMMANDS_CONFIG_FILE);

        show_rates = CommandsSettings.getProperty("show_rates", false);
        NOT_USE_USER_VOICED = CommandsSettings.getProperty("NotUsePlayerVoiced", false);

        ENABLE_KM_ALL_TO_ME = CommandsSettings.getProperty("EnableKmAllToMe", false);


        /* .lock */
        FARM_TELEPORT_ITEM_ID = CommandsSettings.getProperty("FARM_TELEPORT_ITEM_ID", 57);
        PRICE_FARM = CommandsSettings.getProperty("PRICE_FARM", 57);
        FARM_X = CommandsSettings.getProperty("FARM_X", 57);
        FARM_Y = CommandsSettings.getProperty("FARM_Y", 57);
        FARM_Z = CommandsSettings.getProperty("FARM_Z", 57);

        FARM_HARD_TELEPORT_ITEM_ID = CommandsSettings.getProperty("FARM_HARD_TELEPORT_ITEM_ID", 57);
        PRICE_FARM_HARD = CommandsSettings.getProperty("PRICE_FARM_HARD", 57);
        FARM_HARD_X = CommandsSettings.getProperty("FARM_HARD_X", 57);
        FARM_HARD_Y = CommandsSettings.getProperty("FARM_HARD_Y", 57);
        FARM_HARD_Z = CommandsSettings.getProperty("FARM_HARD_Z", 57);

        FARM_LOW_TELEPORT_ITEM_ID = CommandsSettings.getProperty("FARM_LOW_TELEPORT_ITEM_ID", 57);
        PRICE_FARM_LOW = CommandsSettings.getProperty("PRICE_FARM_LOW", 57);
        FARM_LOW_X = CommandsSettings.getProperty("FARM_LOW_X", 57);
        FARM_LOW_Y = CommandsSettings.getProperty("FARM_LOW_Y", 57);
        FARM_LOW_Z = CommandsSettings.getProperty("FARM_LOW_Z", 57);

        PVP_X = CommandsSettings.getProperty("PVP_X", 0);
        PVP_Y = CommandsSettings.getProperty("PVP_Y", 0);
        PVP_Z = CommandsSettings.getProperty("PVP_Z", 0);
        PVP_TELEPORT_ITEM_ID = CommandsSettings.getProperty("PVP_TELEPORT_ITEM_ID", 57);
        PRICE_PVP = CommandsSettings.getProperty("PRICE_PVP", 57);

        ALT_SHOW_SERVER_TIME = CommandsSettings.getProperty("ShowServerTime", false);
    }

    private static void loadCommunityPvPboardsettings() {
        ExProperties CommunityPvPboardSettings = load(BOARD_MANAGER_CONFIG_FILE);

        COMMUNITYBOARD_ENABLED = CommunityPvPboardSettings.getProperty("AllowCommunityBoard", true);
        BBS_DEFAULT = CommunityPvPboardSettings.getProperty("BBSDefault", "_bbshome");
        BBS_HOME_DIR = CommunityPvPboardSettings.getProperty("BBSHomeDir", "scripts/services/community/");
        ALLOW_BBS_WAREHOUSE = CommunityPvPboardSettings.getProperty("AllowBBSWarehouse", true);
        BBS_WAREHOUSE_ALLOW_PK = CommunityPvPboardSettings.getProperty("BBSWarehouseAllowPK", false);
        ALLOW_DROP_CALCULATOR = CommunityPvPboardSettings.getProperty("AllowDropCalculator", true);

        ALLOW_SENDING_IMAGES = CommunityPvPboardSettings.getProperty("AllowSendingImages", true);
    }


    private static void loadCommunityPvPclasssettings() {
        ExProperties CommunityPvPClassSettings = load(CLASS_MASTER_CONFIG_FILE);

        CommunityPvPClassSettings.getProperty("AllowClassMasters", new ArrayList<>()).stream()
                .filter(id -> id != 0)
                .forEach(ALLOW_CLASS_MASTERS_LIST::add);

        CLASS_MASTERS_PRICE = CommunityPvPClassSettings.getProperty("ClassMastersPrice", "0,0,0");
        setClassMasterPriceList();
        CLASS_MASTERS_PRICE_ITEM = CommunityPvPClassSettings.getProperty("ClassMastersPriceItem", 57);

    }

    private static void setClassMasterPriceList() {
        if (CLASS_MASTERS_PRICE.length() >= 5) {
            int level = 1;
            for (String id : CLASS_MASTERS_PRICE.split(",")) {
                CLASS_MASTERS_PRICE_LIST[level] = toInt(id);
                level++;
            }
        }
    }

    private static void loadCommunityPvPshopsettings() {
        ExProperties CommunityPvPshopSettings = load(SHOP_MANAGER_CONFIG_FILE);

        BBS_PVP_ALLOW_BUY = CommunityPvPshopSettings.getProperty("CommunityShopEnable", false);
        BBS_PVP_ALLOW_SELL = CommunityPvPshopSettings.getProperty("CommunitySellEnable", false);
        BBS_PVP_ALLOW_AUGMENT = CommunityPvPshopSettings.getProperty("CommunityAugmentEnable", false);
    }

    private static void loadPvPSettings() {
        ExProperties pvpSettings = load(PVP_CONFIG_FILE);

        /* KARMA SYSTEM */
        KARMA_MIN_KARMA = pvpSettings.getProperty("MinKarma", 240);
        KARMA_SP_DIVIDER = pvpSettings.getProperty("SPDivider", 7);
        KARMA_LOST_BASE = pvpSettings.getProperty("BaseKarmaLost", 0);

        KARMA_DROP_GM = pvpSettings.getProperty("CanGMDropEquipment", false);
        KARMA_NEEDED_TO_DROP = pvpSettings.getProperty("KarmaNeededToDrop", true);
        DROP_ITEMS_ON_DIE = pvpSettings.getProperty("DropOnDie", false);
        DROP_ITEMS_AUGMENTED = pvpSettings.getProperty("DropAugmented", false);

        KARMA_DROP_ITEM_LIMIT = pvpSettings.getProperty("MaxItemsDroppable", 10);
        MIN_PK_TO_ITEMS_DROP = pvpSettings.getProperty("MinPKToDropItems", 5);

        KARMA_RANDOM_DROP_LOCATION_LIMIT = pvpSettings.getProperty("MaxDropThrowDistance", 70);

        KARMA_DROPCHANCE_BASE = pvpSettings.getProperty("ChanceOfPKDropBase", 20.);
        KARMA_DROPCHANCE_MOD = pvpSettings.getProperty("ChanceOfPKsDropMod", 1.);
        NORMAL_DROPCHANCE_BASE = pvpSettings.getProperty("ChanceOfNormalDropBase", 1.);
        DROPCHANCE_EQUIPPED_WEAPON = pvpSettings.getProperty("ChanceOfDropWeapon", 3);
        DROPCHANCE_EQUIPMENT = pvpSettings.getProperty("ChanceOfDropEquippment", 17);
        DROPCHANCE_ITEM = pvpSettings.getProperty("ChanceOfDropOther", 80);

        KARMA_LIST_NONDROPPABLE_ITEMS = new ArrayList<>(
                pvpSettings.getProperty("ListOfNonDroppableItems", List.of(
                        57, 1147, 425, 1146, 461, 10, 2368, 7, 6, 2370, 2369, 3500, 3501, 3502, 4422,
                        4423, 4424, 2375, 6648, 6649, 6650, 6842, 6834, 6835, 6836, 6837, 6838, 6839,
                        6840, 5575, 7694, 6841, 8181)));

        PVP_TIME = pvpSettings.getProperty("PvPTime", 120000);
    }


    private static void loadAISettings() {
        ExProperties aiSettings = load(AI_CONFIG_FILE);

        ALLOW_NPC_AIS = aiSettings.getProperty("AllowNpcAIs", true);
        AI_TASK_MANAGER_COUNT = aiSettings.getProperty("AiTaskManagers", 1);
        AI_TASK_ATTACK_DELAY = aiSettings.getProperty("AiTaskDelay", 1000);
        AI_TASK_ACTIVE_DELAY = aiSettings.getProperty("AiTaskActiveDelay", 1000);
        BLOCK_ACTIVE_TASKS = aiSettings.getProperty("BlockActiveTasks", false);
        ALWAYS_TELEPORT_HOME = aiSettings.getProperty("AlwaysTeleportHome", false);

        RND_WALK = aiSettings.getProperty("RndWalk", true);
        RND_WALK_RATE = aiSettings.getProperty("RndWalkRate", 1);
        RND_ANIMATION_RATE = aiSettings.getProperty("RndAnimationRate", 2);

        AGGRO_CHECK_INTERVAL = aiSettings.getProperty("AggroCheckInterval", 400);
        NONAGGRO_TIME_ONTELEPORT = aiSettings.getProperty("NonAggroTimeOnTeleport", 15000);
        MAX_DRIFT_RANGE = aiSettings.getProperty("MaxDriftRange", 100);
        MAX_PURSUE_RANGE = aiSettings.getProperty("MaxPursueRange", 4000);
        MAX_PURSUE_UNDERGROUND_RANGE = aiSettings.getProperty("MaxPursueUndergoundRange", 2000);
        MAX_PURSUE_RANGE_RAID = aiSettings.getProperty("MaxPursueRangeRaid", 5000);
    }

    private static void loadGeodataSettings() {
        ExProperties geodataSettings = load(GEODATA_CONFIG_FILE);

        GEO_X_FIRST = geodataSettings.getProperty("GeoFirstX", 11);
        GEO_Y_FIRST = geodataSettings.getProperty("GeoFirstY", 10);
        GEO_X_LAST = geodataSettings.getProperty("GeoLastX", 26);
        GEO_Y_LAST = geodataSettings.getProperty("GeoLastY", 26);

        GEOFILES_PATTERN = geodataSettings.getProperty("GeoFilesPattern", "(\\d{2}_\\d{2})\\.l2j");
        ALLOW_GEODATA = geodataSettings.getProperty("AllowGeodata", true);
        ALLOW_FALL_FROM_WALLS = geodataSettings.getProperty("AllowFallFromWalls", false);
        COMPACT_GEO = geodataSettings.getProperty("CompactGeoData", false);
        CLIENT_Z_SHIFT = geodataSettings.getProperty("ClientZShift", 16);
        PATHFIND_BOOST = geodataSettings.getProperty("PathFindBoost", 2);
        PATHFIND_DIAGONAL = geodataSettings.getProperty("PathFindDiagonal", true);
        PATH_CLEAN = geodataSettings.getProperty("PathClean", true);
        PATHFIND_MAX_Z_DIFF = geodataSettings.getProperty("PathFindMaxZDiff", 32);
        MAX_Z_DIFF = geodataSettings.getProperty("MaxZDiff", 64);
        MIN_LAYER_HEIGHT = geodataSettings.getProperty("MinLayerHeight", 64);
        PATHFIND_MAX_TIME = geodataSettings.getProperty("PathFindMaxTime", 10000000);
        PATHFIND_BUFFERS = geodataSettings.getProperty("PathFindBuffers", "8x96;8x128;8x160;8x192;4x224;4x256;4x288;2x320;2x384;2x352;1x512");
    }

    private static void loadEventsSettings() {
        ExProperties eventSettings = load(EVENTS_CONFIG_FILE);

        EVENT_CofferOfShadowsPriceRate = eventSettings.getProperty("CofferOfShadowsPriceRate", 1.);
        EVENT_CofferOfShadowsRewardRate = eventSettings.getProperty("CofferOfShadowsRewardRate", 1.);

        EVENT_TFH_POLLEN_CHANCE = eventSettings.getProperty("TFH_POLLEN_CHANCE", 5.);

        EVENT_GLITTMEDAL_NORMAL_CHANCE = eventSettings.getProperty("MEDAL_CHANCE", 10.);
        EVENT_GLITTMEDAL_GLIT_CHANCE = eventSettings.getProperty("GLITTMEDAL_CHANCE", 0.1);

        EVENT_L2DAY_LETTER_CHANCE = eventSettings.getProperty("L2DAY_LETTER_CHANCE", 1.);
        EVENT_CHANGE_OF_HEART_CHANCE = eventSettings.getProperty("EVENT_CHANGE_OF_HEART_CHANCE", 5.);

        EVENT_APIL_FOOLS_DROP_CHANCE = eventSettings.getProperty("AprilFollsDropChance", 50.);

        EVENT_BOUNTY_HUNTERS_ENABLED = eventSettings.getProperty("BountyHuntersEnabled", true);

        EVENT_SAVING_SNOWMAN_LOTERY_PRICE = eventSettings.getProperty("SavingSnowmanLoteryPrice", 50000);
        EVENT_SAVING_SNOWMAN_REWARDER_CHANCE = eventSettings.getProperty("SavingSnowmanRewarderChance", 2);

        EVENT_TRICK_OF_TRANS_CHANCE = eventSettings.getProperty("TRICK_OF_TRANS_CHANCE", 10.);

        EVENT_MARCH8_DROP_CHANCE = eventSettings.getProperty("March8DropChance", 10.);
        EVENT_MARCH8_PRICE_RATE = eventSettings.getProperty("March8PriceRate", 1.);

        ENCHANT_CHANCE_MASTER_YOGI_STAFF = eventSettings.getProperty("MasterYogiEnchantChance", 66);
        ENCHANT_MAX_MASTER_YOGI_STAFF = eventSettings.getProperty("MasterYogiEnchantMaxWeapon", 28);
        SAFE_ENCHANT_MASTER_YOGI_STAFF = eventSettings.getProperty("MasterYogiSafeEnchant", 3);

        AllowCustomDropItems = eventSettings.getProperty("AllowCustomDropItems", true);
        CDItemsAllowMinMaxPlayerLvl = eventSettings.getProperty("CDItemsAllowMinMaxPlayerLvl", false);
        CDItemsAllowMinMaxMobLvl = eventSettings.getProperty("CDItemsAllowMinMaxMobLvl", false);
        CDItemsAllowOnlyRbDrops = eventSettings.getProperty("CDItemsAllowOnlyRbDrops", false);
        CDItemsId = eventSettings.getProperty("CDItemsId", List.of(57));
        CDItemsCountDropMin = eventSettings.getProperty("CDItemsCountDropMin", List.of(1));
        CDItemsCountDropMax = eventSettings.getProperty("CDItemsCountDropMax", List.of(1));
        CustomDropItemsChance = eventSettings.getProperty("CustomDropItemsChance", List.of(1));
        CDItemsMinPlayerLvl = eventSettings.getProperty("CDItemsMinPlayerLvl", 20);
        CDItemsMaxPlayerLvl = eventSettings.getProperty("CDItemsMaxPlayerLvl", 85);
        CDItemsMinMobLvl = eventSettings.getProperty("CDItemsMinMobLvl", 20);
        CDItemsMaxMobLvl = eventSettings.getProperty("CDItemsMaxMobLvl", 80);
        RANDOM_BOSS_ENABLE = eventSettings.getProperty("EnableRandomBossEvent", false);
        RANDOM_BOSS_ID = eventSettings.getProperty("RandomBossID", 37000);
        RANDOM_BOSS_TIME = eventSettings.getProperty("RandomBossTime", 60);
        RANDOM_BOSS_X = eventSettings.getProperty("RandomBossSpawnX", 20168);
        RANDOM_BOSS_Y = eventSettings.getProperty("RandomBossSpawnY", -15336);
        RANDOM_BOSS_Z = eventSettings.getProperty("RandomBossSpawnZ", -3109);

        EVENT_SANTA_ALLOW = eventSettings.getProperty("AllowSantaEvent", false);
        EVENT_SANTA_CHANCE_MULT = eventSettings.getProperty("SantaItemsChanceMult", 1.0);
    }

    private static void loadOlympiadSettings() {
        ExProperties olympSettings = load(OLYMPIAD);

        ENABLE_OLYMPIAD = olympSettings.getProperty("EnableOlympiad", true);
        ENABLE_OLYMPIAD_SPECTATING = olympSettings.getProperty("EnableOlympiadSpectating", true);
        ALT_OLY_START_TIME = olympSettings.getProperty("AltOlyStartTime", 18);
        ALT_OLY_MIN = olympSettings.getProperty("AltOlyMin", 0);
        ALT_OLY_CPERIOD = olympSettings.getProperty("AltOlyCPeriod", 21600000);
        OLYMPIAD_SHOUT_ONCE_PER_START = olympSettings.getProperty("OlyManagerShoutJustOneMessage", false);
        ALT_OLY_WPERIOD = olympSettings.getProperty("AltOlyWPeriod", 604800000);
        ALT_OLY_VPERIOD = olympSettings.getProperty("AltOlyVPeriod", 43200000);
        for (String prop : olympSettings.getProperty("AltOlyDateEnd", "1,15").split(",")) {
            ALT_OLY_DATE_END.add(toInt(prop));
        }
        CLASS_GAME_MIN = olympSettings.getProperty("ClassGameMin", 5);
        NONCLASS_GAME_MIN = olympSettings.getProperty("NonClassGameMin", 9);
        TEAM_GAME_MIN = olympSettings.getProperty("TeamGameMin", 4);

        GAME_MAX_LIMIT = olympSettings.getProperty("GameMaxLimit", 70);
        GAME_CLASSES_COUNT_LIMIT = olympSettings.getProperty("GameClassesCountLimit", 30);
        GAME_NOCLASSES_COUNT_LIMIT = olympSettings.getProperty("GameNoClassesCountLimit", 60);
        GAME_TEAM_COUNT_LIMIT = olympSettings.getProperty("GameTeamCountLimit", 10);

        ALT_OLY_BATTLE_REWARD_ITEM = olympSettings.getProperty("AltOlyBattleRewItem", 13722);
        ALT_OLY_CLASSED_RITEM_C = olympSettings.getProperty("AltOlyClassedRewItemCount", 50);
        ALT_OLY_NONCLASSED_RITEM_C = olympSettings.getProperty("AltOlyNonClassedRewItemCount", 40);
        ALT_OLY_TEAM_RITEM_C = olympSettings.getProperty("AltOlyTeamRewItemCount", 50);
        ALT_OLY_COMP_RITEM = olympSettings.getProperty("AltOlyCompRewItem", 13722);
        ALT_OLY_GP_PER_POINT = olympSettings.getProperty("AltOlyGPPerPoint", 1000);
        ALT_OLY_HERO_POINTS = olympSettings.getProperty("AltOlyHeroPoints", 180);
        ALT_OLY_RANK1_POINTS = olympSettings.getProperty("AltOlyRank1Points", 120);
        ALT_OLY_RANK2_POINTS = olympSettings.getProperty("AltOlyRank2Points", 80);
        ALT_OLY_RANK3_POINTS = olympSettings.getProperty("AltOlyRank3Points", 55);
        ALT_OLY_RANK4_POINTS = olympSettings.getProperty("AltOlyRank4Points", 35);
        ALT_OLY_RANK5_POINTS = olympSettings.getProperty("AltOlyRank5Points", 20);
        OLYMPIAD_STADIAS_COUNT = olympSettings.getProperty("OlympiadStadiasCount", 160);
        OLYMPIAD_BATTLES_FOR_REWARD = olympSettings.getProperty("OlympiadBattlesForReward", 15);
        OLYMPIAD_POINTS_DEFAULT = olympSettings.getProperty("OlympiadPointsDefault", 50);
        OLYMPIAD_POINTS_WEEKLY = olympSettings.getProperty("OlympiadPointsWeekly", 10);
        OLYMPIAD_OLDSTYLE_STAT = olympSettings.getProperty("OlympiadOldStyleStat", false);
        ALT_OLY_WAIT_TIME = olympSettings.getProperty("AltOlyWaitTime", 120);
        ALT_OLY_PORT_BACK_TIME = olympSettings.getProperty("AltOlyPortBackTime", 20);

    }

    private static void loadTalkGuardConfig() {
        ExProperties TalkGuardSetting = load(TALKING_GUARD_CONFIG_FILE);

        TalkGuardChance = TalkGuardSetting.getProperty("TalkGuardChance", 4037);
        TalkNormalChance = TalkGuardSetting.getProperty("TalkNormalChance", 4037);
        TalkNormalPeriod = TalkGuardSetting.getProperty("TalkNormalPeriod", 4037);
        TalkAggroPeriod = TalkGuardSetting.getProperty("TalkAggroPeriod", 4037);
    }

    private static void loadBufferConfig() {
        // Buffer
    }

    private static void loadBuffStoreConfig() {
        ExProperties buffStoreConfig = load(BUFF_STORE_CONFIG_FILE);

        // Buff Store
        BUFF_STORE_ENABLED = buffStoreConfig.getProperty("BuffStoreEnabled", false);
        BUFF_STORE_MP_ENABLED = buffStoreConfig.getProperty("BuffStoreMpEnabled", true);
        BUFF_STORE_MP_CONSUME_MULTIPLIER = buffStoreConfig.getProperty("BuffStoreMpConsumeMultiplier", 1.0f);

        BUFF_STORE_NAME_COLOR = Integer.decode("0x" + buffStoreConfig.getProperty("BuffStoreNameColor", "808080"));
        BUFF_STORE_TITLE_COLOR = Integer.decode("0x" + buffStoreConfig.getProperty("BuffStoreTitleColor", "808080"));
        BUFF_STORE_OFFLINE_NAME_COLOR = Integer.decode("0x" + buffStoreConfig.getProperty("BuffStoreOfflineNameColor", "808080"));

        final String[] classes = buffStoreConfig.getProperty("BuffStoreAllowedClassList", "").split(",");
        BUFF_STORE_ALLOWED_CLASS_LIST = new ArrayList<>();
        if (classes.length > 0) {
            for (String classId : classes) {
                BUFF_STORE_ALLOWED_CLASS_LIST.add(toInt(classId));
            }
        }

        final String[] skills = buffStoreConfig.getProperty("BuffStoreForbiddenSkillList", "").split(",");
        BUFF_STORE_FORBIDDEN_SKILL_LIST = new ArrayList<>();
        if (skills.length > 0) {
            for (String skillId : skills) {
                BUFF_STORE_FORBIDDEN_SKILL_LIST.add(toInt(skillId));
            }
        }
    }

    private static void loadForgeSettings() {
        ExProperties forge = load(FORGE_CONFIG_FILE);
        BBS_FORGE_ENABLED = forge.getProperty("Allow", false);
        BBS_FORGE_ENCHANT_ITEM = forge.getProperty("Item", 4356);
        BBS_FORGE_FOUNDATION_ITEM = forge.getProperty("FoundationItem", 37000);
        BBS_FORGE_FOUNDATION_PRICE_ARMOR = forge.getProperty("FoundationPriceArmor", List.of(1, 1, 1, 1, 1, 2, 5, 10));
        BBS_FORGE_FOUNDATION_PRICE_WEAPON = forge.getProperty("FoundationPriceWeapon", List.of(1, 1, 1, 1, 1, 2, 5, 10));
        BBS_FORGE_FOUNDATION_PRICE_JEWEL = forge.getProperty("FoundationPriceJewel", List.of(1, 1, 1, 1, 1, 2, 5, 10));
        BBS_FORGE_ENCHANT_MAX = forge.getProperty("MaxEnchant", List.of(25));
        BBS_FORGE_WEAPON_ENCHANT_LVL = forge.getProperty("WValue", List.of(5));
        BBS_FORGE_ARMOR_ENCHANT_LVL = forge.getProperty("AValue", List.of(5));
        BBS_FORGE_JEWELS_ENCHANT_LVL = forge.getProperty("JValue", List.of(5));
        BBS_FORGE_ENCHANT_PRICE_WEAPON = forge.getProperty("WPrice", List.of(5));
        BBS_FORGE_ENCHANT_PRICE_ARMOR = forge.getProperty("APrice", List.of(5));
        BBS_FORGE_ENCHANT_PRICE_JEWELS = forge.getProperty("JPrice", List.of(5));


        BBS_FORGE_ATRIBUTE_LVL_WEAPON = forge.getProperty("AtributeWeaponValue", List.of(25));
        BBS_FORGE_ATRIBUTE_PRICE_WEAPON = forge.getProperty("PriceForAtributeWeapon", List.of(25));
        BBS_FORGE_ATRIBUTE_LVL_ARMOR = forge.getProperty("AtributeArmorValue", List.of(25));
        BBS_FORGE_ATRIBUTE_PRICE_ARMOR = forge.getProperty("PriceForAtributeArmor", List.of(25));
        BBS_FORGE_WEAPON_ATTRIBUTE_MAX = forge.getProperty("MaxWAttribute", 25);
        BBS_FORGE_ARMOR_ATTRIBUTE_MAX = forge.getProperty("MaxAAttribute", 25);
    }

    public static void load() {
        loadServerConfig();
        loadTelnetConfig();
        loadResidenceConfig();
        loadOtherConfig();
        loadSpoilConfig();
        loadAltSettings();
        loadServicesSettings();
        loadPvPSettings();
        loadAISettings();
        loadGeodataSettings();
        loadEventsSettings();
        loadOlympiadSettings();
        loadDevelopSettings();
        loadExtSettings();
        loadRatesConfig();
        loadItemsUseConfig();
        loadSchemeBuffer();
        loadChatConfig();
        loadNpcConfig();
        loadBossConfig();
        loadEpicBossConfig();
        loadInstancesConfig();
        loadItemsSettings();
        loadGMAccess();
        loadForgeSettings();
        loadPvPmodConfig();
        loadHitmanSettings();
        loadVIKTORINAsettings();
        if (ALLOW_ADDONS_CONFIG) {
            AddonsConfig.load();
        }
        // Load Community Board
        loadCommunityPvPboardsettings();
        loadCommunityPvPclasssettings();
        loadCommunityPvPshopsettings();
        loadCommandssettings();
        loadBufferConfig();
        loadl2fConfig();
        loadTalkGuardConfig();
        // Ady
        //loadRaidEventConfig();
        loadBuffStoreConfig();
    }


    public static void loadGMAccess() {
        gmlist.clear();
        loadGMAccess(GM_PERSONAL_ACCESS_FILE);
        Path dir = GM_ACCESS_FILES_DIR;
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            _log.info("Dir " + dir.toAbsolutePath() + " not exists.");
            return;
        }
        for (Path f : FileUtils.getAllFiles(dir, false, ".xml")) {
            loadGMAccess(f);
        }
    }

    private static void loadGMAccess(Path file) {
        try {
            Field fld;
            // File file = new File(filename);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);
            Document doc = factory.newDocumentBuilder().parse(file.toFile());

            for (Node z = doc.getFirstChild(); z != null; z = z.getNextSibling()) {
                for (Node n = z.getFirstChild(); n != null; n = n.getNextSibling()) {
                    if (!n.getNodeName().equalsIgnoreCase("char")) {
                        continue;
                    }

                    PlayerAccess pa = new PlayerAccess();
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        Class<?> cls = pa.getClass();
                        String node = d.getNodeName();

                        if (node.equalsIgnoreCase("#text")) {
                            continue;
                        }
                        try {
                            fld = cls.getField(node);
                        } catch (NoSuchFieldException e) {
                            _log.info("Not found desclarate ACCESS name: " + node + " in XML Player access Object");
                            continue;
                        }

                        if (fld.getType().getName().equalsIgnoreCase("boolean")) {
                            fld.setBoolean(pa, Boolean.parseBoolean(d.getAttributes().getNamedItem("set").getNodeValue()));
                        } else if (fld.getType().getName().equalsIgnoreCase("int")) {
                            fld.setInt(pa, Integer.valueOf(d.getAttributes().getNamedItem("set").getNodeValue()));
                        }
                    }
                    gmlist.put(pa.PlayerID, pa);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ExProperties load(String filename) {
        return load(Paths.get(filename));
    }

    public static ExProperties load(Path file) {
        ExProperties result = new ExProperties();

        result.load(file);

        return result;
    }

}