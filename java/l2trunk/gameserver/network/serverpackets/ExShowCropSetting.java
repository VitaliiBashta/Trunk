package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.instancemanager.CastleManorManager;
import l2trunk.gameserver.model.Manor;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.templates.manor.CropProcure;

import java.util.List;

public final class ExShowCropSetting extends L2GameServerPacket {
    private final int manorId;
    private final int _count;
    private final long[] _cropData; // data to send, size:count*14

    public ExShowCropSetting(int manorId) {
        this.manorId = manorId;
        Castle c = ResidenceHolder.getCastle(this.manorId);
        List<Integer> crops = Manor.INSTANCE.getCropsForCastle(this.manorId);
        _count = crops.size();
        _cropData = new long[_count * 14];
        int i = 0;
        for (int cr : crops) {
            _cropData[i * 14] = cr;
            _cropData[i * 14 + 1] = Manor.INSTANCE.getSeedLevelByCrop(cr);
            _cropData[i * 14 + 2] = Manor.INSTANCE.getRewardItem(cr, 1);
            _cropData[i * 14 + 3] = Manor.INSTANCE.getRewardItem(cr, 2);
            _cropData[i * 14 + 4] = Manor.INSTANCE.getCropPuchaseLimit(cr);
            _cropData[i * 14 + 5] = 0; // Looks like not used
            _cropData[i * 14 + 6] = Manor.INSTANCE.getCropBasicPrice(cr) * 60 / 100;
            _cropData[i * 14 + 7] = Manor.INSTANCE.getCropBasicPrice(cr) * 10;
            CropProcure cropPr = c.getCrop(cr, CastleManorManager.PERIOD_CURRENT);
            if (cropPr != null) {
                _cropData[i * 14 + 8] = cropPr.getStartAmount();
                _cropData[i * 14 + 9] = cropPr.price;
                _cropData[i * 14 + 10] = cropPr.getReward();
            } else {
                _cropData[i * 14 + 8] = 0;
                _cropData[i * 14 + 9] = 0;
                _cropData[i * 14 + 10] = 0;
            }
            cropPr = c.getCrop(cr, CastleManorManager.PERIOD_NEXT);
            if (cropPr != null) {
                _cropData[i * 14 + 11] = cropPr.getStartAmount();
                _cropData[i * 14 + 12] = cropPr.price;
                _cropData[i * 14 + 13] = cropPr.getReward();
            } else {
                _cropData[i * 14 + 11] = 0;
                _cropData[i * 14 + 12] = 0;
                _cropData[i * 14 + 13] = 0;
            }
            i++;
        }
    }

    @Override
    public void writeImpl() {
        writeEx(0x2b); // SubId

        writeD(manorId); // manor id
        writeD(_count); // size

        for (int i = 0; i < _count; i++) {
            writeD((int) _cropData[i * 14]); // crop id
            writeD((int) _cropData[i * 14 + 1]); // seed occupation

            writeC(1);
            writeD((int) _cropData[i * 14 + 2]); // reward 1 id

            writeC(1);
            writeD((int) _cropData[i * 14 + 3]); // reward 2 id

            writeD((int) _cropData[i * 14 + 4]); // next sale limit
            writeD((int) _cropData[i * 14 + 5]); // ???
            writeD((int) _cropData[i * 14 + 6]); // min crop price
            writeD((int) _cropData[i * 14 + 7]); // max crop price

            writeQ(_cropData[i * 14 + 8]); // today buy
            writeQ(_cropData[i * 14 + 9]); // today price
            writeC((int) _cropData[i * 14 + 10]); // today reward
            writeQ(_cropData[i * 14 + 11]); // next buy
            writeQ(_cropData[i * 14 + 12]); // next price

            writeC((int) _cropData[i * 14 + 13]); // next reward
        }
    }
}