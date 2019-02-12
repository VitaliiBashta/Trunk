package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.MinionList;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.MinionInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;
import java.util.stream.Collectors;

public final class Kama56Boss extends Fighter {
    private long _nextOrderTime = 0;
    private Player lastMinionsTarget = null;

    public Kama56Boss(NpcInstance actor) {
        super(actor);
    }

    private void sendOrderToMinions(NpcInstance actor) {
        if (!actor.isInCombat()) {
            lastMinionsTarget = null;
            return;
        }

        MinionList ml = actor.getMinionList();
        if (ml == null || !ml.hasMinions()) {
            lastMinionsTarget = null;
            return;
        }

        long now = System.currentTimeMillis();
        if (_nextOrderTime > now && lastMinionsTarget != null) {
            Player old_target = lastMinionsTarget;
            if (!old_target.isAlikeDead()) {
                for (MinionInstance m : ml.getAliveMinions())
                    if (m.getAI().getAttackTarget() != old_target)
                        m.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, old_target, 10000000);
                return;
            }
        }

        _nextOrderTime = now + 30000;

        if (!World.getAroundPlayers(actor).isEmpty()) {
            lastMinionsTarget = null;
            return;
        }

        List<Player> alive = World.getAroundPlayers(actor).stream()
                .filter(p -> !p.isAlikeDead())
                .collect(Collectors.toList());
        if (alive.isEmpty()) {
            lastMinionsTarget = null;
            return;
        }

        Player target = Rnd.get(alive);
        lastMinionsTarget = target;

        Functions.npcSayCustomMessage(actor, "Kama56Boss.attack", target.getName());
        for (MinionInstance m : ml.getAliveMinions()) {
            m.getAggroList().clear();
            m.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, target, 10000000);
        }
    }

    @Override
    public void thinkAttack() {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        sendOrderToMinions(actor);
        super.thinkAttack();
    }
}