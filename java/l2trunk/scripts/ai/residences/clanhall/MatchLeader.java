package l2trunk.scripts.ai.residences.clanhall;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;

/**
 * @author VISTALL
 * @date 16:38/22.04.2011
 */
public class MatchLeader extends MatchFighter {
    private static final Skill ATTACK_SKILL = SkillTable.getInstance().getInfo(4077, 6);

    public MatchLeader(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int dam) {
        super.onEvtAttacked(attacker, dam);

        if (Rnd.chance(10))
            addTaskCast(attacker, ATTACK_SKILL);
    }
}
