package l2trunk.scripts.ai.dragonvalley;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.Arrays;
import java.util.List;

public final class DrakeRunners extends PatrollersNoWatch {
    public DrakeRunners(NpcInstance actor) {
        super(actor);
        _points = List.of(new Location(148984, 112952, -3720),
                new Location(149160, 114312, -3720),
                new Location(149096, 115480, -3720),
                new Location(147720, 116216, -3720),
                new Location(146536, 116296, -3720),
                new Location(145192, 115304, -3720),
                new Location(144888, 114504, -3720),
                new Location(145240, 113272, -3720),
                new Location(145960, 112696, -3720),
                new Location(147416, 112488, -3720));
    }
}
