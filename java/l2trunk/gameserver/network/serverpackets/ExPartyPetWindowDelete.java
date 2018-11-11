package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Summon;

public class ExPartyPetWindowDelete extends L2GameServerPacket {
    private final int _summonObjectId;
    private final int _ownerObjectId;
    private final String _summonName;

    public ExPartyPetWindowDelete(Summon summon) {
        _summonObjectId = summon.getObjectId();
        _summonName = summon.getName();
        _ownerObjectId = summon.getPlayer().getObjectId();
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x6a);
        writeD(_summonObjectId);
        writeD(_ownerObjectId);
        writeS(_summonName);
    }
}