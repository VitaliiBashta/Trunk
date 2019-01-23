package l2trunk.gameserver.model.reward;

import l2trunk.gameserver.model.Player;

import java.util.ArrayList;
import java.util.List;

public final class RewardList extends ArrayList<RewardGroup> {
    public static final int MAX_CHANCE = 1000000;
    private final RewardType type;

    public RewardList(RewardType rewardType) {
        super(5);
        type = rewardType;
    }

    public List<RewardItem> roll(Player player) {
        return roll(player, 1.0, false, false);
    }

    public List<RewardItem> roll(Player player, double mod) {
        return roll(player, mod, false, false);
    }

    public List<RewardItem> roll(Player player, double mod, boolean isRaid) {
        return roll(player, mod, isRaid, false);
    }

    public List<RewardItem> roll(Player player, double mod, boolean isRaid, boolean isSiegeGuard) {
        List<RewardItem> temp = new ArrayList<>(size());
        for (RewardGroup g : this) {
            List<RewardItem> tdl = g.roll(type, player, mod, isRaid, isSiegeGuard);
            if (!tdl.isEmpty())
                temp.addAll(tdl);
        }
        return temp;
    }

    public boolean validate() {
        for (RewardGroup g : this) {
            double chanceSum = 0; // сумма шансов группы
            for (RewardData d : g.getItems())
                chanceSum += d.getChance();
            if (chanceSum <= MAX_CHANCE) // всё в порядке?
                return true;
            double mod = MAX_CHANCE / chanceSum;
            for (RewardData d : g.getItems()) {
                double chance = d.getChance() * mod; // коррекция шанса группы
                d.setChance(chance);
                g.setChance(MAX_CHANCE);
            }
        }
        return false;
    }

    public RewardType getType() {
        return type;
    }
}