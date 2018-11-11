package l2trunk.gameserver.network.loginservercon.lspackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.GameClient;
import l2trunk.gameserver.network.loginservercon.AuthServerCommunication;
import l2trunk.gameserver.network.loginservercon.ReceivablePacket;


public class ChangePasswordResponse extends ReceivablePacket {
    private String account;
    private boolean changed;

    @Override
    public void readImpl() {
        account = readS();
        changed = readD() == 1;
    }

    @Override
    protected void runImpl() {
        GameClient client = AuthServerCommunication.getInstance().getAuthedClient(account);
        if (client == null)
            return;

        Player activeChar = client.getActiveChar();

        if (activeChar == null)
            return;

        if (changed) {
            activeChar.sendMessage("Your password has been changed!");

            //Log.LogAction(activeChar, "Password", "Successfully changed password!");
        } else {
            activeChar.sendMessage("Current password is incorrect!");

            //Log.LogAction(activeChar, "Password", "!!!!!!!! UNSUCCESSFULLY TRIED TO CHANGE PASSWORD IN GAME!!!!!!!!");
        }
    }
}