package l2trunk.scripts.ai.dragonvalley;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;

public final class ExplodingOrcGhost extends Fighter {

    private final Skill SELF_DESTRUCTION = SkillTable.getInstance().getInfo(6850, 1);

    public ExplodingOrcGhost(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        ThreadPoolManager.getInstance().schedule(new StartSelfDestructionTimer(getActor()), 3000L);
        super.onEvtSpawn();
    }

    private class StartSelfDestructionTimer extends RunnableImpl {

        private final NpcInstance _npc;

        StartSelfDestructionTimer(NpcInstance npc) {
            _npc = npc;
        }

        @Override
        public void runImpl() {
            _npc.abortAttack(true, false);
            _npc.abortCast(true, false);
            _npc.doCast(SELF_DESTRUCTION, _actor, true);
        }
    }

}
