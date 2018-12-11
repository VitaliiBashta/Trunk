package l2trunk.scripts.ai.dragonvalley;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.Arrays;

public final class Knoriks3 extends Patrollers {
    public Knoriks3(NpcInstance actor) {
        super(actor);
        points = Arrays.asList(
                new Location(140904, 108856, -3764),
                new Location(140648, 112360, -3750),
                new Location(142856, 111768, -3974),
                new Location(142216, 109432, -3966));
    }
}
