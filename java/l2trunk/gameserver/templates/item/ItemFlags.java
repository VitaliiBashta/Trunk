package l2trunk.gameserver.templates.item;

public enum ItemFlags {
    DESTROYABLE(true),  // возможность уничтожить
    //CRYSTALLIZABLE,// возможность разбить на кристары
    DROPABLE(true),     // возможность дропнуть
    FREIGHTABLE(false),     // возможность передать в рамках аккаунта
    AUGMENTABLE(true),  // возможность аугментировать
    ENCHANTABLE(true),  // возможность заточить
    ATTRIBUTABLE(true), // возможность заточить атрибутом
    SELLABLE(true),     // возможность продать
    TRADEABLE(true),     // возможность передать
    STOREABLE(true);      // возможность положить в ВХ

    public static final ItemFlags[] VALUES = values();

    private final int mask;
    private final boolean defaultValue;

    ItemFlags(boolean defaultValue) {
        this.defaultValue = defaultValue;
        mask = 1 << ordinal();
    }

    public int mask() {
        return mask;
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }
}
