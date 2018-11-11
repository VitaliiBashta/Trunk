package l2trunk.gameserver.network.telnet.commands;

import l2trunk.gameserver.network.telnet.TelnetCommand;
import l2trunk.gameserver.network.telnet.TelnetCommandHolder;
import l2trunk.gameserver.utils.AdminFunctions;

import java.util.LinkedHashSet;
import java.util.Set;


public class TelnetBan implements TelnetCommandHolder {
    private final Set<TelnetCommand> _commands = new LinkedHashSet<>();

    public TelnetBan() {
        _commands.add(new TelnetCommand("kick") {
            @Override
            public String getUsage() {
                return "kick <name>";
            }

            @Override
            public String handle(String[] args) {
                if (args.length == 0 || args[0].isEmpty())
                    return null;

                if (AdminFunctions.kick(args[0], "telnet"))
                    return "Player kicked.\n";
                else
                    return "Player not found.\n";
            }
        });
    }

    @Override
    public Set<TelnetCommand> getCommands() {
        return _commands;
    }
}