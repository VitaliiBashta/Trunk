package l2trunk.scripts.handler.items;

import l2trunk.gameserver.data.xml.holder.EnchantItemHolder;
import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.WarehouseInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.ChooseInventoryItem;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.*;

public final class EnchantScrolls extends ScriptItemHandler implements ScriptFile {
    private static final Integer[] _itemIds = {
            729, 730, 731, 732, 947, 948, 949, 950, 951, 952, 953, 954, 955, 956,
            957, 958, 959, 960, 961, 962, 6569, 6570, 6571, 6572, 6573, 6574, 6575,
            6576, 6577, 6578, 13540, 22006, 22007, 22008, 22009, 22010, 22011, 22012,
            22013, 22014, 22015, 22016, 22017, 22018, 22019, 22020, 22021, 20517, 20518,
            20519, 20520, 20521, 20522, 21581, 21582, 22221, 22222, 22223, 22224, 22225,
            22226, 22227, 22228, 22229, 22230};


    @Override
    public boolean pickupItem(Playable playable, ItemInstance item) {
        return true;
    }

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {

    }

    @Override
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        if (playable == null || !playable.isPlayer())
            return false;
        Player player = (Player) playable;

        if (player.getEnchantScroll() != null)
            return false;

        if (player.isSitting()) {
            player.sendMessage("You can't enchant while sitting.");
            return false;
        }

        final List<NpcInstance> wh = player.getAroundNpc(200, 200);

        for (NpcInstance warehouse : wh) {
            if (warehouse instanceof WarehouseInstance) {
                player.sendMessage("You can't enchant near warehouse.");
                return false;
            }
        }

        player.setEnchantScroll(item);
        player.sendPacket(new ChooseInventoryItem(item.getItemId()));
        return true;
    }

    @Override
    public final List<Integer> getItemIds() {
        Set<Integer> enchantScrolls = new HashSet<>(EnchantItemHolder.getEnchantScrolls());
        enchantScrolls.addAll(Arrays.asList(_itemIds));
        return new ArrayList<>(enchantScrolls);
    }
}