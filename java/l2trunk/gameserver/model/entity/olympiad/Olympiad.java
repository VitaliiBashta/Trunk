package l2trunk.gameserver.model.entity.olympiad;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.configuration.ExProperties;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.dao.OlympiadNobleDAO;
import l2trunk.gameserver.instancemanager.OlympiadHistoryManager;
import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Hero;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.MultiValueIntegerMap;
import l2trunk.gameserver.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

public final class Olympiad {
    public static final String OLYMPIAD_HTML_PATH = "olympiad/";
    public static final String CHAR_ID = "char_id";
    public static final String CLASS_ID = "class_id";
    public static final String CHAR_NAME = "char_name";
    public static final String POINTS = "olympiad_points";
    public static final String POINTS_PAST = "olympiad_points_past";
    public static final String POINTS_PAST_STATIC = "olympiad_points_past_static";
    public static final String COMP_DONE = "competitions_done";
    public static final String COMP_WIN = "competitions_win";
    public static final String COMP_LOOSE = "competitions_loose";
    public static final String GAME_CLASSES_COUNT = "game_classes_count";
    public static final String GAME_NOCLASSES_COUNT = "game_noclasses_count";
    public static final String GAME_TEAM_COUNT = "game_team_count";
    public static final Stadia[] STADIUMS = new Stadia[Config.OLYMPIAD_STADIAS_COUNT];
    public static final List<Integer> _nonClassBasedRegisters = new CopyOnWriteArrayList<>();
    public static final MultiValueIntegerMap CLASS_BASED_REGISTERS = new MultiValueIntegerMap();
    public static final MultiValueIntegerMap TEAM_BASED_REGISTERS = new MultiValueIntegerMap();
    //public static final int DEFAULT_POINTS = 50;
    //private static final int WEEKLY_POINTS = 10;
    private static final int TEAM_PARTY_SIZE = 3;
    private static final Logger _log = LoggerFactory.getLogger(Olympiad.class);
    private static final List<NpcInstance> NPCS = new ArrayList<>();
    public static Map<Integer, StatsSet> nobles;
    public static Map<Integer, Integer> noblesRank;
    public static List<StatsSet> heroesToBe;
    public static long olympiadEnd;
    public static long validationEnd;
    public static int period;
    public static long nextWeeklyChange;
    public static int currentCycle;
    public static boolean _inCompPeriod;
    public static boolean _isOlympiadEnd;
    public static ScheduledFuture<?> _scheduledManagerTask;
    public static ScheduledFuture<?> _scheduledWeeklyTask;
    public static ScheduledFuture<?> _scheduledValdationTask;
    public static OlympiadManager _manager;
    private static long _compEnd;
    private static Calendar _compStart;
    private static ScheduledFuture<?> _scheduledOlympiadEnd;

    public static void load() {
        nobles = new ConcurrentHashMap<>();
        currentCycle = ServerVariables.getInt("Olympiad_CurrentCycle", -1);
        period = ServerVariables.getInt("Olympiad_Period", -1);
        olympiadEnd = ServerVariables.getLong("Olympiad_End", -1);
        validationEnd = ServerVariables.getLong("Olympiad_ValdationEnd", -1);
        nextWeeklyChange = ServerVariables.getLong("Olympiad_NextWeeklyChange", -1);

        ExProperties olympiadProperties = Config.load(Config.OLYMPIAD);

        if (currentCycle == -1)
            currentCycle = olympiadProperties.getProperty("CurrentCycle", 1);
        if (period == -1)
            period = olympiadProperties.getProperty("Period", 0);
        if (olympiadEnd == -1)
            olympiadEnd = olympiadProperties.getProperty("OlympiadEnd", 0L);
        if (validationEnd == -1)
            validationEnd = olympiadProperties.getProperty("ValdationEnd", 0L);
        if (nextWeeklyChange == -1)
            nextWeeklyChange = olympiadProperties.getProperty("NextWeeklyChange", 0L);

        initStadiums();

        OlympiadHistoryManager.INSTANCE.init();
        OlympiadNobleDAO.select();
        OlympiadDatabase.loadNoblesRank();

        switch (period) {
            case 0:
                if (olympiadEnd == 0 || olympiadEnd < Calendar.getInstance().getTimeInMillis())
                    OlympiadDatabase.setNewOlympiadEnd();
                else
                    _isOlympiadEnd = false;
                break;
            case 1:
                _isOlympiadEnd = true;
                _scheduledValdationTask = ThreadPoolManager.INSTANCE.schedule(new ValidationTask(), getMillisToValidationEnd());
                break;
            default:
                _log.warn("Olympiad System: Omg something went wrong in loading!! Period = " + period);
                return;
        }

        _log.info("Olympiad System: Loading Olympiad System....");
        if (period == 0)
            _log.info("Olympiad System: Currently in Olympiad Period");
        else
            _log.info("Olympiad System: Currently in Validation Period");

        _log.info("Olympiad System: Period Ends....");

        long milliToEnd;
        if (period == 0)
            milliToEnd = getMillisToOlympiadEnd();
        else
            milliToEnd = getMillisToValidationEnd();

        double numSecs = milliToEnd / 1000. % 60;
        double countDown = (milliToEnd / 1000. - numSecs) / 60;
        int numMins = (int) Math.floor(countDown % 60);
        countDown = (countDown - numMins) / 60;
        int numHours = (int) Math.floor(countDown % 24);
        int numDays = (int) Math.floor((countDown - numHours) / 24);

        _log.info("Olympiad System: In " + numDays + " days, " + numHours + " hours and " + numMins + " mins.");

        if (period == 0) {
            _log.info("Olympiad System: Next Weekly Change is in....");

            milliToEnd = getMillisToWeekChange();

            double numSecs2 = milliToEnd / 1000. % 60;
            double countDown2 = (milliToEnd / 1000. - numSecs2) / 60;
            int numMins2 = (int) Math.floor(countDown2 % 60);
            countDown2 = (countDown2 - numMins2) / 60;
            int numHours2 = (int) Math.floor(countDown2 % 24);
            int numDays2 = (int) Math.floor((countDown2 - numHours2) / 24);

            _log.info("Olympiad System: In " + numDays2 + " days, " + numHours2 + " hours and " + numMins2 + " mins.");
        }

        _log.info("Olympiad System: Loaded " + nobles.size() + " Noblesses");

        if (period == 0)
            init();
    }

    public static void cancelPeriodTasks() {
        if (_scheduledValdationTask != null)
            _scheduledValdationTask.cancel(false);
        if (_scheduledOlympiadEnd != null)
            _scheduledOlympiadEnd.cancel(false);
    }

    private static void initStadiums() {
        for (int i = 0; i < STADIUMS.length; i++)
            if (STADIUMS[i] == null)
                STADIUMS[i] = new Stadia();
    }

    public static void init() {
        if (period == 1)
            return;

        _compStart = Calendar.getInstance();
        _compStart.set(Calendar.HOUR_OF_DAY, Config.ALT_OLY_START_TIME);
        _compStart.set(Calendar.MINUTE, Config.ALT_OLY_MIN);
        _compEnd = _compStart.getTimeInMillis() + Config.ALT_OLY_CPERIOD;

        if (_scheduledOlympiadEnd != null)
            _scheduledOlympiadEnd.cancel(false);
        _scheduledOlympiadEnd = ThreadPoolManager.INSTANCE.schedule(new OlympiadEndTask(), getMillisToOlympiadEnd());

        updateCompStatus();

        if (_scheduledWeeklyTask != null)
            _scheduledWeeklyTask.cancel(false);
        _scheduledWeeklyTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new WeeklyTask(), getMillisToWeekChange(), Config.ALT_OLY_WPERIOD);
    }

    public static synchronized void registerNoble(Player noble, CompType type) {
        if (noble.getClassId().occupation() < 3) return;

        if (!_inCompPeriod || _isOlympiadEnd) {
            noble.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
            return;
        }

        if (getMillisToOlympiadEnd() <= 600 * 1000) {
            noble.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
            return;
        }

        if (getMillisToCompEnd() <= 600 * 1000) {
            noble.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
            return;
        }

        if (noble.isCursedWeaponEquipped()) {
            noble.sendPacket(SystemMsg.YOU_CANNOT_REGISTER_WHILE_IN_POSSESSION_OF_A_CURSED_WEAPON);
            return;
        }

        StatsSet nobleInfo = nobles.get(noble.objectId());

        if (!validPlayer(noble, noble, type)) {
            return;
        }

        if (getNoblePoints(noble.objectId()) < 1) {
            noble.sendMessage(new CustomMessage("l2trunk.gameserver.model.entity.Olympiad.LessPoints"));
            return;
        }

        if (noble.getOlympiadGame() != null) {
            //
            return;
        }

        int classId = nobleInfo.getInteger(CLASS_ID);

        // SoulHound hack
        if (classId == 133)
            classId = 132;

        switch (type) {
            case CLASSED: {
                CLASS_BASED_REGISTERS.put(classId, noble.objectId());
                noble.sendPacket(SystemMsg.YOU_HAVE_BEEN_REGISTERED_FOR_THE_GRAND_OLYMPIAD_WAITING_LIST_FOR_A_CLASS_SPECIFIC_MATCH);
                break;
            }
            case NON_CLASSED: {
                _nonClassBasedRegisters.add(noble.objectId());
                noble.sendPacket(SystemMsg.YOU_ARE_CURRENTLY_REGISTERED_FOR_A_1V1_CLASS_IRRELEVANT_MATCH);
                break;
            }
            case TEAM: {
                Party party = noble.getParty();
                if (party == null) {
                    noble.sendPacket(SystemMsg.ONLY_A_PARTY_LEADER_CAN_REQUEST_A_TEAM_MATCH);
                    return;
                }

                if (party.size() != TEAM_PARTY_SIZE) {
                    noble.sendPacket(SystemMsg.THE_REQUEST_CANNOT_BE_MADE_BECAUSE_THE_REQUIREMENTS_HAVE_NOT_BEEN_MET);
                    return;
                }

                if (party.getMembersStream()
                        .anyMatch(member -> !validPlayer(noble, member, type))) {
                    return;
                }

                TEAM_BASED_REGISTERS.putAll(noble.objectId(), party.getMembersObjIds());
                noble.sendPacket(SystemMsg.YOU_ARE_CURRENTLY_REGISTERED_FOR_A_3_VS_3_CLASS_IRRELEVANT_TEAM_MATCH);
                break;
            }
        }

    }

    private static boolean validPlayer(Player sendPlayer, Player validPlayer, CompType type) {
        if (!validPlayer.isNoble()) {
            sendPlayer.sendPacket(new SystemMessage2(SystemMsg.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_ONLY_NOBLESSE_CHARACTERS_CAN_PARTICIPATE_IN_THE_OLYMPIAD).addName(validPlayer));
            return false;
        }

        if (validPlayer.getBaseClassId() != validPlayer.getClassId()) {
            sendPlayer.sendPacket(new SystemMessage2(SystemMsg.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_SUBCLASS_CHARACTER_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD).addName(validPlayer));
            return false;
        }

        int[] ar = getWeekGameCounts(validPlayer.objectId());

        switch (type) {
            case CLASSED:
                if (CLASS_BASED_REGISTERS.containsValue(validPlayer.objectId())) {
                    sendPlayer.sendPacket(new SystemMessage2(SystemMsg.C1_IS_ALREADY_REGISTERED_ON_THE_CLASS_MATCH_WAITING_LIST).addName(validPlayer));
                    return false;
                }

                if (ar[1] == 0) {
                    validPlayer.sendPacket(SystemMsg.THE_TOTAL_NUMBER_OF_MATCHES_THAT_CAN_BE_ENTERED_IN_1_WEEK_IS_60_CLASS_IRRELEVANT_INDIVIDUAL_MATCHES_30_SPECIFIC_MATCHES_AND_10_TEAM_MATCHES);
                    return false;
                }
                break;
            case NON_CLASSED:
                if (_nonClassBasedRegisters.contains(validPlayer.objectId())) {
                    sendPlayer.sendPacket(new SystemMessage2(SystemMsg.C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_CLASS_IRRELEVANT_INDIVIDUAL_MATCH).addName(validPlayer));
                    return false;
                }
                if (ar[2] == 0) {
                    validPlayer.sendPacket(SystemMsg.THE_TOTAL_NUMBER_OF_MATCHES_THAT_CAN_BE_ENTERED_IN_1_WEEK_IS_60_CLASS_IRRELEVANT_INDIVIDUAL_MATCHES_30_SPECIFIC_MATCHES_AND_10_TEAM_MATCHES);
                    return false;
                }
                break;
            case TEAM:
                if (TEAM_BASED_REGISTERS.containsValue(validPlayer.objectId())) {
                    sendPlayer.sendPacket(new SystemMessage2(SystemMsg.C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_3_VS_3_CLASS_IRRELEVANT_TEAM_MATCH).addName(validPlayer));
                    return false;
                }
                if (ar[3] == 0) {
                    validPlayer.sendPacket(SystemMsg.THE_TOTAL_NUMBER_OF_MATCHES_THAT_CAN_BE_ENTERED_IN_1_WEEK_IS_60_CLASS_IRRELEVANT_INDIVIDUAL_MATCHES_30_SPECIFIC_MATCHES_AND_10_TEAM_MATCHES);
                    return false;
                }
                break;
        }

        if (ar[0] == 0) {
            validPlayer.sendPacket(SystemMsg.THE_MAXIMUM_MATCHES_YOU_CAN_PARTICIPATE_IN_1_WEEK_IS_70);
            return false;
        }

        if (isRegisteredInComp(validPlayer)) {
            sendPlayer.sendPacket(new SystemMessage2(SystemMsg.C1_IS_ALREADY_REGISTERED_ON_THE_MATCH_WAITING_LIST).addName(validPlayer));
            return false;
        }

        return true;
    }

    public static synchronized void logoutPlayer(Player player) {
        CLASS_BASED_REGISTERS.removeValue(player.objectId());
        _nonClassBasedRegisters.remove(player.objectId());
        TEAM_BASED_REGISTERS.removeValue(player.objectId());

        OlympiadGame game = player.getOlympiadGame();
        if (game != null)
            try {
                if (!game.logoutPlayer(player) && !game.validated)
                    game.endGame(20, true);
            } catch (Exception e) {
                _log.error("Error on Olympiad logout Player", e);
            }
    }

    public static synchronized boolean unRegisterNoble(Player noble) {
        if (!_inCompPeriod || _isOlympiadEnd) {
            noble.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
            return false;
        }

        if (!noble.isNoble()) {
            noble.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
            return false;
        }

        if (!isRegistered(noble)) {
            noble.sendPacket(SystemMsg.YOU_ARE_NOT_CURRENTLY_REGISTERED_FOR_THE_GRAND_OLYMPIAD);
            return false;
        }

        OlympiadGame game = noble.getOlympiadGame();
        if (game != null) {
            if (game.getStatus() == BattleStatus.Begin_Countdown) {
                // TODO: System Message
                //TODO [VISTALL] узнать ли прерывается бой и если так ли это та мессага SystemMsg.YOUR_OPPONENT_MADE_HASTE_WITH_THEIR_TAIL_BETWEEN_THEIR_LEGS_THE_MATCH_HAS_BEEN_CANCELLED
                noble.sendMessage("Now you can't cancel participation in the Grand Olympiad.");
                return false;
            }

            try {
                if (!game.logoutPlayer(noble) && !game.validated)
                    game.endGame(20, true);
            } catch (Exception e) {
                _log.error("Error on olympiad unRegister Noble", e);
            }
        }
        CLASS_BASED_REGISTERS.removeValue(noble.objectId());
        _nonClassBasedRegisters.remove(noble.objectId());
        TEAM_BASED_REGISTERS.removeValue(noble.objectId());

        noble.sendPacket(SystemMsg.YOU_HAVE_BEEN_REMOVED_FROM_THE_GRAND_OLYMPIAD_WAITING_LIST);

        return true;
    }

    private static synchronized void updateCompStatus() {
        long milliToStart = getMillisToCompBegin();
        double numSecs = milliToStart / 1000. % 60;
        double countDown = (milliToStart / 1000. - numSecs) / 60;
        int numMins = (int) Math.floor(countDown % 60);
        countDown = (countDown - numMins) / 60;
        int numHours = (int) Math.floor(countDown % 24);
        int numDays = (int) Math.floor((countDown - numHours) / 24);

        _log.info("Olympiad System: Competition Period Starts in " + numDays + " days, " + numHours + " hours and " + numMins + " mins.");
        _log.info("Olympiad System: Event starts/started: " + _compStart.getTime());

        ThreadPoolManager.INSTANCE.schedule(new CompStartTask(), getMillisToCompBegin());
    }

    private static long getMillisToOlympiadEnd() {
        return olympiadEnd - System.currentTimeMillis();
    }

    static long getMillisToValidationEnd() {
        if (validationEnd > System.currentTimeMillis())
            return validationEnd - System.currentTimeMillis();
        return 10L;
    }

    public static boolean isOlympiadEnd() {
        return _isOlympiadEnd;
    }

    public static boolean inCompPeriod() {
        return _inCompPeriod;
    }

    private static long getMillisToCompBegin() {
        if (_compStart.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() && _compEnd > Calendar.getInstance().getTimeInMillis())
            return 10L;
        if (_compStart.getTimeInMillis() > Calendar.getInstance().getTimeInMillis())
            return _compStart.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        return setNewCompBegin();
    }

    private static long setNewCompBegin() {
        _compStart = Calendar.getInstance();
        _compStart.set(Calendar.HOUR_OF_DAY, Config.ALT_OLY_START_TIME);
        _compStart.set(Calendar.MINUTE, Config.ALT_OLY_MIN);
        _compStart.add(Calendar.HOUR_OF_DAY, 24);
        _compEnd = _compStart.getTimeInMillis() + Config.ALT_OLY_CPERIOD;

        _log.info("Olympiad System: New Schedule @ " + _compStart.getTime());

        return _compStart.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
    }

    public static long getMillisToCompEnd() {
        return _compEnd - Calendar.getInstance().getTimeInMillis();
    }

    private static long getMillisToWeekChange() {
        if (nextWeeklyChange > Calendar.getInstance().getTimeInMillis())
            return nextWeeklyChange - Calendar.getInstance().getTimeInMillis();
        return 10L;
    }

    protected static void reloadOlympiadEnd() {
        olympiadEnd = TimeUtils.getMilisecondsToNextDay(Config.ALT_OLY_DATE_END, 0, 1);
    }

    static synchronized void doWeekTasks() {
        if (period == 1)
            return;
        nobles.forEach((playerId, stats) -> {
            Player player = GameObjectsStorage.getPlayer(playerId);

            if (period != 1)
                stats.inc(POINTS, Config.OLYMPIAD_POINTS_WEEKLY);
            stats.unset(GAME_CLASSES_COUNT);
            stats.unset(GAME_NOCLASSES_COUNT);
            stats.unset(GAME_TEAM_COUNT);

            if (player != null)
                player.sendPacket(new SystemMessage2(SystemMsg.C1_HAS_EARNED_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES).addName(player).addInteger(Config.OLYMPIAD_POINTS_WEEKLY));
        });
    }

    public static int getCurrentCycle() {
        return currentCycle;
    }

    public static synchronized void addSpectator(int id, Player spectator) {
        if (spectator.isInOlympiadMode() || isRegistered(spectator) || Olympiad.isRegisteredInComp(spectator)) {
            spectator.sendPacket(SystemMsg.YOU_MAY_NOT_OBSERVE_A_GRAND_OLYMPIAD_GAMES_MATCH_WHILE_YOU_ARE_ON_THE_WAITING_LIST);
            return;
        }

        if (spectator.isInCombat() || spectator.getPvpFlag() > 0) {
            spectator.sendPacket(SystemMsg.YOU_CANNOT_OBSERVE_WHILE_YOU_ARE_IN_COMBAT);
            return;
        }

        final OlympiadGame game = getOlympiadGame(id);
        if (game == null || game.getStatus() == BattleStatus.Begining || game.getStatus() == BattleStatus.Begin_Countdown || game.getStatus() == BattleStatus.Ending) {
            spectator.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
            return;
        }

        if (spectator.getPet() != null)
            spectator.getPet().unSummon();

        List<Location> spawns = game.getReflection().getInstancedZone().getTeleportCoords();
        if (spawns.size() < 3) {
            Location c1 = spawns.get(0);
            Location c2 = spawns.get(1);
            spectator.enterOlympiadObserverMode(new Location((c1.x + c2.x) / 2, (c1.y + c2.y) / 2, (c1.z + c2.z) / 2), game, game.getReflection());
        } else
            spectator.enterOlympiadObserverMode(spawns.get(2), game, game.getReflection());
    }

    public static synchronized void removeSpectator(int id, Player spectator) {
        if (_manager == null || _manager.getOlympiadInstance(id) == null)
            return;

        _manager.getOlympiadInstance(id).removeSpectator(spectator);
    }

    public static List<Player> getSpectators(int id) {
        if (_manager == null || _manager.getOlympiadInstance(id) == null)
            return null;
        return _manager.getOlympiadInstance(id).getSpectators();
    }

    private static OlympiadGame getOlympiadGame(int gameId) {
        if (_manager == null || gameId < 0)
            return null;
        return _manager.getOlympiadGames().get(gameId);
    }

    public static synchronized int[] getWaitingList() {
        if (!inCompPeriod())
            return null;

        int[] array = new int[3];
        array[0] = CLASS_BASED_REGISTERS.totalSize();
        array[1] = _nonClassBasedRegisters.size();
        array[2] = TEAM_BASED_REGISTERS.totalSize();

        return array;
    }

    public static synchronized int getNoblessePasses(Player player) {
        int objId = player.objectId();

        StatsSet noble = nobles.get(objId);
        if (noble == null)
            return 0;

        int points = noble.getInteger(POINTS_PAST);
        if (points == 0) // Уже получил бонус
            return 0;

        int rank = noblesRank.get(objId);
        switch (rank) {
            case 1:
                points = Config.ALT_OLY_RANK1_POINTS;
                break;
            case 2:
                points = Config.ALT_OLY_RANK2_POINTS;
                break;
            case 3:
                points = Config.ALT_OLY_RANK3_POINTS;
                break;
            case 4:
                points = Config.ALT_OLY_RANK4_POINTS;
                break;
            default:
                points = Config.ALT_OLY_RANK5_POINTS;
        }

        if (player.isHero() || Hero.INSTANCE.isInactiveHero(player.objectId()))
            points += Config.ALT_OLY_HERO_POINTS;

        noble.unset(POINTS_PAST);
        OlympiadDatabase.saveNobleData(objId);

        return points * Config.ALT_OLY_GP_PER_POINT;
    }

    public static synchronized boolean isRegistered(Player noble) {
        if (CLASS_BASED_REGISTERS.containsValue(noble.objectId()))
            return true;
        if (_nonClassBasedRegisters.contains(noble.objectId()))
            return true;
        return TEAM_BASED_REGISTERS.containsValue(noble.objectId());
    }

    public static synchronized boolean isRegisteredInComp(Player player) {
        if (isRegistered(player))
            return true;
        if (_manager == null || _manager.getOlympiadGames() == null)
            return false;
        for (OlympiadGame g : _manager.getOlympiadGames().values())
            if (g != null && g.isRegistered(player.objectId()))
                return true;
        return false;
    }

    /**
     * Возвращает олимпийские очки за текущий период
     */
    public static synchronized int getNoblePoints(int objId) {
        StatsSet noble = nobles.get(objId);
        if (noble == null)
            return 0;
        return noble.getInteger(POINTS);
    }

    public static synchronized int getCompetitionDone(int objId) {
        StatsSet noble = nobles.get(objId);
        if (noble == null)
            return 0;
        return noble.getInteger(COMP_DONE);
    }

    public static synchronized int getCompetitionWin(int objId) {
        StatsSet noble = nobles.get(objId);
        if (noble == null)
            return 0;
        return noble.getInteger(COMP_WIN);
    }

    public static synchronized int getCompetitionLoose(int objId) {
        StatsSet noble = nobles.get(objId);
        if (noble == null)
            return 0;
        return noble.getInteger(COMP_LOOSE);
    }

    public static synchronized int[] getWeekGameCounts(int objId) {
        int[] ar = new int[4];

        StatsSet noble = nobles.get(objId);
        if (noble == null)
            return ar;

        ar[0] = Config.GAME_MAX_LIMIT - noble.getInteger(GAME_CLASSES_COUNT) - noble.getInteger(GAME_NOCLASSES_COUNT) - noble.getInteger(GAME_TEAM_COUNT);
        ar[1] = Config.GAME_CLASSES_COUNT_LIMIT - noble.getInteger(GAME_CLASSES_COUNT);
        ar[2] = Config.GAME_NOCLASSES_COUNT_LIMIT - noble.getInteger(GAME_NOCLASSES_COUNT);
        ar[3] = Config.GAME_TEAM_COUNT_LIMIT - noble.getInteger(GAME_TEAM_COUNT);

        return ar;
    }

    public static List<NpcInstance> getNpcs() {
        return NPCS;
    }

    public static void addOlympiadNpc(NpcInstance npc) {
        NPCS.add(npc);
    }

    public static String getNobleName(int objId) {
        StatsSet noble = nobles.get(objId);
        if (noble == null)
            return null;
        return noble.getString(CHAR_NAME, "");
    }

    public static int getNobleClass(int objId) {
        StatsSet noble = nobles.get(objId);
        if (noble == null)
            return 0;
        return noble.getInteger(CLASS_ID);
    }

    public static void manualSetNoblePoints(int objId, int points) {
        StatsSet noble = nobles.get(objId);
        if (noble == null)
            return;
        noble.set(POINTS, points);
        OlympiadDatabase.saveNobleData(objId);
    }

    public static synchronized boolean isNoble(int objId) {
        return nobles.get(objId) != null;
    }

    public static synchronized void addNoble(Player noble) {
        if (!nobles.containsKey(noble.objectId())) {

            StatsSet statDat = new StatsSet();
            statDat.set(CLASS_ID, noble.getBaseClassId().id);
            statDat.set(CHAR_NAME, noble.getName());
            statDat.set(POINTS, Config.OLYMPIAD_POINTS_DEFAULT);

            nobles.put(noble.objectId(), statDat);
            OlympiadDatabase.saveNobleData();
        }
    }

    public static synchronized void removeNoble(Player noble) {
        nobles.remove(noble.objectId());
        OlympiadDatabase.saveNobleData();
    }

    public static int getPeriod() {
        return period;
    }

    public static int getCountOpponents() {
        return _nonClassBasedRegisters.size() + CLASS_BASED_REGISTERS.size() + TEAM_BASED_REGISTERS.size();
    }

    public static class Stadia {
        private boolean busy = false;

        boolean isBusy() {
            return busy;
        }

        void setStadiaBusy() {
            busy = true;
        }

        void setStadiaFree() {
            busy = false;
        }
    }
}