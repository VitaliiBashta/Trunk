package l2trunk.scripts.ai.PaganTemplete;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

/**
 * @author Grivesky
 * - AI for Rabe Boss Andreas Van Halter (29062).
 * - All the information about the AI ​​painted.
 * - AI is tested and works.
 */
public final class TriolsBeliever extends Mystic {
    private static final Location[] locs = {new Location(-16128, -35888, -10726), new Location(-16397, -44970, -10724), new Location(-15729, -42001, -10724)};
    private boolean tele = true;

    public TriolsBeliever(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null)
            return true;

        World.getAroundPlayers(actor, 500, 500)
                .filter(Player::isInParty)
                .filter(p -> p.getParty().size() >= 5 && tele)
                .forEach(p -> {
                    tele = false;
                    p.teleToLocation(Rnd.get(locs));
                });


        return true;
    }

    @Override
    public void onEvtDead(Creature killer) {
        tele = true;
        super.onEvtDead(killer);
    }
}