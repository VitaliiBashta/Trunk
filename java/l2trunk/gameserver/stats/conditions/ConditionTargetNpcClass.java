package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Scripts;
import l2trunk.gameserver.stats.Env;

public final class ConditionTargetNpcClass extends Condition {
    private final Class<NpcInstance> _npcClass;

    public ConditionTargetNpcClass(String name) {
        Class<NpcInstance>  classType = (Class<NpcInstance>) Scripts.INSTANCE.getNpcInstanceAI( name + "Instance");

        if (classType == null)
            throw new IllegalArgumentException("Not found type class for type: " + name + ".");
        else
            _npcClass = classType;
    }

    @Override
    protected boolean testImpl(Env env) {
        return env.target != null && env.target.getClass() == _npcClass;
    }
}
