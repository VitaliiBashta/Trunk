package l2trunk.scripts.ai.dragonvalley;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class Knoriks1 extends Patrollers {
    public Knoriks1(NpcInstance actor) {
        super(actor);
        points = List.of(
                new Location(145452, 115969, -3760),
                new Location(144630, 115316, -3760),
                new Location(145136, 114851, -3760),
                new Location(146549, 116126, -3760),
                new Location(146421, 116429, -3760));
    }
}
