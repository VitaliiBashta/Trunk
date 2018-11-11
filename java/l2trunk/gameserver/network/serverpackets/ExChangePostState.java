package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.mail.Mail;

public class ExChangePostState extends L2GameServerPacket {
    private final boolean _receivedBoard;
    private final Mail[] _mails;
    private final int _changeId;

    public ExChangePostState(boolean receivedBoard, int type, Mail... n) {
        _receivedBoard = receivedBoard;
        _mails = n;
        _changeId = type;
    }

    @Override
    protected void writeImpl() {
        writeEx(0xB3);
        writeD(_receivedBoard ? 1 : 0);
        writeD(_mails.length);
        for (Mail mail : _mails) {
            writeD(mail.getMessageId()); // postId
            writeD(_changeId); // state
        }
    }
}