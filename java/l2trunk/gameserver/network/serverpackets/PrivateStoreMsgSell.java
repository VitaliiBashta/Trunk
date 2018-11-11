package l2trunk.gameserver.network.serverpackets;

import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.model.Player;

public class PrivateStoreMsgSell extends L2GameServerPacket {
    private final int _objId;
    private final String _name;
    private final boolean _pkg;

    /**
     * Название личного магазина продажи
     */
    public PrivateStoreMsgSell(Player player) {
        _objId = player.getObjectId();
        _pkg = player.getPrivateStoreType() == Player.STORE_PRIVATE_SELL_PACKAGE;
        _name = StringUtils.defaultString(player.getSellStoreName());
    }

    @Override
    protected final void writeImpl() {
        if (_pkg) {
            writeEx(0x80);
        } else
            writeC(0xA2);
        writeD(_objId);
        writeS(_name);
    }
}