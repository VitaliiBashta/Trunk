package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;

public final class BlacksmithMammon extends DefaultAI {
    private long chatVar = 0;
    private static final long chatDelay = 30 * 60 * 1000L;

    private static final List<NpcString> mamonText = List.of(
            NpcString.RULERS_OF_THE_SEAL_I_BRING_YOU_WONDROUS_GIFTS,
            NpcString.RULERS_OF_THE_SEAL_I_HAVE_SOME_EXCELLENT_WEAPONS_TO_SHOW_YOU,
            NpcString.IVE_BEEN_SO_BUSY_LATELY_IN_ADDITION_TO_PLANNING_MY_TRIP);

    public BlacksmithMammon(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead())
            return true;
        if (chatVar + chatDelay < System.currentTimeMillis()) {
            chatVar = System.currentTimeMillis();
            Functions.npcShout(actor, Rnd.get(mamonText));
        }
        return false;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }
}