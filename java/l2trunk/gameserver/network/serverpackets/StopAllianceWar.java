package l2trunk.gameserver.network.serverpackets;

public class StopAllianceWar extends L2GameServerPacket {
    private final String _allianceName;
    private final String _char;

    public StopAllianceWar(String alliance, String charName) {
        _allianceName = alliance;
        _char = charName;
    }

    @Override
    protected final void writeImpl() {
        writeC(0xc4);
        writeS(_allianceName);
        writeS(_char);
    }
}