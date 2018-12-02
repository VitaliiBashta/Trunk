package l2trunk.gameserver.utils;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.InventoryUpdate;

import java.util.List;

public class FixEnchantOlympiad {
    public static void storeEnchantItemsOly(Player player) {
        List<ItemInstance> arr = player.getInventory().getItems();
        int len = arr.size();
        StringBuilder items = new StringBuilder();

        for (ItemInstance _item : arr) {
            if (isMaxEnchant(_item)) {
                items.append(_item.getObjectId()).append(";").append(_item.getEnchantLevel()).append(":");

                if (_item.isWeapon())
                    _item.setEnchantLevel(Config.OLY_ENCHANT_LIMIT_WEAPON);
                if (_item.isArmor())
                    _item.setEnchantLevel(Config.OLY_ENCHANT_LIMIT_ARMOR);
                if (_item.isAccessory())
                    _item.setEnchantLevel(Config.OLY_ENCHANT_LIMIT_JEWEL);

                player.sendPacket(new InventoryUpdate().addModifiedItem(_item));
                player.broadcastUserInfo(true);
                player.broadcastCharInfo();
                player.setVar("EnItemOlyRec", items.toString(), -1);
            }
        }
    }

    private static boolean isMaxEnchant(ItemInstance item) {
        if ((item.isWeapon() && item.getEnchantLevel() > Config.OLY_ENCHANT_LIMIT_WEAPON)
                || (item.isArmor() && item.getEnchantLevel() > Config.OLY_ENCHANT_LIMIT_ARMOR)
                || (item.isAccessory() && item.getEnchantLevel() > Config.OLY_ENCHANT_LIMIT_JEWEL))
            return true;
        return false;
    }

    public static void restoreEnchantItemsOly(Player player) {
        if (player.getVar("EnItemOlyRec") == null)
            return;

        String var;
        var = player.getVar("EnItemOlyRec");
        if (var != null) {
            String[] items = var.split(":");
            for (String item : items) {
                if (item.equals(""))
                    continue;
                String[] values = item.split(";");
                if (values.length < 2)
                    continue;

                int oId = Integer.parseInt(values[0]);
                int enchant = Integer.parseInt(values[1]);

                ItemInstance itemToEnchant = player.getInventory().getItemByObjectId(oId);
                if (itemToEnchant == null)
                    continue;

                itemToEnchant.setEnchantLevel(enchant);
                itemToEnchant.setJdbcState(JdbcEntityState.UPDATED);
                itemToEnchant.update();

                player.sendPacket(new InventoryUpdate().addModifiedItem(itemToEnchant));
                player.broadcastUserInfo(true);
                player.broadcastCharInfo();
            }
        }

        player.unsetVar("EnItemOlyRec");
    }
}