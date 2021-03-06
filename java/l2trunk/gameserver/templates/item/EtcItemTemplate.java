package l2trunk.gameserver.templates.item;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.templates.item.ArmorTemplate.ArmorType;
import l2trunk.gameserver.templates.item.WeaponTemplate.WeaponType;

public final class EtcItemTemplate extends ItemTemplate {
    public EtcItemTemplate(StatsSet set) {
        super(set);
        type = set.getEnum("type", EtcItemType.class);
        type1 = TYPE1_ITEM_QUESTITEM_ADENA;
        switch (getItemType()) {
            case QUEST:
                type2 = TYPE2_QUEST;
                break;
            case MONEY:
                type2 = TYPE2_MONEY;
                break;
            default:
                type2 = TYPE2_OTHER;
                break;
        }
    }

    /**
     * Returns the type of Etc Item
     *
     * @return L2EtcItemType
     */
    @Override
    public EtcItemType getItemType() {
        return (EtcItemType) super.type;
    }

    /**
     * Returns the ID of the Etc item after applying the mask.
     *
     * @return int : ID of the EtcItem
     */
    @Override
    public long getItemMask() {
        return getItemType().mask();
    }

    @Override
    public final boolean isShadowItem() {
        return false;
    }

    @Override
    public final boolean canBeEnchanted(boolean gradeCheck) {
        return false;
    }

    public enum EtcItemType implements ItemType {
        ARROW(1, "Arrow"),
        MATERIAL(2, "Material"),
        PET_COLLAR(3, "PetCollar"),
        POTION(4, "Potion"),
        RECIPE(5, "Recipe"),
        SCROLL(6, "Scroll"),
        QUEST(7, "Quest"),
        MONEY(8, "Money"),
        OTHER(9, "Other"),
        SPELLBOOK(10, "Spellbook"),
        SEED(11, "Seed"),
        BAIT(12, "Bait"),
        SHOT(13, "Shot"),
        BOLT(14, "Bolt"),
        RUNE(15, "Rune"),
        HERB(16, "Herb"),
        MERCENARY_TICKET(17, "Mercenary Ticket");

        private final long _mask;
        private final String _name;

        EtcItemType(int id, String name) {
            _mask = 1L << (id + WeaponType.VALUES.length + ArmorType.VALUES.length);
            _name = name;
        }

        public long mask() {
            return _mask;
        }

        @Override
        public String toString() {
            return _name;
        }
    }
}