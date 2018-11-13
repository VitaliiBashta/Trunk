package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.KrateisCubeEvent;
import l2trunk.gameserver.model.entity.events.objects.KrateisCubePlayerObject;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class KrateisFighter extends Fighter {
    public KrateisFighter(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        super.onEvtDead(killer);

        Player player = killer.getPlayer();
        if (player == null)
            return;

        KrateisCubeEvent cubeEvent = getActor().getEvent(KrateisCubeEvent.class);
        if (cubeEvent == null)
            return;

        KrateisCubePlayerObject particlePlayer = cubeEvent.getParticlePlayer(player);

        if (particlePlayer == null)
            return;

        particlePlayer.setPoints(particlePlayer.getPoints() + 3);
        cubeEvent.updatePoints(particlePlayer);
    }
}
