package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Summon;

public final class ExPartyPetWindowAdd extends L2GameServerPacket {
    private final int ownerId, npcId, type, curHp, maxHp, curMp, maxMp, level;
    private final int summonId;
    private final String name;

    public ExPartyPetWindowAdd(Summon summon) {
        summonId = summon.objectId();
        ownerId = summon.owner.objectId();
        npcId = summon.getTemplate().npcId + 1000000;
        type = summon.getSummonType();
        name = summon.getName();
        curHp = (int) summon.getCurrentHp();
        maxHp = summon.getMaxHp();
        curMp = (int) summon.getCurrentMp();
        maxMp = summon.getMaxMp();
        level = summon.getLevel();
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x18);
        writeD(summonId);
        writeD(npcId);
        writeD(type);
        writeD(ownerId);
        writeS(name);
        writeD(curHp);
        writeD(maxHp);
        writeD(curMp);
        writeD(maxMp);
        writeD(level);
    }
}