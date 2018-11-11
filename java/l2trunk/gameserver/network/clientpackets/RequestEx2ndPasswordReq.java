package l2trunk.gameserver.network.clientpackets;

/**
 * (ch)cS{S}
 * c: change pass?
 * S: current password
 * S: new password
 */
public final class RequestEx2ndPasswordReq extends L2GameClientPacket {
    private int _changePass;
    private String _password;
    private String _newPassword;

    @Override
    protected void readImpl() {
        _changePass = readC();
        _password = readS();
        if (_changePass == 2)
            _newPassword = readS();
    }

    @Override
    protected void runImpl() {
    }
}