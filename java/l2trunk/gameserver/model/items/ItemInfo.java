package l2trunk.gameserver.model.items;

import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.model.base.Element;
import l2trunk.gameserver.templates.item.ItemTemplate;

public class ItemInfo {
    private int ownerId;
    private int lastChange;
    private int objectId;
    private int itemId;
    private long count;
    private int customType1;
    private boolean isEquipped;
    private int bodyPart;
    private int enchantLevel;
    private int customType2;
    private int augmentationId;
    private int shadowLifeTime;
    private int attackElement = Element.NONE.getId();
    private int attackElementValue;
    private int defenceFire;
    private int defenceWater;
    private int defenceWind;
    private int defenceEarth;
    private int defenceHoly;
    private int defenceUnholy;
    private int equipSlot;
    private int temporalLifeTime;
    private int[] enchantOptions = ItemInstance.EMPTY_ENCHANT_OPTIONS;

    private ItemTemplate item;

    protected ItemInfo() {
    }

    public ItemInfo(ItemInstance item) {
        setOwnerId(item.getOwnerId());
        this.objectId = item.objectId();
        setItemId(item.getItemId());
        this.count = item.getCount();
        this.customType1 = item.getCustomType1();
        this.isEquipped = item.isEquipped();
        this.enchantLevel = item.getEnchantLevel();
        this.customType2 = item.getCustomType2();
        this.augmentationId = item.getAugmentationId();
        this.shadowLifeTime = item.getShadowLifeTime();
        this.attackElement = item.getAttackElement().getId();
        this.attackElementValue = item.getAttackElementValue();
        this.defenceFire = item.getDefenceFire();
        this.defenceWater = item.getDefenceWater();
        this.defenceWind = item.getDefenceWind();
        this.defenceEarth = item.getDefenceEarth();
        this.defenceHoly = item.getDefenceHoly();
        this.defenceUnholy = item.getDefenceUnholy();
        this.equipSlot =  item.getEquipSlot();
        this.temporalLifeTime = item.getTemporalLifeTime();
        this.enchantOptions = item.getEnchantOptions();
    }

    public ItemTemplate getItem() {
        return item;
    }

    public int getOwnerId() {
        return ownerId;
    }

    private void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getLastChange() {
        return lastChange;
    }

    public void setLastChange(int lastChange) {
        this.lastChange = lastChange;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
        if (itemId > 0)
            item = ItemHolder.getTemplate(getItemId());
        else
            item = null;
        if (item != null) {
            this.bodyPart = item.getBodyPart();
        }
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }


    public int getCustomType1() {
        return customType1;
    }

    public boolean isEquipped() {
        return isEquipped;
    }

    protected void setEquipped(boolean isEquipped) {
        this.isEquipped = isEquipped;
    }

    public int getBodyPart() {
        return bodyPart;
    }

    public int getEnchantLevel() {
        return enchantLevel;
    }

    protected void setEnchantLevel(int enchantLevel) {
        this.enchantLevel = enchantLevel;
    }

    public int getAugmentationId() {
        return augmentationId;
    }

    protected void setAugmentationId(int augmentationId) {
        this.augmentationId = augmentationId;
    }

    public int getShadowLifeTime() {
        return shadowLifeTime;
    }

    public int getCustomType2() {
        return customType2;
    }

    public int getAttackElement() {
        return attackElement;
    }

    public int getAttackElementValue() {
        return attackElementValue;
    }

    public int getDefenceFire() {
        return defenceFire;
    }

    public int getDefenceWater() {
        return defenceWater;
    }

    public int getDefenceWind() {
        return defenceWind;
    }

    public int getDefenceEarth() {
        return defenceEarth;
    }

    public int getDefenceHoly() {
        return defenceHoly;
    }

    public int getDefenceUnholy() {
        return defenceUnholy;
    }

    public int getEquipSlot() {
        return equipSlot;
    }

    public int getTemporalLifeTime() {
        return temporalLifeTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        if (getObjectId() == 0)
            return getItemId() == ((ItemInfo) obj).getItemId();
        return getObjectId() == ((ItemInfo) obj).getObjectId();
    }

    public int[] getEnchantOptions() {
        return enchantOptions;
    }

}
