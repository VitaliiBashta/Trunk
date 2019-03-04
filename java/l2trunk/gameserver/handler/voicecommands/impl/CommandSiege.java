package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.network.serverpackets.CastleSiegeInfo;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.Collections;
import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

/**
 * Command .siege which allows players to Participate to Castle Sieges or check their starting dates.
 */
public final class CommandSiege implements IVoicedCommandHandler {
    /**
     * Showing file command/siege.htm to the getPlayer
     *
     * @param activeChar Player that will receive the main Siege Page
     */
    private static void showMainPage(Player activeChar) {
        activeChar.sendPacket(new NpcHtmlMessage(0).setFile("command/siege.htm"));
    }

    /**
     * Shows Main Siege Page(@link #showMainPage) Also if target contains Castle Id, showing Siege Info of that Castle
     *
     * @param command    - "siege"
     * @param activeChar - getPlayer using command
     * @param target     - Empty(if just clicked .siege) or just number of castle siege
     * @return always true
     */
    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {
        if (!target.isEmpty()) {
            Castle castle = ResidenceHolder.getCastle(toInt(target));
            activeChar.sendPacket(new CastleSiegeInfo(castle, activeChar));
        }
        showMainPage(activeChar);
        return true;
    }

    @Override
    public List<String> getVoicedCommandList() {
        return List.of("siege");
    }

}
