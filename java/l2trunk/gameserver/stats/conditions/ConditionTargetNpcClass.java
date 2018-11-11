package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.stats.Env;

@SuppressWarnings("unchecked")
public class ConditionTargetNpcClass extends Condition {
    private final Class<NpcInstance> _npcClass;

    public ConditionTargetNpcClass(String name) {
        Class<NpcInstance> classType = null;
        try {
            classType = (Class<NpcInstance>) Class.forName("l2trunk.gameserver.model.instances." + name + "Instance");
        } catch (ClassNotFoundException e) {
            try {
                classType = (Class<NpcInstance>) Class.forName("l2trunk.scripts.npc.model." + name + "Instance");
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        }

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
