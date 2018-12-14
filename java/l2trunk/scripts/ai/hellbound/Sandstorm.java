package l2trunk.scripts.ai.hellbound;

import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Location;

public final class Sandstorm extends DefaultAI {
    private static final int AGGRO_RANGE = 200;
    private static final int gust = 5435;
    private static final int blow = 5494;
    private long lastThrow = 0;

    public Sandstorm(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (lastThrow + 5000 < System.currentTimeMillis())
            for (Playable target : World.getAroundPlayables(actor, AGGRO_RANGE, AGGRO_RANGE))
                if (target != null && !target.isAlikeDead() && !target.isInvul() && target.isVisible() && GeoEngine.canSeeTarget(actor, target, false)) {
                    actor.doCast(gust, target, true);
                    actor.doCast(blow, target, true);
                    lastThrow = System.currentTimeMillis();
                    break;
                }

        return super.thinkActive();
    }

    @Override
    public void thinkAttack() {
    }

    @Override
    public void onIntentionAttack(Creature target) {
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    public void onEvtAggression(Creature attacker, int aggro) {
    }

    @Override
    public void onEvtClanAttacked(Creature attacked_member, Creature attacker, int damage) {
    }

    @Override
    public boolean randomWalk() {
        NpcInstance actor = getActor();
        Location sloc = actor.getSpawnedLoc();

        Location pos = Location.findPointToStay(actor, sloc, 150, 300);
        if (GeoEngine.canMoveToCoord(actor, pos)) {
            actor.setRunning();
            addTaskMove(pos, false);
        }

        return true;
    }
}