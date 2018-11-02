package l2f.gameserver.network.serverpackets;

public final class PledgeSkillListAdd extends L2GameServerPacket {
    private int skillId;
    private int skillLevel;

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