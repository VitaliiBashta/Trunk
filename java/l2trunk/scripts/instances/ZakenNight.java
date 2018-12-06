package l2trunk.scripts.instances;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.utils.Location;

import java.util.Arrays;
import java.util.List;

public final class ZakenNight extends Reflection {
    private static final int Zaken = 29022;
    private static final long initdelay = 480 * 1000L;
    private final List<Location> zakenspawn = Arrays.asList(
            new Location(55272, 219080, -2952),
            new Location(55272, 219080, -3224),
            new Location(55272, 219080, -3496));

    @Override
    protected void onCreate() {
        super.onCreate();
        ThreadPoolManager.INSTANCE.schedule(new ZakenSpawn(this), initdelay + Rnd.get(120, 240) * 1000L);
    }

    public class ZakenSpawn extends RunnableImpl {
        final Reflection r;

        ZakenSpawn(Reflection r) {
            this.r = r;
        }

        @Override
        public void runImpl() {

            Location rndLoc = Rnd.get(zakenspawn);
            r.addSpawnWithoutRespawn(Zaken, rndLoc, 0);
            for (int i = 0; i < 4; i++) {
                r.addSpawnWithoutRespawn(20845, rndLoc, 200);
                r.addSpawnWithoutRespawn(20847, rndLoc, 200);
            }
        }
    }
}