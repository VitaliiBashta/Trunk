package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.entity.events.impl.DuelEvent;

public class ExDuelStart extends L2GameServerPacket {
    private final int _duelType;

    public ExDuelStart(DuelEvent e) {
        _duelType = e.getDuelType();
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x4e);
        writeD(_duelType);
    }
}