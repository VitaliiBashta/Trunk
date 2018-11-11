package l2trunk.scripts.ai.den_of_evil;

import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.taskmanager.AiTaskManager;

public final class HestuiGuard extends DefaultAI {
    public HestuiGuard(NpcInstance actor) {
        super(actor);

    }

    @Override
    public synchronized void startAITask() {
        if (_aiTask == null)
            _aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(this, 10000L, 10000L);
    }

    @Override
    protected synchronized void switchAITask(long NEW_DELAY) {
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();

        for (Player player : World.getAroundPlayers(actor)) {
            if (player.getLevel() <= 37)
                Functions.npcSay(actor, NpcString.THIS_PLACE_IS_DANGEROUS_S1, player.getName());
        }

        return false;
    }
}
