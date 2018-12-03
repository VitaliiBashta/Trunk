package l2trunk.gameserver.network.clientpackets;


import l2trunk.gameserver.instancemanager.games.HandysBlockCheckerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Format: chddd
 * <p>
 * d: Arena
 * d: Answer
 */
public final class RequestExCubeGameReadyAnswer extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(RequestExCubeGameReadyAnswer.class);

    private int arena;
    private int answer;

    @Override
    protected void readImpl() {
        arena = readD() + 1;
        answer = readD();
    }

    @Override
    public void runImpl() {
        if (getClient().getActiveChar() == null)
            return;
        if (answer == 0) // Cancel
            return;

        if (answer == 1) {// OK or Time Over
            HandysBlockCheckerManager.INSTANCE.increaseArenaVotes(arena);
            return;
        }
        _log.warn("Unknown Cube Game Answer ID: " + answer);
    }
}
