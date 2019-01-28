package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Summon;

public final class PetStatusShow extends L2GameServerPacket {
    private final int summonType;

    public PetStatusShow(Summon summon) {
        summonType = summon.getSummonType();
    }

    @Override
    protected final void writeImpl() {
        writeC(0xb1);
        writeD(summonType);
    }
}