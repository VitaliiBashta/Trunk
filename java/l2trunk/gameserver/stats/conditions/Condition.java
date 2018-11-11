package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;

public abstract class Condition {
    private SystemMsg _message;

    public final SystemMsg getSystemMsg() {
        return _message;
    }

    public final void setSystemMsg(int msgId) {
        _message = SystemMsg.valueOf(msgId);
    }

    public final boolean test(Env env) {
        return testImpl(env);
    }

    protected abstract boolean testImpl(Env env);
}
