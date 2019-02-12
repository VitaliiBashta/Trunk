package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInfo;

import java.util.ArrayList;
import java.util.List;

public final class TradeStart extends L2GameServerPacket {
    private final List<ItemInfo> _tradelist = new ArrayList<>();
    private final int targetId;

    public TradeStart(Player player, Player target) {
        targetId = target.objectId();
        player.getInventory().getItems().stream()
                .filter(item -> item.canBeTraded(player))
                .forEach(item -> _tradelist.add(new ItemInfo(item)));
    }

    @Override
    protected final void writeImpl() {
        writeC(0x14);
        writeD(targetId);
        writeH(_tradelist.size());
        _tradelist.forEach(this::writeItemInfo);
    }
}