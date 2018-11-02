package ai.monas;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.tables.SkillTable;
import npc.model.events.SumielInstance;

public class Furnface extends DefaultAI {
    public Furnface(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSeeSpell(Skill skill, Creature caster) {
        NpcInstance actor = getActor();

        if (skill.getId() == 9059) {
            actor.setNpcState(1);
            actor.setTargetable(false);
            actor.doCast(SkillTable.getInstance().getInfo(5144, 1), caster, true);
            for (NpcInstance npc : GameObjectsStorage.getAllNpcs()) {
                if (npc != null && npc.getNpcId() == 32758 && actor.getDistance(npc) <= 1000)
                    ((SumielInstance) npc).setSCE_POT_ON(actor.getAISpawnParam());
            }

            ThreadPoolManager.getInstance().schedule(new OFF_TIMER(), 2 * 1000);
            actor.setTargetable(true);
        }
    }

    private class OFF_TIMER extends RunnableImpl {
        @Override
        public void runImpl() {
            NpcInstance actor = getActor();
            actor.setNpcState(2);
        }
    }
}