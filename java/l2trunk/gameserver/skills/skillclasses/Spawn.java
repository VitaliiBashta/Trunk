package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Spawn extends Skill {
    private static final Logger _log = LoggerFactory.getLogger(Spawn.class);
    private final int _npcId;
    private final int despawnDelay;
    private final boolean randomOffset;
    private NpcInstance _spawnNpc;

    public Spawn(StatsSet set) {
        super(set);
        _npcId = set.getInteger("npcId", 0);
        despawnDelay = set.getInteger("despawnDelay", 0);
        set.getBool("isSummonSpawn", false);
        randomOffset = set.getBool("randomOffset", true);
        set.getInteger("skillToCast", 0);
    }

    @Override
    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (player.isInZone(Zone.ZoneType.peace_zone) || player.isInZone(Zone.ZoneType.water) || player.isInZone(Zone.ZoneType.epic) || player.isInZone(Zone.ZoneType.SIEGE) || player.isInOlympiadMode()) {
            player.sendMessage("In this zone, the action is disabled");
            return false;
        }

        if (player.isFlying()) {
            player.sendMessage("In this state, the action is disabled");
            return false;
        }
        return true;
    }

    @Override
    public void useSkill(Creature caster, Creature targes) {
        if (_npcId == 0) {
            _log.warn("NPC ID not defined for skill ID:" + id);
            return;
        }

        if (caster instanceof Player && ((Player) caster).getBoat() != null) return;

        SimpleSpawner spawn = new SimpleSpawner(_npcId);
        spawn.setReflection(ReflectionManager.DEFAULT);
        Location loc1 = Location.findAroundPosition(caster, 20, 50);
        Location loc2 = new Location(caster.getX(), caster.getY(), caster.getZ() + 20, -1);
        if (randomOffset) {
            spawn.setLoc(loc1);
        } else {
            spawn.setLoc(loc2);
        }
        spawn.doSpawn(true);
        spawn.init();
        _spawnNpc = spawn.getLastSpawn();
        if (despawnDelay > 0) ThreadPoolManager.INSTANCE.schedule(() -> {
            if (_spawnNpc == null) return;
            _spawnNpc.deleteMe();
        }, despawnDelay);

    }
}
