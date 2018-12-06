package l2trunk.scripts.events.AprilFoolsDay;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.ExBR_BroadcastEventState;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AprilFoolsDay extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener {
    private static final Logger _log = LoggerFactory.getLogger(AprilFoolsDay.class);
    private static final int[] HERBS = new int[]{20923, 20924, 20925}; // Хербы
    private static boolean _active = false;

    private static boolean isActive() {
        return isActive("AprilFoolsDay");
    }

    public void startEvent() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (SetActive("AprilFoolsDay", true)) {
            System.out.println("Event: 'April Fools Day' started.");
            ExBR_BroadcastEventState es = new ExBR_BroadcastEventState(ExBR_BroadcastEventState.APRIL_FOOLS, 1);
            GameObjectsStorage.getAllPlayers().forEach(p -> p.sendPacket(es));
        } else
            player.sendMessage("Event 'April Fools Day' already started.");

        _active = true;
        show("admin/events/events.htm", player);
    }

    /**
     * Останавливает эвент
     */
    public void stopEvent() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm)
            return;
        if (SetActive("AprilFoolsDay", false))
            System.out.println("Event: 'April Fools Day' stopped.");
        else
            player.sendMessage("Event: 'April Fools Day' not started.");

        _active = false;
        show("admin/events/events.htm", player);
    }

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (isActive()) {
            _active = true;
            _log.info("Loaded Event: Apil Fool's Day [state: activated]");
        } else
            _log.info("Loaded Event: Apil Fool's Day [state: deactivated]");
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (_active)
            player.sendPacket(new ExBR_BroadcastEventState(ExBR_BroadcastEventState.APRIL_FOOLS_10, 1));
    }

    /**
     * Обработчик смерти мобов, управляющий эвентовым дропом
     */
    @Override
    public void onDeath(Creature cha, Creature killer) {
        if (_active && SimpleCheckDrop(cha, killer) && Rnd.chance(Config.EVENT_APIL_FOOLS_DROP_CHANCE / 10.0D))
            ((NpcInstance) cha).dropItem(killer.getPlayer(), HERBS[Rnd.get(HERBS.length)], 1);
    }
}