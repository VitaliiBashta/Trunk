package l2trunk.scripts.ai.dragonvalley;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class Knoriks2 extends Patrollers {
    public Knoriks2(NpcInstance actor) {
        super(actor);
        points = List.of(
                new Location(140456, 117832, -3942),
                new Location(142632, 117336, -3942),
                new Location(142680, 118680, -3942),
                new Location(141864, 119240, -3942),
                new Location(140856, 118904, -3942));
    }
}
