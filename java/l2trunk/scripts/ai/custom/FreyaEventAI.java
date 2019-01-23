package l2trunk.scripts.ai.custom;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;

public final class FreyaEventAI extends DefaultAI {
    private static final List<Integer> GIFT_SKILLS = List.of(9150, 9151, 9152, 9153, 9154, 9155, 9156);
    private static final int GIFT_CHANCE = 5;
    private static final int FREYA_GIFT = 17138;
    private static final List<NpcString> SAY_TEXT = List.of(
            NpcString.DEAR_S1,
            NpcString.BUT_I_KIND_OF_MISS_IT,
            NpcString.I_JUST_DONT_KNOW_WHAT_EXPRESSION_I_SHOULD_HAVE_IT_APPEARED_ON_ME,
            NpcString.EVEN_THOUGH_YOU_BRING_SOMETHING_CALLED_A_GIFT_AMONG_YOUR_HUMANS_IT_WOULD_JUST_BE_PROBLEMATIC_FOR_ME,
            NpcString.THE_FEELING_OF_THANKS_IS_JUST_TOO_MUCH_DISTANT_MEMORY_FOR_ME,
            NpcString.I_AM_ICE_QUEEN_FREYA);

    public FreyaEventAI(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public boolean randomWalk() {
        return false;
    }

    @Override
    public void onEvtSeeSpell(Skill skill, Creature caster) {
        NpcInstance actor = getActor();

        if (caster == null || !caster.isPlayer())
            return;

        GameObject casterTarget = caster.getTarget();
        if (casterTarget == null || casterTarget.getObjectId() != actor.getObjectId())
            return;

        Player player = caster.getPlayer();

        if (GIFT_SKILLS.contains(skill.id)) {
            if (Rnd.chance(GIFT_CHANCE)) {
                Functions.npcSay(actor, SAY_TEXT.get(0), player.getName());
                Functions.addItem(player, FREYA_GIFT, 1, "FreyaEventAI");
            } else if (Rnd.chance(70))
                Functions.npcSay(actor, Rnd.get(SAY_TEXT));
        }
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
    }
}