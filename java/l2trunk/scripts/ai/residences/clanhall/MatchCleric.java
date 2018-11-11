package l2trunk.scripts.ai.residences.clanhall;

import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;

/**
 * @author VISTALL
 * @date 16:38/22.04.2011
 */
public class MatchCleric extends MatchFighter {
    private static final Skill HEAL = SkillTable.getInstance().getInfo(4056, 6);

    public MatchCleric(NpcInstance actor) {
        super(actor);
    }

    public void heal() {
        NpcInstance actor = getActor();
        addTaskCast(actor, HEAL);
        doTask();
    }
}
