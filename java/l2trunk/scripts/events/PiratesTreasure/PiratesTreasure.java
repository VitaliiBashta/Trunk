package l2trunk.scripts.events.PiratesTreasure;

import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.NpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class PiratesTreasure extends Functions implements ScriptFile {
    private static final Logger LOG = LoggerFactory.getLogger(PiratesTreasure.class);
    public static boolean eventStoped;
    private static int id;
    private static List<Location> locs = List.of(
            Location.of(-102296, 257448, -2960, 0),
            Location.of(51928, 187528, -3624, 0),
            Location.of(48728, -190328, -3624, 0),
            Location.of(8632, -23944, -3760, 0),
            Location.of(-37912, -101096, -3728, 0),
            Location.of(34552, -38024, -3640, 0),
            Location.of(41112, -37688, -3632, 0),
            Location.of(-89960, 154584, -3728, 0));

    private static List<String> infos = List.of(
            "The Pirate King was seen in the port of Talking Island!",
            "The Pirate King was in Giran Harbor.",
            "The Pirate King was in Giran Harbor.",
            "The Pirate King was seen near Primeval Isle.",
            "The Pirate King was seen near Vallery of Heroes.",
            "The Pirate King was seen near Rune Harbor.",
            "The Pirate King was seen near Rune Harbor.",
            "The Pirate King was seen near Gludin Harbor.");

    public static void stopEvent() {
        sayToAll("Pirate King of Darkness not founded and get away!");
        eventStoped = true;
    }

    private static void sayToAll(String text) {
        Announcements.INSTANCE.announceToAll(text);
    }

    public static void annoncePointInfo() {
        sayToAll(infos.get(id - 1));
    }

    @Override
    public void onLoad() {
        LOG.info("Loaded Event: PiratesTreasure loaded.");
    }

    public void startEvent(String[] args) {

        if (!player.getPlayerAccess().IsEventGm)
            return;
        id = Integer.parseInt(args[0]);
        sayToAll("The Pirate Ship is approaching!");
        ThreadPoolManager.INSTANCE.schedule(this::callPirates, 60000);
        ThreadPoolManager.INSTANCE.schedule(PiratesTreasure::stopEvent, 31 * 60000);

    }

    private void callPirates() {
        int pirateId = 13009;
        Location loc = locs.get(id - 1);
        NpcUtils.spawnSingle(pirateId, loc);
        sayToAll(infos.get(id - 1));
        sayToAll("Hurry! King can escape! Remaining time: 30 minutes");

    }

}
