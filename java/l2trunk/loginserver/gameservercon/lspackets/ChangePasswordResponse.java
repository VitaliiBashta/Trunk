package l2trunk.loginserver.gameservercon.lspackets;

import l2trunk.loginserver.gameservercon.SendablePacket;

public class ChangePasswordResponse extends SendablePacket {

    private final boolean _hasChanged;
    private final String _account;

    public ChangePasswordResponse(String account, boolean hasChanged) {
        _account = account;
        _hasChanged = hasChanged;
    }

    @Override
    protected void writeImpl() {
        writeC(0x06);
        writeS(_account);
        writeD(_hasChanged ? 1 : 0);
    }
}
