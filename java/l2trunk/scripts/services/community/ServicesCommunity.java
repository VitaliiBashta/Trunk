package l2trunk.scripts.services.community;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.ServicesConfig;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.handler.bbs.CommunityBoardManager;
import l2trunk.gameserver.handler.bbs.ICommunityBoardHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.network.serverpackets.ShowBoard;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class ServicesCommunity extends Functions implements ScriptFile, ICommunityBoardHandler {

    private static final Logger _log = LoggerFactory.getLogger(ServicesCommunity.class);
    private final String NameItemPice = ItemFunctions.createItem(ServicesConfig.get("LevelUpItemPice", 4357)).getName();

    @Override
    public void onLoad() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            _log.info("ServicesCommunity: Services Community service loaded.");
            CommunityBoardManager.registerHandler(this);
        }
    }

    @Override
    public void onReload() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            CommunityBoardManager.removeHandler(this);
        }
    }

    @Override
    public List<String> getBypassCommands() {
        return List.of("_bbsservices");
    }

    @Override
    public void onBypassCommand(Player player, String bypass) {

        if (!checkCondition(player))
            return;

        if (!ServicesConfig.get("LevelUpEnable", false)) {
            show("Service is disabled.", player);
            return;
        }


        if (bypass.startsWith("_bbsservices:occupation")) {
            String html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "pages/pages/content.htm", player);

            StringBuilder _content = new StringBuilder();
            _content.append("<table width=400><tr><td align=center> Improve Services. </td></tr></table>");
            _content.append("<table border=0 width=400><tr>");
            int[] LvList = ServicesConfig.get("LevelUpList");
            int[] LvPiceList = ServicesConfig.get("LevelUpPiceList");
            for (int i = 0; i < LvList.length; i++) {
                if (LvList[i] > player.getLevel()) {
                    if (i % 4 == 0)
                        _content.append("</tr><tr>");
                    _content.append("<td><center><button value=\"On ").append(LvList[i]).append(" (Price:").append(LvPiceList[i]).append(" ").append(NameItemPice).append(")\" action=\"bypass _bbsservices:occupation:up:").append(LvList[i]).append(":").append(LvPiceList[i]).append("\" width=180 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\"></center></td>");
                }
            }
            _content.append("</tr></table>");
            html = html.replace("%content%", _content.toString());
            ShowBoard.separateAndSend(html, player);
        }
        if (bypass.startsWith("_bbsservices:occupation:up")) {
            String[] var = bypass.split(":");
            if (player.getInventory().destroyItemByItemId(ServicesConfig.get("LevelUpItemPice", 4357), Integer.parseInt(var[4]), "Level Up Service"))
                player.addExpAndSp(Experience.LEVEL[Integer.parseInt(var[3])] - player.getExp(), 0);
            else
                player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
            onBypassCommand(player, "_bbsservices:occupation");
        }
    }


    private boolean checkCondition(Player player) {
        if (/*player.isInJail() ||*/player.getReflectionId() != 0 || player.isDead() || player.isAlikeDead() || player.isCastingNow() || player.isInCombat() || player.isAttackingNow() || player.isInOlympiadMode() || player.isFlying()) {
            player.sendMessage("Raising is not possible");
            return false;
        }
        return true;
    }


    @Override
    public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
    }

}