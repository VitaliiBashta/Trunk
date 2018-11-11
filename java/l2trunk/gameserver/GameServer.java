package l2trunk.gameserver;

import Elemental.datatables.OfflineBuffersTable;
import l2trunk.commons.listener.Listener;
import l2trunk.commons.listener.ListenerList;
import l2trunk.commons.net.AdvIP;
import l2trunk.commons.net.nio.impl.SelectorThread;
import l2trunk.gameserver.cache.CrestCache;
import l2trunk.gameserver.cache.ImagesCache;
import l2trunk.gameserver.dao.CharacterDAO;
import l2trunk.gameserver.dao.ItemsDAO;
import l2trunk.gameserver.data.BoatHolder;
import l2trunk.gameserver.data.xml.Parsers;
import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.data.xml.holder.StaticObjectHolder;
import l2trunk.gameserver.data.xml.parser.ClassesStatsBalancerParser;
import l2trunk.gameserver.data.xml.parser.NpcStatsBalancerParser;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.database.LoginDatabaseFactory;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.handler.admincommands.AdminCommandHandler;
import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.handler.usercommands.UserCommandHandler;
import l2trunk.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.instancemanager.*;
import l2trunk.gameserver.instancemanager.games.FishingChampionShipManager;
import l2trunk.gameserver.instancemanager.games.LotteryManager;
import l2trunk.gameserver.instancemanager.games.MiniGameScoreManager;
import l2trunk.gameserver.instancemanager.itemauction.ItemAuctionManager;
import l2trunk.gameserver.instancemanager.naia.NaiaCoreManager;
import l2trunk.gameserver.instancemanager.naia.NaiaTowerManager;
import l2trunk.gameserver.listener.GameListener;
import l2trunk.gameserver.listener.game.OnShutdownListener;
import l2trunk.gameserver.listener.game.OnStartListener;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.entity.*;
import l2trunk.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2trunk.gameserver.model.entity.achievements.AchievementNotification;
import l2trunk.gameserver.model.entity.achievements.Achievements;
import l2trunk.gameserver.model.entity.auction.AuctionManager;
import l2trunk.gameserver.model.entity.events.fightclubmanager.FightClubEventManager;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.network.GameClient;
import l2trunk.gameserver.network.GamePacketHandler;
import l2trunk.gameserver.network.loginservercon.AuthServerCommunication;
import l2trunk.gameserver.network.telnet.TelnetServer;
import l2trunk.gameserver.scripts.Scripts;
import l2trunk.gameserver.tables.*;
import l2trunk.gameserver.taskmanager.AutoImageSenderManager;
import l2trunk.gameserver.taskmanager.ItemsAutoDestroy;
import l2trunk.gameserver.taskmanager.TaskManager;
import l2trunk.gameserver.taskmanager.tasks.RestoreOfflineTraders;
import l2trunk.gameserver.utils.Strings;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Date;

public class GameServer {
    public static final int AUTH_SERVER_PROTOCOL = 2;
    private static final Logger _log = LoggerFactory.getLogger(GameServer.class);
    public static Date server_started;
    private static GameServer _instance;
    private final SelectorThread<GameClient> _selectorThreads[];
    private final GameServerListenerList _listeners;
    private final int _serverStarted;
    private TelnetServer statusServer;

    private GameServer() throws Exception {
        int update = 993;
        _instance = this;
        _serverStarted = time();
        _listeners = new GameServerListenerList();

        new File(Config.DATAPACK_ROOT + "/log/").mkdir();

        _log.info("=================================================");
        _log.info("Copyright: ............... " + "L2Mythras.EU");
        _log.info("Update: .................. " + update + " contact L2Mythras.eu Team");
        _log.info("Chronicle: ............... " + "High Five Part 5");
        _log.info("=================================================");

        // Initialize config
        Config.load();

        // Check binding address
        checkFreePorts();



        // Initialize database
        System.out.println("Server is Loading on IP " + Config.EXTERNAL_HOSTNAME + "");

//        Class.forName(Config.DATABASE_DRIVER).newInstance();
        DatabaseFactory.getInstance().getConnection().close();
        LoginDatabaseFactory.getInstance().getConnection().close();
        _log.info("=======[Loading Protection Configuration]========");
        IdFactory idFactory = IdFactory.getInstance();
        if (!idFactory.isInitialized()) {
            _log.error("Could not read object IDs from DB. Please Check Your Data.", new Exception("Could not initialize the ID factory"));
            throw new Exception("Could not initialize the ID factory");
        }

        CacheManager.getInstance();

        ThreadPoolManager.getInstance();
        _log.info("===============[Loading Scripts]==================");
        Scripts.getInstance();
        BalancerConfig.LoadConfig();
        GeoEngine.load();
        Strings.reload();
        GameTimeController.getInstance();
        printSection("Lineage World");
        World.init();
        printSection("");
        Parsers.parseAll();
        ItemsDAO.getInstance();
        printSection("Clan Crests");
        CrestCache.getInstance();
        // Alexander - Load all the information for the Server Ranking
        //_log.info("===================[Ranking]=======================");
        //ServerRanking.getInstance();
        //CharacterMonthlyRanking.getInstance();
        _log.info("===============[Loading Images]==================");
        ImagesCache.getInstance();
        printSection("");
        CharacterDAO.getInstance();
        ClanTable.getInstance();
        printSection("Fish Table");
        FishTable.getInstance();
        printSection("Skills");
        SkillTreeTable.getInstance();
        printSection("Augmentation Data");
        AugmentationData.getInstance();
        EnchantHPBonusTable.getInstance();
        printSection("Level Up Table");
        LevelUpTable.getInstance();
        PetSkillsTable.getInstance();
        printSection("Auctioneer");
        ItemAuctionManager.getInstance();
        _log.info("===============[Loading RAIDBOSS]==================");
//        Scripts.getInstance().init();
        SpawnManager.getInstance().spawnAll();
        printSection("Boats");
        BoatHolder.getInstance().spawnAll();
        StaticObjectHolder.getInstance().spawnAll();
        _log.info("===============[Spawn Manager]==================");
        RaidBossSpawnManager.getInstance();
        printSection("Dimensional Rift");
        DimensionalRiftManager.getInstance();
        Announcements.getInstance();
        LotteryManager.getInstance();
        PlayerMessageStack.getInstance();
        if (Config.AUTODESTROY_ITEM_AFTER > 0) {
            ItemsAutoDestroy.getInstance();
        }
        MonsterRace.getInstance();
        printSection("Seven Signs");
        SevenSigns.getInstance();
        SevenSignsFestival.getInstance();
        SevenSigns.getInstance().updateFestivalScore();
        AutoSpawnManager.getInstance();
        SevenSigns.getInstance().spawnSevenSignsNPC();
        _log.info("===================[Loading Olympiad System]=======================");
        if (Config.ENABLE_OLYMPIAD) {
            Olympiad.load();
            Hero.getInstance();
        }
        _log.info("===================[Olympiad System Loaded]=======================");
        PetitionManager.getInstance();
        CursedWeaponsManager.getInstance();
        if (!Config.ALLOW_WEDDING) {
            CoupleManager.getInstance();
            _log.info("CoupleManager initialized");
        }
        ItemHandler.getInstance();
        _log.info("======================[Loading BALANCER]==========================");
        NpcStatsBalancerParser.getInstance();
        ClassesStatsBalancerParser.getInstance();
        _log.info("======================[Loading BALANCER]==========================");
        printSection("Admin Commands");
        AdminCommandHandler.getInstance().log();
        printSection("Players Commands");
        UserCommandHandler.getInstance().log();
        VoicedCommandHandler.getInstance().log();
        TaskManager.getInstance();
        _log.info("======================[Loading Castels & Clan Halls]==========================");
        ResidenceHolder.getInstance().callInit();
        EventHolder.getInstance().callInit();
        CastleManorManager.getInstance();
        printSection("");
        Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
        printSection("Auto Cleaner");
        _log.info("IdFactory: Free ObjectID's remaining: " + IdFactory.getInstance().size());
        printSection("");
        CoupleManager.getInstance();
        if (Config.ALT_FISH_CHAMPIONSHIP_ENABLED) {
            FishingChampionShipManager.getInstance();
        }
        printSection("Hellbound");
        HellboundManager.getInstance();

        NaiaTowerManager.getInstance();
        NaiaCoreManager.getInstance();
        printSection("");
        SoDManager.getInstance();
        SoIManager.getInstance();
        BloodAltarManager.getInstance();
        AuctionManager.getInstance();
        if (Config.ALLOW_DROP_CALCULATOR) {
            _log.info("Preparing Drop Calculator");
            ItemHolder.getInstance().getDroppableTemplates();
        }
        MiniGameScoreManager.getInstance();
        L2TopManager.getInstance();
        //AutoRaidEventManager.getInstance();

        if (Config.BUFF_STORE_ENABLED) {
            printSection("Offline Buffers");
            OfflineBuffersTable.getInstance().restoreOfflineBuffers();
        }
        Shutdown.getInstance().schedule(Config.RESTART_AT_TIME, 2);
        printSection("");
        _log.info(">>>>>>>>>> GameServer Started <<<<<<<<<");
        _log.info("Maximum Numbers of Connected Players: " + Config.MAXIMUM_ONLINE_USERS);
        //Protection.Init();
        CharacterDAO.getInstance().markTooOldChars();
        _log.info("===============[Protection Database]==================");
        CharacterDAO.getInstance().checkCharactersToDelete();
        printSection("");
        FightClubEventManager.getInstance();
        GamePacketHandler gph = new GamePacketHandler();
        InetAddress serverAddr = Config.GAMESERVER_HOSTNAME.equalsIgnoreCase("*") ? null : InetAddress.getByName(Config.GAMESERVER_HOSTNAME);
        int arrayLen = Config.GAMEIPS.isEmpty() ? Config.PORTS_GAME.length : Config.PORTS_GAME.length + Config.GAMEIPS.size();
        _selectorThreads = new SelectorThread[arrayLen];
        for (int i = 0; i < Config.PORTS_GAME.length; i++) {
            try {
                _selectorThreads[i] = new SelectorThread<>(Config.SELECTOR_CONFIG, gph, gph, gph, null);
                _selectorThreads[i].openServerSocket(serverAddr, Config.PORTS_GAME[i]);
                _selectorThreads[i].start();
            } catch (IOException ioe) {
                _log.error("Cannot bind address: " + serverAddr + ":" + Config.PORTS_GAME[i], ioe);
            }
        }
        if (!Config.GAMEIPS.isEmpty()) // AdvIP support. server.ini ports are ignored and accepted only IPs and ports from advipsystem.ini
        {
            int i = Config.PORTS_GAME.length; // Start from the last spot.
            for (AdvIP advip : Config.GAMEIPS) {
                try {
                    _selectorThreads[i] = new SelectorThread<>(Config.SELECTOR_CONFIG, gph, gph, gph, null);
                    _selectorThreads[i].openServerSocket(InetAddress.getByName(advip.channelAdress), advip.channelPort);
                    _selectorThreads[i++].start();
                    _log.info("AdvIP: Channel " + advip.channelId + " is open on: " + advip.channelAdress + ":" + advip.channelPort);
                } catch (IOException ioe) {
                    _log.error("Cannot bind address: " + advip.channelAdress + ":" + advip.channelPort, ioe);
                }
            }
        }

        if (Config.SERVICES_OFFLINE_TRADE_RESTORE_AFTER_RESTART) {
            ThreadPoolManager.getInstance().schedule(new RestoreOfflineTraders(), 100000L);
        }
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new AutoAnnounce(), 60000, 60000);
        AutoImageSenderManager.startSendingImages();

        if (Config.ENABLE_ACHIEVEMENTS) {
            Achievements.getInstance();
            AchievementNotification.getInstance();
        }

        getListeners().onStart();
        if (Config.IS_TELNET_ENABLED) {
            statusServer = new TelnetServer();
        } else {
            _log.info("Telnet server is currently disabled.");
        }

        AuthServerCommunication.getInstance().start();
        server_started = new Date();
    }

    private static void printSection(String s) {
        if (s.isEmpty()) {
            s = "==============================================================================";
        } else {
            s = "=[ " + s + " ]";
            while (s.length() < 78) {
                s = "-" + s;
            }
        }
        _log.info(s);
    }

    public static GameServer getInstance() {
        return _instance;
    }

    private static void checkFreePorts() {
        boolean binded = false;
        while (!binded) {
            for (int PORT_GAME : Config.PORTS_GAME) {
                try {
                    ServerSocket ss;
                    if (Config.GAMESERVER_HOSTNAME.equalsIgnoreCase("*")) {
                        ss = new ServerSocket(PORT_GAME);
                    } else {
                        ss = new ServerSocket(PORT_GAME, 50, InetAddress.getByName(Config.GAMESERVER_HOSTNAME));
                    }
                    ss.close();
                    binded = true;
                } catch (Exception e) {
                    _log.warn("Port " + PORT_GAME + " is allready binded. Please free it and restart server.");
                    binded = false;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e2) {
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new GameServer();
    }

    public SelectorThread<GameClient>[] getSelectorThreads() {
        return _selectorThreads;
    }

    private int time() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public int uptime() {
        return time() - _serverStarted;
    }

    private GameServerListenerList getListeners() {
        return _listeners;
    }

    public <T extends GameListener> boolean addListener(T listener) {
        return _listeners.add(listener);
    }

    public <T extends GameListener> boolean removeListener(T listener) {
        return _listeners.remove(listener);
    }

    public TelnetServer getStatusServer() {
        return statusServer;
    }

    public class GameServerListenerList extends ListenerList<GameServer> {
        void onStart() {
            for (Listener<GameServer> listener : getListeners()) {
                if (OnStartListener.class.isInstance(listener)) {
                    ((OnStartListener) listener).onStart();
                }
            }
        }

        public void onShutdown() {
            for (Listener<GameServer> listener : getListeners()) {
                if (OnShutdownListener.class.isInstance(listener)) {
                    ((OnShutdownListener) listener).onShutdown();
                }
            }
        }
    }
}