package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.dao.MailDAO;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.mail.Mail;
import l2trunk.gameserver.network.serverpackets.ExShowSentPostList;

import java.util.Collection;
import java.util.List;

/**
 * Запрос на удаление отправленных сообщений. Удалить можно только письмо без вложения. Отсылается при нажатии на "delete" в списке отправленных писем.
 */
public final class RequestExDeleteSentPost extends L2GameClientPacket {
    private int count;
    private List<Integer> list;

    /**
     * format: dx[d]
     */
    @Override
    protected void readImpl() {
        count = readD(); // количество элементов для удаления
        if (count * 4 > buf.remaining() || count > Short.MAX_VALUE || count < 1) {
            count = 0;
            return;
        }
//        list = new int[count];
        for (int i = 0; i < count; i++)
            list.add(readD()); // уникальный номер письма
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null || count == 0)
            return;

        Collection<Mail> mails = MailDAO.getInstance().getSentMailByOwnerId(activeChar.objectId());
        mails.stream()
                .filter(mail -> list.contains(mail.getMessageId()))
                .filter(mail -> mail.getAttachments().isEmpty())
                .forEach(mail -> MailDAO.getInstance().deleteSentMailByMailId(activeChar.objectId(), mail.getMessageId()));
        activeChar.sendPacket(new ExShowSentPostList(activeChar));
    }
}