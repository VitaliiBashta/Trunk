package services.totalonline;

import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;

/**
 * Online -> real + fake
 */
public class totalonline extends Functions implements ScriptFile {

    public void onLoad() {
    }

    //for future possibility of parsing names of players method is taking also name to array for init
    private int getOnlineMembers() {
        int i = 0;
        for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
            i++;
        }
        return i;
    }

    private int getOfflineMembers() {
        int i = 0;
        for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
            if (player.isInOfflineMode())
                i++;
        }

        return i;
    }


    public void onReload() {
    }

    public void onShutdown() {
    }
}