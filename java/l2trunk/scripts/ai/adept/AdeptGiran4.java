package l2trunk.scripts.ai.adept;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public class AdeptGiran4 extends Adept {
    public AdeptGiran4(NpcInstance actor) {
        super(actor);
        _points = new Location[]
                {
                        new Location(84872, 149608, -3400),
                        new Location(81544, 149592, -3472),
                        new Location(81544, 152216, -3536),
                        new Location(81544, 149592, -3472)
                };
    }
}