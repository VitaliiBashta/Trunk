package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.achievements.Achievements;
import l2trunk.gameserver.model.entity.events.fightclubmanager.FightClubEventManager;
import l2trunk.gameserver.model.quest.Quest;

public class RequestTutorialQuestionMark extends L2GameClientPacket {
    // format: cd
    private int _number = 0;

    @Override
    protected void readImpl() {
        _number = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;
        if (player.isInFightClub()) {
            FightClubEventManager.getInstance().sendEventPlayerMenu(player);
        } else {
            Quest q = QuestManager.getQuest(255);
            if (q != null)
                player.processQuestEvent(q.getName(), "QM" + _number, null);

            if (_number == player.getObjectId())
                Achievements.INSTANCE.onBypass(player, "_bbs_achievements", null);
        }
    }
}