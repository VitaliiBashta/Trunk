package l2trunk.gameserver.utils;

public enum Language {
    ENGLISH("en"),
    RUSSIAN("ru");

    public static final Language[] VALUES = Language.values();

    private final String shortName;

    Language(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }
}
