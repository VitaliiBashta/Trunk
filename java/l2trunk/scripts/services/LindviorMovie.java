package l2trunk.scripts.services;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.network.serverpackets.ExStartScenePlayer;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.ReflectionUtils;

/**
 * Раз в 3 часа на всей территории базы альянса на Грации всем внутри зоны показывается мувик
 */

public final class LindviorMovie implements ScriptFile {
    private static final long movieDelay = 3 * 60 * 60 * 1000L; // показывать мувик раз в n часов

    @Override
    public void onLoad() {
        Zone zone = ReflectionUtils.getZone("[keucereus_alliance_base_town_peace]");
        zone.setActive(true);

        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(() -> zone.getInsidePlayers().stream()
                .filter(player -> !player.isInBoat())
                .filter(player -> !player.isInFlyingTransform())
                .forEach(player -> player.showQuestMovie(ExStartScenePlayer.SCENE_LINDVIOR)), movieDelay, movieDelay);
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

}
