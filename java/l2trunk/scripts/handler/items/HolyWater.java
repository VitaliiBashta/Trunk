package l2trunk.scripts.handler.items;

import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.scripts.npc.model.HellboundRemnantInstance;

import java.util.Collections;
import java.util.List;

public final class HolyWater extends SimpleItemHandler implements ScriptFile {
    private static final int ITEM_IDS = 9673;

    @Override
    public List<Integer> getItemIds() {
        return List.of(ITEM_IDS);
    }


    @Override
    public boolean pickupItem(Playable playable, ItemInstance item) {
        return true;
    }

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl) {
        GameObject target = player.getTarget();

        if (!(target instanceof HellboundRemnantInstance)) {
            player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }

        HellboundRemnantInstance npc = (HellboundRemnantInstance) target;
        if (npc.isDead()) {
            player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }

        player.broadcastPacket(new MagicSkillUse(player, npc, 2358));
        npc.onUseHolyWater(player);

        return true;
    }
}
