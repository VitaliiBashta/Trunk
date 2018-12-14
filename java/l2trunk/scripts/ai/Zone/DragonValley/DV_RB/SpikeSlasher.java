package l2trunk.scripts.ai.Zone.DragonValley.DV_RB;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.NpcUtils;

public final class SpikeSlasher extends Fighter {

    private boolean spawn_50 = true;
    private boolean spawn_33 = true;
    private boolean spawn_5 = true;

    private static final int gem_dragon = 25733;

    private long last_attack_time = 0;
    private long last_cast_anchor = 0;

    private static final int paralysis = 6878;

    public SpikeSlasher(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        super.thinkActive();
        if (last_attack_time != 0 && last_attack_time + 30 * 60 * 1000L < System.currentTimeMillis()) {
            getActor().deleteMe();
        }
        return true;
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        last_attack_time = System.currentTimeMillis();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (actor.getCurrentHpPercents() <= 50 && spawn_50) {
            spawn_gemdragons(actor, attacker);
            spawn_50 = false;
        } else if (actor.getCurrentHpPercents() <= 33 && spawn_33) {
            spawn_gemdragons(actor, attacker);
            spawn_33 = false;
        } else if (actor.getCurrentHpPercents() <= 5 && spawn_5) {
            spawn_gemdragons(actor, attacker);
            spawn_5 = false;
        }
        if (last_cast_anchor < System.currentTimeMillis()) {
            actor.doCast(paralysis, attacker, true);
            last_cast_anchor = System.currentTimeMillis() + Rnd.get(20, 90) * 1000;
        }
        super.onEvtAttacked(attacker, damage);
    }

    private void spawn_gemdragons(NpcInstance actor, Creature attacker) {
        for (int i = 0; i < 3; i++) {
            NpcInstance n = NpcUtils.spawnSingle(gem_dragon, (actor.getX() + Rnd.get(-100, 100)), (actor.getY() + Rnd.get(-100, 100)), actor.getZ());
            n.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2);
        }
    }

}