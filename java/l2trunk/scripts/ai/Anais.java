package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.ReflectionUtils;

public final class Anais extends Fighter {
    private static Zone zone;

    public Anais(NpcInstance actor) {
        super(actor);
        this.AI_TASK_ATTACK_DELAY = 1000;
        this.AI_TASK_ACTIVE_DELAY = 1000;
        zone = ReflectionUtils.getZone("[FourSepulchers1]");
    }

    public static Zone getZone() {
        return zone;
    }

    @Override
    public boolean canSeeInSilentMove(Playable target) {
        return (!target.isSilentMoving()) || (Rnd.chance(10));
    }
}