package l2trunk.scripts.ai.seedofdestruction;

import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.ExStartScenePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class TiatCamera extends DefaultAI {
    private List<Player> players = new ArrayList<>();

    public TiatCamera(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
        actor.startDamageBlocked();
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        players = World.getAroundPlayers(actor, 300, 300)
                .filter(p -> !players.contains(p))
                .peek(p -> p.showQuestMovie(ExStartScenePlayer.SCENE_TIAT_OPENING))
                .collect(Collectors.toList());
        return true;
    }
}