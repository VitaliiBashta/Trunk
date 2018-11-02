package l2f.gameserver.handler.usercommands;

import gnu.trove.map.hash.TIntObjectHashMap;
import l2f.commons.data.xml.AbstractHolder;
import l2f.gameserver.handler.usercommands.impl.*;


public class UserCommandHandler extends AbstractHolder {
    private static final UserCommandHandler _instance = new UserCommandHandler();
    private TIntObjectHashMap<IUserCommandHandler> _datatable = new TIntObjectHashMap<IUserCommandHandler>();

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

    public void registerUserCommandHandler(IUserCommandHandler handler) {
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
