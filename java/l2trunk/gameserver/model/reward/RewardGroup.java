package l2trunk.gameserver.model.reward;

import l2trunk.commons.math.SafeMath;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.stats.Stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RewardGroup /*implements Cloneable*/ {
    private final List<RewardData> items = new ArrayList<>();
    private double chance;
    private boolean isAdena = false; // Шанс фиксирован, растет только количество
    private boolean notRate = false; // Рейты вообще не применяются
    private double chanceSum;

    public RewardGroup(double chance) {
        this.chance = chance;
    }

    public boolean notRate() {
        return notRate;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public boolean isAdena() {
        return isAdena;
    }

    public void addData(RewardData item) {
        if (item.getItem().isAdena())
            isAdena = true;
        chanceSum += item.getChance();
        item.setChanceInGroup(chanceSum);
        items.add(item);
    }

    /**
     * Возвращает список вещей
     */
    public List<RewardData> getItems() {
        return items;
    }


    /**
     * Функция используется в основном механизме расчета дропа, выбирает одну/несколько вещей из группы, в зависимости от рейтов
     */
    public List<RewardItem> roll(RewardType type, Player player, double mod, boolean isRaid, boolean isSiegeGuard) {
        switch (type) {
            case NOT_RATED_GROUPED:
            case NOT_RATED_NOT_GROUPED:
                return rollItems(mod, 1.0, 1.0);
            case SWEEP:
                return rollItems(mod, Config.RATE_DROP_SPOIL, player.getRateSpoil());
            case RATED_GROUPED:
                if (isAdena) {
                    // Ady - Support for adena multipliers in items and skills. Separated from drop multipliers
                    mod *= player.calcStat(Stats.ADENA_MULTIPLIER, 1., player, null);
                    return rollAdena(mod, Config.RATE_DROP_ADENA, player.getRateAdena());
                }

                if (isRaid)
                    return rollItems(mod, Config.RATE_DROP_RAIDBOSS, 1.0);

                if (isSiegeGuard)
                    return rollItems(mod, Config.RATE_DROP_SIEGE_GUARD, 1.0);

                return rollItems(mod, Config.RATE_DROP_ITEMS, player.getRateItems());
            default:
                return Collections.emptyList();
        }
    }

    private List<RewardItem> rollItems(double mod, double baseRate, double playerRate) {
        if (mod <= 0)
            return Collections.emptyList();

        double rate;
        if (notRate)
            rate = Math.min(mod, 1.0);
        else
            rate = baseRate * playerRate * mod;

        double mult = Math.ceil(rate);

        List<RewardItem> ret = new ArrayList<>((int) (mult * items.size()));
        for (long n = 0; n < mult; n++)
            if (Rnd.get(1, RewardList.MAX_CHANCE) <= chance * Math.min(rate - n, 1.0))
                rollFinal(items, ret, 1., Math.max(chanceSum, RewardList.MAX_CHANCE));
        return ret;
    }

    private List<RewardItem> rollAdena(double mod, double baseRate, double playerRate) {
        double chance = this.chance;
        if (mod > 10) {
            mod *= this.chance / RewardList.MAX_CHANCE;
            chance = RewardList.MAX_CHANCE;
        }

        if (mod <= 0)
            return Collections.emptyList();

        if (Rnd.get(1, RewardList.MAX_CHANCE) > chance)
            return Collections.emptyList();

        double rate = baseRate * playerRate * mod;

        List<RewardItem> ret = new ArrayList<>(items.size());
        rollFinal(items, ret, rate, Math.max(chanceSum, RewardList.MAX_CHANCE));
        for (RewardItem i : ret)
            i.isAdena = true;

        return ret;
    }

    private void rollFinal(List<RewardData> items, List<RewardItem> ret, double mult, double chanceSum) {
        // перебираем все вещи в группе и проверяем шанс
        int chance = Rnd.get(0, (int) chanceSum);
        long count;

        for (RewardData i : items) {
            if (chance < i.getChanceInGroup() && chance > i.getChanceInGroup() - i.getChance()) {
                double imult = i.notRate() ? 1.0 : mult;

                if (i.getMinDrop() >= i.getMaxDrop())
                    count = Math.round(i.getMinDrop() * imult);
                else
                    count = Rnd.get(Math.round(i.getMinDrop() * imult), Math.round(i.getMaxDrop() * imult));

                RewardItem t = ret.stream()
                        .filter(r -> i.getItemId() == r.itemId)
                        .findFirst().orElse(null);

                if (t == null) {
                    ret.add(t = new RewardItem(i.getItemId()));
                    t.count = count;
                } else if (!i.notRate()) {
                    t.count = SafeMath.addAndLimit(t.count, count);
                }

                break;
            }
        }
    }
}