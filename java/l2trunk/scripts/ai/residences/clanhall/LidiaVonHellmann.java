package l2trunk.scripts.ai.residences.clanhall;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.PositionUtils;
import l2trunk.scripts.ai.residences.SiegeGuardFighter;

public final class LidiaVonHellmann extends SiegeGuardFighter {
    private static final int DRAIN_SKILL = 4999;
    private static final int DAMAGE_SKILL = 4998;

    public LidiaVonHellmann(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();

        Functions.npcShout(getActor(), NpcString.HMM_THOSE_WHO_ARE_NOT_OF_THE_BLOODLINE_ARE_COMING_THIS_WAY_TO_TAKE_OVER_THE_CASTLE__HUMPH__THE_BITTER_GRUDGES_OF_THE_DEAD);
    }

    @Override
    public void onEvtDead(Creature killer) {
        super.onEvtDead(killer);

        Functions.npcShout(getActor(), NpcString.GRARR_FOR_THE_NEXT_2_MINUTES_OR_SO_THE_GAME_ARENA_ARE_WILL_BE_CLEANED);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();

        super.onEvtAttacked(attacker, damage);

        if (Rnd.chance(0.22))
            addTaskCast(attacker, DRAIN_SKILL);
        else if (actor.getCurrentHpPercents() < 20 && Rnd.chance(0.22))
            addTaskCast(attacker, DRAIN_SKILL);

        if (PositionUtils.calculateDistance(actor, attacker, false) > 300 && Rnd.chance(0.13))
            addTaskCast(attacker, DAMAGE_SKILL);
    }
}
