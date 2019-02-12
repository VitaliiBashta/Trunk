package l2trunk.scripts.instances;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.templates.InstantZone;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.npc.model.PathfinderInstance;

import java.util.concurrent.Future;

public final class KamalokaNightmare extends Reflection {
    private static final int PATHFINDER = 32485;

    private static final int RANK_1_MIN_POINTS = 500;
    private static final int RANK_2_MIN_POINTS = 2500;
    private static final int RANK_3_MIN_POINTS = 4500;
    private static final int RANK_4_MIN_POINTS = 5500;
    private static final int RANK_5_MIN_POINTS = 7000;
    private static final int RANK_6_MIN_POINTS = 9000;

    private final int playerId;
    private Future<?> _expireTask;

    private int killedKanabions = 0;
    private int killedDoplers = 0;
    private int killedVoiders = 0;

    private int delay_after_spawn = 0;
    private boolean is_spawn_possible = true;

    public KamalokaNightmare(Player player) {
        playerId = player.objectId();
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        InstantZone iz = getInstancedZone();
        if (iz != null) {
            int time_limit = iz.getTimelimit() * 1000 * 60;
            delay_after_spawn = time_limit / 3;
            startPathfinderTimer(time_limit - delay_after_spawn); // спавн патчфиндера происходит через 2\3 прошедшего времени.
        }
    }

    @Override
    protected void onCollapse() {
        super.onCollapse();

        stopPathfinderTimer();
    }

    public void addKilledKanabion(int type) {
        switch (type) {
            case 1:
                killedKanabions++;
                break;
            case 2:
                killedDoplers++;
                break;
            case 3:
                killedVoiders++;
                break;
        }
    }

    public int getRank() {
        int total = killedKanabions * 10 + killedDoplers * 20 + killedVoiders * 50;
        if (total >= RANK_6_MIN_POINTS)
            return 6;
        else if (total >= RANK_5_MIN_POINTS)
            return 5;
        else if (total >= RANK_4_MIN_POINTS)
            return 4;
        else if (total >= RANK_3_MIN_POINTS)
            return 3;
        else if (total >= RANK_2_MIN_POINTS)
            return 2;
        else if (total >= RANK_1_MIN_POINTS)
            return 1;
        else
            return 0;
    }

    private void startPathfinderTimer(long timeInMillis) {
        if (_expireTask != null) {
            _expireTask.cancel(false);
            _expireTask = null;
        }

        _expireTask = ThreadPoolManager.INSTANCE.schedule(new RunnableImpl() {
            @Override
            public void runImpl() {
                try {
                    is_spawn_possible = false;
                    KamalokaNightmare.this.getSpawns().forEach(Spawner::deleteAll);

                    KamalokaNightmare.this.getSpawns().clear();

                    lock.lock();
                    try {
                        objects.stream()
                                .filter(o -> !(o instanceof Playable))
                                .forEach(GameObject::deleteMe);
                    } finally {
                        lock.unlock();
                    }


                    Player p = (Player) GameObjectsStorage.findObject(playerId);
                    if (p != null) {
                        p.sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(delay_after_spawn / 60000));

                        InstantZone iz = KamalokaNightmare.this.getInstancedZone();
                        if (iz != null) {
                            String loc = iz.getAddParams().getString("pathfinder_loc", null);
                            if (loc != null) {
                                PathfinderInstance npc = new PathfinderInstance(IdFactory.getInstance().getNextId(), NpcHolder.getTemplate(PATHFINDER));
                                npc.setSpawnedLoc(Location.of(loc));
                                npc.setReflection(KamalokaNightmare.this);
                                npc.spawnMe(npc.getSpawnedLoc());
                            }
                        }
                    } else
                        collapse();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, timeInMillis);
    }

    private void stopPathfinderTimer() {
        if (_expireTask != null) {
            _expireTask.cancel(false);
            _expireTask = null;
        }
    }

    @Override
    public boolean canChampions() {
        return false;
    }

    public boolean isSpawnPossible() {
        return is_spawn_possible;
    }
}