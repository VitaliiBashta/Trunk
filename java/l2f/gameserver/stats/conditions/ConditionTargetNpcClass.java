package l2f.gameserver.stats.conditions;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.scripts.Scripts;
import l2f.gameserver.stats.Env;

@SuppressWarnings("unchecked")
public class ConditionTargetNpcClass extends Condition {
    private final Class<NpcInstance> _npcClass;

    public ConditionTargetNpcClass(String name) {
        Class<NpcInstance> classType;
        try {
            classType = (Class<NpcInstance>) Class.forName("l2f.gameserver.model.instances." + name + "Instance");
        } catch (ClassNotFoundException e) {
            classType = (Class<NpcInstance>) Scripts.getInstance().getClasses().get("npc.model." + name + "Instance");
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
