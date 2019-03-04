package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;

public final class NetPingPacket extends L2GameServerPacket {
    private final int clientId;

    public NetPingPacket(Player cha) {
        clientId = cha.objectId();
    }

    @Override
    protected void writeImpl() {
        writeC(0xD9);
        writeD(clientId);
    }
}