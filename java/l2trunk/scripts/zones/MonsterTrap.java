package l2trunk.scripts.zones;

import l2trunk.commons.math.random.RndSelector;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.List;

public final class MonsterTrap implements ScriptFile {
    private static ZoneListener _zoneListener;
    private static final List<String> zones = List.of(
            "[hellbound_trap1]",
            "[hellbound_trap2]",
            "[hellbound_trap3]",
            "[hellbound_trap4]",
            "[hellbound_trap5]",
            "[SoD_trap_center]",
            "[SoD_trap_left]",
            "[SoD_trap_right]",
            "[SoD_trap_left_back]",
            "[SoD_trap_right_back]");

    @Override
    public void onLoad() {
        _zoneListener = new ZoneListener();

        zones.forEach(s -> {
            Zone zone = ReflectionUtils.getZone(s);
            zone.addListener(_zoneListener);
        });
    }

    private class ZoneListener implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Creature cha) {
            Player player = cha.getPlayer();
            if (player == null || zone.getParams() == null)
                return;

            String[] params;

            int reuse = zone.getParams().getInteger("reuse"); // В секундах
            int despawn = zone.getParams().getInteger("despawn", 5 * 60); // В секундах
            boolean attackOnSpawn = zone.getParams().getBool("attackOnSpawn", true);
            long currentMillis = System.currentTimeMillis();
            long nextReuse = zone.getParams().getLong("nextReuse", currentMillis);
            if (nextReuse > currentMillis)
                return;
            zone.getParams().set("nextReuse", currentMillis + reuse * 1000L);

            //Структура: chance1:id11,id12...;chance2:id21,id22...
            String[] groups = zone.getParams().getString("monsters").split(";");
            RndSelector<int[]> rnd = new RndSelector<>();
            for (String group : groups) {
                //Структура: chance:id1,id2,idN
                params = group.split(":");
                int chance = Integer.parseInt(params[0]);
                params = params[1].split(",");
                int[] mobs = new int[params.length];
                for (int j = 0; j < params.length; j++)
                    mobs[j] = Integer.parseInt(params[j]);
                rnd.add(mobs, chance);
            }

            int[] mobs = rnd.chance();

            for (int npcId : mobs)
                try {
                    SimpleSpawner spawn = new SimpleSpawner(npcId);
                    spawn.setTerritory(zone.getTerritory());
                    spawn.setAmount(1);
                    spawn.setReflection(player.getReflection());
                    spawn.stopRespawn();
                    NpcInstance mob = spawn.doSpawn(true);
                    if (mob != null) {
                        ThreadPoolManager.INSTANCE.schedule(spawn::deleteAll, despawn * 1000L);
                        if (mob.isAggressive() && attackOnSpawn)
                            mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 100);
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature cha) {
        }
    }
}
