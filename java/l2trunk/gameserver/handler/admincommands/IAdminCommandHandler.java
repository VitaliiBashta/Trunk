package l2trunk.gameserver.handler.admincommands;

import l2trunk.gameserver.model.Player;

public interface IAdminCommandHandler {
    /**
     * this is the worker method that is called when someone uses an admin command.
     *
     */
    boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar);

    /**
     * this method is called at initialization to register all the item ids automatically
     *
     * @return all known itemIds
     */
    Enum[] getAdminCommandEnum();
}