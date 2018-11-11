package l2trunk.gameserver.handler.bypass;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;

public interface IBypassHandler {
    String[] getBypasses();

    void onBypassFeedback(NpcInstance npc, Player player, String command);
}
