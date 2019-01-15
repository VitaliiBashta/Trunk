package l2trunk.scripts.ai;

import l2trunk.commons.lang.reference.HardReference;
import l2trunk.commons.lang.reference.HardReferences;
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
    private HardReference<Player> _lastMinionsTargetRef = HardReferences.emptyRef();

    public Kama56Boss(NpcInstance actor) {
        super(actor);
    }

    private void sendOrderToMinions(NpcInstance actor) {
        if (!actor.isInCombat()) {
            _lastMinionsTargetRef = HardReferences.emptyRef();
            return;
        }

        MinionList ml = actor.getMinionList();
        if (ml == null || !ml.hasMinions()) {
            _lastMinionsTargetRef = HardReferences.emptyRef();
            return;
        }

        long now = System.currentTimeMillis();
        if (_nextOrderTime > now && _lastMinionsTargetRef.get() != null) {
            Player old_target = _lastMinionsTargetRef.get();
            if (old_target != null && !old_target.isAlikeDead()) {
                for (MinionInstance m : ml.getAliveMinions())
                    if (m.getAI().getAttackTarget() != old_target)
                        m.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, old_target, 10000000);
                return;
            }
        }

        _nextOrderTime = now + 30000;

        if (World.getAroundPlayers(actor).count() == 0) {
            _lastMinionsTargetRef = HardReferences.emptyRef();
            return;
        }

        List<Player> alive = World.getAroundPlayers(actor)
                .filter(p -> !p.isAlikeDead())
                .collect(Collectors.toList());
        if (alive.isEmpty()) {
            _lastMinionsTargetRef = HardReferences.emptyRef();
            return;
        }

        Player target = alive.get(Rnd.get(alive.size()));
        _lastMinionsTargetRef = target.getRef();

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