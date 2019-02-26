package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Creature;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


public final class MagicSkillLaunched extends L2GameServerPacket {
    private final int _casterId;
    private final int _skillId;
    private final int _skillLevel;
    private final Collection<Creature> _targets;

    public MagicSkillLaunched(int casterId, int skillId, int skillLevel, Creature target) {
        _casterId = casterId;
        _skillId = skillId;
        _skillLevel = skillLevel;
        _targets = List.of(target);
    }

    public MagicSkillLaunched(int casterId, int skillId, int skillLevel, Collection<Creature> targets) {
        _casterId = casterId;
        _skillId = skillId;
        _skillLevel = skillLevel;
        _targets = targets;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x54);
        writeD(_casterId);
        writeD(_skillId);
        writeD(_skillLevel);
        writeD(_targets.size());
        for (Creature target : _targets)
            if (target != null)
                writeD(target.objectId());
    }
}