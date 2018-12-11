package l2trunk.scripts.ai.Zone.DragonValley.DV_RB;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;

public final class BleedingFlyMinion extends Fighter {

    private final Skill self_destruction = SkillTable.INSTANCE.getInfo(6872);

    private long last_cast_sd = 0;

    public BleedingFlyMinion(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (last_cast_sd < System.currentTimeMillis()) {
            actor.doCast(self_destruction, attacker, true);
            last_cast_sd = System.currentTimeMillis() + Rnd.get(15, 30) * 1000;
        }
        super.onEvtAttacked(attacker, damage);
    }
}
