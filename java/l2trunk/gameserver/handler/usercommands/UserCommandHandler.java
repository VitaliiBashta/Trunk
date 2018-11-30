package l2trunk.gameserver.handler.usercommands;

import l2trunk.gameserver.handler.usercommands.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public enum UserCommandHandler {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(UserCommandHandler.class);
    private final Map<Integer, IUserCommandHandler> datatable = new HashMap<>();

    UserCommandHandler() {
        registerUserCommandHandler(new ClanWarsList());
        registerUserCommandHandler(new ClanPenalty());
        registerUserCommandHandler(new CommandChannel());
        registerUserCommandHandler(new Escape());
        registerUserCommandHandler(new Loc());
        registerUserCommandHandler(new MyBirthday());
        registerUserCommandHandler(new OlympiadStat());
        registerUserCommandHandler(new PartyInfo());
        registerUserCommandHandler(new SiegeStatus());
        registerUserCommandHandler(new InstanceZone());
        registerUserCommandHandler(new Time());
    }


    private void registerUserCommandHandler(IUserCommandHandler handler) {
        handler.getUserCommandList().forEach(e -> datatable.put(e, handler));
    }

    public IUserCommandHandler getUserCommandHandler(int userCommand) {
        return datatable.get(userCommand);
    }

    public int size() {
        return datatable.size();
    }

    public void clear() {
        datatable.clear();
    }

    public void log() {
        LOG.info(String.format("loaded %d %s(s) count.", size(), getClass().getSimpleName()));
    }
}
