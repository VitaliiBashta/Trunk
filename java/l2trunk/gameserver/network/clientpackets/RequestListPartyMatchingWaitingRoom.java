package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ExListPartyMatchingWaitingRoom;

import java.util.ArrayList;
import java.util.List;

public final class RequestListPartyMatchingWaitingRoom extends L2GameClientPacket {
    private int _minLevel, _maxLevel, _page;
    private List<Integer> _classes = new ArrayList<>();

    @Override
    protected void readImpl() {
        _page = readD();
        _minLevel = readD();
        _maxLevel = readD();
        int size = readD();
        if (size > Byte.MAX_VALUE || size < 0)
            size = 0;
        for (int i = 0; i < size; i++)
            _classes.add(readD());
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        activeChar.sendPacket(new ExListPartyMatchingWaitingRoom(activeChar, _minLevel, _maxLevel, _page, _classes));
    }
}