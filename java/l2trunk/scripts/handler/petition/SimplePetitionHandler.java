package l2trunk.scripts.handler.petition;

import l2trunk.gameserver.handler.petition.IPetitionHandler;
import l2trunk.gameserver.model.Player;

public final class SimplePetitionHandler implements IPetitionHandler {
    public SimplePetitionHandler() {
        //
    }

    @Override
    public void handle(Player player, int id, String txt) {
        player.sendMessage(txt);
    }
}
