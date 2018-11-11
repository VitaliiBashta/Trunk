package l2trunk.scripts.ai.custom;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.ReflectionBossInstance;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.FuncSet;

public class LabyrinthLostBeholder extends Fighter {

    public LabyrinthLostBeholder(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        Reflection r = actor.getReflection();
        if (!r.isDefault())
            if (checkMates(actor.getNpcId()))
                if (findLostCaptain() != null)
                    findLostCaptain().addStatFunc(new FuncSet(Stats.MAGIC_DEFENCE, 0x30, this, findLostCaptain().getTemplate().baseMDef * 0.66));
        super.onEvtDead(killer);
    }

    private boolean checkMates(int id) {
        for (NpcInstance n : getActor().getReflection().getNpcs())
            if (n.getNpcId() == id && !n.isDead())
                return false;
        return true;
    }

    private NpcInstance findLostCaptain() {
        for (NpcInstance n : getActor().getReflection().getNpcs())
            if (n instanceof ReflectionBossInstance)
                return n;
        return null;
    }
}