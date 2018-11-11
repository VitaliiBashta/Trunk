package l2trunk.gameserver.templates.augmentation;

import l2trunk.commons.math.random.RndSelector;

public class OptionGroup {
    private final RndSelector<Integer> _options = new RndSelector<>();

    public void addOptionWithChance(int option, int chance) {
        _options.add(option, chance);
    }

    public Integer random() {
        return _options.chance(1000000);
    }
}
