package l2f.gameserver.network.clientpackets;

import l2f.gameserver.Config;
import l2f.gameserver.SecondaryPasswordAuth;
import l2f.gameserver.network.serverpackets.Ex2ndPasswordAck;

/**
 * (ch)cS{S}
 * c: change pass?
 * S: current password
 * S: new password
 */
public final class RequestEx2ndPasswordReq extends L2GameClientPacket {
    int _changePass;
    String _password, _newPassword;

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