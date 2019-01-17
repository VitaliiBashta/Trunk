package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.actor.instances.player.BookMark;

import java.util.List;

/**
 * dd d*[ddddSdS]
 */
public final class ExGetBookMarkInfo extends L2GameServerPacket {
    private final int bookmarksCapacity;
    private final List<BookMark> bookmarks;

    public ExGetBookMarkInfo(Player player) {
        bookmarksCapacity = player.bookmarks.getCapacity();
        bookmarks = player.bookmarks.getBookMarks();
    }

    @Override
    protected void writeImpl() {
        writeEx(0x84);

        writeD(0x00); // должно быть 0
        writeD(bookmarksCapacity);
        writeD(bookmarks.size());
        int slotId = 0;
        for (BookMark bookmark : bookmarks) {
            writeD(++slotId);
            writeD(bookmark.loc.x);
            writeD(bookmark.loc.y);
            writeD(bookmark.loc.z);
            writeS(bookmark.getName());
            writeD(bookmark.getIcon());
            writeS(bookmark.getAcronym());
        }
    }
}