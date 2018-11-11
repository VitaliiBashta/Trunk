package l2trunk.gameserver.network.serverpackets;

public class StartAllianceWar extends L2GameServerPacket {
    private final String _allianceName;
    private final String _char;

    public StartAllianceWar(String alliance, String charName) {
        _allianceName = alliance;
        _char = charName;
    }

    @Override
    protected final void writeImpl() {
        writeC(0xc2);
        writeS(_char);
        writeS(_allianceName);
    }
}