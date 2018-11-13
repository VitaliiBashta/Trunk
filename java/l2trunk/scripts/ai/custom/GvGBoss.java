package l2trunk.scripts.ai.custom;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

public final class GvGBoss extends Fighter {
    private boolean phrase1 = false;
    private boolean phrase2 = false;
    private boolean phrase3 = false;

    public GvGBoss(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();

        if (actor.getCurrentHpPercents() < 50 && !phrase1) {
            phrase1 = true;
            Functions.npcSay(actor, "You can not steal the treasures Herald!");
        } else if (actor.getCurrentHpPercents() < 30 && !phrase2) {
            phrase2 = true;
            Functions.npcSay(actor, "I'll skull fractured!");
        } else if (actor.getCurrentHpPercents() < 5 && !phrase3) {
            phrase3 = true;
            Functions.npcSay(actor, "All of you will die in terrible agony! destroy!");
        }

        super.onEvtAttacked(attacker, damage);
    }
}