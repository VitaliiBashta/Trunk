package l2trunk.gameserver.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.scripts.npc.model.residences.SiegeGuardInstance;

public class DoorAI extends CharacterAI {
    public DoorAI(DoorInstance actor) {
        super(actor);
    }

    public void onEvtTwiceClick(Player player) {
        //
    }


    @Override
    public DoorInstance getActor() {
        return (DoorInstance) super.getActor();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        Creature actor;
        if (attacker == null || (actor = getActor()) == null)
            return;

        Player player = attacker.getPlayer();
        if (player == null)
            return;

        SiegeEvent<?, ?> siegeEvent1 = player.getEvent(SiegeEvent.class);
        SiegeEvent<?, ?> siegeEvent2 = actor.getEvent(SiegeEvent.class);

        if (siegeEvent1 == null || siegeEvent1 == siegeEvent2 && siegeEvent1.getSiegeClan(SiegeEvent.ATTACKERS, player.getClan()) != null)
            actor.getAroundNpc(900, 200)
                    .filter(npc -> npc instanceof SiegeGuardInstance)
                    .forEach(npc -> {
                        if (Rnd.chance(20))
                            npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 10000);
                        else
                            npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2000);
                    });
    }
}