package l2trunk.scripts.handler.admincommands;

import l2trunk.gameserver.handler.admincommands.AdminCommandHandler;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.scripts.bosses.AntharasManager;
import l2trunk.scripts.bosses.BaiumManager;
import l2trunk.scripts.bosses.ValakasManager;

import java.util.Calendar;
import java.util.List;

public final class AdminBosses implements IAdminCommandHandler, ScriptFile {
    private enum Commands {
        admin_epics_respawn
    }

    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, final Player activeChar) {
        Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanEditNPC)
            return false;

        if (command == Commands.admin_epics_respawn) {
            getEpicsRespawn(activeChar);
        }

        return true;
    }

    private static void getEpicsRespawn(Player activeChar) {
        activeChar.sendMessage("Antharas: " + convertRespawnDate(AntharasManager.getState().getRespawnDate()));
        activeChar.sendMessage("Valakas: " + convertRespawnDate(ValakasManager.getState().getRespawnDate()));
        activeChar.sendMessage("Baium: " + convertRespawnDate(BaiumManager.getState().getRespawnDate()));
        activeChar.sendMessage("Beleth: " + convertRespawnDate(ServerVariables.getLong("BelethKillTime", 0L)));
    }

    private static String convertRespawnDate(long date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date);

        return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    @Override
    public void onLoad() {
        AdminCommandHandler.INSTANCE.registerAdminCommandHandler(this);
    }

}
