package l2trunk.scripts.ai.adept;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class AdeptGludio extends Adept {
    public AdeptGludio(NpcInstance actor) {
        super(actor);
        points = List.of(
                new Location(-14601, 121243, -2984),
                new Location(-14174, 121266, -2984),
                new Location(-14551, 121247, -2984),
                new Location(-13702, 121246, -2984),
                new Location(-14134, 121250, -2984),
                new Location(-14145, 121680, -2984),
                new Location(-13896, 122250, -2984),
                new Location(-13096, 122259, -2984),
                new Location(-13885, 122272, -2984),
                new Location(-14153, 121682, -2984),
                new Location(-14156, 121261, -2984),
                new Location(-13683, 121252, -2984));
    }
}