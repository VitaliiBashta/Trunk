package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

/**
 * Mucrokian (22650, 22651, 22652, 22653).
 * Кричат в чат перед атакой. Игнорируют атаку стражей и убегают.
 */
public class Mucrokian extends Fighter {

    private static final NpcString[] MsgText = {
            NpcString.PEUNGLUI_MUGLANEP_NAIA_WAGANAGEL_PEUTAGUN,
            NpcString.PEUNGLUI_MUGLANEP};

    public Mucrokian(NpcInstance actor) {
        super(actor);
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
                    if (Rnd.chance(10)) {
                        Functions.npcSay(actor, Rnd.get(MsgText), ChatType.NPC_ALL, 5000,"");
                    }
                }
            }
            super.onEvtAttacked(attacker, damage);
        }
    }
}