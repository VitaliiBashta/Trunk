package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.achievements.Achievements;
import l2trunk.gameserver.model.quest.Quest;

public class RequestTutorialLinkHtml extends L2GameClientPacket {
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

        Quest q = QuestManager.getQuest(255);
        if (q != null)
            player.processQuestEvent(q.getName(), _bypass, null);

        if (_bypass.startsWith("_bbs_achievements")) {
            _bypass = _bypass.replaceAll("%", " ");

            if (_bypass.length() < 5)
                return;

            Achievements.INSTANCE.onBypass(player, _bypass, null);
        }
    }
}