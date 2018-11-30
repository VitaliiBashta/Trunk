package l2trunk.scripts.ai.residences.fortress.siege;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2trunk.gameserver.model.entity.residence.Fortress;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.scripts.ai.residences.SiegeGuardRanger;
import l2trunk.scripts.npc.model.residences.SiegeGuardInstance;

public final class ArcherCaption extends SiegeGuardRanger {
    public ArcherCaption(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        SiegeGuardInstance actor = getActor();

        FortressSiegeEvent siegeEvent = actor.getEvent(FortressSiegeEvent.class);
        if (siegeEvent == null)
            return;

        if (siegeEvent.getResidence().getFacilityLevel(Fortress.GUARD_BUFF) > 0)
            actor.doCast(SkillTable.INSTANCE().getInfo(5432, siegeEvent.getResidence().getFacilityLevel(Fortress.GUARD_BUFF)), actor, false);

        siegeEvent.barrackAction(0, false);
    }

    @Override
    public void onEvtDead(Creature killer) {
        SiegeGuardInstance actor = getActor();
        FortressSiegeEvent siegeEvent = actor.getEvent(FortressSiegeEvent.class);
        if (siegeEvent == null)
            return;

        siegeEvent.barrackAction(0, true);

        siegeEvent.broadcastTo(SystemMsg.THE_BARRACKS_HAVE_BEEN_SEIZED, FortressSiegeEvent.ATTACKERS, FortressSiegeEvent.DEFENDERS);

        Functions.npcShout(actor, NpcString.YOU_MAY_HAVE_BROKEN_OUR_ARROWS_BUT_YOU_WILL_NEVER_BREAK_OUR_WILL_ARCHERS_RETREAT);

        super.onEvtDead(killer);

        siegeEvent.checkBarracks();
    }
}
