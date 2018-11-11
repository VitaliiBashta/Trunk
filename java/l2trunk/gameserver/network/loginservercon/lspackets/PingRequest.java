package l2trunk.gameserver.network.loginservercon.lspackets;

import l2trunk.gameserver.network.loginservercon.AuthServerCommunication;
import l2trunk.gameserver.network.loginservercon.ReceivablePacket;
import l2trunk.gameserver.network.loginservercon.gspackets.PingResponse;

public class PingRequest extends ReceivablePacket {
    @Override
    public void readImpl() {

    }

    @Override
    protected void runImpl() {
        AuthServerCommunication.getInstance().sendPacket(new PingResponse());
    }
}