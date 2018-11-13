package l2trunk.scripts.ai.residences.clanhall;

import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;

public final class MatchCleric extends MatchFighter {
    private final Skill HEAL = SkillTable.getInstance().getInfo(4056, 6);

    public MatchCleric(NpcInstance actor) {
        super(actor);
    }

    public void heal() {
        NpcInstance actor = getActor();
        addTaskCast(actor, HEAL);
        doTask();
    }
}
