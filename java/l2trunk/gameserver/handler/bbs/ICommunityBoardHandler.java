package l2trunk.gameserver.handler.bbs;

import l2trunk.gameserver.model.Player;

import java.util.List;

public interface ICommunityBoardHandler {
    List<String> getBypassCommands();

    void onBypassCommand(Player player, String bypass);

    default void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
    }
}
