package l2trunk.scripts.ai.adept;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class AdeptGiran extends Adept {
    public AdeptGiran(NpcInstance actor) {
        super(actor);
        points = List.of(
                new Location(84856, 147760, -3400),
                new Location(83625, 147707, -3400),
                new Location(83617, 149544, -3400),
                new Location(83816, 149541, -3400),
                new Location(83632, 149559, -3400),
                new Location(83616, 147708, -3400));
    }
}