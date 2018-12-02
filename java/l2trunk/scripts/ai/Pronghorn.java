package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Pronghorn extends Fighter {
    private static final Logger LOG = LoggerFactory.getLogger(Pronghorn.class);

    private boolean _mobsNotSpawned = true;
    private static final int MOBS = 22087;
    private static final int MOBS_COUNT = 4;

    private Pronghorn(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSeeSpell(Skill skill, Creature caster) {
        NpcInstance actor = getActor();
        if (skill.isMagic())
            return;
        if (_mobsNotSpawned) {
            _mobsNotSpawned = false;
            for (int i = 0; i < MOBS_COUNT; i++)
                try {
                    SimpleSpawner sp = new SimpleSpawner(NpcHolder.getTemplate(MOBS));
                    sp.setLoc(Location.findPointToStay(actor, 100, 120));
                    NpcInstance npc = sp.doSpawn(true);
                    if (caster.isPet() || caster.isSummon())
                        npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, caster, Rnd.get(2, 100));
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, caster.getPlayer(), Rnd.get(1, 100));
                } catch (RuntimeException e) {
                    LOG.error("Error while spawning Mobs of Pronghorn", e);
                }
        }
    }

    @Override
    public void onEvtDead(Creature killer) {
        _mobsNotSpawned = true;
        super.onEvtDead(killer);
    }

    @Override
    public boolean randomWalk() {
        return _mobsNotSpawned;
    }
}