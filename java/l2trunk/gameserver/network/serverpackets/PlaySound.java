package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.utils.Location;

public final class PlaySound extends L2GameServerPacket {
    public static final L2GameServerPacket SIEGE_VICTORY = new PlaySound("Siege_Victory");
    public static final L2GameServerPacket B04_S01 = new PlaySound("B04_S01");
    public static final L2GameServerPacket HB01 = new PlaySound(PlaySound.Type.MUSIC, "HB01", 0, 0, 0, 0, 0);
    private final Type type;
    private final String soundFile;
    private final int hasCenterObject;
    private final int objectId;
    private final int x;
    private final int y;
    private final int z;

    public PlaySound(String soundFile) {
        this(Type.SOUND, soundFile, 0, 0, 0, 0, 0);
    }

    public PlaySound(Type type, String soundFile, int c, int objectId, Location loc) {
        this(type, soundFile, c, objectId, loc == null ? 0 : loc.x, loc == null ? 0 : loc.y, loc == null ? 0 : loc.z);
    }

    public PlaySound(Type type, String soundFile, int c, int objectId, int x, int y, int z) {
        this.type = type;
        this.soundFile = soundFile;
        hasCenterObject = c;
        this.objectId = objectId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x9e);
        //dSdddddd
        writeD(type.ordinal()); //0 for quest and ship, c4 toturial = 2
        writeS(soundFile);
        writeD(hasCenterObject); //0 for quest; 1 for ship;
        writeD(objectId); //0 for quest; objectId of ship
        writeD(x); //x
        writeD(y); //y
        writeD(z); //z
    }

    public enum Type {
        SOUND,
        MUSIC,
        VOICE
    }
}