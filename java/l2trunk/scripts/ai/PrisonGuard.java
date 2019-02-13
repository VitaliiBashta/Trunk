package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;

/**
 * AI мобов Prison Guard на Isle of Prayer.<br>
 * - Не используют функцию Random Walk<br>
 * - Ругаются на атаковавших чаров без эффекта Event Timer<br>
 * - Ставят в петрификацию атаковавших чаров без эффекта Event Timer<br>
 * - Не могут быть убиты чарами без эффекта Event Timer<br>
 * - Не проявляют агресии к чарам без эффекта Event Timer<br>
 * ID: 18367, 18368
 *
 */
public final class PrisonGuard extends Fighter {
    private static final int RACE_STAMP = 10013;
    private static final int  petrification = 4578; // Petrification
    public PrisonGuard(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean checkAggression(Playable target, boolean avoidAttack) {
        // 18367 не агрятся
        NpcInstance actor = getActor();
        if (actor.isDead() || actor.getNpcId() == 18367)
            return false;

        if (target.getEffectList().getEffectsCountForSkill(Skill.SKILL_EVENT_TIMER) == 0)
            return false;

        return super.checkAggression(target, avoidAttack);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (actor.isDead())
            return;
        if (attacker instanceof Summon )
            attacker = ((Summon)attacker).owner;
        if (attacker.getEffectList().getEffectsCountForSkill(Skill.SKILL_EVENT_TIMER) == 0) {
            if (actor.getNpcId() == 18367)
                Functions.npcSay(actor, "It's not easy to obtain.");
            else if (actor.getNpcId() == 18368)
                Functions.npcSay(actor, "You're out of mind comming here...");


            actor.doCast(petrification, attacker, true);


            return;
        }

        // 18367 не отвечают на атаку, но зовут друзей
        if (actor.getNpcId() == 18367) {
            notifyFriends(attacker, damage);
            return;
        }

        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        if (actor.getNpcId() == 18367 && killer.getPlayer().getEffectList().getEffectsBySkillId(Skill.SKILL_EVENT_TIMER) != null)
            addItem(killer.getPlayer(), RACE_STAMP, 1);

        super.onEvtDead(killer);
    }
}