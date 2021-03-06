package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.SkillLearn;
import l2trunk.gameserver.model.base.AcquireType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AcquireSkillInfo extends L2GameServerPacket {
    private final SkillLearn learn;
    private final AcquireType type;
    private List<Require> reqs = Collections.emptyList();

    public AcquireSkillInfo(AcquireType type, SkillLearn learn) {
        this.type = type;
        this.learn = learn;
        if (this.learn.itemId != 0) {
            reqs = new ArrayList<>(1);
            reqs.add(new Require(99, this.learn.itemId, this.learn.itemCount, 50));
        }
    }

    @Override
    public void writeImpl() {
        writeC(0x91);
        writeD(learn.id);
        writeD(learn.level);
        writeD(learn.cost); // sp/rep
        writeD(type.ordinal());

        writeD(reqs.size()); //requires size

        for (Require temp : reqs) {
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