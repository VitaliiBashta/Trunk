package l2trunk.scripts.ai.dragonvalley;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;

public final class ExplodingOrcGhost extends Fighter {

    private static final int SELF_DESTRUCTION = 6850;

    public ExplodingOrcGhost(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        ThreadPoolManager.INSTANCE.schedule(() -> {
            NpcInstance npc = getActor();
            npc.abortAttack(true, false);
            npc.abortCast(true, false);
            npc.doCast(SELF_DESTRUCTION, actor, true);
        }, 3000L);
        super.onEvtSpawn();
    }

}
