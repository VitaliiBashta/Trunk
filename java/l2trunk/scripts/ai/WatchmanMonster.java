package l2trunk.scripts.ai;

import l2trunk.commons.lang.reference.HardReference;
import l2trunk.commons.lang.reference.HardReferences;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;

public final class WatchmanMonster extends Fighter {
    private static final List<String> flood = List.of("I'll be back", "You are stronger than expected");
    private static final List<String> flood2 = List.of("Help me!", "Alarm! We are under attack!");
    private long _lastSearch = 0;
    private boolean isSearching = false;
    private HardReference<? extends Creature> _attackerRef = HardReferences.emptyRef();

    public WatchmanMonster(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(final Creature attacker, int damage) {
        final NpcInstance actor = getActor();
        if (attacker != null && !actor.getFaction().isNone() && actor.getCurrentHpPercents() < 50 && _lastSearch < System.currentTimeMillis() - 15000) {
            _lastSearch = System.currentTimeMillis();
            _attackerRef = attacker.getRef();

            if (findHelp())
                return;
            Functions.npcSay(actor, "Anyone, help me!");
        }
        super.onEvtAttacked(attacker, damage);
    }

    private boolean findHelp() {
        isSearching = false;
        final NpcInstance actor = getActor();
        Creature attacker = _attackerRef.get();
        if (attacker == null)
            return false;
        if (!actor.isDead())
            actor.getAroundNpc(1000, 150)
                    .filter(npc -> npc.isInFaction(actor))
                    .filter(npc -> !npc.isInCombat())
                    .findFirst().ifPresent(npc -> {
                clearTasks();
                isSearching = true;
                addTaskMove(npc.getLoc(), true);
                Functions.npcSay(actor, Rnd.get(flood));
            });
        return isSearching;
    }

    @Override
    public void onEvtDead(Creature killer) {
        _lastSearch = 0;
        _attackerRef = HardReferences.emptyRef();
        isSearching = false;
        super.onEvtDead(killer);
    }

    @Override
    public void onEvtArrived() {
        NpcInstance actor = getActor();
        if (isSearching) {
            Creature attacker = _attackerRef.get();
            if (attacker != null) {
                Functions.npcSay(actor, Rnd.get(flood2));
                notifyFriends(attacker, 100);
            }
            isSearching = false;
            notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 100);
        } else
            super.onEvtArrived();
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
        if (!isSearching)
            super.onEvtAggression(target, aggro);
    }
}