package l2trunk.scripts.ai.seedofdestruction;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;

import java.util.Arrays;
import java.util.List;

public final class TiatsTrap extends DefaultAI {
    private static final List<Integer> holdTraps = List.of(18720, 18721, 18722, 18723, 18724, 18725, 18726, 18727, 18728);
    private static final List<Integer> damageTraps = List.of(
            18737, 18738, 18739, 18740, 18741, 18742, 18743, 18744, 18745, 18746, 18747, 18748, 18749,
            18750, 18751, 18752, 18753, 18754, 18755, 18756, 18757, 18758, 18759, 18760, 18761, 18762,
            18763, 18764, 18765, 18766, 18767, 18768, 18769, 18770, 18771, 18772, 18773, 18774);
    private static final List<Integer> stunTraps = List.of(18729, 18730, 18731, 18732, 18733, 18734, 18735, 18736);

    public TiatsTrap(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
        actor.startDamageBlocked();
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.getAroundCharacters(200, 150).count() >0) {
            int skillId;
            int skillLvl = 9;
            if (holdTraps.contains(actor.getNpcId()))
                skillId = 4186;
            else if (damageTraps.contains(actor.getNpcId()))
                skillId = 5311;
            else if (stunTraps.contains(actor.getNpcId())) {
                skillId = 4072;
                skillLvl= 10;
            }else
                return false;
            actor.doCast(skillId,skillLvl, actor, true);
            ThreadPoolManager.INSTANCE.schedule(() -> getActor().doDie(null), 5000);
            return true;
        }
        return true;
    }
}