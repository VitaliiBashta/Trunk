package l2trunk.scripts.services.community;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.FoundationHolder;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.handler.bbs.CommunityBoardManager;
import l2trunk.gameserver.handler.bbs.ICommunityBoardHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Element;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.PcInventory;
import l2trunk.gameserver.network.serverpackets.ExShowVariationCancelWindow;
import l2trunk.gameserver.network.serverpackets.ExShowVariationMakeWindow;
import l2trunk.gameserver.network.serverpackets.InventoryUpdate;
import l2trunk.gameserver.network.serverpackets.ShowBoard;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.templates.item.WeaponTemplate;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class Forge implements ScriptFile, ICommunityBoardHandler {
    private static final Logger _log = LoggerFactory.getLogger(Forge.class);

    @Override
    public void onLoad() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            _log.info("CommunityBoard: Forge loaded.");
            CommunityBoardManager.registerHandler(this);
        }
    }

    @Override
    public void onReload() {
        if (Config.COMMUNITYBOARD_ENABLED)
            CommunityBoardManager.removeHandler(this);
    }

    @Override
    public List<String> getBypassCommands() {
        return List.of("_bbsforge");
    }

    @Override
    public void onBypassCommand(Player player, String command) {
        if (!Config.BBS_FORGE_ENABLED) {
            player.sendMessage("This service is turned off.");
            Util.communityNextPage(player, "_bbshome");
            return;
        }

        String content = "";
        if (command.equals("_bbsforge")) {
            content = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "forge/index.htm", player);
        } else {
            if (command.equals("_bbsforge:augment")) {
                onBypassCommand(player, "_bbsforge");
                //getPlayer.addSessionVar("augmentation", Boolean.valueOf(true));
                player.sendPacket(SystemMsg.SELECT_THE_ITEM_TO_BE_AUGMENTED);
                player.sendPacket(ExShowVariationMakeWindow.STATIC);
                return;
            }
            if (command.equals("_bbsforge:remove:augment")) {
                onBypassCommand(player, "_bbsforge");
                //getPlayer.addSessionVar("augmentation", Boolean.valueOf(true));
                player.sendPacket(SystemMsg.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION);
                player.sendPacket(ExShowVariationCancelWindow.STATIC);
                return;
            }
            if (command.equals("_bbsforge:enchant:list")) {
                content = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "forge/itemlist.htm", player);

                ItemInstance head = player.getInventory().getPaperdollItem(6);
                ItemInstance chest = player.getInventory().getPaperdollItem(10);
                ItemInstance legs = player.getInventory().getPaperdollItem(11);
                ItemInstance gloves = player.getInventory().getPaperdollItem(9);
                ItemInstance feet = player.getInventory().getPaperdollItem(12);

                ItemInstance lhand = player.getInventory().getPaperdollItem(8);
                ItemInstance rhand = player.getInventory().getPaperdollItem(7);

                ItemInstance lfinger = player.getInventory().getPaperdollItem(5);
                ItemInstance rfinger = player.getInventory().getPaperdollItem(4);
                ItemInstance neck = player.getInventory().getPaperdollItem(3);
                ItemInstance lear = player.getInventory().getPaperdollItem(2);
                ItemInstance rear = player.getInventory().getPaperdollItem(1);

                Map<Integer, String[]> data = new HashMap<>();

                data.put(6, ForgeElement.generateEnchant(head, Config.BBS_FORGE_ENCHANT_MAX.get(1), 6, player));
                data.put(10, ForgeElement.generateEnchant(chest, Config.BBS_FORGE_ENCHANT_MAX.get(1), 10, player));
                data.put(11, ForgeElement.generateEnchant(legs, Config.BBS_FORGE_ENCHANT_MAX.get(1), 11, player));
                data.put(9, ForgeElement.generateEnchant(gloves, Config.BBS_FORGE_ENCHANT_MAX.get(1), 9, player));
                data.put(12, ForgeElement.generateEnchant(feet, Config.BBS_FORGE_ENCHANT_MAX.get(1), 12, player));

                data.put(5, ForgeElement.generateEnchant(lfinger, Config.BBS_FORGE_ENCHANT_MAX.get(2), 5, player));
                data.put(4, ForgeElement.generateEnchant(rfinger, Config.BBS_FORGE_ENCHANT_MAX.get(2), 4, player));
                data.put(3, ForgeElement.generateEnchant(neck, Config.BBS_FORGE_ENCHANT_MAX.get(2), 3, player));
                data.put(2, ForgeElement.generateEnchant(lear, Config.BBS_FORGE_ENCHANT_MAX.get(2), 2, player));
                data.put(1, ForgeElement.generateEnchant(rear, Config.BBS_FORGE_ENCHANT_MAX.get(2), 1, player));

                data.put(7, ForgeElement.generateEnchant(rhand, Config.BBS_FORGE_ENCHANT_MAX.get(0), 7, player));
                if (rhand != null &&
                        (rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.BIGBLUNT ||
                                rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.BOW ||
                                rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.DUALDAGGER ||
                                rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.ANCIENTSWORD ||
                                rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.CROSSBOW ||
                                rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.BIGBLUNT ||
                                rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.BIGSWORD ||
                                rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.DUALFIST ||
                                rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.DUAL ||
                                rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.POLE ||
                                rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.FIST)) {
                    data.put(8, new String[]
                            {
                                    rhand.getTemplate().getIcon(),
                                    rhand.getName() + " " + (rhand.getEnchantLevel() > 0 ? "+" + rhand.getEnchantLevel() : ""),
                                    "<font color=\"FF0000\">...</font>",
                                    "L2UI_CT1.ItemWindow_DF_SlotBox_Disable"
                            });
                } else {
                    data.put(8, ForgeElement.generateEnchant(lhand, Config.BBS_FORGE_ENCHANT_MAX.get(0), 8, player));
                }
                content = content.replace("<?content?>", ForgeElement.page(player));

                for (Entry<Integer, String[]> info : data.entrySet()) {
                    int slot = info.getKey();
                    String[] array = info.getValue();
                    content = content.replace("<?" + slot + "_icon?>", array[0]);
                    content = content.replace("<?" + slot + "name?>", array[1]);
                    content = content.replace("<?" + slot + "_button?>", array[2]);
                    content = content.replace("<?" + slot + "_pic?>", array[3]);
                }
            } else if (command.startsWith("_bbsforge:enchant:item:")) {
                String[] array = command.split(":");
                int item = toInt(array[3]);

                String name = ItemHolder.getTemplate(Config.BBS_FORGE_ENCHANT_ITEM).getName();

                if (name.isEmpty()) {
                    name = new CustomMessage("common.item.no.name").toString();
                }
                if ((item < 1) || (item > 12)) {
                    return;
                }
                ItemInstance _item = player.getInventory().getPaperdollItem(item);
                if (_item == null) {
                    player.sendMessage(new CustomMessage("communityboard.forge.item.null"));
                    Util.communityNextPage(player, "_bbsforge:enchant:list");
                    return;
                }

                if (_item.isHeroWeapon()) {
                    player.sendMessage("You can not enchant the weapons of heroes.");
                    Util.communityNextPage(player, "_bbsforge:enchant:list");
                    return;
                }

                if (_item.getTemplate().isArrow()) {
                    player.sendMessage(new CustomMessage("communityboard.forge.item.arrow"));
                    Util.communityNextPage(player, "_bbsforge:enchant:list");
                    return;
                }

                content = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "forge/enchant.htm", player);

                String template = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "forge/enchant_template.htm", player);

                template = template.replace("{icon}", _item.getTemplate().getIcon());
                String _name = _item.getName();
                _name = _name.replace(" {PvP}", "");

                if (_name.length() > 30) {
                    _name = _name.substring(0, 29) + "...";
                }
                template = template.replace("{name}", _name);
                template = template.replace("{enchant}", _item.getEnchantLevel() <= 0 ? "" : "+" + _item.getEnchantLevel());
                template = template.replace("{msg}", new CustomMessage("communityboard.forge.enchant.getBonuses").toString());

                String button_tm = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "forge/enchant_button_template.htm", player);
                String button = null;
                String block;

                List<Integer> level = _item.getTemplate().isArmor() ? Config.BBS_FORGE_ARMOR_ENCHANT_LVL : _item.getTemplate().isWeapon() ? Config.BBS_FORGE_WEAPON_ENCHANT_LVL : Config.BBS_FORGE_JEWELS_ENCHANT_LVL;
                for (int i = 0; i < level.size(); i++) {
                    if (_item.getEnchantLevel() >= level.get(i))
                        continue;
                    block = button_tm;
                    block = block.replace("{link}", "bypass _bbsforge:enchant:" + i * item + ":" + item);
                    block = block.replace("{value}", "+" + level.get(i) + " (" + (_item.getTemplate().isArmor() ? Config.BBS_FORGE_ENCHANT_PRICE_ARMOR.get(i) : _item.getTemplate().isWeapon() ? Config.BBS_FORGE_ENCHANT_PRICE_WEAPON.get(i) : Config.BBS_FORGE_ENCHANT_PRICE_JEWELS.get(i)) + " " + name + ")");
                    button = button + block;
                }

                template = template.replace("{button}", ((button == null) ? "" : button));

                content = content.replace("<?content?>", template);
            } else if (command.equals("_bbsforge:foundation:list")) {
                content = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "forge/foundationlist.htm", player);

                ItemInstance head = player.getInventory().getPaperdollItem(6);
                ItemInstance chest = player.getInventory().getPaperdollItem(10);
                ItemInstance legs = player.getInventory().getPaperdollItem(11);
                ItemInstance gloves = player.getInventory().getPaperdollItem(9);
                ItemInstance feet = player.getInventory().getPaperdollItem(12);

                ItemInstance lhand = player.getInventory().getPaperdollItem(8);
                ItemInstance rhand = player.getInventory().getPaperdollItem(7);

                ItemInstance lfinger = player.getInventory().getPaperdollItem(5);
                ItemInstance rfinger = player.getInventory().getPaperdollItem(4);
                ItemInstance neck = player.getInventory().getPaperdollItem(3);
                ItemInstance lear = player.getInventory().getPaperdollItem(2);
                ItemInstance rear = player.getInventory().getPaperdollItem(1);

                Map<Integer, String[]> data = new HashMap<>();

                data.put(6, ForgeElement.generateFoundation(head, 6, player));
                data.put(10, ForgeElement.generateFoundation(chest, 10, player));
                data.put(11, ForgeElement.generateFoundation(legs, 11, player));
                data.put(9, ForgeElement.generateFoundation(gloves, 9, player));
                data.put(12, ForgeElement.generateFoundation(feet, 12, player));

                data.put(5, ForgeElement.generateFoundation(lfinger, 5, player));
                data.put(4, ForgeElement.generateFoundation(rfinger, 4, player));
                data.put(3, ForgeElement.generateFoundation(neck, 3, player));
                data.put(2, ForgeElement.generateFoundation(lear, 2, player));
                data.put(1, ForgeElement.generateFoundation(rear, 1, player));

                data.put(7, ForgeElement.generateFoundation(rhand, 7, player));
                if (rhand != null &&
                        (rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.BIGBLUNT ||
                                rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.BOW ||
                                rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.DUALDAGGER ||
                                rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.ANCIENTSWORD ||
                                rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.CROSSBOW ||
                                rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.BIGBLUNT ||
                                rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.BIGSWORD ||
                                rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.DUALFIST ||
                                rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.DUAL ||
                                rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.POLE ||
                                rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.FIST)) {
                    data.put(8, new String[]
                            {
                                    rhand.getTemplate().getIcon(),
                                    rhand.getName() + " " + (rhand.getEnchantLevel() > 0 ? "+" + rhand.getEnchantLevel() : ""),
                                    "<font color=\"FF0000\">...</font>",
                                    "L2UI_CT1.ItemWindow_DF_SlotBox_Disable"
                            });
                } else {
                    data.put(8, ForgeElement.generateFoundation(lhand, 8, player));
                }
                content = content.replace("<?content?>", ForgeElement.page(player));

                for (Entry<Integer, String[]> info : data.entrySet()) {
                    int slot = info.getKey();
                    String[] array = info.getValue();
                    content = content.replace("<?" + slot + "_icon?>", array[0]);
                    content = content.replace("<?" + slot + "name?>", array[1]);
                    content = content.replace("<?" + slot + "_button?>", array[2]);
                    content = content.replace("<?" + slot + "_pic?>", array[3]);
                }
            } else {
                if (command.startsWith("_bbsforge:foundation:item:")) {
                    String[] array = command.split(":");
                    int item = toInt(array[3]);

                    if ((item < 1) || (item > 12)) {
                        return;
                    }
                    ItemInstance _item = player.getInventory().getPaperdollItem(item);
                    if (_item == null) {
                        player.sendMessage("You removed the item.");
                        onBypassCommand(player, "_bbsforge:foundation:list");
                        return;
                    }

                    if (_item.isHeroWeapon()) {
                        player.sendMessage("You can not enchant the weapons of heroes.");
                        Util.communityNextPage(player, "_bbsforge:foundation:list");
                        return;
                    }

                    int found = FoundationHolder.getFoundation(_item.getItemId());
                    if (found == -1) {
                        player.sendMessage("You removed the item.");
                        onBypassCommand(player, "_bbsforge:foundation:list");
                        return;
                    }

                    final int price;
                    if (_item.isAccessory())
                        price = Config.BBS_FORGE_FOUNDATION_PRICE_JEWEL.get(_item.getCrystalType().ordinal());
                    else if (_item.isWeapon())
                        price = Config.BBS_FORGE_FOUNDATION_PRICE_WEAPON.get(_item.getCrystalType().ordinal());
                    else
                        price = Config.BBS_FORGE_FOUNDATION_PRICE_ARMOR.get(_item.getCrystalType().ordinal());

                    if (Util.getPay(player, Config.BBS_FORGE_FOUNDATION_ITEM, price, true)) {
                        PcInventory inv = player.getInventory();
                        ItemInstance _found = ItemFunctions.createItem(found);
                        if (inv.destroyItemByObjectId(_item.objectId(), _item.getCount(), "Forge")) {
                            _found.setEnchantLevel(_item.getEnchantLevel());
                            _found.setAugmentationId(_item.getAugmentationId());

                            for (Element element : Element.VALUES) {
                                int val = _item.getAttributes().getValue(element);
                                if (val > 0) {
                                    _found.setAttributeElement(element, val);
                                }
                            }
                            inv.addItem(_found, "Forge");
                            _found.setJdbcState(JdbcEntityState.UPDATED);
                            _found.update();
                            if (ItemFunctions.checkIfCanEquip(player, _found) == null)
                                inv.equipItem(_found);
                            player.sendMessage("You exchange item " + _item.getName() + " to Foundation " + _found.getName());
                        } else {
                            _found.deleteMe();
                            player.sendMessage("Foundation failed");
                        }
                    }
                    onBypassCommand(player, "_bbsforge:foundation:list");
                    return;
                }
                if (command.startsWith("_bbsforge:enchant:")) {
                    String[] array = command.split(":");

                    int val = toInt(array[2]);
                    int item = toInt(array[3]);

                    int conversion = val / item;

                    ItemInstance _item = player.getInventory().getPaperdollItem(item);
                    if (_item == null) {
                        player.sendMessage("You removed the item.");
                        Util.communityNextPage(player, "_bbsforge:enchant:list");
                        return;
                    }

                    if (_item.isHeroWeapon()) {
                        player.sendMessage("You can not enchant the weapons of heroes.");
                        Util.communityNextPage(player, "_bbsforge:enchant:list");
                        return;
                    }

                    List<Integer> level = _item.getTemplate().isArmor() ? Config.BBS_FORGE_ARMOR_ENCHANT_LVL
                            : _item.getTemplate().isWeapon() ? Config.BBS_FORGE_WEAPON_ENCHANT_LVL
                            : Config.BBS_FORGE_JEWELS_ENCHANT_LVL;
                    int Value = level.get(conversion);

                    int max = _item.getTemplate().isArmor() ? Config.BBS_FORGE_ENCHANT_MAX.get(1) : _item.getTemplate().isWeapon() ? Config.BBS_FORGE_ENCHANT_MAX.get(0)
                            : Config.BBS_FORGE_ENCHANT_MAX.get(2);
                    if (Value > max) {
                        return;
                    }
                    if (_item.getTemplate().isArrow()) {
                        player.sendMessage("You can not enchant the arrows.");
                        Util.communityNextPage(player, "_bbsforge:enchant:list");
                        return;
                    }

                    int price = _item.getTemplate().isArmor() ? Config.BBS_FORGE_ENCHANT_PRICE_ARMOR.get(conversion)
                            : _item.isWeapon() ? Config.BBS_FORGE_ENCHANT_PRICE_WEAPON.get(conversion)
                            : Config.BBS_FORGE_ENCHANT_PRICE_JEWELS.get(conversion);

                    if (Util.getPay(player, Config.BBS_FORGE_ENCHANT_ITEM, price, true)) {
                        player.getInventory().unEquipItem(_item);
                        _item.setEnchantLevel(Value);
                        player.getInventory().equipItem(_item);

                        player.sendPacket(new InventoryUpdate().addModifiedItem(_item));
                        player.broadcastUserInfo(true);

                        player.sendMessage(new CustomMessage("{0} was enchanted to +{1}. Thank you!").addString(_item.getName()).addNumber(Value));
                    }

                    Util.communityNextPage(player, "_bbsforge:enchant:list");
                    return;
                }
                if (command.equals("_bbsforge:attribute:list")) {
                    content = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "forge/attributelist.htm", player);

                    ItemInstance head = player.getInventory().getPaperdollItem(6);
                    ItemInstance chest = player.getInventory().getPaperdollItem(10);
                    ItemInstance legs = player.getInventory().getPaperdollItem(11);
                    ItemInstance gloves = player.getInventory().getPaperdollItem(9);
                    ItemInstance feet = player.getInventory().getPaperdollItem(12);

                    ItemInstance lhand = player.getInventory().getPaperdollItem(8);
                    ItemInstance rhand = player.getInventory().getPaperdollItem(7);

                    ItemInstance lfinger = player.getInventory().getPaperdollItem(5);
                    ItemInstance rfinger = player.getInventory().getPaperdollItem(4);
                    ItemInstance neck = player.getInventory().getPaperdollItem(3);
                    ItemInstance lear = player.getInventory().getPaperdollItem(2);
                    ItemInstance rear = player.getInventory().getPaperdollItem(1);

                    Map<Integer, String[]> data = new HashMap<>();

                    data.put(6, ForgeElement.generateAttribution(head, 6, player, true));
                    data.put(10, ForgeElement.generateAttribution(chest, 10, player, true));
                    data.put(11, ForgeElement.generateAttribution(legs, 11, player, true));
                    data.put(9, ForgeElement.generateAttribution(gloves, 9, player, true));
                    data.put(12, ForgeElement.generateAttribution(feet, 12, player, true));

                    data.put(5, ForgeElement.generateAttribution(lfinger, 5, player, true));
                    data.put(4, ForgeElement.generateAttribution(rfinger, 4, player, true));
                    data.put(3, ForgeElement.generateAttribution(neck, 3, player, true));
                    data.put(2, ForgeElement.generateAttribution(lear, 2, player, true));
                    data.put(1, ForgeElement.generateAttribution(rear, 1, player, true));

                    data.put(7, ForgeElement.generateAttribution(rhand, 7, player, true));
                    if (rhand != null &&
                            (rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.BIGBLUNT ||
                                    rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.BOW ||
                                    rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.DUALDAGGER ||
                                    rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.ANCIENTSWORD ||
                                    rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.CROSSBOW ||
                                    rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.BIGBLUNT ||
                                    rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.BIGSWORD ||
                                    rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.DUALFIST ||
                                    rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.DUAL ||
                                    rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.POLE ||
                                    rhand.getTemplate().getItemType() == WeaponTemplate.WeaponType.FIST)) {
                        data.put(8, new String[]
                                {
                                        rhand.getTemplate().getIcon(),
                                        rhand.getName() + " " + (rhand.getEnchantLevel() > 0 ? "+" + rhand.getEnchantLevel() : ""),
                                        "<font color=\"FF0000\">...</font>",
                                        "L2UI_CT1.ItemWindow_DF_SlotBox_Disable"
                                });
                    } else {
                        data.put(8, ForgeElement.generateAttribution(lhand, 8, player, true));
                    }
                    content = content.replace("<?content?>", ForgeElement.page(player));

                    for (Entry<Integer, String[]> info : data.entrySet()) {
                        int slot = info.getKey();
                        String[] array = info.getValue();
                        content = content.replace("<?" + slot + "_icon?>", array[0]);
                        content = content.replace("<?" + slot + "name?>", array[1]);
                        content = content.replace("<?" + slot + "_button?>", array[2]);
                        content = content.replace("<?" + slot + "_pic?>", array[3]);
                    }
                } else if (command.startsWith("_bbsforge:attribute:item:")) {
                    String[] array = command.split(":");
                    int item = toInt(array[3]);

                    if ((item < 1) || (item > 12)) {
                        return;
                    }
                    ItemInstance _item = player.getInventory().getPaperdollItem(item);
                    if (_item == null) {
                        player.sendMessage("You removed the item.");
                        Util.communityNextPage(player, "_bbsforge:attribute:list");
                        return;
                    }

                    if (!ForgeElement.itemCheckGrade(true, _item)) {
                        player.sendMessage("You can not enchant this grade.");
                        Util.communityNextPage(player, "_bbsforge:attribute:list");
                        return;
                    }

                    if (_item.isHeroWeapon()) {
                        player.sendMessage("You can not enchant the weapons of heroes.");
                        Util.communityNextPage(player, "_bbsforge:attribute:list");
                        return;
                    }

                    if (_item.isAccessory()) {
                        player.sendMessage("You cannot enchant jewels.");
                        Util.communityNextPage(player, "_bbsforge:attribute:list");
                        return;
                    }

                    if (_item.getTemplate().isShield()) {
                        player.sendMessage("You cannot enchant Shield.");
                        Util.communityNextPage(player, "_bbsforge:attribute:list");
                        return;
                    }

                    content = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "forge/attribute.htm", player);

                    String slotclose = "<img src=\"L2UI_CT1.ItemWindow_DF_SlotBox_Disable\" width=\"32\" height=\"32\">";
                    String buttonFire = "<button action=\"bypass _bbsforge:attribute:element:0:" + item + "\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>";
                    String buttonWater = "<button action=\"bypass _bbsforge:attribute:element:1:" + item + "\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>";
                    String buttonWind = "<button action=\"bypass _bbsforge:attribute:element:2:" + item + "\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>";
                    String buttonEarth = "<button action=\"bypass _bbsforge:attribute:element:3:" + item + "\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>";
                    String buttonHoly = "<button action=\"bypass _bbsforge:attribute:element:4:" + item + "\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>";
                    String buttonUnholy = "<button action=\"bypass _bbsforge:attribute:element:5:" + item + "\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>";

                    if (_item.isWeapon()) {
                        if (_item.getAttributes().getFire() > 0) {
                            buttonWater = slotclose;
                            buttonWind = slotclose;
                            buttonEarth = slotclose;
                            buttonHoly = slotclose;
                            buttonUnholy = slotclose;
                        }
                        if (_item.getAttributes().getWater() > 0) {
                            buttonFire = slotclose;
                            buttonWind = slotclose;
                            buttonEarth = slotclose;
                            buttonHoly = slotclose;
                            buttonUnholy = slotclose;
                        }
                        if (_item.getAttributes().getWind() > 0) {
                            buttonWater = slotclose;
                            buttonFire = slotclose;
                            buttonEarth = slotclose;
                            buttonHoly = slotclose;
                            buttonUnholy = slotclose;
                        }
                        if (_item.getAttributes().getEarth() > 0) {
                            buttonWater = slotclose;
                            buttonWind = slotclose;
                            buttonFire = slotclose;
                            buttonHoly = slotclose;
                            buttonUnholy = slotclose;
                        }
                        if (_item.getAttributes().getHoly() > 0) {
                            buttonWater = slotclose;
                            buttonWind = slotclose;
                            buttonEarth = slotclose;
                            buttonFire = slotclose;
                            buttonUnholy = slotclose;
                        }
                        if (_item.getAttributes().getUnholy() > 0) {
                            buttonWater = slotclose;
                            buttonWind = slotclose;
                            buttonEarth = slotclose;
                            buttonHoly = slotclose;
                            buttonFire = slotclose;
                        }
                    }

                    if (_item.isArmor()) {
                        if (_item.getAttributes().getFire() > 0) {
                            if (_item.getAttributes().getFire() >= Config.BBS_FORGE_ARMOR_ATTRIBUTE_MAX) {
                                buttonFire = slotclose;
                            }
                            buttonWater = slotclose;
                        }

                        if (_item.getAttributes().getWater() > 0) {
                            if (_item.getAttributes().getWater() >= Config.BBS_FORGE_ARMOR_ATTRIBUTE_MAX) {
                                buttonWater = slotclose;
                            }
                            buttonFire = slotclose;
                        }
                        if (_item.getAttributes().getWind() > 0) {
                            if (_item.getAttributes().getWind() >= Config.BBS_FORGE_ARMOR_ATTRIBUTE_MAX) {
                                buttonWind = slotclose;
                            }
                            buttonEarth = slotclose;
                        }
                        if (_item.getAttributes().getEarth() > 0) {
                            if (_item.getAttributes().getEarth() >= Config.BBS_FORGE_ARMOR_ATTRIBUTE_MAX) {
                                buttonEarth = slotclose;
                            }
                            buttonWind = slotclose;
                        }
                        if (_item.getAttributes().getHoly() > 0) {
                            if (_item.getAttributes().getHoly() >= Config.BBS_FORGE_ARMOR_ATTRIBUTE_MAX) {
                                buttonHoly = slotclose;
                            }
                            buttonUnholy = slotclose;
                        }
                        if (_item.getAttributes().getUnholy() > 0) {
                            if (_item.getAttributes().getUnholy() >= Config.BBS_FORGE_ARMOR_ATTRIBUTE_MAX) {
                                buttonUnholy = slotclose;
                            }
                            buttonHoly = slotclose;
                        }
                    }

                    String html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "forge/attribute_choice_template.htm", player);

                    html = html.replace("{icon}", _item.getTemplate().getIcon());
                    String _name = _item.getName();
                    _name = _name.replace(" {PvP}", "");

                    if (_name.length() > 30) {
                        _name = _name.substring(0, 29) + "...";
                    }
                    html = html.replace("{name}", _name);
                    html = html.replace("{enchant}", _item.getEnchantLevel() <= 0 ? "" : " +" + _item.getEnchantLevel());
                    html = html.replace("{msg}", new CustomMessage("communityboard.forge.attribute.getBonuses").toString());
                    html = html.replace("{fire}", buttonFire);
                    html = html.replace("{water}", buttonWater);
                    html = html.replace("{earth}", buttonEarth);
                    html = html.replace("{wind}", buttonWind);
                    html = html.replace("{holy}", buttonHoly);
                    html = html.replace("{unholy}", buttonUnholy);

                    content = content.replace("<?content?>", html);
                } else if (command.startsWith("_bbsforge:attribute:element:")) {
                    String[] array = command.split(":");
                    int element = toInt(array[3]);

                    String elementName = "";
                    if (element == 0)
                        elementName = new CustomMessage("common.element.0").toString();
                    else if (element == 1)
                        elementName = new CustomMessage("common.element.1").toString();
                    else if (element == 2)
                        elementName = new CustomMessage("common.element.2").toString();
                    else if (element == 3)
                        elementName = new CustomMessage("common.element.3").toString();
                    else if (element == 4)
                        elementName = new CustomMessage("common.element.4").toString();
                    else if (element == 5) {
                        elementName = new CustomMessage("common.element.5").toString();
                    }
                    int item = toInt(array[4]);

                    String name = ItemHolder.getTemplate(Config.BBS_FORGE_ENCHANT_ITEM).getName();

                    if (name.isEmpty()) {
                        name = new CustomMessage("common.item.no.name").toString();
                    }
                    ItemInstance _item = player.getInventory().getPaperdollItem(item);

                    if (_item == null) {
                        player.sendMessage("You removed the item.");
                        Util.communityNextPage(player, "_bbsforge:attribute:list");
                        return;
                    }

                    if (!ForgeElement.itemCheckGrade(true, _item)) {
                        player.sendMessage("You can not enchant this grade.");
                        Util.communityNextPage(player, "_bbsforge:attribute:list");
                        return;
                    }

                    if (_item.isHeroWeapon()) {
                        player.sendMessage("You can not enchant the weapons of heroes.");
                        Util.communityNextPage(player, "_bbsforge:attribute:list");
                        return;
                    }

                    content = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "forge/attribute.htm", player);
                    String template = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "forge/enchant_template.htm", player);

                    template = template.replace("{icon}", _item.getTemplate().getIcon());
                    String _name = _item.getName();
                    _name = _name.replace(" {PvP}", "");

                    if (_name.length() > 30) {
                        _name = _name.substring(0, 29) + "...";
                    }
                    template = template.replace("{name}", _name);
                    template = template.replace("{enchant}", _item.getEnchantLevel() <= 0 ? "" : "+" + _item.getEnchantLevel());
                    template = template.replace("{msg}", new CustomMessage("communityboard.forge.attribute.selected").addString(elementName).toString());

                    String button_tm = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "forge/enchant_button_template.htm", player);
                    StringBuilder button = new StringBuilder();
                    String block;

                    List<Integer> level = _item.getTemplate().isWeapon() ? Config.BBS_FORGE_ATRIBUTE_LVL_WEAPON
                            : Config.BBS_FORGE_ATRIBUTE_LVL_ARMOR;
                    for (int i = 0; i < level.size(); i++) {
                        if (_item.getAttributeElementValue(Element.getElementById(element), false) >= (_item.isWeapon() ? Config.BBS_FORGE_ATRIBUTE_LVL_WEAPON.get(i)
                                : Config.BBS_FORGE_ATRIBUTE_LVL_ARMOR.get(i)))
                            continue;
                        block = button_tm;
                        block = block.replace("{link}", String.valueOf("bypass _bbsforge:attribute:" + i * item + ":" + item + ":" + element));
                        block = block.replace("{value}", "+"
                                + (_item.isWeapon() ? Config.BBS_FORGE_ATRIBUTE_LVL_WEAPON.get(i)
                                : Config.BBS_FORGE_ATRIBUTE_LVL_ARMOR.get(i)) + " (" + (_item.isWeapon() ? Config.BBS_FORGE_ATRIBUTE_PRICE_WEAPON.get(i) : Config.BBS_FORGE_ATRIBUTE_PRICE_ARMOR.get(i)) + " " + name + ")");
                        button.append(block);
                    }

                    template = template.replace("{button}", button.toString());

                    content = content.replace("<?content?>", template);
                } else if (command.startsWith("_bbsforge:attribute:")) {
                    String[] array = command.split(":");
                    int val = toInt(array[2]);
                    int item = toInt(array[3]);
                    int att = toInt(array[4]);

                    ItemInstance _item = player.getInventory().getPaperdollItem(item);

                    if (_item == null) {
                        player.sendMessage(new CustomMessage("communityboard.forge.item.null").toString());
                        Util.communityNextPage(player, "_bbsforge:attribute:list");
                        return;
                    }

                    if (!ForgeElement.itemCheckGrade(true, _item)) {
                        player.sendMessage(new CustomMessage("communityboard.forge.grade.incorrect").toString());
                        Util.communityNextPage(player, "_bbsforge:attribute:list");
                        return;
                    }

                    if (_item.isHeroWeapon()) {
                        player.sendMessage(new CustomMessage("communityboard.forge.item.hero").toString());
                        Util.communityNextPage(player, "_bbsforge:attribute:list");
                        return;
                    }

                    if ((_item.isArmor()) && (!ForgeElement.canEnchantArmorAttribute(att, _item))) {
                        player.sendMessage(new CustomMessage("communityboard.forge.attribute.terms.incorrect").toString());
                        Util.communityNextPage(player, "_bbsforge:attribute:list");
                        return;
                    }

                    int conversion = val / item;

                    int Value = _item.isWeapon() ? Config.BBS_FORGE_ATRIBUTE_LVL_WEAPON.get(conversion)
                            : Config.BBS_FORGE_ATRIBUTE_LVL_ARMOR.get(conversion);

                    if (Value > (_item.isWeapon() ? Config.BBS_FORGE_WEAPON_ATTRIBUTE_MAX : Config.BBS_FORGE_ARMOR_ATTRIBUTE_MAX)) {
                        return;
                    }
                    int price = _item.isWeapon() ? Config.BBS_FORGE_ATRIBUTE_PRICE_WEAPON.get(conversion)
                            : Config.BBS_FORGE_ATRIBUTE_PRICE_ARMOR.get(conversion);

                    if (Util.getPay(player, Config.BBS_FORGE_ENCHANT_ITEM, price, true)) {
                        player.getInventory().unEquipItem(_item);

                        _item.setAttributeElement(Element.getElementById(att), Value);

                        player.getInventory().equipItem(_item);

                        player.sendPacket(new InventoryUpdate().addModifiedItem(_item));
                        player.broadcastUserInfo(true);

                        String elementName = "";
                        if (att == 0)
                            elementName = new CustomMessage("common.element.0").toString();
                        else if (att == 1)
                            elementName = new CustomMessage("common.element.1").toString();
                        else if (att == 2)
                            elementName = new CustomMessage("common.element.2").toString();
                        else if (att == 3)
                            elementName = new CustomMessage("common.element.3").toString();
                        else if (att == 4)
                            elementName = new CustomMessage("common.element.4").toString();
                        else if (att == 5) {
                            elementName = new CustomMessage("common.element.5").toString();
                        }
                        player.sendMessage(new CustomMessage("communityboard.forge.enchant.attribute.success").addString(_item.getName()).addString(elementName).addNumber(Value).toString());
                    }

                    Util.communityNextPage(player, "_bbsforge:attribute:list");
                    return;
                }
            }
        }
        ShowBoard.separateAndSend(content, player);
    }

}
