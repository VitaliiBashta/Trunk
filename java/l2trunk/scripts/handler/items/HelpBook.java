package l2trunk.scripts.handler.items;

import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.RadarControl;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;

import java.util.Arrays;
import java.util.List;

public final class HelpBook extends ScriptItemHandler implements ScriptFile {
    private static final List<Integer> ITEM_IDS = List.of(
            5588, 6317, 7561, 7063, 7064, 7065, 7066, 7082, 7083, 7084, 7085,
            7086, 7087, 7088, 7089, 7090, 7091, 7092, 7093, 7094, 7095, 7096,
            7097, 7098, 7099, 7100, 7101, 7102, 7103, 7104, 7105, 7106, 7107,
            7108, 7109, 7110, 7111, 7112, 8059, 13130, 13131, 13132, 13133,
            13134, 13135, 13136, 17213);

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }


    @Override
    public boolean useItem(Player player, ItemInstance item, boolean ctrl) {
        Functions.show("help/" + item.getItemId() + ".htm", player, null);
        if (item.getItemId() == 7063)
            player.sendPacket(new RadarControl(0, 2, Location.of(51995, -51265, -3104)));
        player.sendActionFailed();
        return true;
    }

    @Override
    public List<Integer> getItemIds() {
        return ITEM_IDS;
    }
}