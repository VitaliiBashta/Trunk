package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.instancemanager.CastleManorManager;
import l2trunk.gameserver.model.Manor;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.templates.manor.SeedProduction;

import java.util.List;

public final class ExShowSeedSetting extends L2GameServerPacket {
    private final int manorId;
    private final int count;
    private final long[] seedData; // data to send, size:count*12

    public ExShowSeedSetting(int manorId) {
        this.manorId = manorId;
        Castle c = ResidenceHolder.getCastle(this.manorId);
        List<Integer> seeds = Manor.INSTANCE.getSeedsForCastle(this.manorId);
        count = seeds.size();
        seedData = new long[count * 12];
        int i = 0;
        for (int s : seeds) {
            seedData[i * 12 + 0] = s;
            seedData[i * 12 + 1] = Manor.INSTANCE.getSeedLevel(s);
            seedData[i * 12 + 2] = Manor.INSTANCE.getRewardItemBySeed(s, 1);
            seedData[i * 12 + 3] = Manor.INSTANCE.getRewardItemBySeed(s, 2);
            seedData[i * 12 + 4] = Manor.INSTANCE.getSeedSaleLimit(s);
            seedData[i * 12 + 5] = Manor.INSTANCE.getSeedBuyPrice(s);
            seedData[i * 12 + 6] = Manor.INSTANCE.getSeedBasicPrice(s) * 60 / 100;
            seedData[i * 12 + 7] = Manor.INSTANCE.getSeedBasicPrice(s) * 10;
            SeedProduction seedPr = c.getSeed(s, CastleManorManager.PERIOD_CURRENT);
            if (seedPr != null) {
                seedData[i * 12 + 8] = seedPr.getStartProduce();
                seedData[i * 12 + 9] = seedPr.getPrice();
            } else {
                seedData[i * 12 + 8] = 0;
                seedData[i * 12 + 9] = 0;
            }
            seedPr = c.getSeed(s, CastleManorManager.PERIOD_NEXT);
            if (seedPr != null) {
                seedData[i * 12 + 10] = seedPr.getStartProduce();
                seedData[i * 12 + 11] = seedPr.getPrice();
            } else {
                seedData[i * 12 + 10] = 0;
                seedData[i * 12 + 11] = 0;
            }
            i++;
        }
    }

    @Override
    public void writeImpl() {
        writeEx(0x26); // SubId

        writeD(manorId); // manor id
        writeD(count); // size

        for (int i = 0; i < count; i++) {
            writeD((int) seedData[i * 12 + 0]); // seed id
            writeD((int) seedData[i * 12 + 1]); // occupation

            writeC(1);
            writeD((int) seedData[i * 12 + 2]); // reward 1 id

            writeC(1);
            writeD((int) seedData[i * 12 + 3]); // reward 2 id

            writeD((int) seedData[i * 12 + 4]); // next sale limit
            writeD((int) seedData[i * 12 + 5]); // price for castle to produce 1
            writeD((int) seedData[i * 12 + 6]); // min seed price
            writeD((int) seedData[i * 12 + 7]); // max seed price

            writeQ(seedData[i * 12 + 8]); // today sales
            writeQ(seedData[i * 12 + 9]); // today price
            writeQ(seedData[i * 12 + 10]); // next sales
            writeQ(seedData[i * 12 + 11]); // next price
        }
    }
}