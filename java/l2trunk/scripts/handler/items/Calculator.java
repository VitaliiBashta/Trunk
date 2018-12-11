package l2trunk.scripts.handler.items;

import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.ShowCalc;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.Collections;
import java.util.List;

public final class Calculator extends ScriptItemHandler implements ScriptFile {
    private static final int CALCULATOR = 4393;


    @Override
    public boolean pickupItem(Playable playable, ItemInstance item) {
        return true;
    }

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {

    }

    @Override
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        if (!playable.isPlayer())
            return false;

        playable.sendPacket(new ShowCalc(item.getItemId()));
        return true;
    }

    @Override
    public List<Integer> getItemIds() {
        return Collections.singletonList(CALCULATOR);
    }
}
