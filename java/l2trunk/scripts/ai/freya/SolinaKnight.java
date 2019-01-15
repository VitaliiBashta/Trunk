package l2trunk.scripts.ai.freya;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.instances.NpcInstance;

import java.util.List;

public final class SolinaKnight extends Fighter {
    public SolinaKnight(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public boolean thinkActive() {
        NpcInstance scarecrow = getActor().getAroundNpc(300, 100)
                .filter(npc -> npc.getNpcId() == 18912)
                .min((o1, o2) -> (int) (getActor().getDistance3D(o1) - getActor().getDistance3D(o2))).orElse(null);

        if (scarecrow != null) {
            getActor().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, scarecrow, 1);
            return true;
        }
        return false;
    }
}