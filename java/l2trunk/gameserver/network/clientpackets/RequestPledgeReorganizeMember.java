package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.SubUnit;
import l2trunk.gameserver.model.pledge.UnitMember;
import l2trunk.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public final class RequestPledgeReorganizeMember extends L2GameClientPacket {
    // format: (ch)dSdS
    private int _replace;
    private String _subjectName;
    private int _targetUnit;
    private String _replaceName;

    @Override
    protected void readImpl() {
        _replace = readD();
        _subjectName = readS(16);
        _targetUnit = readD();
        if (_replace > 0)
            _replaceName = readS();
    }

    @Override
    protected void runImpl() {
        //LOG.warn("Received RequestPledgeReorganizeMember("+_arg1+","+_arg2+","+_arg3+","+_arg4+") from getPlayer "+getClient().getActiveChar().name());

        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        Clan clan = activeChar.getClan();
        if (clan == null) {
            activeChar.sendActionFailed();
            return;
        }

        if (!activeChar.isClanLeader()) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.RequestPledgeReorganizeMember.ChangeAffiliations"));
            activeChar.sendActionFailed();
            return;
        }

        UnitMember subject = clan.getAnyMember(_subjectName);
        if (subject == null) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.RequestPledgeReorganizeMember.NotInYourClan"));
            activeChar.sendActionFailed();
            return;
        }

        if (subject.getPledgeType() == _targetUnit) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.RequestPledgeReorganizeMember.AlreadyInThatCombatUnit"));
            activeChar.sendActionFailed();
            return;
        }

        if (_targetUnit != 0 && clan.getSubUnit(_targetUnit) == null) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.RequestPledgeReorganizeMember.NoSuchCombatUnit"));
            activeChar.sendActionFailed();
            return;
        }

        if (clan.isAcademy(_targetUnit)) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.RequestPledgeReorganizeMember.AcademyViaInvitation"));
            activeChar.sendActionFailed();
            return;
        }
        /*
         * unsure for next check, but anyway as workaround before academy refactoring
         * (needs LvlJoinedAcademy to be put on UnitMember if so, to be able relocate from academy correctly)
         */
        if (clan.isAcademy(subject.getPledgeType())) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.RequestPledgeReorganizeMember.CantMoveAcademyMember"));
            activeChar.sendActionFailed();
            return;
        }

        UnitMember replacement = null;

        if (_replace > 0) {
            replacement = clan.getAnyMember(_replaceName);
            if (replacement == null) {
                activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.RequestPledgeReorganizeMember.CharacterNotBelongClan"));
                activeChar.sendActionFailed();
                return;
            }
            if (replacement.getPledgeType() != _targetUnit) {
                activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.RequestPledgeReorganizeMember.CharacterNotBelongCombatUnit"));
                activeChar.sendActionFailed();
                return;
            }
            if (replacement.isSubLeader()) {
                activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.RequestPledgeReorganizeMember.CharacterLeaderAnotherCombatUnit"));
                activeChar.sendActionFailed();
                return;
            }
        } else {
            if (clan.getUnitMembersSize(_targetUnit) >= clan.getSubPledgeLimit(_targetUnit)) {
                if (_targetUnit == Clan.SUBUNIT_MAIN_CLAN)
                    activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_IS_FULL_AND_CANNOT_ACCEPT_ADDITIONAL_CLAN_MEMBERS_AT_THIS_TIME).addString(clan.getName()));
                else
                    activeChar.sendPacket(SystemMsg.THE_ACADEMYROYAL_GUARDORDER_OF_KNIGHTS_IS_FULL_AND_CANNOT_ACCEPT_NEW_MEMBERS_AT_THIS_TIME);
                activeChar.sendActionFailed();
                return;
            }

            if (subject.isSubLeader()) {
                activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.RequestPledgeReorganizeMember.MemberLeaderAnotherUnit"));
                activeChar.sendActionFailed();
                return;
            }

        }

        SubUnit oldUnit;

        if (replacement != null) {
            oldUnit = replacement.getSubUnit();

            oldUnit.replace(replacement.objectId(), subject.getPledgeType());

            clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(replacement));

            if (replacement.isOnline()) {
                replacement.getPlayer().updatePledgeClass();
                replacement.getPlayer().broadcastCharInfo();
            }
        }

        oldUnit = subject.getSubUnit();

        oldUnit.replace(subject.objectId(), _targetUnit);

        clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(subject));

        if (subject.isOnline()) {
            subject.getPlayer().updatePledgeClass();
            subject.getPlayer().broadcastCharInfo();
        }
    }
}