package l2trunk.scripts.events.PiratesTreasure;

import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PiratesTreasure extends Functions implements ScriptFile {

    private static int id;
    private static String pointInfo;
    public static boolean eventStoped;
    private Location loc;

    private static final Logger LOG = LoggerFactory.getLogger(PiratesTreasure.class);

    @Override
    public void onLoad() {
        LOG.info("Loaded Event: PiratesTreasure loaded.");
    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {

    }

    public void startEvent(String args[]) {

        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm)
            return;
        id = Integer.parseInt(args[0]);
        sayToAll("The Pirate Ship is approaching!");
        ThreadPoolManager.INSTANCE.schedule(this::callPirates, 60000);
        ThreadPoolManager.INSTANCE.schedule(PiratesTreasure::stopEvent, 31 * 60000);

    }

    public static void stopEvent() {
        sayToAll("Pirate King of Darkness not founded and get away!");
        eventStoped = true;
    }

    private static void sayToAll(String text) {
        Announcements.INSTANCE.announceToAll(text);
    }

    private void callPirates() {

        switch (id) {
            case 1:
                loc = new Location(-102296, 257448, -2960, 0);
                spawn(loc, 13099);
                pointInfo = "The Pirate King was seen in the port of Talking Island!";
                break;
            case 2:
                loc = new Location(51928, 187528, -3624, 0);
                spawn(loc, 13099);
                pointInfo = "The Pirate King was in Giran Harbor.";
                break;
            case 3:
                loc = new Location(48728, -190328, -3624, 0);
                spawn(loc, 13099);
                pointInfo = "The Pirate King was in Giran Harbor.";
                break;
            case 4:
                loc = new Location(8632, -23944, -3760, 0);
                spawn(loc, 13099);
                pointInfo = "The Pirate King was seen near Primeval Isle.";
                break;
            case 5:
                loc = new Location(-37912, -101096, -3728, 0);
                spawn(loc, 13099);
                pointInfo = "The Pirate King was seen near Vallery of Heroes.";
                break;
            case 6:
                loc = new Location(34552, -38024, -3640, 0);
                spawn(loc, 13099);
                pointInfo = "The Pirate King was seen near Rune Harbor.";
                break;
            case 7:
                loc = new Location(41112, -37688, -3632, 0);
                spawn(loc, 13099);
                pointInfo = "The Pirate King was seen near Rune Harbor.";
                break;
            case 8:
                loc = new Location(-89960, 154584, -3728, 0);
                spawn(loc, 13099);
                pointInfo = "The Pirate King was seen near Gludin Harbor.";
                break;
        }
        sayToAll(pointInfo);
        sayToAll("Hurry! King can escape! Remaining time: 30 minutes");

    }

    public static void annoncePointInfo() {
        sayToAll(pointInfo);
    }

}
