package l2trunk.scripts.ai.residences.clanhall;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.entity.events.impl.ClanHallSiegeEvent;
import l2trunk.gameserver.model.entity.events.objects.SpawnExObject;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.PositionUtils;
import l2trunk.gameserver.utils.ReflectionUtils;
import l2trunk.scripts.ai.residences.SiegeGuardMystic;

public final class GiselleVonHellmann extends SiegeGuardMystic {
    private final Skill DAMAGE_SKILL = SkillTable.INSTANCE.getInfo(5003);

    private final Zone ZONE_1 = ReflectionUtils.getZone("lidia_zone1");
    private final Zone ZONE_2 = ReflectionUtils.getZone("lidia_zone2");

    public GiselleVonHellmann(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();

        ZONE_1.setActive(true);
        ZONE_2.setActive(true);

        Functions.npcShout(getActor(), NpcString.ARISE_MY_FAITHFUL_SERVANTS_YOU_MY_PEOPLE_WHO_HAVE_INHERITED_THE_BLOOD);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();

        super.onEvtDead(killer);

        ZONE_1.setActive(false);
        ZONE_2.setActive(false);

        Functions.npcShout(actor, NpcString.AARGH_IF_I_DIE_THEN_THE_MAGIC_FORCE_FIELD_OF_BLOOD_WILL);

        ClanHallSiegeEvent siegeEvent = actor.getEvent(ClanHallSiegeEvent.class);
        if (siegeEvent == null)
            return;
        SpawnExObject spawnExObject = siegeEvent.getFirstObject(ClanHallSiegeEvent.BOSS);
        NpcInstance lidiaNpc = spawnExObject.getFirstSpawned();

        if (lidiaNpc.getCurrentHpRatio() == 1.)
            lidiaNpc.setCurrentHp(lidiaNpc.getMaxHp() / 2., true);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();

        super.onEvtAttacked(attacker, damage);

        if (PositionUtils.calculateDistance(attacker, actor, false) > 300. && Rnd.chance(0.13))
            addTaskCast(attacker, DAMAGE_SKILL);
    }
}
