package l2trunk.gameserver.network.telnet;

import java.util.Set;

public interface TelnetCommandHolder {
    /**
     * Get handler commands
     *
     * @return
     */
    Set<TelnetCommand> getCommands();

}
