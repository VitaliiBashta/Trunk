package l2trunk.gameserver.handler.usercommands;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.handler.usercommands.impl.*;

import java.util.HashMap;
import java.util.Map;


public class UserCommandHandler extends AbstractHolder {
    private static final UserCommandHandler _instance = new UserCommandHandler();
    private final Map<Integer,IUserCommandHandler> _datatable = new HashMap<>();

    private UserCommandHandler() {
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

    public static UserCommandHandler getInstance() {
        return _instance;
    }

    private void registerUserCommandHandler(IUserCommandHandler handler) {
        int[] ids = handler.getUserCommandList();
        for (int element : ids)
            _datatable.put(element, handler);
    }

    public IUserCommandHandler getUserCommandHandler(int userCommand) {
        return _datatable.get(userCommand);
    }

    @Override
    public int size() {
        return _datatable.size();
    }

    @Override
    public void clear() {
        _datatable.clear();
    }
}
