package l2trunk.scripts.ai.adept;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class AdeptRune extends Adept {
    public AdeptRune(NpcInstance actor) {
        super(actor);
        points = List.of(
                Location.of(45948, -48190, -792),
                Location.of(45253, -47988, -792),
                Location.of(43516, -48105, -792),
                Location.of(43318, -47420, -792),
                Location.of(43386, -46879, -792),
                Location.of(43318, -47420, -792),
                Location.of(43516, -48105, -792),
                Location.of(45253, -47988, -792));
    }
}