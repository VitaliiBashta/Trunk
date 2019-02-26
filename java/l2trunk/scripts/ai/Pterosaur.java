package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class Pterosaur extends DefaultAI {
    private static final List<Location> points = List.of(
            Location.of(3964, -7496, -3488),
            Location.of(7093, -6207, -3447),
            Location.of(7838, -7407, -3616),
            Location.of(7155, -9208, -1467),
            Location.of(7667, -10459, -3687),
            Location.of(9431, -11590, -3979),
            Location.of(8241, -13708, -3731),
            Location.of(8417, -15135, -3698),
            Location.of(7604, -15878, -3703),
            Location.of(7835, -18087, -3564),
            Location.of(7880, -20446, -3520),
            Location.of(6889, -21556, -3430),
            Location.of(5506, -21796, -3350),
            Location.of(5350, -20690, -3511),
            Location.of(3718, -19280, -3523),
            Location.of(2819, -17029, -3583),
            Location.of(2394, -14635, -3334),
            Location.of(3169, -13397, -3609),
            Location.of(2596, -11971, -3601),
            Location.of(2040, -9636, -3546),
            Location.of(2910, -7033, -3315),
            Location.of(5099, -6510, -3396),
            Location.of(5895, -8563, -3656),
            Location.of(3970, -9894, -3684),
            Location.of(5994, -10320, -3651),
            Location.of(6468, -11106, -3660),
            Location.of(7273, -18036, -3657),
            Location.of(5827, -20411, -3527),
            Location.of(4708, -18472, -3702),
            Location.of(4104, -15834, -3609),
            Location.of(5770, -15281, -3692),
            Location.of(7596, -19798, -3631),
            Location.of(10069, -22629, -3716),
            Location.of(10015, -23379, -3714),
            Location.of(8079, -22995, -3741),
            Location.of(5846, -23514, -3756),
            Location.of(5683, -24093, -3776),
            Location.of(4663, -24953, -4166),
            Location.of(7631, -25726, -4115),
            Location.of(9875, -27738, -4417),
            Location.of(11293, -27864, -4439),
            Location.of(11058, -25030, -3688),
            Location.of(11074, -23164, -3675),
            Location.of(10370, -22899, -3704),
            Location.of(9788, -24086, -3762),
            Location.of(11039, -24780, -3669),
            Location.of(11341, -23669, -3669),
            Location.of(8189, -20399, -3500),
            Location.of(6438, -20501, -3573),
            Location.of(4972, -17586, -3728),
            Location.of(6393, -13759, -3729),
            Location.of(8841, -13530, -3891),
            Location.of(9567, -12500, -3986),
            Location.of(9023, -11165, -3996),
            Location.of(7626, -11191, -3973),
            Location.of(7341, -12035, -3937),
            Location.of(11039, -24780, -3669),
            Location.of(8234, -13204, -3986),
            Location.of(9316, -12869, -3989),
            Location.of(6935, -7852, -3685));

    private int current_point = -1;
    private long wait_timeout = 0;
    private boolean wait = false;

    public Pterosaur(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        NpcInstance actor = getActor();
        actor.setFlying(true);
        actor.setHasChatWindow(false);
        super.onEvtSpawn();
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead())
            return true;

        if (defThink && doTask()) {
            clearTasks();
            return true;
        }

        long now = System.currentTimeMillis();
        if (now > wait_timeout && (current_point > -1 || Rnd.chance(5))) {
            if (!wait)
                switch (current_point) {
                    case 0:
                    case 8:
                        wait_timeout = now + 10000;
                        wait = false;
                        return true;
                }

            wait_timeout = 0;
            wait = true;
            current_point++;

            if (current_point >= points.size())
                current_point = 0;

            addTaskMove(points.get(current_point), false);
            doTask();
            return true;
        }

        return randomAnimation();

    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
    }
}