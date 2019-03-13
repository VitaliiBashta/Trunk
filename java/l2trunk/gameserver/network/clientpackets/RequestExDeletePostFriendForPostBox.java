package l2trunk.gameserver.network.clientpackets;

import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.dao.CharacterPostFriendDAO;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.Map;

public final class RequestExDeletePostFriendForPostBox extends L2GameClientPacket {
    private String name;

    @Override
    protected void readImpl() {
        name = readS();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        if (StringUtils.isEmpty(name))
            return;

        int key = 0;
        Map<Integer, String> postFriends = player.getPostFriends();
        for (Map.Entry<Integer, String> entry : postFriends.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(name))
                key = entry.getKey();
        }

        if (key == 0) {
            player.sendPacket(SystemMsg.THE_NAME_IS_NOT_CURRENTLY_REGISTERED);
            return;
        }

        player.getPostFriends().remove(key);

        CharacterPostFriendDAO.delete(player, key);
        player.sendPacket(new SystemMessage2(SystemMsg.S1_WAS_SUCCESSFULLY_DELETED_FROM_YOUR_CONTACT_LIST).addString(name));
    }
}
