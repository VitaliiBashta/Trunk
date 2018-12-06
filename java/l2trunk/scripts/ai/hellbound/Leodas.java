package l2trunk.scripts.ai.hellbound;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.ReflectionUtils;

public final class Leodas extends Fighter {
    private DoorInstance door1 = ReflectionUtils.getDoor(19250003);
    private DoorInstance door2 = ReflectionUtils.getDoor(19250004);

    public Leodas(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        door1.openMe();
        door2.openMe();
        ThreadPoolManager.INSTANCE.schedule(() -> {
            door1.closeMe();
            door2.closeMe();
        }, 60 * 1000L);
        super.onEvtDead(killer);
    }
}