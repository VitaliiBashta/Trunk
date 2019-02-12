package l2trunk.gameserver.model.base;

public enum ClassType2 {
    None(10280, 10612),
    Warrior(10281, 10289),
    Knight(10282, 10288),
    Rogue(10283, 10290),
    Healer(10285, 10291),
    Enchanter(10287, 10293),
    Summoner(10286, 10294),
    Wizard(10284, 10292);

    public static final ClassType2[] VALUES = values();

    public final int certificate;
    public final int transformation;

    ClassType2(int cer, int tra) {
        certificate = cer;
        transformation = tra;
    }

    public int certificate() {
        return certificate;
    }

    public int transformation() {
        return transformation;
    }
}
