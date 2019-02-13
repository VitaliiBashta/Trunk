package l2trunk.gameserver.handler.admincommands.impl;


import l2trunk.commons.lang.NumberUtils;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Spawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.SpawnTable;

public final class AdminDelete implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanEditNPC)
            return false;

        if (command == Commands.admin_delete) {
            GameObject obj = wordList.length == 1 ? activeChar.getTarget() : GameObjectsStorage.getNpc(NumberUtils.toInt(wordList[1], 0));
            if (obj instanceof NpcInstance) {
                NpcInstance target = (NpcInstance) obj;
                if (Config.SAVE_GM_SPAWN)
                    SpawnTable.INSTANCE.deleteSpawn(target.getSpawnedLoc(), target.getNpcId());
                target.deleteMe();

                Spawner spawn = target.getSpawn();

                if (spawn != null)
                    spawn.stopRespawn();
            } else
                activeChar.sendPacket(SystemMsg.INVALID_TARGET);
        }

        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private enum Commands {
        admin_delete
    }
}