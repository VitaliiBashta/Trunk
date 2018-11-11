package l2trunk.scripts.ai.seedofdestruction;

import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.ExStartScenePlayer;

import java.util.ArrayList;
import java.util.List;

public class TiatCamera extends DefaultAI {
    private final List<Player> _players = new ArrayList<>();

    public TiatCamera(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
        actor.startDamageBlocked();
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        for (Player p : World.getAroundPlayers(actor, 300, 300))
            if (!_players.contains(p)) {
                p.showQuestMovie(ExStartScenePlayer.SCENE_TIAT_OPENING);
                _players.add(p);
            }
        return true;
    }
}