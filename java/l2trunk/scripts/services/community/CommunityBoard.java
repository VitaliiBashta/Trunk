package l2trunk.scripts.services.community;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.GameServer;
import l2trunk.gameserver.GameTimeController;
import l2trunk.gameserver.cache.ImagesCache;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.BuyListHolder;
import l2trunk.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2trunk.gameserver.data.xml.holder.MultiSellHolder;
import l2trunk.gameserver.handler.bbs.CommunityBoardManager;
import l2trunk.gameserver.handler.bbs.ICommunityBoardHandler;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.scripts.Scripts;
import l2trunk.gameserver.tables.ClanTable;
import l2trunk.gameserver.taskmanager.AutoImageSenderManager;
import l2trunk.gameserver.utils.TimeUtils;
import l2trunk.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import static l2trunk.commons.lang.NumberUtils.toInt;


public final class CommunityBoard implements ScriptFile, ICommunityBoardHandler {
    private static final Logger _log = LoggerFactory.getLogger(CommunityBoard.class);
    private static final SimpleDateFormat dataDateFormat = new SimpleDateFormat("hh:mm dd.MM.yyyy");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    private static String uptime() {
        return dataDateFormat.format(GameServer.server_started);
    }

    private static String time() {
        return TIME_FORMAT.format(new Date(System.currentTimeMillis()));
    }

    public static String getOnlineTime(Player player) {
        long total = player.getOnlineTime() + (System.currentTimeMillis() / 1000 - player.getOnlineBeginTime());

        long days = total / (60 * 60 * 24) % 7;
        long hours = (total - TimeUnit.DAYS.toSeconds(days)) / (60 * 60) % 24;
        long minutes = (total - TimeUnit.DAYS.toSeconds(days) - TimeUnit.HOURS.toSeconds(hours)) / 60;

        if (days >= 1)
            return days + " d. " + hours + " h. " + minutes + " min";
        else
            return hours + " hours " + player.getOnlineTime();
    }

    @Override
    public void onLoad() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            _log.info("CommunityBoard: service loaded.");
            CommunityBoardManager.registerHandler(this);
        }
    }

    @Override
    public void onReload() {
        if (Config.COMMUNITYBOARD_ENABLED)
            CommunityBoardManager.removeHandler(this);
    }

    @Override
    public void onShutdown() {

    }

    @Override
    public List<String> getBypassCommands() {
        return Arrays.asList("_bbshome", "_bbsmultisell", "_bbssell", "_bbsaugment", "_bbsdeaugment", "_bbspage", "_bbsfile", "_bbsscripts");
    }

    @Override
    public void onBypassCommand(Player player, String bypass) {
        StringTokenizer st = new StringTokenizer(bypass, "_");
        String cmd = st.nextToken();
        String html = "";
        if ("bbshome".equals(cmd)) {
            //Checking if all required images were sent to the player, if not - not allowing to pass
            if (!AutoImageSenderManager.wereAllImagesSent(player)) {
                player.sendPacket(new Say2(player.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "CB", "Community wasn't loaded yet, try again in few seconds."));
                return;
            }
            StringTokenizer p = new StringTokenizer(Config.BBS_DEFAULT, "_");
            String dafault = p.nextToken();
            if (dafault.equals(cmd)) {
                html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "pages/index.htm", player);
                html = html.replaceFirst("%nick%", player.getName());
                html = html.replace("<?fav_count?>", "0");
                html = html.replace("<?clan_count?>", String.valueOf(ClanTable.INSTANCE.getClans().size()));
                html = html.replace("<?market_count?>", String.valueOf(CommunityBoardManager.getIntProperty("col_count")));
                html = html.replace("<?player_name?>", String.valueOf(player.getName()));
                html = html.replace("<?player_class?>", String.valueOf(Util.getFullClassName(player.getClassId().getId())));
                html = html.replace("<?player_level?>", String.valueOf(player.getLevel()));
                html = html.replace("<?player_clan?>", String.valueOf(player.getClan() != null ? player.getClan().getName() : "<font color=\"FF0000\">No</font>"));
                html = html.replace("<?player_noobless?>", String.valueOf(player.isNoble() ? "<font color=\"18FF00\">Yes</font>" : "<font color=\"FF0000\">No</font>"));
                html = html.replace("<?online_time?>", TimeUtils.formatTime((int) player.getOnlineTime() / 1000, false));
                html = html.replace("<?player_ip?>", String.valueOf(player.getIP()));
                html = html.replace("<?server_uptime?>", String.valueOf(uptime()));

                html = html.replace("<?time?>", String.valueOf(time()));
                html = html.replace("<?online?>", online());
                ImagesCache.sendUsedImages(html, player);
            } else {
                onBypassCommand(player, Config.BBS_DEFAULT);
                return;
            }
        } else if (bypass.startsWith("_bbspage")) {
            String[] b = bypass.split(":");
            String page = b[1];
            html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "pages/" + page + ".htm", player);
            ImagesCache.sendUsedImages(html, player);

            if (bypass.equals("_bbspage:information")) {
                html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "pages/information.htm", player);
                html = html.replaceFirst("%nick%", String.valueOf(player.getName()));
                html = html.replaceFirst("%prof%", String.valueOf(player.getActiveClass().toStringCB()));
                html = html.replaceFirst("%lvl%", String.valueOf(player.getLevel()));
                html = html.replaceFirst("%clan%", player.getClan() != null ? String.valueOf(player.getClan().getName()) : "<font color=\"FF0000\">No</font>");
                html = html.replaceFirst("%noobl%", player.isNoble() ? String.valueOf("Yes") : "<font color=\"FF0000\">Need Subclass lvl 76</font>");
                html = html.replaceFirst("%time%", String.valueOf(player.getHoursInGames()).concat(" hour(s)"));
                html = html.replaceFirst("%servhwid%", "<a action=\"bypass -h user_lock\">Lock</a>");
                html = html.replaceFirst("%servip%", "<a action=\"bypass -h user_lock\">Lock</a>");
                html = html.replaceFirst("%ip%", player.getIP());
                html = html.replaceFirst("%mytime%", getTimeInServer(player));
                html = html.replaceFirst("%online%", String.valueOf(GameObjectsStorage.getAllPlayersCount()));
            } else if (bypass.equals("_bbspage:HowToDonate")) {
                html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "pages/HowToDonate.htm", player);
                html = html.replaceFirst("%nick%", String.valueOf(player.getName()));

            }

            // Synerge - Remove tabs and enters to improve performance
            html = html.replace("\t", "");
            html = html.replace("\r\n", "");
            html = html.replace("\n", "");
        } else if (bypass.startsWith("_bbsfile")) {
            String[] b = bypass.split(":");
            String page = b[1];
            html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + page + ".htm", player);
            ImagesCache.sendUsedImages(html, player);
        } else if (Config.BBS_PVP_ALLOW_BUY && bypass.startsWith("_bbsmultisell")) {
            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            String[] mBypass = st2.nextToken().split(":");
            String pBypass = st2.hasMoreTokens() ? st2.nextToken() : null;
            if (pBypass != null) {
                ICommunityBoardHandler handler = CommunityBoardManager.getCommunityHandler(pBypass);
                if (handler != null)
                    handler.onBypassCommand(player, pBypass);
            }

            int listId = toInt(mBypass[1]);
            MultiSellHolder.INSTANCE.SeparateAndSend(listId, player, 0);
            return;
        } else if (Config.BBS_PVP_ALLOW_SELL && bypass.startsWith("_bbssell")) {
            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            st2.nextToken();
            String pBypass = st2.hasMoreTokens() ? st2.nextToken() : null;
            if (pBypass != null) {
                ICommunityBoardHandler handler = CommunityBoardManager.getCommunityHandler(pBypass);
                if (handler != null)
                    handler.onBypassCommand(player, pBypass);
            }
            NpcTradeList list = BuyListHolder.INSTANCE.getBuyList(-1);
            player.sendPacket(new ExBuySellList.BuyList(list, player, 0.), new ExBuySellList.SellRefundList(player, false));
            return;
        } else if (bypass.startsWith("_bbsaugment")) {
            if (Config.BBS_PVP_ALLOW_AUGMENT)
                player.sendPacket(Msg.SELECT_THE_ITEM_TO_BE_AUGMENTED, ExShowVariationMakeWindow.STATIC);
            else
                player.sendMessage("Augmentation function disabled by an administrator.!");
            return;
        }
//		else if (bypass.startsWith("_maillist_0_1_0_") || bypass.startsWith("_bbsPartyMatching"))
//		{
//			PartyMatchingBBSManager.INSTANCE().parsecmd(bypass, player);
//		}
        else if (bypass.startsWith("_bbsdeaugment")) {
            if (Config.BBS_PVP_ALLOW_AUGMENT)
                player.sendPacket(Msg.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION, ExShowVariationCancelWindow.STATIC);
            else
                player.sendMessage("Augmentation function disabled by an administrator.!");
            return;
        } else if (bypass.startsWith("_bbsscripts")) {
            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            String sBypass = st2.nextToken().substring(12);
            String pBypass = st2.hasMoreTokens() ? st2.nextToken() : null;
            if (pBypass != null) {
                ICommunityBoardHandler handler = CommunityBoardManager.getCommunityHandler(pBypass);
                if (handler != null)
                    handler.onBypassCommand(player, pBypass);
            }

            String[] word = sBypass.split("\\s+");
            String[] args = sBypass.substring(word[0].length()).trim().split("\\s+");
            String[] path = word[0].split(":");
            if (path.length != 2)
                return;

            Scripts.INSTANCE.callScripts(player, path[0], path[1], word.length == 1 ? new Object[]{} : new Object[]{args});
            return;
        }

        ShowBoard.separateAndSend(html, player);
    }

    private String online() {
        return Util.formatAdena(GameObjectsStorage.getAllPlayers().size());
    }

    @Override
    public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {

    }

    private String getTimeInServer(Player player) {
        int h = GameTimeController.INSTANCE.getGameHour();
        int m = GameTimeController.INSTANCE.getGameMin();
        if (GameTimeController.INSTANCE.isNowNight()) {
            String nd = player.isLangRus() ? "Night." : "Night.";
        } else {
            String nd = player.isLangRus() ? "Day." : "Day.";
        }
        String strH;
        if (h < 10) {
            strH = "0" + h;
        } else {
            strH = "" + h;
        }
        String strM;
        if (m < 10) {
            strM = "0" + m;
        } else {
            strM = "" + m;
        }
        return strH + ":" + strM;
    }
}
