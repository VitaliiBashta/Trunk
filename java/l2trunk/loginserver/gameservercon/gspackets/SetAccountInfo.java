package l2trunk.loginserver.gameservercon.gspackets;

import l2trunk.loginserver.accounts.SessionManager;
import l2trunk.loginserver.gameservercon.GameServer;
import l2trunk.loginserver.gameservercon.ReceivablePacket;

public final class SetAccountInfo extends ReceivablePacket {
    private String _account;
    private int _size;
    private int[] _deleteChars;

    @Override
    protected void readImpl() {
        _account = readS();
        _size = readC();
        int size = readD();
        if (size > 7 || size <= 0)
            _deleteChars = new int[0];
        else {
            _deleteChars = new int[size];
            for (int i = 0; i < _deleteChars.length; i++)
                _deleteChars[i] = readD();
        }
    }

    @Override
    protected void runImpl() {
        GameServer gs = getGameServer();
        if (gs.isAuthed()) {
            SessionManager.Session session = SessionManager.getInstance().getSessionByName(_account);
            if (session == null)
                return;
            session.getAccount().addAccountInfo(gs.getId(), _size, _deleteChars);
        }
    }
}
