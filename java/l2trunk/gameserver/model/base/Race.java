package l2trunk.gameserver.model.base;

import java.util.stream.Stream;

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

    public static Race of(int id) {
        return Stream.of(values()).filter(race ->  race.id == id)
                .findFirst().orElse(null);
    }
}
