package l2trunk.gameserver.network.clientpackets;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.dao.MailDAO;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.mail.Mail;
import l2trunk.gameserver.network.serverpackets.ExChangePostState;
import l2trunk.gameserver.network.serverpackets.ExReplyReceivedPost;
import l2trunk.gameserver.network.serverpackets.ExShowReceivedPostList;

/**
 * Запрос информации об полученном письме. Появляется при нажатии на письмо из списка {@link ExShowReceivedPostList}.
 *
 * @see RequestExRequestSentPost
 */
public class RequestExRequestReceivedPost extends L2GameClientPacket {
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

        Mail mail = MailDAO.getInstance().getReceivedMailByMailId(activeChar.getObjectId(), postId);
        if (mail != null) {
            if (mail.isUnread()) {
                mail.setUnread(false);
                mail.setJdbcState(JdbcEntityState.UPDATED);
                mail.update();
                activeChar.sendPacket(new ExChangePostState(true, Mail.READED, mail));
            }

            activeChar.sendPacket(new ExReplyReceivedPost(mail));
            return;
        }

        activeChar.sendPacket(new ExShowReceivedPostList(activeChar));
    }
}