package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.Revive;
import l2trunk.gameserver.scripts.Functions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class res extends Functions implements IVoicedCommandHandler {
    private static final List<String> _commandList = Collections.singletonList("res");

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {

        if (command.equalsIgnoreCase("res")) {

            final int CoinCount = Config.PRICE_RESS;
            final ItemInstance Coin = activeChar.getInventory().getItemByItemId(Config.ITEM_ID_RESS);

            if (!activeChar.isAlikeDead() | activeChar.isFakeDeath()) {
                activeChar.sendMessage("You can not be revived during the life!");
                return false;
            }

            if (activeChar.isInOlympiadMode()) {
                activeChar.sendMessage("You can not use this during the Olympics.");
                return false;
            }

            if (Coin == null) {
                activeChar.sendMessage("You do not have enough money");
            }

            if ((CoinCount != 0) && (activeChar.getInventory().getItemByItemId(Config.ITEM_ID_RESS).getCount() < CoinCount)) {
                activeChar.sendMessage("You do not have enough money");
                activeChar.sendActionFailed();
                return false;
            }

            if (Config.COMMAND_RES) {
                Functions.removeItem(activeChar, Config.ITEM_ID_RESS, CoinCount, ".res");
                activeChar.restoreExp();
                activeChar.setCurrentCp(activeChar.getMaxCp());
                activeChar.setCurrentHp(activeChar.getMaxHp(), true);
                activeChar.setCurrentMp(activeChar.getMaxMp());
                activeChar.broadcastPacket(new Revive(activeChar));
                activeChar.sendMessage("You rose!");
                activeChar.sendMessage("You have successfully paid for the services of service. Thank you!");
            }

        }
        return true;
    }

    @Override
    public List<String> getVoicedCommandList() {
        return _commandList;
    }
}
