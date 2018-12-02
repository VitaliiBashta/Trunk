package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Manor;
import l2trunk.gameserver.templates.manor.SeedProduction;

import java.util.List;


/**
 * format
 * cddd[dddddc[d]c[d]]
 * cddd[dQQQdc[d]c[d]] - Gracia Final
 */
public class ExShowSeedInfo extends L2GameServerPacket {
    private final List<SeedProduction> _seeds;
    private final int _manorId;

    public ExShowSeedInfo(int manorId, List<SeedProduction> seeds) {
        _manorId = manorId;
        _seeds = seeds;
    }

    @Override
    protected void writeImpl() {
        writeEx(0x23); // SubId
        writeC(0);
        writeD(_manorId); // Manor ID
        writeD(0);
        writeD(_seeds.size());
        for (SeedProduction seed : _seeds) {
            writeD(seed.getId()); // Seed id

            writeQ(seed.getCanProduce()); // Left to buy
            writeQ(seed.getStartProduce()); // Started amount
            writeQ(seed.getPrice()); // Sell Price
            writeD(Manor.INSTANCE.getSeedLevel(seed.getId())); // Seed Level

            writeC(1); // reward 1 Type
            writeD(Manor.INSTANCE.getRewardItemBySeed(seed.getId(), 1)); // Reward 1 Type Item Id

            writeC(1); // reward 2 Type
            writeD(Manor.INSTANCE.getRewardItemBySeed(seed.getId(), 2)); // Reward 2 Type Item Id
        }
    }
}