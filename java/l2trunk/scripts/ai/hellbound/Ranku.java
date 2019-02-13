package l2trunk.scripts.ai.hellbound;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.AggroList.AggroInfo;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class Ranku extends Fighter {
    private static final int TELEPORTATION_CUBIC_ID = 32375;
    private static final Location CUBIC_POSITION = new Location(-19056, 278732, -15000, 0);
    private static final int SCAPEGOAT_ID = 32305;
    private long _massacreTimer = 0;

    public Ranku(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        Reflection r = getActor().getReflection();
        if (r != null)
            for (int i = 0; i < 4; i++)
                r.addSpawnWithRespawn(SCAPEGOAT_ID, getActor().getLoc(), 300, 60);
    }

    @Override
    public void thinkAttack() {
        NpcInstance actor = getActor();
        if (actor.isDead())
            return;

        long _massacreDelay = 30000L;
        if (_massacreTimer + _massacreDelay < System.currentTimeMillis()) {
            NpcInstance victim = getScapegoat();
            _massacreTimer = System.currentTimeMillis();
            if (victim != null)
                actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, victim, getMaximumHate() + 200000);
        }
        super.thinkAttack();
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();

        if (actor.getReflection() != null) {
            actor.getReflection().setReenterTime(System.currentTimeMillis());
            actor.getReflection().addSpawnWithoutRespawn(TELEPORTATION_CUBIC_ID, CUBIC_POSITION);
        }
        super.onEvtDead(killer);
    }

    private NpcInstance getScapegoat() {
        return getActor().getReflection().getNpcs()
                .filter(n -> n.getNpcId() == SCAPEGOAT_ID)
                .filter(n -> !n.isDead())
                .findFirst().orElse(null);
    }

    private int getMaximumHate() {
        NpcInstance actor = getActor();
        Creature cha = actor.getAggroList().getMostHated();
        if (cha != null) {
            AggroInfo ai = actor.getAggroList().get(cha);
            if (ai != null)
                return ai.hate;
        }
        return 0;
    }
}