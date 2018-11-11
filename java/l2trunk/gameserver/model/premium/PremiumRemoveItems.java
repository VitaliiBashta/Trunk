package l2trunk.gameserver.model.premium;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.ItemFunctions;

import java.util.ArrayList;
import java.util.List;

public class PremiumRemoveItems {
    private static final PremiumRemoveItems _instance = new PremiumRemoveItems();
    private final List<PremiumGift> _list = new ArrayList<>();

    public static PremiumRemoveItems getInstance() {
        return _instance;
    }

    void remove(Player player) {
        boolean removed = false;
        for (PremiumGift gift : _list) {
            ItemFunctions.removeItem(player, gift.getId(), gift.getCount(), true, "removed");
        }

        if (removed) {
            player.sendPacket(new SystemMessage(SystemMsg.THE_PREMIUM_ACCOUNT_HAS_BEEN_TERMINATED));
        }
    }

    public void add(PremiumGift gift) {
        _list.add(gift);
    }
}
