package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class RequestQuestAbort extends L2GameClientPacket {
    private int questID;

    @Override
    protected void readImpl() {
        questID = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        Quest quest = QuestManager.getQuest(questID);
        if (activeChar == null || quest == null || activeChar.isBlocked())
            return;

        if (!quest.canAbortByPacket())
            return;

        QuestState qs = activeChar.getQuestState(quest);
        if (qs != null && !qs.isCompleted())
            qs.abortQuest();
    }
}