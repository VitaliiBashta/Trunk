package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;


public final class AdminDisconnect implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanKick)
            return false;

        switch (command) {
            case admin_disconnect:
            case admin_kick:
                final Player player;
                if (wordList.length == 1) {
                    // Обработка по таргету
                    GameObject target = activeChar.getTarget();
                    if (target == null) {
                        activeChar.sendMessage("Select character or specify player name.");
                        break;
                    }
                    if (!target.isPlayer()) {
                        activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                        break;
                    }
                    player = (Player) target;
                } else {
                    // Обработка по нику
                    player = World.getPlayer(wordList[1]);
                    if (player == null) {
                        activeChar.sendMessage("Character " + wordList[1] + " not found in game.");
                        break;
                    }
                }

                activeChar.sendMessage("Character " + player.getName() + " disconnected from server.");

                player.sendMessage(new CustomMessage("admincommandhandlers.AdminDisconnect.YoureKickedByGM", player));
                player.sendPacket(SystemMsg.YOU_HAVE_BEEN_DISCONNECTED_FROM_THE_SERVER_);
                ThreadPoolManager.INSTANCE.schedule(player::kick, 500);
                break;
            case admin_kick_count:
                GameObjectsStorage.getAllPlayers().stream()
                        .filter(Player::isOnline)
                        .filter(playerToKick -> playerToKick.getNetConnection() != null)
                        .filter(playerToKick -> !playerToKick.equals(activeChar))
                        .forEach(playerToKick -> {
                            ThreadPoolManager.INSTANCE.schedule(playerToKick::kick, 500);

                        });
                break;
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private enum Commands {
        admin_disconnect,
        admin_kick,
        admin_kick_count
    }
}