package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.actor.instances.player.BookMark;
import l2trunk.gameserver.network.serverpackets.ExGetBookMarkInfo;

/**
 * dSdS
 */
public final class RequestModifyBookMarkSlot extends L2GameClientPacket {
    private String name, acronym;
    private int icon, slot;

    @Override
    protected void readImpl() {
        slot = readD();
        name = readS(32);
        icon = readD();
        acronym = readS(4);
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar != null) {
            final BookMark mark = activeChar.bookmarks.get(slot);
            if (mark != null) {
                mark.setName(name)
                        .setIcon(icon)
                        .setAcronym(acronym);
                activeChar.sendPacket(new ExGetBookMarkInfo(activeChar));
            }
        }
    }
}