package l2trunk.scripts.services;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.OptionDataHolder;
import l2trunk.gameserver.model.Options;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.actor.instances.player.ShortCut;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.PcInventory;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.AugmentationData;
import l2trunk.gameserver.templates.OptionDataTemplate;
import l2trunk.gameserver.utils.Util;

import java.util.Collection;

public final class Augmentation extends Functions {
    private static final int MAX_AUGMENTATIONS_PER_PAGE = 7;
    private static final int MAX_PAGES_PER_PAGE = 6;

    public void run(String[] arg) {
        int _page = 0;
        Options.AugmentationFilter _filter = Options.AugmentationFilter.NONE;
        Player player = getSelf();
        if (arg.length < 1) {
            showMainMenu(player, 0, _filter);
            return;
        }
        String command = arg[0];
        if (command.equals("menu")) {
            showMainMenu(player, 0, _filter);
            return;
        }
        switch (command) {
            case "section":
                try {
                    switch (Integer.parseInt(arg[1])) {
                        case 1:
                            _filter = Options.AugmentationFilter.NONE;
                            break;
                        case 2:
                            _filter = Options.AugmentationFilter.ACTIVE_SKILL;
                            break;
                        case 3:
                            _filter = Options.AugmentationFilter.PASSIVE_SKILL;
                            break;
                        case 4:
                            _filter = Options.AugmentationFilter.CHANCE_SKILL;
                            break;
                        case 5:
                            _filter = Options.AugmentationFilter.STATS;
                    }

                    _page = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    player.sendMessage("Error.");
                }
                break;
            case "page":
                try {
                    switch (Integer.parseInt(arg[2])) {
                        case 1:
                            _filter = Options.AugmentationFilter.NONE;
                            break;
                        case 2:
                            _filter = Options.AugmentationFilter.ACTIVE_SKILL;
                            break;
                        case 3:
                            _filter = Options.AugmentationFilter.PASSIVE_SKILL;
                            break;
                        case 4:
                            _filter = Options.AugmentationFilter.CHANCE_SKILL;
                            break;
                        case 5:
                            _filter = Options.AugmentationFilter.STATS;
                    }

                    _page = Integer.parseInt(arg[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                    player.sendMessage("Error.");
                }
                break;
            case "put":
                try {
                    if ((player.isInStoreMode()) || (player.isProcessingRequest()) || (player.isInTrade())) {
                        player.sendMessage("You cannot edit augmentation because you are on store mode");
                        return;
                    }
                    PcInventory inv = player.getInventory();
                    ItemInstance targetItem = inv.getItemByObjectId(inv.getPaperdollObjectId(7));
                    if (targetItem == null) {
                        player.sendMessage("You doesn't have any weapon equipped");
                        return;
                    }
                    if (!check(targetItem))
                        return;
                    if (Util.getPay(player, Config.SERVICES_AUGMENTATION_ITEM, Config.SERVICES_AUGMENTATION_PRICE, true)) {
                        unAugment(targetItem);
                        int augId = Integer.parseInt(arg[1]);
                        int secAugId = AugmentationData.getInstance().generateRandomSecondaryAugmentation();
                        int aug = (augId << 16) + secAugId;
                        targetItem.setAugmentationId(aug);
                        targetItem.setJdbcState(JdbcEntityState.UPDATED);
                        targetItem.update();
                        inv.equipItem(targetItem);
                        player.sendPacket(new InventoryUpdate().addModifiedItem(targetItem));
                        for (ShortCut sc : player.getAllShortCuts()) {
                            if ((sc.getId() == targetItem.getObjectId()) && (sc.getType() == 1))
                                player.sendPacket(new ShortCutRegister(player, sc));
                        }
                        player.sendChanges();
                    }
                    switch (Integer.parseInt(arg[2])) {
                        case 1:
                            _filter = Options.AugmentationFilter.NONE;
                            break;
                        case 2:
                            _filter = Options.AugmentationFilter.ACTIVE_SKILL;
                            break;
                        case 3:
                            _filter = Options.AugmentationFilter.PASSIVE_SKILL;
                            break;
                        case 4:
                            _filter = Options.AugmentationFilter.CHANCE_SKILL;
                            break;
                        case 5:
                            _filter = Options.AugmentationFilter.STATS;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    player.sendMessage("Error.");
                }
                break;
        }
        showMainMenu(player, _page, _filter);
    }

    private void unAugment(ItemInstance item) {
        if (item.getAugmentationId() == 0)
            return;

        Player player = getSelf();
        boolean equipped = item.isEquipped();
        if (equipped) {
            player.getInventory().unEquipItem(item);
        }
        item.setAugmentationId(0);
        item.setJdbcState(JdbcEntityState.UPDATED);
        item.update();
        if (equipped) {
            player.getInventory().equipItem(item);
        }
        InventoryUpdate iu = new InventoryUpdate().addModifiedItem(item);

        SystemMessage2 sm = new SystemMessage2(SystemMsg.AUGMENTATION_HAS_BEEN_SUCCESSFULLY_REMOVED_FROM_YOUR_S1);
        sm.addItemName(item.getItemId());
        player.sendPacket(new ExVariationCancelResult(1), iu, sm);
        for (ShortCut sc : player.getAllShortCuts())
            if ((sc.getId() == item.getObjectId()) && (sc.getType() == 1))
                player.sendPacket(new ShortCutRegister(player, sc));
        player.sendChanges();
    }

    private boolean check(ItemInstance item) {
        if (item.isHeroWeapon())
            return false;

        switch (item.getItemId()) {
            case 13752:
            case 13753:
            case 13754:
            case 13755:
                return false;
        }

        switch (item.getCrystalType()) {
            case NONE:
            case D:
            case C:
            case B:
            case A:
                return false;
        }
        return true;
    }

    private void showMainMenu(Player player, int _page, Options.AugmentationFilter _filter) {
        if (_page < 1) {
            NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
            adminReply.setFile("scripts/services/Augmentations/index.htm");
            player.sendPacket(adminReply);
            return;
        }
        Collection<OptionDataTemplate> augmentations = OptionDataHolder.getUniqueOptions(_filter);
        if (augmentations.isEmpty()) {
            showMainMenu(player, 0, Options.AugmentationFilter.NONE);
            player.sendMessage("Augmentation list is empty. Try with another filter");
            return;
        }
        NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
        adminReply.setFile("scripts/services/Augmentations/list.htm");
        String template = HtmCache.INSTANCE.getNotNull("scripts/services/Augmentations/template.htm", player);
        String block = "";
        StringBuilder list = new StringBuilder();
        StringBuilder pagesHtm = new StringBuilder();
        int maxPage = (int) Math.ceil(augmentations.size() / 7.0D);
        _page = Math.min(_page, maxPage);
        int page = 1;
        int count = 0;
        boolean lastColor = true;

        for (int i = Math.max(maxPage - _page < 3 ? maxPage - MAX_PAGES_PER_PAGE : _page - 3, 1); i <= maxPage; i++) {
            if (i == _page)
                pagesHtm.append("<td background=L2UI_ct1.button_df><button action=\"\" value=\"" + i + "\" width=38 height=20 back=\"\" fore=\"\"></td>");
            else
                pagesHtm.append("<td><button action=\"bypass -h scripts_services.Augmentation:run page " + i + " " + (_filter.ordinal() + 1) + "\" value=\"" + i + "\" width=34 height=20 back=L2UI_ct1.button_df fore=L2UI_ct1.button_df></td>");
            count++;
            if (count >= 6)
                break;
        }
        count = 0;
        for (OptionDataTemplate augm : augmentations) {
            if (!checkId(augm.getId()))
                continue;
            count++;
            if (count >= MAX_AUGMENTATIONS_PER_PAGE) {
                count = 0;
                page++;
                continue;
            }
            if (page > _page)
                break;
            if (page != _page)
                continue;
            Skill skill = !augm.getSkills().isEmpty() ? augm.getSkills().get(0) : !augm.getTriggerList().isEmpty() ? augm.getTriggerList().get(0).getSkill() : null;
            block = template;
            block = block.replace("{bypass}", "bypass -h scripts_services.Augmentation:run put " + augm.getId() + " " + (_filter.ordinal() + 1));
            String name;
            if (skill != null) {
                name = skill.name.length() > 28 ? skill.name.substring(0, 28) : skill.name;
            } else {
                name = "+1 ";
                switch (augm.getId()) {
                    case 16341:
                        name = name + "STR";
                        break;
                    case 16342:
                        name = name + "CON";
                        break;
                    case 16343:
                        name = name + "INT";
                        break;
                    case 16344:
                        name = name + "MEN";
                        break;
                    default:
                        name = name + "(Id:" + augm.getId() + ")";
                }
            }

            block = block.replace("{name}", name);
            block = block.replace("{icon}", skill != null ? skill.icon : "icon.skill5041");
            block = block.replace("{color}", lastColor ? "222222" : "333333");
            block = block.replace("{price}", Util.formatAdena(Config.SERVICES_AUGMENTATION_PRICE) + " " + Util.getItemName(Config.SERVICES_AUGMENTATION_ITEM));
            list.append(block);
            lastColor = !lastColor;
        }
        adminReply.replace("%pages%", pagesHtm.toString());
        adminReply.replace("%augs%", list.toString());
        player.sendPacket(adminReply);
    }

    private boolean checkId(int id) {
        for (int skill : Config.SERVICES_AUGMENTATION_DISABLED_LIST) {
            if (skill == id)
                return false;
        }
        return true;
    }
}