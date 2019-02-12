package l2trunk.scripts.ai.den_of_evil;

import l2trunk.gameserver.ai.DefaultAI;
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
    public synchronized void switchAITask(long NEW_DELAY) {
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        World.getAroundPlayers(actor).stream()
                .filter(p -> p.getLevel() <= 37)
                .forEach(p -> Functions.npcSay(actor, NpcString.THIS_PLACE_IS_DANGEROUS_S1, p.getName()));
        return false;
    }
}
