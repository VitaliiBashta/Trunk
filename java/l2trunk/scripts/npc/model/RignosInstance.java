package l2trunk.scripts.npc.model;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ItemFunctions;

import java.util.concurrent.Future;

public final class RignosInstance extends NpcInstance {
    private static final int RACE_STAMP = 10013;
    private static final int SECRET_KEY = 9694;
    private final static int SKILL_EVENT_TIMER = 5239;
    private Future<?> _raceTask;

    public RignosInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.equalsIgnoreCase("startRace")) {
            if (_raceTask != null)
                return;

            altUseSkill(SKILL_EVENT_TIMER,5, player);
            ItemFunctions.removeItem(player, RACE_STAMP, player.inventory.getCountOf(RACE_STAMP), "RignosInstance");
            _raceTask = ThreadPoolManager.INSTANCE.schedule(() -> _raceTask = null, 30 * 60 * 1000L);
        } else if (command.equalsIgnoreCase("endRace")) {
            if (_raceTask == null)
                return;

            long count = player.inventory.getCountOf(RACE_STAMP);
            if (count >= 4) {
                ItemFunctions.removeItem(player, RACE_STAMP, count, "RignosInstance");
                ItemFunctions.addItem(player, SECRET_KEY, 1, "RignosInstance");
                player.getEffectList().stopEffect(SKILL_EVENT_TIMER);
                _raceTask.cancel(false);
                _raceTask = null;
            }
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public void showChatWindow(Player player, int val) {
        if (player.haveItem(RACE_STAMP,4))
            showChatWindow(player, "race_start001a.htm");
        else if (player.getLevel() >= 78 && _raceTask == null)
            showChatWindow(player, "race_start001.htm");
        else
            showChatWindow(player, "race_start002.htm");
    }
}
