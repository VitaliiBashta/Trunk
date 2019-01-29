package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExQuestNpcLogList;

public final class RequestAddExpandQuestAlarm extends L2GameClientPacket {
    private int questId;

    @Override
    protected void readImpl() {
        questId = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        Quest quest = QuestManager.getQuest(questId);
        if (quest == null)
            return;

        QuestState state = player.getQuestState(quest);
        if (state == null)
            return;

        player.sendPacket(new ExQuestNpcLogList(state));
    }
}
