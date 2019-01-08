package l2trunk.gameserver.tables;

import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class GmListTable {
    private GmListTable() {
    }

    private static Stream<Player> getAllGMsStream() {
        return GameObjectsStorage.getAllPlayersStream()
                .filter(Player::isGM);
    }

    public static List<Player> getAllVisibleGMs() {
        return GameObjectsStorage.getAllPlayersStream()
                .filter(Player::isGM)
                .filter(player -> player.getVarInt("gmOnList", 1) == 1)
                .collect(Collectors.toList());
    }

    public static void sendListToPlayer(Player player) {
        List<Player> gmList = getAllVisibleGMs();
        if (gmList.isEmpty()) {
            player.sendPacket(SystemMsg.THERE_ARE_NOT_ANY_GMS_THAT_ARE_PROVIDING_CUSTOMER_SERVICE_CURRENTLY);
            return;
        }

        player.sendPacket(SystemMsg._GM_LIST_);
        gmList.forEach(gm -> player.sendPacket(new SystemMessage2(SystemMsg.GM_S1).addString(gm.getName())));
    }

    public static void broadcastToGMs(L2GameServerPacket packet) {
        getAllGMsStream().forEach(gm -> gm.sendPacket(packet));
    }

    public static void broadcastMessageToGMs(String message) {
        getAllGMsStream().forEach(gm -> gm.sendMessage(message));
    }
}