package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.cache.CrestCache;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ExPledgeCrestLarge;

public final class RequestPledgeCrestLarge extends L2GameClientPacket {
    // format: chd
    private int crestId;

    @Override
    protected void readImpl() {
        crestId = readD();
    }

    @Override
    protected void runImpl() {
        if (getClient().getActiveChar() == null)
            return;
        if (crestId == 0)
            return;
        byte[] data = CrestCache.getPledgeCrestLarge(crestId);
        if (data != null) {
            ExPledgeCrestLarge pcl = new ExPledgeCrestLarge(crestId, data);
            sendPacket(pcl);
        }
    }
}