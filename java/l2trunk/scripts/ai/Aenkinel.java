package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class Aenkinel extends Fighter {

    public Aenkinel(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();

        // Устанавливаем реюз для Tower и Great Seal
        if (actor.getNpcId() == 25694 || actor.getNpcId() == 25695) {
            Reflection ref = actor.getReflection();
            ref.setReenterTime(System.currentTimeMillis());
        }

        if (actor.getNpcId() == 25694)
            for (int i = 0; i < 4; i++)
                actor.getReflection().addSpawnWithoutRespawn(18820, actor, 250);
        else if (actor.getNpcId() == 25695)
            for (int i = 0; i < 4; i++)
                actor.getReflection().addSpawnWithoutRespawn(18823, actor, 250);

        super.onEvtDead(killer);
    }
}