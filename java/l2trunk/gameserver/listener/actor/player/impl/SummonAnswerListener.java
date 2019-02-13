package l2trunk.gameserver.listener.actor.player.impl;

import l2trunk.gameserver.listener.actor.player.OnAnswerListener;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.Location;

public final class SummonAnswerListener implements OnAnswerListener {
    private final Player player;
    private final Location location;
    private final long count;

    public SummonAnswerListener(Player player, Location loc, long count) {
        this.player = player;
        location = loc;
        this.count = count;
    }

    @Override
    public void sayYes() {
        if (player == null)
            return;

        player.abortAttack(true, true);
        player.abortCast(true, true);
        player.stopMove();
        if (count > 0) {
            if (player.getInventory().destroyItemByItemId(8615, count, "SummonPlayer")) {
                player.sendPacket(SystemMessage2.removeItems(8615, count));
                player.teleToLocation(location);
            } else
                player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
        } else
            player.teleToLocation(location);
    }

}
