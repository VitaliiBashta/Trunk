package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

public final class Archangel extends Fighter {
    private final Zone _zone = ReflectionUtils.getZone("[baium_epic]");
    private long _new_target = System.currentTimeMillis() + 20000;

    public Archangel(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public void thinkAttack() {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        if (_new_target < System.currentTimeMillis()) {
            List<Creature> alive = new ArrayList<>();
            actor.getAroundCharacters(2000, 200)
                    .filter(target -> !target.isDead())
                    .forEach(target -> {
                        if (target.getNpcId() == 29020) {
                            if (Rnd.chance(5))
                                alive.add(target);
                        } else
                            alive.add(target);

                    });
            if (!alive.isEmpty()) {
                Creature rndTarget = alive.get(Rnd.get(alive.size()));
                if (rndTarget != null && (rndTarget.getNpcId() == 29020 || rndTarget.isPlayer())) {
                    setIntentionAttack(rndTarget);
                    actor.getAggroList().addDamageHate(rndTarget, 100, 10);
                }
            }

            _new_target = (System.currentTimeMillis() + 20000);
        }
        super.thinkAttack();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (actor != null && !actor.isDead()) {
            if (attacker != null) {
                if (attacker.getNpcId() == 29020) {
                    actor.getAggroList().addDamageHate(attacker, damage, 10);
                    setIntentionAttack(attacker);
                }
            }
        }
        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public boolean maybeMoveToHome() {
        NpcInstance actor = getActor();
        if (actor != null && !_zone.checkIfInZone(actor))
            returnHome();
        return false;
    }

    @Override
    public void returnHome() {
        NpcInstance actor = getActor();
        Location sloc = actor.getSpawnedLoc();

        clearTasks();
        actor.stopMove();

        actor.getAggroList().clear(true);

        setAttackTimeout(Long.MAX_VALUE);
        setAttackTarget(null);

        changeIntention(CtrlIntention.AI_INTENTION_ACTIVE);

        actor.broadcastPacketToOthers(new MagicSkillUse(actor, 2036, 500));
        actor.teleToLocation(sloc.x, sloc.y, GeoEngine.getHeight(sloc, actor.getGeoIndex()));
    }
}