package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.dao.ClickersSignatureDao;


public class ClickersSignatures extends L2GameServerPacket {
    private final int[] signatures;

    public ClickersSignatures() {
        signatures = ClickersSignatureDao.getInstance().getSignatures();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x0E);
        writeD(signatures.length);

        for (int sig : signatures) {
            writeD(sig);
        }
    }
}