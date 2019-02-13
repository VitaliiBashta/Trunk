package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.entity.boat.Boat;

public final class VehicleStart extends L2GameServerPacket {
    private final int _objectId;
    private final int _state;

    public VehicleStart(Boat boat) {
        _objectId = boat.objectId();
        _state = boat.getRunState();
    }

    @Override
    protected void writeImpl() {
        writeC(0xC0);
        writeD(_objectId);
        writeD(_state);
    }
}