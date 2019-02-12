package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.DimensionalRift;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;

public final class RequestWithDrawalParty extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        Party party = activeChar.getParty();
        if (party == null) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInOlympiadMode()) {
            activeChar.sendMessage("Вы не можете сейчас выйти из группы."); //TODO [G1ta0] custom message
            return;
        }

        Reflection r = activeChar.getParty().getReflection();
        if (r instanceof DimensionalRift && activeChar.getReflection().equals(r))
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.RequestWithDrawalParty.Rift"));
        else if (r != null && activeChar.isInCombat())
            activeChar.sendMessage("Вы не можете сейчас выйти из группы.");
        else
            activeChar.leaveParty();
    }
}