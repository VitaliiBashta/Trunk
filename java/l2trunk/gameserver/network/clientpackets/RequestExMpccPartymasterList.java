package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.matching.MatchingRoom;
import l2trunk.gameserver.network.serverpackets.ExMpccPartymasterList;

import java.util.HashSet;
import java.util.Set;

/**
 * @author VISTALL
 */
public class RequestExMpccPartymasterList extends L2GameClientPacket {
    @Override
    protected void readImpl() {
        //
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        MatchingRoom room = player.getMatchingRoom();
        if (room == null || room.getType() != MatchingRoom.CC_MATCHING)
            return;

        Set<String> set = new HashSet<>();
        for (Player $member : room.getPlayers())
            if ($member.getParty() != null)
                set.add($member.getParty().getLeader().getName());

        player.sendPacket(new ExMpccPartymasterList(set));
    }
}