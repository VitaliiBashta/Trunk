package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;

public final class EffectCharge extends Effect {
    // Максимальное количество зарядов находится в поле val="xx"

    public EffectCharge(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (effected instanceof Player) {
            final Player player = (Player) effected;

            if (player.getIncreasedForce() >= calc())
                player.sendPacket(SystemMsg.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY_);
            else
                player.setIncreasedForce(player.getIncreasedForce() + 1);
        }
    }

}
