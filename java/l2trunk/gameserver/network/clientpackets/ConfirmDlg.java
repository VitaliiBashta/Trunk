package l2trunk.gameserver.network.clientpackets;

import l2trunk.commons.lang.Pair;
import l2trunk.gameserver.listener.actor.player.OnAnswerListener;
import l2trunk.gameserver.model.Player;

public class ConfirmDlg extends L2GameClientPacket {
    private int _answer, _requestId;

    @Override
    protected void readImpl() {
        readD();
        _answer = readD();
        _requestId = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        Pair<Integer, OnAnswerListener> entry = activeChar.getAskListener(true);
        if (entry == null || entry.getKey() != _requestId)
            return;

        OnAnswerListener listener = entry.getValue();
        if (_answer == 1)
            listener.sayYes();
        else
            listener.sayNo();
    }
}