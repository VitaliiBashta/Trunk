package l2trunk.gameserver.network.telnet.commands;

import l2trunk.gameserver.network.telnet.TelnetCommand;
import l2trunk.gameserver.network.telnet.TelnetCommandHolder;

import java.util.LinkedHashSet;
import java.util.Set;


public class TelnetConfig implements TelnetCommandHolder {
    private final Set<TelnetCommand> _commands = new LinkedHashSet<>();

    public TelnetConfig() {
        _commands.add(new TelnetCommand("config", "control") {
            @Override
            public String getUsage() {
                return "config parameter[=value]";
            }

            @Override
            public String handle(String[] args) {
                return "Done.\n";
            }
        });
    }

    @Override
    public Set<TelnetCommand> getCommands() {
        return _commands;
    }
}
