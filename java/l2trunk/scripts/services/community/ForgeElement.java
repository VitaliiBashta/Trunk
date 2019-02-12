package l2trunk.scripts.services.community;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.FoundationHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Element;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.templates.item.ItemTemplate;

public final class ForgeElement {
    static String[] generateAttribution(ItemInstance item, int slot, Player player, boolean hasBonus) {
        String[] data = new String[4];

        String noicon = "icon.NOIMAGE";
        String slotclose = "L2UI_CT1.ItemWindow_DF_SlotBox_Disable";
        String dot = "<font color=\"FF0000\">...</font>";
        String immposible = new CustomMessage("communityboard.forge.attribute.immposible").toString();
        String maxenchant = new CustomMessage("communityboard.forge.attribute.maxenchant").toString();
        String heronot = new CustomMessage("communityboard.forge.attribute.heronot").toString();
        String picenchant = "l2ui_ch3.multisell_plusicon";
        String pvp = "icon.pvp_tab";

        if (item != null) {
            data[0] = item.getTemplate().getIcon();
            data[1] = item.getName() + " " + (item.getEnchantLevel() > 0 ? "+" + item.getEnchantLevel() : "");
            if ((item.getTemplate().isAttributable()) && (itemCheckGrade(hasBonus, item))) {
                if (item.isHeroWeapon()) {
                    data[2] = heronot;
                    data[3] = slotclose;
                } else if (((item.isArmor()) && (((item.getAttributes().getFire() | item.getAttributes().getWater()) & (item.getAttributes().getWind() | item.getAttributes().getEarth()) & (item.getAttributes().getHoly() | item.getAttributes().getUnholy())) >= Config.BBS_FORGE_ARMOR_ATTRIBUTE_MAX))
                        || ((item.isWeapon()) && (item.getAttributes().getValue() >= Config.BBS_FORGE_WEAPON_ATTRIBUTE_MAX))
                        || item.isAccessory()
                        || item.getTemplate().isShield()) {
                    data[2] = maxenchant;
                    data[3] = slotclose;
                } else {
                    data[2] = "<button action=\"bypass _bbsforge:attribute:item:" + slot + "\" value=\"" + new CustomMessage("common.enchant.attribute").toString() + "\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
					/*
					if (item.getTemplate().isPvP())
						data[3] = pvp;
					else
					*/
                    data[3] = picenchant;
                }
            } else {
                data[2] = immposible;
                data[3] = slotclose;
            }
        } else {
            data[0] = noicon;
            data[1] = new CustomMessage("common.item.not.clothed." + slot + "").toString();
            data[2] = dot;
            data[3] = slotclose;
        }

        return data;
    }

    static String[] generateEnchant(ItemInstance item, int max, int slot, Player player) {
        String[] data = new String[4];

        String noicon = "icon.NOIMAGE";
        String slotclose = "L2UI_CT1.ItemWindow_DF_SlotBox_Disable";
        String dot = "<font color=\"FF0000\">...</font>";
        String maxenchant = new CustomMessage("communityboard.forge.enchant.max").toString();
        String picenchant = "l2ui_ch3.multisell_plusicon";
        String pvp = "icon.pvp_tab";

        if (item != null) {
            data[0] = item.getTemplate().getIcon();
            data[1] = item.getName() + " " + (item.getEnchantLevel() > 0 ? "+" + item.getEnchantLevel() : "");
            if (!item.getTemplate().isArrow()) {
                if ((item.getEnchantLevel() >= max) || (!item.canBeEnchanted(true))) {
                    data[2] = maxenchant;
                    data[3] = slotclose;
                } else {
                    data[2] = "<button action=\"bypass _bbsforge:enchant:item:" + slot + "\" value=\"" + new CustomMessage("common.enchant").toString() + "\"width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
					/*
					if (item.getTemplate().isPvP())
						data[3] = pvp;
					else
					*/
                    data[3] = picenchant;
                }
            } else {
                data[2] = dot;
                data[3] = slotclose;
            }
        } else {
            data[0] = noicon;
            data[1] = new CustomMessage("common.item.not.clothed." + slot + "").toString();
            data[2] = dot;
            data[3] = slotclose;
        }

        return data;
    }

    static String[] generateFoundation(ItemInstance item, int slot, Player player) {
        String[] data = new String[4];

        String noicon = "icon.NOIMAGE";
        String slotclose = "L2UI_CT1.ItemWindow_DF_SlotBox_Disable";
        String dot = "<font color=\"FF0000\">...</font>";
        String no = new CustomMessage("communityboard.forge.no.foundation").toString();
        String picenchant = "l2ui_ch3.multisell_plusicon";
        String pvp = "icon.pvp_tab";

        if (item != null) {
            data[0] = item.getTemplate().getIcon();
            data[1] = item.getName() + " " + (item.getEnchantLevel() > 0 ? new StringBuilder().append("+").append(item.getEnchantLevel()).toString() : "");
            if (!item.getTemplate().isArrow()) {
                int found = FoundationHolder.getFoundation(item.getItemId());
                if (found == -1) {
                    data[2] = no;
                    data[3] = slotclose;
                } else {
                    data[2] = "<button action=\"bypass _bbsforge:foundation:item:" + slot + "\" value=\"" + new CustomMessage("common.exchange").toString() + "\"width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
					/*
					if (item.getTemplate().isPvP())
						data[3] = pvp;
					else
					*/
                    data[3] = picenchant;
                }
            } else {
                data[2] = dot;
                data[3] = slotclose;
            }
        } else {
            data[0] = noicon;
            data[1] = new CustomMessage("common.item.not.clothed." + slot + "").toString();
            data[2] = dot;
            data[3] = slotclose;
        }

        return data;
    }

    static String page(Player player) {
        return HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "forge/page_template.htm", player);
    }

    static boolean itemCheckGrade(boolean hasBonus, ItemInstance item) {
        ItemTemplate.Grade grade = item.getCrystalType();

        switch (grade) {
//			case NONE:
//				return hasBonus;
//			case D:
//				return hasBonus;
//			case C:
//				return hasBonus;
//			case B:
//				return hasBonus;
//			case A:
//				return hasBonus;
//			case S:
//				return hasBonus;
            case S80:
                return hasBonus;
            case S84:
                return hasBonus;
        }
        return false;
    }

    static boolean canEnchantArmorAttribute(int attr, ItemInstance item) {
        switch (attr) {
            case 0:
                if (item.getAttributeElementValue(Element.getReverseElement(Element.FIRE), false) == 0)
                    break;
                return false;
            case 1:
                if (item.getAttributeElementValue(Element.getReverseElement(Element.WATER), false) == 0)
                    break;
                return false;
            case 2:
                if (item.getAttributeElementValue(Element.getReverseElement(Element.WIND), false) == 0)
                    break;
                return false;
            case 3:
                if (item.getAttributeElementValue(Element.getReverseElement(Element.EARTH), false) == 0)
                    break;
                return false;
            case 4:
                if (item.getAttributeElementValue(Element.getReverseElement(Element.HOLY), false) == 0)
                    break;
                return false;
            case 5:
                if (item.getAttributeElementValue(Element.getReverseElement(Element.UNHOLY), false) == 0)
                    break;
                return false;
        }

        return true;
    }
}
