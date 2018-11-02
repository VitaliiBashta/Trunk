package l2f.gameserver.network.serverpackets;


import l2f.commons.lang.StringUtils;
import l2f.gameserver.model.Player;

public final class PrivateStoreMsgBuy extends L2GameServerPacket {
    private int _objId;
    private String _name;

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