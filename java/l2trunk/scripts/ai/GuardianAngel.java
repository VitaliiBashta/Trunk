package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

public class GuardianAngel extends DefaultAI {
    private static final String[] flood = {
            "Waaaah! Step back from the confounded box! I will take it myself!",
            "Grr! Who are you and why have you stopped me?",
            "Grr. I've been hit..."};

    public GuardianAngel(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        Functions.npcSay(actor, flood[Rnd.get(2)]);

        return super.thinkActive();
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        if (actor != null)
            Functions.npcSay(actor, flood[2]);
        super.onEvtDead(killer);
    }
}