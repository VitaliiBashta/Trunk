package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.network.serverpackets.Ex2ndPasswordCheck;

/**
 * Format: (ch)
 */
public class RequestEx2ndPasswordCheck extends L2GameClientPacket {
    @Override
    protected void readImpl() {

    }

    @Override
    protected void runImpl() {
        if (getClient().getSecondaryAuth().isAuthed()) {
            sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheck.PASSWORD_OK));
            return;
        }

        getClient().getSecondaryAuth().openDialog();
    }
}