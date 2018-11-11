package l2trunk.gameserver.network.serverpackets;

public class StopPledgeWar extends L2GameServerPacket {
    private final String _pledgeName;
    private final String _char;

    public StopPledgeWar(String pledge, String charName) {
        _pledgeName = pledge;
        _char = charName;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x65);
        writeS(_pledgeName);
        writeS(_char);
    }
}