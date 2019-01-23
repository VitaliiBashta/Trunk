package l2trunk.scripts.ai.seedofinfinity;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class YehanBrother extends Fighter {
    private static final List<Integer> MINIONS = List.of(22509, 22510, 22511, 22512);
    private long _spawnTimer = 0;

    public YehanBrother(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        _spawnTimer = System.currentTimeMillis();
    }

    private NpcInstance getBrother() {
        NpcInstance actor = getActor();
        int brotherId = 0;
        if (actor.getNpcId() == 25665)
            brotherId = 25666;
        else if (actor.getNpcId() == 25666)
            brotherId = 25665;
        int id = brotherId;
        return actor.getReflection().getNpcs()
                .filter(npc -> npc.getNpcId() == id)
                .findFirst().orElse(null);
    }

    @Override
    public void thinkAttack() {
        NpcInstance actor = getActor();
        NpcInstance brother = getBrother();
        if (!brother.isDead() && !actor.isInRange(brother, 300))
            actor.altOnMagicUseTimer(getActor(), 6371);
        else
            removeInvul(actor);
        if (_spawnTimer + 40000 < System.currentTimeMillis()) {
            _spawnTimer = System.currentTimeMillis();
            NpcInstance mob = actor.getReflection().addSpawnWithoutRespawn(Rnd.get(MINIONS), Location.findAroundPosition(actor, 300), 0);
            mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, actor.getAggressionTarget(), 1000);
        }
        super.thinkAttack();
    }

    private void removeInvul(NpcInstance npc) {
        npc.getEffectList().getAllEffects()
                .filter(e -> e.getSkill().id == 6371)
                .forEach(Effect::exit);
    }
}