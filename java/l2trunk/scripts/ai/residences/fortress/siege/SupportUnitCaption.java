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
import l2trunk.scripts.ai.residences.SiegeGuardMystic;
import l2trunk.scripts.npc.model.residences.SiegeGuardInstance;

public final class SupportUnitCaption extends SiegeGuardMystic {
    public SupportUnitCaption(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int dam) {
        super.onEvtAttacked(attacker, dam);
        SiegeGuardInstance actor = getActor();

        if (Rnd.chance(1))
            Functions.npcShout(actor, NpcString.SPIRIT_OF_FIRE_UNLEASH_YOUR_POWER_BURN_THE_ENEMY);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        SiegeGuardInstance actor = getActor();

        FortressSiegeEvent siegeEvent = actor.getEvent(FortressSiegeEvent.class);
        if (siegeEvent == null)
            return;

        if (siegeEvent.getResidence().getFacilityLevel(Fortress.GUARD_BUFF) > 0)
            actor.doCast(5432, siegeEvent.getResidence().getFacilityLevel(Fortress.GUARD_BUFF), actor, false);

        siegeEvent.barrackAction(2, false);
    }

    @Override
    public void onEvtDead(Creature killer) {
        SiegeGuardInstance actor = getActor();
        FortressSiegeEvent siegeEvent = actor.getEvent(FortressSiegeEvent.class);
        if (siegeEvent == null)
            return;

        siegeEvent.barrackAction(2, true);

        siegeEvent.broadcastTo(SystemMsg.THE_BARRACKS_HAVE_BEEN_SEIZED, FortressSiegeEvent.ATTACKERS, FortressSiegeEvent.DEFENDERS);

        Functions.npcShout(actor, NpcString.AT_LAST_THE_MAGIC_FIELD_THAT_PROTECTS_THE_FORTRESS_HAS_WEAKENED_VOLUNTEERS_STAND_BACK);

        super.onEvtDead(killer);

        siegeEvent.checkBarracks();
    }
}
