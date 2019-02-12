package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.SummonInstance;
import l2trunk.gameserver.utils.Location;

public final class FrostBuffalo extends Fighter {
    private static final int MOBS = 22093;
    private static final int MOBS_COUNT = 4;
    private boolean mobsNotSpawned = true;

    public FrostBuffalo(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSeeSpell(Skill skill, Creature caster) {
        NpcInstance actor = getActor();
        if (skill.isMagic())
            return;
        if (mobsNotSpawned) {
            mobsNotSpawned = false;
            for (int i = 0; i < MOBS_COUNT; i++) {
                SimpleSpawner sp = new SimpleSpawner(MOBS);
                sp.setLoc(Location.findPointToStay(actor, 100, 120));
                NpcInstance npc = sp.doSpawn(true);
                if (caster instanceof Summon)
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, caster, Rnd.get(2, 100));
                npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, caster.getPlayer(), Rnd.get(1, 100));
            }
        }
    }

    @Override
    public void onEvtDead(Creature killer) {
        mobsNotSpawned = true;
        super.onEvtDead(killer);
    }

    @Override
    public boolean randomWalk() {
        return mobsNotSpawned;
    }
}