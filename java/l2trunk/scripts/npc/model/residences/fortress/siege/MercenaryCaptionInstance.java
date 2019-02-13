package l2trunk.scripts.npc.model.residences.fortress.siege;

import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2trunk.gameserver.model.entity.events.objects.DoorObject;
import l2trunk.gameserver.model.entity.events.objects.SiegeClanObject;
import l2trunk.gameserver.model.entity.residence.Fortress;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.scripts.ai.residences.fortress.siege.MercenaryCaption;

import java.util.List;

/**
 * @author VISTALL
 * @date 8:41/19.04.2011
 */
public class MercenaryCaptionInstance extends MonsterInstance {
    private class DoorDeathListener implements OnDeathListener {
        @Override
        public void onDeath(Creature door, Creature killer) {
            if (isDead())
                return;

            FortressSiegeEvent event = door.getEvent(FortressSiegeEvent.class);
            if (event == null)
                return;

            Functions.npcShout(MercenaryCaptionInstance.this, NpcString.WE_HAVE_BROKEN_THROUGH_THE_GATE_DESTROY_THE_ENCAMPMENT_AND_MOVE_TO_THE_COMMAND_POST);

            List<DoorObject> objects = event.getObjects(FortressSiegeEvent.ENTER_DOORS);
            for (DoorObject d : objects)
                d.open(event);

            ((MercenaryCaption) getAI()).startMove(true);
        }
    }

    private final DoorDeathListener _doorDeathListener = new DoorDeathListener();

    public MercenaryCaptionInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setHasChatWindow(false);
    }

    @Override
    public void onSpawn() {
        super.onSpawn();

        Fortress f = getFortress();
        FortressSiegeEvent event = f.getSiegeEvent();
        List<DoorObject> objects = event.getObjects(FortressSiegeEvent.ENTER_DOORS);
        for (DoorObject d : objects)
            d.getDoor().addListener(_doorDeathListener);
    }

    @Override
    public boolean isAttackable(Creature attacker) {
        return isAutoAttackable(attacker);
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        Player player = (Player) attacker;
        FortressSiegeEvent event = getEvent(FortressSiegeEvent.class);
        if (event == null)
            return false;
        SiegeClanObject object = event.getSiegeClan(FortressSiegeEvent.DEFENDERS, player.getClan());
        return object != null;
    }

    @Override
    public void onDeath(Creature killer) {
        super.onDeath(killer);

        Functions.npcShout(this, NpcString.THE_GODS_HAVE_FORSAKEN_US__RETREAT);
    }

    @Override
    public void onDecay() {
        super.onDecay();
        List<DoorObject> objects = getFortress().getSiegeEvent().getObjects(FortressSiegeEvent.ENTER_DOORS);
        objects.forEach(d ->
            d.getDoor().removeListener(_doorDeathListener));
    }
}
