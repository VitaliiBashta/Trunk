package l2trunk.gameserver.handler.bbs;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class CommunityBoardManager {
    private CommunityBoardManager() {
    }

    private static final Logger _log = LoggerFactory.getLogger(CommunityBoardManager.class);

    private static final Map<String, ICommunityBoardHandler> _handlers = new HashMap<>();
    private static final StatsSet properties = new StatsSet();


    public static void registerHandler(ICommunityBoardHandler commHandler) {
        for (String bypass : commHandler.getBypassCommands()) {
            if (_handlers.containsKey(bypass))
                _log.warn("CommunityBoard: dublicate bypass registered! First handler: " + _handlers.get(bypass).getClass().getSimpleName() + " second: " + commHandler.getClass().getSimpleName());

            _handlers.put(bypass, commHandler);
        }
    }

    public static void removeHandler(ICommunityBoardHandler handler) {
        for (String bypass : handler.getBypassCommands())
            _handlers.remove(bypass);
        _log.info("CommunityBoard: " + handler.getClass().getSimpleName() + " unloaded.");
    }

    public static ICommunityBoardHandler getCommunityHandler(String bypass) {
        if (!Config.COMMUNITYBOARD_ENABLED || _handlers.isEmpty())
            return null;

        for (Map.Entry<String, ICommunityBoardHandler> entry : _handlers.entrySet())
            if (bypass.contains(entry.getKey()))
                return entry.getValue();

        return null;
    }

    public void setProperty(String name, String val) {
        properties.set(name, val);
    }

    public void setProperty(String name, int val) {
        properties.set(name, val);
    }

    public static int getIntProperty(String name) {
        return properties.getInteger(name);
    }
}
