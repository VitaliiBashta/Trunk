package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.base.Element;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.utils.ItemFunctions;

public class ExChooseInventoryAttributeItem extends L2GameServerPacket {
    private final int _itemId;
    private final boolean _disableFire;
    private final boolean _disableWater;
    private final boolean _disableEarth;
    private final boolean _disableWind;
    private final boolean _disableDark;
    private final boolean _disableHoly;
    private final int _stoneLvl;

    public ExChooseInventoryAttributeItem(ItemInstance item) {
        _itemId = item.getItemId();
        _disableFire = ItemFunctions.getEnchantAttributeStoneElement(item.getItemId(), false) == Element.FIRE;
        _disableWater = ItemFunctions.getEnchantAttributeStoneElement(item.getItemId(), false) == Element.WATER;
        _disableWind = ItemFunctions.getEnchantAttributeStoneElement(item.getItemId(), false) == Element.WIND;
        _disableEarth = ItemFunctions.getEnchantAttributeStoneElement(item.getItemId(), false) == Element.EARTH;
        _disableHoly = ItemFunctions.getEnchantAttributeStoneElement(item.getItemId(), false) == Element.HOLY;
        _disableDark = ItemFunctions.getEnchantAttributeStoneElement(item.getItemId(), false) == Element.UNHOLY;
        _stoneLvl = item.getTemplate().isAttributeCrystal() ? 6 : 3;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x62);
        writeD(_itemId);
        writeD(_disableFire ? 1 : 0);  //fire
        writeD(_disableWater ? 1 : 0); // water
        writeD(_disableWind ? 1 : 0);  //wind
        writeD(_disableEarth ? 1 : 0);  //earth
        writeD(_disableHoly ? 1 : 0); //holy
        writeD(_disableDark ? 1 : 0); //dark
        writeD(_stoneLvl); //max enchant lvl
    }
}