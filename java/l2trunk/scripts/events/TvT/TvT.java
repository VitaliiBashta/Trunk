package l2trunk.scripts.events.TvT;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.actor.player.OnPlayerExitListener;
import l2trunk.gameserver.listener.actor.player.OnTeleportListener;
import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;

public class TvT extends Functions implements ScriptFile, OnDeathListener,
        OnTeleportListener, OnPlayerExitListener {


    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public static boolean isRunned() {
        return false;
    }

    public static int getMinLevelForCategory(int category) {
        return 0;
    }

    public static int getMaxLevelForCategory(int category) {
        return 0;
    }

    public static int getCategory(int level) {
        return 0;
    }

    public void start(String[] var) {
    }

    public static void sayToAll(String address, String[] replacements) {
        /*Announcements.INSTANCE().announceByCustomMessage(address, replacements, ChatType.CRITICAL_ANNOUNCE);*/
    }

    public static void question() {
		/*for(Player player : GameObjectsStorage.getAllPlayersForIterate())
			if (player != null && !player.isDead() && player.getLevel() >= _minLevel && player.getLevel() <= _maxLevel && player.getReflection().isDefault() && !player.isInOlympiadMode() && !player.isInObserverMode())
				player.scriptRequest(new CustomMessage("scripts.events.TvT.AskPlayer", player).toString(), "events.TvT.TvT:addPlayer", new Object[0]);*/
    }

    public static void announce() {
    }

    public void addPlayer() {
    }

    public static void prepare() {
    }

    public static void go() {
    }

    public static void end() {
    }

    public void autoContinue() {
    }

    public static void giveItemsToWinner(boolean team1, boolean team2, double rate) {
    }

    public static void teleportPlayersToColiseum() {
    }

    public static void teleportPlayers() {
    }

    public static void paralyzePlayers() {
    }

    public static void upParalyzePlayers() {
    }

    public static void ressurectPlayers() {
    }

    public static void healPlayers() {
    }

    public static void cleanPlayers() {
    }

    public static void checkLive() {
    }

    public static void removeAura() {
    }

    public static void clearArena() {
    }

    @Override
    public void onDeath(Creature self, Creature killer) {

    }

    public static void resurrectAtBase(Creature self) {
    }

    public static void buffPlayer(Player player) {
    }

    @Override
    public void onTeleport(Player player, int x, int y, int z, Reflection reflection) {
    }

    @Override
    public void onPlayerExit(Player player) {
    }

    private static class ZoneListener implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Creature cha) {
        }

        @Override
        public void onZoneLeave(Zone zone, Creature cha) {
        }
    }

    public static void buffPlayers() {

    }

    public void scheduleEventStart() {
    }

    public static void mageBuff(Player player) {
    }

    public static void fighterBuff(Player player) {
    }

    public class StartTask extends RunnableImpl {

        @Override
        public void runImpl() {
        }
    }
}