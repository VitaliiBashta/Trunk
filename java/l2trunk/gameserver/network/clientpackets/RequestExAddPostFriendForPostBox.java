package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.dao.CharacterDAO;
import l2trunk.gameserver.dao.CharacterPostFriendDAO;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ExConfirmAddingPostFriend;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.Map;

public class RequestExAddPostFriendForPostBox extends L2GameClientPacket {
    private String _name;

    @Override
    protected void readImpl() {
        _name = readS(Config.CNAME_MAXLEN);
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        int targetObjectId = CharacterDAO.getObjectIdByName(_name);
        if (targetObjectId == 0) {
            player.sendPacket(new ExConfirmAddingPostFriend(_name, ExConfirmAddingPostFriend.NAME_IS_NOT_EXISTS));
            return;
        }

        if (_name.equalsIgnoreCase(player.getName())) {
            player.sendPacket(new ExConfirmAddingPostFriend(_name, ExConfirmAddingPostFriend.NAME_IS_NOT_REGISTERED));
            return;
        }

        Map<Integer, String> postFriend = player.getPostFriends();
        if (postFriend.size() >= Player.MAX_POST_FRIEND_SIZE) {
            player.sendPacket(new ExConfirmAddingPostFriend(_name, ExConfirmAddingPostFriend.LIST_IS_FULL));
            return;
        }

        if (postFriend.containsKey(targetObjectId)) {
            player.sendPacket(new ExConfirmAddingPostFriend(_name, ExConfirmAddingPostFriend.ALREADY_ADDED));
            return;
        }

        CharacterPostFriendDAO.getInstance().insert(player, targetObjectId);
        postFriend.put(targetObjectId, CharacterDAO.getNameByObjectId(targetObjectId));

        player.sendPacket(new SystemMessage2(SystemMsg.S1_WAS_SUCCESSFULLY_ADDED_TO_YOUR_CONTACT_LIST).addString(_name), new ExConfirmAddingPostFriend(_name, ExConfirmAddingPostFriend.SUCCESS));
    }
}
