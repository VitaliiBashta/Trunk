package l2trunk.gameserver.network.serverpackets;

public final class PledgeSkillListAdd extends L2GameServerPacket {
    private final int skillId;
    private final int skillLevel;

    public PledgeSkillListAdd(int skillId, int skillLevel) {
        this.skillId = skillId;
        this.skillLevel = skillLevel;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x3b);
        writeD(skillId);
        writeD(skillLevel);
    }
}