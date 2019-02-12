package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Stats;

public final class ConditionPlayerCubic extends Condition {
    private final int id;

    public ConditionPlayerCubic(int id) {
        this.id = id;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (env.target instanceof Player) {
            Player targetPlayer = (Player) env.target;
            if (targetPlayer.getCubic(id) != null)
                return true;

            int size = (int) targetPlayer.calcStat(Stats.CUBICS_LIMIT, 1);
            if (targetPlayer.getCubics().size() >= size) {
                if (env.character == targetPlayer)
                    targetPlayer.sendPacket(SystemMsg.CUBIC_SUMMONING_FAILED); //todo un hard code it

                return false;
            }

            return true;
        } else {
            return false;
        }

    }
}
