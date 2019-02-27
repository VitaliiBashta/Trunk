package l2trunk.scripts.handler.admincommands;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.model.Player;
import l2trunk.scripts.bosses.AntharasManager;
import l2trunk.scripts.bosses.BaiumManager;
import l2trunk.scripts.bosses.ValakasManager;

import java.util.Calendar;

public final class AdminBosses implements IAdminCommandHandler {
    private static void getEpicsRespawn(Player activeChar) {
        activeChar.sendMessage("Antharas: " + convertRespawnDate(AntharasManager.getState().getRespawnDate()));
        activeChar.sendMessage("Valakas: " + convertRespawnDate(ValakasManager.getState().getRespawnDate()));
        activeChar.sendMessage("Baium: " + convertRespawnDate(BaiumManager.getState().getRespawnDate()));
        activeChar.sendMessage("Beleth: " + convertRespawnDate(ServerVariables.getLong("BelethKillTime")));
    }

    private static String convertRespawnDate(long date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date);

        return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
    }

    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, final Player activeChar) {
        if (!activeChar.getPlayerAccess().CanEditNPC)
            return false;

        getEpicsRespawn(activeChar);

        return true;
    }

    @Override
    public String getAdminCommand() {
        return "admin_epics_respawn";
    }

}
