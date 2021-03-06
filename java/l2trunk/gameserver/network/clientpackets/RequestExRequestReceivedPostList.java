package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ExNoticePostArrived;
import l2trunk.gameserver.network.serverpackets.ExShowReceivedPostList;

/**
 * Отсылается при нажатии на кнопку "почта", "received mail" или уведомление от {@link ExNoticePostArrived}, запрос входящих писем.
 * В ответ шлется {@link ExShowReceivedPostList}
 */
public final class RequestExRequestReceivedPostList extends L2GameClientPacket {
    @Override
    protected void readImpl() {
        //just a trigger
    }

    @Override
    protected void runImpl() {
        Player cha = getClient().getActiveChar();
        if (cha != null) {
            cha.sendItemList(true);
            cha.sendItemList(false);
            cha.sendPacket(new ExShowReceivedPostList(cha));
        }


    }
}