package l2trunk.scripts.instances;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.utils.Location;

/**
 * Class controls Night Zaken
 *
 * @author pchayka
 */

public class ZakenNight extends Reflection {
    private static final int Zaken = 29022;
    private static final long initdelay = 480 * 1000L;        // 480
    private final Location[] zakenspawn = {new Location(55272, 219080, -2952), new Location(55272, 219080, -3224), new Location(55272, 219080, -3496),};

    @Override
    protected void onCreate() {
        super.onCreate();
        ThreadPoolManager.INSTANCE().schedule(new ZakenSpawn(this), initdelay + Rnd.get(120, 240) * 1000L);
    }

    public class ZakenSpawn extends RunnableImpl {
        final Reflection _r;

        ZakenSpawn(Reflection r) {
            _r = r;
        }

        @Override
        public void runImpl() {

            Location rndLoc = zakenspawn[Rnd.get(zakenspawn.length)];
            _r.addSpawnWithoutRespawn(Zaken, rndLoc, 0);
            for (int i = 0; i < 4; i++) {
                _r.addSpawnWithoutRespawn(20845, rndLoc, 200);
                _r.addSpawnWithoutRespawn(20847, rndLoc, 200);
            }
        }
    }
}