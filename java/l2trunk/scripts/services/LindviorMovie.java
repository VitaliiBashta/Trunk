package l2trunk.scripts.services;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.network.serverpackets.ExStartScenePlayer;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.List;

/**
 * Раз в 3 часа на всей территории базы альянса на Грации всем внутри зоны показывается мувик
 *
 * @author pchayka
 */

public class LindviorMovie implements ScriptFile {
    private static final long movieDelay = 3 * 60 * 60 * 1000L; // показывать мувик раз в n часов

    @Override
    public void onLoad() {
        Zone zone = ReflectionUtils.getZone("[keucereus_alliance_base_town_peace]");
        zone.setActive(true);

        ThreadPoolManager.INSTANCE().scheduleAtFixedRate(new ShowLindviorMovie(zone), movieDelay, movieDelay);
    }

    public class ShowLindviorMovie extends RunnableImpl {
        final Zone _zone;

        ShowLindviorMovie(Zone zone) {
            _zone = zone;
        }

        @Override
        public void runImpl() {
            List<Player> insideZoners = _zone.getInsidePlayers();

            if (insideZoners != null && !insideZoners.isEmpty())
                for (Player player : insideZoners)
                    if (!player.isInBoat() && !player.isInFlyingTransform())
                        player.showQuestMovie(ExStartScenePlayer.SCENE_LINDVIOR);
        }
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }
}
