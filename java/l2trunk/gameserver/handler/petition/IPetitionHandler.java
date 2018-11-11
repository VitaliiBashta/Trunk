package l2trunk.gameserver.handler.petition;

import l2trunk.gameserver.model.Player;

public interface IPetitionHandler {
    void handle(Player player, int id, String txt);
}
