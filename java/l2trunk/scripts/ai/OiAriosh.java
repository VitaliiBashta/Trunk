package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class OiAriosh extends Fighter {
    private static final int MOB = 18556;
    private static final List<Integer> _hps = List.of(80, 60, 40, 30, 20, 10, 5, -5);
    private int _hpCount = 0;

    public OiAriosh(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (!actor.isDead())
            if (_hpCount < _hps.size() && actor.getCurrentHpPercents() < _hps.get(_hpCount)) {
                spawnMob(attacker);
                _hpCount++;
            }
        super.onEvtAttacked(attacker, damage);
    }

    private void spawnMob(Creature attacker) {
        NpcInstance actor = getActor();
        SimpleSpawner sp = (SimpleSpawner) new SimpleSpawner(MOB)
                .setLoc(Location.findPointToStay(actor, 100, 120))
                .setReflection(actor.getReflection());
        NpcInstance npc = sp.doSpawn(true);
        npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 100);
    }

    @Override
    public void onEvtDead(Creature killer) {
        _hpCount = 0;
        super.onEvtDead(killer);
    }
}