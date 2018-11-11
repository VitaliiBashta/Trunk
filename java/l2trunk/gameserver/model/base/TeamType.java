package l2trunk.gameserver.model.base;

public enum TeamType {
    NONE,
    BLUE,
    RED;

    public TeamType revert() {
        return this == BLUE ? RED : this == RED ? BLUE : NONE;
    }
}
