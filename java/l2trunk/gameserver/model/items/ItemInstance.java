package l2trunk.gameserver.model.items;

import l2trunk.commons.dao.JdbcEntity;
import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.dao.ItemsDAO;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.instancemanager.CursedWeaponsManager;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.base.Element;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.attachment.FlagItemAttachment;
import l2trunk.gameserver.model.items.attachment.ItemAttachment;
import l2trunk.gameserver.model.items.listeners.ItemEnchantOptionsListener;
import l2trunk.gameserver.network.serverpackets.DropItem;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.network.serverpackets.SpawnItem;
import l2trunk.gameserver.scripts.Events;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.Func;
import l2trunk.gameserver.stats.funcs.FuncTemplate;
import l2trunk.gameserver.tables.PetDataTable;
import l2trunk.gameserver.taskmanager.ItemsAutoDestroy;
import l2trunk.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.templates.item.ItemTemplate.Grade;
import l2trunk.gameserver.templates.item.ItemTemplate.ItemClass;
import l2trunk.gameserver.templates.item.ItemType;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Stream;

public final class ItemInstance extends GameObject implements JdbcEntity {
    public static final int CHARGED_NONE = 0;
    public static final int CHARGED_SOULSHOT = 1;
    public static final int CHARGED_SPIRITSHOT = 1;
    public static final int CHARGED_BLESSED_SPIRITSHOT = 2;
    private static final int FLAG_NO_DROP = 1;
    private static final int FLAG_NO_TRADE = 1 << 1;
    private static final int FLAG_NO_TRANSFER = 1 << 2;
    private static final int FLAG_NO_CRYSTALLIZE = 1 << 3;
    private static final int FLAG_NO_ENCHANT = 1 << 4;
    private static final int FLAG_NO_DESTROY = 1 << 5;
    private static final ItemsDAO ITEMS_DAO = ItemsDAO.INSTANCE;
    /**
     * ID of the owner
     */
    private int ownerId;
    /**
     * ID of the item
     */
    private int itemId;
    private int visualItemId = 0;
    /**
     * Quantity of the item
     */
    private long count;

    private int enchantLevel = -1;

    private ItemLocation loc;
    /**
     * Slot where item is stored
     */
    private int locData;
    /**
     * Custom item types (used loto, race tickets)
     */
    private int customType1;
    private int customType2;
    /**
     * Время жизни временных вещей
     */
    private int lifeTime;
    /**
     * Спецфлаги для конкретного инстанса
     */
    private int customFlags;
    /**
     * Атрибуты вещи
     */
    private ItemAttributes attrs = new ItemAttributes();
    /**
     * Аугментация вещи
     */
    private List<Integer> enchantOptions = new ArrayList<>();
    /**
     * Object L2Item associated to the item
     */
    private ItemTemplate template;
    /**
     * Флаг, что вещь одета, выставляется в инвентаре
     **/
    private boolean isEquipped;
    /**
     * Item drop time for autodestroy task
     */
    private long timeToDeleteAfterDrop;
    private Set<Integer> dropPlayers = new HashSet<>();
    private long _dropTimeOwner;
    private int _chargedSoulshot = CHARGED_NONE;
    private int chargedSpiritshot = CHARGED_NONE;
    private boolean _chargedFishtshot = false;
    private int augmentationId;
    private int agathionEnergy;
    private int[] augmentations = new int[2];
    private ItemAttachment attachment;
    private JdbcEntityState state = JdbcEntityState.CREATED;
    private ScheduledFuture<?> timerTask;

    public ItemInstance(int objectId) {
        super(objectId);
    }

    public ItemInstance(int objectId, int itemId) {
        super(objectId);
        setItemId(itemId);
        setLifeTime(getTemplate().temporal ? (int) (System.currentTimeMillis() / 1000L) + getTemplate().getDurability() * 60 : getTemplate().getDurability());
        setAgathionEnergy(getTemplate().getAgathionEnergy());
        setLocData(-1);
        setEnchantLevel(0);
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int id) {
        itemId = id;
        template = ItemHolder.getTemplate(id);
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        if (count < 0)
            count = 0;

        if (!isStackable() && count > 1L) {
            this.count = 1L;
            return;
        }

        this.count = count;
    }

    public int getEnchantLevel() {
        return enchantLevel;
    }

    public void setEnchantLevel(int enchantLevel) {
        final int old = this.enchantLevel;

        this.enchantLevel = enchantLevel;

        if (old != this.enchantLevel && template.getEnchantOptions().size() > 0) {
            Player player = GameObjectsStorage.getPlayer(ownerId);

            if (isEquipped() && player != null)
                ItemEnchantOptionsListener.getInstance().onUnequip(getEquipSlot(), this, player);

            List<Integer> enchantOptions = template.getEnchantOptions().get(this.enchantLevel);

            this.enchantOptions = enchantOptions == null ? new ArrayList<>() : enchantOptions;

            if (isEquipped() && player != null)
                ItemEnchantOptionsListener.getInstance().onEquip(getEquipSlot(), this, player);
        }
    }

    public String getLocName() {
        return loc.name();
    }

    public void setLocName(String loc) {
        this.loc = ItemLocation.valueOf(loc);
    }

    public ItemLocation getLocation() {
        return loc;
    }

    public void setLocation(ItemLocation loc) {
        this.loc = loc;
    }

    public int getLocData() {
        return locData;
    }

    public void setLocData(int locData) {
        this.locData = locData;
    }

    public int getCustomType1() {
        return customType1;
    }

    public void setCustomType1(int newtype) {
        customType1 = newtype;
    }

    public int getCustomType2() {
        return customType2;
    }

    public void setCustomType2(int newtype) {
        customType2 = newtype;
    }

    public int getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(int lifeTime) {
        this.lifeTime = Math.max(0, lifeTime);
    }

    public int getCustomFlags() {
        return customFlags;
    }

    public void setCustomFlags(int flags) {
        customFlags = flags;
    }

    public ItemAttributes getAttributes() {
        return attrs;
    }

    public void setAttributes(ItemAttributes attrs) {
        this.attrs = attrs;
    }

    public int getShadowLifeTime() {
        if (!isShadowItem())
            return 0;
        return getLifeTime();
    }

    public int getTemporalLifeTime() {
        if (!isTemporalItem())
            return 0;
        return getLifeTime() - (int) (System.currentTimeMillis() / 1000L);
    }

    void startTimer(Runnable r) {
        timerTask = LazyPrecisionTaskManager.getInstance().scheduleAtFixedRate(r, 0, 60000L);
    }

    void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel(false);
            timerTask = null;
        }
    }

    public boolean isEquipable() {
        if (getAttachment() != null && (getAttachment() instanceof FlagItemAttachment) && !((FlagItemAttachment) getAttachment()).canBeLost())
            return false;

        return template.isEquipable();
    }

    /**
     * Returns if item is equipped
     *
     * @return boolean
     */
    public boolean isEquipped() {
        return isEquipped;
    }

    public void setEquipped(boolean isEquipped) {
        this.isEquipped = isEquipped;
    }

    public int getBodyPart() {
        return template.getBodyPart();
    }

    public int getEquipSlot() {
        return locData;
    }

    public ItemTemplate getTemplate() {
        return template;
    }

    public long getTimeToDeleteAfterDrop() {
        return timeToDeleteAfterDrop;
    }

    public void setTimeToDeleteAfterDrop(long time) {
        timeToDeleteAfterDrop = time;
    }

    public long getDropTimeOwner() {
        return _dropTimeOwner;
    }

    public ItemType getItemType() {
        return template.getItemType();
    }

    public boolean isArmor() {
        return template.isArmor();
    }

    public boolean isAccessory() {
        return template.isAccessory();
    }

    public boolean isWeapon() {
        return template.isWeapon();
    }

    public boolean isNotAugmented() {
        return template.isNotAugmented();
    }

    public boolean isArrow() {
        return template.isArrow();
    }

    public boolean isUnderwear() {
        return template.isUnderwear();
    }

    public int getReferencePrice() {
        return template.referencePrice;
    }

    /**
     * Returns if item is stackable
     *
     * @return boolean
     */
    public boolean isStackable() {
        return template.stackable();
    }

    @Override
    public void onAction(Player player, boolean shift) {
        if (Events.onAction(player, this, shift))
            return;

        if (player.isCursedWeaponEquipped() && CursedWeaponsManager.INSTANCE.isCursed(itemId))
            return;

        player.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, this);
    }

    public boolean isAugmented() {
        return getAugmentationId() != 0;
    }

    //	public boolean isAugmented()
//	{
//		return augmentationId != 0;
//	}
    public int getAugmentationId() {
        return augmentationId;
    }

    public void setAugmentationId(int val) {
        augmentationId = val;
    }

    /**
     * Returns the type of charge with SoulShot of the item.
     *
     * @return int (CHARGED_NONE, CHARGED_SOULSHOT)
     */
    public int getChargedSoulshot() {
        return _chargedSoulshot;
    }

    /**
     * Sets the type of charge with SoulShot of the item
     *
     * @param type : int (CHARGED_NONE, CHARGED_SOULSHOT)
     */
    public void setChargedSoulshot(int type) {
        _chargedSoulshot = type;
    }

    /**
     * Returns the type of charge with SpiritShot of the item
     *
     * @return int (CHARGED_NONE, CHARGED_SPIRITSHOT, CHARGED_BLESSED_SPIRITSHOT)
     */
    public int getChargedSpiritshot() {
        return chargedSpiritshot;
    }

    /**
     * Sets the type of charge with SpiritShot of the item
     *
     * @param type : int (CHARGED_NONE, CHARGED_SPIRITSHOT, CHARGED_BLESSED_SPIRITSHOT)
     */
    public void setChargedSpiritshot(int type) {
        chargedSpiritshot = type;
    }

    public boolean getChargedFishshot() {
        return _chargedFishtshot;
    }

    public void setChargedFishshot(boolean type) {
        _chargedFishtshot = type;
    }

    /**
     * This function basically returns a set of functions from
     * L2Item/L2Armor/L2Weapon, but may add additional
     * functions, if this particular item instance is enhanched
     * for a particular getPlayer.
     */
    public Stream<Func> getStatFuncs() {
        List<Func> result = new ArrayList<>();

        List<Func> funcs = new ArrayList<>();

        if (template.getAttachedFuncs().size() > 0)
            for (FuncTemplate t : template.getAttachedFuncs()) {
                Func f = t.getFunc(this);
                if (f != null)
                    funcs.add(f);
            }

        for (Element e : Element.VALUES) {
            if (isWeapon())
                funcs.add(new FuncAttack(e, 0x40, this));
            if (isArmor())
                funcs.add(new FuncDefence(e, 0x40, this));
        }

        if (!funcs.isEmpty())
            result = funcs;

        return result.stream();
    }

    public boolean isHeroWeapon() {
        return template.isHeroWeapon();
    }

    public boolean canBeDestroyed(Player player) {
        if ((customFlags & FLAG_NO_DESTROY) == FLAG_NO_DESTROY)
            return false;

        if (isHeroWeapon())
            return false;

        if (PetDataTable.isPetControlItem(this) && player.isMounted())
            return false;

        if (player.getPetControlItem() == this)
            return false;

        if (player.getEnchantScroll() == this)
            return false;

        if (isCursed())
            return false;

        if (getAttachment() != null && (getAttachment() instanceof FlagItemAttachment) && !((FlagItemAttachment) getAttachment()).canBeLost())
            return false;

        return template.isDestroyable();
    }

    public boolean canBeDropped(Player player, boolean pk) {
        if (player.isGM())
            return true;

        if ((customFlags & FLAG_NO_DROP) == FLAG_NO_DROP)
            return false;

        if (isShadowItem())
            return false;

        if (isTemporalItem())
            return false;

        if (isAugmented() && (!pk || !Config.DROP_ITEMS_AUGMENTED) && !Config.ALT_ALLOW_DROP_AUGMENTED)
            return false;

        if (!ItemFunctions.checkIfCanDiscard(player, this))
            return false;

        if (!template.isDropable())
            return false;

        return getAttachment() == null || (!(getAttachment() instanceof FlagItemAttachment)) || ((FlagItemAttachment) getAttachment()).canBeLost();
    }

    public boolean canBeTraded(Player player) {
        if (isEquipped())
            return false;

        if (player.isGM())
            return true;

        if ((customFlags & FLAG_NO_TRADE) == FLAG_NO_TRADE)
            return false;

        if (isShadowItem())
            return false;

        if (isTemporalItem())
            return false;

        if (isAugmented() && !Config.ALT_ALLOW_DROP_AUGMENTED)
            return false;

        if (!ItemFunctions.checkIfCanDiscard(player, this))
            return false;

        if (!template.isTradeable() && !Config.CAN_BE_TRADED_NO_TARADEABLE)
            return false;

        if (!template.isSellable() && !Config.CAN_BE_TRADED_NO_SELLABLE)
            return false;

        if (!template.isStoreable() && !Config.CAN_BE_TRADED_NO_STOREABLE)
            return false;

        if (isShadowItem() && !Config.CAN_BE_TRADED_SHADOW_ITEM)
            return false;

        if (isHeroWeapon() && !Config.CAN_BE_TRADED_HERO_WEAPON)
            return false;

        return getAttachment() == null || (!(getAttachment() instanceof FlagItemAttachment)) || ((FlagItemAttachment) getAttachment()).canBeLost();
    }

    /**
     * Можно ли продать в магазин NPC
     */
    public boolean canBeSold(Player player) {
        if ((customFlags & FLAG_NO_DESTROY) == FLAG_NO_DESTROY)
            return false;

        if (itemId == ItemTemplate.ITEM_ID_ADENA)
            return false;

        if (template.referencePrice == 0)
            return false;

        if (isShadowItem())
            return false;

        if (isTemporalItem())
            return false;

        if (isAugmented() && !Config.ALT_ALLOW_DROP_AUGMENTED)
            return false;

        if (isEquipped())
            return false;

        if (!ItemFunctions.checkIfCanDiscard(player, this))
            return false;

        if (!template.isTradeable())
            return false;

        if (!template.isSellable())
            return false;

        if (!template.isStoreable())
            return false;

        return attachment == null || (!(attachment instanceof FlagItemAttachment)) || ((FlagItemAttachment) attachment).canBeLost();
    }

    public boolean canBeStored(Player player, boolean privatewh) {
        if ((customFlags & FLAG_NO_TRANSFER) == FLAG_NO_TRANSFER)
            return false;

        if (!template.isStoreable())
            return false;

        if (!privatewh && (isShadowItem() || isTemporalItem()))
            return false;

        if (!privatewh && isAugmented() && !Config.ALT_ALLOW_DROP_AUGMENTED)
            return false;

        if (isEquipped())
            return false;

        if (!ItemFunctions.checkIfCanDiscard(player, this))
            return false;

        if (!privatewh && isAugmented() && !Config.CAN_BE_CWH_IS_AUGMENTED)
            return false;

        if (getAttachment() != null && (getAttachment() instanceof FlagItemAttachment) && !((FlagItemAttachment) getAttachment()).canBeLost())
            return false;

        return privatewh || template.isTradeable();
    }

    public boolean canBeCrystallized(Player player) {
        if ((customFlags & FLAG_NO_CRYSTALLIZE) == FLAG_NO_CRYSTALLIZE)
            return false;

        if (isShadowItem())
            return false;

        if (isTemporalItem())
            return false;

        if (!ItemFunctions.checkIfCanDiscard(player, this))
            return false;

        if (getAttachment() != null && (getAttachment() instanceof FlagItemAttachment) && !((FlagItemAttachment) getAttachment()).canBeLost())
            return false;

        return template.isCrystallizable();
    }

    public boolean canBeEnchanted(boolean gradeCheck) {
        if ((customFlags & FLAG_NO_ENCHANT) == FLAG_NO_ENCHANT)
            return false;

        return template.canBeEnchanted(gradeCheck);
    }

    public boolean canBeAugmented(Player player, boolean isAccessoryLifeStone) {
        if (!canBeEnchanted(true))
            return false;

        if (isAugmented())
            return false;

        if (isCommonItem())
            return false;

        if (isTerritoryAccessory())
            return false;

        if (template.getItemGrade().ordinal() < Grade.C.ordinal())
            return false;

        if (!template.isAugmentable())
            return false;

        if (isAccessory())
            return isAccessoryLifeStone;

        if (isArmor())
            return Config.ALT_ALLOW_AUGMENT_ALL;

        if (isWeapon())
            return !isAccessoryLifeStone;

        return true;
    }

    public boolean canBeExchanged(Player player) {
        if ((customFlags & FLAG_NO_DESTROY) == FLAG_NO_DESTROY)
            return false;

        if (isShadowItem())
            return false;

        if (isHeroWeapon())
            return false;

        if (isTemporalItem())
            return false;

        if (!ItemFunctions.checkIfCanDiscard(player, this))
            return false;

        if (getAttachment() != null && (getAttachment() instanceof FlagItemAttachment) && !((FlagItemAttachment) getAttachment()).canBeLost())
            return false;

        return template.isDestroyable();
    }

    private boolean isTerritoryAccessory() {
        return template.isTerritoryAccessory();
    }

    public boolean isShadowItem() {
        return template.isShadowItem();
    }

    public boolean isTemporalItem() {
        return template.temporal;
    }

    private boolean isCommonItem() {
        return template.isCommonItem();
    }

    public boolean isAdena() {
        return template.isAdena();
    }

    public boolean isCursed() {
        return template.isCursed();
    }

    /**
     * Бросает на землю лут с NPC
     */
    public void dropToTheGround(Player lastAttacker, NpcInstance fromNpc) {
        Creature dropper = fromNpc;
        if (dropper == null)
            dropper = lastAttacker;

        Location pos = Location.findAroundPosition(dropper, 100);

        // activate non owner penalty
        if (lastAttacker != null) // lastAttacker в данном случае top damager
        {
            dropPlayers = new HashSet<>(1, 2);
            for (Player player : lastAttacker.getPlayerGroup())
                dropPlayers.add(player.objectId());

            _dropTimeOwner = System.currentTimeMillis() + Config.NONOWNER_ITEM_PICKUP_DELAY + (fromNpc != null && fromNpc.isRaid() ? 285000 : 0);
        }

        // Init the dropped L2ItemInstance and add it in the world as a visible object at the position where mob was last
        dropMe(dropper, pos);

        // Add drop to auto destroy item task
        if (isHerb())
            ItemsAutoDestroy.INSTANCE.addHerb(this);
        else if (Config.AUTODESTROY_ITEM_AFTER > 0 && !isCursed() && attachment == null)
            ItemsAutoDestroy.INSTANCE.addItem(this, Config.AUTODESTROY_ITEM_AFTER * 1000L);
    }

    /**
     * Бросает вещь на землю туда, где ее можно поднять
     */
    public void dropToTheGround(Creature dropper, Location dropPos) {
        if (GeoEngine.canMoveToCoord(dropper, dropPos))
            dropMe(dropper, dropPos);
        else
            dropMe(dropper, dropper.getLoc());
    }

    /**
     * Бросает вещь на землю из инвентаря туда, где ее можно поднять
     */
    public void dropToTheGround(Playable dropper, Location dropPos) {
        setLocation(ItemLocation.VOID);
        if (getJdbcState().isPersisted()) {
            setJdbcState(JdbcEntityState.UPDATED);
            update();
        }

        if (GeoEngine.canMoveToCoord(dropper, dropPos))
            dropMe(dropper, dropPos);
        else
            dropMe(dropper, dropper.getLoc());

        // Add drop to auto destroy item task from getPlayer items.
        if (Config.AUTODESTROY_PLAYER_ITEM_AFTER > 0 && attachment == null)
            ItemsAutoDestroy.INSTANCE.addItem(this, Config.AUTODESTROY_PLAYER_ITEM_AFTER * 1000L);
    }

    /**
     * Init a dropped L2ItemInstance and add it in the world as a visible object.<BR><BR>
     *
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Set the x,y,z position of the L2ItemInstance dropped and update its _worldregion </li>
     * <li>Add the L2ItemInstance dropped to _visibleObjects of its L2WorldRegion</li>
     * <li>Add the L2ItemInstance dropped in the world as a <B>visible</B> object</li><BR><BR>
     *
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T ADD the object to _allObjects of L2World </B></FONT><BR><BR>
     *
     * <B><U> Assert </U> :</B><BR><BR>
     * <li> this instanceof L2ItemInstance</li>
     * <li> _worldRegion == null <I>(L2Object is invisible at the beginning)</I></li><BR><BR>
     *
     * <B><U> Example of use </U> :</B><BR><BR>
     * <li> Drop item</li>
     * <li> Call Pet</li><BR>
     *
     * @param dropper Char that dropped item
     * @param loc     drop coordinates
     */
    public void dropMe(Creature dropper, Location loc) {
        if (dropper != null)
            setReflection(dropper.getReflection());

        spawnMe0(loc, dropper);
        if (isHerb()) {
            ItemsAutoDestroy.INSTANCE.addHerb(this);
        } else if ((Config.AUTODESTROY_ITEM_AFTER > 0) && (!isCursed())) {
            ItemsAutoDestroy.INSTANCE.addItem(this, 100000L);
        }
    }

    public final void pickupMe() {
        decayMe();
        setReflection(ReflectionManager.DEFAULT);
    }

    ItemClass getItemClass() {
        return template.getItemClass();
    }

    public int getDefence(Element element) {
        return isArmor() ? getAttributeElementValue(element, true) : 0;
    }

    /**
     * Возвращает защиту от элемента: вода.
     *
     * @return значение защиты
     */
    public int getDefenceWater() {
        return getDefence(Element.WATER);
    }

    /**
     * Возвращает защиту от элемента: воздух.
     *
     * @return значение защиты
     */
    public int getDefenceWind() {
        return getDefence(Element.WIND);
    }

    /**
     * Возвращает защиту от элемента: земля.
     *
     * @return значение защиты
     */
    public int getDefenceEarth() {
        return getDefence(Element.EARTH);
    }

    /**
     * Возвращает защиту от элемента: свет.
     *
     * @return значение защиты
     */
    public int getDefenceHoly() {
        return getDefence(Element.HOLY);
    }

    /**
     * Возвращает защиту от элемента: тьма.
     *
     * @return значение защиты
     */
    public int getDefenceUnholy() {
        return getDefence(Element.UNHOLY);
    }

    /**
     * Возвращает значение элемента.
     */
    public int getAttributeElementValue(Element element, boolean withBase) {
        return attrs.getValue(element) + (withBase ? template.getBaseAttributeValue(element) : 0);
    }

    public Element getAttributeElement() {
        return attrs.getElement();
    }

    public int getAttributeElementValue() {
        return attrs.getValue();
    }

    public Element getAttackElement() {
        Element element = isWeapon() ? getAttributeElement() : Element.NONE;
        if (element == Element.NONE)
            for (Element e : Element.VALUES)
                if (template.getBaseAttributeValue(e) > 0)
                    return e;
        return element;
    }

    public int getAttackElementValue() {
        return isWeapon() ? getAttributeElementValue(getAttackElement(), true) : 0;
    }

    /**
     * Устанавливает элемент атрибуции предмета.<br>
     * Element (0 - Fire, 1 - Water, 2 - Wind, 3 - Earth, 4 - Holy, 5 - Dark, -1 - None)
     *
     * @param element элемент
     */
    public void setAttributeElement(Element element, int value) {
        attrs.setValue(element, value);
    }

    /**
     * Проверяет, является ли данный инстанс предмета хербом
     *
     * @return true если предмет является хербом
     */
    public boolean isHerb() {
        return getTemplate().isHerb();
    }

    public Grade getCrystalType() {
        return template.getCrystalType();
    }

    @Override
    public String getName() {
        return getTemplate().getName();
    }

    @Override
    public void save() {
        ITEMS_DAO.save(this);
    }

    @Override
    public void update() {
        ITEMS_DAO.update(this);
    }

    @Override
    public void delete() {
        ITEMS_DAO.delete(this);
    }

    @Override
    public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
        L2GameServerPacket packet;
        if (dropper != null)
            packet = new DropItem(this, dropper.objectId());
        else
            packet = new SpawnItem(this);

        return List.of(packet);
    }

    /**
     * Returns the item in String format
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getTemplate().itemId());
        sb.append(" ");
        if (getEnchantLevel() > 0) {
            sb.append("+");
            sb.append(getEnchantLevel());
            sb.append(" ");
        }
        sb.append(getTemplate().getName());
        if (!getTemplate().getAdditionalName().isEmpty()) {
            sb.append(" ");
            sb.append("\\").append(getTemplate().getAdditionalName()).append("\\");
        }
        sb.append(" ");
        sb.append("(");
        sb.append(getCount());
        sb.append(")");
        sb.append("[");
        sb.append(objectId());
        sb.append("]");

        return sb.toString();

    }

    @Override
    public JdbcEntityState getJdbcState() {
        return state;
    }

    @Override
    public void setJdbcState(JdbcEntityState state) {
        this.state = state;
    }


    public ItemAttachment getAttachment() {
        return attachment;
    }

    public void setAttachment(ItemAttachment attachment) {
        this.attachment = attachment;
    }

    public int getAgathionEnergy() {
        return agathionEnergy;
    }

    public void setAgathionEnergy(int agathionEnergy) {
        this.agathionEnergy = agathionEnergy;
    }

    public List<Integer> getEnchantOptions() {
        return enchantOptions;
    }

    public Set<Integer> getDropPlayers() {
        return dropPlayers;
    }

    public double getStatFunc(Stats stat) {
        return template.getAttachedFuncs().stream()
                .filter(func -> func.stat == stat)
                .map(func -> func.value)
                .findFirst().orElse(0.0);
    }

    public int getVisualItemId() {
        return visualItemId;
    }

    public void setVisualItemId(int visualItemId) {
        this.visualItemId = visualItemId;
    }

    public int getAugmentationMineralId() {
        return augmentationId;
    }

    public void setAugmentation(int mineralId, int[] augmentations) {
        augmentationId = mineralId;
        this.augmentations = augmentations;
    }

    public int[] getAugmentations() {
        return augmentations;
    }

    /**
     * Enumeration of locations for item
     */
    public enum ItemLocation {
        VOID,
        INVENTORY,
        PAPERDOLL,
        PET_INVENTORY,
        PET_PAPERDOLL,
        WAREHOUSE,
        CLANWH,
        FREIGHT, //restored used Dimension Manager
        @Deprecated
        LEASE,
        MAIL,
        AUCTION
    }

    private class FuncAttack extends Func {
        private final Element element;

        FuncAttack(Element element, int order, Object owner) {
            super(element.getAttack(), order, owner);
            this.element = element;
        }

        @Override
        public void calc(Env env) {
            env.value += getAttributeElementValue(element, true);
        }
    }

    public class FuncDefence extends Func {
        private final Element element;

        FuncDefence(Element element, int order, Object owner) {
            super(element.getDefence(), order, owner);
            this.element = element;
        }

        @Override
        public void calc(Env env) {
            env.value += getAttributeElementValue(element, true);
        }
    }

}