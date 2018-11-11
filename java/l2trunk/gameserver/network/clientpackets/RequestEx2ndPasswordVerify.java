package l2trunk.gameserver.network.clientpackets;

/**
 * Format: (ch)S
 * S: numerical password
 */
public class RequestEx2ndPasswordVerify extends L2GameClientPacket {
    private String _password;

    @Override
    protected void readImpl() {
        _password = readS();
    }

    @Override
    protected void runImpl() {
    }
}