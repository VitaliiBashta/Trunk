package l2trunk.scripts.events.PcCafePointsExchange;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PcCafePointsExchange extends Functions implements ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(PcCafePointsExchange.class);
    private static final String EVENT_NAME = "PcCafePointsExchange";
    private static final List<SimpleSpawner> _spawns = new ArrayList<>();

    /**
     * Спавнит эвент менеджеров
     */
    private void spawnEventManagers() {

    }

    /**
     * Удаляет спавн эвент менеджеров
     */
    private void unSpawnEventManagers() {
        deSpawnNPCs(_spawns);
    }

    private static boolean isActive() {
        return isActive(EVENT_NAME);
    }

    /**
     * Запускает эвент
     */
    public void startEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (setActive(EVENT_NAME, true)) {
            spawnEventManagers();
            System.out.println("Event: 'PcCafePointsExchange' started.");
        } else
            player.sendMessage("Event 'PcCafePointsExchange' already started.");

        show("admin/events/events.htm", player);
    }

    /**
     * Останавливает эвент
     */
    public void stopEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;
        if (setActive(EVENT_NAME, false)) {
            unSpawnEventManagers();
            System.out.println("Event: 'PcCafePointsExchange' stopped.");
        } else
            player.sendMessage("Event: 'PcCafePointsExchange' not started.");

        show("admin/events/events.htm", player);
    }

    @Override
    public void onLoad() {
        if (isActive()) {
            spawnEventManagers();
            _log.info("Loaded Event: PcCafePointsExchange [state: activated]");
        } else
            _log.info("Loaded Event: PcCafePointsExchange [state: deactivated]");
    }

    @Override
    public void onReload() {
        unSpawnEventManagers();
    }

    @Override
    public void onShutdown() {
        unSpawnEventManagers();
    }
}