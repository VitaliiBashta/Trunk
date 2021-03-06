package l2trunk.scripts.handler.items;

import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.xml.holder.DoorHolder;
import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.templates.DoorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class Keys extends ScriptItemHandler implements ScriptFile {
    private final Set<Integer> itemIds;


    public Keys() {
        itemIds = DoorHolder.getDoors().values().stream()
                .filter(Objects::nonNull)
                .filter(door -> door.getKey() > 0)
                .map(DoorTemplate::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public boolean useItem(Player player, ItemInstance item, boolean ctrl) {
        GameObject target = player.getTarget();
        if (!(target instanceof DoorInstance)) {
            player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }
        DoorInstance door = (DoorInstance) target;
        if (door.isOpen()) {
            player.sendPacket(Msg.IT_IS_NOT_LOCKED);
            return false;
        }
        if (door.getKey() <= 0 || item.getItemId() != door.getKey()) // ключ не подходит к двери
        {
            player.sendPacket(Msg.YOU_ARE_UNABLE_TO_UNLOCK_THE_DOOR);
            return false;
        }
        if (player.getDistance(door) > 300) {
            player.sendPacket(Msg.YOU_CANNOT_CONTROL_BECAUSE_YOU_ARE_TOO_FAR);
            return false;
        }
        if (!player.inventory.destroyItem(item, 1L, "Keys")) {
            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
            return false;
        }
        player.sendPacket(SystemMessage2.removeItems(item.getItemId(), 1));
        player.sendMessage(new CustomMessage("l2trunk.gameserver.skills.skillclasses.Unlock.Success"));
        door.openMe(true);
        return true;
    }

    @Override
    public List<Integer> getItemIds() {
        return new ArrayList<>(itemIds);
    }
}