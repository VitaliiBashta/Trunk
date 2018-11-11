package l2trunk.gameserver.listener.inventory;

import l2trunk.commons.listener.Listener;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.items.ItemInstance;

public interface OnEquipListener extends Listener<Playable>
{
	void onEquip(int slot, ItemInstance item, Playable actor);

	void onUnequip(int slot, ItemInstance item, Playable actor);
}
