package l2trunk.scripts.services.community;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.handler.bbs.CommunityBoardManager;
import l2trunk.gameserver.handler.bbs.ICommunityBoardHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ShowBoard;
import l2trunk.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public final class StatManager implements ScriptFile, ICommunityBoardHandler {
    private static final Logger _log = LoggerFactory.getLogger(StatManager.class);

    /**
     * Имплементированые методы скриптов
     */
    @Override
    public void onLoad() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            _log.info("CommunityBoard: Statistic service loaded.");
            CommunityBoardManager.registerHandler(this);
        }
    }

    @Override
    public void onReload() {
        if (Config.COMMUNITYBOARD_ENABLED)
            CommunityBoardManager.removeHandler(this);
    }

    /**
     * Регистратор команд
     */
    @Override
    public List<String> getBypassCommands() {
        return List.of("_bbsstat",
                "_bbsstat_pk",
                "_bbsstat_online",
                "_bbsstat_clan",
                "_bbsstat_castle");
    }

    /**
     * Обработчик команд класса
     */
    @Override
    public void onBypassCommand(Player player, String command) {
        if (command.equals("_bbsstat")) {
            showPvp(player);
        } else if (command.startsWith("_bbsstat_pk")) {
            showPK(player);
        } else if (command.startsWith("_bbsstat_online")) {
            showOnline(player);
        } else if (command.startsWith("_bbsstat_clan")) {
            showClan(player);
        } else if (command.startsWith("_bbsstat_castle")) {
            showCastle(player);
        } else
            ShowBoard.separateAndSend("<html><body><br><br><center>In community stats function: " + command + " not " +
                    "implemented yet</center><br><br></body></html>", player);
    }

    /**
     * Вызываем показ текущего списка лучших 20 плееров по ПВП показателю
     * Осуществляем внутри-классовый конект и чекинг таблицы (по приведённым параметрам)
     *
     * @param player
     */
    private void showPvp(Player player) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * FROM characters WHERE accesslevel = '0' ORDER BY pvpkills DESC LIMIT 20;");
             ResultSet rs = statement.executeQuery()) {

            StringBuilder html = new StringBuilder();
            html.append("<table width=440>");
            while (rs.next()) {
                CBStatMan tp = new CBStatMan();
                tp.PlayerId = rs.getInt("obj_Id");
                tp.ChName = rs.getString("char_name");
                tp.ChSex = rs.getInt("sex");
                tp.ChGameTime = rs.getInt("onlinetime");
                tp.ChPk = rs.getInt("pkkills");
                tp.ChPvP = rs.getInt("pvpkills");
                tp.ChOnOff = rs.getInt("online");

                String sex = tp.ChSex == 1 ? "F" : "М";
                String color;
                String OnOff;
                if (tp.ChOnOff == 1) {
                    OnOff = "Online.";
                    color = "00CC00";
                } else {
                    OnOff = "Offline.";
                    color = "D70000";
                }
                html.append("<tr>");
                html.append("<td width=150 align=\"center\">").append(tp.ChName).append("</td>");
                html.append("<td width=50 align=\"center\">" + sex + "</td>");
                html.append("<td width=80 align=\"center\">" + OnlineTime(tp.ChGameTime) + "</td>");
                html.append("<td width=50 align=\"center\">" + tp.ChPk + "</td>");
                html.append("<td width=50 align=\"center\"><font color=00CC00>" + tp.ChPvP + "</font></td>");
                html.append("<td width=80 align=\"center\"><font color=" + color + ">" + OnOff + "</font></td>");
                html.append("</tr>");
            }
            html.append("</table>");

            String content = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "pages/stats/stats_top_pvp.htm", player);

            content = content.replace("%stats_top_pvp%", html.toString());
            ShowBoard.separateAndSend(content, player);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showPK(Player player) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * FROM characters WHERE accesslevel = '0' ORDER BY pkkills DESC LIMIT 20;");
             ResultSet rs = statement.executeQuery()) {

            StringBuilder html = new StringBuilder();
            html.append("<table width=440>");
            while (rs.next()) {
                CBStatMan tp = new CBStatMan();
                tp.PlayerId = rs.getInt("obj_Id");
                tp.ChName = rs.getString("char_name");
                tp.ChSex = rs.getInt("sex");
                tp.ChGameTime = rs.getInt("onlinetime");
                tp.ChPk = rs.getInt("pkkills");
                tp.ChPvP = rs.getInt("pvpkills");
                tp.ChOnOff = rs.getInt("online");

                String sex = tp.ChSex == 1 ? "F" : "M";
                String color;
                String OnOff;
                if (tp.ChOnOff == 1) {
                    OnOff = "Online.";
                    color = "00CC00";
                } else {
                    OnOff = "Offline.";
                    color = "D70000";
                }
                html.append("<tr>");
                html.append("<td width=150 align=\"center\">").append(tp.ChName).append("</td>");
                html.append("<td width=50 align=\"center\">" + sex + "</td>");
                html.append("<td width=80 align=\"center\">" + OnlineTime(tp.ChGameTime) + "</td>");
                html.append("<td width=50 align=\"center\"><font color=00CC00>" + tp.ChPk + "</font></td>");
                html.append("<td width=50 align=\"center\">" + tp.ChPvP + "</td>");
                html.append("<td width=80 align=\"center\"><font color=" + color + ">" + OnOff + "</font></td>");
                html.append("</tr>");
            }
            html.append("</table>");
            String content = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "pages/stats/stats_top_pk.htm", player);
            content = content.replace("%stats_top_pk%", html.toString());
            ShowBoard.separateAndSend(content, player);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showOnline(Player player) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * FROM characters WHERE accesslevel = '0' ORDER BY onlinetime DESC LIMIT 20;");
             ResultSet rs = statement.executeQuery()) {

            StringBuilder html = new StringBuilder();
            html.append("<table width=440>");
            while (rs.next()) {
                CBStatMan tp = new CBStatMan();
                tp.PlayerId = rs.getInt("obj_Id");
                tp.ChName = rs.getString("char_name");
                tp.ChSex = rs.getInt("sex");
                tp.ChGameTime = rs.getInt("onlinetime");
                tp.ChPk = rs.getInt("pkkills");
                tp.ChPvP = rs.getInt("pvpkills");
                tp.ChOnOff = rs.getInt("online");

                String sex = tp.ChSex == 1 ? "F" : "М";
                String color;
                String OnOff;
                if (tp.ChOnOff == 1) {
                    OnOff = "Online.";
                    color = "00CC00";
                } else {
                    OnOff = "Offline.";
                    color = "D70000";
                }
                html.append("<tr>");
                html.append("<td width=150 align=\"center\">" + tp.ChName + "</td>");
                html.append("<td width=50 align=\"center\">" + sex + "</td>");
                html.append("<td width=80 align=\"center\"><font color=00CC00>" + OnlineTime(tp.ChGameTime) + "</font></td>");
                html.append("<td width=50 align=\"center\">" + tp.ChPk + "</td>");
                html.append("<td width=50 align=\"center\">" + tp.ChPvP + "</td>");
                html.append("<td width=80 align=\"center\"><font color=" + color + ">" + OnOff + "</font></td>");
                html.append("</tr>");
            }
            html.append("</table>");

            String content = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "pages/stats/stats_online.htm", player);
            content = content.replace("%stats_online%", html.toString());
            ShowBoard.separateAndSend(content, player);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showCastle(Player player) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT castle.name, castle.id, castle.tax_percent, castle.siege_date as siegeDate, clan_subpledges.name as clan_name, clan_data.clan_id " +
                     "FROM castle " +
                     "LEFT JOIN clan_data ON clan_data.hasCastle=castle.id " +
                     "LEFT JOIN clan_subpledges ON clan_subpledges.clan_id=clan_data.clan_id AND clan_subpledges.type='0';");
             ResultSet rs = statement.executeQuery()) {
            StringBuilder html = new StringBuilder();
            html.append("<table width=460>");
            String color;

            while (rs.next()) {
                CBStatMan tp = new CBStatMan();
                tp.Owner = rs.getString("clan_name");
                tp.NameCastl = rs.getString("name");
                tp.Percent = rs.getString("tax_Percent") + "%";
                tp.siegeDate = sdf.format(new Date(rs.getLong("siegeDate")));

                if (tp.Owner != null)
                    color = "00CC00";
                else {
                    color = "FFFFFF";
                    tp.Owner = "no owner";
                }
                html.append("<tr>");
                html.append("<td width=160 align=\"center\">" + tp.NameCastl + "</td>");
                html.append("<td width=110 align=\"center\">" + tp.Percent + "</td>");
                html.append("<td width=200 align=\"center\"><font color=" + color + ">" + tp.Owner + "</font></td>");
                html.append("<td width=160 align=\"center\">" + tp.siegeDate + "</td>");
                html.append("</tr>");
            }
            html.append("</table>");
            String content = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "pages/stats/stats_castle.htm", player);
            content = content.replace("%stats_castle%", html.toString());
            ShowBoard.separateAndSend(content, player);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showClan(Player player) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT clan_subpledges.name,clan_data.clan_level,clan_data.reputation_score,clan_data.hasCastle,ally_data.ally_name FROM clan_data LEFT JOIN ally_data ON clan_data.ally_id = ally_data.ally_id, clan_subpledges WHERE clan_data.clan_level>0 AND clan_subpledges.clan_id=clan_data.clan_id AND clan_subpledges.type='0' order by clan_data.clan_level desc limit 20;");
             ResultSet rs = statement.executeQuery()) {

            StringBuilder html = new StringBuilder();
            html.append("<table width=440>");
            while (rs.next()) {
                CBStatMan tp = new CBStatMan();
                tp.ClanName = rs.getString("name");
                tp.AllyName = rs.getString("ally_name");
                tp.ReputationClan = rs.getInt("reputation_score");
                tp.ClanLevel = rs.getInt("clan_level");
                tp.hasCastle = rs.getInt("hasCastle");
                String hasCastle;
                String castleColor;

                switch (tp.hasCastle) {
                    case 1:
                        hasCastle = "Gludio";
                        castleColor = "00CC00";
                        break;
                    case 2:
                        hasCastle = "Dion";
                        castleColor = "00CC00";
                        break;
                    case 3:
                        hasCastle = "Giran";
                        castleColor = "00CC00";
                        break;
                    case 4:
                        hasCastle = "Oren";
                        castleColor = "00CC00";
                        break;
                    case 5:
                        hasCastle = "Aden";
                        castleColor = "00CC00";
                        break;
                    case 6:
                        hasCastle = "Innadril";
                        castleColor = "00CC00";
                        break;
                    case 7:
                        hasCastle = "Goddard";
                        castleColor = "00CC00";
                        break;
                    case 8:
                        hasCastle = "Rune";
                        castleColor = "00CC00";
                        break;
                    case 9:
                        hasCastle = "Schuttgart";
                        castleColor = "00CC00";
                        break;
                    default:
                        hasCastle = "None";
                        castleColor = "D70000";
                        break;
                }
                html.append("<tr>");
                html.append("<td width=140>" + tp.ClanName + "</td>");
                if (tp.AllyName != null)
                    html.append("<td width=140>" + tp.AllyName + "</td>");
                else
                    html.append("<td width=140>No alliance</td>");
                html.append("<td width=100 align=\"center\">").append(tp.ReputationClan).append("</td>");
                html.append("<td width=80 align=\"center\">").append(tp.ClanLevel).append("</td>");
                html.append("<td width=100 align=\"center\"><font color=" + castleColor + ">" + hasCastle + "</font></td>");
                html.append("</tr>");
            }
            html.append("</table>");
            String content = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "pages/stats/stats_clan.htm", player);
            content = content.replace("%stats_clan%", html.toString());
            ShowBoard.separateAndSend(content, player);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Вызываем показ текущего списка лучших 20 плееров по PK показателю
     * Осуществляем внутри-классовый конект и чекинг таблицы (по приведённым параметрам)
     */
    private String OnlineTime(int time) {
        long onlinetimeH;
        long onlinetimeM;
        if (time / 60 / 60 - 0.5 <= 0)
            onlinetimeH = 0;
        else
            onlinetimeH = Math.round(time / 60. / 60 - 0.5);
        onlinetimeM = Math.round((time / 60. / 60 - onlinetimeH) * 60);
        return "" + onlinetimeH + " h. " + onlinetimeM + " m.";
    }

    /**
     * Не используемый, но вызываемый метод имплемента
     */
    @Override
    public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
    }

    /**
     * Класс общих пер-х
     */
    class CBStatMan {
        public Object id2;
        public int id;
        int PlayerId = 0; // obj_id Char
        String ChName = ""; // Char name
        int ChGameTime = 0; // Time in game
        int ChPk = 0; // Char PK
        int ChPvP = 0; // Char PVP
        int ChOnOff = 0; // Char offline/online cure time
        int ChSex = 0; // Char sex
        String NameCastl;
        Object siegeDate;
        String Percent;
        int ClanLevel;
        int hasCastle;
        int ReputationClan;
        String AllyName;
        String ClanName;
        String Owner;
    }
}