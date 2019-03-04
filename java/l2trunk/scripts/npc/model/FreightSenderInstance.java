package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.MerchantInstance;
import l2trunk.gameserver.network.serverpackets.PackageToList;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.WarehouseFunctions;

public final class FreightSenderInstance extends MerchantInstance {
    public FreightSenderInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if ("deposit_items".equalsIgnoreCase(command))
            player.sendPacket(new PackageToList(player));
        else if ("withdraw_items".equalsIgnoreCase(command))
            WarehouseFunctions.showFreightWindow(player);
        else
            super.onBypassFeedback(player, command);
    }
}
