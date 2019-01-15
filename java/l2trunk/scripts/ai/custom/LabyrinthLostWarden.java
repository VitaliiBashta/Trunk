package l2trunk.scripts.ai.custom;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.ReflectionBossInstance;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.FuncSet;

public final class LabyrinthLostWarden extends Fighter {

    public LabyrinthLostWarden(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        Reflection r = actor.getReflection();
        if (!r.isDefault())
            if (checkMates(actor.getNpcId()))
                if (findLostCaptain() != null)
                    findLostCaptain().addStatFunc(new FuncSet(Stats.POWER_ATTACK, 0x30, this, findLostCaptain().getTemplate().basePAtk * 0.66));
        super.onEvtDead(killer);
    }

    private boolean checkMates(int id) {
        return getActor().getReflection().getNpcs()
                .filter(n -> n.getNpcId() == id)
                .allMatch(Creature::isDead);
    }

    private NpcInstance findLostCaptain() {
        return getActor().getReflection().getNpcs()
                .filter(n -> (n instanceof ReflectionBossInstance))
                .findFirst().orElse(null);
    }
}