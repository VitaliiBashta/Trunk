package l2trunk.gameserver.model.quest;


import l2trunk.commons.lang.ArrayUtils;

public class Drop {
    public final int condition;
    public final int maxcount;
    public final int chance;

    public int[] itemList = ArrayUtils.EMPTY_INT_ARRAY;

    public Drop(int condition, int maxcount, int chance) {
        this.condition = condition;
        this.maxcount = maxcount;
        this.chance = chance;
    }

    public Drop addItem(int item) {
        itemList = ArrayUtils.add(itemList, item);
        return this;
    }
}