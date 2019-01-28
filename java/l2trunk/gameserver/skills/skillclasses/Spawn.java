package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class Spawn extends Skill {
    private static final Logger _log = LoggerFactory.getLogger(Spawn.class);
    private final int _npcId;
    private final int _despawnDelay;
    private final boolean _randomOffset;
    private NpcInstance _spawnNpc;

    public Spawn(StatsSet set) {
        super(set);
        _npcId = set.getInteger("npcId", 0);
        _despawnDelay = set.getInteger("despawnDelay", 0);
        set.getBool("isSummonSpawn", false);
        _randomOffset = set.getBool("randomOffset", true);
        set.getInteger("skillToCast", 0);
    }

    @Override
    public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (!activeChar.isPlayer()) {
            return false;
        }
        if (activeChar.isInZone(Zone.ZoneType.peace_zone) || activeChar.isInZone(Zone.ZoneType.water) || activeChar.isInZone(Zone.ZoneType.epic) || activeChar.isInZone(Zone.ZoneType.SIEGE) || activeChar.isInOlympiadMode()) {
            activeChar.sendMessage("In this zone, the action is disabled");
            return false;
        }

        if (activeChar.isFlying()) {
            activeChar.sendMessage("In this state, the action is disabled");
            return false;
        }
        return true;
    }

    @Override
    public void useSkill(Creature caster, List<Creature> targets) {
        if (_npcId == 0) {
            _log.warn("NPC ID not defined for skill ID:" + id);
            return;
        }

        if (caster.getPlayer() != null && caster.getPlayer().getBoat() != null) return;

        SimpleSpawner spawn = new SimpleSpawner(_npcId);
        spawn.setReflection(ReflectionManager.DEFAULT);
        Location loc1 = new Location(caster.getX() + (Rnd.nextBoolean() ? Rnd.get(20, 50) : Rnd.get(-50, -20)),
                caster.getY() + (Rnd.nextBoolean() ? Rnd.get(20, 50) : Rnd.get(-50, -20)),
                caster.getZ() + 20, -1);
        Location loc2 = new Location(caster.getX(), caster.getY(), caster.getZ() + 20, -1);
        if (_randomOffset) {
            spawn.setLoc(loc1);
        } else {
            spawn.setLoc(loc2);
        }
        spawn.doSpawn(true);
        spawn.init();
        _spawnNpc = spawn.getLastSpawn();
        if (_despawnDelay > 0) ThreadPoolManager.INSTANCE.schedule(() -> {
            if (_spawnNpc == null) return;
            _spawnNpc.deleteMe();
        }, _despawnDelay);

    }
}
