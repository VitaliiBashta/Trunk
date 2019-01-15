package l2trunk.scripts.ai.custom;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.ReflectionBossInstance;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.FuncSet;

import java.util.Optional;

public final class LabyrinthLostWatcher extends Fighter {
    public LabyrinthLostWatcher(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        Reflection r = actor.getReflection();
        if (!r.isDefault())
            if (checkMates(actor.getNpcId()))
                findLostCaptain().ifPresent(cap -> cap.addStatFunc(new FuncSet(Stats.POWER_DEFENCE, 0x30, this, cap.getTemplate().basePDef * 0.66)));
        super.onEvtDead(killer);
    }

    private boolean checkMates(int id) {
        return getActor().getReflection().getNpcs()
                .filter(n -> n.getNpcId() == id)
                .allMatch(Creature::isDead);
    }

    private Optional<NpcInstance> findLostCaptain() {
        return getActor().getReflection().getNpcs()
                .filter(n -> n instanceof ReflectionBossInstance)
                .findFirst();
    }
}