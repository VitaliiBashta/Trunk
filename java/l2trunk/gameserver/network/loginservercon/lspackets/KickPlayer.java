package l2trunk.gameserver.network.loginservercon.lspackets;

import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.GameClient;
import l2trunk.gameserver.network.loginservercon.AuthServerCommunication;
import l2trunk.gameserver.network.loginservercon.ReceivablePacket;
import l2trunk.gameserver.network.serverpackets.ServerClose;

public class KickPlayer extends ReceivablePacket {
    private String account;

    @Override
    public void readImpl() {
        account = readS();
    }

    @Override
    protected void runImpl() {
        GameClient client = AuthServerCommunication.getInstance().removeWaitingClient(account);
        if (client == null)
            client = AuthServerCommunication.getInstance().removeAuthedClient(account);
        if (client == null)
            return;

        Player activeChar = client.getActiveChar();
        if (activeChar != null) {
            //FIXME [G1ta0] сообщение чаще всего не показывается, т.к. при закрытии соединения очередь на отправку очищается
            activeChar.sendPacket(Msg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);
            activeChar.kick();
        } else {
            client.close(ServerClose.STATIC);
        }
    }
}