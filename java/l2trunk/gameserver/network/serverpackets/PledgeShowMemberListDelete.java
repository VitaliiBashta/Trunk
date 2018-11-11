package l2trunk.gameserver.network.serverpackets;

public class PledgeShowMemberListDelete extends L2GameServerPacket {
    private final String _player;

    public PledgeShowMemberListDelete(String playerName) {
        _player = playerName;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x5d);
        writeS(_player);
    }
}