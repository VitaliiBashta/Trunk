package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;

public class PartySmallWindowDelete extends L2GameServerPacket {
    private final int _objId;
    private final String _name;

    public PartySmallWindowDelete(Player member) {
        _objId = member.objectId();
        _name = member.getName();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x51);
        writeD(_objId);
        writeS(_name);
    }
}