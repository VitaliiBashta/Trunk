package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.dao.MailDAO;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.mail.Mail;
import l2trunk.gameserver.network.serverpackets.ExReplySentPost;
import l2trunk.gameserver.network.serverpackets.ExShowSentPostList;

/**
 * Запрос информации об отправленном письме. Появляется при нажатии на письмо из списка {@link ExShowSentPostList}.
 * В ответ шлется {@link ExReplySentPost}.
 *
 * @see RequestExRequestReceivedPost
 */
public class RequestExRequestSentPost extends L2GameClientPacket {
    private int postId;

    /**
     * format: d
     */
    @Override
    protected void readImpl() {
        postId = readD(); // id письма
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        Mail mail = MailDAO.getInstance().getSentMailByMailId(activeChar.objectId(), postId);
        if (mail != null) {
            activeChar.sendPacket(new ExReplySentPost(mail));
            return;
        }

        activeChar.sendPacket(new ExShowSentPostList(activeChar));
    }
}