/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2trunk.gameserver.handler.voicecommands.impl;

import Elemental.managers.GmEventManager;
import Elemental.managers.GmEventManager.StateEnum;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.scripts.Functions;

import java.util.Collections;
import java.util.List;

public class VoiceGmEvent extends Functions implements IVoicedCommandHandler {
    private static final List<String> VOICED_COMMANDS = Collections.singletonList("gmevent");

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String args) {
        // Evento no disponible o no en periodo de registro
        if (GmEventManager.getInstance().getEventStatus() != StateEnum.REGISTERING)
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
                GmEventManager.getInstance().registerToEvent(activeChar);
                break;
            case "unregister":
                GmEventManager.getInstance().unregisterOfEvent(activeChar);
                break;
        }
        return true;
    }

    @Override
    public List<String> getVoicedCommandList() {
        return VOICED_COMMANDS;
    }
}
