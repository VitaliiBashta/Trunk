package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.scripts.quests._255_Tutorial;

public final class RequestTutorialClientEvent extends L2GameClientPacket {
    // format: cd
    private int event = 0;

    /**
     * Пакет от клиента, если вы в туториале подергали мышкой как надо - клиент пришлет его со значением 1 ну или нужным ивентом
     */
    @Override
    protected void readImpl() {
        event = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        Quest tutorial = QuestManager.getQuest(_255_Tutorial.class);
            player.processQuestEvent(tutorial, "CE" + event, null);
    }
}