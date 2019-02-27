package l2trunk.scripts.services.community;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.handler.bbs.CommunityBoardManager;
import l2trunk.gameserver.handler.bbs.ICommunityBoardHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.ShowBoard;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.StringTokenizer;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class CareerManager implements ScriptFile, ICommunityBoardHandler {
    private static final Logger _log = LoggerFactory.getLogger(CareerManager.class);

    private static void changeClass(Player player, ClassId classId) {
        if (player.getClassId().occupation() == 2)
            player.sendPacket(SystemMsg.CONGRATULATIONS__YOUVE_COMPLETED_YOUR_THIRDCLASS_TRANSFER_QUEST); // ??? 3 ?????
        else
            player.sendPacket(SystemMsg.CONGRATULATIONS__YOUVE_COMPLETED_A_CLASS_TRANSFER); // ??? 1 ? 2 ?????

        player.setClassId(classId, false, false);
        player.broadcastUserInfo(true);
    }

    private static boolean checkCondition(Player player) {
        if (player == null)
            return false;

        if (!Config.USE_BBS_PROF_IS_COMBAT && (player.getPvpFlag() != 0 || player.isInDuel() || player.isInCombat() || player.isAttackingNow())) {
            player.sendMessage("During combat, you can not use this feature.");
            return false;
        }

        return true;
    }

    private static boolean checkIfCanTransferToClass(Player player, ClassId newClassId) {
        if (player == null)
            return false;

        final ClassId currentClassId = player.getClassId();
        final int jobLevel = player.getLevel();
        final int level = player.getLevel();

        if (Config.ALLOW_CLASS_MASTERS_LIST.isEmpty() || !Config.ALLOW_CLASS_MASTERS_LIST.contains(jobLevel))
            return false;

        if (level >= 76 && jobLevel == 3 && Config.ALLOW_CLASS_MASTERS_LIST.contains(jobLevel)) {
            for (ClassId cid : ClassId.values()) {
                if (cid != newClassId)
                    continue;

                if (cid == ClassId.inspector)
                    return false;

                if (cid.childOf(currentClassId) && cid.occupation() == currentClassId.occupation() + 1)
                    return true;

            }
        }

        return false;
    }

    @Override
    public void onLoad() {
        if (Config.COMMUNITYBOARD_ENABLED/* && Config.BBS_PVP_CB_ENABLED*/) {
            _log.info("CommunityBoard: Manage Career service loaded.");
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
        return List.of("_bbscareer;", "_bbscareer;sub;", "_bbscareer;classmaster;change_class;");
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void onBypassCommand(Player activeChar, String command) {
        if (!checkCondition(activeChar))
            return;

        if (command.startsWith("_bbscareer;classmaster;change_class;")) {
            StringTokenizer st = new StringTokenizer(command, ";");
            st.nextToken();
            st.nextToken();
            st.nextToken();

            final ClassId classId = ClassId.getById(st.nextToken());

            // Synerge - Check if the getPlayer can actually transfer to that class
            if (!checkIfCanTransferToClass(activeChar, classId)) {
                _log.warn("CareerManager: The getPlayer " + activeChar.getName() + " wanted to transfer to a class that he cannot to. Cheater?");
                return;
            }

            final int price = Integer.parseInt(st.nextToken());
            ItemTemplate item = ItemHolder.getTemplate(Config.CLASS_MASTERS_PRICE_ITEM);
            ItemInstance pay = activeChar.getInventory().getItemByItemId(item.itemId());
            if (pay != null && pay.getCount() >= price) {
                activeChar.getInventory().destroyItem(pay, price, "Class Changer");
                changeClass(activeChar, classId);
                onBypassCommand(activeChar, "_bbscareer;");
            } else if (Config.CLASS_MASTERS_PRICE_ITEM == 57) {
                activeChar.sendPacket(new SystemMessage2(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA));
            } else {
                activeChar.sendPacket(new SystemMessage2(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA));
            }
        } else if (command.startsWith("_bbscareer;")) {
            ClassId classId = activeChar.getClassId();
            int jobLevel = classId.occupation()+1;
            int level = activeChar.getLevel();
            StringBuilder html = new StringBuilder();
            html.append("<br>");
            html.append("<table width=600>");
            html.append("<tr><td>");
            if (Config.ALLOW_CLASS_MASTERS_LIST.isEmpty() || !Config.ALLOW_CLASS_MASTERS_LIST.contains(jobLevel))
                jobLevel = 4;

            if ((level >= 20 && jobLevel == 1 || level >= 40 && jobLevel == 2 || level >= 76 && jobLevel == 3) && Config.ALLOW_CLASS_MASTERS_LIST.contains(jobLevel)) {
                ItemTemplate item = ItemHolder.getTemplate(Config.CLASS_MASTERS_PRICE_ITEM);
                html.append("You have to pay: <font color=\"LEVEL\">");
                html.append(Util.formatAdena(Config.CLASS_MASTERS_PRICE_LIST[jobLevel])).append("</font> <font color=\"LEVEL\">").append(item.getName()).append("</font> to change profession<br>");
                html.append("<center><table width=600><tr>");
                for (ClassId cid : ClassId.values()) {
                    if (cid == ClassId.inspector)
                        continue;
                    if (cid.childOf(classId) && cid.occupation() == classId.occupation() + 1)
                        html.append("<td><center><button value=\"").append(cid.name()).append("\" action=\"bypass _bbscareer;classmaster;change_class;").append(cid.id).append(";").append(Config.CLASS_MASTERS_PRICE_LIST[jobLevel]).append("\" width=150 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center></td>");

                }
                html.append("</tr></table></center>");
                html.append("</td>");
                html.append("</tr>");
                html.append("</table>");
            }
            String content = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "pages/career.htm", activeChar);
            content = content.replace("%career%", html.toString());
            ShowBoard.separateAndSend(content, activeChar);
        }
    }

}