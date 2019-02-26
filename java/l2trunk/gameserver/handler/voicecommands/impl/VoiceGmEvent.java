package l2trunk.gameserver.handler.voicecommands.impl;

import Elemental.managers.GmEventManager;
import Elemental.managers.GmEventManager.StateEnum;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;

public final class VoiceGmEvent extends Functions implements IVoicedCommandHandler {
    private static final List<String> VOICED_COMMANDS = List.of("gmevent");

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String args) {
        // Evento no disponible o no en periodo de registro
        if (GmEventManager.INSTANCE.getEventStatus() != StateEnum.REGISTERING)
            return false;
        // Menu principal
        if (args == null || args.isEmpty()) {
            final NpcHtmlMessage html = new NpcHtmlMessage(0);
            html.setFile("events/GmEvent.htm");
            activeChar.sendPacket(html);
            return true;
        }

        switch (args) {
            case "register":
                GmEventManager.INSTANCE.registerToEvent(activeChar);
                break;
            case "unregister":
                GmEventManager.INSTANCE.unregisterOfEvent(activeChar);
                break;
        }
        return true;
    }

    @Override
    public List<String> getVoicedCommandList() {
        return VOICED_COMMANDS;
    }
}
