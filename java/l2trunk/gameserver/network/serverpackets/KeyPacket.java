package l2trunk.gameserver.network.serverpackets;

public final class KeyPacket extends L2GameServerPacket {
    private final byte[] key;

    public KeyPacket(byte key[]) {
        this.key = key;
    }

    @Override
    public void writeImpl() {
        writeC(0x2E);
        if (key == null || key.length == 0) {
            writeC(0x00);
            return;
        }
        writeC(0x01);
        writeB(key);
        writeD(0x01);
        writeD(0x00);
        writeC(0x00);
        writeD(0x00); // Seed (obfuscation key)
    }
}