package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.achievements.Achievements;
import l2trunk.gameserver.model.quest.Quest;

public final class RequestTutorialQuestionMark extends L2GameClientPacket {
    // format: cd
    private int number = 0;

    @Override
    protected void readImpl() {
        number = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;
        Quest q = QuestManager.getQuest(255);
        if (q != null)
            player.processQuestEvent(q.getName(), "QM" + number, null);

        if (number == player.getObjectId())
            Achievements.INSTANCE.onBypass(player, "_bbs_achievements", null);
    }
}