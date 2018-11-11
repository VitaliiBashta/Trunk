package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.entity.events.objects.KrateisCubePlayerObject;

/**
 * @author VISTALL
 */
public class ExPVPMatchCCMyRecord extends L2GameServerPacket {
    private final int _points;

    public ExPVPMatchCCMyRecord(KrateisCubePlayerObject player) {
        _points = player.getPoints();
    }

    @Override
    public void writeImpl() {
        writeEx(0x8A);
        writeD(_points);
    }
}