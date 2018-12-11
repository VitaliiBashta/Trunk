package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;

public final class SolinaGuardian extends Fighter {

    public SolinaGuardian(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        getActor().altOnMagicUseTimer(getActor(), SkillTable.INSTANCE.getInfo(6371));
    }
}