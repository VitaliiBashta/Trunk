package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.KrateisCubeEvent;

public final class RequestExStartShowCrataeCubeRank extends L2GameClientPacket {
    @Override
    protected void readImpl() {
        //
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;
        KrateisCubeEvent cubeEvent = player.getEvent(KrateisCubeEvent.class);
        if (cubeEvent == null)
            return;

        cubeEvent.showRank(player);

    }
}