package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.data.xml.holder.HennaHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.HennaUnequipInfo;
import l2trunk.gameserver.templates.Henna;

public class RequestHennaUnequipInfo extends L2GameClientPacket {
    private int _symbolId;

    /**
     * format: d
     */
    @Override
    protected void readImpl() {
        _symbolId = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        Henna henna = HennaHolder.getInstance().getHenna(_symbolId);
        if (henna != null)
            player.sendPacket(new HennaUnequipInfo(henna, player));
    }
}