package l2trunk.gameserver.model.base;

public enum Race {
    human(0),
    elf(1),
    darkelf(2),
    orc(3),
    dwarf(4),
    kamael(5);

    public int id;

    Race(int id) {
        this.id = id;
    }
}
