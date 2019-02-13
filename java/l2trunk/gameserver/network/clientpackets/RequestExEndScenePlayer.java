package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;

public final class RequestExEndScenePlayer extends L2GameClientPacket {
    private int movieId;

    @Override
    protected void readImpl() {
        movieId = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;
        if (!activeChar.isInMovie() || activeChar.getMovieId() != movieId) {
            activeChar.sendActionFailed();
            return;
        }
        activeChar.setIsInMovie(false);
        activeChar.setMovieId(0);
        activeChar.decayMe();
        activeChar.spawnMe();
    }

}