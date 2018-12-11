package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Summon;

public final class ExPartyPetWindowUpdate extends L2GameServerPacket {
    private Summon summon;

    public ExPartyPetWindowUpdate(Summon summon) {
        this.summon = summon;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x19);
        writeD(summon.getObjectId());
        writeD(summon.getTemplate().npcId + 1000000);
        writeD(summon.getSummonType());
        writeD(summon.getPlayer().getObjectId());
        writeS(summon.getName());
        writeD((int) summon.getCurrentHp());
        writeD(summon.getMaxHp());
        writeD((int) summon.getCurrentMp());
        writeD(summon.getMaxMp());
        writeD(summon.getLevel());
    }
}