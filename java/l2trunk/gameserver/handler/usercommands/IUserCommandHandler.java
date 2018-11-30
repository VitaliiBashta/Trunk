package l2trunk.gameserver.handler.usercommands;

import l2trunk.gameserver.model.Player;

import java.util.List;

public interface IUserCommandHandler {
    /**
     * this is the worker method that is called when someone uses an admin command.
     *
     * @param id
     * @param activeChar
     * @return command success
     */
    boolean useUserCommand(int id, Player activeChar);

    /**
     * this method is called at initialization to register all the item ids automatically
     *
     * @return all known itemIds
     */
    List<Integer> getUserCommandList();
}
