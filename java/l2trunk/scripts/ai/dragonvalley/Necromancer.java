package l2trunk.scripts.ai.dragonvalley;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.NpcUtils;

public final class Necromancer extends Mystic {

    public Necromancer(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (attacker == null || actor.isDead()) {
            return;
        }

        actor.getAggroList().addDamageHate(attacker, 0, damage);

        if (damage > 0 && (attacker.isSummon() || attacker.isPet())) {
            actor.getAggroList().addDamageHate(attacker.getPlayer(), 0, actor.getParameter("searchingMaster", false) ? damage : 1);
        }

        if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK) {
            if (!actor.isRunning()) {
                startRunningTask(AI_TASK_ATTACK_DELAY);
            }
            setIntentionAttack(CtrlIntention.AI_INTENTION_ATTACK, attacker);
        }
        if (Rnd.chance(Config.NECROMANCER_MS_CHANCE)) {
            NpcInstance n = NpcUtils.spawnSingle(Rnd.chance(50) ? 22818 : 22819, getActor().getLoc());
            n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2);
        }
        notifyFriends(attacker, damage);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        int count = actor.getMinionList().getAliveMinions().size();
        if (Rnd.chance(Config.NECROMANCER_MS_CHANCE * 2)) {
            NpcInstance n = NpcUtils.spawnSingle(Rnd.chance(50) ? 22818 : 22819, getActor().getLoc());
            n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 2);
        }
        super.onEvtDead(killer);
    }
}
