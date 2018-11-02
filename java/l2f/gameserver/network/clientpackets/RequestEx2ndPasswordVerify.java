package l2f.gameserver.network.clientpackets;

/**
 * Format: (ch)S
 * S: numerical password
 */
public class RequestEx2ndPasswordVerify extends L2GameClientPacket {
    String _password;

    @Override
    protected void readImpl() {
        _password = readS();
    }

    @Override
    protected void runImpl() {
    }
}