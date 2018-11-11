package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;

/**
 * @author pchayka
 */
public class SolinaGuardian extends Fighter {

    public SolinaGuardian(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getInfo(6371, 1));
    }
}