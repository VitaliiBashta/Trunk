package l2f.gameserver.network.telnet.commands;

import l2f.gameserver.network.telnet.TelnetCommand;
import l2f.gameserver.network.telnet.TelnetCommandHolder;

import java.util.LinkedHashSet;
import java.util.Set;


public class TelnetConfig implements TelnetCommandHolder {
    private Set<TelnetCommand> _commands = new LinkedHashSet<TelnetCommand>();

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
