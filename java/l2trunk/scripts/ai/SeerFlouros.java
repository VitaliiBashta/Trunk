package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SeerFlouros extends Mystic {
    private static final int MOB = 18560;
    private static final int MOBS_COUNT = 2;
    private static final int[] _hps = {80, 60, 40, 30, 20, 10, 5, -5};
    private int _hpCount = 0;

    public SeerFlouros(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (!actor.isDead())
            if (_hpCount < _hps.length && actor.getCurrentHpPercents() < _hps[_hpCount]) {
                spawnMobs(attacker);
                _hpCount++;
            }
        super.onEvtAttacked(attacker, damage);
    }

    private void spawnMobs(Creature attacker) {
        NpcInstance actor = getActor();
        for (int i = 0; i < MOBS_COUNT; i++) {
            SimpleSpawner sp = (SimpleSpawner) new SimpleSpawner(MOB)
            .setLoc(Location.findPointToStay(actor, 100, 120))
            .setReflection(actor.getReflection());
            NpcInstance npc = sp.doSpawn(true);
            npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 100);
        }
    }

    @Override
    public void onEvtDead(Creature killer) {
        _hpCount = 0;
        super.onEvtDead(killer);
    }
}