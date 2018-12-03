package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.GameTimeController;
import l2trunk.gameserver.stats.Env;

public class ConditionGameTime extends Condition {
    private final CheckGameTime _check;
    private final boolean _required;

    public ConditionGameTime(CheckGameTime check, boolean required) {
        _check = check;
        _required = required;
    }

    @Override
    protected boolean testImpl(Env env) {
        switch (_check) {
            case NIGHT:
                return GameTimeController.INSTANCE.isNowNight() == _required;
        }
        return !_required;
    }

    public enum CheckGameTime {
        NIGHT
    }
}
