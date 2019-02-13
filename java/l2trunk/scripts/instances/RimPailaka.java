package l2trunk.scripts.instances;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.utils.Location;

import java.util.concurrent.ScheduledFuture;

public final class RimPailaka extends Reflection {
    private static final int SeducedKnight = 36562;
    private static final int SeducedRanger = 36563;
    private static final int SeducedMage = 36564;
    private static final int SeducedWarrior = 36565;
    private static final int KanadisGuide1 = 25659;
    private static final int KanadisGuide2 = 25660;
    private static final int KanadisGuide3 = 25661;
    private static final int KanadisFollower1 = 25662;
    private static final int KanadisFollower2 = 25663;
    private static final int KanadisFollower3 = 25664;
    private static final long initdelay = 30 * 1000L;
    private static final long firstwavedelay = 120 * 1000L;
    private static final long secondwavedelay = 480 * 1000L; // 8 минут после первой волны
    private static final long thirdwavedelay = 480 * 1000L; // 16 минут после первой волны
    private static final Location MINIONS_LOC = new Location(50536, -12232, -9384, 32768);
    private final static Location RANGER_LOC = new Location(49192, -12232, -9384, 0);
    private final static Location MAGE_LOC = new Location(49192, -12456, -9392, 0);
    private final static Location WARRIOR_LOC = new Location(49192, -11992, -9392, 0);
    private final static Location KNIGHT_LOC = new Location(49384, -12232, -9384, 0);
    private static ExShowScreenMessage MSG1 = new ExShowScreenMessage(NpcString.NONE, 3000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true, "First stage begins!");
    private static ExShowScreenMessage MSG2 = new ExShowScreenMessage(NpcString.NONE, 3000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true, "Second stage begins!");
    private static ExShowScreenMessage MSG3 = new ExShowScreenMessage(NpcString.NONE, 3000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true, "Third stage begins!");
    private ScheduledFuture<?> initTask;
    private ScheduledFuture<?> firstwaveTask;
    private ScheduledFuture<?> secondWaveTask;
    private ScheduledFuture<?> thirdWaveTask;

    public RimPailaka() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ThreadPoolManager.INSTANCE.schedule(() -> getPlayers().forEach(player ->
                player.sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(10))), (getInstancedZone().getTimelimit() - 10) * 60 * 1000L);
        initTask = ThreadPoolManager.INSTANCE.schedule(new InvestigatorsSpawn(), initdelay);
        firstwaveTask = ThreadPoolManager.INSTANCE.schedule(() -> {
            sendWave(MSG1, KanadisGuide1, KanadisFollower1);
            secondWaveTask = ThreadPoolManager.INSTANCE.schedule(() -> {
                sendWave(MSG2, KanadisGuide2, KanadisFollower2);
                thirdWaveTask = ThreadPoolManager.INSTANCE.schedule(
                        () -> sendWave(MSG3, KanadisGuide3, KanadisFollower3), thirdwavedelay);
            }, secondwavedelay);
        }, firstwavedelay);
    }

    @Override
    public void onCollapse() {
        if (initTask != null)
            initTask.cancel(true);
        if (firstwaveTask != null)
            firstwaveTask.cancel(true);
        if (secondWaveTask != null)
            secondWaveTask.cancel(true);
        if (thirdWaveTask != null)
            thirdWaveTask.cancel(true);

        super.onCollapse();
    }

    private void sendWave(ExShowScreenMessage message, int guideId, int folowwerID) {
        getPlayers().forEach(player -> player.sendPacket(message));
        addSpawnWithoutRespawn(guideId, MINIONS_LOC);
        for (int i = 0; i < 10; i++)
            addSpawnWithoutRespawn(folowwerID, MINIONS_LOC, 400);
    }

    public class InvestigatorsSpawn extends RunnableImpl {
        @Override
        public void runImpl() {
            addSpawnWithoutRespawn(SeducedKnight, KNIGHT_LOC);
            addSpawnWithoutRespawn(SeducedRanger, RANGER_LOC);
            addSpawnWithoutRespawn(SeducedMage, MAGE_LOC);
            addSpawnWithoutRespawn(SeducedWarrior, WARRIOR_LOC);
        }
    }

}