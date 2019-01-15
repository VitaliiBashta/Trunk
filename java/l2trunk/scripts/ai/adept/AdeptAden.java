package l2trunk.scripts.ai.adept;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class AdeptAden extends Adept {
    public AdeptAden(NpcInstance actor) {
        super(actor);
        points = List.of(
                new Location(146363, 24149, -2008),
                new Location(146345, 25803, -2008),
                new Location(147443, 25811, -2008),
                new Location(146369, 25817, -2008));
    }
}