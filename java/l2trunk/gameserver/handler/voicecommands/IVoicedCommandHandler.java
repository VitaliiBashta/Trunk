package l2trunk.gameserver.handler.voicecommands;

import l2trunk.gameserver.model.Player;

import java.util.Collection;
import java.util.List;

public interface IVoicedCommandHandler {
    /**
     * this is the worker method that is called when someone uses an admin command.
     * @return command success
     */
    boolean useVoicedCommand(String command, Player activeChar, String target);

    /**
     * this method is called at initialization to register all the item ids automatically
     *
     * @return all known itemIds
     */
    Collection<String> getVoicedCommandList();
}
