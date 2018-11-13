package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class Scarecrow extends Fighter {
    public Scarecrow(NpcInstance actor) {
        super(actor);
        actor.block();
        actor.setIsInvul(true);
    }

    @Override
    public void onIntentionAttack(Creature target) {
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    public void onEvtAggression(Creature attacker, int aggro) {
    }
}