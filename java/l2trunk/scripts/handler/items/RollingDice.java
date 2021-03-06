package l2trunk.scripts.handler.items;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.Dice;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.List;

public final class RollingDice extends ScriptItemHandler implements ScriptFile {
    // all the items ids that this handler knowns
    private static final List<Integer> ITEM_IDS = List.of(4625, 4626, 4627, 4628);

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public boolean useItem(Player player, ItemInstance item, boolean ctrl) {
        int itemId = item.getItemId();

        if (player.isInOlympiadMode()) {
            player.sendPacket(Msg.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
            return false;
        }

        if (player.isSitting()) {
            player.sendPacket(Msg.YOU_CANNOT_MOVE_WHILE_SITTING);
            return false;
        }

        int number = Rnd.get(1, 6);
        if (number == 0) {
            player.sendPacket(Msg.YOU_MAY_NOT_THROW_THE_DICE_AT_THIS_TIMETRY_AGAIN_LATER);
            return false;
        }

        player.broadcastPacket(new Dice(player.objectId(), itemId, number, player.getX() - 30, player.getY() - 30, player.getZ()), new SystemMessage(SystemMessage.S1_HAS_ROLLED_S2).addString(player.getName()).addNumber(number));

        return true;
    }


    @Override
    public final List<Integer> getItemIds() {
        return ITEM_IDS;
    }
}