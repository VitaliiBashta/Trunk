package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.skills.skillclasses.Call;
import l2trunk.gameserver.utils.Location;

import java.util.Collections;
import java.util.List;

public class Relocate extends Functions implements IVoicedCommandHandler {
    @Override
    public List<String> getVoicedCommandList() {
        return Collections.singletonList("km-all-to-me");
    }

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {
        if (!Config.ENABLE_KM_ALL_TO_ME) {
            return false;
        }
        if (command.equalsIgnoreCase("km-all-to-me")) {
            if (!activeChar.isClanLeader()) {
                activeChar.sendPacket(Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
                return false;
            }
            SystemMessage msg = Call.canSummonHere(activeChar);
            if (msg != null) {
                activeChar.sendPacket(msg);
                return false;
            }
            List<Player> players = activeChar.getClan().getOnlineMembers(activeChar.getObjectId());
            for (Player player : players) {
                if (Call.canBeSummoned(activeChar, player) == null) {
                    player.summonCharacterRequest(activeChar, Location.findAroundPosition(activeChar, 100, 150), 5);
                }
            }
            return true;
        }
        return false;
    }
}