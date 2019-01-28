package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.cache.CrestCache;
import l2trunk.gameserver.network.serverpackets.AllianceCrest;

public final class RequestAllyCrest extends L2GameClientPacket {
    // format: cd

    private int crestId;

    @Override
    protected void readImpl() {
        crestId = readD();
    }

    @Override
    protected void runImpl() {
        if (crestId == 0)
            return;
        byte[] data = CrestCache.getAllyCrest(crestId);
        if (data != null) {
            AllianceCrest ac = new AllianceCrest(crestId, data);
            sendPacket(ac);
        }
    }
}