package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class GMViewQuestInfo extends L2GameServerPacket {
    private final Player _cha;

    public GMViewQuestInfo(Player cha) {
        _cha = cha;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x99);
        writeS(_cha.getName());

        List<Quest> quests = _cha.getAllActiveQuests();

        if (quests.size() == 0) {
            writeH(0);
            writeH(0);
            return;
        }

        writeH(quests.size());
        for (Quest q : quests) {
            writeD(q.getQuestIntId());
            QuestState qs = _cha.getQuestState(q.getName());
            writeD(qs == null ? 0 : qs.getInt("cond"));
        }

        writeH(0); //количество элементов типа: ddQd , как-то связано с предметами
    }
}