package l2trunk.gameserver.model.items.attachment;

import l2trunk.gameserver.model.Player;

public interface PickableAttachment extends ItemAttachment {
    boolean canPickUp(Player player);

    void pickUp(Player player);
}
