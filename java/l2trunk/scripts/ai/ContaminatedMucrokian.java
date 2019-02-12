package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

/**
 * Contaminated Mucrokian (22654).
 * Кричит в чат перед атакой.
 * Игнорирует атаку стражей и убегает.
 */
public final class ContaminatedMucrokian extends Fighter {

    public ContaminatedMucrokian(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onIntentionAttack(Creature target) {
        NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }
        if (getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) {
            Functions.npcSay(actor, NpcString.NAIA_WAGANAGEL_PEUTAGUN, ChatType.NPC_ALL, 5000, "");
        }
        super.onIntentionAttack(target);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (actor != null && !actor.isDead()) {
            if (attacker != null) {
                if (attacker.getNpcId() >= 22656 && attacker.getNpcId() <= 22659) {
                    if (Rnd.chance(100)) {
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
        }
        super.onEvtAttacked(attacker, damage);
    }
}