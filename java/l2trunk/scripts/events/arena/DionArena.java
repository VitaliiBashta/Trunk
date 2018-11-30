package l2trunk.scripts.events.arena;

import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.actor.player.OnPlayerExitListener;
import l2trunk.gameserver.listener.actor.player.OnTeleportListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;

public class DionArena extends Functions implements ScriptFile, OnDeathListener, OnTeleportListener, OnPlayerExitListener {
    private static class DionArenaImpl extends ArenaTemplate {
        @Override
        protected void onLoad() {
			/*_managerId = 20230001;
			_className = "DionArena";
			_status = 0;

			_team1list = new CopyOnWriteArrayList<Long>();
			_team2list = new CopyOnWriteArrayList<Long>();
			_team1live = new CopyOnWriteArrayList<Long>();
			_team2live = new CopyOnWriteArrayList<Long>();

			_expToReturn = new HashMap<Integer, Integer>();
			_classToReturn = new HashMap<Integer, Integer>();

			_zoneListener = new ZoneListener();
			_zone = ReflectionUtils.getZone("[dion_monster_pvp]");
			_zone.addListener(_zoneListener);

			_team1points = new ArrayList<Location>();
			_team2points = new ArrayList<Location>();

			_team1points.add(new Location(12053, 183101, -3563));
			_team1points.add(new Location(12253, 183101, -3563));
			_team1points.add(new Location(12459, 183101, -3563));
			_team1points.add(new Location(12659, 183101, -3563));
			_team1points.add(new Location(12851, 183101, -3563));
			_team2points.add(new Location(12851, 183941, -3563));
			_team2points.add(new Location(12659, 183941, -3563));
			_team2points.add(new Location(12459, 183941, -3563));
			_team2points.add(new Location(12253, 183941, -3563));
			_team2points.add(new Location(12053, 183941, -3563));*/
        }

        @Override
        protected void onReload() {
			/*if(_status > 0)
				template_stop();
			_zone.removeListener(_zoneListener);*/
        }
    }

    private static ArenaTemplate _instance = new DionArenaImpl();

    private static ArenaTemplate getInstance() {
        if (_instance == null)
            _instance = new DionArenaImpl();
        return _instance;
    }

    @Override
    public void onLoad() {
        getInstance().onLoad();
        CharListenerList.addGlobal(this);
    }

    @Override
    public void onReload() {
        //INSTANCE().onReload();
        //_instance = null;
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public void onDeath(Creature cha, Creature killer) {
        //INSTANCE().onDeath(cha, killer);
    }

    @Override
    public void onPlayerExit(Player player) {
        getInstance().onPlayerExit(player);
    }

    @Override
    public void onTeleport(Player player, int x, int y, int z, Reflection reflection) {
        getInstance().onTeleport(player);
    }

    public String DialogAppend_20230001(Integer val) {
		/*if(val == 0)
		{
			Player player = getSelf();
			if (player.isGM())
				return HtmCache.INSTANCE().getNotNull("scripts/events/arena/20230001.htm", player) + HtmCache.INSTANCE().getNotNull("scripts/events/arena/20230001-4.htm", player);
			return HtmCache.INSTANCE().getNotNull("scripts/events/arena/20230001.htm", player);
		}*/
        return "";
    }

    public String DialogAppend_20230002(Integer val) {
        return "";//DialogAppend_20230001(val);
    }

    public void create1() {
        //INSTANCE().template_create1(getSelf());
    }

    public void create2() {
        //INSTANCE().template_create2(getSelf());
    }

    public void register() {
        //INSTANCE().template_register(getSelf());
    }

    public void check1(String[] var) {
        //INSTANCE().template_check1(getSelf(), var);
    }

    public void check2(String[] var) {
        //INSTANCE().template_check2(getSelf(), var);
    }

    public void register_check(String[] var) {
        //INSTANCE().template_register_check(getSelf(), var);
    }

    public void stop() {
        //INSTANCE().template_stop();
    }

    public void announce() {
        //INSTANCE().template_announce();
    }

    public void prepare() {
        //INSTANCE().template_prepare();
    }

    public void start() {
        //INSTANCE().template_start();
    }

    public static void timeOut() {
        //INSTANCE().template_timeOut();
    }
}