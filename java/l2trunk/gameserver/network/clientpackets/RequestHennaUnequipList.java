package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.HennaUnequipList;

public class RequestHennaUnequipList extends L2GameClientPacket {
    private int _symbolId;

    /**
     * format: d
     */
    @Override
    protected void readImpl() {
        _symbolId = readD(); //?
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        HennaUnequipList he = new HennaUnequipList(activeChar);
        activeChar.sendPacket(he);
    }
}