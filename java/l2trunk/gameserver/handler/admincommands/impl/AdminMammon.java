package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static l2trunk.commons.lang.NumberUtils.toInt;


@SuppressWarnings("unused")
public class AdminMammon implements IAdminCommandHandler {
    private final List<Integer> npcIds = new ArrayList<>();

    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        Commands command = (Commands) comm;

        npcIds.clear();

        if (!activeChar.getPlayerAccess().Menu)
            return false;

        else if (fullString.startsWith("admin_find_mammon")) {
            npcIds.add(31113);
            npcIds.add(31126);
            npcIds.add(31092); // Add the Marketeer of Mammon also
            int teleportIndex = toInt(fullString.substring(18),-1);

            findAdminNPCs(activeChar, npcIds, teleportIndex, -1);
        } else if ("admin_show_mammon".equals(fullString)) {
            npcIds.add(31113);
            npcIds.add(31126);

            findAdminNPCs(activeChar, npcIds, -1, 1);
        } else if ("admin_hide_mammon".equals(fullString)) {
            npcIds.add(31113);
            npcIds.add(31126);

            findAdminNPCs(activeChar, npcIds, -1, 0);
        } else if (fullString.startsWith("admin_list_spawns")) {
            int npcId =  toInt(fullString.substring(18).trim());
            npcIds.add(npcId);
            findAdminNPCs(activeChar, npcIds, -1, -1);
        }

        // Used for testing SystemMessage IDs - Use //msg <ID>
        else if (fullString.startsWith("admin_msg"))
            activeChar.sendPacket(new SystemMessage2().addInteger(Integer.parseInt(fullString.substring(10).trim())));

        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void findAdminNPCs(Player activeChar, List<Integer> npcIdList, int teleportIndex, int makeVisible) {
        int index = 0;

        for (NpcInstance npc : GameObjectsStorage.getAllNpcs().collect(Collectors.toList())) {
            if (npcIdList.contains(npc.getNpcId())) {
                if (makeVisible == 1)
                    npc.spawnMe();
                else if (makeVisible == 0)
                    npc.decayMe();

                if (npc.isVisible()) {
                    index++;

                    if (teleportIndex > -1) {
                        if (teleportIndex == index)
                            activeChar.teleToLocation(npc.getLoc());
                    } else
                        activeChar.sendMessage(index + " - " + npc.getName() + " (" + npc.getObjectId() + "): " + npc.getLoc());
                }
            }
        }
    }

    private enum Commands {
        admin_find_mammon,
        admin_show_mammon,
        admin_hide_mammon,
        admin_list_spawns
    }
}