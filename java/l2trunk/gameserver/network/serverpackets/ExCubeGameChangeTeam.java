package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;

/**
 * Format: (chd) ddd
 * d: always -1
 * d: getPlayer team
 * d: getPlayer object id
 */
public class ExCubeGameChangeTeam extends L2GameServerPacket {
    private final int _objectId;
    private final boolean _fromRedTeam;

    public ExCubeGameChangeTeam(Player player, boolean fromRedTeam) {
        _objectId = player.objectId();
        _fromRedTeam = fromRedTeam;
    }

    @Override
    protected void writeImpl() {
        writeEx(0x97);
        writeD(0x05);

        writeD(_objectId);
        writeD(_fromRedTeam ? 0x01 : 0x00);
        writeD(_fromRedTeam ? 0x00 : 0x01);
    }
}