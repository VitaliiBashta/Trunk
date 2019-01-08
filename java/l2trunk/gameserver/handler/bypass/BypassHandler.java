package l2trunk.gameserver.handler.bypass;

public final class BypassHandler {
    private static final BypassHandler _instance = new BypassHandler();

    public static BypassHandler getInstance() {
        return _instance;
    }

    public void registerBypass(IBypassHandler bypass) {

    }
}
