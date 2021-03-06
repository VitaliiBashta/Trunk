package l2trunk.gameserver.utils;

import l2trunk.gameserver.model.Effect;

import java.io.Serializable;
import java.util.Comparator;

public final class EffectsComparator implements Comparator<Effect>, Serializable {
    private static final EffectsComparator instance = new EffectsComparator();

    public static EffectsComparator getInstance() {
        return instance;
    }

    private static int compareStartTime(Effect o1, Effect o2) {
        return Long.compare(o1.getStartTime(), o2.getStartTime());
    }

    @Override
    public int compare(Effect o1, Effect o2) {
        boolean toggle1 = o1.skill.isToggle();
        boolean toggle2 = o2.skill.isToggle();

        if (toggle1 && toggle2)
            return compareStartTime(o1, o2);

        if (toggle1 || toggle2)
            if (toggle1)
                return 1;
            else
                return -1;

        boolean music1 = o1.skill.isMusic();
        boolean music2 = o2.skill.isMusic();

        if (music1 && music2)
            return compareStartTime(o1, o2);

        if (music1 || music2)
            if (music1)
                return 1;
            else
                return -1;

        boolean offensive1 = o1.isOffensive();
        boolean offensive2 = o2.isOffensive();

        if (offensive1 && offensive2)
            return compareStartTime(o1, o2);

        if (offensive1 || offensive2)
            if (!offensive1)
                return 1;
            else
                return -1;

        boolean trigger1 = o1.skill.isTrigger;
        boolean trigger2 = o2.skill.isTrigger;

        if (trigger1 && trigger2)
            return compareStartTime(o1, o2);

        if (trigger1 || trigger2)
            if (trigger1)
                return 1;
            else
                return -1;

        return compareStartTime(o1, o2);
    }
}