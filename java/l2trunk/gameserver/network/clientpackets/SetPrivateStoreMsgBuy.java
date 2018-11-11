package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;

public class SetPrivateStoreMsgBuy extends L2GameClientPacket {
    private static final int MAX_MSG_LENGTH = 29;
    private String _storename;

    @Override
    protected void readImpl() {
        _storename = readS(32);
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;
        if ((_storename != null) && (_storename.length() > MAX_MSG_LENGTH))
            return;

        activeChar.setBuyStoreName(_storename);
    }
}