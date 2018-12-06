package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.scripts.Functions;

import java.util.ArrayList;
import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class AdminGiveAll implements IAdminCommandHandler {

    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        if (wordList.length >= 3) {
            int _id = toInt(wordList[1]);
            int _count = toInt(wordList[2]);
            GameObjectsStorage.getAllPlayers()
                    .forEach(player -> {
                        Functions.addItem(player, _id, _count, "Give ALl");
                        player.sendMessage("You have been rewarded!");
                    });
        } else {
            activeChar.sendMessage("use: //giveall itemId count");
            return false;
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    enum Commands {
        admin_giveall
    }
}
