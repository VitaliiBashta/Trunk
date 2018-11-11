package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ExListMpccWaiting;

/**
 * @author VISTALL
 */
public class RequestExListMpccWaiting extends L2GameClientPacket {
    private int _listId;
    private int _locationId;
    private boolean _allLevels;

    @Override
    protected void readImpl() {
        _listId = readD();
        _locationId = readD();
        _allLevels = readD() == 1;
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        player.sendPacket(new ExListMpccWaiting(player, _listId, _locationId, _allLevels));
    }
}