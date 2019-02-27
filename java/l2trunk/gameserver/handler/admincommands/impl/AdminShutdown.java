package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.commons.lang.NumberUtils;
import l2trunk.commons.lang.StatsUtils;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.GameTimeController;
import l2trunk.gameserver.Shutdown;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public final class AdminShutdown implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (!activeChar.getPlayerAccess().CanRestart)
            return false;

        try {
            switch (comm) {
                case "admin_server_shutdown":
                    Shutdown.getInstance().schedule(NumberUtils.toInt(wordList[1], -1), Shutdown.SHUTDOWN);
                    break;
                case "admin_server_restart":
                    Shutdown.getInstance().schedule(NumberUtils.toInt(wordList[1], -1), Shutdown.RESTART);
                    break;
                case "admin_server_abort":
                    Shutdown.getInstance().cancel();
                    break;
            }
        } catch (Exception e) {
            sendHtmlForm(activeChar);
        }

        return true;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_server_shutdown",
                "admin_server_restart",
                "admin_server_abort");
    }

    private void sendHtmlForm(Player activeChar) {
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

        int t = GameTimeController.INSTANCE.getGameTime();
        int h = t / 60;
        int m = t % 60;
        SimpleDateFormat format = new SimpleDateFormat("h:mm a");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, h);
        cal.set(Calendar.MINUTE, m);

        String replyMSG = "<html><body>" + "<table width=260><tr>" +
                "<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=60 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>" +
                "<td width=150></td>" +
                "<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=60 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>" +
                "</tr></table>" +
                "<br>" +
                "<table width=260><tr>" +
                "<td width=180><center><font name=hs12 color=LEVEL>Server Management Menu</font></center></td>" +
                "</tr></table>" +
                "<br><br>" +
                "<table>" +
                "<tr><td>Players Online: " + "<font color=00FF00>" + GameObjectsStorage.getAllPlayersCount() + "</font></td></tr>" +
                "<br>" +
                "<tr><td>Used Memory: " + "<font color=FF0000>" + StatsUtils.getMemUsedMb() + "<font></td></tr>" +
                "<br>" +
                "<tr><td>Server Rates: " + Config.RATE_XP + "x|| " + Config.RATE_SP + "x|| " + Config.RATE_DROP_ADENA + "x|| " + Config.RATE_DROP_ITEMS + "x</td></tr>" +
                "<br>" +
                "<tr><td>Game Time: " + format.format(cal.getTime()) + "</td></tr>" +
                "</table><br>" +
                "<table width=270>" +
                "<br>" +
                "<tr><td><center>Seconds till: <edit var=\"shutdown_time\" width=100></center></td></tr>" +
                "</table><br>" +
                "<center><table><tr><td>" +
                "<button value=\"Shutdown\" action=\"bypass -h admin_server_shutdown $shutdown_time\" width=90 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>" +
                "<button value=\"Restart\" action=\"bypass -h admin_server_restart $shutdown_time\" width=90 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td><td>" +
                "<button value=\"Abort\" action=\"bypass -h admin_server_abort\" width=90 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">" +
                "</td></tr></table></center>" +
                "</body></html>";
        adminReply.setHtml(replyMSG);
        activeChar.sendPacket(adminReply);
    }


}