package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.dao.MailDAO;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.mail.Mail;
import l2trunk.gameserver.network.serverpackets.ExShowReceivedPostList;

import java.util.ArrayList;
import java.util.List;

public final class RequestExDeleteReceivedPost extends L2GameClientPacket {
    private int _count;
    private List<Integer> _list;

    /**
     * format: dx[d]
     */
    @Override
    protected void readImpl() {
        _count = readD();
        if (_count * 4 > buf.remaining() || _count > Short.MAX_VALUE || _count < 1) {
            _count = 0;
            return;
        }
        _list = new ArrayList<>(_count); // количество элементов для удаления
        for (int i = 0; i < _count; i++)
            _list.add(readD()); // уникальный номер письма
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null || _count == 0)
            return;

        List<Mail> mails = MailDAO.getInstance().getReceivedMailByOwnerId(activeChar.objectId());
        if (!mails.isEmpty()) {
            for (Mail mail : mails)
                if (_list.contains(mail.getMessageId()))
                    if (mail.getAttachments().isEmpty()) {
                        MailDAO.getInstance().deleteReceivedMailByMailId(activeChar.objectId(), mail.getMessageId());
                    }
        }

        activeChar.sendPacket(new ExShowReceivedPostList(activeChar));
    }
}