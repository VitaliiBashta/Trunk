package l2trunk.gameserver.model.quest;

import java.util.ArrayList;
import java.util.List;

public final class Drop {
    public final int condition;
    public final int maxcount;
    public final int chance;

    public List<Integer> itemList = new ArrayList<>();

    public Drop(int condition, int maxcount, int chance) {
        this.condition = condition;
        this.maxcount = maxcount;
        this.chance = chance;
    }

    public Drop addItem(int item) {
        itemList.add(item);
        return this;
    }
}