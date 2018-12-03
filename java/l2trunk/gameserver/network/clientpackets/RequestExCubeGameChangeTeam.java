package l2trunk.gameserver.network.clientpackets;


import l2trunk.gameserver.instancemanager.games.HandysBlockCheckerManager;
import l2trunk.gameserver.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Format: chdd
 * d: team
 */
public final class RequestExCubeGameChangeTeam extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(RequestExCubeGameChangeTeam.class);

    private int _team;
    private int _arena;

    @Override
    protected void readImpl() {
        _arena = readD() + 1;
        _team = readD();
    }

    @Override
    protected void runImpl() {
        if (HandysBlockCheckerManager.INSTANCE.arenaIsBeingUsed(_arena))
            return;
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null || activeChar.isDead())
            return;

        switch (_team) {
            case 0:
            case 1:
                // Change Player Team
                HandysBlockCheckerManager.INSTANCE.changePlayerToTeam(activeChar, _arena, _team);
                break;
            case -1: {
                int team = HandysBlockCheckerManager.INSTANCE.getHolder(_arena).getPlayerTeam(activeChar);
                // client sends two times this packet if click on exit
                // client did not send this packet on restart
                if (team > -1)
                    HandysBlockCheckerManager.INSTANCE.removePlayer(activeChar, _arena, team);
                break;
            }
            default:
                _log.warn("Wrong Team ID: " + _team);
                break;
        }
    }
}
