package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;

/**
 * Evil Spirits Magic Force (22658).
 * Срывается на защиту кристалла в радиусе. Атакует крокодилов в пределах радиуса.
 */
public final class EvilSpiritsMagicForce extends Fighter {

    private static final List<NpcString> MsgText = List.of(
            NpcString.AH_AH_FROM_THE_MAGIC_FORCE_NO_MORE_I_WILL_BE_FREED,
            NpcString.EVEN_THE_MAGIC_FORCE_BINDS_YOU_YOU_WILL_NEVER_BE_FORGIVEN);
    private NpcInstance mob = null;
    private boolean _firstTimeAttacked = true;

    public EvilSpiritsMagicForce(NpcInstance actor) {
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
            mob = getActor().getAroundNpc(300, 300)
                    .filter(npc -> npc.getNpcId() >= 22650)
                    .filter(npc -> npc.getNpcId() <= 22655).min((o1, o2) -> (int) (getActor().getDistance3D(o1) - getActor().getDistance3D(o2))).orElse(null);
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