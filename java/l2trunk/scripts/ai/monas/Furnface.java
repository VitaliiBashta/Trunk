package l2trunk.scripts.ai.monas;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.scripts.npc.model.events.SumielInstance;

public final class Furnface extends DefaultAI {
    public Furnface(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSeeSpell(Skill skill, Creature caster) {
        NpcInstance actor = getActor();

        if (skill.getId() == 9059) {
            actor.setNpcState(1);
            actor.setTargetable(false);
            actor.doCast(SkillTable.getInstance().getInfo(5144, 1), caster, true);
            for (NpcInstance npc : GameObjectsStorage.getAllNpcs()) {
                if (npc != null && npc.getNpcId() == 32758 && actor.getDistance(npc) <= 1000)
                    ((SumielInstance) npc).setSCE_POT_ON(actor.getAISpawnParam());
            }

            ThreadPoolManager.getInstance().schedule(() -> {
                NpcInstance act = getActor();
                act.setNpcState(2);
            }, 2 * 1000);
            actor.setTargetable(true);
        }
    }

}