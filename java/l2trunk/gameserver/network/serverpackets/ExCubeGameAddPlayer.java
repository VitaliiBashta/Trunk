package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;

/**
 * Format: (chd) dddS
 * d: always -1
 * d: getPlayer team
 * d: getPlayer object id
 * S: getPlayer name
 */
public class ExCubeGameAddPlayer extends L2GameServerPacket {
    private final boolean _isRedTeam;
    private final int _objectId;
    private final String _name;

    public ExCubeGameAddPlayer(Player player, boolean isRedTeam) {
        _objectId = player.objectId();
        _name = player.getName();
        _isRedTeam = isRedTeam;
    }

    @Override
    protected void writeImpl() {
        writeEx(0x97);
        writeD(0x01);

        writeD(0xffffffff);

        writeD(_isRedTeam ? 0x01 : 0x00);
        writeD(_objectId);
        writeS(_name);
    }
}