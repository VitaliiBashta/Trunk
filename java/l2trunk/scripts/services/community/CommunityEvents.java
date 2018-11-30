package l2trunk.scripts.services.community;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.handler.bbs.CommunityBoardManager;
import l2trunk.gameserver.handler.bbs.ICommunityBoardHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.fightclubmanager.FightClubEventManager;
import l2trunk.gameserver.model.entity.events.fightclubmanager.FightClubLastPlayerStats;
import l2trunk.gameserver.model.entity.events.fightclubmanager.FightClubLastStatsManager;
import l2trunk.gameserver.model.entity.events.impl.AbstractFightClub;
import l2trunk.gameserver.network.serverpackets.Say2;
import l2trunk.gameserver.network.serverpackets.ShowBoard;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.taskmanager.AutoImageSenderManager;
import l2trunk.gameserver.utils.BbsUtil;
import l2trunk.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class CommunityEvents implements ScriptFile, ICommunityBoardHandler {
    private static final Logger _log = LoggerFactory.getLogger(CommunityEvents.class);

    @Override
    public void onLoad() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            _log.info("CommunityBoard: Community Events service loaded.");
            CommunityBoardManager.getInstance().registerHandler(this);
        }
    }

    @Override
    public void onReload() {
        if (Config.COMMUNITYBOARD_ENABLED)
            CommunityBoardManager.getInstance().removeHandler(this);
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public List<String> getBypassCommands() {
        return Arrays.asList("_bbslink", "_bbsevent", "_bbseventUnregister", "_bbseventRegister");
    }


    @Override
    public void onBypassCommand(Player player, String bypass) {
        //Checking if all required images were sent to the player, if not - not allowing to pass
        if (!AutoImageSenderManager.wereAllImagesSent(player)) {
            player.sendPacket(new Say2(player.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "CB", "Community wasn't loaded yet, try again in few seconds."));
            return;
        }

        StringTokenizer st = new StringTokenizer(bypass, "_");
        String cmd = st.nextToken();

        if ("bbsevent".equals(cmd) || "bbslink".equals(cmd)) {
            String html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "bbs_events.htm", player);
            html = getEventInfo(html, player);
            html = getEventStats(html, player);
            html = BbsUtil.htmlAll(html, player);
            ShowBoard.separateAndSend(html, player);
        } else if ("bbseventUnregister".equals(cmd)) {
            FightClubEventManager.getInstance().unsignFromEvent(player);
            onBypassCommand(player, "_bbsevent");
        } else if ("bbseventRegister".equals(cmd)) {
            AbstractFightClub event = FightClubEventManager.getInstance().getNextEvent();
            FightClubEventManager.getInstance().trySignForEvent(player, event, true);
            onBypassCommand(player, "_bbsevent");
        }
    }

    private String getEventInfo(String html, Player player) {
        AbstractFightClub event = FightClubEventManager.getInstance().getNextEvent();
        if (event == null) {
            event = FightClubEventManager.getInstance().getNextEvent();
        }
        if (event != null) {
            html = html.replace("%eventIcon%", event.getIcon());
            html = html.replace("%eventName%", event.getName());
            html = html.replace("%eventDesc%", event.getDescription());
        } else {
            html = html.replace("%eventIcon%", "icon.NOIMAGE");
            html = html.replace("%eventName%", "...");
            html = html.replace("%eventDesc%", "Loading event engine...");
        }

        String register;
        if (event == null) {
            register = "<font color=\"FF0000\">Event not loaded yet!</font>";
        } else {
            if (!FightClubEventManager.getInstance().isRegistrationOpened(event)) {
                register = "<font color=\"FF0000\">Registration Closed</font>";
            } else {
                if (FightClubEventManager.getInstance().isPlayerRegistered(player)) {
                    register = "<button value=\"Unregister from Event\" action=\"bypass _bbseventUnregister\" back=\"L2UI_CT1.OlympiadWnd_DF_Fight3None_Down\" width=200 height=30 fore=\"L2UI_CT1.OlympiadWnd_DF_Fight3None\">";
                } else {
                    register = "<button value=\"Register to Event\" action=\"bypass _bbseventRegister\" back=\"L2UI_CT1.OlympiadWnd_DF_Fight3None_Down\" width=200 height=30 fore=\"L2UI_CT1.OlympiadWnd_DF_Fight3None\">";
                }
            }

        }
        html = html.replace("%eventRegister%", register);

        return html;
    }

    private static String getEventStats(String html, Player player) {
        List<FightClubLastPlayerStats> stats = FightClubLastStatsManager.getInstance().getStats(true);

        for (int i = 0; i < 10; i++) {
            if ((i + 1) <= stats.size()) {
                FightClubLastPlayerStats stat = stats.get(i);
                if (stat.isMyStat(player)) {
                    html = html.replace("<?name_" + i + "?>", "<fonr color=\"CC3333\">" + stat.getPlayerName() + "</font>");
                    html = html.replace("<?count_" + i + "?>", "<fonr color=\"CC3333\">" + Util.formatAdena(stat.getScore()) + "</font>");
                    html = html.replace("<?class_" + i + "?>", "<fonr color=\"CC3333\">" + stat.getClassName() + "</font>");
                    html = html.replace("<?clan_" + i + "?>", "<fonr color=\"CC3333\">" + stat.getClanName() + "</font>");
                    html = html.replace("<?ally_" + i + "?>", "<fonr color=\"CC3333\">" + stat.getAllyName() + "</font>");
                } else {
                    html = html.replace("<?name_" + i + "?>", stat.getPlayerName());
                    html = html.replace("<?count_" + i + "?>", Util.formatAdena(stat.getScore()));
                    html = html.replace("<?class_" + i + "?>", stat.getClassName());
                    html = html.replace("<?clan_" + i + "?>", stat.getClanName());
                    html = html.replace("<?ally_" + i + "?>", stat.getAllyName());
                }
            } else {
                html = html.replace("<?name_" + i + "?>", "...");
                html = html.replace("<?count_" + i + "?>", "...");
                html = html.replace("<?class_" + i + "?>", "...");
                html = html.replace("<?clan_" + i + "?>", "...");
                html = html.replace("<?ally_" + i + "?>", "...");
            }
        }

        FightClubLastPlayerStats my = FightClubLastStatsManager.getInstance().getMyStat(player);
        if (my != null) {
            html = html.replace("<?name_me?>", "<fonr color=\"CC3333\">" + my.getPlayerName() + "</font>");
            html = html.replace("<?count_me?>", "<fonr color=\"CC3333\">" + Util.formatAdena(my.getScore()) + "</font>");
            html = html.replace("<?class_me?>", "<fonr color=\"CC3333\">" + my.getClassName() + "</font>");
            html = html.replace("<?clan_me?>", "<fonr color=\"CC3333\">" + my.getClanName() + "</font>");
            html = html.replace("<?ally_me?>", "<fonr color=\"CC3333\">" + my.getAllyName() + "</font>");
        } else {
            html = html.replace("<?name_me?>", "...");
            html = html.replace("<?count_me?>", "...");
            html = html.replace("<?class_me?>", "...");
            html = html.replace("<?clan_me?>", "...");
            html = html.replace("<?ally_me?>", "...");
        }

        return html;
    }

    @Override
    public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
    }
}
