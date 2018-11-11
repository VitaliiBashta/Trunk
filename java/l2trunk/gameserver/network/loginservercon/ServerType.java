package l2trunk.gameserver.network.loginservercon;

public enum ServerType {
    NORMAL,
    RELAX,
    TEST,
    NO_LABEL,
    RESTRICTED,
    EVENT,
    FREE;

    private final int _mask;

    ServerType() {
        _mask = 1 << ordinal();
    }

    public int getMask() {
        return _mask;
    }
}
