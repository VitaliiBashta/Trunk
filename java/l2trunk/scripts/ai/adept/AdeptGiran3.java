package l2trunk.scripts.ai.adept;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class AdeptGiran3 extends Adept {
    public AdeptGiran3(NpcInstance actor) {
        super(actor);
        points = List.of(
                        new Location(82840, 147848, -3472),
                        new Location(81096, 147816, -3464),
                        new Location(81096, 149352, -3472),
                        new Location(82936, 149352, -3472));
    }
}