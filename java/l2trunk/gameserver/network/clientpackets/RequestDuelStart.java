package l2trunk.gameserver.network.clientpackets;


import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.impl.DuelEvent;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public final class RequestDuelStart extends L2GameClientPacket {
    private String name;
    private int duelType;

    @Override
    protected void readImpl() {
        name = readS(Config.CNAME_MAXLEN);
        duelType = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        if (player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }

        if (player.isProcessingRequest()) {
            player.sendPacket(SystemMsg.WAITING_FOR_ANOTHER_REPLY);
            return;
        }

        Player target = World.getPlayer(name);
        if (target == null || target == player) {
            player.sendPacket(SystemMsg.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL);
            return;
        }

        DuelEvent duelEvent = EventHolder.getEvent(EventType.PVP_EVENT, duelType);
        if (duelEvent == null)
            return;

        if (!duelEvent.canDuel(player, target, true))
            return;


        if (target.isBusy()) {
            player.sendPacket(new SystemMessage2(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(target));
            return;
        }

        duelEvent.askDuel(player, target);
    }
}