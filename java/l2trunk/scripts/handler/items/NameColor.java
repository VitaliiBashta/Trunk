package l2trunk.scripts.handler.items;

import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.ExChangeNicknameNColor;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.Arrays;
import java.util.List;

public final class NameColor extends SimpleItemHandler implements ScriptFile {
    private static final List<Integer> ITEM_IDS = List.of(13021, 13307);

    @Override
    public List<Integer> getItemIds() {
        return ITEM_IDS;
    }

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
    protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl) {
        player.sendPacket(new ExChangeNicknameNColor(item.getObjectId()));
        return true;
    }
}
