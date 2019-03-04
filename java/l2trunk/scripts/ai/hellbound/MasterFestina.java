package l2trunk.scripts.ai.hellbound;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.List;

public final class MasterFestina extends Fighter {
    private final static int FOUNDRY_MYSTIC_ID = 22387;
    private final static int FOUNDRY_SPIRIT_GUARD_ID = 22389;
    private final static Zone zone = ReflectionUtils.getZone("[tully2]");
    private final static List<Location> _mysticSpawnPoints = List.of(
            Location.of(-11480, 273992, -11768),
            Location.of(-11128, 273992, -11864),
            Location.of(-10696, 273992, -11936),
            Location.of(-12552, 274920, -11752),
            Location.of(-12568, 275320, -11864),
            Location.of(-12568, 275784, -11936),
            Location.of(-13480, 273880, -11752),
            Location.of(-13880, 273880, -11864),
            Location.of(-14328, 273880, -11936),
            Location.of(-12456, 272968, -11752),
            Location.of(-12456, 272552, -11864),
            Location.of(-12456, 272120, -11936));
    private final static List<Location> _spiritGuardSpawnPoints = List.of(
            Location.of(-12552, 272168, -11936),
            Location.of(-12552, 272520, -11872),
            Location.of(-12552, 272984, -11744),
            Location.of(-13432, 273960, -11736),
            Location.of(-13864, 273960, -11856),
            Location.of(-14296, 273976, -11936),
            Location.of(-12504, 275736, -11936),
            Location.of(-12472, 275288, -11856),
            Location.of(-12472, 274888, -11744),
            Location.of(-11544, 273912, -11752),
            Location.of(-11160, 273912, -11856),
            Location.of(-10728, 273896, -11936));

    private long _lastFactionNotifyTime = 0;

    public MasterFestina(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        NpcInstance actor = getActor();
        // Спауним охрану
        for (Location loc : _mysticSpawnPoints) {
            MonsterInstance mob = new MonsterInstance(IdFactory.getInstance().getNextId(), NpcHolder.getTemplate(FOUNDRY_MYSTIC_ID));
            mob.setSpawnedLoc(loc);
            mob.setReflection(actor.getReflection());
            mob.setFullHpMp();
            mob.spawnMe(mob.getSpawnedLoc());
        }
        for (Location loc : _spiritGuardSpawnPoints) {
            MonsterInstance mob = new MonsterInstance(IdFactory.getInstance().getNextId(), NpcHolder.getTemplate(FOUNDRY_SPIRIT_GUARD_ID));
            mob.setSpawnedLoc(loc);
            mob.setReflection(actor.getReflection());
            mob.setFullHpMp();
            mob.spawnMe(mob.getSpawnedLoc());
        }

        setZoneInactive();
        super.onEvtSpawn();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (System.currentTimeMillis() - _lastFactionNotifyTime > _minFactionNotifyInterval) {
            _lastFactionNotifyTime = System.currentTimeMillis();

            actor.getAroundNpc(3000, 500)
                    .filter(npc -> npc.getNpcId() == FOUNDRY_MYSTIC_ID || npc.getNpcId() == FOUNDRY_SPIRIT_GUARD_ID)
                    .forEach(npc ->
                            npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100)));
        }

        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        _lastFactionNotifyTime = 0;

        // Удаляем охрану
        actor.getAroundNpc(3000, 500)
                .filter(npc -> npc.getNpcId() == FOUNDRY_MYSTIC_ID || npc.getNpcId() == FOUNDRY_SPIRIT_GUARD_ID)
                .forEach(GameObject::deleteMe);

        setZoneActive();
        super.onEvtDead(killer);
    }

    private void setZoneActive() {
        zone.setActive(true);
    }

    private void setZoneInactive() {
        zone.setActive(false);
    }
}