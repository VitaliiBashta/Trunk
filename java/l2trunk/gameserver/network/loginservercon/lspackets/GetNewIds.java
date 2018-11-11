package l2trunk.gameserver.network.loginservercon.lspackets;

import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.network.loginservercon.AuthServerCommunication;
import l2trunk.gameserver.network.loginservercon.ReceivablePacket;
import l2trunk.gameserver.network.loginservercon.gspackets.SendNewIds;


public class GetNewIds extends ReceivablePacket {
    private int count;

    @Override
    public void readImpl() {
        count = readD();
    }

    @Override
    protected void runImpl() {
        int[] ids = new int[count];
        for (int i = 0; i < count; i++)
            ids[i] = IdFactory.getInstance().getNextId();

        AuthServerCommunication.getInstance().sendPacket(new SendNewIds(ids));
    }
}