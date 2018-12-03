package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.fightclubmanager.FightClubEventManager;
import l2trunk.gameserver.model.entity.events.impl.AbstractFightClub;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminEvents implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().IsEventGm)
            return false;

        switch (command) {
            case admin_events:
                if (wordList.length == 1)
                    activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/events/events.htm"));
                else
                    activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/events/" + wordList[1].trim()));
                break;
            case admin_start_event:
                int id;
                try {
                    id = Integer.parseInt(wordList[1]);
                } catch (Exception e) {
                    activeChar.sendMessage("Use it like that: //start_event id(Id can be found in dir: data/events/fight_club)");
                    return false;
                }
                AbstractFightClub event = EventHolder.getInstance().getEvent(EventType.FIGHT_CLUB_EVENT, id);
                FightClubEventManager.INSTANCE.startEventCountdown(event);
                activeChar.sendMessage("Event Started!");
                break;
        }

        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private enum Commands {
        admin_events,
        admin_start_event
    }
}