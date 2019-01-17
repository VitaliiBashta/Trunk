package l2trunk.gameserver.model.instances;

import l2trunk.commons.lang.reference.HardReference;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.reference.L2Reference;
import l2trunk.gameserver.network.serverpackets.MyTargetSelected;

public final class ControlKeyInstance extends GameObject {
    private final HardReference<ControlKeyInstance> reference;

    public ControlKeyInstance() {
        super(IdFactory.getInstance().getNextId());
        reference = new L2Reference<>(this);
    }

    @Override
    public HardReference<ControlKeyInstance> getRef() {
        return reference;
    }

    @Override
    public void onAction(Player player, boolean shift) {
        if (player.getTarget() != this) {
            player.setTarget(this);
            player.sendPacket(new MyTargetSelected(getObjectId(), 0));
            return;
        }

        player.sendActionFailed();
    }
}
