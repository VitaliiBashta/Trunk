package l2trunk.scripts.ai.seedofdestruction;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class Obelisk extends DefaultAI {
    private static final List<Integer> MOBS = List.of(22541, 22544, 22543);
    private boolean _firstTimeAttacked = true;

    public Obelisk(NpcInstance actor) {
        super(actor);
        actor.setBlock(true);
    }

    @Override
    public void onEvtDead(Creature killer) {
        _firstTimeAttacked = true;
        NpcInstance actor = getActor();
        actor.broadcastPacket(new ExShowScreenMessage(NpcString.NONE, 3000, ScreenMessageAlign.MIDDLE_CENTER, false, "Obelisk has collapsed. Don't let the enemies jump around wildly anymore!!!"));
        actor.stopDecay();
        actor.getReflection().getNpcs()
                .filter(n -> n.getNpcId() == 18777)
                .forEach(Creature::stopDamageBlocked);
        super.onEvtDead(killer);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (_firstTimeAttacked) {
            _firstTimeAttacked = false;
            for (int i = 0; i < 8; i++)
                MOBS.forEach(mobId -> {
                    NpcInstance npc = actor.getReflection().addSpawnWithoutRespawn(mobId, Location.findPointToStay(actor, 400, 1000));
                    Creature randomHated = actor.getAggroList().getRandomHated();
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, randomHated != null ? randomHated : attacker, Rnd.get(1, 100));
                });
        }
        super.onEvtAttacked(attacker, damage);
    }
}