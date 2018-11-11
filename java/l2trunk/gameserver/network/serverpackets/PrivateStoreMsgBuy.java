package l2trunk.gameserver.network.serverpackets;


import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.model.Player;

public final class PrivateStoreMsgBuy extends L2GameServerPacket {
    private final int _objId;
    private final String _name;

    public PrivateStoreMsgBuy(Player player) {
        _objId = player.getObjectId();
        _name = StringUtils.defaultString(player.getBuyStoreName());
    }

    @Override
    protected final void writeImpl() {
        writeC(0xBF);
        writeD(_objId);
        writeS(_name);
    }
}