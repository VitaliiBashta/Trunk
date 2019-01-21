package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;

public final class GuardianAngel extends DefaultAI {
    private static final List<String> flood = List.of(
            "Waaaah! Step back from the confounded box! I will take it myself!",
            "Grr! Who are you and why have you stopped me?",
            "Grr. I've been hit...");

    public GuardianAngel(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        Functions.npcSay(actor, Rnd.get(flood));

        return super.thinkActive();
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        if (actor != null)
            Functions.npcSay(actor, flood.get(2));
        super.onEvtDead(killer);
    }
}