package l2trunk.scripts.ai.PaganTemplete;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.data.xml.holder.DoorHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.DoorTemplate;
import l2trunk.gameserver.utils.Location;

import java.util.Arrays;
import java.util.List;

/**
 * @author Grivesky
 * - AI for the monster Andreas Captain Royal Guard (22175).
 * - If you see a player in a range of 500 when its party composes more than 9 Membury.
 * - Then throw on a random coordinates of the first who saw the player.
 * - If the attack when HP is below 70%, throw a debuff and die.
 * - AI is tested and works.
 */
public final class AndreasCaptainRoyalGuard extends Fighter {
    private static final List<Location> locs = Arrays.asList(
            new Location(-16128, -35888, -10726),
            new Location(-17029, -39617, -10724),
            new Location(-15729, -42001, -10724));
    private static int NUMBER_OF_DEATH = 0;
    private boolean _tele = true;
    private boolean _talk = true;

    public AndreasCaptainRoyalGuard(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null)
            return true;

        for (Player player : World.getAroundPlayers(actor, 500, 500)) {
            if (player == null || !player.isInParty())
                continue;

            if (player.getParty().size() >= 9 && _tele) {
                _tele = false;
                player.teleToLocation(Rnd.get(locs));
            }
        }

        return true;
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();

        if (actor.getCurrentHpPercents() <= 70) {
            actor.doCast(4612, 9, attacker, true);
            actor.doDie(attacker);
        }
        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        NUMBER_OF_DEATH++;
        // The doors to the balcony
        DoorTemplate door1 = DoorHolder.getTemplate(19160014);
        DoorTemplate door2 = DoorHolder.getTemplate(19160015);
        // The doors to the althar
        DoorTemplate door3 = DoorHolder.getTemplate(19160016);
        DoorTemplate door4 = DoorHolder.getTemplate(19160017);
        if (NUMBER_OF_DEATH == 39 && _talk) {
            _talk = false;
            // Reset the memory
            NUMBER_OF_DEATH = 0;
            // We have killed all the monsters on the balcony, close the doors to the balcony
//             door1.closeMe(actor);
//             door2.closeMe(actor);
            // Open the door to the altar
//             door3.openMe(actor, false);
//             door4.openMe(actor, false);
        }
        _tele = true;
        super.onEvtDead(killer);
    }
}