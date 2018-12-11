package l2trunk.gameserver.model.base;

import l2trunk.gameserver.stats.Stats;

import java.util.Arrays;


public enum Element {
    FIRE(0, Stats.ATTACK_FIRE, Stats.DEFENCE_FIRE),
    WATER(1, Stats.ATTACK_WATER, Stats.DEFENCE_WATER),
    WIND(2, Stats.ATTACK_WIND, Stats.DEFENCE_WIND),
    EARTH(3, Stats.ATTACK_EARTH, Stats.DEFENCE_EARTH),
    HOLY(4, Stats.ATTACK_HOLY, Stats.DEFENCE_HOLY),
    UNHOLY(5, Stats.ATTACK_UNHOLY, Stats.DEFENCE_UNHOLY),
    NONE(-2, null, null);

    /**
     * Массив элементов без NONE
     **/
    public final static Element[] VALUES = Arrays.copyOf(values(), 6);

    private final int id;
    private final Stats attack;
    private final Stats defence;

    Element(int id, Stats attack, Stats defence) {
        this.id = id;
        this.attack = attack;
        this.defence = defence;
    }



    public static Element getReverseElement(Element element) {
        switch (element) {
            case WATER:
                return FIRE;
            case FIRE:
                return WATER;
            case WIND:
                return EARTH;
            case EARTH:
                return WIND;
            case HOLY:
                return UNHOLY;
            case UNHOLY:
                return HOLY;
        }

        return NONE;
    }

    public static Element getElement(String name) {
        try {
            int id = Integer.parseInt(name);
            return getElementById(id);
        } catch (NumberFormatException e) {
            return getElementByName(name);
        }

    }

    public static Element getElementById(int id) {
        return Arrays.stream(VALUES)
                .filter(e -> e.getId() == id)
                .findFirst().orElse(NONE);
    }

    public static Element getElementByName(String name) {
        return Arrays.stream(VALUES)
                .filter(e -> (e.name().equalsIgnoreCase(name)))
                .findFirst().orElse(NONE);
    }

    public int getId() {
        return id;
    }

    public Stats getAttack() {
        return attack;
    }

    public Stats getDefence() {
        return defence;
    }
}
