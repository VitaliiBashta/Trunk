package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;

import static l2trunk.commons.lang.NumberUtils.toInt;
import static l2trunk.gameserver.utils.ItemFunctions.addItem;

public final class AdminGiveAll implements IAdminCommandHandler {

    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (wordList.length >= 3) {
            int _id = toInt(wordList[1]);
            int _count = toInt(wordList[2]);
            GameObjectsStorage.getAllPlayersStream().forEach(player -> {
                addItem(player, _id, _count);
                player.sendMessage("You have been rewarded!");
            });
        } else {
            activeChar.sendMessage("use: //giveall itemId count");
            return false;
        }
        return true;
    }

    @Override
    public String getAdminCommand() {
        return "admin_giveall";
    }

}
