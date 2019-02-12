package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.MyTargetSelected;

public final class ControlKeyInstance extends GameObject {

    public ControlKeyInstance() {
        super(IdFactory.getInstance().getNextId());
    }


    @Override
    public void onAction(Player player, boolean shift) {
        if (player.getTarget() != this) {
            player.setTarget(this);
            player.sendPacket(new MyTargetSelected(objectId(), 0));
            return;
        }

        player.sendActionFailed();
    }
}
