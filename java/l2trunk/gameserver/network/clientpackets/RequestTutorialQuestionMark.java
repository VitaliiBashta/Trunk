package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.achievements.Achievements;
import l2trunk.scripts.quests._255_Tutorial;

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
        player.processQuestEvent(_255_Tutorial.class, "QM" + number, null);

        if (number == player.getObjectId())
            Achievements.INSTANCE.onBypass(player, "_bbs_achievements", null);
    }
}