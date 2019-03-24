package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ExListPartyMatchingWaitingRoom;

import java.util.ArrayList;
import java.util.List;

public final class RequestListPartyMatchingWaitingRoom extends L2GameClientPacket {
    private int minLevel, maxLevel, page;
    private List<Integer> classes = new ArrayList<>();

    @Override
    protected void readImpl() {
        page = readD();
        minLevel = readD();
        maxLevel = readD();
        int size = readD();
        if (size > Byte.MAX_VALUE || size < 0)
            size = 0;
        for (int i = 0; i < size; i++)
            classes.add(readD());
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        activeChar.sendPacket(new ExListPartyMatchingWaitingRoom(minLevel, maxLevel, page, classes));
    }
}