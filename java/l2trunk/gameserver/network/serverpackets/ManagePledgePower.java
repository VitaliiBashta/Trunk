package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.pledge.RankPrivs;

public class ManagePledgePower extends L2GameServerPacket {
    private final int _action;
    private final int _clanId;
    private final int privs;

    public ManagePledgePower(Player player, int action, int rank) {
        _clanId = player.getClanId();
        _action = action;
        RankPrivs temp = player.getClan().getRankPrivs(rank);
        privs = temp == null ? 0 : temp.getPrivs();
        player.sendPacket(new PledgeReceiveUpdatePower(privs));
    }

    @Override
    protected final void writeImpl() {
        writeC(0x2a);
        writeD(_clanId);
        writeD(_action);
        writeD(privs);
    }
}