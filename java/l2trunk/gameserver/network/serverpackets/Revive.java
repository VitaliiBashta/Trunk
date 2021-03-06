package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.GameObject;

/**
 * sample
 * 0000: 0c  9b da 12 40                                     ....@
 * <p>
 * format  d
 */
public class Revive extends L2GameServerPacket {
    private final int _objectId;

    public Revive(GameObject obj) {
        _objectId = obj.objectId();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x01);
        writeD(_objectId);
    }
}