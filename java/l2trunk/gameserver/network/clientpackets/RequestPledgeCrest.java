package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.cache.CrestCache;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.PledgeCrest;

public final class RequestPledgeCrest extends L2GameClientPacket {
    // format: cd

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
        byte[] data = CrestCache.getPledgeCrest(crestId);
        if (data != null) {
            PledgeCrest pc = new PledgeCrest(crestId, data);
            sendPacket(pc);
        }
    }
}