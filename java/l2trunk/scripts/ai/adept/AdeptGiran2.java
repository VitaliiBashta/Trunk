package l2trunk.scripts.ai.adept;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class AdeptGiran2 extends Adept {
    public AdeptGiran2(NpcInstance actor) {
        super(actor);
        points = List.of(
                        new Location(83336, 147992, -3400),
                        new Location(83320, 148568, -3400),
                        new Location(82856, 148872, -3472),
                        new Location(82232, 149592, -3472),
                        new Location(82856, 148872, -3472),
                        new Location(83320, 148568, -3400));
    }
}