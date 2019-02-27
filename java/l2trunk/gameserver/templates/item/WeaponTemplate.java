package l2trunk.gameserver.templates.item;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.FuncTemplate;

public final class WeaponTemplate extends ItemTemplate {
    private final int soulShotCount;
    private final int spiritShotCount;
    private final int kamaelConvert;
    private final int rndDam;
    private final int atkReuse;
    private final int mpConsume;
    private int critical;

    public WeaponTemplate(StatsSet set) {
        super(set);
        type = set.getEnum("type", WeaponType.class);
        soulShotCount = set.getInteger("soulshots");
        spiritShotCount = set.getInteger("spiritshots");
        kamaelConvert = set.getInteger("kamael_convert");

        rndDam = set.getInteger("rnd_dam");
        atkReuse = set.getInteger("atk_reuse", type == WeaponType.BOW ? 1500 : type == WeaponType.CROSSBOW ? 820 : 0);
        mpConsume = set.getInteger("mp_consume");

        if (getItemType() == WeaponType.NONE) {
            type1 = TYPE1_SHIELD_ARMOR;
            _type2 = TYPE2_SHIELD_ARMOR;
        } else {
            type1 = TYPE1_WEAPON_RING_EARRING_NECKLACE;
            _type2 = TYPE2_WEAPON;
        }

        if (getItemType() == WeaponType.PET) {
            type1 = ItemTemplate.TYPE1_WEAPON_RING_EARRING_NECKLACE;

            if (bodyPart == ItemTemplate.SLOT_WOLF)
                _type2 = ItemTemplate.TYPE2_PET_WOLF;
            else if (bodyPart == ItemTemplate.SLOT_GWOLF)
                _type2 = ItemTemplate.TYPE2_PET_GWOLF;
            else if (bodyPart == ItemTemplate.SLOT_HATCHLING)
                _type2 = ItemTemplate.TYPE2_PET_HATCHLING;
            else
                _type2 = ItemTemplate.TYPE2_PET_STRIDER;

            bodyPart = ItemTemplate.SLOT_R_HAND;
        }
    }

    /**
     * Returns the type of Weapon
     *
     * @return L2WeaponType
     */
    @Override
    public WeaponType getItemType() {
        return (WeaponType) type;
    }

    /**
     * Returns the ID of the Etc item after applying the mask.
     *
     * @return int : ID of the Weapon
     */
    @Override
    public long getItemMask() {
        return getItemType().mask();
    }

    /**
     * Returns the quantity of SoulShot used.
     *
     * @return int
     */
    public int getSoulShotCount() {
        return soulShotCount;
    }

    /**
     * Returns the quatity of SpiritShot used.
     *
     * @return int
     */
    public int getSpiritShotCount() {
        return spiritShotCount;
    }

    public int getCritical() {
        return critical;
    }

    /**
     * Returns the random damage inflicted by the weapon
     *
     * @return int
     */
    public int getRandomDamage() {
        return rndDam;
    }

    /**
     * Return the Attack Reuse Delay of the L2Weapon.<BR><BR>
     *
     * @return int
     */
    public int getAttackReuseDelay() {
        return atkReuse;
    }

    /**
     * Returns the MP consumption with the weapon
     *
     * @return int
     */
    public int getMpConsume() {
        return mpConsume;
    }

    /**
     * Возвращает разницу между длиной этого оружия и стандартной, то есть x-40
     */
    public int getAttackRange() {
        switch (getItemType()) {
            case BOW:
                return 460;
            case CROSSBOW:
                return 360;
            case POLE:
                return 40;
            default:
                return 0;
        }
    }

    @Override
    public void attachFunc(FuncTemplate f) {
        //TODO для параметров set с дп,может считать стат с L2ItemInstance? (VISTALL)
        if (f.stat == Stats.CRITICAL_BASE && f.order == 0x08) {
            critical = (int) Math.round(f.value / 10);
        }
        super.attachFunc(f);
    }

    public int getKamaelConvert() {
        return kamaelConvert;
    }

    public enum WeaponType implements ItemType {
        NONE(1, "Shield", null),
        SWORD(2, "Sword", Stats.SWORD_WPN_VULNERABILITY),
        BLUNT(3, "Blunt", Stats.BLUNT_WPN_VULNERABILITY),
        DAGGER(4, "Dagger", Stats.DAGGER_WPN_VULNERABILITY),
        BOW(5, "Bow", Stats.BOW_WPN_VULNERABILITY),
        POLE(6, "Pole", Stats.POLE_WPN_VULNERABILITY),
        ETC(7, "Etc", null),
        FIST(8, "Fist", Stats.FIST_WPN_VULNERABILITY),
        DUAL(9, "Dual Sword", Stats.DUAL_WPN_VULNERABILITY),
        DUALFIST(10, "Dual Fist", Stats.FIST_WPN_VULNERABILITY),
        BIGSWORD(11, "Big Sword", Stats.SWORD_WPN_VULNERABILITY), // Two Handed Swords
        PET(12, "Pet", Stats.FIST_WPN_VULNERABILITY),
        ROD(13, "Rod", null),
        BIGBLUNT(14, "Big Blunt", Stats.BLUNT_WPN_VULNERABILITY),
        CROSSBOW(15, "Crossbow", Stats.CROSSBOW_WPN_VULNERABILITY),
        RAPIER(16, "Rapier", Stats.DAGGER_WPN_VULNERABILITY),
        ANCIENTSWORD(17, "Ancient Sword", Stats.SWORD_WPN_VULNERABILITY), // Kamael 2h sword
        DUALDAGGER(18, "Dual Dagger", Stats.DAGGER_WPN_VULNERABILITY);

        public final static WeaponType[] VALUES = values();

        private final long mask;
        private final String name;
        private final Stats defence;

        WeaponType(int id, String name, Stats defence) {
            mask = 1L << id;
            this.name = name;
            this.defence = defence;
        }

        public long mask() {
            return mask;
        }

        public Stats getDefence() {
            return defence;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}