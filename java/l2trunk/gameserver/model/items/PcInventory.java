package l2trunk.gameserver.model.items;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.data.xml.holder.DressArmorHolder;
import l2trunk.gameserver.instancemanager.CursedWeaponsManager;
import l2trunk.gameserver.model.DressArmorData;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance.ItemLocation;
import l2trunk.gameserver.model.items.listeners.*;
import l2trunk.gameserver.network.serverpackets.ExBR_AgathionEnergyInfo;
import l2trunk.gameserver.network.serverpackets.InventoryUpdate;
import l2trunk.gameserver.network.serverpackets.Say2;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.utils.ItemFunctions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class PcInventory extends Inventory {
    private static final int[][] arrows = {
            //
            {17}, // NG
            {1341, 22067}, // D
            {1342, 22068}, // C
            {1343, 22069}, // B
            {1344, 22070}, // A
            {1345, 22071}, // S
    };
    private static final int[][] bolts = {
            //
            {9632}, // NG
            {9633, 22144}, // D
            {9634, 22145}, // C
            {9635, 22146}, // B
            {9636, 22147}, // A
            {9637, 22148}, // S
    };
    private final Player owner;
    /**
     * FIXME Hack to update skills to equip when changing subclass
     */
    public boolean isRefresh = false;
    // locks
    private LockType _lockType = LockType.NONE;
    private List<Integer> _lockItems = Collections.emptyList();
    // Alexander - Vars to check when visual ids for items of dressme must be used. Only when the set is complete
    private boolean mustShowDressMe = false;

    public PcInventory(Player owner) {
        super(owner.objectId());
        this.owner = owner;

        addListener(ItemSkillsListener.getInstance());
        addListener(ItemAugmentationListener.getInstance());
        addListener(ItemEnchantOptionsListener.getInstance());
        addListener(ArmorSetListener.getInstance());
        addListener(BowListener.getInstance());
        addListener(AccessoryListener.getInstance());
    }

    @Override
    public Player getActor() {
        return owner;
    }

    @Override
    protected ItemLocation getBaseLocation() {
        return ItemLocation.INVENTORY;
    }

    @Override
    protected ItemLocation getEquipLocation() {
        return ItemLocation.PAPERDOLL;
    }

    public long getAdena() {
        ItemInstance adena = getItemByItemId(57);
        if (adena == null) {
            return 0;
        }
        return adena.getCount();
    }

    public void addAdena(long amount, String log) {
        addItem(ItemTemplate.ITEM_ID_ADENA, amount, log);
    }

    public boolean reduceAdena(long adena, String log) {
        return destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, adena, log);
    }

    public int getPaperdollAugmentationId(int slot) {
        ItemInstance item = paperdoll.get(slot);
        if ((item != null) && item.isAugmented()) {
            return item.getAugmentationId();
        }
        return 0;
    }

    @Override
    public int getPaperdollItemId(int slot) {
        Player player = getActor();

        int itemId = super.getPaperdollItemId(slot);

        if ((slot == PAPERDOLL_RHAND) && (itemId == 0) && player.isClanAirShipDriver()) {
            itemId = 13556; // Затычка на отображение штурвала - Airship Helm
        }

        return itemId;
    }

    @Override
    public int getPaperdollVisualItemId(int slot) {
        if (slot == PAPERDOLL_RHAND && getActor().isClanAirShipDriver())
            return 13556; // Airship Helm

        ItemInstance item = getPaperdollItem(slot);
        if (item != null) {
            // Alexander - Support for visual dressme
            switch (slot) {
                case PAPERDOLL_CHEST:
                case PAPERDOLL_LEGS:
                case PAPERDOLL_GLOVES:
                case PAPERDOLL_FEET: {
                    if (mustShowDressMe()) {
                        int visualItemId = item.getVisualItemId();

                        if (visualItemId == -1)
                            return 0;
                        if (visualItemId != 0)
                            return visualItemId;
                    }
                    break;
                }
                default: {
                    int visualItemId = item.getVisualItemId();

                    if (visualItemId == -1)
                        return 0;
                    if (visualItemId != 0)
                        return visualItemId;
                    break;
                }

            }

            return item.getItemId();
        } else if (slot == PAPERDOLL_HAIR) {
            item = paperdoll.get(PAPERDOLL_DHAIR);
            if (item != null)
                return item.getItemId();
        }

        return 0;
    }

    @Override
    protected void onRefreshWeight() {
        // notify char for overload checking
        getActor().refreshOverloaded();
    }

    /**
     * Функция для валидации вещей в инвентаре. Снимает все вещи, которые нельзя носить. Применяется при входе в игру, смене саба, захвате замка, выходе из клана.
     */
    public void validateItems() {
        for (ItemInstance item : paperdoll) {
            if ((item != null) && ((ItemFunctions.checkIfCanEquip(getActor(), item) != null) || !item.getTemplate().testCondition(getActor(), item))) {
                unEquipItem(item);
                getActor().sendDisarmMessage(item);
            }
        }
    }

    /**
     * FIXME [VISTALL] for skills is critical to always delete them and add, for no triggers
     */
    public void validateItemsSkills() {
        for (ItemInstance item : paperdoll) {
            if ((item == null) || (item.getTemplate().getType2() != ItemTemplate.TYPE2_WEAPON)) {
                continue;
            }

            boolean needUnequipSkills = getActor().getWeaponsExpertisePenalty() > 0;

            if (item.getTemplate().getAttachedSkills().size() > 0) {
                boolean has = getActor().getSkillLevel(item.getTemplate().getAttachedSkills().get(0).id) > 0;
                if (needUnequipSkills && has) {
                    ItemSkillsListener.getInstance().onUnequip(item.getEquipSlot(), item, getActor());
                } else if (!needUnequipSkills && !has) {
                    ItemSkillsListener.getInstance().onEquip(item.getEquipSlot(), item, getActor());
                }
            } else if (item.getTemplate().getEnchant4Skill() != null) {
                boolean has = getActor().getSkillLevel(item.getTemplate().getEnchant4Skill().id) > 0;
                if (needUnequipSkills && has) {
                    ItemSkillsListener.getInstance().onUnequip(item.getEquipSlot(), item, getActor());
                } else if (!needUnequipSkills && !has) {
                    ItemSkillsListener.getInstance().onEquip(item.getEquipSlot(), item, getActor());
                }
            } else if (!item.getTemplate().getTriggerList().isEmpty()) {
                if (needUnequipSkills) {
                    ItemSkillsListener.getInstance().onUnequip(item.getEquipSlot(), item, getActor());
                } else {
                    ItemSkillsListener.getInstance().onEquip(item.getEquipSlot(), item, getActor());
                }
            }
        }
    }

    public void refreshEquip() {
        isRefresh = true;
        for (ItemInstance item : getItems()) {
            if (item.isEquipped()) {
                int slot = item.getEquipSlot();
                listeners.onUnequip(slot, item);
                listeners.onEquip(slot, item);
            } else if (item.getItemType() == EtcItemType.RUNE) {
                listeners.onUnequip(-1, item);
                listeners.onEquip(-1, item);
            }
        }
        isRefresh = false;
    }

    /**
     * Вызывается из RequestSaveInventoryOrder
     */
    public void sort(int[][] order) {
        boolean needSort = false;
        for (int[] element : order) {
            ItemInstance item = getItemByObjectId(element[0]);
            if (item == null) {
                continue;
            }
            if (item.getLocation() != ItemLocation.INVENTORY) {
                continue;
            }
            if (item.getLocData() == element[1]) {
                continue;
            }
            item.setLocData(element[1]);
            item.setJdbcState(JdbcEntityState.UPDATED); // lazy update
            needSort = true;
        }
        if (needSort) {
            items.sort(ItemOrderComparator.getInstance());
        }
    }

    public ItemInstance findArrowForBow(ItemTemplate bow) {
        int[] arrowsId = arrows[bow.getCrystalType().externalOrdinal];
        ItemInstance ret;
        for (int id : arrowsId) {
            if ((ret = getItemByItemId(id)) != null) {
                return ret;
            }
        }
        return null;
    }

    public ItemInstance findArrowForCrossbow(ItemTemplate xbow) {
        int[] boltsId = bolts[xbow.getCrystalType().externalOrdinal];
        ItemInstance ret;
        for (int id : boltsId) {
            if ((ret = getItemByItemId(id)) != null) {
                return ret;
            }
        }
        return null;
    }

    public ItemInstance findEquippedLure() {
        ItemInstance res = null;
        int last_lure = owner.getVarInt("LastLure");

        for (ItemInstance temp : getItems()) {
            if (temp.getItemType() == EtcItemType.BAIT) {
                if ((temp.getLocation() == ItemLocation.PAPERDOLL) && (temp.getEquipSlot() == PAPERDOLL_LHAND)) {
                    return temp;
                } else if ((last_lure > 0) && (res == null) && (temp.objectId() == last_lure)) {
                    res = temp;
                }
            }
        }
        return res;
    }

    public void lockItems(LockType lock, List<Integer> items) {
        if (_lockType != LockType.NONE) {
            return;
        }

        _lockType = lock;
        _lockItems = items;

        getActor().sendItemList(false);
    }

    public void unlock() {
        if (_lockType == LockType.NONE) {
            return;
        }

        _lockType = LockType.NONE;
        _lockItems = Collections.emptyList();

        getActor().sendItemList(false);
    }

    public boolean isLockedItem(ItemInstance item) {
        switch (_lockType) {
            case INCLUDE:
                return _lockItems.contains(item.getItemId());
            case EXCLUDE:
                return !_lockItems.contains(item.getItemId());
            default:
                return false;
        }
    }

    public LockType getLockType() {
        return _lockType;
    }

    public List<Integer> getLockItems() {
        return _lockItems;
    }

    @Override
    protected void onRestoreItem(ItemInstance item) {
        super.onRestoreItem(item);

        if (item.getItemType() == EtcItemType.RUNE) {
            listeners.onEquip(-1, item);
        }

        if (item.isTemporalItem()) {
            item.startTimer(new LifeTimeTask(item));
        }

        if (item.isCursed()) {
            CursedWeaponsManager.INSTANCE.checkPlayer(getActor(), item);
        }
    }

    @Override
    protected void onAddItem(ItemInstance item) {
        super.onAddItem(item);

        if (item.getItemType() == EtcItemType.RUNE) {
            listeners.onEquip(-1, item);
        }

        if (item.isTemporalItem()) {
            item.startTimer(new LifeTimeTask(item));
        }

        if (item.isCursed()) {
            CursedWeaponsManager.INSTANCE.checkPlayer(getActor(), item);
        }
    }

    @Override
    protected void onRemoveItem(ItemInstance item) {
        super.onRemoveItem(item);

        getActor().removeItemFromShortCut(item.objectId());

        if (item.getItemType() == EtcItemType.RUNE) {
            listeners.onUnequip(-1, item);
        }

        if (item.isTemporalItem()) {
            item.stopTimer();
        }
    }

    @Override
    protected void onEquip(int slot, ItemInstance item) {
        super.onEquip(slot, item);

        if (item.isShadowItem()) {
            item.startTimer(new ShadowLifeTimeTask(item));
        }
    }

    @Override
    protected void onUnequip(int slot, ItemInstance item) {
        super.onUnequip(slot, item);

        if (item.isShadowItem()) {
            item.stopTimer();
        }
    }

    @Override
    public void restore() {
        writeLock();
        try {

            ITEMS_DAO.getItemsByOwnerIdAndLoc(ownerId, getBaseLocation()).forEach(item -> {
                this.items.add(item);
                onRestoreItem(item);
            });
            this.items.sort(ItemOrderComparator.getInstance());

            ITEMS_DAO.getItemsByOwnerIdAndLoc(ownerId, getEquipLocation()).forEach(item -> {
                this.items.add(item);
                onRestoreItem(item);
                if (item.getEquipSlot() >= PAPERDOLL_MAX) {
                    // Invalid slot - item returned to inventory.
                    item.setLocation(getBaseLocation());
                    item.setLocData(0); // A bit ugly, but all the equipment is not loaded and can not find a free slot
                    item.setEquipped(false);
                } else
                    setPaperdollItem(item.getEquipSlot(), item);
            });
        } finally {
            writeUnlock();
        }

        refreshWeight();
    }

    @Override
    public void store() {
        writeLock();
        try {
            ITEMS_DAO.update(items);
        } finally {
            writeUnlock();
        }
    }

    @Override
    protected void sendAddItem(ItemInstance item) {
        Player actor = getActor();

        actor.sendPacket(new InventoryUpdate().addNewItem(item));
        if (item.getTemplate().getAgathionEnergy() > 0) {
            actor.sendPacket(new ExBR_AgathionEnergyInfo(1, List.of(item)));
        }
    }

    @Override
    protected void sendModifyItem(ItemInstance item) {
        Player actor = getActor();

        actor.sendPacket(new InventoryUpdate().addModifiedItem(item));
        if (item.getTemplate().getAgathionEnergy() > 0) {
            actor.sendPacket(new ExBR_AgathionEnergyInfo(1, List.of(item)));
        }
    }

    @Override
    protected void sendRemoveItem(ItemInstance item) {
        getActor().sendPacket(new InventoryUpdate().addRemovedItem(item));
    }

    public boolean destroyItem(ItemInstance item, long count, String log) {
        return destroyItem(item, count, owner.toString(), log);
    }

    public boolean destroyItem(ItemInstance item, String log) {
         return destroyItem(item, owner.toString(), log);
    }

    public boolean destroyItemByItemId(int itemId, String log) {
        return destroyItemByItemId(itemId, 1, log);
    }

    public boolean destroyItemByItemId(int itemId, long count, String log) {
        return destroyItemByItemId(itemId, count, owner.toString(), log);
    }

    public boolean destroyItemByObjectId(int objectId, long count, String log) {
        return destroyItemByObjectId(objectId, count, owner.toString(), log);
    }

    public void addItem(ItemInstance item, String log) {
         addItem(item, owner.toString(), log);
    }

    public ItemInstance addItem(int itemId, long count, String log) {
        return addItem(itemId, count, owner.toString(), log);
    }

    public void removeItem(ItemInstance item, long count, String log) {
        removeItem(item, count, owner.toString(), log);
    }

    public ItemInstance removeItem(ItemInstance item, String log) {
        return removeItem(item, owner.toString(), log);
    }

    public ItemInstance removeItemByItemId(int itemId, long count, String log) {
        return removeItemByItemId(itemId, count, owner.toString(), log);
    }

    public ItemInstance removeItemByObjectId(int objectId, long count, String log) {
        return removeItemByObjectId(objectId, count, owner.toString(), log);
    }

    public void stopAllTimers() {
        getItems().stream()
                .filter(item -> (item.isShadowItem() || item.isTemporalItem()))
                .forEach(ItemInstance::stopTimer);
    }

    @Override
    protected void onDestroyItem(ItemInstance item) {
        // Alexander - If one item of the set for the dress me system is destroyed, then we have to disolve the complete set to avoid problems
        if (item.getVisualItemId() > 0) {
            DressArmorData dress = DressArmorHolder.getArmorByPartId(item.getVisualItemId());
            if (dress != null) {
                getItems().stream()
                        .filter(i -> i.objectId != item.objectId())
                        .filter(i -> i.getVisualItemId() > 0)
                        .filter(i -> dress.getVisualIds().contains(i.getVisualItemId()))
                        .forEach(i -> {
                            i.setVisualItemId(0);
                            i.setJdbcState(JdbcEntityState.UPDATED);
                            i.update();
                        });

                // Refund the price paid for this set so he can pay for it again
                ItemFunctions.addItem(owner, dress.priceId, dress.priceCount, "DressMeRefund");

                // Send message
                owner.sendPacket(new Say2(owner.objectId(), ChatType.CRITICAL_ANNOUNCE, "DressMe", "You have destroyed a part of a dressMe set, for that you will be refunded with the original price, so you can make it again"));
            }
        }

        super.onDestroyItem(item);
    }

    public void setMustShowDressMe(boolean val) {
        mustShowDressMe = val;
    }

    public boolean mustShowDressMe() {
        return mustShowDressMe;
    }

    public boolean hasAllDressMeItemsEquipped() {
        return List.of(PAPERDOLL_CHEST, PAPERDOLL_LEGS, PAPERDOLL_GLOVES, PAPERDOLL_FEET).stream()
                .map(this::getPaperdollItem)
                .filter(Objects::nonNull)
                .filter(i -> i.getVisualItemId() != 0)
                .count() == 4;
    }

    protected class ShadowLifeTimeTask extends RunnableImpl {
        private final ItemInstance item;

        ShadowLifeTimeTask(ItemInstance item) {
            this.item = item;
        }

        @Override
        public void runImpl() {
            Player player = getActor();

            if (!item.isEquipped()) {
                return;
            }

            int mana;
            synchronized (item) {
                item.setLifeTime(item.getLifeTime() - 1);
                mana = item.getShadowLifeTime();
                if (mana <= 0) {
                    destroyItem(item, "Shadow Life Time End");
                }
            }

            SystemMessage sm = null;
            if (mana == 10) {
                sm = new SystemMessage(SystemMessage.S1S_REMAINING_MANA_IS_NOW_10);
            } else if (mana == 5) {
                sm = new SystemMessage(SystemMessage.S1S_REMAINING_MANA_IS_NOW_5);
            } else if (mana == 1) {
                sm = new SystemMessage(SystemMessage.S1S_REMAINING_MANA_IS_NOW_1_IT_WILL_DISAPPEAR_SOON);
            } else if (mana <= 0) {
                sm = new SystemMessage(SystemMessage.S1S_REMAINING_MANA_IS_NOW_0_AND_THE_ITEM_HAS_DISAPPEARED);
            } else {
                player.sendPacket(new InventoryUpdate().addModifiedItem(item));
            }

            if (sm != null) {
                sm.addItemName(item.getItemId());
                player.sendPacket(sm);
            }
        }
    }

    private class LifeTimeTask extends RunnableImpl {
        private final ItemInstance item;

        LifeTimeTask(ItemInstance item) {
            this.item = item;
        }

        @Override
        public void runImpl() {
            Player player = getActor();

            int left;
            synchronized (item) {
                left = item.getTemporalLifeTime();
                if (left <= 0) {
                    destroyItem(item, "Life Time End");
                }
            }

            if (left <= 0) {
                player.sendPacket(new SystemMessage(SystemMessage.THE_LIMITED_TIME_ITEM_HAS_BEEN_DELETED).addItemName(item.getItemId()));
            }
        }
    }
}