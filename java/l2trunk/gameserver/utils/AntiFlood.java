package l2trunk.gameserver.utils;


import java.util.HashMap;
import java.util.Map;

public final class AntiFlood {
    private final Map<Integer, Long> _recentReceivers = new HashMap<>();
    private String _lastText = "";

    private long _lastHeroTime;
    private long _lastTradeTime;
    private long _lastShoutTime;

    private long _lastMailTime;

    public boolean canTrade(String text) {
        long currentMillis = System.currentTimeMillis();

        if (currentMillis - _lastTradeTime < 5000L)
            return false;

        _lastTradeTime = currentMillis;
        return true;
    }

    public boolean canShout(String text) {
        long currentMillis = System.currentTimeMillis();

        if (currentMillis - _lastShoutTime < 5000L)
            return false;

        _lastShoutTime = currentMillis;
        return true;
    }

    public boolean canHero(String text) {
        long currentMillis = System.currentTimeMillis();

        if (currentMillis - _lastHeroTime < 10000L)
            return false;

        _lastHeroTime = currentMillis;
        return true;
    }

    public boolean canMail() {
        long currentMillis = System.currentTimeMillis();

        if (currentMillis - _lastMailTime < 10000L)
            return false;

        _lastMailTime = currentMillis;
        return true;
    }

    public boolean canTell(int charId, String text) {
        long currentMillis = System.currentTimeMillis();
        _recentReceivers.entrySet().removeIf(a -> currentMillis - a.getValue() >= (text.equalsIgnoreCase(_lastText) ? 600000L : 60000L));

        Long lastSent = _recentReceivers.put(charId, currentMillis);
        long delay = 333L;

        _lastText = text;
        if (lastSent == null) return true;
        else return currentMillis - lastSent > delay;
    }
}
