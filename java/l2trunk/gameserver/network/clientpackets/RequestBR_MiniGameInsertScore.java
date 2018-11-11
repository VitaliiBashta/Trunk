package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.instancemanager.games.MiniGameScoreManager;
import l2trunk.gameserver.model.Player;

public class RequestBR_MiniGameInsertScore extends L2GameClientPacket {
    private int _score;

    @Override
    protected void readImpl() {
        _score = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null || !Config.EX_JAPAN_MINIGAME)
            return;

        MiniGameScoreManager.getInstance().insertScore(player, _score);
    }
}