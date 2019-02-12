package l2trunk.scripts.services;

import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.ReflectionUtils;

public final class PaganDoormans extends Functions {
    private static final int MainDoorId = 19160001;
    private static final int SecondDoor1Id = 19160011;
    private static final int SecondDoor2Id = 19160010;

    public void openMainDoor() {
        if (player == null || npc == null)
            return;

        if (!NpcInstance.canBypassCheck(player, npc))
            return;

        if (!player.haveItem(8064) && !player.haveItem( 8067)) {
            player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
            return;
        }

        openDoor(MainDoorId);
        show("default/32034-1.htm", player, npc);
    }

    public void openSecondDoor() {
        if (player == null || npc == null)
            return;

        if (!NpcInstance.canBypassCheck(player, npc))
            return;

        if (!player.haveItem( 8067)) {
            show("default/32036-2.htm", player, npc);
            return;
        }

        openDoor(SecondDoor1Id);
        openDoor(SecondDoor2Id);
        show("default/32036-1.htm", player, npc);
    }

    public void pressSkull() {
        if (player == null || npc == null)
            return;

        if (!NpcInstance.canBypassCheck(player, npc))
            return;

        openDoor(MainDoorId);
        show("default/32035-1.htm", player, npc);
    }

    public void press2ndSkull() {
        if (player == null || npc == null)
            return;

        if (!NpcInstance.canBypassCheck(player, npc))
            return;

        openDoor(SecondDoor1Id);
        openDoor(SecondDoor2Id);
        show("default/32037-1.htm", player, npc);
    }

    private static void openDoor(int doorId) {
        ReflectionUtils.getDoor(doorId).openMe();
    }
}