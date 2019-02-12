package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class GMViewQuestInfo extends L2GameServerPacket {
    private final Player player;

    public GMViewQuestInfo(Player player) {
        this.player = player;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x99);
        writeS(player.getName());

        List<Quest> quests = player.getAllActiveQuests();

        if (quests.size() == 0) {
            writeH(0);
            writeH(0);
            return;
        }

        writeH(quests.size());
        quests.forEach(q -> {
            writeD(q.id);
            QuestState qs = player.getQuestState(q);
            writeD(qs == null ? 0 : qs.getInt("cond"));
        });

        writeH(0); //количество элементов типа: ddQd , как-то связано с предметами
    }
}