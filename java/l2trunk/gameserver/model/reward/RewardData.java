package l2trunk.gameserver.model.reward;

import l2trunk.commons.math.SafeMath;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.templates.item.ItemTemplate;

import java.util.ArrayList;
import java.util.List;

public final class RewardData implements Cloneable {
    private final ItemTemplate _item;
    private boolean notRate = false; // Рейты к вещи не применяются

    private long mindrop;
    private long maxdrop;
    private double chance;
    private double _chanceInGroup;

    public RewardData(int itemId) {
        _item = ItemHolder.getTemplate(itemId);
        if (_item.isArrow() // стрелы не рейтуются
                || (Config.NO_RATE_EQUIPMENT && _item.isEquipment()) // отключаемая рейтовка эквипа
                || (Config.NO_RATE_KEY_MATERIAL && _item.isKeyMatherial()) // отключаемая рейтовка ключевых материалов
                || (Config.NO_RATE_RECIPES && _item.isRecipe()) // отключаемая рейтовка рецептов
                || (Config.NO_RATE_ITEMS.contains(itemId))) // индивидаульная отключаемая рейтовка для списка предметов
            notRate = true;
    }

    public RewardData(int itemId, long min, long max, double chance) {
        this(itemId);
        mindrop = min;
        maxdrop = max;
        this.chance = chance;
    }

    public boolean notRate() {
        return notRate;
    }

    public int getItemId() {
        return _item.getItemId();
    }

    public ItemTemplate getItem() {
        return _item;
    }

    public long getMinDrop() {
        return mindrop;
    }

    public void setMinDrop(long mindrop) {
        this.mindrop = mindrop;
    }

    public long getMaxDrop() {
        return maxdrop;
    }

    public void setMaxDrop(long maxdrop) {
        this.maxdrop = maxdrop;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public double getChanceInGroup() {
        return _chanceInGroup;
    }

    public void setChanceInGroup(double chance) {
        _chanceInGroup = chance;
    }

    @Override
    public String toString() {
        return "ItemID: " + getItem() + " Min: " + getMinDrop() + " Max: " + getMaxDrop() + " Chance: " + getChance() / 10000.0 + "%";
    }

    @Override
    public RewardData clone() {
        return new RewardData(getItemId(), getMinDrop(), getMaxDrop(), getChance());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RewardData) {
            RewardData drop = (RewardData) o;
            return drop.getItemId() == getItemId();
        }
        return false;
    }

    /**
     * Подсчет шанса выпадения этой конкретной вещи
     * Используется в эвентах и некоторых специальных механизмах
     *
     * @param player игрок (его бонус влияет на шанс)
     * @param mod    (просто множитель шанса)
     * @return информация о выпавшей вещи
     */
    public List<RewardItem> roll(Player player, double mod) {
        double rate = 1.0;
        if (_item.isAdena())
            rate = Config.RATE_DROP_ADENA * player.getRateAdena();
        else
            rate = Config.RATE_DROP_ITEMS * (player != null ? player.getRateItems() : 1.);

        return roll(rate * mod);
    }

    /**
     * Подсчет шанса выпадения этой конкретной вещи
     * Используется в эвентах и некоторых специальных механизмах
     *
     * @param rate множитель количества
     * @return информация о выпавшей вещи
     */
    private List<RewardItem> roll(double rate) {
        double mult = Math.ceil(rate);

        List<RewardItem> ret = new ArrayList<>(1);
        RewardItem t = null;
        long count;
        for (int n = 0; n < mult; n++) {
            if (Rnd.get(RewardList.MAX_CHANCE) <= chance * Math.min(rate - n, 1.0)) {
                if (getMinDrop() >= getMaxDrop())
                    count = getMinDrop();
                else
                    count = Rnd.get(getMinDrop(), getMaxDrop());

                if (t == null) {
                    ret.add(t = new RewardItem(_item.getItemId()));
                    t.count = count;
                } else
                    t.count = SafeMath.addAndLimit(t.count, count);
            }
        }

        return ret;
    }
}