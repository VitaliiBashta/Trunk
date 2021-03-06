package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.Inventory;
import l2trunk.gameserver.model.items.ItemAttributes;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.utils.ItemFunctions;

import java.util.List;

public final class AdminMasterwork implements IAdminCommandHandler {
    private static final int[] SLOTS_TO_MASTERWORK = {Inventory.PAPERDOLL_RHAND, Inventory.PAPERDOLL_LHAND, Inventory.PAPERDOLL_HEAD, Inventory.PAPERDOLL_LEGS, Inventory.PAPERDOLL_GLOVES, Inventory.PAPERDOLL_FEET};

    private static void showMainMasterwork(Player activeChar, Player target) {
        String html = HtmCache.INSTANCE.getNullable("admin/masterwork.htm");

        StringBuilder main = new StringBuilder("<table width=250>");

        for (int slot : SLOTS_TO_MASTERWORK) {
            ItemInstance item = target.getInventory().getPaperdollItem(slot);
            if (item != null && item.getTemplate().getMasterworkConvert() > 0) {
                main.append("<tr><td width=250>");
                main.append("<center>").append(item.getName());
                main.append("<br1>");
                main.append("<button value=\"Make Masterwork\" action=\"bypass -h admin_create_masterwork ").append(slot).append("\" width=200 height=25 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></center>");
                main.append("</td></tr>");
            }
        }
        main.append("</table>");

        html = html.replace("%main%", main.toString());
        activeChar.sendPacket(new NpcHtmlMessage(0).setHtml(html));
    }

    private static void createMasterwork(Player activeChar, Player target, int slot) {
        ItemInstance item = target.getInventory().getPaperdollItem(slot);
        if (item != null && item.getTemplate().getMasterworkConvert() > 0) {
            convertToMasterwork(target, item);
            activeChar.sendMessage("Item was converted to Masterwork!");
        } else
            activeChar.sendMessage("Item couldn't be converted!");
    }

    private static void convertToMasterwork(Player target, ItemInstance item) {
        int enchant = item.getEnchantLevel();
        ItemAttributes attributes = item.getAttributes();
        int augmentation = item.getAugmentationId();

        ItemInstance newItem = ItemFunctions.createItem(item.getTemplate().getMasterworkConvert());
        newItem.setEnchantLevel(enchant);
        newItem.setAttributes(attributes);
        newItem.setAugmentationId(augmentation);

        target.getInventory().destroyItem(item, "Admin Masterwork Convert");
        target.getInventory().addItem(newItem, "Admin Masterwork Convert");
    }

    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {

        if (!activeChar.getPlayerAccess().CanEditChar)
            return false;

        Player target;
        if (activeChar.getTarget() instanceof Player)
            target = (Player) activeChar.getTarget();
        else
            target = activeChar;

        if ("admin_masterwork".equals(comm)) {
            showMainMasterwork(activeChar, target);

        } else if ("admin_create_masterwork".equals(comm)) {
            int slot = Integer.parseInt(wordList[1]);
            createMasterwork(activeChar, target, slot);
            showMainMasterwork(activeChar, target);

        }

        return true;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_masterwork",
                "admin_create_masterwork");
    }
}
