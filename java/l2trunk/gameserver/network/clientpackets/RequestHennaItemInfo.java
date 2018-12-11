package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.data.xml.holder.HennaHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.HennaItemInfo;
import l2trunk.gameserver.templates.Henna;

public class RequestHennaItemInfo extends L2GameClientPacket {
    // format  cd
    private int _symbolId;

    @Override
    protected void readImpl() {
        _symbolId = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        Henna henna = HennaHolder.getHenna(_symbolId);
        player.sendPacket(new HennaItemInfo(henna, player));
    }
}