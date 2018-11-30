package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.GameClient;
import l2trunk.gameserver.network.GameClient.GameClientState;
import l2trunk.gameserver.network.serverpackets.ActionFail;
import l2trunk.gameserver.network.serverpackets.CharSelected;

public class CharacterSelected extends L2GameClientPacket {
    private int _charSlot;

    /**
     * Format: cdhddd
     */
    @Override
    protected void readImpl() {
        _charSlot = readD();
    }

    @Override
    protected void runImpl() {
        GameClient client = getClient();

        if (client.getActiveChar() != null)
            return;

        int objId = client.getObjectIdForSlot(_charSlot);

        Player activeChar = client.loadCharFromDisk(_charSlot, objId);
        if (activeChar == null) {
            sendPacket(ActionFail.STATIC);
            return;
        }

        if (activeChar.getAccessLevel() < 0)
            activeChar.setAccessLevel(0);

        client.setState(GameClientState.IN_GAME);

        sendPacket(new CharSelected(activeChar, client.getSessionKey().playOkID1));
    }
}