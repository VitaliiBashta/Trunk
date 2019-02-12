package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.StaticObjectInstance;

/**
 * format: d
 */
public final class ChairSit extends L2GameServerPacket {
    private final int _objectId;
    private final int _staticObjectId;

    public ChairSit(Player player, StaticObjectInstance throne) {
        _objectId = player.objectId();
        _staticObjectId = throne.getUId();
    }

    @Override
    protected final void writeImpl() {
        writeC(0xed);
        writeD(_objectId);
        writeD(_staticObjectId);
    }
}