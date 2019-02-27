package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.data.xml.holder.BuyListHolder;
import l2trunk.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ExBuySellList;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.utils.GameStats;

import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class AdminShop implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {

        if (!activeChar.getPlayerAccess().UseGMShop)
            return false;

        switch (comm) {
            case "admin_buy":
                try {
                    handleBuyRequest(activeChar, fullString.substring(10));
                } catch (IndexOutOfBoundsException e) {
                    activeChar.sendMessage("Please specify buylist.");
                }
                break;
            case "admin_gmshop":
                activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/gmshops.htm"));
                break;
            case "admin_tax":
                activeChar.sendMessage("TaxSum: " + GameStats.getTaxSum());
                break;
            case "admin_taxclear":
                GameStats.addTax(-GameStats.getTaxSum());
                activeChar.sendMessage("TaxSum: " + GameStats.getTaxSum());
                break;
        }

        return true;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_buy",
                "admin_gmshop",
                "admin_tax",
                "admin_taxclear",
                "admin_restore_offline_stores"
        );
    }

    private void handleBuyRequest(Player activeChar, String command) {
        int val = toInt(command, -1);

        NpcTradeList list = BuyListHolder.INSTANCE.getBuyList(val);

        if (list != null)
            activeChar.sendPacket(new ExBuySellList.BuyList(list, activeChar, 0.), new ExBuySellList.SellRefundList(activeChar, false));

        activeChar.sendActionFailed();
    }

}