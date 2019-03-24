package l2trunk.gameserver.templates.item;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.time.cron.SchedulingPattern;
import l2trunk.gameserver.handler.items.IItemHandler;
import l2trunk.gameserver.instancemanager.CursedWeaponsManager;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.base.Element;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.StatTemplate;
import l2trunk.gameserver.stats.conditions.Condition;
import l2trunk.gameserver.templates.augmentation.AugmentationInfo;
import l2trunk.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import l2trunk.gameserver.templates.item.WeaponTemplate.WeaponType;

import java.util.*;

public abstract class ItemTemplate extends StatTemplate {
    public static final int ITEM_ID_PC_BANG_POINTS = -100;
    public static final int ITEM_ID_CLAN_REPUTATION_SCORE = -200;
    public static final int ITEM_ID_FAME = -300;
    public static final int ITEM_ID_ADENA = 57;
    /**
     * Item ID для замковых корон
     */
    public static final List<Integer> ITEM_ID_CASTLE_CIRCLET = List.of(
            0, // no castle - no circlet.. :)
            6838, // Circlet of Gludio
            6835, // Circlet of Dion
            6839, // Circlet of Giran
            6837, // Circlet of Oren
            6840, // Circlet of Aden
            6834, // Circlet of Innadril
            6836, // Circlet of Goddard
            8182, // Circlet of Rune
            8183); // Circlet of Schuttgart

    public static final List<Integer> ITEM_ID_CASTLE_CLOAK = List.of(
            0, // no castle - no cloak..
            37018, // Cloak of Gludio
            37016, // Cloak of Dion
            37017, // Cloak of Giran
            37021, // Cloak of Oren
            37015, // Cloak of Aden
            37020, // Cloak of Innadril
            37019, // Cloak of Goddard
            37022, // Cloak of Rune
            37023); // Cloak of Schuttgart
    public static final int ITEM_ID_FORMAL_WEAR = 6408;
    // Uniforms
    public static final int ITEM_ID_SCHOOL_UNIFORM_A = 57000;
    public static final int ITEM_ID_SCHOOL_UNIFORM_B = 57001;
    public static final int ITEM_ID_CHRISTMAS_UNIFORM = 57002;
    public static final int ITEM_ID_KNIGHT_UNIFORM = 57003;
    public static final int ITEM_ID_QUIPAO_UNIFORM = 57004;
    public static final int ITEM_ID_NAVI_UNIFORM = 57005;
    public static final int ITEM_ID_PIRAT_UNIFORM = 57007;
    public static final int ITEM_ID_MUSKETEER_UNIFORM = 57008;
    public static final int ITEM_ID_MAGICIAN_UNIFORM = 57009;
    public static final int ITEM_ID_MAGICIAN_UPGRADED_UNIFORM = 57010;
    public static final int ITEM_ID_NINJA_UNIFORM = 57011;
    public static final int ITEM_ID_DARK_ASSASIN_UNIFORM = 57012;
    public static final int ITEM_ID_METAL_UNIFORM = 57013;
    public static final int TYPE2_WEAPON = 0;
    public static final int TYPE2_SHIELD_ARMOR = 1;
    public static final int TYPE2_ACCESSORY = 2;
    public static final int TYPE2_OTHER = 5;
    public static final int SLOT_UNDERWEAR = 0x00001;
    public static final int SLOT_R_EAR = 0x00002;
    public static final int SLOT_L_EAR = 0x00004;
    public static final int SLOT_NECK = 0x00008;
    public static final int SLOT_R_FINGER = 0x00010;
    public static final int SLOT_L_FINGER = 0x00020;
    public static final int SLOT_HEAD = 0x00040;
    public static final int SLOT_R_HAND = 0x00080;
    public static final int SLOT_L_HAND = 0x00100;
    public static final int SLOT_GLOVES = 0x00200;
    public static final int SLOT_CHEST = 0x00400;
    public static final int SLOT_LEGS = 0x00800;
    public static final int SLOT_FEET = 0x01000;
    public static final int SLOT_BACK = 0x02000;
    public static final int SLOT_LR_HAND = 0x04000;
    public static final int SLOT_FULL_ARMOR = 0x08000;
    public static final int SLOT_HAIR = 0x10000;
    public static final int SLOT_FORMAL_WEAR = 0x20000;
    public static final int SLOT_DHAIR = 0x40000;
    public static final int SLOT_HAIRALL = 0x80000;
    public static final int SLOT_R_BRACELET = 0x100000;
    public static final int SLOT_L_BRACELET = 0x200000;
    public static final int SLOT_DECO = 0x400000;
    public static final int SLOT_BELT = 0x10000000;
    // Все слоты, используемые броней.
    public static final int SLOTS_ARMOR = SLOT_HEAD | SLOT_L_HAND | SLOT_GLOVES | SLOT_CHEST | SLOT_LEGS | SLOT_FEET | SLOT_BACK | SLOT_FULL_ARMOR;
    // Все слоты, используемые бижей.
    public static final int SLOTS_JEWELRY = SLOT_R_EAR | SLOT_L_EAR | SLOT_NECK | SLOT_R_FINGER | SLOT_L_FINGER;
    public static final int CRYSTAL_NONE = 0;
    public static final int CRYSTAL_D = 1458;
    public static final int CRYSTAL_C = 1459;
    public static final int CRYSTAL_B = 1460;
    public static final int CRYSTAL_A = 1461;
    public static final int CRYSTAL_S = 1462;
    public static final int ATTRIBUTE_FIRE = 0;
    public static final int ATTRIBUTE_WATER = 1;
    public static final int ATTRIBUTE_WIND = 2;
    public static final int ATTRIBUTE_EARTH = 3;
    public static final int ATTRIBUTE_HOLY = 4;
    public static final int ATTRIBUTE_DARK = 5;
    static final int TYPE1_WEAPON_RING_EARRING_NECKLACE = 0;
    static final int TYPE1_SHIELD_ARMOR = 1;
    static final int TYPE1_OTHER = 2;
    static final int TYPE1_ITEM_QUESTITEM_ADENA = 4;
    static final int TYPE2_QUEST = 3;
    static final int TYPE2_MONEY = 4;
    static final int TYPE2_PET_WOLF = 6;
    static final int TYPE2_PET_HATCHLING = 7;
    static final int TYPE2_PET_STRIDER = 8;
    static final int TYPE2_PET_GWOLF = 10;
    static final int TYPE2_PENDANT = 11;
    static final int TYPE2_PET_BABY = 12;
    static final int SLOT_NONE = 0x00000;
    static final int SLOT_WOLF = -100;
    static final int SLOT_HATCHLING = -101;
    static final int SLOT_STRIDER = -102;
    static final int SLOT_BABYPET = -103;
    static final int SLOT_GWOLF = -104;
    static final int SLOT_PENDANT = -105;
    public final int itemId;
    protected final String name;
    private final String addname;
    private final String icon;
    public final String icon32;
    private final Grade crystalType; // default to none-grade
    private final ItemClass clazz;
    public final int weight;
    private final boolean masterwork;
    private final int masterworkConvert;
    private final int durability;
    public final int referencePrice;
    private final int crystalCount;
    public final boolean temporal;
    public final boolean stackable;
    private final boolean crystallizable;
    private final ReuseType reuseType;
    private final int reuseDelay;
    private final int reuseGroup;
    private final int agathionEnergy;
    private final List<CapsuledItem> capsuledItems = new ArrayList<>();
    ItemType type;
    int type1; // needed for item list (inventory)
    int type2; // different lists for armor, weapon, etc
    int bodyPart;
    private final List<Skill> skills =new ArrayList<>() ;
    private Map<Integer, AugmentationInfo> augmentationInfos = new HashMap<>();
    private int flags;
    private Skill enchant4Skill = null; // skill that activates when item is enchanted +4 (for duals)
    private int[] _baseAttributes = new int[6];
    private Map<Integer, List<Integer>> enchantOptions = new HashMap<>();
    private Condition condition;
    private IItemHandler handler = IItemHandler.NULL;

    ItemTemplate(final StatsSet set) {
        itemId = set.getInteger("item_id");
        clazz = set.getEnum("class", ItemClass.class, ItemClass.OTHER);
        name = set.getString("name");
        addname = set.getString("add_name", "");
        icon = set.getString("icon", "");
        icon32 = "<img src=icon." + icon + " width=32 height=32>";
        weight = set.getInteger("weight");
        crystallizable = set.isSet("crystallizable");
        stackable = set.isSet("stackable");
        crystalType = set.getEnum("crystal_type", Grade.class, Grade.NONE); // default to none-grade
        durability = set.getInteger("durability", -1);
        temporal = set.isSet("temporal");
        bodyPart = set.getInteger("bodypart");
        referencePrice = set.getInteger("price");
        crystalCount = set.getInteger("crystal_count");
        reuseType = set.getEnum("reuse_type", ReuseType.class, ReuseType.NORMAL);
        reuseDelay = set.getInteger("reuse_delay");
        reuseGroup = set.getInteger("delay_share_group", -itemId);
        agathionEnergy = set.getInteger("agathion_energy");
        masterwork = set.isSet("masterwork");
        masterworkConvert = set.getInteger("masterwork_convert", -1);

        for (ItemFlags f : ItemFlags.VALUES) {
            boolean flag = set.getBool(f.name().toLowerCase(), f.getDefaultValue());
            if (flag) {
                activeFlag(f);
            }
        }

        funcTemplates = new ArrayList<>();
    }

    /**
     * Returns the itemType.
     *
     * @return Enum
     */
    public ItemType getItemType() {
        return type;
    }

    public String getIcon() {
        return icon;
    }

    public final int getDurability() {
        return durability;
    }


    public final int itemId() {
        return itemId;
    }

    public abstract long getItemMask();

    /**
     * Returns the type 2 of the item
     *
     * @return int
     */
    public final int getType2() {
        return type2;
    }

    public final int getBaseAttributeValue(Element element) {
        if (element == Element.NONE) {
            return 0;
        }
        return _baseAttributes[element.getId()];
    }

    public final void setBaseAtributeElements(int[] val) {
        _baseAttributes = val;
    }

    public final int getType2ForPackets() {
        int type2 = this.type2;
        switch (this.type2) {
            case TYPE2_PET_WOLF:
            case TYPE2_PET_HATCHLING:
            case TYPE2_PET_STRIDER:
            case TYPE2_PET_GWOLF:
            case TYPE2_PET_BABY:
                if (bodyPart == ItemTemplate.SLOT_CHEST) {
                    type2 = TYPE2_SHIELD_ARMOR;
                } else {
                    type2 = TYPE2_WEAPON;
                }
                break;
            case TYPE2_PENDANT:
                type2 = TYPE2_ACCESSORY;
                break;
        }
        return type2;
    }

    public final int weight() {
        return weight;
    }

    /**
     * Returns if the item is crystallizable
     *
     * @return boolean
     */
    public final boolean isCrystallizable() {
        return crystallizable && !stackable() && (getCrystalType() != Grade.NONE) && (getCrystalCount() > 0);
    }

    /**
     * Return the type of crystal if item is crystallizable
     *
     * @return int
     */
    public final Grade getCrystalType() {
        return crystalType;
    }

    /**
     * Returns the grade of the item.<BR>
     * <BR>
     * <U><I>Concept :</I></U><BR>
     * In fact, this fucntion returns the type of crystal of the item.
     *
     * @return int
     */
    public final Grade getItemGrade() {
        return getCrystalType();
    }

    /**
     * Returns the quantity of crystals for crystallization
     *
     * @return int
     */
    public final int getCrystalCount() {
        return crystalCount;
    }

    /**
     * Returns the name of the item
     *
     * @return String
     */
    public final String getName() {
        return name;
    }


    public final String getAdditionalName() {
        return addname;
    }

    /**
     * Return the part of the body used with the item.
     *
     * @return int
     */
    public final int getBodyPart() {
        return bodyPart;
    }


    public final int getType1() {
        return type1;
    }

    public final boolean stackable() {
        return stackable;
    }

    public boolean isForHatchling() {
        return type2 == TYPE2_PET_HATCHLING;
    }

    public boolean isForStrider() {
        return type2 == TYPE2_PET_STRIDER;
    }

    public boolean isForWolf() {
        return type2 == TYPE2_PET_WOLF;
    }

    public boolean isForPetBaby() {
        return type2 == TYPE2_PET_BABY;
    }

    /**
     * Returns if item is for great wolf
     *
     * @return boolean
     */
    public boolean isForGWolf() {
        return type2 == TYPE2_PET_GWOLF;
    }

    public boolean isPendant() {
        return type2 == TYPE2_PENDANT;
    }

    public boolean isForPet() {
        return (type2 == TYPE2_PENDANT) || (type2 == TYPE2_PET_HATCHLING) || (type2 == TYPE2_PET_WOLF) || (type2 == TYPE2_PET_STRIDER) || (type2 == TYPE2_PET_GWOLF) || (type2 == TYPE2_PET_BABY);
    }

    /**
     * Add the L2Skill skill to the list of skills generated by the item
     *
     * @param skill : L2Skill
     */
    public void attachSkill(Skill skill) {
        skills.add(skill);
    }

    public List<Skill> getAttachedSkills() {
        return skills;
    }

    public Skill getEnchant4Skill() {
        return enchant4Skill;
    }

    public void setEnchant4Skill(Skill enchant4Skill) {
        this.enchant4Skill = enchant4Skill;
    }

    /**
     * Returns the name of the item
     *
     * @return String
     */
    @Override
    public String toString() {
        return itemId + " " + name;
    }

    /**
     * Определяет призрачный предмет или нет
     *
     * @return true, если предмет призрачный
     */
    public boolean isShadowItem() {
        return (durability > 0) && !temporal;
    }

    public boolean isCommonItem() {
        return name.startsWith("Common Item - ");
    }

    public boolean isAltSeed() {
        return name.contains("Alternative");
    }

    public ItemClass getItemClass() {
        return clazz;
    }

    public boolean isAdena() {
        return (itemId == 57) || (itemId == 6360) || (itemId == 6361) || (itemId == 6362);
    }

    public boolean isLifeStone() {
        return ((itemId >= 8723) && (itemId <= 8762)) || ((itemId >= 9573) && (itemId <= 9576)) || ((itemId >= 10483) && (itemId <= 10486)) || ((itemId >= 12754) && (itemId <= 12763)) || (itemId == 12821) || (itemId == 12822) || ((itemId >= 12840) && (itemId <= 12851)) || (itemId == 14008) || ((itemId >= 14166) && (itemId <= 14169)) || ((itemId >= 16160) && (itemId <= 16167)) || (itemId == 16177) || (itemId == 16178);
    }

    public boolean isEnchantScroll() {
        return ((itemId >= 6569) && (itemId <= 6578)) || ((itemId >= 17255) && (itemId <= 17264)) || ((itemId >= 22314) && (itemId <= 22323)) || ((itemId >= 949) && (itemId <= 962)) || ((itemId >= 729) && (itemId <= 732));
    }

    public boolean isForgottenScroll() {
        return ((itemId >= 10549) && (itemId <= 10599)) || ((itemId >= 12768) && (itemId <= 12778)) || ((itemId >= 14170) && (itemId <= 14227)) || (itemId == 17030) || ((itemId >= 17034) && (itemId <= 17039));
    }

    public boolean isCodexBook() {
        return itemId >= 9625 && itemId <= 9627 || itemId == 6622;
    }

    public boolean isAttributeStone() {
        return itemId >= 9546 && itemId <= 9551;
    }

    public boolean isEquipment() {
        return type1 != TYPE1_ITEM_QUESTITEM_ADENA;
    }

    public boolean isKeyMatherial() {
        return clazz == ItemClass.PIECES;
    }

    public boolean isRecipe() {
        return clazz == ItemClass.RECIPIES;
    }

    public boolean isTerritoryAccessory() {
        return ((itemId >= 13740) && (itemId <= 13748)) || ((itemId >= 14592) && (itemId <= 14600)) || ((itemId >= 14664) && (itemId <= 14672)) || ((itemId >= 14801) && (itemId <= 14809)) || ((itemId >= 15282) && (itemId <= 15299));
    }

    public boolean isArrow() {
        return type == EtcItemType.ARROW;
    }

    public boolean isBelt() {
        return bodyPart == SLOT_BELT;
    }

    public boolean isBracelet() {
        return (bodyPart == SLOT_R_BRACELET) || (bodyPart == SLOT_L_BRACELET);
    }

    public boolean isUnderwear() {
        return bodyPart == SLOT_UNDERWEAR;
    }

    public boolean isCloak() {
        return bodyPart == SLOT_BACK;
    }

    public boolean isTalisman() {
        return bodyPart == SLOT_DECO;
    }

    public boolean isHerb() {
        return type == EtcItemType.HERB;
    }

    public boolean isAtt() {
        return isAttributeCrystal() || isAttributeJewel() || isAttributeEnergy();
    }

    public boolean isAttributeCrystal() {
        return (itemId == 9552) || (itemId == 9553) || (itemId == 9554) || (itemId == 9555) || (itemId == 9556) || (itemId == 9557);
    }

    private boolean isAttributeJewel() {
        return (itemId == 9558) || (itemId == 9559) || (itemId == 9560) || (itemId == 9561) || (itemId == 9562) || (itemId == 9563);
    }

    private boolean isAttributeEnergy() {
        return (itemId == 9564) || (itemId == 9565) || (itemId == 9566) || (itemId == 9567) || (itemId == 9568) || (itemId == 9569);
    }

    public boolean isHeroWeapon() {
        return ((itemId >= 6611) && (itemId <= 6621)) || ((itemId >= 9388) && (itemId <= 9390));
    }

    public boolean isEpolets() {
        return itemId == 9912;
    }

    public boolean isCursed() {
        return CursedWeaponsManager.INSTANCE.isCursed(itemId);
    }

    public boolean isTerritoryFlag() {
        return (itemId == 13560) || (itemId == 13561) || (itemId == 13562) || (itemId == 13563) || (itemId == 13564) || (itemId == 13565) || (itemId == 13566) || (itemId == 13567) || (itemId == 13568);
    }

    public boolean isRod() {
        return getItemType() == WeaponType.ROD;
    }

    public boolean isWeapon() {
        return getType2() == ItemTemplate.TYPE2_WEAPON;
    }

    public boolean isNotAugmented() {
        return itemId == 21712;
    }

    public boolean isArmor() {
        return getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR;
    }

    public boolean isAccessory() {
        return getType2() == ItemTemplate.TYPE2_ACCESSORY;
    }

    public boolean isQuest() {
        return getType2() == ItemTemplate.TYPE2_QUEST;
    }

    public boolean canBeEnchanted(boolean gradeCheck) {
        if (gradeCheck && (getCrystalType() == Grade.NONE)) {
            return false;
        }

        if (isCursed()) {
            return false;
        }

        if (isQuest()) {
            return false;
        }

        return isEnchantable();
    }

    public boolean isEquipable() {
        return (getItemType() == EtcItemType.BAIT) || (getItemType() == EtcItemType.ARROW) || (getItemType() == EtcItemType.BOLT) || !((getBodyPart() == 0) || (this instanceof EtcItemTemplate));
    }

    public boolean testCondition(Playable player, ItemInstance instance) {
        if (condition == null) {
            return true;
        }

        Env env = new Env();
        env.character = player;
        env.item = instance;

        boolean res = condition.test(env);
        if (!res && (condition.getSystemMsg() != null)) {
            if (condition.getSystemMsg().size() > 0) {
                player.sendPacket(new SystemMessage2(condition.getSystemMsg()).addItemName(itemId()));
            } else {
                player.sendPacket(condition.getSystemMsg());
            }
        }

        return res;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    private boolean isEnchantable() {
        return hasFlag(ItemFlags.ENCHANTABLE);
    }

    public boolean isTradeable() {
        return hasFlag(ItemFlags.TRADEABLE);
    }

    public boolean isDestroyable() {
        return hasFlag(ItemFlags.DESTROYABLE);
    }

    public final boolean isDropable() {
        return hasFlag(ItemFlags.DROPABLE);
    }

    public final boolean isSellable() {
        return hasFlag(ItemFlags.SELLABLE);
    }

    public final boolean isAugmentable() {
        return hasFlag(ItemFlags.AUGMENTABLE);
    }

    public final boolean isAttributable() {
        return hasFlag(ItemFlags.ATTRIBUTABLE);
    }

    public final boolean isStoreable() {
        return hasFlag(ItemFlags.STOREABLE);
    }

    public final boolean isFreightable() {
        return hasFlag(ItemFlags.FREIGHTABLE);
    }

    private boolean hasFlag(ItemFlags f) {
        return (flags & f.mask()) == f.mask();
    }

    private void activeFlag(ItemFlags f) {
        flags |= f.mask();
    }

    public IItemHandler getHandler() {
        return handler;
    }

    public final void setHandler(IItemHandler handler) {
        this.handler = handler;
    }

    public final int getReuseDelay() {
        return reuseDelay;
    }

    public int getReuseGroup() {
        return reuseGroup;
    }

    public int getDisplayReuseGroup() {
        return reuseGroup < 0 ? -1 : reuseGroup;
    }

    public int getAgathionEnergy() {
        return agathionEnergy;
    }

    public void addEnchantOptions(int level, List<Integer> options) {
        enchantOptions.put(level, options);
    }

    public Map<Integer, List<Integer>> getEnchantOptions() {
        return enchantOptions;
    }

    public ReuseType getReuseType() {
        return reuseType;
    }

    public boolean isShield() {
        return this.bodyPart == 256;
    }

    public void addCapsuledItem(CapsuledItem ci) {
        this.capsuledItems.add(ci);
    }

    public boolean isMasterwork() {
        return masterwork;
    }

    public int getMasterworkConvert() {
        return masterworkConvert;
    }

    public void addAugmentationInfo(AugmentationInfo augmentationInfo) {
        if (augmentationInfos.isEmpty()) {
            augmentationInfos = new HashMap<>();
        }
        augmentationInfos.put(augmentationInfo.getMineralId(), augmentationInfo);
    }

    public enum ReuseType {
        NORMAL(SystemMsg.THERE_ARE_S2_SECONDS_REMAINING_IN_S1S_REUSE_TIME, SystemMsg.THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_IN_S1S_REUSE_TIME, SystemMsg.THERE_ARE_S2_HOURS_S3_MINUTES_AND_S4_SECONDS_REMAINING_IN_S1S_REUSE_TIME) {
            @Override
            public long next(ItemInstance item) {
                return System.currentTimeMillis() + item.getTemplate().getReuseDelay();
            }
        },
        EVERY_DAY_AT_6_30(SystemMsg.THERE_ARE_S2_SECONDS_REMAINING_FOR_S1S_REUSE_TIME, SystemMsg.THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_FOR_S1S_REUSE_TIME, SystemMsg.THERE_ARE_S2_HOURS_S3_MINUTES_S4_SECONDS_REMAINING_FOR_S1S_REUSE_TIME) {
            private final SchedulingPattern _pattern = new SchedulingPattern("30 6 * * *");

            @Override
            public long next(ItemInstance item) {
                return _pattern.next(System.currentTimeMillis());
            }
        };

        private final SystemMsg[] _messages;

        ReuseType(SystemMsg... msg) {
            _messages = msg;
        }

        public abstract long next(ItemInstance item);

        public SystemMsg[] getMessages() {
            return _messages;
        }
    }

    public enum ItemClass {
        ALL,
        WEAPON,
        ARMOR,
        JEWELRY,
        ACCESSORY,
        /**
         * Soul/Spiritshot, Potions, Scrolls
         */
        CONSUMABLE,
        /**
         * Common craft matherials
         */
        MATHERIALS,
        /**
         * Special (item specific) craft matherials
         */
        PIECES,
        /**
         * Crafting recipies
         */
        RECIPIES,
        /**
         * Skill learn books
         */
        SPELLBOOKS,
        /**
         * Dyes, lifestones
         */
        MISC,
        EXTRACTABLE,
        OTHER
    }

    public enum Grade {
        NONE(CRYSTAL_NONE, 0),
        D(CRYSTAL_D, 1),
        C(CRYSTAL_C, 2),
        B(CRYSTAL_B, 3),
        A(CRYSTAL_A, 4),
        S(CRYSTAL_S, 5),
        S80(CRYSTAL_S, 5),
        S84(CRYSTAL_S, 5);

        public final int cry;
        public final int externalOrdinal;

        Grade(int crystal, int ext) {
            cry = crystal;
            externalOrdinal = ext;
        }
    }

    public static class CapsuledItem {
        private final int item_id;
        private final double chance;

        public CapsuledItem(int item_id, double chance) {
            this.item_id = item_id;
            this.chance = chance;
        }

        public int getItemId() {
            return this.item_id;
        }

        public double getChance() {
            return this.chance;
        }
    }
}