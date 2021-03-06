package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.RaidBossInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public final class YehanBrotherInstance extends RaidBossInstance {
    public YehanBrotherInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp) {
        if (getBrother().getCurrentHp() > 500 && damage > getCurrentHp()) {
            damage = getCurrentHp() - 1;
        }
        super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
    }

    @Override
    protected void onDeath(Creature killer) {
        super.onDeath(killer);
        if (!getBrother().isDead())
            getBrother().doDie(killer);
    }

    private NpcInstance getBrother() {
        int brotherId = 0;
        if (getNpcId() == 25665)
            brotherId = 25666;
        else if (getNpcId() == 25666)
            brotherId = 25665;
        int id = brotherId;
        return getReflection().getNpcs()
                .filter(npc -> getNpcId() == id)
                .findFirst().orElse(null);
    }
}