package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;

public class ExBR_PremiumState extends L2GameServerPacket {
    private final int _objectId;
    private final int _state;

    public ExBR_PremiumState(Player activeChar, boolean state) {
        _objectId = activeChar.getObjectId();
        _state = state ? 1 : 0;
    }

    @Override
    protected void writeImpl() {
        writeEx(0xD9);
        writeD(_objectId);
        writeC(_state);
    }
}
