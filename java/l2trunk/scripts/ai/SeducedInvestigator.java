package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

import java.util.List;
import java.util.stream.Collectors;

public final class SeducedInvestigator extends Fighter {
    private final List<Integer> _allowedTargets = List.of(25659, 25660, 25661, 25662, 25663, 25664);
    private long _reuse = 0;

    public SeducedInvestigator(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
        actor.startHealBlocked();
        AI_TASK_ACTIVE_DELAY = 5000;
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead())
            return false;

        actor.getAroundNpc(1000, 300)
                .filter(npc -> _allowedTargets.contains(npc.getNpcId()))
                .forEach(npc -> actor.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, npc, 300));

        if (Rnd.chance(0.1) && _reuse + 30000 < System.currentTimeMillis()) {
            List<Player> players = World.getAroundPlayers(actor, 500, 200).collect(Collectors.toList());
            if (players.isEmpty())
                return false;
            Player player = Rnd.get(players);
            if (player.getReflectionId() == actor.getReflectionId()) {
                _reuse = System.currentTimeMillis();
                int[] buffs = {5970, 5971, 5972, 5973};
                if (actor.getNpcId() == 36562)
                    actor.doCast(buffs[0], player, true);
                else if (actor.getNpcId() == 36563)
                    actor.doCast(buffs[1], player, true);
                else if (actor.getNpcId() == 36564)
                    actor.doCast(buffs[2], player, true);
                else
                    actor.doCast(buffs[3], player, true);
            }
        }

        return true;
    }

    @Override
    public void onEvtDead(Creature killer) {
        Reflection r = getActor().getReflection();
        r.getPlayers().forEach(p ->
                p.sendPacket(new ExShowScreenMessage("The Investigator has been killed. The mission is failed.")));

        r.startCollapseTimer(5 * 1000L);

        super.onEvtDead(killer);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (attacker == null)
            return;

        if (attacker instanceof Playable)
            return;

        if (attacker.getNpcId() == 25659 || attacker.getNpcId() == 25660 || attacker.getNpcId() == 25661)
            actor.getAggroList().addDamageHate(attacker, 0, 20);

        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
        if (target instanceof Playable)
            return;

        super.onEvtAggression(target, aggro);
    }

    @Override
    public boolean checkAggression(Playable target, boolean avoidAttack) {
        return false;
    }
}