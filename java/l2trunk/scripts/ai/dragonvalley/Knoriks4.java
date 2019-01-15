package l2trunk.scripts.ai.dragonvalley;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class Knoriks4 extends Patrollers {
    public Knoriks4(NpcInstance actor) {
        super(actor);
        points = List.of(
                new Location(147960, 110216, -3974),
                new Location(146072, 109400, -3974),
                new Location(145576, 110856, -3974),
                new Location(144504, 107768, -3974),
                new Location(145864, 109224, -3974));
    }
}
