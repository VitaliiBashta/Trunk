package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.SkillLearn;
import l2trunk.gameserver.model.base.AcquireType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Reworked: VISTALL
 */
public class AcquireSkillInfo extends L2GameServerPacket {
    private final SkillLearn _learn;
    private final AcquireType _type;
    private List<Require> _reqs = Collections.emptyList();

    public AcquireSkillInfo(AcquireType type, SkillLearn learn) {
        _type = type;
        _learn = learn;
        if (_learn.getItemId() != 0) {
            _reqs = new ArrayList<>(1);
            _reqs.add(new Require(99, _learn.getItemId(), _learn.getItemCount(), 50));
        }
    }

    @Override
    public void writeImpl() {
        writeC(0x91);
        writeD(_learn.id());
        writeD(_learn.getLevel());
        writeD(_learn.getCost()); // sp/rep
        writeD(_type.ordinal());

        writeD(_reqs.size()); //requires size

        for (Require temp : _reqs) {
            writeD(temp.type);
            writeD(temp.itemId);
            writeQ(temp.count);
            writeD(temp.unk);
        }
    }

    private static class Require {
        final int itemId;
        final long count;
        final int type;
        final int unk;

        Require(int pType, int pItemId, long pCount, int pUnk) {
            itemId = pItemId;
            type = pType;
            count = pCount;
            unk = pUnk;
        }
    }
}