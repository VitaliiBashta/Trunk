package l2trunk.gameserver.network.serverpackets;

public class PledgeReceiveUpdatePower extends L2GameServerPacket {
    private final int _privs;

    public PledgeReceiveUpdatePower(int privs) {
        _privs = privs;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x42);
        writeD(_privs); //Filler??????
    }
}