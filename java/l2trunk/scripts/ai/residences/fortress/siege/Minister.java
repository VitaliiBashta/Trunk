package l2trunk.scripts.ai.residences.fortress.siege;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2trunk.gameserver.model.entity.residence.Fortress;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.scripts.ai.residences.SiegeGuardFighter;
import l2trunk.scripts.npc.model.residences.SiegeGuardInstance;

/**
 * @author kekess
 * @date 02:41/05.06.2012
 */
public class Minister extends SiegeGuardFighter {
    public Minister(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int dam) {
        super.onEvtAttacked(attacker, dam);
        SiegeGuardInstance actor = getActor();

        if (Rnd.chance(1))
            Functions.npcSay(actor, NpcString.ATTACKING_THE_ENEMYS_REINFORCEMENTS_IS_NECESSARY);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        SiegeGuardInstance actor = getActor();

        FortressSiegeEvent siegeEvent = actor.getEvent(FortressSiegeEvent.class);
        if (siegeEvent == null)
            return;

        if (siegeEvent.getResidence().getFacilityLevel(Fortress.GUARD_BUFF) > 0)
            actor.doCast(SkillTable.getInstance().getInfo(5432, siegeEvent.getResidence().getFacilityLevel(Fortress.GUARD_BUFF)), actor, false);

        siegeEvent.barrackAction(3, false);
    }

    @Override
    public void onEvtDead(Creature killer) {
        SiegeGuardInstance actor = getActor();
        FortressSiegeEvent siegeEvent = actor.getEvent(FortressSiegeEvent.class);
        if (siegeEvent == null)
            return;

        siegeEvent.barrackAction(3, true);

        siegeEvent.broadcastTo(SystemMsg.THE_BARRACKS_HAVE_BEEN_SEIZED, FortressSiegeEvent.ATTACKERS, FortressSiegeEvent.DEFENDERS);

        Functions.npcShout(actor, NpcString.I_FEEL_SO_MUCH_GRIEF_THAT_I_CANT_EVEN_TAKE_CARE_OF_MYSELF, killer.getPlayer().getName());

        super.onEvtDead(killer);

        siegeEvent.checkBarracks();
    }
}
