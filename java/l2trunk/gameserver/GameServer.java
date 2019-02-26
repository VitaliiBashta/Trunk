package l2trunk.gameserver;

import Elemental.datatables.OfflineBuffersTable;
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
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.database.LoginDatabaseFactory;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.handler.admincommands.AdminCommandHandler;
import l2trunk.gameserver.handler.usercommands.UserCommandHandler;
import l2trunk.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2trunk.gameserver.hibenate.HibernateUtil;
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
import l2trunk.gameserver.model.entity.Hero;
import l2trunk.gameserver.model.entity.SevenSigns;
import l2trunk.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2trunk.gameserver.model.entity.achievements.AchievementNotification;
import l2trunk.gameserver.model.entity.achievements.Achievements;
import l2trunk.gameserver.model.entity.auction.AuctionManager;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.network.GameClient;
import l2trunk.gameserver.network.GamePacketHandler;
import l2trunk.gameserver.network.loginservercon.AuthServerCommunication;
import l2trunk.gameserver.scripts.Scripts;
import l2trunk.gameserver.tables.*;
import l2trunk.gameserver.taskmanager.AutoImageSenderManager;
import l2trunk.gameserver.taskmanager.ItemsAutoDestroy;
import l2trunk.gameserver.taskmanager.TaskManager;
import l2trunk.gameserver.utils.Strings;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class GameServer {
    public static final int AUTH_SERVER_PROTOCOL = 2;
    private static final Logger LOG = LoggerFactory.getLogger(GameServer.class);
    public static Date server_started;
    private static GameServer _instance;
    private final List<SelectorThread<GameClient>> _selectorThreads;
    private final GameServerListenerList _listeners;
    private final int _serverStarted;

    private GameServer() throws Exception {
        int update = 993;
        _instance = this;
        _serverStarted = time();
        _listeners = new GameServerListenerList();

        Files.createDirectories(Config.DATAPACK_ROOT.resolve("log/"));

        LOG.info("=================================================");
        LOG.info("Copyright: ............... " + "L2Mythras.EU");
        LOG.info("Update: .................. " + update + " contact L2Mythras.eu Team");
        LOG.info("Chronicle: ............... " + "High Five Part 5");
        LOG.info("=================================================");

        // Initialize config
        Config.load();

        // Check binding address
        checkFreePorts();


        // Initialize database
        System.out.println("Server is Loading on IP " + Config.EXTERNAL_HOSTNAME + "");

        DatabaseFactory.getInstance().getConnection().close();
        LoginDatabaseFactory.getInstance().getConnection().close();
        ThreadPoolManager.INSTANCE.init();
        LOG.info("=======[Loading Protection Configuration]========");

        IdFactory idFactory = IdFactory.getInstance();
        if (!idFactory.isInitialized()) {
            LOG.error("Could not read object IDs from DB. Please Check Your Data.", new Exception("Could not initialize the ID factory"));
            throw new Exception("Could not initialize the ID factory");
        }

        CacheManager.getInstance();


        LOG.info("===============[Loading Scripts]==================");
        Scripts.INSTANCE.load();
        GeoEngine.load();
        Strings.reload();
        GameTimeController.INSTANCE.init();
        printSection("Lineage World");
        World.init();
        printSection("");
        Parsers.parseAll();
        ItemsDAO.INSTANCE.init();
        printSection("Clan Crests");
        CrestCache.init();
        LOG.info("===============[Loading Images]==================");
        ImagesCache.init();
        printSection("");
//        CharacterDAO.INSTANCE.toString();
        ClanTable.INSTANCE.init();
        printSection("Fish Table");
        FishTable.INSTANCE.init();
        printSection("Skills");
        SkillTreeTable.getInstance();
        printSection("Augmentation Data");
        AugmentationData.getInstance();
        EnchantHPBonusTable.getInstance();
        PetSkillsTable.INSTANCE.load();
        printSection("Auctioneer");
        ItemAuctionManager.INSTANCE.init();
        NaiaTowerManager.init();
        LOG.info("===============[Adding handlers to scripts]==================");
        Scripts.INSTANCE.init();
        SpawnManager.INSTANCE.spawnAll();
        printSection("Boats");
        BoatHolder.getInstance().spawnAll();
        StaticObjectHolder.spawnAll();
        LOG.info("===============[Spawn Manager]==================");
        RaidBossSpawnManager.INSTANCE.init();
        printSection("Dimensional Rift");
        DimensionalRiftManager.INSTANCE.init();
        Announcements.INSTANCE.loadAnnouncements();
        LotteryManager.INSTANCE.init();
        PlayerMessageStack.getInstance();
        if (Config.AUTODESTROY_ITEM_AFTER > 0) {
            ItemsAutoDestroy.INSTANCE.init();
        }
        printSection("Seven Signs");
        SevenSigns.INSTANCE.init();
        SevenSignsFestival.INSTANCE.restoreFestivalData();
        SevenSigns.INSTANCE.updateFestivalScore();
        AutoSpawnManager.INSTANCE();
        SevenSigns.INSTANCE.spawnSevenSignsNPC();
        LOG.info("===================[Loading Olympiad System]=======================");
        if (Config.ENABLE_OLYMPIAD) {
            Olympiad.load();
            Hero.INSTANCE.log();
        }
        LOG.info("===================[Olympiad System Loaded]=======================");
        PetitionManager.getInstance();
        LOG.info("======================[Loading BALANCER]==========================");
        printSection("Admin Commands");
        AdminCommandHandler.INSTANCE.log();
        printSection("Players Commands");
        UserCommandHandler.INSTANCE.log();
        VoicedCommandHandler.INSTANCE.log();
        TaskManager.INSTANCE.init();
        LOG.info("======================[Loading Castels & Clan Halls]==========================");
        ResidenceHolder.callInit();
        EventHolder.callInit();
        CastleManorManager.INSTANCE.init(); // schedule all manor related events
        printSection("");
        Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
        printSection("Auto Cleaner");
        LOG.info("IdFactory: Free ObjectID's remaining: " + IdFactory.getInstance().size());
        printSection("");
//        CoupleManager.INSTANCE();
        if (Config.ALT_FISH_CHAMPIONSHIP_ENABLED) {
            FishingChampionShipManager.INSTANCE.toString();
        }
        printSection("Hellbound");
        HellboundManager.INSTANCE.init();
//        NaiaTowerManager.init();
        NaiaCoreManager.INSTANCE.init();
        printSection("");
        SoDManager.getInstance();
        SoIManager.init();
        BloodAltarManager.INSTANCE.init();
        AuctionManager.getInstance();
        if (Config.ALLOW_DROP_CALCULATOR) {
            LOG.info("Preparing Drop Calculator");
            ItemHolder.getDroppableTemplates();
        }
        MiniGameScoreManager.INSTANCE.init();

        if (Config.BUFF_STORE_ENABLED) {
            printSection("Offline Buffers");
            OfflineBuffersTable.INSTANCE.restoreOfflineBuffers();
        }
        Shutdown.getInstance().schedule(Config.RESTART_AT_TIME, 2);
        printSection("");
        LOG.info(">>>>>>>>>> GameServer Started <<<<<<<<<");
        LOG.info("Maximum Numbers of Connected Players: " + Config.MAXIMUM_ONLINE_USERS);
        LOG.info("===============[Protection Database]==================");
        CharacterDAO.checkCharactersToDelete();
        printSection("");
        GamePacketHandler gph = new GamePacketHandler();
        InetAddress serverAddr = Config.GAMESERVER_HOSTNAME.equalsIgnoreCase("*") ? null : InetAddress.getByName(Config.GAMESERVER_HOSTNAME);
        _selectorThreads = new ArrayList<>();
        for (int i = 0; i < Config.GAME_PORT.size(); i++) {
            try {
                _selectorThreads.add(new SelectorThread<>(Config.SELECTOR_CONFIG, gph, gph, gph, null));
                _selectorThreads.get(i).openServerSocket(serverAddr, Config.GAME_PORT.get(i));
                _selectorThreads.get(i).start();
            } catch (IOException ioe) {
                LOG.error("Cannot bind address: " + serverAddr + ":" + Config.GAME_PORT.get(i), ioe);
            }
        }
        if (!Config.GAMEIPS.isEmpty()) // AdvIP support. server.ini ports are ignored and accepted only IPs and ports from advipsystem.ini
        {
            int i = Config.GAME_PORT.size(); // Start from the last spot.
            for (AdvIP advip : Config.GAMEIPS) {
                try {
                    _selectorThreads.add(new SelectorThread<>(Config.SELECTOR_CONFIG, gph, gph, gph, null));
                    _selectorThreads.get(i).openServerSocket(InetAddress.getByName(advip.channelAdress), advip.channelPort);
                    _selectorThreads.get(i++).start();
                    LOG.info("AdvIP: Channel " + advip.channelId + " is open on: " + advip.channelAdress + ":" + advip.channelPort);
                } catch (IOException ioe) {
                    LOG.error("Cannot bind address: " + advip.channelAdress + ":" + advip.channelPort, ioe);
                }
            }
        }

        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new AutoAnnounce(), 60000, 60000);
        AutoImageSenderManager.startSendingImages();

        if (Config.ENABLE_ACHIEVEMENTS) {
            Achievements.INSTANCE.log();
            AchievementNotification.getInstance();
        }

        getListeners().onStart();
        if (!Config.IS_TELNET_ENABLED) {
            LOG.info("Telnet server is currently disabled.");
        }

        AuthServerCommunication.getInstance().start();
        server_started = new Date();
    }

    private static void printSection(String s) {
        if (s.isEmpty()) {
            s = "==============================================================================";
        } else {
            StringBuilder sBuilder = new StringBuilder("=[ " + s + " ]");
            while (sBuilder.length() < 78) {
                sBuilder.insert(0, "-");
            }
            s = sBuilder.toString();
        }
        LOG.info(s);
    }

    public static GameServer getInstance() {
        return _instance;
    }

    private static void checkFreePorts() {
        boolean binded = false;
        while (!binded) {
            for (int PORT_GAME : Config.GAME_PORT)
                try {
                    ServerSocket ss;
                    if (Config.GAMESERVER_HOSTNAME.equalsIgnoreCase("*")) {
                        ss = new ServerSocket(PORT_GAME);
                    } else {
                        ss = new ServerSocket(PORT_GAME, 50, InetAddress.getByName(Config.GAMESERVER_HOSTNAME));
                    }
                    ss.close();
                    binded = true;
                } catch (IOException e) {
                    LOG.warn("Port " + PORT_GAME + " is allready binded. Please free it and restart server.");
                    binded = false;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                }
        }
    }

    public static void main(String[] args) throws Exception {

        HibernateUtil.getSession();
        new GameServer();
    }

    public List<SelectorThread<GameClient>> getSelectorThreads() {
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

    private class GameServerListenerList extends ListenerList {
        void onStart() {
            getListeners().stream().filter(listener -> listener instanceof OnStartListener)
                    .map(listener -> (OnStartListener) listener)
                    .forEach(OnStartListener::onStart);
        }

        public void onShutdown() {
            getListeners().stream().filter(listener -> listener instanceof OnShutdownListener)
                    .map(listener -> (OnShutdownListener) listener)
                    .forEach(OnShutdownListener::onShutdown);
        }

    }
}