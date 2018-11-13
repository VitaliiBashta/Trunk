package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;

import java.util.Collection;

/**
 * Suppressor(22656).
 * Срывается на защиту устройства в радиусе. Атакует крокодилов в пределах радиуса.
 */
public final class Suppressor extends Fighter {

    private NpcInstance mob = null;
    private boolean _firstTimeAttacked = true;
    private static final NpcString[] MsgText = {
            NpcString.DRIVE_DEVICE_ENTIRE_DESTRUCTION_MOVING_SUSPENSION,
            NpcString.DRIVE_DEVICE_PARTIAL_DESTRUCTION_IMPULSE_RESULT};

    public Suppressor(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onIntentionAttack(Creature target) {
        NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }
        super.onIntentionAttack(target);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null || actor.isDead()) {
            return true;
        }

        if (mob == null) {
            Collection<NpcInstance> around = getActor().getAroundNpc(300, 300);
            if (around != null && !around.isEmpty()) {
                for (NpcInstance npc : around) {
                    if (npc.getNpcId() >= 22650 && npc.getNpcId() <= 22655) {
                        if (mob == null || getActor().getDistance3D(npc) < getActor().getDistance3D(mob)) {
                            mob = npc;
                        }
                    }
                }
            }

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
        if (actor == null) {
            return;
        }

        if (_firstTimeAttacked) {
            _firstTimeAttacked = false;
            if (Rnd.chance(5)) {
                Functions.npcSay(actor, Rnd.get(MsgText), ChatType.NPC_ALL, 5000);
            }
        }
        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public void onEvtDead(Creature killer) {
        _firstTimeAttacked = true;
        super.onEvtDead(killer);
    }
}
