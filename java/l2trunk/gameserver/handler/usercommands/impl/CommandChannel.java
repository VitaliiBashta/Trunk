package l2trunk.gameserver.handler.usercommands.impl;

import l2trunk.gameserver.handler.usercommands.IUserCommandHandler;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ExMultiPartyCommandChannelInfo;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.List;

/**
 * Support for CommandChannel commands:<br>
 * 92 /channelcreate<br>
 * 93 /channeldelete<br>
 * 94 /channelinvite [party leader] отправляет пакет RequestExMPCCAskJoin<br>
 * 95 /channelkick [party leader] отправляет пакет RequestExMPCCExit<br>
 * 96 /channelleave<br>
 * 97 /channelinfo<br>
 *
 * @author SYS
 */
public final class CommandChannel implements IUserCommandHandler {
    private static final List<Integer> COMMAND_IDS = List.of(92, 93, 96, 97);

    @Override
    public boolean useUserCommand(int id, Player activeChar) {
        if (!COMMAND_IDS.contains(id))
            return false;

        switch (id) {
            case 92: // channelcreate
                // "Используйте команду /channelinvite"
                activeChar.sendMessage(new CustomMessage("usercommandhandlers.CommandChannel"));
                break;
            case 93: // channeldelete
                if (!activeChar.isInParty() || !activeChar.getParty().isInCommandChannel())
                    return true;
                if (activeChar.getParty().getCommandChannel().getLeader() == activeChar) {
                    l2trunk.gameserver.model.CommandChannel channel = activeChar.getParty().getCommandChannel();
                    channel.disbandChannel();
                } else
                    activeChar.sendPacket(SystemMsg.ONLY_THE_CREATOR_OF_A_COMMAND_CHANNEL_CAN_USE_THE_CHANNEL_DISMISS_COMMAND);
                break;
            case 96: // channelleave
                // FIXME создатель канала вылетел, надо автоматом передать кому-то права
                if (!activeChar.isInParty() || !activeChar.getParty().isInCommandChannel())
                    return true;
                if (!activeChar.getParty().isLeader(activeChar)) {
                    activeChar.sendPacket(SystemMsg.ONLY_A_PARTY_LEADER_CAN_LEAVE_A_COMMAND_CHANNEL);
                    return true;
                }
                l2trunk.gameserver.model.CommandChannel channel = activeChar.getParty().getCommandChannel();

                // Лидер СС не может покинуть СС, можно только распустить СС
                // FIXME по идее может, права автоматом должны передаться другой партии
                if (channel.getLeader() == activeChar) {
                    if (channel.getParties().size() > 1)
                        return false;

                    // Закрываем СС, если в СС 1 партия и лидер нажал Quit
                    channel.disbandChannel();
                    return true;
                }

                Party party = activeChar.getParty();
                channel.removeParty(party);
                party.sendPacket(SystemMsg.YOU_HAVE_QUIT_THE_COMMAND_CHANNEL);
                channel.sendPacket(new SystemMessage2(SystemMsg.C1S_PARTY_HAS_LEFT_THE_COMMAND_CHANNEL).addString(activeChar.getName()));
                break;
            case 97: // channelinfo
                if (!activeChar.isInParty() || !activeChar.getParty().isInCommandChannel())
                    return false;
                activeChar.sendPacket(new ExMultiPartyCommandChannelInfo(activeChar.getParty().getCommandChannel()));
                break;
        }
        return true;
    }

    @Override
    public final List<Integer> getUserCommandList() {
        return COMMAND_IDS;
    }
}