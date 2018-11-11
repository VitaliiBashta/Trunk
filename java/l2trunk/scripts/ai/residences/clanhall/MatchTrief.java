package l2trunk.scripts.ai.residences.clanhall;

import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;

/**
 * @author VISTALL
 * @date 16:38/22.04.2011
 */
public class MatchTrief extends MatchFighter {
    private static final Skill HOLD = SkillTable.getInstance().getInfo(4047, 6);

    public MatchTrief(NpcInstance actor) {
        super(actor);
    }

    public void hold() {
        NpcInstance actor = getActor();
        addTaskCast(actor, HOLD);
        doTask();
    }
}
