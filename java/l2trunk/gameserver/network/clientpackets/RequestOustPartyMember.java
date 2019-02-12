package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.DimensionalRift;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;

public class RequestOustPartyMember extends L2GameClientPacket {
    //Format: cS
    private String _name;

    @Override
    protected void readImpl() {
        _name = readS(16);
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        Party party = activeChar.getParty();
        if (party == null || !activeChar.getParty().isLeader(activeChar)) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInOlympiadMode()) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.RequestOustPartyMember.CantOutOfGroup"));
            return;
        }

        Player member = party.getPlayerByName(_name);

        if (member == activeChar) {
            activeChar.sendActionFailed();
            return;
        }

        if (member == null) {
            activeChar.sendActionFailed();
            return;
        }

        Reflection r = party.getReflection();

        if (r != null && r instanceof DimensionalRift && member.getReflection().equals(r))
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.RequestOustPartyMember.CantOustInRift"));
        else if (r != null && !(r instanceof DimensionalRift))
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.RequestOustPartyMember.CantOustInDungeon"));
        else
            party.removePartyMember(member, true);
    }
}