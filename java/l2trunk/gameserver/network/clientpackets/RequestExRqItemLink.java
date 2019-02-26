package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.cache.ItemInfoCache;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInfo;
import l2trunk.gameserver.network.serverpackets.ActionFail;
import l2trunk.gameserver.network.serverpackets.ExRpItemLink;

public final class RequestExRqItemLink extends L2GameClientPacket {
    private int objectId;

    @Override
    protected void readImpl() {
        objectId = readD();
    }

    @Override
    protected void runImpl() {
        ItemInfo item;
        if ((item = ItemInfoCache.INSTANCE.get(objectId)) == null) {
            // Nik: Support for question mark listeners. Used for party find and other shits. objectId is used as the questionMarkId. Use with caution.
            getClient().getActiveChar().getListeners().onQuestionMarkClicked(objectId);

            if (objectId >= 5000000 && objectId < 6000000) {
                Player player = getClient().getActiveChar();
                String varName = "DisabledAnnounce" + objectId;
                if (!player.containsQuickVar(varName)) {
                    player.addQuickVar(varName, true);
                    player.sendMessage("Announcement Disabled!");
                }
            }
            sendPacket(ActionFail.STATIC);
        } else
            sendPacket(new ExRpItemLink(item));
    }
}