package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;

public final class Thomas extends Fighter {
    private long _lastSay;

    private static final List<String> _stay = List.of(
            "Ha ... Ha ... You came to save the snowman?",
            "So I just do not give it to you!",
            "In order to save your snowman, you'll have to kill me!",
            "Ha ... Ha ... You think it's that simple?");

    private static final List<String> _attacked = List.of(
            "You must all die!",
            "My Snowman and will not have any New Year!",
            "I'll kill you all!",
            "With so little beat? Not eating porridge? Ha ... Ha ...",
            "And it's called heroes?",
            "Do not you seen a snowman!",
            "Only the ancient weapon capable of defeating me!");

    public Thomas(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead())
            return true;

        // Ругаемся не чаще, чем раз в 10 секунд
        if (!actor.isInCombat() && System.currentTimeMillis() - _lastSay > 10000) {
            Functions.npcSay(actor, Rnd.get(_stay));
            _lastSay = System.currentTimeMillis();
        }
        return super.thinkActive();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (attacker == null || attacker.getPlayer() == null)
            return;

        // Ругаемся не чаще, чем раз в 5 секунд
        if (System.currentTimeMillis() - _lastSay > 5000) {
            Functions.npcSay(actor, Rnd.get(_attacked));
            _lastSay = System.currentTimeMillis();
        }
        super.onEvtAttacked(attacker, damage);
    }
}