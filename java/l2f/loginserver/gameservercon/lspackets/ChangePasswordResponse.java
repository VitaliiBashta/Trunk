package l2f.loginserver.gameservercon.lspackets;

import l2f.loginserver.gameservercon.SendablePacket;

public class ChangePasswordResponse extends SendablePacket {

    boolean _hasChanged;
    private String _account;

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
