package l2trunk.gameserver.network.serverpackets;

public final class PetDelete extends L2GameServerPacket {
    private final int petId;
    private final int petnum;

    public PetDelete(int petId, int petnum) {
        this.petId = petId;
        this.petnum = petnum;
    }

    @Override
    protected final void writeImpl() {
        writeC(0xb7);
        writeD(petId);// dont really know what these two are since i never needed them
        writeD(petnum);
    }
}