package l2trunk.scripts.handler.items;

import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.ShowCalc;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.List;

public final class Calculator extends ScriptItemHandler implements ScriptFile {
    private static final int CALCULATOR = 4393;

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }


    @Override
    public boolean useItem(Player player, ItemInstance item, boolean ctrl) {
        player.sendPacket(new ShowCalc(item.getItemId()));
        return true;
    }

    @Override
    public List<Integer> getItemIds() {
        return List.of(CALCULATOR);
    }
}
