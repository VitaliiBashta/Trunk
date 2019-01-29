package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.achievements.Achievements;
import l2trunk.scripts.quests._255_Tutorial;

public final class RequestTutorialLinkHtml extends L2GameClientPacket {
    // format: cS

    private String _bypass;

    @Override
    protected void readImpl() {
        _bypass = readS();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        player.processQuestEvent(_255_Tutorial.class, _bypass, null);

        if (_bypass.startsWith("_bbs_achievements")) {
            _bypass = _bypass.replaceAll("%", " ");

            if (_bypass.length() < 5)
                return;

            Achievements.INSTANCE.onBypass(player, _bypass, null);
        }
    }
}