package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;

/**
 * @author Grivesky
 */
public class NetPingPacket extends L2GameServerPacket {
    private final int _clientId;

    public NetPingPacket(Player cha) {
        _clientId = cha.objectId();
    }

    @Override
    protected void writeImpl() {
        writeC(0xD9);
        writeD(_clientId);
    }
}