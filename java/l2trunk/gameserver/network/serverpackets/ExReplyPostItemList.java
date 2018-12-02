package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInfo;
import l2trunk.gameserver.network.clientpackets.RequestExPostItemList;

import java.util.ArrayList;
import java.util.List;

/**
 * Ответ на запрос создания нового письма.
 * Отсылается при получении {@link RequestExPostItemList}
 * Содержит список вещей, которые можно приложить к письму.
 */
public final class ExReplyPostItemList extends L2GameServerPacket {
    private final List<ItemInfo> infoList = new ArrayList<>();

    public ExReplyPostItemList(Player activeChar) {
        activeChar.getInventory().getItems().stream()
                .filter(item -> item.canBeTraded(activeChar))
                .forEach(item -> infoList.add(new ItemInfo(item)));
    }

    @Override
    protected void writeImpl() {
        writeEx(0xB2);
        writeD(infoList.size());
        infoList.forEach(this::writeItemInfo);
    }
}