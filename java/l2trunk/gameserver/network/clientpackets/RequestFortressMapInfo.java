package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Fortress;
import l2trunk.gameserver.network.serverpackets.ExShowFortressMapInfo;

public final class RequestFortressMapInfo extends L2GameClientPacket {
    private int fortressId;

    @Override
    protected void readImpl() {
        fortressId = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;
        Fortress fortress = ResidenceHolder.getFortress(fortressId);
        sendPacket(new ExShowFortressMapInfo(fortress));
    }
}