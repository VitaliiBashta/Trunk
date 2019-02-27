package l2trunk.gameserver.handler.admincommands;

import l2trunk.gameserver.model.Player;

import java.util.Collection;
import java.util.List;

public interface IAdminCommandHandler {
    /**
     * this is the worker method that is called when someone uses an admin command.
     */
    boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar);

    default Collection<String> getAdminCommands(){
        return List.of(getAdminCommand());
    }

    default String getAdminCommand() {
        throw new IllegalArgumentException("not admin command registeged");
    }
}