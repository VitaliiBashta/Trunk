package l2trunk.scripts.ai.residences.clanhall;

import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;

public class MatchTrief extends MatchFighter {
    private final Skill HOLD = SkillTable.INSTANCE().getInfo(4047, 6);

    public MatchTrief(NpcInstance actor) {
        super(actor);
    }

    public void hold() {
        NpcInstance actor = getActor();
        addTaskCast(actor, HOLD);
        doTask();
    }
}
