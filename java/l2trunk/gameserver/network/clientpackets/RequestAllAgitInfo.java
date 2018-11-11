package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.network.serverpackets.ExShowAgitInfo;

public class RequestAllAgitInfo extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        getClient().getActiveChar().sendPacket(new ExShowAgitInfo());
    }
}