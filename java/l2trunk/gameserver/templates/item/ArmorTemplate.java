package l2trunk.gameserver.templates.item;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.templates.item.WeaponTemplate.WeaponType;

public final class ArmorTemplate extends ItemTemplate {
    public static final double EMPTY_RING = 5;
    public static final double EMPTY_EARRING = 9;
    public static final double EMPTY_NECKLACE = 13;
    public static final double EMPTY_HELMET = 12;
    public static final double EMPTY_BODY_FIGHTER = 31;
    public static final double EMPTY_LEGS_FIGHTER = 18;
    public static final double EMPTY_BODY_MYSTIC = 15;
    public static final double EMPTY_LEGS_MYSTIC = 8;
    public static final double EMPTY_GLOVES = 8;
    public static final double EMPTY_BOOTS = 7;

    public ArmorTemplate(StatsSet set) {
        super(set);
        type = set.getEnum("type", ArmorType.class);

        if (bodyPart == SLOT_NECK || (bodyPart & SLOT_L_EAR) != 0 || (bodyPart & SLOT_L_FINGER) != 0) {
            type1 = TYPE1_WEAPON_RING_EARRING_NECKLACE;
            _type2 = TYPE2_ACCESSORY;
        } else if (bodyPart == SLOT_HAIR || bodyPart == SLOT_DHAIR || bodyPart == SLOT_HAIRALL) {
            type1 = TYPE1_OTHER;
            _type2 = ItemTemplate.TYPE2_OTHER;
        } else {
            type1 = TYPE1_SHIELD_ARMOR;
            _type2 = TYPE2_SHIELD_ARMOR;
        }

        if (getItemType() == ArmorType.PET) {
            type1 = TYPE1_SHIELD_ARMOR;
            switch (bodyPart) {
                case SLOT_WOLF:
                    _type2 = TYPE2_PET_WOLF;
                    bodyPart = SLOT_CHEST;
                    break;
                case SLOT_GWOLF:
                    _type2 = TYPE2_PET_GWOLF;
                    bodyPart = SLOT_CHEST;
                    break;
                case SLOT_HATCHLING:
                    _type2 = TYPE2_PET_HATCHLING;
                    bodyPart = SLOT_CHEST;
                    break;
                case SLOT_PENDANT:
                    _type2 = TYPE2_PENDANT;
                    bodyPart = SLOT_NECK;
                    break;
                case SLOT_BABYPET:
                    _type2 = TYPE2_PET_BABY;
                    bodyPart = SLOT_CHEST;
                    break;
                default:
                    _type2 = TYPE2_PET_STRIDER;
                    bodyPart = SLOT_CHEST;
                    break;
            }
        }
    }

    /**
     * Returns the type of the armor.
     *
     * @return L2ArmorType
     */
    @Override
    public ArmorType getItemType() {
        return (ArmorType) super.type;
    }

    /**
     * Returns the ID of the item after applying the mask.
     *
     * @return int : ID of the item
     */
    @Override
    public final long getItemMask() {
        return getItemType().mask();
    }

    public enum ArmorType implements ItemType {
        NONE(1, "None"),
        LIGHT(2, "Light"),
        HEAVY(3, "Heavy"),
        MAGIC(4, "Magic"),
        PET(5, "Pet"),
        SIGIL(6, "Sigil");

        public final static ArmorType[] VALUES = values();

        private final long _mask;
        private final String _name;

        ArmorType(int id, String name) {
            _mask = 1L << (id + WeaponType.VALUES.length);
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