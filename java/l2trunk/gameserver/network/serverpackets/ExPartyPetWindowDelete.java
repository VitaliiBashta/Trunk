package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Summon;

public final class ExPartyPetWindowDelete extends L2GameServerPacket {
    private final int summonObjectId;
    private final int ownerObjectId;
    private final String summonName;

    public ExPartyPetWindowDelete(Summon summon) {
        summonObjectId = summon.getObjectId();
        summonName = summon.getName();
        ownerObjectId = summon.getPlayer().getObjectId();
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x6a);
        writeD(summonObjectId);
        writeD(ownerObjectId);
        writeS(summonName);
    }
}