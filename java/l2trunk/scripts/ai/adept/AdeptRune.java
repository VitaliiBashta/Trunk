package l2trunk.scripts.ai.adept;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

class AdeptRune extends Adept {
    public AdeptRune(NpcInstance actor) {
        super(actor);
        _points = new Location[]
                {
                        new Location(45948, -48190, -792),
                        new Location(45253, -47988, -792),
                        new Location(43516, -48105, -792),
                        new Location(43318, -47420, -792),
                        new Location(43386, -46879, -792),
                        new Location(43318, -47420, -792),
                        new Location(43516, -48105, -792),
                        new Location(45253, -47988, -792)
                };
    }
}