package l2trunk.scripts.events.lastHero;

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
import l2trunk.gameserver.utils.Location;

public final class LastHero extends Functions implements ScriptFile, OnDeathListener, OnTeleportListener, OnPlayerExitListener {

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
        return false;//_isRegistrationActive || _status > 0;
    }

    public static int getMinLevelForCategory(int category) {
        return 0;
    }

    public void start(int var) {
    }

    public static void question() {
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

    public static void saveBackCoords() {
    }

    public static void clearArena() {
    }

    @Override
    public void onDeath(Creature self, Creature killer) {
    }

    @Override
    public void onTeleport(Player player, int x, int y, int z, Reflection reflection) {
    }

    @Override
    public void onPlayerExit(Player player) {
    }

    @SuppressWarnings("unused")
    private static class ZoneListener implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Creature cha) {
        }

        @Override
        public void onZoneLeave(Zone zone, Creature cha) {
        }
    }

    public static void preLoad() {
    }

    public void preStartTask() {
    }
}