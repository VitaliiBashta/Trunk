package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

/**
 * Contaminated Mucrokian (22655).
 * Tries to attack the protective devices within sight.
 * When attacking defenders, ignoring the attack and escapes.
 */
public final class AwakenedMucrokian extends Fighter {
    private NpcInstance mob = null;

    public AwakenedMucrokian(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null || actor.isDead()) {
            return true;
        }
        if (mob == null) {
            getActor().getAroundNpc(300, 300)
                    .filter(npc -> (npc.getNpcId() == 18805 || npc.getNpcId() == 18806))
                    .forEach(npc -> {
                        if (mob == null || getActor().getDistance3D(npc) < getActor().getDistance3D(mob)) {
                            mob = npc;
                        }
                    });
        }
        if (mob != null) {
            actor.stopMove();
            actor.setRunning();
            getActor().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, mob, 1);
            return true;
        }
        return false;
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (actor != null && !actor.isDead()) {
            if (attacker != null) {
                if (attacker.getNpcId() >= 22656 && attacker.getNpcId() <= 22659) {
                    actor.abortAttack(true, false);
                    actor.getAggroList().clear();
                    Location pos = Location.findPointToStay(actor, 450, 600);
                    if (GeoEngine.canMoveToCoord(actor, pos)) {
                        actor.setRunning();
                        addTaskMove(pos, false);
                    }

                }
            }
        }
        super.onEvtAttacked(attacker, damage);
    }
}
