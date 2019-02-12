package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.base.Element;
import l2trunk.gameserver.model.instances.PetInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.InventoryUpdate;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Location;

import static l2trunk.commons.lang.NumberUtils.toInt;
import static l2trunk.commons.lang.NumberUtils.toLong;

public class AdminCreateItem implements IAdminCommandHandler {
    @SuppressWarnings("rawtypes")
    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().UseGMShop)
            return false;

        switch (command) {
            case admin_itemcreate:
                activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/itemcreation.htm"));
                break;
            case admin_ci:
            case admin_create_item:
                if (wordList.length < 2) {
                    activeChar.sendMessage("USAGE: create_item id [count]");
                    return false;
                }

                int item_id = toInt(wordList[1]);
                long item_count = wordList.length < 3 ? 1 : Long.parseLong(wordList[2]);
                createItem(activeChar, item_id, item_count);
                activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/itemcreation.htm"));
                break;
            case admin_create_item_hwid:
                if (wordList.length < 2) {
                    activeChar.sendMessage("USAGE: create_item id [count]");
                    return false;
                }

                item_id = toInt(wordList[1]);
                item_count = wordList.length < 3 ? 1 : Long.parseLong(wordList[2]);
                GameObjectsStorage.getAllPlayersStream()
                        .filter(player -> player.getNetConnection() != null)
                        .filter(player -> !player.isInStoreMode())
                        .forEach(player -> {
                            createItem(player, item_id, item_count);
                            player.sendMessage("You have been rewarded!");
                        });
                activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/itemcreation.htm"));
                break;
            case admin_create_item_char:
                if (wordList.length < 2) {
                    activeChar.sendMessage("USAGE: create_item id [count]");
                    return false;
                }

                item_id = toInt(wordList[1]);
                item_count = wordList.length < 3 ? 1 : Long.parseLong(wordList[2]);
                GameObjectsStorage.getAllPlayersStream()
                        .filter(player -> player.getNetConnection() != null)
                        .filter(player -> !player.isInStoreMode())
                        .forEach(player -> createItem(player, item_id, item_count));
                activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/itemcreation.htm"));
                break;
            case admin_create_item_target:
                GameObject target = activeChar.getTarget();
                if ((target instanceof Player || target instanceof PetInstance)) {
                    if (wordList.length < 2) {
                        activeChar.sendMessage("USAGE: create_item_target id [count]");
                        return false;
                    }

                    item_id = toInt(wordList[1]);
                    item_count = wordList.length < 3 ? 1 : Long.parseLong(wordList[2]);
                    createItem((Player) activeChar.getTarget(), item_id, item_count);
                    activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/itemcreation.htm"));
                    break;
                } else {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                    return false;
                }
            case admin_create_item_range:
                if (wordList.length < 3) {
                    activeChar.sendMessage("USAGE: create_item_range id count range");
                    return false;
                }

                item_id = toInt(wordList[1]);
                int itemCount = toInt(wordList[2]);
                int distance = toInt(wordList[3]);
                int rewardedCount = (int) World.getAroundPlayers(activeChar, distance, 1000)
                        .filter(player -> !player.isInStoreMode())
                        .peek(player -> createItem(player, item_id, itemCount))
                        .count();
                activeChar.sendMessage("You have rewarded " + rewardedCount + " players!");
                break;
            case admin_add_pp:
                target = activeChar.getTarget();
                if (!(target instanceof Player)) {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                    return false;
                }
                Player player = (Player)target;
                if (wordList.length < 2) {
                    activeChar.sendMessage("USAGE: add_pp [count]");
                    return false;
                }

                item_count = toInt(wordList[1]);
                player.addPremiumPoints((int)item_count);
                activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/itemcreation.htm"));
                break;
            case admin_add_pcp:
                target = activeChar.getTarget();
                if (!(target instanceof Player)) {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                    return false;
                }
                if (wordList.length < 2) {
                    activeChar.sendMessage("USAGE: add_pcp [count]");
                    return false;
                }

                item_count = toInt(wordList[1]);
                ((Player)target).addPcBangPoints((int) item_count, false);
                activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/itemcreation.htm"));
                break;
            case admin_spreaditem:
                int id = toInt(wordList[1]);
                int num = wordList.length > 2 ? toInt(wordList[2]) : 1;
                long count = wordList.length > 3 ? toLong(wordList[3]) : 1;
                for (int i = 0; i < num; i++) {
                    ItemInstance createditem = ItemFunctions.createItem(id);
                    createditem.setCount(count);
                    createditem.dropMe(activeChar, Location.findPointToStay(activeChar, 100));
                }
                break;
            case admin_create_item_element:
                if (wordList.length < 4) {
                    activeChar.sendMessage("USAGE: create_item_attribue [id] [element id] [value]");
                    return false;
                }

                item_id = toInt(wordList[1]);
                int elementId = toInt(wordList[2]);
                int value = toInt(wordList[3]);
                if (elementId > 5 || elementId < 0) {
                    activeChar.sendMessage("Improper element Id");
                    return false;
                }
                if (value < 1 || value > 300) {
                    activeChar.sendMessage("Improper element value");
                    return false;
                }
                ItemInstance item = createItem(activeChar, item_id, 1);
                Element element = Element.getElementById(elementId);
                item.setAttributeElement(element, item.getAttributeElementValue(element, false) + value);
                item.setJdbcState(JdbcEntityState.UPDATED);
                item.update();
                activeChar.sendPacket(new InventoryUpdate().addModifiedItem(item));
                activeChar.sendPacket(new NpcHtmlMessage(5).setFile("data/html/admin/itemcreation.htm"));
                break;
        }

        return true;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private ItemInstance createItem(Player activeChar, int itemId, long count) {
        ItemInstance createditem = ItemFunctions.createItem(itemId);
        createditem.setCount(count);
        activeChar.getInventory().addItem(createditem, "AdminCreateItem");
        if (!createditem.isStackable())
            for (long i = 0; i < count - 1; i++) {
                createditem = ItemFunctions.createItem(itemId);
                activeChar.getInventory().addItem(createditem, "AdminCreateItem");
            }
        activeChar.sendPacket(SystemMessage2.obtainItems(itemId, count, 0));
        return createditem;
    }

    private enum Commands {
        admin_itemcreate,
        admin_create_item,
        admin_create_item_all,
        admin_create_item_hwid,
        admin_create_item_char,
        admin_create_item_target,
        admin_create_item_range,
        admin_ci,
        admin_spreaditem,
        admin_add_pp,
        admin_add_pcp,
        admin_create_item_element
    }
}