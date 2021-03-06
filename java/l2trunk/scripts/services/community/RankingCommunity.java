package l2trunk.scripts.services.community;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.handler.bbs.CommunityBoardManager;
import l2trunk.gameserver.handler.bbs.ICommunityBoardHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.Say2;
import l2trunk.gameserver.network.serverpackets.ShowBoard;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.tables.ClanTable;
import l2trunk.gameserver.taskmanager.AutoImageSenderManager;
import l2trunk.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public final class RankingCommunity implements ScriptFile, ICommunityBoardHandler {
    private static final Logger _log = LoggerFactory.getLogger(RankingCommunity.class);
    private static final RankingManager RankingManagerStats = new RankingManager();
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private final int time_update = 60;
    private long update = System.currentTimeMillis() / 1000;

    private static String time(long time) {
        return TIME_FORMAT.format(new Date(time * 1000));
    }

    @Override
    public void onLoad() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            selectRankingPK();
            selectRankingPVP();
            selectRankingRK();
            selectRankingCIS();
            selectRankingCIP();
            selectRankingAdena();
            _log.info("Ranking in the commynity board has been updated.");
            CommunityBoardManager.registerHandler(this);
        }
    }

    @Override
    public void onReload() {
        if (Config.COMMUNITYBOARD_ENABLED)
            CommunityBoardManager.removeHandler(this);
    }

    @Override
    public List<String> getBypassCommands() {
        return List.of("_bbsloc", "_bbsranking");
    }

    @Override
    public void onBypassCommand(Player player, String bypass) {
        String html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "off.htm", player);
        ShowBoard.separateAndSend(html, player);

        //Checking if all required images were sent to the getPlayer, if not - not allowing to pass
        if (!AutoImageSenderManager.wereAllImagesSent(player)) {
            player.sendPacket(new Say2(player.objectId(), ChatType.CRITICAL_ANNOUNCE, "CB", "Community wasn't loaded yet, try again in few seconds."));
            return;
        }

        player.setSessionVar("add_fav", null);
        if (update + time_update * 60 < System.currentTimeMillis() / 1000) {
            selectRankingPK();
            selectRankingPVP();
            selectRankingRK();
            selectRankingCIS();
            selectRankingCIP();
            selectRankingAdena();
            update = System.currentTimeMillis() / 1000;
            _log.info("Ranking in the commynity board has been updated.");
        }

        if (bypass.equals("_bbsloc") || bypass.equals("_bbsranking:pk"))
            show(player, 1);
        else if (bypass.equals("_bbsranking:pvp"))
            show(player, 2);
        else if (bypass.equals("_bbsranking:rk"))
            show(player, 3);
        else if (bypass.equals("_bbsranking:cis"))
            show(player, 4);
        else if (bypass.equals("_bbsranking:cip"))
            show(player, 5);
        else if (bypass.equals("_bbsranking:adena") && player.getAccessLevel() > 0)
            show(player, 6);
    }

    private void show(Player player, int page) {
        int number = 0;
        String html;

        if (page == 1) {
            html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "ranking/pk.htm", player);
            while (number < 10) {
                if (RankingManagerStats.RankingPkName[number] != null) {
                    html = html.replace("<?name_" + number + "?>", RankingManagerStats.RankingPkName[number]);
                    html = html.replace("<?clan_" + number + "?>", RankingManagerStats.RankingPkClan[number] == null ? "<font color=\"B59A75\">No Clan</font>" : RankingManagerStats.RankingPkClan[number]);
                    html = html.replace("<?class_" + number + "?>", Util.getFullClassName(RankingManagerStats.RankingPkClass[number]));
                    html = html.replace("<?on_" + number + "?>", RankingManagerStats.RankingPkOn[number] == 1 ? "<font color=\"66FF33\">Yes</font>" : "<font color=\"B59A75\">No</font>");
                    html = html.replace("<?count_" + number + "?>", "" +RankingManagerStats.RankingPk[number]);
                } else {
                    html = html.replace("<?name_" + number + "?>", "...");
                    html = html.replace("<?clan_" + number + "?>", "...");
                    html = html.replace("<?class_" + number + "?>", "...");
                    html = html.replace("<?on_" + number + "?>", "...");
                    html = html.replace("<?count_" + number + "?>", "...");
                }

                number++;
            }
        } else if (page == 2) {
            html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "ranking/pvp.htm", player);
            while (number < 10) {
                if (RankingManagerStats.RankingPvPName[number] != null) {
                    html = html.replace("<?name_" + number + "?>", RankingManagerStats.RankingPvPName[number]);
                    html = html.replace("<?clan_" + number + "?>", RankingManagerStats.RankingPvPClan[number] == null ? "<font color=\"B59A75\">No Clan</font>" : RankingManagerStats.RankingPvPClan[number]);
                    html = html.replace("<?class_" + number + "?>", Util.getFullClassName(RankingManagerStats.RankingPvPClass[number]));
                    html = html.replace("<?on_" + number + "?>", RankingManagerStats.RankingPvPOn[number] == 1 ? "<font color=\"66FF33\">Yes</font>" : "<font color=\"B59A75\">No</font>");
                    html = html.replace("<?count_" + number + "?>", ""+RankingManagerStats.RankingPvP[number]);
                } else {
                    html = html.replace("<?name_" + number + "?>", "...");
                    html = html.replace("<?clan_" + number + "?>", "...");
                    html = html.replace("<?class_" + number + "?>", "...");
                    html = html.replace("<?on_" + number + "?>", "...");
                    html = html.replace("<?count_" + number + "?>", "...");
                }
                number++;
            }
        } else if (page == 3) {
            html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "ranking/rk.htm", player);
            while (number < 10) {
                if (RankingManagerStats.RankingRaidName[number] != null) {
                    html = html.replace("<?name_" + number + "?>", RankingManagerStats.RankingRaidName[number]);
                    html = html.replace("<?clan_" + number + "?>", RankingManagerStats.RankingRaidClan[number] == null ? "<font color=\"B59A75\">No Clan</font>" : RankingManagerStats.RankingRaidClan[number]);
                    html = html.replace("<?class_" + number + "?>", Util.getFullClassName(RankingManagerStats.RankingRaidClass[number]));
                    html = html.replace("<?on_" + number + "?>", RankingManagerStats.RankingRaidOn[number] == 1 ? "<font color=\"66FF33\">Yes</font>" : "<font color=\"B59A75\">No</font>");
                    html = html.replace("<?count_" + number + "?>", ""+ RankingManagerStats.RankingRaid[number]);
                } else {
                    html = html.replace("<?name_" + number + "?>", "...");
                    html = html.replace("<?clan_" + number + "?>", "...");
                    html = html.replace("<?class_" + number + "?>", "...");
                    html = html.replace("<?on_" + number + "?>", "...");
                    html = html.replace("<?count_" + number + "?>", "...");
                }
                number++;
            }
        } else if (page == 4) {
            html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "ranking/cis.htm", player);
            while (number < 10) {
                if (RankingManagerStats.RankingInstanceSoloName[number] != null) {
                    html = html.replace("<?name_" + number + "?>", RankingManagerStats.RankingInstanceSoloName[number]);
                    html = html.replace("<?clan_" + number + "?>", RankingManagerStats.RankingInstanceSoloClan[number] == null ? "<font color=\"B59A75\">No Clan</font>" : RankingManagerStats.RankingInstanceSoloClan[number]);
                    html = html.replace("<?class_" + number + "?>", Util.getFullClassName(RankingManagerStats.RankingInstanceSoloClass[number]));
                    html = html.replace("<?on_" + number + "?>", RankingManagerStats.RankingInstanceSoloOn[number] == 1 ? "<font color=\"66FF33\">Yes</font>" : "<font color=\"B59A75\">No</font>");
                    html = html.replace("<?count_" + number + "?>", Integer.toString(RankingManagerStats.RankingInstanceSolo[number]));
                } else {
                    html = html.replace("<?name_" + number + "?>", "...");
                    html = html.replace("<?clan_" + number + "?>", "...");
                    html = html.replace("<?class_" + number + "?>", "...");
                    html = html.replace("<?on_" + number + "?>", "...");
                    html = html.replace("<?count_" + number + "?>", "...");
                }
                number++;
            }
        } else if (page == 5) {
            html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "ranking/cip.htm", player);
            while (number < 10) {
                if (RankingManagerStats.RankingInstancePartyName[number] != null) {
                    html = html.replace("<?name_" + number + "?>", RankingManagerStats.RankingInstancePartyName[number]);
                    html = html.replace("<?clan_" + number + "?>", RankingManagerStats.RankingInstancePartyClan[number] == null ? "<font color=\"B59A75\">No Clan</font>" : RankingManagerStats.RankingInstancePartyClan[number]);
                    html = html.replace("<?class_" + number + "?>", Util.getFullClassName(RankingManagerStats.RankingInstancePartyClass[number]));
                    html = html.replace("<?on_" + number + "?>", RankingManagerStats.RankingInstancePartyOn[number] == 1 ? "<font color=\"66FF33\">Yes</font>" : "<font color=\"B59A75\">No</font>");
                    html = html.replace("<?count_" + number + "?>", Integer.toString(RankingManagerStats.RankingInstanceParty[number]));
                } else {
                    html = html.replace("<?name_" + number + "?>", "...");
                    html = html.replace("<?clan_" + number + "?>", "...");
                    html = html.replace("<?class_" + number + "?>", "...");
                    html = html.replace("<?on_" + number + "?>", "...");
                    html = html.replace("<?count_" + number + "?>", "...");
                }
                number++;
            }
        } else if (page == 6) {
            html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "ranking/adena.htm", player);
            while (number < 10) {
                if (RankingManagerStats.RankingAdenaName[number] != null) {
                    html = html.replace("<?name_" + number + "?>", RankingManagerStats.RankingAdenaName[number]);
                    html = html.replace("<?clan_" + number + "?>", RankingManagerStats.RankingAdenaClan[number] == null ? "<font color=\"B59A75\">No Clan</font>" : RankingManagerStats.RankingAdenaClan[number]);
                    html = html.replace("<?class_" + number + "?>", Util.getFullClassName(RankingManagerStats.RankingAdenaClass[number]));
                    html = html.replace("<?on_" + number + "?>", RankingManagerStats.RankingAdenaOn[number] == 1 ? "<font color=\"66FF33\">Yes</font>" : "<font color=\"B59A75\">No</font>");
                    html = html.replace("<?count_" + number + "?>", Long.toString(RankingManagerStats.RankingAdena[number]));
                } else {
                    html = html.replace("<?name_" + number + "?>", "...");
                    html = html.replace("<?clan_" + number + "?>", "...");
                    html = html.replace("<?class_" + number + "?>", "...");
                    html = html.replace("<?on_" + number + "?>", "...");
                    html = html.replace("<?count_" + number + "?>", "...");
                }
                number++;
            }
        } else {
            _log.warn("Unknown page: " + page + " - " + player.getName());
            return;
        }

        html = html.replace("<?update?>", String.valueOf(time_update));
        html = html.replace("<?last_update?>", time(update));
        html = html.replace("<?ranking_menu?>", HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "ranking/menu.htm", player));
        ShowBoard.separateAndSend(html, player);
    }

    private void selectRankingPVP() {
        int number = 0;

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT char_name, class_id, clanid, online, pvpkills FROM characters AS c LEFT JOIN character_subclasses AS cs ON (c.obj_Id=cs.char_obj_id) WHERE cs.isBase=1 AND accesslevel = 0 ORDER BY pvpkills DESC LIMIT " + 10);
             ResultSet rset = statement.executeQuery()) {

            while (rset.next()) {
                if (!rset.getString("char_name").isEmpty()) {
                    RankingManagerStats.RankingPvPName[number] = rset.getString("char_name");
                    int clan_id = rset.getInt("clanid");
                    Clan clan = clan_id == 0 ? null : ClanTable.INSTANCE.getClan(clan_id);
                    RankingManagerStats.RankingPvPClan[number] = clan == null ? null : clan.getName();
                    RankingManagerStats.RankingPvPClass[number] = rset.getInt("class_id");
                    RankingManagerStats.RankingPvPOn[number] = rset.getInt("online");
                    RankingManagerStats.RankingPvP[number] = rset.getInt("pvpkills");
                } else {
                    RankingManagerStats.RankingPvPName[number] = null;
                    RankingManagerStats.RankingPvPClan[number] = null;
                    RankingManagerStats.RankingPvPClass[number] = 0;
                    RankingManagerStats.RankingPvPOn[number] = 0;
                    RankingManagerStats.RankingPvP[number] = 0;
                }
                number++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectRankingPK() {
        int number = 0;

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT char_name, class_id, clanid, online, pkkills FROM characters AS c LEFT JOIN character_subclasses AS cs ON (c.obj_Id=cs.char_obj_id) WHERE cs.isBase=1 AND accesslevel = 0 ORDER BY pkkills DESC LIMIT 10" );
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                if (!rset.getString("char_name").isEmpty()) {
                    RankingManagerStats.RankingPkName[number] = rset.getString("char_name");
                    int clan_id = rset.getInt("clanid");
                    Clan clan = clan_id == 0 ? null : ClanTable.INSTANCE.getClan(clan_id);
                    RankingManagerStats.RankingPkClan[number] = clan == null ? null : clan.getName();
                    RankingManagerStats.RankingPkClass[number] = rset.getInt("class_id");
                    RankingManagerStats.RankingPkOn[number] = rset.getInt("online");
                    RankingManagerStats.RankingPk[number] = rset.getInt("pkkills");
                } else {
                    RankingManagerStats.RankingPkName[number] = null;
                    RankingManagerStats.RankingPkClan[number] = null;
                    RankingManagerStats.RankingPkClass[number] = 0;
                    RankingManagerStats.RankingPkOn[number] = 0;
                    RankingManagerStats.RankingPk[number] = 0;
                }
                number++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectRankingRK() {
        int number = 0;

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT char_name, class_id, clanid, online, raidkills FROM characters AS c LEFT JOIN character_subclasses AS cs ON (c.obj_Id=cs.char_obj_id) WHERE cs.isBase=1 AND accesslevel = 0 ORDER BY raidkills DESC LIMIT " + 10);
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                if (!rset.getString("char_name").isEmpty()) {
                    RankingManagerStats.RankingRaidName[number] = rset.getString("char_name");
                    int clan_id = rset.getInt("clanid");
                    Clan clan = clan_id == 0 ? null : ClanTable.INSTANCE.getClan(clan_id);
                    RankingManagerStats.RankingRaidClan[number] = clan == null ? null : clan.getName();
                    RankingManagerStats.RankingRaidClass[number] = rset.getInt("class_id");
                    RankingManagerStats.RankingRaidOn[number] = rset.getInt("online");
                    RankingManagerStats.RankingRaid[number] = rset.getInt("raidkills");
                } else {
                    RankingManagerStats.RankingRaidName[number] = null;
                    RankingManagerStats.RankingRaidClan[number] = null;
                    RankingManagerStats.RankingRaidClass[number] = 0;
                    RankingManagerStats.RankingRaidOn[number] = 0;
                    RankingManagerStats.RankingRaid[number] = 0;
                }
                number++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectRankingCIS() {
        int number = 0;

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT char_name, class_id, clanid, online, soloinstance FROM characters AS c LEFT JOIN character_subclasses AS cs ON (c.obj_Id=cs.char_obj_id) WHERE cs.isBase=1 AND accesslevel = 0  ORDER BY soloinstance DESC LIMIT " + 10);
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                if (!rset.getString("char_name").isEmpty()) {
                    RankingManagerStats.RankingInstanceSoloName[number] = rset.getString("char_name");
                    int clan_id = rset.getInt("clanid");
                    Clan clan = clan_id == 0 ? null : ClanTable.INSTANCE.getClan(clan_id);
                    RankingManagerStats.RankingInstanceSoloClan[number] = clan == null ? null : clan.getName();
                    RankingManagerStats.RankingInstanceSoloClass[number] = rset.getInt("class_id");
                    RankingManagerStats.RankingInstanceSoloOn[number] = rset.getInt("online");
                    RankingManagerStats.RankingInstanceSolo[number] = rset.getInt("soloinstance");
                } else {
                    RankingManagerStats.RankingInstanceSoloName[number] = null;
                    RankingManagerStats.RankingInstanceSoloClan[number] = null;
                    RankingManagerStats.RankingInstanceSoloClass[number] = 0;
                    RankingManagerStats.RankingInstanceSoloOn[number] = 0;
                    RankingManagerStats.RankingInstanceSolo[number] = 0;
                }
                number++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectRankingCIP() {
        int number = 0;
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT char_name, class_id, clanid, online, partyinstance FROM characters AS c LEFT JOIN character_subclasses AS cs ON (c.obj_Id=cs.char_obj_id) WHERE cs.isBase=1 AND accesslevel = 0  ORDER BY partyinstance DESC LIMIT " + 10);
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                if (!rset.getString("char_name").isEmpty()) {
                    RankingManagerStats.RankingInstancePartyName[number] = rset.getString("char_name");
                    int clan_id = rset.getInt("clanid");
                    Clan clan = clan_id == 0 ? null : ClanTable.INSTANCE.getClan(clan_id);
                    RankingManagerStats.RankingInstancePartyClan[number] = clan == null ? null : clan.getName();
                    RankingManagerStats.RankingInstancePartyClass[number] = rset.getInt("class_id");
                    RankingManagerStats.RankingInstancePartyOn[number] = rset.getInt("online");
                    RankingManagerStats.RankingInstanceParty[number] = rset.getInt("partyinstance");
                } else {
                    RankingManagerStats.RankingInstancePartyName[number] = null;
                    RankingManagerStats.RankingInstancePartyClan[number] = null;
                    RankingManagerStats.RankingInstancePartyClass[number] = 0;
                    RankingManagerStats.RankingInstancePartyOn[number] = 0;
                    RankingManagerStats.RankingInstanceParty[number] = 0;
                }
                number++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectRankingAdena() {
        int number = 0;

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT char_name, class_id, clanid, online, it.count FROM characters AS c LEFT JOIN character_subclasses AS cs ON (c.obj_Id=cs.char_obj_id) JOIN items AS it ON (c.obj_Id=it.owner_id) WHERE cs.isBase=1 AND it.item_id=57 AND accesslevel = 0 ORDER BY it.count DESC LIMIT " + 10);
             ResultSet rset = statement.executeQuery()) {

            while (rset.next()) {
                if (!rset.getString("char_name").isEmpty()) {
                    RankingManagerStats.RankingAdenaName[number] = rset.getString("char_name");
                    int clan_id = rset.getInt("clanid");
                    Clan clan = clan_id == 0 ? null : ClanTable.INSTANCE.getClan(clan_id);
                    RankingManagerStats.RankingAdenaClan[number] = clan == null ? null : clan.getName();
                    RankingManagerStats.RankingAdenaClass[number] = rset.getInt("class_id");
                    RankingManagerStats.RankingAdenaOn[number] = rset.getInt("online");
                    RankingManagerStats.RankingAdena[number] = rset.getLong("count");
                } else {
                    RankingManagerStats.RankingAdenaName[number] = null;
                    RankingManagerStats.RankingAdenaClan[number] = null;
                    RankingManagerStats.RankingAdenaClass[number] = 0;
                    RankingManagerStats.RankingAdenaOn[number] = 0;
                    RankingManagerStats.RankingAdena[number] = 0;
                }
                number++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private static class RankingManager {
        private final String[] RankingPvPName = new String[10];
        private final String[] RankingPvPClan = new String[10];
        private final int[] RankingPvPClass = new int[10];
        private final int[] RankingPvPOn = new int[10];
        private final int[] RankingPvP = new int[10];

        private final String[] RankingPkName = new String[10];
        private final String[] RankingPkClan = new String[10];
        private final int[] RankingPkClass = new int[10];
        private final int[] RankingPkOn = new int[10];
        private final int[] RankingPk = new int[10];

        private final String[] RankingRaidName = new String[10];
        private final String[] RankingRaidClan = new String[10];
        private final int[] RankingRaidClass = new int[10];
        private final int[] RankingRaidOn = new int[10];
        private final int[] RankingRaid = new int[10];

        private final String[] RankingInstanceSoloName = new String[10];
        private final String[] RankingInstanceSoloClan = new String[10];
        private final int[] RankingInstanceSoloClass = new int[10];
        private final int[] RankingInstanceSoloOn = new int[10];
        private final int[] RankingInstanceSolo = new int[10];

        private final String[] RankingInstancePartyName = new String[10];
        private final String[] RankingInstancePartyClan = new String[10];
        private final int[] RankingInstancePartyClass = new int[10];
        private final int[] RankingInstancePartyOn = new int[10];
        private final int[] RankingInstanceParty = new int[10];

        private final String[] RankingAdenaName = new String[10];
        private final String[] RankingAdenaClan = new String[10];
        private final int[] RankingAdenaClass = new int[10];
        private final int[] RankingAdenaOn = new int[10];
        private final long[] RankingAdena = new long[10];
    }
}
