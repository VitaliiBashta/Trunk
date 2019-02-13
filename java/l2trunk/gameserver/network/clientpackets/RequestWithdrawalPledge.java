package l2trunk.gameserver.network.clientpackets;

import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.UnitMember;
import l2trunk.gameserver.network.serverpackets.PledgeShowMemberListDelete;
import l2trunk.gameserver.network.serverpackets.PledgeShowMemberListDeleteAll;
import l2trunk.gameserver.network.serverpackets.SkillList;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public final class RequestWithdrawalPledge extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        //is the guy in a clan  ?
        if (activeChar.getClanId() == 0) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInCombat()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_LEAVE_A_CLAN_WHILE_ENGAGED_IN_COMBAT);
            return;
        }

        Clan clan = activeChar.getClan();
        if (clan == null)
            return;

        UnitMember member = clan.getAnyMember(activeChar.objectId());
        if (member == null) {
            activeChar.sendActionFailed();
            return;
        }

        if (member.isClanLeader()) {
            activeChar.sendMessage("A clan leader may not be dismissed.");
            return;
        }

        DominionSiegeEvent siegeEvent = activeChar.getEvent(DominionSiegeEvent.class);
        if (siegeEvent != null && siegeEvent.isInProgress()) {
            activeChar.sendPacket(SystemMsg.THIS_CLAN_MEMBER_CANNOT_WITHDRAW_OR_BE_EXPELLED_WHILE_PARTICIPATING_IN_A_TERRITORY_WAR);
            return;
        }

        int subUnitType = activeChar.getPledgeType();

        clan.removeClanMember(subUnitType, activeChar.objectId());

        clan.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.S1_HAS_WITHDRAWN_FROM_THE_CLAN).addString(activeChar.getName()), new PledgeShowMemberListDelete(activeChar.getName()));

        if (subUnitType == Clan.SUBUNIT_ACADEMY)
            activeChar.setLvlJoinedAcademy(0);

        activeChar.setClan(null);
        if (!activeChar.isNoble())
            activeChar.setTitle(StringUtils.EMPTY);

        activeChar.setLeaveClanCurTime();
        activeChar.broadcastCharInfo();
        activeChar.sendPacket(new SkillList(activeChar));

        activeChar.sendPacket(SystemMsg.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN, PledgeShowMemberListDeleteAll.STATIC);
    }
}